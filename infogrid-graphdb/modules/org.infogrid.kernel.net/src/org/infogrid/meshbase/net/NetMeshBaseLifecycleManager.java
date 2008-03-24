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

import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObjectFactory;
import org.infogrid.mesh.security.MustNotDeleteHomeObjectException;

import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;

/**
 * This MeshBaseLifecycleManager, appropriate for a NetMeshBase, specifies
 * additional methods that only apply in a networked environment.
 */
public interface NetMeshBaseLifecycleManager
        extends
            MeshBaseLifecycleManager,
            ParserFriendlyExternalizedNetMeshObjectFactory

{
    /**
     * <p>Obtain the MeshBase that this MeshBaseLifecycleManager works on.</p>
     * <p>We list this here again because this type returns a more concrete type than our supertype does.</p>
     * 
     * @return the MeshBase that this MMeshBaseLifecycleManagerworks on
     */
    public abstract NetMeshBase getMeshBase();

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
    public abstract NetMeshObject createMeshObject()
        throws
            TransactionException;

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
    public abstract NetMeshObject createMeshObject(
            EntityType type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException;

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
    public abstract NetMeshObject createMeshObject(
            EntityType [] types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException;

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
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException;

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
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType      type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException;

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
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException;

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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalIdentifierNotUniqueExceptionif a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeExpires )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException;

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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNaIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType      type,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeExpires )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;

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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNaIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeExpires )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;    

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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @param proxyTowardsHomeAndLock the Proxy in whose direction the home replica and the updateable replica can be found
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNamIdentifierNotUniqueExceptiona MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeExpires,
            boolean         giveUpLock,
            Proxy           proxyTowardsHomeAndLock )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;    
    
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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @param proxies the Proxies in whose direction the other related nodes of the replication graph may be found
     * @param homeProxyIndex the index, into the proxies array, that identifies the Proxy in whose direction the home replica may be found
     * @param proxyTowardsLockIndex the index, into the proxies array, that identifies the Proxy in whose direction the update rights may be found
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws IdentifierNoMeshObjectIdentifierNotUniqueExceptionady in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract NetMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []   types,
            long            timeCreated,
            long            timeUpdated,
            long            timeRead,
            long            timeExpires,
            boolean         giveUpLock,
            Proxy []        proxies,
            int             homeProxyIndex,
            int             proxyTowardsLockIndex )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;

    /**
     * Purge a non-home replica.
     *
     * @param replica the non-home replica
     * @throws TransactionException thrown if invoked outside proper Transaction boundaries
     * @throws MustNotDeleteHomeObjectException thrown if applied to the home object
     */
    public abstract void purgeReplica(
            NetMeshObject replica )
        throws
            TransactionException,
            MustNotDeleteHomeObjectException;
    
    /**
     * Purge a non-home replicas.
     *
     * @param replicas the non-home replicas
     * @throws TransactionException thrown if invoked outside proper Transaction boundaries
     * @throws MustNotDeleteHomeObjectException thrown if applied to the home object
     */
    public abstract void purgeReplicas(
            NetMeshObject [] replicas )
        throws
            TransactionException,
            MustNotDeleteHomeObjectException;

    /**
     * Instantiate a replica MeshObject in this MeshBase, thereby setting 
     * up a branch in the replication graph. This may also be invoked if the replica exists
     * already in more complex replication topologies.
     *
     * @param original the original MeshObject that we copy
     * @param proxy the Proxy that conveyed this command
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public abstract NetMeshObject rippleCreate(
            ExternalizedNetMeshObject original,
            NetMeshBaseIdentifier         proxy )
        throws
            NotPermittedException,
            TransactionException;

    /**
     * Delete a replica MeshObject in this MeshBase, thereby removing a
     * branch in the replication graph.
     * 
     * @param identifier the Identifier of the MeshObject whose replica is to be deleted
     * @param proxy the Proxy that conveyed this command
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public abstract NetMeshObject rippleDelete(
            MeshObjectIdentifier   identifier,
            NetMeshBaseIdentifier proxy,
            long              time )
        throws
            TransactionException;
    
    /**
     * Resynchronize a local replica to the provided ExternalizedNetMeshObject.
     * 
     * @param original the original MeshObject that we copy
     * @param proxy the Proxy that conveyed this command
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public abstract NetMeshObject resynchronize(
            ExternalizedNetMeshObject original,
            NetMeshBaseIdentifier         proxy )
        throws
            NotPermittedException,
            TransactionException;
}
