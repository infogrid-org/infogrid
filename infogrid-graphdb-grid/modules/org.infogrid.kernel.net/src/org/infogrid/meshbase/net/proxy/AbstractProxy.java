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

package org.infogrid.meshbase.net.proxy;

import java.util.List;
import org.infogrid.comm.MessageEndpoint;
import org.infogrid.comm.MessageEndpointIsDeadException;
import org.infogrid.comm.WaitForResponseEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpointListener;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.LeaseManagementEvent;
import org.infogrid.meshbase.net.LeaseManagementListener;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.NetMeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.externalized.ExternalizedProxy;
import org.infogrid.meshbase.net.externalized.SimpleExternalizedProxy;
import org.infogrid.meshbase.net.security.NetAccessManager;
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
import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.security.IdentityChangeException;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.net.NetMessageEndpoint;
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
     * @param policy the ProxyPolicy to use
     */
    protected AbstractProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb,
            ProxyPolicy        policy )
    {
        theEndpoint    = ep;
        theMeshBase    = mb;
        theProxyPolicy = policy;

        theEndpoint.addDirectMessageEndpointListener( this );
            // this must be contained direct, otherwise the proxy may be swapped out and the endpoint "swallows"
            // the incoming message that will never make it to the Proxy

        // Don't use the factory. We dispatch the incoming messages ourselves, so we can make
        // sure we process them in order.
        theWaitForLockResponseEndpoint        = new MyWaitForLockResponseEndpoint( theEndpoint );
        theWaitForHomeResponseEndpoint = new MyWaitForHomeReplicaResponseEndpoint( theEndpoint );
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
     * Obtain the CoherenceSpecification currently in effect.
     *
     * @return the current CoherenceSpecification
     */
    public final CoherenceSpecification getCoherenceSpecification()
    {
        return theProxyPolicy.getCoherenceSpecification();
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
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public final long obtainReplicas(
            NetMeshObjectAccessSpecification [] paths,
            long                                duration )
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForObtainReplicas( paths, duration, this );
            performInstructions( instructions );

            return instructions.getExpectedObtainReplicasWait();
            
        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
            return 0; // what else?
        }
    }
    
    /**
     * Ask this Proxy to obtain the lock for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the lock should be obtained
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public final void tryToObtainLocks(
            NetMeshObject [] localReplicas,
            long             duration )
        throws
            RemoteQueryTimeoutException
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTryToObtainLocks( localReplicas, duration, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }
    
    /**
     * Ask this Proxy to push the locks for one or more replicas to the partner
     * NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     * 
     * @param localReplicas the local replicas for which the lock should be pushed
     * @param isNewProxy if true, the the NetMeshObject did not replicate via this Proxy prior to this call.
     *         The sequence in the array is the same sequence as in localReplicas.
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public void tryToPushLocks(
            NetMeshObject [] localReplicas,
            boolean []       isNewProxy,
            long             duration )
        throws
            RemoteQueryTimeoutException
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTryToPushLocks( localReplicas, isNewProxy, duration, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Ask this Proxy to obtain the home replica status for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the home replica status should be obtained
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public void tryToObtainHomeReplicas(
            NetMeshObject [] localReplicas,
            long             duration )
        throws
            RemoteQueryTimeoutException
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTryToObtainHomeReplicas( localReplicas, duration, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Ask this Proxy to push the home replica status for one or more replicas to the partner
     * NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     * 
     * @param localReplicas the local replicas for which the home replica status should be pushed
     * @param isNewProxy if true, the the NetMeshObject did not replicate via this Proxy prior to this call.
     *         The sequence in the array is the same sequence as in localReplicas.
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public void tryToPushHomeReplicas(
            NetMeshObject [] localReplicas,
            boolean []       isNewProxy,
            long             duration )
        throws
            RemoteQueryTimeoutException
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTryToPushHomeReplicas( localReplicas, isNewProxy, duration, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
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
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForForceObtainLocks( localReplicas, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Tell the partner NetMeshBase that one or more local replicas would like to be
     * resynchronized. This call uses NetMeshObjectIdentifier instead of NetMeshObject
     * as sometimes the NetMeshObjects have not been instantiated when this call is
     * most naturally made.
     *
     * @param identifiers the identifiers of the NetMeshObjects
     */
    public void tryResynchronizeReplicas(
            NetMeshObjectIdentifier [] identifiers )
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTryResynchronizeReplicas( identifiers, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Ask this Proxy to cancel the leases for the given replicas from its partner NetMeshBase.
     * 
     * @param localReplicas the local replicas for which the lease should be canceled
     */
    public void cancelReplicas(
            NetMeshObject [] localReplicas )
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForCancelReplicas( localReplicas, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }        
    }

    /**
     * Invoked by the NetMeshBase that this Proxy belongs to,
     * it causes this Proxy to initiate the "ceasing communication" sequence with
     * the partner NetMeshBase, and then kill itself.
     */
    @SuppressWarnings(value={"unchecked"})
    public void initiateCeaseCommunications()
    {
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForCeaseCommunications( this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
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
        
        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForTransactionCommitted( theTransaction, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
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
        if( log.isDebugEnabled() ) {
            log.debug( this + ".messageReceived( " + incoming + " )" );
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
        long    responseId    = incoming.getResponseId();
        boolean callIsWaiting = theWaitForHomeResponseEndpoint.isCallWaitingFor( responseId );

        if( !callIsWaiting ) {
            callIsWaiting = theWaitForLockResponseEndpoint.isCallWaitingFor( responseId );
        }
        if( !callIsWaiting ) {
            callIsWaiting = theWaitForReplicaResponseEndpoint.isCallWaitingFor( responseId );
        }

        try {
            ProxyProcessingInstructions instructions = theProxyPolicy.calculateForIncomingMessage( endpoint, incoming, callIsWaiting, this );
            performInstructions( instructions );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Perform the instructions obtained from our ProxyPolicy. The provided instructions
     * may be null, in which case nothing is done.
     * 
     * @param instructions the ProxyProcessingInstructions
     * @throws NetMeshObjectAccessException accessing the NetMeshBase and obtaining a replica failed
     */
    protected void performInstructions(
            ProxyProcessingInstructions instructions )
        throws
            NetMeshObjectAccessException
    {
        if( log.isInfoEnabled() ) {
            log.info( this + ".performInstructions( " + instructions + " )" );
        }
        long now = System.currentTimeMillis();

        if( instructions == null ) {
            return; // nothing to do
        }
        // don't do "if instructions.isEmpty() return" here because we still need to acknowledge return receipt
        
        instructions.check();

        XprisoMessage incoming = instructions.getIncomingXprisoMessage();
        
        if( incoming != null ) {
            theWaitForLockResponseEndpoint.messageReceived( instructions.getIncomingXprisoMessageEndpoint(), incoming );
            theWaitForHomeResponseEndpoint.messageReceived( instructions.getIncomingXprisoMessageEndpoint(), incoming );
        }
    //
        
        NetMeshBaseLifecycleManager life   = theMeshBase.getMeshBaseLifecycleManager();
        NetAccessManager            access = theMeshBase.getAccessManager();
        
        for( NetMeshObject current : instructions.getRegisterReplicationsIfNotAlready()) {
            try {
                current.proxyOnlyRegisterReplicationTowards( this  );
            } catch( IllegalArgumentException ex ) {
                // we have it already
                if( log.isDebugEnabled() ) {
                    log.debug( ex );
                }
            } finally {
                meshObjectModifiedDuringMessageProcessing( current );
            }
        }

        Transaction tx = null;
        try {
            for( RippleInstructions current : instructions.getRippleCreates() ) {
                tx = ensureRights( access, tx );
                NetMeshObject obj = null;
                try {
                    obj = life.rippleCreate(
                            current.getExternalizedNetMeshObject(),
                            current.getProxies(),
                            current.getProxyTowardsHomeIndex(),
                            current.getProxyTowardsLockIndex() );
                } catch( MeshObjectIdentifierNotUniqueException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( RippleInstructions current : instructions.getRippleResynchronizes() ) {
                tx = ensureRights( access, tx );
                NetMeshObject obj = null;
                try {
                    obj = life.rippleResynchronize(
                            current.getExternalizedNetMeshObject(),
                            current.getProxies(), current.getProxyTowardsHomeIndex(),
                            current.getProxyTowardsLockIndex() );
                } catch( NotPermittedException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }                    
            }
            for( NetMeshObjectTypeAddedEvent event : instructions.getTypeAdditions() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectEquivalentsAddedEvent event : instructions.getEquivalentsAdditions() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectNeighborAddedEvent event : instructions.getNeighborAdditions() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectRoleAddedEvent event : instructions.getRoleAdditions() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectPropertyChangeEvent event : instructions.getPropertyChanges() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectRoleRemovedEvent event : instructions.getRoleRemovals() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectNeighborRemovedEvent event : instructions.getNeighborRemovals() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectEquivalentsRemovedEvent event : instructions.getEquivalentsRemovals() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectTypeRemovedEvent event : instructions.getTypeRemovals() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
            for( NetMeshObjectDeletedEvent event : instructions.getDeletions() ) {
                NetMeshObject obj = null;
                try {
                    obj = event.potentiallyApplyToReplicaIn( theMeshBase, this );
                } catch( CannotApplyChangeException ex ) {
                    log.error( ex );
                } finally {
                    if( obj != null ) {
                        meshObjectModifiedDuringMessageProcessing( obj );
                    }
                }
            }
        } catch( TransactionException ex ) {
            log.error( ex );
        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }

        for( NetMeshObject current : instructions.getSurrenderLocks() ) {
            current.proxyOnlySurrenderLock( this );
            meshObjectModifiedDuringMessageProcessing( current );
        }
        for( NetMeshObject current : instructions.getSurrenderHomes() ) {
            current.proxyOnlySurrenderHomeReplica( this );
            meshObjectModifiedDuringMessageProcessing( current );
        }
        for( NetMeshObject current : instructions.getCancels() ) {
            current.proxyOnlyUnregisterReplicationTowards( this );
            meshObjectModifiedDuringMessageProcessing( current );
        }
        
        for( ResynchronizeInstructions current : instructions.getResynchronizeInstructions() ) {
            NetMeshObjectIdentifier [] toResync = current.getNetMeshObjectIdentifiers();
            current.getProxy().tryResynchronizeReplicas( toResync );
        }
        for( CancelInstructions current : instructions.getCancelInstructions() ) {
            NetMeshObject [] toCancel = current.getNetMeshObjects();
            current.getProxy().cancelReplicas( toCancel );
        }
        
        
    // send messages
        
        if( instructions.getStartCommunicating() ) {
            theEndpoint.startCommunicating(); // this is no-op on subsequent calls
        }

        XprisoMessage outgoing = instructions.getSendViaWaitForReplicaResponseEndpoint();
        if( outgoing != null ) {
            XprisoMessage incoming2; // this is only here to make debugging easier
            try {
                incoming2 = theWaitForReplicaResponseEndpoint.call( outgoing, instructions.getWaitForReplicaResponseEndpointTimeout() );

            } catch( Throwable t ) {
                instructions.sendViaWaitForReplicaResponseEndpointFailed( this, t );
            }
        }

        outgoing = instructions.getSendViaWaitForHomeResponseEndpoint();
        if( outgoing != null ) {
            XprisoMessage incoming2; // this is only here to make debugging easier
            try {
                incoming2 = theWaitForHomeResponseEndpoint.call( outgoing, instructions.getWaitForHomeResponseEndpointTimeout() );

            } catch( Throwable t ) {
                instructions.sendViaWaitForHomeResponseEndpointFailed( this, t );
            }
        }
        
        outgoing = instructions.getSendViaWaitForLockResponseEndpoint();
        if( outgoing != null ) {
            XprisoMessage incoming2; // this is only here to make debugging easier
            try {
                incoming2 = theWaitForLockResponseEndpoint.call( outgoing, instructions.getWaitForLockResponseEndpointTimeout() );

            } catch( Throwable t ) {
                instructions.sendViaWaitForLockResponseEndpointFailed( this, t );
            }
        }
        
        outgoing = instructions.getSendViaWaitEndpoint();
        if( outgoing != null ) {
            theEndpoint.enqueueMessageForSend( outgoing );
        }
        if( incoming != null ) {
            theWaitForReplicaResponseEndpoint.messageReceived( instructions.getIncomingXprisoMessageEndpoint(), incoming );
        }
        
        theTimeRead = now;

        if( instructions.getCeaseCommunications() ) {
            // it's all over
            ( ( SmartFactory<NetMeshBaseIdentifier, Proxy, CoherenceSpecification> ) theFactory ).remove( theEndpoint.getNetworkIdentifierOfPartner() );
        }
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
        theWaitForHomeResponseEndpoint.disablingError( endpoint, msg, t );
        theWaitForReplicaResponseEndpoint.disablingError( endpoint, msg, t );
        
        proxyUpdated();
    }
    
    /**
     * 
     * @param access
     * @param tx
     * @return
     * @throws org.infogrid.meshbase.transaction.TransactionException
     */
    protected Transaction ensureRights(
            AccessManager access,
            Transaction tx )
        throws
            TransactionException
    {
        if( tx == null ) {
            tx = theMeshBase.createTransactionAsapIfNeeded();

            if( access != null && !access.isSu() ) {
                try {
                    access.sudo();
                } catch( IdentityChangeException ex ) {
                    log.error( ex );
                }
            }
        }
        return tx;
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
                    "theMeshBase.getNetworkIdentifier()",
                    "getPartnerMeshBaseIdentifier()"
                },
                new Object[] {
                    theMeshBase.getIdentifier().toExternalForm(),
                    getPartnerMeshBaseIdentifier().toExternalForm()
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
    protected WaitForResponseEndpoint<XprisoMessage> theWaitForHomeResponseEndpoint;

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
     * The Policy to use for communication.
     */
    protected ProxyPolicy theProxyPolicy;

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

