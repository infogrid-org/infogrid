//
// This file is part of InfoGrid(tm). You may not use this file except in
// compliance with the InfoGrid license. The InfoGrid license and important
// disclaimers are contained in the file LICENSE.InfoGrid.txt that you should
// have received with InfoGrid. If you have not received LICENSE.InfoGrid.txt
// or you do not consent to all aspects of the license and the disclaimers,
// no license is granted; do not use this file.
// 
// For more information about InfoGrid go to http://infogrid.org/
//
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.infogrid.comm.MessageEndpoint;
import org.infogrid.comm.MessageEndpointIsDeadException;
import org.infogrid.comm.WaitForResponseEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpointListener;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.net.externalized.ExternalizedProxy;
import org.infogrid.meshbase.net.externalized.SimpleExternalizedProxy;
import org.infogrid.meshbase.net.transaction.NetChange;
import org.infogrid.meshbase.net.transaction.NetMeshObjectBecameDeadStateEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectEquivalentsAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectEquivalentsRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.net.transaction.ReplicaCreatedEvent;
import org.infogrid.meshbase.net.transaction.ReplicaPurgedEvent;
import org.infogrid.meshbase.net.transaction.Utils;
import org.infogrid.meshbase.net.xpriso.SimpleXprisoMessage;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.security.IdentityChangeException;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.net.NetMessageEndpoint;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.Factory;
import org.infogrid.util.FlexibleListenerSet;
import org.infogrid.util.RemoteQueryTimeoutException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.SmartFactory;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentation;

/**
 * <p>Factors out functionality common to many Proxy implementations.</>
 *
 * <p>Note that this class does not listen to incoming messages itself; only the created
 * WaitForResponseEndpoint does. It is also not a TransactionListener any more: the
 * NetMeshBase behaves as if it is, though. This makes sure that the Proxy gets the updates
 * even if temporarily not in memory and swapped out.
 */
