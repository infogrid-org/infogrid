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

package org.infogrid.mesh.net.a;

import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.EquivalentAlreadyException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotEquivalentException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeNotBlessedException;
import org.infogrid.mesh.a.AMeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.SimpleExternalizedNetMeshObject;
import org.infogrid.mesh.net.security.CannotObtainLockException;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.net.LockChangedEvent;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.AnetMeshBase;
import org.infogrid.meshbase.net.transaction.NetMeshObjectBecameDeadStateEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectBecamePurgedStateEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectStateEvent;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.ArrayCursorIterator;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.IsDeadException;
import org.infogrid.util.RemoteQueryTimeoutException;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.text.StringRepresentation;

/**
 * Subclasses AMeshObject to add information necessary for NetworkedMeshBases.
 * 
 * To save memory, lock operations are synchronized on theIdentifier instead of a
 * (cleaner) additional object; they cannot be synchronized on the AnetMeshObject itself
 * as this conflicts with property change operations' synchronization which may trigger
 * an attempt to obtain the lock on a different thread.
 */
public class AnetMeshObject
        extends
            AMeshObject
        implements
            NetMeshObject
{
    private static final Log log = Log.getLogInstance( AnetMeshObject.class ); // our own, private logger

    /**
     * Constructor for regular instantiation.
     * 
     * @param identifier the value for the Identifier
     * @param meshBase the MeshBase that this MeshObject belongs to
     * @param created the time this MeshObject was created
     * @param updated the time this MeshObject was last updated
     * @param read the time this MeshObject was last read
     * @param autoDeletes the time this MeshObject will auto-delete
     */
    public AnetMeshObject(
            NetMeshObjectIdentifier identifier,
            AnetMeshBase            meshBase,
            long                    created,
            long                    updated,
            long                    read,
            long                    autoDeletes,
            boolean                 giveUpLock,
            Proxy []                proxies,
            int                     homeProxyIndex,
            int                     proxyTowardsLockIndex )
    {
        super( identifier, meshBase, created, updated, read, autoDeletes );

        theGiveUpLock            = giveUpLock;
        theProxies               = proxies;
        theHomeProxyIndex        = homeProxyIndex;
        theProxyTowardsLockIndex = proxyTowardsLockIndex;
    }

    /**
     * Constructor for re-instantion from external storage.
     * 
     * @param identifier the value for the Identifier
     * @param meshBase the MeshBase that this MeshObject belongs to
     * @param created the time this MeshObject was created
     * @param updated the time this MeshObject was last updated
     * @param read the time this MeshObject was last read
     * @param autoDeletes the time this MeshObject will auto-delete
     */
    public AnetMeshObject(
            NetMeshObjectIdentifier             identifier,
            AnetMeshBase                        meshBase,
            long                                created,
            long                                updated,
            long                                read,
            long                                autoDeletes,
            HashMap<PropertyType,PropertyValue> properties,
            EntityType []                       meshTypes,
            NetMeshObjectIdentifier []          equivalents,
            NetMeshObjectIdentifier []          otherSides,
            RoleType [][]                       roleTypes,
            boolean                             giveUpLock,
            Proxy []                            proxies,
            int                                 homeProxyIndex,
            int                                 proxyTowardsLockIndex )
    {
        super(  identifier,
                meshBase,
                created,
                updated,
                read,
                autoDeletes,
                properties,
                meshTypes,
                equivalents,
                otherSides,
                roleTypes );

        theGiveUpLock            = giveUpLock;
        theProxies               = proxies;
        theHomeProxyIndex        = homeProxyIndex;
        theProxyTowardsLockIndex = proxyTowardsLockIndex;
    }
    
    /**
      * Obtain the globally unique identifier of this MeshObject.
      *
      * @return the globally unique identifier of this MeshObject
      */
    @Override
    public NetMeshObjectIdentifier getIdentifier()
    {
        return (NetMeshObjectIdentifier) super.getIdentifier();
    }

    /**
      * Obtain the NetMeshBase that contains this NetMeshObject.
      *
      * @return the NetMeshBase that contains this NetMeshObject.
      */
    @Override
    public NetMeshBase getMeshBase()
    {
        return (NetMeshBase) theMeshBase;
    }

    /**
     * Find MeshObjects known by their Identifier, in the context of this MeshObject.
     * 
     * @param identifiers the Identifiers of the MeshObjects we are looking for
     * @return the MeshObjects that we found
     */
    @Override
    protected NetMeshObject [] findRelatedMeshObjects(
            MeshObjectIdentifier [] identifiers )
    {
        Proxy       homeProxy = getProxyTowardsHomeReplica();
        NetMeshBase realBase  = (NetMeshBase) theMeshBase;
        
        NetMeshObject [] ret = new NetMeshObject[ identifiers.length ]; // make compiler happy
        if( homeProxy != null ) {
            NetMeshBaseIdentifier networkId = homeProxy.getPartnerMeshBaseIdentifier();

            NetMeshObjectAccessSpecification [] paths = new NetMeshObjectAccessSpecification[ identifiers.length ];
            for( int i=0 ; i<identifiers.length ; ++i ) {
                if( identifiers[i] instanceof NetMeshObjectIdentifier ) {
                    paths[i] = NetMeshObjectAccessSpecification.create( networkId, (NetMeshObjectIdentifier) identifiers[i] );
                }
            }
            try {
                ret = realBase.accessLocally( paths );

            } catch( NetMeshObjectAccessException ex ) {
                log.warn( ex );
                if( ex.isPartialResultAvailable() ) {
                    ret = (NetMeshObject []) ex.getBestEffortResult();
                }
            }

        } else {
            try {
                ret = realBase.accessLocally( identifiers );

            } catch( NetMeshObjectAccessException ex ) {
                log.warn( ex );
                if( ex.isPartialResultAvailable() ) {
                    ret = (NetMeshObject []) ex.getBestEffortResult();
                }
            }
        }
        return ret;
    }

    /**
     * Check whether it is permitted to set this MeshObject's given property to the given
     * value. Subclasses may override this.
     *
     * @param thePropertyType the PropertyType identifing the property to be modified
     * @param newValue the proposed new value for the property
     * @throws NotPermittedException thrown if it is not permitted
     */
    @Override
    public void checkPermittedSetProperty(
            PropertyType  thePropertyType,
            PropertyValue newValue )
        throws
            NotPermittedException
    {
        try {
            if( !tryToObtainLock() ) {
                throw new CannotObtainLockException( this );
            }
        } catch( RemoteQueryTimeoutException ex ) {
            throw new CannotObtainLockException( this, ex );
        }
        super.checkPermittedSetProperty( thePropertyType, newValue );
    }

    /**
     * Check whether it is permitted to bless this MeshObject with the given EntityTypes. Subclasses
     * may override this.
     * 
     * @param types the EntityTypes with which to bless
     * @throws NotPermittedException thrown if it is not permitted
     */
    @Override
    public void checkPermittedBless(
            EntityType [] types )
        throws
            NotPermittedException
    {
        try {
            if( !tryToObtainLock() ) {
                throw new CannotObtainLockException( this );
            }
        } catch( RemoteQueryTimeoutException ex ) {
            throw new CannotObtainLockException( this, ex );
        }
        super.checkPermittedBless( types );
    }
    
    /**
     * Check whether it is permitted to unbless this MeshObject from the given EntityTypes. Subclasses
     * may override this.
     * 
     * @param types the EntityTypes from which to unbless
     * @throws NotPermittedException thrown if it is not permitted
     */
    @Override
    public void checkPermittedUnbless(
            EntityType [] types )
        throws
            NotPermittedException
    {
        try {
            if( !tryToObtainLock() ) {
                throw new CannotObtainLockException( this );
            }
        } catch( RemoteQueryTimeoutException ex ) {
            throw new CannotObtainLockException( this, ex );
        }
        super.checkPermittedUnbless( types );
    }

    /**
     * Check whether it is permitted to delete this MeshObject. This checks both whether the
     * MeshObject itself may be deleted, and whether the relationships it participates in may
     * be deleted (which in turn depends on whether the relationships may be unblessed).
     * Subclasses may override this.
     *
     * @throws NotPermittedException thrown if it is not permitted
     */
    @Override
    public void checkPermittedDelete()
        throws
            NotPermittedException
    {
        try {
            if( !tryToObtainLock() ) {
                throw new CannotObtainLockException( this );
            }
        } catch( RemoteQueryTimeoutException ex ) {
            throw new CannotObtainLockException( this, ex );
        }
        super.checkPermittedDelete();
    }

    /**
     * Internal helper to implement a method.
     */
    @Override
    protected void internalDelete(
            boolean isMaster )
        throws
            TransactionException,
            NotPermittedException
    {
        if( theMeshBase == null ) {
            // this is a loop, do nothing
            return;
        }
        theProxies = null;
        super.internalDelete( isMaster );
    }

    /**
     * Allows us to bless MeshObjects that act as ForwardReferences with EntityTypes
     * that are abstract.
     */
    public void blessForwardReference(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        internalBless( types, false, false, false );
    }

    /**
     * Purge this MeshObject. This must only be invoked by our MeshObjectLifecycleManager
     * and thus is defined down here, not higher up in the inheritance hierarchy.
     */
    public void purge()
        throws
            TransactionException,
            NotPermittedException
    {
        if( theMeshBase == null ) {
            // this is a loop, do nothing
            return;
        }
        NetMeshBase oldMeshBase = (NetMeshBase) theMeshBase;

        NetMeshObjectIdentifier identifier = getIdentifier();
        
        theMeshBase = null; // this needs to happen rather late so the other code still works
        theProxies  = null;
        
        firePurged( oldMeshBase, identifier, System.currentTimeMillis() );
    }
    
    /**
     * Obtain the same MeshObject as ExternalizedMeshObject so it can be easily serialized.
     *
     * @return this MeshObject as ExternalizedMeshObject
     */
    @Override
    public SimpleExternalizedNetMeshObject asExternalized()
    {
        return asExternalized( false );
    }

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * 
     * @param captureProxies if true, this ESimpleExternalizedNetMeshObjectwill contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @return this NetMeshObject as ExSimpleExternalizedNetMeshObject
     */
    public SimpleExternalizedNetMeshObject asExternalized(
            boolean captureProxies )
    {
        MeshTypeIdentifier [] types;
        if( theMeshTypes != null && theMeshTypes.size() > 0 ) {
            types = new MeshTypeIdentifier[ theMeshTypes.size() ];

            int i=0;
            for( EntityType current : theMeshTypes.keySet() ) {
                types[i++] = current.getIdentifier();
            }
        } else {
            types = null;
        }
        
        MeshTypeIdentifier [] propertyTypes;
        PropertyValue  [] propertyValues;
        if( theProperties != null && theProperties.size() > 0 ) {
            propertyTypes  = new MeshTypeIdentifier[ theProperties.size() ];
            propertyValues = new PropertyValue[ propertyTypes.length ];

            int i=0;
            for( PropertyType current : theProperties.keySet() ) {
                propertyTypes[i]  = current.getIdentifier();
                propertyValues[i] = theProperties.get( current );
                ++i;
            }
        } else {
            propertyTypes  = null;
            propertyValues = null;
        }
        
        NetMeshObjectIdentifier [] otherSides;
        MeshTypeIdentifier [][]    roleTypes;

        if( theOtherSides != null && theOtherSides.length > 0 ) {
            otherSides = new NetMeshObjectIdentifier[ theOtherSides.length ];

            roleTypes = new MeshTypeIdentifier[ theOtherSides.length][];
            for( int i=0 ; i<theOtherSides.length ; ++i ) {
                otherSides[i] = (NetMeshObjectIdentifier) theOtherSides[i];
                if( theRoleTypes[i] != null && theRoleTypes[i].length > 0 ) {
                    roleTypes[i] = new MeshTypeIdentifier[ theRoleTypes[i].length ];
                    for( int j=0 ; j<roleTypes[i].length ; ++j ) {
                        roleTypes[i][j] = theRoleTypes[i][j].getIdentifier();
                    }
                }
            }
        } else {
            otherSides = null;
            roleTypes  = null;
        }
        
        NetMeshBaseIdentifier [] proxyNames;
        int homeProxyIndex;
        int lockProxyIndex;
        
        if( captureProxies ) {
            if( theProxies == null ) {
                proxyNames = null;
            } else {
                proxyNames = new NetMeshBaseIdentifier[ theProxies.length ];
                for( int i=0 ; i<theProxies.length ; ++i ) {
                    proxyNames[i] = theProxies[i].getPartnerMeshBaseIdentifier();
                }
            }
            homeProxyIndex = theHomeProxyIndex;
            lockProxyIndex = theProxyTowardsLockIndex;
        } else {
            proxyNames     = null;
            homeProxyIndex = HERE_CONSTANT;
            lockProxyIndex = HERE_CONSTANT;
        }
        
        NetMeshObjectIdentifier [] equivalents;
        if( theEquivalenceSetPointers == null ) {
            equivalents = null;
        } else if( theEquivalenceSetPointers[0] == null ) {
            if( theEquivalenceSetPointers[1] == null ) {
                equivalents = null;
            } else {
                equivalents = new NetMeshObjectIdentifier[] { (NetMeshObjectIdentifier) theEquivalenceSetPointers[1] };
            }
        } else if( theEquivalenceSetPointers[1] == null ) {
            equivalents = new NetMeshObjectIdentifier[] {
                    (NetMeshObjectIdentifier) theEquivalenceSetPointers[0]
            };
        } else {
            equivalents = new NetMeshObjectIdentifier[] {
                    (NetMeshObjectIdentifier) theEquivalenceSetPointers[0],
                    (NetMeshObjectIdentifier) theEquivalenceSetPointers[1]
            };
        }
        
        SimpleExternalizedNetMeshObject ret = SimpleExternalizedNetMeshObject.create(
                getIdentifier(),
                types,
                theTimeCreated,
                theTimeUpdated,
                theTimeRead,
                theTimeExpires,
                propertyTypes,
                propertyValues,
                otherSides,
                roleTypes,
                equivalents,
                theGiveUpLock,
                proxyNames,
                homeProxyIndex,
                lockProxyIndex );

        return ret;
    }

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * At the same time, add the provided Proxy to the list of Proxies of this replica.
     * 
     * @param captureProxies if true, this ESimpleExternalizedNetMeshObjectwill contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @return this NetMeshObject as ExSimpleExternalizedNetMeshObject
     */
    public SimpleExternalizedNetMeshObject asExternalizedAndAddProxy(
            boolean captureProxies,
            Proxy   proxyTowardsNewReplica )
    {
        SimpleExternalizedNetMeshObject ret = asExternalized( captureProxies );
        
        if( theProxies == null ) {
            theProxies = new Proxy[] { proxyTowardsNewReplica };
        } else {
            theProxies = ArrayHelper.append( theProxies, proxyTowardsNewReplica, Proxy.class );
        }
        
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );

        return ret;
    }

    /**
     * Determine whether this the home replica.
     *
     * @return returns true if this is the home replica
     */
    public boolean isHomeReplica()
    {
        return theHomeProxyIndex == HERE_CONSTANT;
    }

    /**
      * Determine whether this replica has update rights.
      *
      * @return returns true if this is replica has the update rights
      */
    public boolean doWeHaveLock()
    {
        return theProxyTowardsLockIndex == HERE_CONSTANT;
    }

    /**
      * Attempt to obtain update rights.
      *
      * @return returns true if we have update rights, or we were successful obtaining them.
      */
    public boolean tryToObtainLock()
        throws
            RemoteQueryTimeoutException
    {
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        return tryToObtainLock( realBase.getTryToObtainLockTimesOutAfter() );
    }

    /**
      * Attempt to obtain update rights. Specify a timeout in milliseconds.
      *
      * @return returns true if we have update rights, or we were successful obtaining them.
      */
    public boolean tryToObtainLock(
            long timeout )
        throws
            RemoteQueryTimeoutException
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".tryToObtainLock()" );
        }

        Proxy p;
        synchronized( theIdentifier ) {
            if( theProxyTowardsLockIndex == HERE_CONSTANT ) {
                return true;
            }
            p = theProxies[ theProxyTowardsLockIndex ];
        }
        if( ((NetMeshBase)theMeshBase).refuseToGiveUpLock() ) {
            return false;
        }
        p.tryToObtainLocks( new NetMeshObject[] { this }, timeout );
        
        if( theProxyTowardsLockIndex == HERE_CONSTANT ) {
            fireLockGainedEvent();

            return true;

        } else {
            return false;
        }
    }

    /**
     * Forced recovery of the lock by the home replica.
     */
    public void forceObtainLock()
    {
        Proxy p;
        synchronized( theIdentifier ) {
            if( theProxyTowardsLockIndex == HERE_CONSTANT ) {
                return;
            }
            p = theProxies[ theProxyTowardsLockIndex ];
            theProxyTowardsLockIndex = HERE_CONSTANT;
        }
        p.forceObtainLocks( new NetMeshObject[] { this } );

        fireLockGainedEvent();
    }
    
    /**
      * Determine whether this replica is going to give up update rights if it has them,
      * in case someone asks. This only says "if this replica has update rights, it will
      * give them up when asked". This call makes no statement about whether this replica
      * currently does or does not have update rights.
      *
      * @return if true, this replica will give up update rights when asked
      * @see #setWillGiveUpLock
      */
    public boolean willGiveUpLock()
    {
        return theGiveUpLock;
    }

    /**
      * Set whether this replica will allow update rights to be given up or not.
      * However, if this is not the home replica and a lease for the replica expires, the
      * home replica will still reclaim the lock. Setting this value will not
      * prevent that.
      *
      * @param yesNo if true, this replica will give update rights when asked
      * @see #willGiveUpLock
      */
    public void setWillGiveUpLock(
            boolean yesNo )
    {
        if( yesNo != theGiveUpLock ) {
            theGiveUpLock = yesNo; // currently does not generate events
            
            AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
            realBase.flushMeshObject( this );
        }        
    }

    /**
     * Obtain the Proxy in the direction of the home replica.
     * This may return null, indicating that this replica is the home replica.
     *
     * @return the Proxy in the direction of the home replica
     */
    public Proxy getProxyTowardsHomeReplica()
    {
        if( theHomeProxyIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxies[ theHomeProxyIndex ];
        }
    }
    
    /**
     * Obtain the Proxy in the direction of the update rights for this replica.
     * This may return null, indicating that this replica has the update rights.
     *
     * @return the Proxy in the direction of the update rights
     */
    public Proxy getProxyTowardsLockReplica()
    {
        if( theProxyTowardsLockIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxies[ theProxyTowardsLockIndex ];
        }
    }
    
    /**
     * Obtain all Proxies applicable to this replica.
     *
     * @return all Proxies. This may return null.
     */
    public Proxy [] getAllProxies()
    {
        return theProxies;
    }

    /**
     * Obtain an Iterator over all Proxies applicable to this replica.
     *
     * @return the Iterator
     */
    public CursorIterator<Proxy> proxyIterator()
    {
        return new ArrayCursorIterator<Proxy>( theProxies );
    }

    /**
     * Find a Proxy towards a partner NetMeshBase with a particular NetMeshBaseIdentifier. If such a
     * Proxy does not exist, return null.
     * 
     * @param partnerIdentifier the NNetMeshBaseIdentifierof the partner NetMeshBase
     * @return the found Proxy, or null
     */
    public Proxy findProxyTowards(
            NetMeshBaseIdentifier partnerIdentifier )
    {
        Proxy [] snapshot = theProxies;
        if( snapshot == null ) {
            return null;
        }
        for( Proxy current : snapshot ) {
            if( partnerIdentifier.equals( current.getPartnerMeshBaseIdentifier() )) {
                return current;
            }
        }
        return null;
    }

    /**
      * Surrender update rights when invoked. This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      * @return true if successful, false otherwise.
      */
    public boolean surrenderLock(
            Proxy theProxy )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".surrenderLock( " + theProxy + " )" );
        }

        synchronized( theIdentifier ) {

            if( !theGiveUpLock ) {
                return false;
            }
            if( theProxyTowardsLockIndex != HERE_CONSTANT ) {
                if( theProxies[ theProxyTowardsLockIndex ] != theProxy ) {
                    return false;
                }
            }

            boolean success = false;

            if( theProxies == null ) {
                theProxies               = new Proxy[] { theProxy };
                theProxyTowardsLockIndex = 0;
                success                  = true;

            } else {
                int index = ArrayHelper.findIn( theProxy, theProxies, false );
                
                if( index >=0 ) {
                    theProxyTowardsLockIndex = index;
                    success                  = true;
                }
            }
            
            if( success ) {
                fireLockLostEvent();
                return true;

            } else {
                return false;
            }
        }        
    }

    /**
      * Push update rights to this replica. This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public void pushLock(
            Proxy theProxy )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".pushLock( " + theProxy + " )" );
        }

        synchronized( theIdentifier ) {

            if( theProxyTowardsLockIndex == HERE_CONSTANT ) {
                log.error( this + ": have lock already" );

            } else {
                if( theProxy != theProxies[theProxyTowardsLockIndex] ) {
                    log.error( "proxy that does not have lock cannot push lock to here" );
                }

                theProxyTowardsLockIndex = HERE_CONSTANT;

                fireLockGainedEvent();
            }
        }
    }

    /**
      * Tell the NetMeshObject to make a note of the fact that a new replica of the
      * NetMeshObject is being created in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public void registerReplicationTowards(
            Proxy theProxy )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".registerReplicationTowards( " + theProxy + " )" );
        }

        synchronized( theIdentifier ) {
            if( theProxies == null ) {
                theProxies = new Proxy[] { theProxy };
                theHomeProxyIndex        = HERE_CONSTANT;
                theProxyTowardsLockIndex = HERE_CONSTANT;

            } else {
                for( Proxy p : theProxies ) {
                    if( p == theProxy ) {
                        log.error( this + " - already registered this proxy: " + theProxy );
                        return;
                    }
                }
                theProxies = ArrayHelper.append( theProxies, theProxy, Proxy.class );
            }
        }
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );
    }

    /**
      * Tell the NetMeshObject to remove the note of the fact that a replica of the
      * NetMeshObject exists in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public void unregisterReplicationTowards(
            Proxy theProxy )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".unregisterReplicationTowards( " + theProxy + " )" );
        }

        synchronized( theIdentifier ) {
            if( theProxies == null ) {
                log.error( this + " - no proxies: " + theProxy );
                return;
            }

            // find the proxy
            int foundIndex;
            for( foundIndex = 0 ; foundIndex < theProxies.length ; ++foundIndex ) {
                if( theProxies[foundIndex] == theProxy ) {
                    break;
                }
            }
            if( foundIndex == theProxies.length ) {
                // not found
                log.error( this + " - proxy not found: " + theProxy );
                return;
            }
            if( theProxies.length == 1 ) {
                theProxies = null;
            } else {
                for( int i=foundIndex+1 ; i<theProxies.length ; ++i ) {
                    theProxies[i-1] = theProxies[i];
                }
                theProxies = ArrayHelper.copyIntoNewArray( theProxies, 0, theProxies.length-1, Proxy.class );
            }

            if( theHomeProxyIndex != HERE_CONSTANT ) {
                if( theHomeProxyIndex == foundIndex ) {
                    theHomeProxyIndex = HERE_CONSTANT; // FIXME? Error message?
                } else if( theHomeProxyIndex > foundIndex ) {
                    --theHomeProxyIndex;
                }
            }
            if( theProxyTowardsLockIndex != HERE_CONSTANT ) {
                if( theProxyTowardsLockIndex == foundIndex ) {
                    theProxyTowardsLockIndex = HERE_CONSTANT; // FIXME? Error message?
                } else if( theProxyTowardsLockIndex > foundIndex ) {
                    --theProxyTowardsLockIndex;
                }
            }
        }
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );
    }

    /**
      * Tell the NetMeshObject to make a note of the fact that it is a replica of the
      * NetMeshObject that exists in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public void makeReplicaFrom(
            Proxy theProxy )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".makeReplicaFrom( " + theProxy + " )" );
        }

        synchronized( theIdentifier ) {
            if( theProxies == null ) {
                theProxies = new Proxy[] { theProxy };
                theHomeProxyIndex        = 0;
                theProxyTowardsLockIndex = 0;

            } else {
                int foundIndex = -1;
                for( int i=0 ; i<theProxies.length ; ++i ) {
                    if( theProxy == theProxies[i] ) {
                        foundIndex = i;
                        break;
                    }
                }
                if( foundIndex < 0 ) {
                    theProxies = ArrayHelper.append( theProxies, theProxy, Proxy.class );
                    foundIndex = theProxies.length-1;
                }
                theHomeProxyIndex        = foundIndex;
                theProxyTowardsLockIndex = foundIndex;
            }
        }
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );
    }
    
    /**
     * Bless a replica MeshObject, as a consequence of the blessing of a master replica.
     *
     * @param types the to-be-blessed EntityTypes
     */
    public void rippleBless(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        try {
            internalBless( types, false, true, true );         

        } catch( IsDeadException ex ) {
            if( log.isDebugEnabled()) {
                log.debug( ex );
            }
        }
    }

    /**
     * Unbless a replica MeshObject, as a consequence of the unblessing of a master replica.
     *
     * @param types the to-be-unblessed EntityTypes
     */
    public void rippleUnbless(
            EntityType [] types )
        throws
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException
    {
        try {
            internalUnbless( types, false );

        } catch( IsDeadException ex ) {
            if( log.isDebugEnabled()) {
                log.debug( ex );
            }
        }
    }

    /**
     * Relate two replica MeshObjects, as a consequence of relating other replicas.
     * 
     * @param identifierOfOtherSide the Identifier of the NetMeshObject to relate to
     */
    public synchronized void rippleRelate(
            NetMeshObjectIdentifier identifierOfOtherSide,
            NetMeshBase             mb )
        throws
            RelatedAlreadyException,
            TransactionException
    {
        // we are not trying to accessLocally anything here as this would create a potentially
        // infinite loop of information becoming replicated in.
        
        // first see whether we have it already
        if( theOtherSides != null ) {
            for( int i=0 ; i<theOtherSides.length ; ++i ) {
                if( identifierOfOtherSide.equals( theOtherSides[i] )) {
                    return;
                }
            }
        }
        
        MeshObjectIdentifier [] oldOtherSides = theOtherSides;
        if( theOtherSides == null ) {
            theOtherSides = new MeshObjectIdentifier[]{ identifierOfOtherSide };
            theRoleTypes  = new RoleType[][] { null };
        } else {
            theOtherSides = ArrayHelper.append( theOtherSides, identifierOfOtherSide, MeshObjectIdentifier.class );
            theRoleTypes  = ArrayHelper.append( theRoleTypes,  (RoleType []) null,      RoleType[].class );
        }
        fireNeighborAdded( null, oldOtherSides, identifierOfOtherSide, theOtherSides, theMeshBase );
        
        MeshObject otherSide = theMeshBase.findMeshObjectByIdentifier( identifierOfOtherSide );
        if( otherSide != null ) {
            AnetMeshObject realOtherObject = (AnetMeshObject) otherSide;
            MeshObjectIdentifier [] oldHereSides = realOtherObject.theOtherSides;
            
            if( realOtherObject.theOtherSides == null ) {
                realOtherObject.theOtherSides = new MeshObjectIdentifier[]{ theIdentifier };
                realOtherObject.theRoleTypes  = new RoleType[][] { null };
            } else {
                realOtherObject.theOtherSides = ArrayHelper.append( realOtherObject.theOtherSides, theIdentifier,    MeshObjectIdentifier.class );
                realOtherObject.theRoleTypes  = ArrayHelper.append( realOtherObject.theRoleTypes,  (RoleType []) null, RoleType[].class );
            }
            realOtherObject.fireNeighborAdded( null, oldHereSides, theIdentifier, realOtherObject.theOtherSides, theMeshBase );
        }
    }
    
    /**
     * Unrelate two replica MeshObjects, as a consequence of unrelating other replicas.
     * 
     * @param identifierOfOtherSide the Identifier of the NetMeshObject to unrelate from
     */
    public void rippleUnrelate(
            NetMeshObjectIdentifier identifierOfOtherSide,
            NetMeshBase             mb )
        throws
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        try {
            MeshObject otherSide = mb.accessLocally( identifierOfOtherSide );
            internalUnrelate( otherSide, mb, false );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Bless the relationship of two replica MeshObjects, as a consequence of blessing the relationship
     * of two other replicas.
     * 
     * @param thisEnds the RoleTypes to use for blessing
     * @param identifierOfOtherSide the Identifier of the NetMeshObject that
     *        identifies the relationship that shall be blessed
     */
    public void rippleBless(
            RoleType []             thisEnds,
            NetMeshObjectIdentifier identifierOfOtherSide )
        throws
            EntityNotBlessedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        // we are not trying to accessLocally anything here as this would create a potentially
        // infinite loop of information becoming replicated in.
        
        // first see whether we have it already
        int foundIndex = -1;
        if( theOtherSides != null ) {
            for( int i=0 ; i<theOtherSides.length ; ++i ) {
                if( identifierOfOtherSide.equals( theOtherSides[i] )) {
                    foundIndex = i;
                    break;
                }
            }
        }
        if( foundIndex == -1 ) {
            log.error( "Cannot find existing relationship from " + this + " to " + identifierOfOtherSide );
            return;
        }

        // then, be lenient
        RoleType []         oldRoleTypes = theRoleTypes[ foundIndex ];
        ArrayList<RoleType> toAdd        = new ArrayList<RoleType>();

        if( oldRoleTypes != null ) {
            boolean foundRole = false;

            for( int i=0 ; i<thisEnds.length ; ++i ) {
                for( int j=0 ; j<oldRoleTypes.length ; ++j ) {
                    if( thisEnds[i].equals( oldRoleTypes[j] )) {
                        foundRole = true;
                        break;
                    }
                }
                if( !foundRole ) {
                    toAdd.add( thisEnds[i] );
                }
            }
        } else {
            oldRoleTypes = new RoleType[0];
            for( int i=0 ; i<thisEnds.length ; ++i ) {
                toAdd.add( thisEnds[i] );
            }
        }

        RoleType [] added        = ArrayHelper.copyIntoNewArray( toAdd, RoleType.class );
        RoleType [] newRoleTypes = ArrayHelper.append( oldRoleTypes, added, RoleType.class );
            
        theRoleTypes[foundIndex] = newRoleTypes;

        fireTypesAdded( oldRoleTypes, added, newRoleTypes, identifierOfOtherSide, theMeshBase );
    }

    /**
     * Unbless the relationship of two replica MeshObjects, as a consequence of unblessing the relationship
     * of two other replicas.
     * 
     * @param thisEnds the RoleTypes to use for unblessing
     * @param identifierOfOtherSide the Identifier of the NetMeshObject that
     *        identifies the relationship that shall be unblessed
     */
    public void rippleUnbless(
            RoleType []             thisEnds,
            NetMeshObjectIdentifier identifierOfOtherSide )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        try {
            MeshObject otherSide = getMeshBase().accessLocally( identifierOfOtherSide );
            internalUnbless( thisEnds, otherSide, false );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Add a replica as an equivalent, as a consequence of adding a different replica as equivalent.
     * 
     * @param identifierOfEquivalent the Identifier of the replica NetMeshObject
     */
    public void rippleAddAsEquivalent(
            NetMeshObjectIdentifier identifierOfEquivalent )
        throws
            EquivalentAlreadyException,
            TransactionException,
            NotPermittedException
    {
        try {
            MeshObject equivalent = getMeshBase().accessLocally( identifierOfEquivalent );
            internalAddAsEquivalent( equivalent, false );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }
    
    /**
     * Remove a replica as an equivalent, as a consequence of removing a different replica as equivalent.
     * 
     * @param identifierOfEquivalent the Identifier of the replica NetMeshObject
     */
    public void rippleRemoveAsEquivalent(
            NetMeshObjectIdentifier identifierOfEquivalent )
        throws
            NotEquivalentException,
            TransactionException,
            NotPermittedException
    {
        try {
            MeshObject equivalent = getMeshBase().accessLocally( identifierOfEquivalent );
            internalRemoveAsEquivalent( equivalent, false );

        } catch( NetMeshObjectAccessException ex ) {
            log.error( ex );
        }
    }

    /**
     * Change the values of Properties on a replica, as a consequence of changing the value of the property
     * in another replica.
     *
     * @param types the PropertyTypes
     * @param values the new value, in the same sequence as the PropertyTypes
     */
    public void rippleSetPropertyValues(
            PropertyType []  types,
            PropertyValue [] values )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        internalSetPropertyValues( types, values, getTimeUpdated(), false );
    }
    
    /**
     * Change the values of Properties on a replica, as a consequence of changing the value of the property
     * in another replica.
     *
     * @param map the Map of PropertyTypes to PropertyValues
     */
    public void rippleSetPropertyValues(
            Map<PropertyType,PropertyValue> map )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        // FIXME not the world's most efficient algorithm
        PropertyType []  types  = new PropertyType[ map.size() ];
        PropertyValue [] values = new PropertyValue[ types.length ];
        
        int i=0;
        for( PropertyType currentType : map.keySet() ) {
            PropertyValue currentValue = map.get( currentType );
            types[i]  = currentType;
            values[i] = currentValue;
            ++i;
        }
        internalSetPropertyValues( types, values, getTimeUpdated(), false );
    }
    
    /**
     * Delete a replica as a consequence of deleting another replica.
     */
    public void rippleDelete()
        throws
            TransactionException,
            NotPermittedException
    {
        internalDelete( false );
    }

    /**
     * Convert into a String representation, for debugging.
     *
     * @return String representation
     */
    @Override
    public String toString()
    {
        // This method is sometimes invoked by the NetBeans request monitor:
        // org.netbeans.modules.web.monitor.server.MonitorFilter.recordRequestAttributes(MonitorFilter.java:1354)
        // This should not happen in production. (Just in case you notice in the debugger)
        
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theIdentifier",
                    "theTimeCreated",
                    "theTimeUpdated",
                    "theTimeRead",
                    "theTimeExpires",
                    "theMeshBase.getNetworkIdentifier()"
                },
                new Object[] {
                    theIdentifier,
                    theTimeCreated,
                    theTimeUpdated,
                    theTimeRead,
                    theTimeExpires,
                    theMeshBase != null ? (((NetMeshBase)theMeshBase).getIdentifier().toExternalForm()) : null
                });
    }
    
    /**
     * Fire an event that indicates that this replica gained update rights.
     */
    protected void fireLockGainedEvent()
    {
        LockChangedEvent.GainedLock theEvent = new LockChangedEvent.GainedLock( this );

        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event that indicates that this replica lost update rights.
     */
    protected void fireLockLostEvent()
    {
        LockChangedEvent.LostLock theEvent = new LockChangedEvent.LostLock( this );

        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;
        realBase.flushMeshObject( this );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that this MeshObject was purged.
     * We pass in the MeshBase, because the member variable has already been zero'd.
     * 
     * 
     * @param oldMeshBase the MeshBase this MeshObject used to belong to
     * @param canonicalIdentifier the canonical Identifier that this MeshObject used to have
     */
    protected void firePurged(
            MeshBase        oldMeshBase,
            MeshObjectIdentifier canonicalIdentifier,
            long            time )
    {
        MeshObjectStateEvent theEvent = new NetMeshObjectBecamePurgedStateEvent(
                this,
                canonicalIdentifier,
                determineIncomingProxyIdentifier( oldMeshBase ),
                time );
        
        oldMeshBase.getCurrentTransaction().addChange( theEvent );
        
        firePropertyChange( theEvent );
    }

    /**
     * This simply invokes the superclass method. It is replicated here in order to
     * get around the restrictions of the <tt>protected</tt> keyword without
     * having to make it public.
     *
     * @param thePropertyTypes the sequence of PropertyTypes to set
     * @param newValues the sequence of PropertyValues for the PropertyTypes
     * @param timeUpdated the time to use as the new timeUpdated
     * @param isMaster if true, check permissions
     */
    @Override
    protected PropertyValue [] internalSetPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] newValues,
            long             timeUpdated,
            boolean          isMaster )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        return super.internalSetPropertyValues( thePropertyTypes, newValues, timeUpdated, isMaster );
    }

    /**
      * Fire an event indicating a change of a property of this MeshObject. This will actually
      * create a MeshObjectPropertyChangeEvent.
      *
      * @param thePropertyType the PropertyType whose value changed
      * @param oldValue the value of the PropertyValue prior to the change
      * @param newValue the value of the PropertyValue now, after the change
      * @param mb the MeshBase to use
      */
    @Override
    protected void firePropertyChange(
            PropertyType  thePropertyType,
            PropertyValue oldValue,
            PropertyValue newValue,
            MeshBase      mb )
    {
        NetMeshObjectPropertyChangeEvent theEvent
                = new NetMeshObjectPropertyChangeEvent(
                        this,
                        thePropertyType,
                        oldValue,
                        newValue,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Helper method to determine the NetMeshBaseIdentifier of the incoming Proxy, if any.
     * 
     * @return the NNetMeshBaseIdentifierof the incoming Proxy, if any
     */
    protected NetMeshBaseIdentifier determineIncomingProxyIdentifier(
            MeshBase mb )
    {
        Proxy p = ((NetMeshBase)mb).determineIncomingProxy();

        NetMeshBaseIdentifier ret;
        if( p != null ) {
             ret = p.getPartnerMeshBaseIdentifier();
        } else {
            ret = null;
        }
        return ret;
    }

    /**
     * Fire an event indicating a change in the set of neighbors of this MeshObject.
     * 
     * @param oldValue the value of the PropertyValue prior to the change
     * @param added the added MeshObjectIdentifier
     * @param newValue the value of the PropertyValue now, after the change
     * @param addedRoleTypes the newly added RoleTypes, if any
     * @param mb the MeshBase to use
     */
    @Override
    protected void fireNeighborAdded(
            RoleType []             addedRoleTypes,
            MeshObjectIdentifier [] oldValue,
            MeshObjectIdentifier    added,
            MeshObjectIdentifier [] newValue,
            MeshBase                mb )
    {
        NetMeshObjectNeighborAddedEvent theEvent
                = new NetMeshObjectNeighborAddedEvent(
                        this,
                        addedRoleTypes,
                        copyIntoNetMeshObjectIdentifierArray( oldValue ),
                        (NetMeshObjectIdentifier) added,
                        copyIntoNetMeshObjectIdentifierArray( newValue ),
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
      * Fire an event indicating a change in the set of neighbors of this MeshObject.
      *
      * @param oldValue the set of neighbors prior to the change
      * @param removed the removed neighbor
      * @param newValue the set of neighbors now, after the change
      * @param mb the MeshBase to use
      */
    @Override
    protected void fireNeighborRemoved(
            MeshObjectIdentifier [] oldValue,
            MeshObjectIdentifier    removed,
            MeshObjectIdentifier [] newValue,
            MeshBase                mb )
    {
        NetMeshObjectNeighborRemovedEvent theEvent
                = new NetMeshObjectNeighborRemovedEvent(
                        this,
                        copyIntoNetMeshObjectIdentifierArray( oldValue ),
                        (NetMeshObjectIdentifier)    removed,
                        copyIntoNetMeshObjectIdentifierArray( newValue ),
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more MeshTypes have been added to this MeshObject.
     *
     * @param addedTypes the added MeshTypes
     * @param mb the MeshBase to use
     */
    @Override
    protected void fireTypesAdded(
            EntityType [] oldTypes,
            EntityType [] addedTypes,
            EntityType [] newTypes,
            MeshBase      mb )
    {
        NetMeshObjectTypeAddedEvent theEvent
                = new NetMeshObjectTypeAddedEvent(
                        this,
                        oldTypes,
                        addedTypes,
                        newTypes,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more MeshTypes have been removed from this MeshObject.
     *
     * @param removedTypes the removed MeshTypes
     * @param mb the MeshBase to use
     */
    @Override
    protected void fireTypesRemoved(
            EntityType [] oldTypes,
            EntityType [] removedTypes,
            EntityType [] newTypes,
            MeshBase      mb )
    {
        NetMeshObjectTypeRemovedEvent theEvent
                = new NetMeshObjectTypeRemovedEvent(
                        this,
                        oldTypes,
                        removedTypes,
                        newTypes,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more RoleTypes were added to the relationship of this
     * MeshObject to another MeshObject.
     *
     * @param thisEnd the RoleTypes that were added
     * @param otherSide the other side of this relationship
     * @param mb the MeshBase to use
     */
    @Override
    protected void fireTypesAdded(
            RoleType [] oldRoleTypes,
            RoleType [] addedRoleTypes,
            RoleType [] newRoleTypes,
            MeshObject  otherSide,
            MeshBase    mb )
    {
        NetMeshObjectRoleAddedEvent theEvent
                = new NetMeshObjectRoleAddedEvent(
                        this,
                        oldRoleTypes,
                        addedRoleTypes,
                        newRoleTypes,
                        (NetMeshObject) otherSide,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more RoleTypes were added to the relationship of this
     * MeshObject to another MeshObject.
     *
     * @param thisEnd the RoleTypes that were added
     * @param otherSide the other side of this relationship
     * @param mb the MeshBase to use
     */
    protected void fireTypesAdded(
            RoleType []          oldRoleTypes,
            RoleType []          addedRoleTypes,
            RoleType []          newRoleTypes,
            MeshObjectIdentifier identifierOfOtherSide,
            MeshBase             mb )
    {
        NetMeshObjectRoleAddedEvent theEvent
                = new NetMeshObjectRoleAddedEvent(
                        this,
                        oldRoleTypes,
                        addedRoleTypes,
                        newRoleTypes,
                        (NetMeshObjectIdentifier) identifierOfOtherSide,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more RoleTypes wwere removed from the relationship of this
     * MeshObject to another MeshObject.
     *
     * @param thisEnd the RoleTypes that were removed
     * @param otherSide the other side of this relationship
     * @param mb the MeshBase to use
     */
    @Override
    protected void fireTypesRemoved(
            RoleType [] oldRoleTypes,
            RoleType [] removedRoleTypes,
            RoleType [] newRoleTypes,
            MeshObject  otherSide,
            MeshBase    mb )
    {
        NetMeshObjectRoleRemovedEvent theEvent
                = new NetMeshObjectRoleRemovedEvent(
                        this,
                        oldRoleTypes,
                        removedRoleTypes,
                        newRoleTypes,
                        (NetMeshObject) otherSide,
                        determineIncomingProxyIdentifier( theMeshBase ),
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that this MeshObject was deleted.
     * We pass in the MeshBase, because the member variable has already been zero'd.
     * 
     * @param oldMeshBase the MeshBase this MeshObject used to belong to
     */
    @Override
    protected void fireDeleted(
            MeshBase       oldMeshBase,
            MeshObjectIdentifier canonicalMeshObjectName,
            long           time )
    {
        NetMeshObjectBecameDeadStateEvent theEvent
                = new NetMeshObjectBecameDeadStateEvent(
                        this,
                        canonicalMeshObjectName,
                        determineIncomingProxyIdentifier( oldMeshBase ),
                        time );
        
        oldMeshBase.getCurrentTransaction().addChange( theEvent );
        
        firePropertyChange( theEvent );
    }

    /**
     * Helper method to get the type of array right. FIXME there must be a less inefficient
     * way of doing this without disrupting all the code in superclasses.
     *
     * @param org the original array
     * @return the new array
     */
    protected NetMeshObjectIdentifier [] copyIntoNetMeshObjectIdentifierArray(
            MeshObjectIdentifier [] org )
    {
        if( org == null ) {
            return null;
        }
        NetMeshObjectIdentifier [] ret = new NetMeshObjectIdentifier[ org.length ];
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = (NetMeshObjectIdentifier) org[i];
        }
        return ret;
    }
    
    /**
     * Obtain the start part of a StringRepresentation of this MeshObject that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkStart(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase )
    {
        String key;
        if( isDefaultMeshBase ) {
            if( isHomeObject() ) {
                key = DEFAULT_MESH_BASE_HOME_LINK_START_ENTRY;
            } else {
                key = DEFAULT_MESH_BASE_LINK_START_ENTRY;
            }
        } else {
            if( isHomeObject() ) {
                key = NON_DEFAULT_MESH_BASE_HOME_LINK_START_ENTRY;
            } else {
                key = NON_DEFAULT_MESH_BASE_LINK_START_ENTRY;
            }
        }

        String meshBaseExternalForm = theMeshBase.getIdentifier().toExternalForm();
        
        NetMeshObjectIdentifier realIdentifier = (NetMeshObjectIdentifier) theIdentifier;
        String meshObjectExternalForm;
        if( realIdentifier.getNetMeshBaseIdentifier().equals( theMeshBase.getIdentifier() )) {
            meshObjectExternalForm = realIdentifier.toLocalExternalForm();
        } else {
            meshObjectExternalForm = realIdentifier.toExternalForm();
        }
        
        meshObjectExternalForm = meshObjectExternalForm.replaceAll( "#" , "%23" );

        String ret = rep.formatEntry(
                ResourceHelper.getInstance( getClass() ), // dispatch to the right subtype
                key,
                meshObjectExternalForm,
                contextPath,
                meshBaseExternalForm );

        return ret;
    }

    /**
     * Obtain the end part of a StringRepresentation of this MeshObject that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkEnd(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase )
    {
        String key;
        if( isDefaultMeshBase ) {
            if( isHomeObject() ) {
                key = DEFAULT_MESH_BASE_HOME_LINK_END_ENTRY;
            } else {
                key = DEFAULT_MESH_BASE_LINK_END_ENTRY;
            }
        } else {
            if( isHomeObject() ) {
                key = NON_DEFAULT_MESH_BASE_HOME_LINK_END_ENTRY;
            } else {
                key = NON_DEFAULT_MESH_BASE_LINK_END_ENTRY;
            }
        }

        String meshBaseExternalForm = theMeshBase.getIdentifier().toExternalForm();
        
        NetMeshObjectIdentifier realIdentifier = (NetMeshObjectIdentifier) theIdentifier;
        String meshObjectExternalForm;
        if( realIdentifier.getNetMeshBaseIdentifier().equals( theMeshBase.getIdentifier() )) {
            meshObjectExternalForm = realIdentifier.toLocalExternalForm();
        } else {
            meshObjectExternalForm = realIdentifier.toExternalForm();
        }
        
        String ret = rep.formatEntry(
                ResourceHelper.getInstance( getClass() ), // dispatch to the right subclass
                key,
                meshObjectExternalForm,
                contextPath,
                meshBaseExternalForm );

        return ret;
    }

    /**
     * Flag indicating our willingness to give up the lock when asked.
     */
    protected boolean theGiveUpLock = false;

    /**
     * The Proxies to other NetworkedMeshBases that contain the replicas that are
     * closest in the replication graph. This may be null.
     */
    protected Proxy [] theProxies;

    /**
     * The index into theProxies that represents our home Proxy. If this is HERE_CONSTANT, it
     * indicates that this is the home replica.
     */
    protected int theHomeProxyIndex = HERE_CONSTANT;
    
    /**
     * The index into theProxies that represents the Proxy towards the lock. If this
     * is HERE_CONSTANT, it indicates that this replica has the lock.
     */
    protected int theProxyTowardsLockIndex = HERE_CONSTANT;
    
    /** 
     * Special value indicating this replica (instead of another, reached through a Proxy).
     */
    public static final int HERE_CONSTANT = -1;
}
