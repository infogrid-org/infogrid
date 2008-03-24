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

package org.infogrid.meshbase.net.a;

import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.AnetMeshObject;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObject;
import org.infogrid.mesh.net.security.CannotObtainLockException;
import org.infogrid.mesh.security.MustNotDeleteHomeObjectException;

import org.infogrid.meshbase.a.AMeshBase;
import org.infogrid.meshbase.a.AMeshBaseLifecycleManager;
import org.infogrid.meshbase.a.AMeshObjectEquivalenceSetComparator;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.transaction.NetChange;
import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.ReplicaPurgedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.modelbase.MeshTypeNotFoundException;
import org.infogrid.modelbase.ModelBase;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.FactoryException;
import org.infogrid.util.RemoteQueryTimeoutException;
import org.infogrid.util.logging.Log;

import java.util.HashMap;

/**
 *
 */
public class AnetMeshBaseLifecycleManager
        extends
            AMeshBaseLifecycleManager
        implements
            NetMeshBaseLifecycleManager
{
    private static final Log log = Log.getLogInstance( AnetMeshBaseLifecycleManager.class ); // our own, private logger

    /**
     * Constructor. The application developer should not call this or a subclass constructor; use
     * MeshBase.getMeshObjectLifecycleManager() instead.
     * 
     * @param base the MeshBase on which this MeshObjectLifecycleManager works
     */
    protected AnetMeshBaseLifecycleManager(
            AnetMeshBase base )
    {
        super( base );
    }

    /**
     * <p>Obtain the MeshBase that this MeshBaseLifecycleManager works on.</p>
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * @return the MeshBase that this MMeshBaseLifecycleManagerworks on
     */
    @Override
    public NetMeshBase getMeshBase()
    {
        return (NetMeshBase) theMeshBase;
    }

    /**
     * <p>Create a new MeshObject without a type.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     *
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     *
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     *
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    @Override
    public AnetMeshObject createMeshObject()
        throws
            TransactionException
    {
        MeshObject ret = super.createMeshObject();
        return (AnetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     *
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     *
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     *
     * @param type the EntityType with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            EntityType type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        MeshObject ret = super.createMeshObject( type );
        return (NetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with zero or more EntityTypes.</p>
     *
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     *
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     *
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            EntityType [] types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        MeshObject ret = super.createMeshObject( types );
        return (NetMeshObject) ret;
    }

    /**
     * <p>Create a new MeshObject (aka EntityType instance) initially without a type.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExtIdentifierNotUniqueExceptionrown if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    @Override
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = super.createMeshObject( identifier );
        return (NetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param type the EntityType with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType      type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = super.createMeshObject( identifier, type );
        return (NetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = super.createMeshObject( identifier, types );
        return (NetMeshObject) ret;
    }

    /**
     * <p>Create a new MeshObject (aka EntityType instance) initially without a type.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalIdentifierNotUniqueExceptionif a MeshObject exists already in this MeshBase with the specified Identifier
     */
    @Override
    public AnetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = super.createMeshObject( identifier, timeCreated, timeUpdated, timeRead, timeAutoDeletes );
        return (AnetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param type the EntityType with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNaIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType      type,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        MeshObject ret = super.createMeshObject( identifier, type, timeCreated, timeUpdated, timeRead, timeAutoDeletes );
        return (NetMeshObject) ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with zero or more EntityTypes.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNaIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    @Override
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        MeshObject ret = super.createMeshObject( identifier, types, timeCreated, timeUpdated, timeRead, timeAutoDeletes );
        return (NetMeshObject) ret;
    }

    /**
     * Create a NetMeshObject that is a replica. Using this method, the created NetMeshObject initially
     * does not have update rights, and its home replica resides in a different MeshBase. The
     * different MeshBase is identified by the Proxy through which this MeshBase communicates with it.
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in System.currentTimeMillis() format
     * @param proxyTowardsHomeAndLock the Proxy in whose direction the home replica and the updateable replica can be found
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNamIdentifierNotUniqueExceptiona MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes,
            boolean         giveUpLock,
            Proxy           proxyTowardsHomeAndLock )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        return createMeshObject(
                identifier,
                types,
                timeCreated,
                timeUpdated,
                timeRead,
                timeAutoDeletes,
                giveUpLock,
                new Proxy[] { proxyTowardsHomeAndLock },
                0,
                0 );
    }
    
    /**
     * Create a NetMeshObject that is a replica. Using this method, the created NetMeshObject initially
     * does not have update rights, and its home replica resides in a different MeshBase. It may also be
     * linked to other MeshBases in the replication graph.
     * 
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in System.currentTimeMillis() format
     * @param proxies the Proxies in whose direction the other related nodes of the replication graph may be found
     * @param homeProxyIndex the index, into the proxies array, that identifies the Proxy in whose direction the home replica may be found
     * @param proxyTowardsLockIndex the index, into the proxies array, that identifies the Proxy in whose direction the update rights may be found
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws MeshObjectIdentifierNotUniqueException exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes,
            boolean         giveUpLock,
            Proxy []        proxies,
            int             homeProxyIndex,
            int             proxyTowardsLockIndex )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        if( identifier == null ) {
            throw new IllegalArgumentException( "null Identifier" );
        }

        long now = determineCreationTime();
        if( timeCreated < 0 ) {
            timeCreated = now;
        }
        if( timeUpdated < 0 ) {
            timeUpdated = now;
        }
        if( timeRead < 0 ) {
            timeRead = now;
        }
        // not timeAutoDeletes

        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

        Transaction tx = realBase.checkTransaction();

        MeshObject existing = findInStore( identifier );
        if( existing != null ) {
            throw new MeshObjectIdentifierNotUniqueException( existing );
        }

        AnetMeshObject ret = instantiateMeshObjectImplementation(
                identifier,
                timeCreated,
                timeUpdated,
                timeRead,
                timeAutoDeletes,
                giveUpLock,
                proxies,
                homeProxyIndex,
                proxyTowardsLockIndex );

        if( types != null && types.length > 0 ) {
            try {
                ret.bless( types );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }

        putIntoStore( ret );

        Proxy             incomingProxy           = realBase.determineIncomingProxy();
        NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

        tx.addChange( new NetMeshObjectCreatedEvent(
                realBase,
                ret,
                incomingProxyIdentifier ));

        assignOwner( ret );

        return ret;        
    }

    /**
     * <p>Semantically delete a number of MeshObjects at the same time.</p>
     * 
     * <p>This call either succeeds or fails in total: if one or more of the specified MeshObject cannot be
     *    deleted for some reason, none of the other MeshObjects will be deleted either.</p>
     * 
     * @param theObjects the MeshObjects to be semantically deleted
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    @Override
    public synchronized void deleteMeshObjects(
            MeshObject [] theObjects )
        throws
            TransactionException,
            NotPermittedException
    {
        AMeshBase realBase = (AMeshBase) theMeshBase;
        long      now      = System.currentTimeMillis();
        
        Transaction tx = realBase.checkTransaction();

        MeshObject home = realBase.getHomeObject();

        for( int i=0 ; i<theObjects.length ; ++i ) {
            if( theObjects[i] == null ) {
                throw new NullPointerException( "MeshObject at index " + i + " is null" );
            }
            if( theObjects[i].getMeshBase() != realBase ) {
                throw new IllegalArgumentException( "cannot delete MeshObjects in a different MeshBases" );
            }
            if( theObjects[i] == home ) {
                throw new MustNotDeleteHomeObjectException( home );
            }
        }
        for( int i=0 ; i<theObjects.length ; ++i ) {
            try {
                ((AnetMeshObject)theObjects[i]).tryToObtainLock();

            } catch( RemoteQueryTimeoutException ex ) {
                log.warn( ex );
                throw new CannotObtainLockException( (NetMeshObject) theObjects[i], ex );
            }

            ((AnetMeshObject)theObjects[i]).checkPermittedDelete(); // this may throw NotPermittedException
        }
        for( int i=0 ; i<theObjects.length ; ++i ) {
            AnetMeshObject  current              = (AnetMeshObject) theObjects[i];
            MeshObjectIdentifier currentCanonicalName = current.getIdentifier();
            
            current.delete();
            removeFromStore( current.getIdentifier() );

            tx.addChange( createDeletedEvent( currentCanonicalName, current, now ));
        }
    }

    /**
     * Helper method to instantiate the right subclass of MeshObject. This makes the creation
     * of subclasses of MMeshBase and this class much easier.
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. This must not be null.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete
     * @return the created MeshObject
     */
    @Override
    protected AnetMeshObject instantiateMeshObjectImplementation(
            MeshObjectIdentifier identifier,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes )
    {
        AnetMeshObject ret = new AnetMeshObject(
                (NetMeshObjectIdentifier) identifier,
                (AnetMeshBase) theMeshBase,
                timeCreated,
                timeUpdated,
                timeRead,
                timeAutoDeletes,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock(),
                null,
                AnetMeshObject.HERE_CONSTANT,
                AnetMeshObject.HERE_CONSTANT );
        
        return ret;
    }
    
    /**
     * Helper method to instantiate the right subclass of MeshObject. This makes the creation
     * of subclasses of MMeshBase and this class much easier.
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. This must not be null.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete
     * @return the created MeshObject
     */
    protected AnetMeshObject instantiateMeshObjectImplementation(
            MeshObjectIdentifier identifier,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeAutoDeletes,
            boolean         giveUpLock,
            Proxy []        proxies,
            int             homeProxyIndex,
            int             proxyTowardsLockIndex )
    {
        AnetMeshObject ret = new AnetMeshObject(
                (NetMeshObjectIdentifier) identifier,
                (AnetMeshBase) theMeshBase,
                timeCreated,
                timeUpdated,
                timeRead,
                timeAutoDeletes,
                giveUpLock,
                proxies,
                homeProxyIndex,
                proxyTowardsLockIndex );
        
        return ret;
    }

    /**
     * Factored out method to instantiate a recreated MeshObject. This exists to make
     * it easy to override it in subclasses.
     * 
     * @param identifier the Identifier of the MeshObject
     * @param timeCreated the time it was created
     * @param timeUpdated the time it was last udpated
     * @param timeRead the time it was last read
     * @param timeAutoDeletes the time it will auto-delete
     * @param properties the properties of the MeshObject
     * @param types the EntityTypes of the MeshObject
     * @param equivalents either an array of length 2, or null. If given, contains the left and right equivalence pointers.
     * @param otherSides the Identifiers of the MeshObject's neighbors, if any
     * @param roleTypes the RoleTypes in which this MeshObject participates with its neighbors
     * @param theObjectBeingParsed the externalized representation of the MeshObject
     * @return the recreated AMeshObject
     */
    @Override
    protected AnetMeshObject instantiateRecreatedMeshObject(
            MeshObjectIdentifier                identifier,
            AMeshBase                           meshBase,
            long                                timeCreated,
            long                                timeUpdated,
            long                                timeRead,
            long                                timeAutoDeletes,
            HashMap<PropertyType,PropertyValue> properties,
            EntityType []                       types,
            MeshObjectIdentifier []             equivalents,
            MeshObjectIdentifier []             otherSides,
            RoleType [][]                       roleTypes,
            ExternalizedMeshObject              theObjectBeingParsed )
    {
        AnetMeshBase              realBase              = (AnetMeshBase)              meshBase;
        ExternalizedNetMeshObject realObjectBeingParsed = (ExternalizedNetMeshObject) theObjectBeingParsed;
        
//        if(    identifier instanceof NetMeshObjectIdentifier ) {
//            NetMeshObjectIdentifier realIdentifier = (NetMeshObjectIdentifier) identifier;
//
//            if( realBase.getIdentifier().toExternalForm().equals( realIdentifier.getPrefix() )) {
//                identifier = MeshObjectIdentifier.create( realIdentifier.getLocalId() );
//            }
//        }
        
        boolean giveUpLock = realObjectBeingParsed.getGiveUpLock();

        NetMeshBaseIdentifier [] proxyNetworkIdentifiers = realObjectBeingParsed.getProxyNames();

        int proxyTowardsHomeIndex = -1;
        int proxyTowardsLockIndex = -1;
        
        Proxy [] proxies = new Proxy[ proxyNetworkIdentifiers.length ];

        for( int i=0 ; i<proxies.length ; ++i ) {
            if( proxyNetworkIdentifiers[i].equals( realObjectBeingParsed.getProxyTowardsHomeNetworkIdentifier() )) {
                proxyTowardsHomeIndex = i;
            }
            if( proxyNetworkIdentifiers[i].equals( realObjectBeingParsed.getProxyTowardsLockNetworkIdentifier() )) {
                proxyTowardsLockIndex = i;
            }

            try {
                proxies[i] = realBase.obtainProxyFor( proxyNetworkIdentifiers[i], null ); // FIXME? What is the right CoherenceSpecification here?

            } catch( FactoryException ex ) {
                log.error( ex );
            }
        }
        
        AnetMeshObject ret = new AnetMeshObject(
                (NetMeshObjectIdentifier) identifier,
                (AnetMeshBase) realBase,
                timeCreated,
                timeUpdated,
                timeRead,
                timeAutoDeletes,
                properties,
                types,
                (NetMeshObjectIdentifier []) equivalents,
                (NetMeshObjectIdentifier []) otherSides,
                roleTypes,
                giveUpLock,
                proxies,
                proxyTowardsHomeIndex,
                proxyTowardsLockIndex );

        return ret;
    }

    /**
     * Purge a non-home replica.
     *
     * @param replica the non-home replica
     */
    public void purgeReplica(
            NetMeshObject replica )
        throws
            TransactionException,
            MustNotDeleteHomeObjectException
    {
        purgeReplicas( new NetMeshObject[] { replica } );
    }
    
    /**
     * Purge a non-home replicas.
     *
     * @param replicas the non-home replicas
     */
    public void purgeReplicas(
            NetMeshObject [] replicas )
        throws
            TransactionException,
            MustNotDeleteHomeObjectException
    {
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

        Transaction tx = realBase.checkTransaction();

        long time = System.currentTimeMillis();
        
        // we don't do that here because it's the slave replica
        // theObject.checkPermittedDelete(); // this may throw NotPermittedException

        Proxy             incomingProxy           = realBase.determineIncomingProxy();
        NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

        for( NetMeshObject current : replicas ) {
            try {
                ((AnetMeshObject) current).purge();
                
                NetMeshObjectIdentifier identifier = current.getIdentifier();
                
                removeFromStore( identifier );
                
                tx.addChange( new ReplicaPurgedEvent(
                        realBase,
                        identifier,
                        current,
                        incomingProxyIdentifier,
                        time ));

            } catch( NotPermittedException ex ) {
                log.error( ex );
            }
        }
    }

    /**
     * Instantiate a replica MeshObject in this MeshBase, thereby setting 
     * up a branch in the replication graph.
     *
     * @param original the original MeshObject that we copy
     * @param proxy the Proxy that conveyed this command
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public NetMeshObject rippleCreate(
            ExternalizedNetMeshObject original,
            NetMeshBaseIdentifier         proxy )
        throws
            NotPermittedException,
            TransactionException
    {
        return rippleCreateOrSynchronize( original, proxy, true );
    }    

    /**
     * Delete a replica MeshObject in this MeshBase, thereby removing a
     * branch in the replication graph.
     * 
     * @param identifier the Identifier of the MeshObject whose replica is to be deleted
     * @param proxy the Proxy that conveyed this command
     * @return the deleted MeshObject, now dead
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public NetMeshObject rippleDelete(
            MeshObjectIdentifier  identifier,
            NetMeshBaseIdentifier proxy,
            long                  time )
        throws
            TransactionException
    {
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

        AnetMeshObject  theObject      = (AnetMeshObject) realBase.findMeshObjectByIdentifier( identifier );
        MeshObjectIdentifier realIdentifier = theObject.getIdentifier();
        
        Transaction tx = realBase.checkTransaction();

        // we don't do that here because it's the slave replica
        // theObject.checkPermittedDelete(); // this may throw NotPermittedException

        try {
            theObject.delete();
            removeFromStore( theObject.getIdentifier() );

            Proxy             incomingProxy           = realBase.determineIncomingProxy();
            NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

            tx.addChange( new NetMeshObjectDeletedEvent(
                    realBase,
                    realIdentifier,
                    theObject,
                    incomingProxyIdentifier,
                    time ));

        } catch( NotPermittedException ex ) {
            log.error( ex );
        }
        return theObject;
    }
    
    /**
     * Resynchronize a local replica to the provided SimpleExternalizedNetMeshObject.
     * 
     * @param original the original MeshObject that we copy
     * @param proxy the Proxy that conveyed this command
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public NetMeshObject resynchronize(
            ExternalizedNetMeshObject original,
            NetMeshBaseIdentifier     proxy )
        throws
            NotPermittedException,
            TransactionException
    {
        return rippleCreateOrSynchronize( original, proxy, false );
    }

    /**
     * Common implementation method for rippleCreate and resynchronize.
     */
    protected NetMeshObject rippleCreateOrSynchronize(
            ExternalizedNetMeshObject original,
            NetMeshBaseIdentifier         proxyName,
            boolean                   isCreate )
        throws
            NotPermittedException,
            TransactionException
    {
        AnetMeshBase realBase  = (AnetMeshBase) theMeshBase;
        ModelBase    modelBase = theMeshBase.getModelBase();

        Proxy proxy;
        try {
            proxy = realBase.obtainProxyFor( proxyName, null );

        } catch( FactoryException ex ) {
            log.error( ex );
            return null;
        }
        
        NetMeshObjectIdentifier identifier = original.getIdentifier();
//        if( identifier instanceof NetMeshObjectIdentifier ) {
//            NetMeshObjectIdentifier realIdentifier = (NetMeshObjectIdentifier) identifier;
//
//            if( realBase.getIdentifier().getCanonicalForm().equals( realIdentifier.getPrefix() )) {
//                identifier = MeshObjectIdentifier.create( realIdentifier.getLocalId() );
//            }
//        }
        
        Transaction tx = realBase.checkTransaction();

        int typeCounter = 0;
        EntityType [] types;
        if( original.getExternalTypeIdentifiers() != null && original.getExternalTypeIdentifiers().length > 0 ) {
            types = new EntityType[ original.getExternalTypeIdentifiers().length ];
            for( int i=0 ; i<original.getExternalTypeIdentifiers().length ; ++i ) {
                try {
                    types[typeCounter] = (EntityType) modelBase.findMeshTypeByIdentifier( original.getExternalTypeIdentifiers()[i] );
                    typeCounter++; // make sure we do the increment after an exception might have been thrown
                    
                } catch( MeshTypeNotFoundException ex ) {
                    log.error( ex );
                }
            }
            if( typeCounter < types.length ) {
                types = ArrayHelper.copyIntoNewArray( types, 0, typeCounter, EntityType.class );
            }
        } else {
            types = null;
        }
        

        HashMap<PropertyType,PropertyValue> localProperties;
        if( original.getPropertyTypes() != null && original.getPropertyTypes().length > 0 ) {

            localProperties = new HashMap<PropertyType,PropertyValue>( original.getPropertyTypes().length );
            for( int i=original.getPropertyTypes().length-1 ; i>=0 ; --i ) {
                
                try {
                    PropertyType type = (PropertyType) modelBase.findMeshTypeByIdentifier( original.getPropertyTypes()[i] );             
                    localProperties.put( type, original.getPropertyValues()[i] );
                } catch( MeshTypeNotFoundException ex ) {
                    log.error( ex );
                }
            }
        } else {
            localProperties = null;
        }

        NetMeshObjectIdentifier [] otherSides = original.getNeighbors();
        RoleType [][]              roleTypes  = null;
        
        if( otherSides != null ) {
            roleTypes = new RoleType[ otherSides.length ][];

            for( int i=0 ; i<otherSides.length ; ++i ) {
                MeshTypeIdentifier [] currentRoleTypes = original.getRoleTypesFor( otherSides[i] );

                roleTypes[i] = new RoleType[ currentRoleTypes.length ];
                typeCounter = 0;

                for( int j=0 ; j<currentRoleTypes.length ; ++j ) {
                    try {
                        roleTypes[i][typeCounter] = (RoleType) modelBase.findMeshTypeByIdentifier( currentRoleTypes[j] );
                        typeCounter++; // make sure we do the increment after an exception might have been thrown
                    } catch( Exception ex ) {
                        log.warn( ex );
                    }
                    if( typeCounter < roleTypes[i].length ) {
                        roleTypes[i] = ArrayHelper.copyIntoNewArray( roleTypes[i], 0, typeCounter, RoleType.class );
                    }
                }
            }
        }
        
        AnetMeshObject existing = findInStore( identifier ); // this may exist already
        
        if( existing != null ) {
            // make type adjustments
            if( types != null ) {
                existing.rippleBless( types ); // FIXME: what about unbless?
            }
            
            // make property adjustments
            if( localProperties != null ) {                
                existing.rippleSetPropertyValues( localProperties );
            }

            // make neighbor adjustments
            if( otherSides != null ) {
                // FIXME? remove neighbors?
                for( int i=0 ; i<otherSides.length ; ++i ) {
                    existing.rippleRelate( otherSides[i], (NetMeshBase) theMeshBase );
                }
                
                if( roleTypes != null ) {
                    for( int i=0 ; i<roleTypes.length ; ++i ) {
                        if( roleTypes[i] != null ) {
                            existing.rippleBless( roleTypes[i], otherSides[i] );
                        }
                    }
                }
            }
            
            if( isCreate ) {
                return existing;
            } else {
                // Make Proxy adjustments
                existing.makeReplicaFrom( proxy );
            }
        }
        
        // proxyTowardsLock MUST BE the same as the proxyTowardsHome, otherwise there
        // are two different versions of the replication graph for the purposes of
        // home and lock, and that won't do.

        Proxy [] proxies;
        Proxy    proxyTowardsHome = proxy;

        if( original.getProxyTowardsHomeNetworkIdentifier() != null ) {
            try {
                proxyTowardsHome = realBase.obtainProxyFor( original.getProxyTowardsHomeNetworkIdentifier(), null );

            } catch( FactoryException ex ) {
                log.warn( ex );
                // default is okay
            }
        }

        proxies = new Proxy[] { proxyTowardsHome };
        int proxyTowardsHomeIndex = 0;
        int proxyTowardsLockIndex = 0;
        
        if( proxy != proxyTowardsHome ) {
            // tell that replica we are here
            proxyTowardsHome.resynchronizeDependentReplicas( new MeshObjectIdentifier[] { identifier } );
        }

        MeshObjectIdentifier [] equivalents = original.getEquivalents();
        
        NetMeshObjectIdentifier [] leftRight = (NetMeshObjectIdentifier [])
                AMeshObjectEquivalenceSetComparator.SINGLETON.findLeftAndRightEquivalents( original.getIdentifier(), equivalents );

        AnetMeshObject ret = existing;
        if( ret == null ) {
            ret = new AnetMeshObject(
                    identifier,
                    realBase,
                    original.getTimeCreated(),
                    original.getTimeUpdated(),
                    original.getTimeRead(),
                    original.getTimeExpires(),
                    localProperties,
                    types,
                    leftRight,
                    otherSides,
                    roleTypes,
                    original.getGiveUpLock(),
                    proxies,
                    proxyTowardsHomeIndex,
                    proxyTowardsLockIndex );

            putIntoStore( ret );

            Proxy             incomingProxy           = realBase.determineIncomingProxy();
            NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

            tx.addChange( new NetMeshObjectCreatedEvent(
                    realBase,
                    ret,
                    incomingProxyIdentifier ));

            // don't assign owner, that comes via replication
        }
        return ret;
    }

    /**
     * Overridable helper to create a MeshObjectCreatedEvent.
     *
     * @return the created event
     */
    @Override
    protected NetChange createCreatedEvent(
            MeshObject createdObject )
    {
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

        Proxy             incomingProxy           = realBase.determineIncomingProxy();
        NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

        NetChange ret = new NetMeshObjectCreatedEvent(
                (NetMeshBase) getMeshBase(),
                (NetMeshObject) createdObject,
                incomingProxyIdentifier );
        return ret;
    } 

    /**
     * Overridable helper to create a MeshObjectDeletedEvent.
     *
     * @return the created event
     */
    @Override
    protected NetChange createDeletedEvent(
            MeshObjectIdentifier canonicalIdentifier,
            MeshObject      deletedObject,
            long            time )
    {
        AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

        Proxy             incomingProxy           = realBase.determineIncomingProxy();
        NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

        NetChange ret = new NetMeshObjectDeletedEvent(
                getMeshBase(),
                canonicalIdentifier,
                (NetMeshObject) deletedObject,
                incomingProxyIdentifier,
                time );
        return ret;
    }
    
    /**
     * Helper method that allows our subclasses to access the Store without having to expose it publicly.
     * 
     * @param identifier the Identifier of the MeshObject
     * @return the found MeshObject, or none.
     */
    @Override
    protected AnetMeshObject findInStore(
            MeshObjectIdentifier identifier )
    {
        return (AnetMeshObject) super.findInStore( identifier );
    }

    /**
     * Instantiate an ExternalizedMeshObject that is appropriate to capture the information held by
     * the subtype of MeshObject used by an AMeshBase. This is factored out so it can be subclassed.
     * 
     * @return the AParserFriendlyExternalizedMeshObjector subclass.
     */
    @Override
    public ParserFriendlyExternalizedNetMeshObject createParserFriendlyExternalizedMeshObject()
    {
        return new ParserFriendlyExternalizedNetMeshObject();
    }
}