public abstract class AbstractProxy
        implements
            Proxy,
            PingPongMessageEndpointListener<XprisoMessage>
{
    private static final Log log = Log.getLogInstance( AbstractProxy.class ); // our own, private logger
    
    /**
     * Constructor.
     *
     * @param ep the NetMessageEndpoint to use by this Proxy
     * @param mb the NetMeshBase that this Proxy belongs to
     */
    protected AbstractProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb )
    {
        theEndpoint = ep;
        theMeshBase = mb;

        theEndpoint.addDirectMessageEndpointListener( this );
            // this must be contained direct, otherwise the proxy may be swapped out and the endpoint "swallows"
            // the incoming message that will never make it to the Proxy

        // Don't use the factory. We dispatch the incoming messages ourselves, so we can make
        // sure we process them in order.
        theWaitForLockResponseEndpoint        = new MyWaitForLockResponseEndpoint( theEndpoint );
        theWaitForHomeReplicaResponseEndpoint = new MyWaitForHomeReplicaResponseEndpoint( theEndpoint );
        theWaitForReplicaResponseEndpoint     = new MyWaitForReplicaResponseEndpoint( theEndpoint );

        // default, subclass constructors can reset
        theTimeCreated = theTimeUpdated = theTimeRead = System.currentTimeMillis();
        theTimeExpires = -1L;
    }

    /**
     * Internal helper triggered when the Proxy is updated in some fashion. This can be
     * used to write Proxy data to disk, for example.
     */
    protected void proxyUpdated()
    {
        if( theFactory != null ) {
            theFactory.factoryCreatedObjectUpdated( this );
        } else {
            log.error( this + ".proxyUpdated() does not have theFactory set" );
        }
    }

    /**
     * Obtain the key for smart factories.
     *
     * @return the key
     */
    public final NetMeshBaseIdentifier getFactoryKey()
    {
        return getPartnerMeshBaseIdentifier();
    }

    /**
     * Enable a Factory to indicate to the FactoryCreatedObject that it was
     * it that created it.
     *
     * @param factory the Factory that created the FactoryCreatedObject
     */
    public final void setFactory(
            Factory<NetMeshBaseIdentifier, Proxy, CoherenceSpecification> factory )
    {
        if( factory instanceof ProxyManager ) {
            theFactory = (ProxyManager) factory;

        } else {
            // This can happen because factories delegating to other factories invoke this method
            // repeatedly with the entire chain of factories, not all of which may be SmartFactories.

            theFactory = null;
        }
    }

    /**
     * Obtain the Factory that created this FactoryCreatedObject. In case of
     * chained factories that delegate to each other, this method is
     * supposed to return the outermost factory invoked by the application programmer.
     *
     * @return the Factory that created the FactoryCreatedObject
     */
    public final ProxyManager getFactory()
    {
        return theFactory;
    }
    
    /**
     * Obtain the NetMeshBase to which this Proxy belongs.
     * 
     * @return the NetMeshBase
     */
    public final NetMeshBase getNetMeshBase()
    {
        return theMeshBase;
    }

    /**
     * Determine the NetMeshBaseIdentifier of the partner NetMeshBase. The partner
     * NetMeshBase is the NetMeshBase with which this Proxy communicates.
     * 
     * @return the NetMeshBaseIdentifier of the partner NetMeshBase
     */
    public final NetMeshBaseIdentifier getPartnerMeshBaseIdentifier()
    {
        return theEndpoint.getNetworkIdentifierOfPartner();
    }
    
    /**
     * Obtain the MessageEndpoint associated with this Proxy.
     *
     * @return the MessageEndpoint
     */
    public final NetMessageEndpoint getMessageEndpoint()
    {
        return theEndpoint;
    }

    /**
     * Set a CoherenceSpecification.
     *
     * @param newValue the new value
     */
    public final void setCoherenceSpecification(
            CoherenceSpecification newValue )
    {
        theCoherenceSpecification = newValue;
    }

    /**
     * Obtain the CoherenceSpecification currently in effect.
     *
     * @return the current CoherenceSpecification
     */
    public final CoherenceSpecification getCoherenceSpecification()
    {
        return theCoherenceSpecification;
    }

    /**
     * Determine when this Proxy was first created. Often this will refer to a time long before this
     * particular Java object instance was created; this time refers to when the connection between
     * the two logical NetMeshBases was created, which could have been in a previous run prior to, say,
     * a server reboot.
     *
     * @return the time this Proxy was created, in System.currentTimeMillis() format
     */
    public final long getTimeCreated()
    {
        return theTimeCreated;
    }

    /**
     * Determine when information held by this Proxy was last updated.
     *
     * @return the time this Proxy was last updated, in System.currentTimeMillis() format
     */
    public final long getTimeUpdated()
    {
        return theTimeUpdated;
    }

    /**
     * Determine when information held by this Proxy was last read.
     *
     * @return the time this Proxy was last read, in System.currentTimeMillis() format
     */
    public final long getTimeRead()
    {
        return theTimeRead;
    }

    /**
     * Determine when this Proxy will expire, if at all.
     *
     * @return the time this Proxy will expire, in System.currentTimeMillis() format, or -1L if never.
     */
    public final long getTimeExpires()
    {
        return theTimeExpires;
    }

    /**
     * Ask this Proxy to obtain from its partner NetMeshBase replicas with the enclosed
     * specification. Do not acquire the lock; that would be a separate operation. 
     * 
     * @param paths the NetMeshObjectAccessSpecifications specifying which replicas should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws NetMeshObjectAccessException accessing the NetMeshBase and obtaining a replica failed
     */
    public final void obtainReplica(
            NetMeshObjectAccessSpecification [] paths,
            long                                timeout )
        throws
            NetMeshObjectAccessException
    {
        theEndpoint.startCommunicating(); // this is no-op on subsequent calls
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        outgoing.setRequestedFirstTimeObjects( paths );

        XprisoMessage incoming; // this is only here to make debugging easier
        try {
            incoming = theWaitForReplicaResponseEndpoint.call( outgoing, timeout );
            
        } catch( RemoteQueryTimeoutException ex ) {
            log.warn( ex );
            throw new NetMeshObjectAccessException( theMeshBase, null, paths, ex );

        } catch( InvocationTargetException ex ) {
            log.warn( ex );
        }
    }

    /**
     * Ask this Proxy to obtain the lock for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the lock should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public final void tryToObtainLocks(
            NetMeshObject [] localReplicas,
            long             timeout )
        throws
            RemoteQueryTimeoutException
    {
        theEndpoint.startCommunicating(); // this is no-op on subsequent calls

        NetMeshObjectIdentifier [] extNames = new NetMeshObjectIdentifier[ localReplicas.length ];
        for( int i=0 ; i<extNames.length ; ++i ) {
            extNames[i] = localReplicas[i].getIdentifier();
        }
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        outgoing.setRequestedLockObjects( extNames );

        XprisoMessage incoming; // this is only here to make debugging easier
        try {
            incoming = theWaitForLockResponseEndpoint.call( outgoing, timeout );

        } catch( InvocationTargetException ex ) {
            log.warn( ex );
        }
    }
    
    /**
     * Ask this Proxy to obtain the home replica status for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the home replica status should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public void tryToObtainHomeReplicas(
            NetMeshObject [] localReplicas,
            long             timeout )
        throws
            RemoteQueryTimeoutException
    {
        theEndpoint.startCommunicating(); // this is no-op on subsequent calls

        NetMeshObjectIdentifier [] extNames = new NetMeshObjectIdentifier[ localReplicas.length ];
        for( int i=0 ; i<extNames.length ; ++i ) {
            extNames[i] = localReplicas[i].getIdentifier();
        }
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        outgoing.setRequestedHomeReplicas( extNames );

        XprisoMessage incoming; // this is only here to make debugging easier
        try {
            incoming = theWaitForHomeReplicaResponseEndpoint.call( outgoing, timeout );

        } catch( InvocationTargetException ex ) {
            log.warn( ex );
        }
    }

    /**
     * Send notification to the partner NetMeshBase that this MeshBase has forcibly taken the
     * lock back for the given NetMeshObjects.
     *
     * @param localReplicas the local replicas for which the lock has been forced back
     */
    public void forceObtainLocks(
            NetMeshObject [] localReplicas )
    {
        theEndpoint.startCommunicating(); // this is no-op on subsequent calls

        NetMeshObjectIdentifier [] extNames = new NetMeshObjectIdentifier[ localReplicas.length ];
        for( int i=0 ; i<extNames.length ; ++i ) {
            extNames[i] = localReplicas[i].getIdentifier();
        }
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        outgoing.setReclaimedLockObjects( extNames );

        theEndpoint.enqueueMessageForSend( outgoing );
    }

    /**
     * Tell the partner NetMeshBase that one or more local replicas exist here that
     * would like to be resynchronized.
     *
     * @param localReplicas the NetMeshObjectIdentifiers of the local replicas
     */
    public void resynchronizeDependentReplicas(
            NetMeshObjectIdentifier [] localReplicas )
    {
        theEndpoint.startCommunicating(); // this is no-op on subsequent calls
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        outgoing.setRequestedResynchronizeDependentReplicas( localReplicas );

        theEndpoint.enqueueMessageForSend( outgoing );
    }

    /**
     * Invoked by the NetMeshBase that this Proxy belongs to,
     * it causes this Proxy to initiate the "ceasing communication" sequence with
     * the partner NetMeshBase, and then kill itself.
     */
    @SuppressWarnings(value={"unchecked"})
    public void initiateCeaseCommunications()
    {
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
        
        outgoing.setCeaseCommunications( true );
        
        theEndpoint.enqueueMessageForSend( outgoing );
        
        theFactory.remove( theEndpoint.getNetworkIdentifierOfPartner() );
    }

    /**
     * Tell this Proxy that it is not needed any more. This will invoke
     * {@link #initiateCaseCommunications} if and only if
     * isPermanent is true.
     * 
     * @param isPermanent if true, this Proxy will go away permanently; if false,
     *        it may come alive again some time later, e.g. after a reboot
     */
    public void die(
            boolean isPermanent )
    {
        if( isPermanent ) {
            initiateCeaseCommunications();
        }
        
        theEndpoint.gracefulDie();
    }

    /**
     * Obtain this Proxy in externalized form.
     *
     * @return the ExternalizedProxy capturing the information in this Proxy
     */
    public ExternalizedProxy asExternalized()
    {
        return SimpleExternalizedProxy.create( this );
    }

    /**
     * Indicates that a Transaction has been committed. This is invoked by the NetMeshBase
     * without needing a subscription.
     *
     * @param theTransaction the Transaction that was committed
     */
    public void transactionCommitted(
            Transaction theTransaction )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".transactionCommitted( " + theTransaction + " )" );
        }
        Change []                                    changes           = theTransaction.getChangeSet().getChanges();
        ArrayList<NetMeshObjectIdentifier>           canceledObjects   = new ArrayList<NetMeshObjectIdentifier>( changes.length );
        ArrayList<NetMeshObjectDeletedEvent>         deletedEvents     = new ArrayList<NetMeshObjectDeletedEvent>( changes.length );
        ArrayList<NetMeshObjectNeighborAddedEvent>   neighborAdditions = new ArrayList<NetMeshObjectNeighborAddedEvent>( changes.length );
        ArrayList<NetMeshObjectNeighborRemovedEvent> neighborRemovals  = new ArrayList<NetMeshObjectNeighborRemovedEvent>( changes.length );
        ArrayList<NetMeshObjectPropertyChangeEvent>  propertyChanges   = new ArrayList<NetMeshObjectPropertyChangeEvent>( changes.length );
        ArrayList<NetMeshObjectRoleAddedEvent>       roleAdditions     = new ArrayList<NetMeshObjectRoleAddedEvent>( changes.length );
        ArrayList<NetMeshObjectRoleRemovedEvent>     roleRemovals      = new ArrayList<NetMeshObjectRoleRemovedEvent>( changes.length );
        ArrayList<NetMeshObjectTypeAddedEvent>       typeAdditions     = new ArrayList<NetMeshObjectTypeAddedEvent>( changes.length );
        ArrayList<NetMeshObjectTypeRemovedEvent>     typeRemovals      = new ArrayList<NetMeshObjectTypeRemovedEvent>( changes.length );
        ArrayList<ExternalizedNetMeshObject>         replicatedObjects = new ArrayList<ExternalizedNetMeshObject>( changes.length );

        for( int i=0 ; i<changes.length ; ++i ) {
            NetChange current = (NetChange) changes[i];
            
            current.setResolver( theMeshBase );

            // NetMeshObject affectedObject = current.getAffectedMeshObject();

            if( current.shouldBeSent( this )) {
                
                if( current instanceof ReplicaPurgedEvent ) {
                    // affectedObject is null
                    NetMeshObjectIdentifier affectedObjectIdentifier = current.getAffectedMeshObjectIdentifier();
                    canceledObjects.add( affectedObjectIdentifier );

                } else if( current instanceof NetMeshObjectBecameDeadStateEvent ) {
                    // ignore

                } else if( current instanceof NetMeshObjectDeletedEvent ) {
                    NetMeshObjectDeletedEvent realCurrent = (NetMeshObjectDeletedEvent) current;
                    deletedEvents.add( realCurrent );

                } else if( current instanceof NetMeshObjectEquivalentsAddedEvent ) {
                    // FIXME

                } else if( current instanceof NetMeshObjectEquivalentsRemovedEvent ) {
                    // FIXME

                } else if( current instanceof NetMeshObjectNeighborAddedEvent ) {
                    NetMeshObjectNeighborAddedEvent realCurrent = (NetMeshObjectNeighborAddedEvent) current;
                    neighborAdditions.add( realCurrent );
                    
                    NetMeshObject neighbor = realCurrent.getNeighborMeshObject();
                    if( neighbor != null && !Utils.hasReplicaInDirection( neighbor, this )) {
                        replicatedObjects.add( neighbor.asExternalized( ! theMeshBase.getPointsReplicasToItself() ) );
                    }

                } else if( current instanceof NetMeshObjectNeighborRemovedEvent ) {
                    NetMeshObjectNeighborRemovedEvent realCurrent = (NetMeshObjectNeighborRemovedEvent) current;
                    neighborRemovals.add( realCurrent );

                } else if( current instanceof NetMeshObjectPropertyChangeEvent ) {
                    NetMeshObjectPropertyChangeEvent realCurrent = (NetMeshObjectPropertyChangeEvent) current;
                    propertyChanges.add( realCurrent );

                } else if( current instanceof NetMeshObjectRoleAddedEvent ) {
                    NetMeshObjectRoleAddedEvent realCurrent = (NetMeshObjectRoleAddedEvent) current;
                    roleAdditions.add( realCurrent );

                } else if( current instanceof NetMeshObjectRoleRemovedEvent ) {
                    NetMeshObjectRoleRemovedEvent realCurrent = (NetMeshObjectRoleRemovedEvent) current;
                    roleRemovals.add( realCurrent );

                } else if( current instanceof NetMeshObjectTypeAddedEvent ) {
                    NetMeshObjectTypeAddedEvent realCurrent = (NetMeshObjectTypeAddedEvent) current;
                    typeAdditions.add( realCurrent );

                } else if( current instanceof NetMeshObjectTypeRemovedEvent ) {
                    NetMeshObjectTypeRemovedEvent realCurrent = (NetMeshObjectTypeRemovedEvent) current;
                    typeRemovals.add( realCurrent );

                } else if( current instanceof ReplicaCreatedEvent ) {
                    // skip

                } else {
                    log.error( "What is this: " + current );
                }
            }
        }
        
        if(    canceledObjects.isEmpty()
            && deletedEvents.isEmpty()
            && neighborAdditions.isEmpty()
            && neighborRemovals.isEmpty()
            && propertyChanges.isEmpty()
            && roleAdditions.isEmpty()
            && roleRemovals.isEmpty()
            && typeAdditions.isEmpty()
            && typeRemovals.isEmpty()
            && replicatedObjects.isEmpty() )
        {
            return;
        }

        theEndpoint.startCommunicating(); // this is no-op on subsequent calls
        
        SimpleXprisoMessage outgoing = SimpleXprisoMessage.create();
     
        if( !canceledObjects.isEmpty() ) {
            outgoing.setRequestedCanceledObjects( ArrayHelper.copyIntoNewArray( canceledObjects, NetMeshObjectIdentifier.class ));
        }
        if( !deletedEvents.isEmpty() ) {
            outgoing.setDeleteChanges( ArrayHelper.copyIntoNewArray( deletedEvents, NetMeshObjectDeletedEvent.class ));
        }
        if( !neighborAdditions.isEmpty() ) {
            outgoing.setNeighborAdditions( ArrayHelper.copyIntoNewArray( neighborAdditions, NetMeshObjectNeighborAddedEvent.class ));
        }
        if( !neighborRemovals.isEmpty() ) {
            outgoing.setNeighborRemovals( ArrayHelper.copyIntoNewArray( neighborRemovals, NetMeshObjectNeighborRemovedEvent.class ));
        }
        if( !propertyChanges.isEmpty() ) {
            outgoing.setPropertyChanges( ArrayHelper.copyIntoNewArray( propertyChanges, NetMeshObjectPropertyChangeEvent.class ));
        }
        if( !roleAdditions.isEmpty() ) {
            outgoing.setRoleAdditions( ArrayHelper.copyIntoNewArray( roleAdditions, NetMeshObjectRoleAddedEvent.class ));
        }
        if( !roleRemovals.isEmpty() ) {
            outgoing.setRoleRemovals( ArrayHelper.copyIntoNewArray( roleRemovals, NetMeshObjectRoleRemovedEvent.class ));
        }
        if( !typeAdditions.isEmpty() ) {
            outgoing.setTypeAdditions( ArrayHelper.copyIntoNewArray( typeAdditions, NetMeshObjectTypeAddedEvent.class ));
        }
        if( !typeRemovals.isEmpty() ) {
            outgoing.setTypeRemovals( ArrayHelper.copyIntoNewArray( typeRemovals, NetMeshObjectTypeRemovedEvent.class ));
        }
        if( !replicatedObjects.isEmpty() ) {
            outgoing.setConveyedMeshObjects( ArrayHelper.copyIntoNewArray( replicatedObjects, ExternalizedNetMeshObject.class ));
        }
        
        theEndpoint.enqueueMessageForSend( outgoing );
    }

    /**
     * Called when an incoming message has arrived.
     *
     * @param incoming the incoming message
     */
    public final void messageReceived(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  incoming )
    {
        if( log.isInfoEnabled() ) {
            log.info( this + ".messageReceived( " + incoming + " )" );
        }

        try {
            theMeshBase.registerIncomingProxy( this );

            internalMessageReceived( endpoint, incoming );

        } catch( RuntimeException ex ) {
            log.error( ex );

        } finally {
            theMeshBase.unregisterIncomingProxy();
        }
    }
    
    /**
     * Internal implementation method for messageReceived. This makes catching exceptions
     * easier.
     *
     * @param endpoint the MessageEndpoint through which the message arrived
     * @param incoming the incoming message
     */
    protected void internalMessageReceived(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  incoming )
    {
        NetMeshBaseLifecycleManager life   = theMeshBase.getMeshBaseLifecycleManager();
        AccessManager               access = theMeshBase.getAccessManager();
        
        SimpleXprisoMessage outgoing        = null;
        long                now             = System.currentTimeMillis();

        if( incoming.getRequestId() != 0 ) {
            outgoing = SimpleXprisoMessage.create();
            outgoing.setResponseId( incoming.getRequestId()); // make this message as a response
        }

        // First, process the pushLock events on existing objects.
        // Then, createCopy transaction and process.
        // Then, process the pushLock events on all other objects
        // Then, process requests.

        ArrayList<MeshObjectIdentifier> stillToPush = null;
        if( incoming.getPushLockObjects() != null ) {
            MeshObjectIdentifier [] pushLockNew = incoming.getPushLockObjects();
            stillToPush = new ArrayList<MeshObjectIdentifier>();

            for( int i=0 ; i<pushLockNew.length ; ++i ) {
                NetMeshObject current = theMeshBase.findMeshObjectByIdentifier( pushLockNew[i] );
                if( current != null ) {
                    current.pushLock( this );
                    meshObjectModifiedDuringMessageProcessing( current );
                } else {
                    stillToPush.add( pushLockNew[i] );
                }
            }
        }

        theWaitForLockResponseEndpoint.messageReceived( endpoint, incoming );
        theWaitForHomeReplicaResponseEndpoint.messageReceived( endpoint, incoming );
        
        Transaction tx = null;
        try {
            // deal with conveyed objects
            if( incoming.getConveyedMeshObjects() != null && incoming.getConveyedMeshObjects().length > 0 ) {

                ExternalizedNetMeshObject [] externalizedConveyedObjects = incoming.getConveyedMeshObjects(); 
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                    if( access != null ) {
                        try {
                            access.sudo();
                        } catch( IdentityChangeException ex ) {
                            log.error( ex );
                        }
                    }
                }
                for( int i=0 ; i<externalizedConveyedObjects.length ; ++i ) {
                    try {
                        NetMeshObject created = life.rippleCreate( externalizedConveyedObjects[i], getPartnerMeshBaseIdentifier() );
                        meshObjectModifiedDuringMessageProcessing( created );
 
                    } catch( NotPermittedException ex ) {
                        log.error( ex );
                    }
                }
            }
            
            // deal with resynchronized objects
            if( incoming.getResynchronizeDependentReplicas() != null && incoming.getResynchronizeDependentReplicas().length > 0 ) {

                ExternalizedNetMeshObject [] externalizedResynchronizedObjects = incoming.getResynchronizeDependentReplicas(); 
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                    if( access != null ) {
                        try {
                            access.sudo();
                        } catch( IdentityChangeException ex ) {
                            log.error( ex );
                        }
                    }
                }
                for( int i=0 ; i<externalizedResynchronizedObjects.length ; ++i ) {
                    try {
                        NetMeshObject resyncd = life.resynchronize( externalizedResynchronizedObjects[i], getPartnerMeshBaseIdentifier() );
                        meshObjectModifiedDuringMessageProcessing( resyncd );
 
                    } catch( NotPermittedException ex ) {
                        log.error( ex );
                    }
                }                
            }

            // deal with type changes
            if( incoming.getTypeAdditions() != null && incoming.getTypeAdditions().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }

                NetMeshObjectTypeAddedEvent [] typeChanges = incoming.getTypeAdditions();
                for( int i=0 ; i<typeChanges.length ; ++i ) {
                    NetMeshObjectTypeAddedEvent current = typeChanges[i];

                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }
                }
            }
            if( incoming.getTypeRemovals() != null && incoming.getTypeRemovals().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }

                NetMeshObjectTypeRemovedEvent [] typeChanges = incoming.getTypeRemovals();
                for( int i=0 ; i<typeChanges.length ; ++i ) {
                    NetMeshObjectTypeRemovedEvent current = typeChanges[i];

                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }
                }
            }

            // deal with neighbor changes
            if( incoming.getNeighborAdditions() != null && incoming.getNeighborAdditions().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }
                
                NetMeshObjectNeighborAddedEvent [] neighborChanges = incoming.getNeighborAdditions();
                for( int i=0 ; i<neighborChanges.length ; ++i ) {
                    NetMeshObjectNeighborAddedEvent current = neighborChanges[i];
                    
                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }                    
                }                
            }
            if( incoming.getNeighborRemovals() != null && incoming.getNeighborRemovals().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }
                
                NetMeshObjectNeighborRemovedEvent [] neighborChanges = incoming.getNeighborRemovals();
                for( int i=0 ; i<neighborChanges.length ; ++i ) {
                    NetMeshObjectNeighborRemovedEvent current = neighborChanges[i];
                    
                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }                    
                }                
            }
            if( incoming.getRoleAdditions() != null && incoming.getRoleAdditions().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }
                
                NetMeshObjectRoleAddedEvent [] neighborChanges = incoming.getRoleAdditions();
                for( int i=0 ; i<neighborChanges.length ; ++i ) {
                    NetMeshObjectRoleAddedEvent current = neighborChanges[i];
                    
                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }
                }
            }
            if( incoming.getRoleRemovals() != null && incoming.getRoleRemovals().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }
                
                NetMeshObjectRoleRemovedEvent [] neighborChanges = incoming.getRoleRemovals();
                for( int i=0 ; i<neighborChanges.length ; ++i ) {
                    NetMeshObjectRoleRemovedEvent current = neighborChanges[i];
                    
                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }
                }
            }
            
            // deal with property changes
            if( incoming.getPropertyChanges() != null && incoming.getPropertyChanges().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }

                NetMeshObjectPropertyChangeEvent [] propertyChanges = incoming.getPropertyChanges();
                for( int i=0 ; i<propertyChanges.length ; ++i ) {
                    NetMeshObjectPropertyChangeEvent current = propertyChanges[i];

                    try {
                        NetMeshObject changed = current.applyToReplicaIn( theMeshBase );
                        meshObjectModifiedDuringMessageProcessing( changed );

                    } catch( CannotApplyChangeException ex ) {
                        log.error( ex );
                    }
                }
            }

            // deal with deleted objects
            if( incoming.getDeletions() != null && incoming.getDeletions().length > 0 ) {
                if( tx == null ) {
                    tx = theMeshBase.createTransactionAsapIfNeeded();
                }

                NetMeshObjectDeletedEvent [] deletedEvents = incoming.getDeletions();

                for( int i=0 ; i<deletedEvents.length ; ++i ) {
                    NetMeshObject deleted = life.rippleDelete(
                            deletedEvents[i].getAffectedMeshObjectIdentifier(),
                            getPartnerMeshBaseIdentifier(),
                            deletedEvents[i].getTimeEventOccurred() );

                    meshObjectModifiedDuringMessageProcessing( deleted );
                }
            }

        } catch( TransactionException ex ) {
            log.error( this + ": Could not create transaction for incoming message " + incoming, ex );

        } catch( NotPermittedException ex ) {
            log.error( this + ": Could not perform necessary actions for incoming message " + incoming, ex );

        } finally {
            if( tx != null ) {
                tx.commitTransaction();
                if( access != null ) {
                    access.sudone();
                }
            }
            tx = null;
        }

        if( stillToPush != null ) {
            for( int i=0 ; i<stillToPush.size() ; ++i ) {
                NetMeshObject current = theMeshBase.findMeshObjectByIdentifier( stillToPush.get( i ));
                current.pushLock( this );
                
                meshObjectModifiedDuringMessageProcessing( current ); // this may be redundant, but it does not hurt either
            }
        }

        outgoing = processRequests( incoming, outgoing );

        if( outgoing != null ) {
            theTimeUpdated = now;
            if( log.isInfoEnabled() ) {
                log.info( this + ".messageReceived() -- enqueueing response " + outgoing + " )" );
            }
            theEndpoint.startCommunicating();
            theEndpoint.enqueueMessageForSend( outgoing );
        }

        theWaitForReplicaResponseEndpoint.messageReceived( endpoint, incoming );
        
        theTimeRead = now;
    }

    /**
     * Factors out the processing of incoming requests.
     *
     * @param incoming the incoming message
     * @param outgoing the outgoing message to augment, or null if none so far
     * @return the outgoing message that was augmented or newly allocated or null
     */
    protected SimpleXprisoMessage processRequests(
            XprisoMessage       incoming,
            SimpleXprisoMessage outgoing )
    {
        // deal with the requests in this message
        
        if( incoming.getCeaseCommunications() ) {
            // it's all over
            
            ((SmartFactory<NetMeshBaseIdentifier,Proxy,CoherenceSpecification>)theFactory).remove( theEndpoint.getNetworkIdentifierOfPartner() );
            return null; // have nothing to say
        }
        
        // cancel objects
        if( incoming.getRequestedCanceledObjects() != null ) {
            MeshObjectIdentifier [] canceledObjects = incoming.getRequestedCanceledObjects();

            for( int i=0 ; i<canceledObjects.length ; ++i ) {

                NetMeshObject current = theMeshBase.findMeshObjectByIdentifier( canceledObjects[i] );
                current.unregisterReplicationTowards( this );

                meshObjectModifiedDuringMessageProcessing( current );
            }
        }

        if( incoming.getRequestedFirstTimeObjects() != null && incoming.getRequestedFirstTimeObjects().length > 0 ) {
            NetMeshObject [] firstTimeObjects = null;
            try {
                firstTimeObjects = theMeshBase.accessLocally( incoming.getRequestedFirstTimeObjects() );

            } catch( NetMeshObjectAccessException ex ) {
                if( ex.isPartialResultAvailable() ) {
                    firstTimeObjects = ex.getBestEffortResult();
                }
            } catch( NotPermittedException ex ) {
                log.warn( ex );
            }
            
            if( firstTimeObjects != null && firstTimeObjects.length > 0 ) {
                ExternalizedNetMeshObject [] externalizedFirstTimeObjects = new ExternalizedNetMeshObject[ firstTimeObjects.length ];
                int                          count                        = 0;
                boolean                      pointsReplicasToItself       = theMeshBase.getPointsReplicasToItself();

                for( int i=0 ; i<firstTimeObjects.length ; ++i ) {
                    if( firstTimeObjects[i] != null ) {
                        boolean replicateToHere = pointsReplicasToItself || firstTimeObjects[i].getProxyTowardsHomeReplica() == null;
                        externalizedFirstTimeObjects[count++] = firstTimeObjects[i].asExternalized( !replicateToHere );

                        if( replicateToHere ) {
                            firstTimeObjects[i].registerReplicationTowards( this );
                            meshObjectModifiedDuringMessageProcessing( firstTimeObjects[i] );
                        }
                    }
                }
                if( count < externalizedFirstTimeObjects.length ) {
                    externalizedFirstTimeObjects = ArrayHelper.copyIntoNewArray( externalizedFirstTimeObjects, 0, count, ExternalizedNetMeshObject.class );
                }
                
                if( outgoing == null ) {
                    outgoing = SimpleXprisoMessage.create();
                }
                outgoing.setConveyedMeshObjects( externalizedFirstTimeObjects );
            }
        }

        // try to resynchronize. This is very similar to requestedFirstTimeObjects
        if( incoming.getRequestedResynchronizeDependentReplicas() != null && incoming.getRequestedResynchronizeDependentReplicas().length > 0 ) {

            // NetMeshObject [] dependentResyncObjects = theMeshBase.accessLocally( incoming.getRequestedResynchronizeDependentReplicas() );
            NetMeshObject [] dependentResyncObjects = theMeshBase.findMeshObjectsByIdentifier( incoming.getRequestedResynchronizeDependentReplicas() );
            
            if( dependentResyncObjects != null && dependentResyncObjects.length > 0 ) {

                ExternalizedNetMeshObject [] externalizedDependentResyncObjects = new ExternalizedNetMeshObject[ dependentResyncObjects.length ];
                int                          count                              = 0;
                boolean                      pointsReplicasToItself             = theMeshBase.getPointsReplicasToItself();

                for( int i=0 ; i<dependentResyncObjects.length ; ++i ) {
                    if( dependentResyncObjects[i] != null ) {
                        boolean replicateToHere = pointsReplicasToItself || dependentResyncObjects[i].getProxyTowardsHomeReplica() == null;
                        externalizedDependentResyncObjects[count++] = dependentResyncObjects[i].asExternalized( !replicateToHere );

                        if( replicateToHere && dependentResyncObjects[i].findProxyTowards( getPartnerMeshBaseIdentifier() ) == null ) {
                            // only need to do this if we don't have it yet
                            dependentResyncObjects[i].registerReplicationTowards( this );                            
                            meshObjectModifiedDuringMessageProcessing( dependentResyncObjects[i] );
                        }
                    }
                }
                if( count < externalizedDependentResyncObjects.length ) {
                    externalizedDependentResyncObjects = ArrayHelper.copyIntoNewArray( externalizedDependentResyncObjects, 0, count, ExternalizedNetMeshObject.class );
                }
                
                if( outgoing == null ) {
                    outgoing = SimpleXprisoMessage.create();
                }
                
                ExternalizedNetMeshObject [] allConveyedObjects = outgoing.getConveyedMeshObjects();
                if( allConveyedObjects != null && allConveyedObjects.length > 0 ) {
                    allConveyedObjects = ArrayHelper.appendWithoutDuplicates( allConveyedObjects, externalizedDependentResyncObjects, true, ExternalizedNetMeshObject.class );
                } else {
                    allConveyedObjects = externalizedDependentResyncObjects;
                }
                outgoing.setResynchronizeDependentReplicas( allConveyedObjects );
            }
        }        
        
        // try to release the requested locks
        if( incoming.getRequestedLockObjects() != null ) {
            NetMeshObjectIdentifier [] requestLock      = incoming.getRequestedLockObjects();
            NetMeshObjectIdentifier [] pushLockExisting = new NetMeshObjectIdentifier[ requestLock.length ];

            int iPushLock = 0;
            for( int i=0 ; i<requestLock.length ; ++i ) {
                NetMeshObject current = theMeshBase.findMeshObjectByIdentifier( requestLock[i] );
                // FIXME: also check access rights
                if( current == null ) {
                    log.warn( "cannot find MeshObject with Identifier " + requestLock[i].toExternalForm() );
                    continue;
                }
                if( !current.getWillGiveUpLock()) {
                    continue;
                }
                try {
                    boolean success = current.tryToObtainLock(); // FIXME: need to specify timing that does not delay things too much
                    if( success ) {
                        pushLockExisting[ iPushLock++ ] = requestLock[i];
                        current.surrenderLock( this );
                        meshObjectModifiedDuringMessageProcessing( current );
                    }
                } catch( RemoteQueryTimeoutException ex ) {
                    log.warn( ex );
                }
            }
            if( iPushLock > 0 ) {
                if( iPushLock < pushLockExisting.length ) {
                    pushLockExisting = ArrayHelper.subarray( pushLockExisting, 0, iPushLock, NetMeshObjectIdentifier.class );
                }

                if( outgoing == null ) {
                    outgoing = SimpleXprisoMessage.create();
                }
                outgoing.setPushLockObjects( pushLockExisting );
            }
        }
        return outgoing;
    }
    
    /**
     * Hook that enables subclasses to take note which MeshObjects in the MeshBase have
     * been modified in response to message processing.
     * 
     * @param modified the NetMeshObject that was modified
     */
    protected void meshObjectModifiedDuringMessageProcessing(
            NetMeshObject modified )
    {
        // do nothing on this level
    }

    /**
     * Called when an outgoing message has been sent.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the sent message
     */
    public void messageSent(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  msg )
    {
        proxyUpdated();
    }

    /**
     * Called when an outgoing message has enqueued for sending.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the enqueued message
     */
    public void messageEnqueued(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  msg )
    {
        proxyUpdated();
    }

    /**
     * Called when an outoing message failed to be sent.
     *
     * @param msg the outgoing messages
     */
    public void messageSendingFailed(
            MessageEndpoint<XprisoMessage> endpoint,
            List<XprisoMessage>            msg )
    {
        proxyUpdated();
    }

    /**
     * Called when the token has been received.
     *
     * @param token the received token
     */
    public final void tokenReceived(
            PingPongMessageEndpoint<XprisoMessage> endpoint,
            long                                   token )
    {
        proxyUpdated();
    }
    
    /**
     * Called when the token has been sent.
     *
     * @param token the sent token
     */
    public final void tokenSent(
            PingPongMessageEndpoint<XprisoMessage> endpoint,
            long                                   token )
    {
        proxyUpdated();
    }

    /**
     * Called when the receiving endpoint threw the EndpointIsDeadException.
     *
     * @param msg the status of the outgoing queue
     */
    public void disablingError(
            MessageEndpoint<XprisoMessage> endpoint,
            List<XprisoMessage>            msg,
            Throwable                      t )
    {
        // just logging right now (FIXME?)

        if( t instanceof MessageEndpointIsDeadException ) {
            if( log.isDebugEnabled() ) {
                log.debug( this + ".disablingError( " + endpoint + ", " + msg + " )", t );
            }
        } else {
            log.error( this + ".disablingError( " + endpoint + ", " + msg + " )", t );
        }
        theWaitForLockResponseEndpoint.disablingError( endpoint, msg, t );
        theWaitForHomeReplicaResponseEndpoint.disablingError( endpoint, msg, t );
        theWaitForReplicaResponseEndpoint.disablingError( endpoint, msg, t );
        
        proxyUpdated();
    }
    
    /**
     * Subscribe to lease-related events, without using a Reference.
     *
     * @param newListener the to-be-added listener
     * @see #addWeakLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public final void addDirectLeaseManagementListener(
            LeaseManagementListener newListener )
    {
        initializeLeaseManagementListenersIfNeeded();
        theLeaseManagementListeners.addDirect( newListener );
    }

    /**
     * Subscribe to lease-related events, using a WeakReference.
     *
     * @param newListener the to-be-added listener
     * @see #addDirectLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public final void addWeakLeaseManagementListener(
            LeaseManagementListener newListener )
    {
        initializeLeaseManagementListenersIfNeeded();
        theLeaseManagementListeners.addWeak( newListener );
    }

    /**
     * Subscribe to lease-related events, using a SoftReference.
     *
     * @param newListener the to-be-added listener
     * @see #addWeakLeaseManagementListener
     * @see #addDirectLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public final void addSoftLeaseManagementListener(
            LeaseManagementListener newListener )
    {
        initializeLeaseManagementListenersIfNeeded();
        theLeaseManagementListeners.addSoft( newListener );
    }

    /**
     * Unsubscribe from lease-related events.
     *
     * @param oldListener the to-be-removed listener
     * @see #addDirectLeaseManagementListener
     * @see #addWeakLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     */
    public final void removeLeaseManagementListener(
            LeaseManagementListener oldListener )
    {
        theLeaseManagementListeners.remove( oldListener );
    }

    /**
     * Internal helper to initialize theLeaseManagementListeners if needed.
     */
    protected synchronized void initializeLeaseManagementListenersIfNeeded()
    {
        if( theLeaseManagementListeners == null ) {
            theLeaseManagementListeners = new FlexibleListenerSet<LeaseManagementListener,LeaseManagementEvent,Object>() {
                public void fireEventToListener(
                        LeaseManagementListener listener,
                        LeaseManagementEvent    event,
                        Object                  arg )
                {
                    listener.leaseUpdated( event );
                }
            };
        }
    }
    
    /**
     * Fire a lease-management event indicating that our lease status has changed.
     */
    protected void fireLeaseUpdated()
    {
        // myRepository.flushProxy( this ); FIXME?

        FlexibleListenerSet<LeaseManagementListener,LeaseManagementEvent,Object> listeners = theLeaseManagementListeners;

        if( listeners != null ) {
            listeners.fireEvent( new LeaseManagementEvent( this ));
        }
    }

    /**
     * Determine whether this NetMeshObject's replication graph crosses this Proxy.
     *
     * @param obj the NetMeshObject
     * @return true if this NetMeshObject has been replicated across this Proxy
     */
    protected final boolean isOnReplicationEdge(
            NetMeshObject obj )
    {
        Proxy [] proxies = obj.getAllProxies();
        if( proxies == null ) {
            return false;
        }
        for( Proxy p : proxies ) {
            if( p == this ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert to String, for debugging.
     *
     * @return String form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theEndpoint",
                    "theMeshBase.getNetworkIdentifier()"
                },
                new Object[] {
                    theEndpoint,
                    theMeshBase.getIdentifier()
                } );
    }

    /**
     * Obtain the right ResourceHelper for StringRepresentation.
     * 
     * @return the ResourceHelper
     */
    protected ResourceHelper getResourceHelperForStringRepresentation()
    {
        return theResourceHelper;
    }

    /**
     * Obtain a String representation of this Proxy that can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public String toStringRepresentation(
            StringRepresentation rep,
            boolean              isDefaultMeshBase )
    {
        String proxyExternalForm    = getPartnerMeshBaseIdentifier().toExternalForm();
        String meshBaseExternalForm = this.getNetMeshBase().getIdentifier().toExternalForm();

        String key;
        if( isDefaultMeshBase ) {
            key = DEFAULT_MESH_BASE_ENTRY;
        } else {
            key = NON_DEFAULT_MESH_BASE_ENTRY;
        }

        String ret = rep.formatEntry(
                getResourceHelperForStringRepresentation(),
                key,
                proxyExternalForm,
                meshBaseExternalForm );

        return ret;        
    }

    /**
     * Obtain the start part of a String representation of this Proxy that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public String toStringRepresentationLinkStart(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase )
    {
        String proxyExternalForm    = getPartnerMeshBaseIdentifier().toExternalForm();
        String meshBaseExternalForm = this.getNetMeshBase().getIdentifier().toExternalForm();

        String key;
        if( isDefaultMeshBase ) {
            key = DEFAULT_MESH_BASE_LINK_START_ENTRY;
        } else {
            key = NON_DEFAULT_MESH_BASE_LINK_START_ENTRY;
        }

        String ret = rep.formatEntry(
                getResourceHelperForStringRepresentation(),
                key,
                contextPath,
                proxyExternalForm,
                meshBaseExternalForm );

        return ret;        
    }

    /**
     * Obtain the end part of a String representation of this Proxy that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase )
    {
        String proxyExternalForm    = getPartnerMeshBaseIdentifier().toExternalForm();
        String meshBaseExternalForm = this.getNetMeshBase().getIdentifier().toExternalForm();

        String key;
        if( isDefaultMeshBase ) {
            key = DEFAULT_MESH_BASE_LINK_END_ENTRY;
        } else {
            key = NON_DEFAULT_MESH_BASE_LINK_END_ENTRY;
        }

        String ret = rep.formatEntry(
                getResourceHelperForStringRepresentation(),
                key,
                contextPath,
                proxyExternalForm,
                meshBaseExternalForm );

        return ret;                
    }

    /**
     * The MessageEndpoint to use to talk to our partner NetMeshBase's Proxy.
     */
    protected NetMessageEndpoint theEndpoint;

    /**
     * The WaitForResponseEndpoint that makes waiting for responses to lock requests much easier.
     */
    protected WaitForResponseEndpoint<XprisoMessage> theWaitForLockResponseEndpoint;

    /**
     * The WaitForResponseEndpoint that makes waiting for responses to homeReplica requests much easier.
     */
    protected WaitForResponseEndpoint<XprisoMessage> theWaitForHomeReplicaResponseEndpoint;

    /**
     * The WaitForResponseEndpoint that makes waiting for responses to replica requests much easier.
     */
    protected WaitForResponseEndpoint<XprisoMessage> theWaitForReplicaResponseEndpoint;

    /**
     * The MeshBase we belong to.
     */
    protected NetMeshBase theMeshBase;

    /**
     * The ProxyManager that created us.
     */
    protected ProxyManager theFactory;

    /**
     * The currently applicable CoherenceSpecification. FIXME: the information held here should
     * impact the communications parameters of theEndpoint, but does not.
     */
    protected CoherenceSpecification theCoherenceSpecification;

    /**
     * The time at which this Proxy was created logically (not necessarily this Java instance).
     */
    protected long theTimeCreated;

    /**
     * The time at which this Proxy was last updated logically (not necessarily this Java instance).
     */
    protected long theTimeUpdated;

    /**
     * The time at which this Proxy was last read logically (not necessarily this Java instance).
     */
    protected long theTimeRead;

    /**
     * The time at which this Proxy will expire logically (not necessarily this Java instance).
     */
    protected long theTimeExpires;

    /**
     * The lease management listeners.
     */
    protected FlexibleListenerSet<LeaseManagementListener,LeaseManagementEvent,Object> theLeaseManagementListeners;

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( AbstractProxy.class );

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_MESH_BASE_ENTRY = "DefaultMeshBaseString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_MESH_BASE_LINK_START_ENTRY = "DefaultMeshBaseLinkStartString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_MESH_BASE_LINK_END_ENTRY = "DefaultMeshBaseLinkEndString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String NON_DEFAULT_MESH_BASE_ENTRY = "NonDefaultMeshBaseString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String NON_DEFAULT_MESH_BASE_LINK_START_ENTRY = "NonDefaultMeshBaseLinkStartString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String NON_DEFAULT_MESH_BASE_LINK_END_ENTRY = "NonDefaultMeshBaseLinkEndString";

    /**
     * Subclass WaitForResponseEndpoint for easier debugging, and to avoid automatic event subscription.
     */
    static class MyWaitForLockResponseEndpoint
            extends
                WaitForResponseEndpoint<XprisoMessage>
    {
        /**
         *  Constructor.
         * 
         * @param ep the MessageEndpoint to use
         */
        public MyWaitForLockResponseEndpoint(
                MessageEndpoint<XprisoMessage> ep )
        {
            super( ep );
        }
    } 

    /**
     * Subclass WaitForResponseEndpoint for easier debugging, and to avoid automatic event subscription.
     */
    static class MyWaitForHomeReplicaResponseEndpoint
            extends
                WaitForResponseEndpoint<XprisoMessage>
    {
        /**
         *  Constructor.
         * 
         * @param ep the MessageEndpoint to use
         */
        public MyWaitForHomeReplicaResponseEndpoint(
                MessageEndpoint<XprisoMessage> ep )
        {
            super( ep );
        }
    } 

    /**
     * Subclass WaitForResponseEndpoint for easier debugging, and to avoid automatic event subscription.
     */
    static class MyWaitForReplicaResponseEndpoint
            extends
                WaitForResponseEndpoint<XprisoMessage>
    {
        /**
         *  Constructor.
         * 
         * @param ep the MessageEndpoint to use
         */
        public MyWaitForReplicaResponseEndpoint(
                MessageEndpoint<XprisoMessage> ep )
        {
            super( ep );
        }
    } 
}

