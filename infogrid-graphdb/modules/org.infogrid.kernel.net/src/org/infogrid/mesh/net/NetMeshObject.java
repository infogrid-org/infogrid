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

package org.infogrid.mesh.net;

import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.EquivalentAlreadyException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;

import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeNotBlessedException;
import org.infogrid.mesh.RoleTypeRequiresEntityTypeException;
import org.infogrid.mesh.net.externalized.SimpleExternalizedNetMeshObject;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;

import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.RemoteQueryTimeoutException;


/**
 * The subtype of MeshObject used in NetworkedMeshBases.
 */
public interface NetMeshObject
        extends
            MeshObject
{
    /**
     * Obtain the globally unique identifier of this MeshObject.
     *
     * @return the globally unique identifier of this MeshObject
     */
    public abstract NetMeshObjectIdentifier getIdentifier();

    /**
     * Obtain the NetMeshBase that contains this NetMeshObject. This is immutable for the
     * lifetime of this instance.
     *
     * @return the MeshBase that contains this MeshObject.
     */
    public abstract NetMeshBase getMeshBase();

    /**
     * Determine whether this the home replica.
     *
     * @return returns true if this is the home replica
     */
    public abstract boolean isHomeReplica();

    /**
      * Determine whether this replica has update rights.
      *
      * @return returns true if this is replica has the update rights
      */
    public abstract boolean doWeHaveLock();

    /**
      * Attempt to obtain update rights.
      *
      * @return returns true if we have update rights, or we were successful obtaining them.
      */
    public abstract boolean tryToObtainLock()
        throws
            RemoteQueryTimeoutException;

    /**
      * Attempt to obtain update rights. Specify a timeout in milliseconds.
      *
      * @return returns true if we have update rights, or we were successful obtaining them.
      */
    public abstract boolean tryToObtainLock(
            long timeout )
        throws
            RemoteQueryTimeoutException;

    /**
     * Forced recovery of the lock by the home replica.
     */
    public abstract void forceObtainLock();
    
    /**
      * Determine whether this replica is going to give up update rights if it has them,
      * in case someone asks. This only says "if this replica has update rights, it will
      * give them up when asked". This call makes no statement about whether this replica
      * currently does or does not have update rights.
      *
      * @return if true, this replica will give up update rights when asked
      * @see #setWillGiveUpLock
      */
    public abstract boolean willGiveUpLock();

    /**
      * Set whether this replica will allow update rights to be given up or not.
      * However, if this is not the home replica and a lease for the replica expires, the
      * home replica will still reclaim the lock. Setting this value will not
      * prevent that.
      *
      * @param yesNo if true, this replica will give update rights when asked
      * @see #willGiveUpLock
      */
    public abstract void setWillGiveUpLock(
            boolean yesNo );

    /**
     * Obtain the Proxy in the direction of the home replica.
     * This may return null, indicating that this replica is the home replica.
     *
     * @return the Proxy in the direction of the home replica
     */
    public abstract Proxy getProxyTowardsHomeReplica();
    
    /**
     * Obtain the Proxy in the direction of the update rights for this replica.
     * This may return null, indicating that this replica has the update rights.
     *
     * @return the Proxy in the direction of the update rights
     */
    public abstract Proxy getProxyTowardsLockReplica();
    
    /**
     * Obtain all Proxies applicable to this replica.
     *
     * @return all Proxies. This may return null for efficiency reasons
     */
    public abstract Proxy [] getAllProxies();

    /**
     * Obtain an Iterator over all Proxies applicable to this replica.
     *
     * @return the Iterator
     */
    public abstract CursorIterator<Proxy> proxyIterator();

    /**
     * Find a Proxy towards a partner NetMeshBase with a particular NetMeshBaseIdentifier. If such a
     * Proxy does not exist, return null.
     * 
     * @param partnerIdentifier the NetMeshBaseIdentifier of the partner NetMeshBase
     * @return the found Proxy, or null
     */
    public abstract Proxy findProxyTowards(
            NetMeshBaseIdentifier partnerIdentifier );

    /**
      * Surrender update rights when invoked. This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      * @return true if successful, false otherwise.
      */
    public abstract boolean surrenderLock(
            Proxy theProxy );

    /**
      * Push update rights to this replica. This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void pushLock(
            Proxy theProxy );

    /**
      * Tell the NetMeshObject to make a note of the fact that a new replica of the
      * NetMeshObject is being created in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void registerReplicationTowards(
            Proxy theProxy );

    /**
      * Tell the NetMeshObject to remove the note of the fact that a replica of the
      * NetMeshObject exists in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void unregisterReplicationTowards(
            Proxy theProxy );

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * 
     * @param captureProxies if true, this SimpleExternalizedNetMeshObject contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @return this NetMeshObject as ExSimpleExternalizedNetMeshObject
     */
    public abstract SimpleExternalizedNetMeshObject asExternalized(
            boolean captureProxies );

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * At the same time, add the provided Proxy to the list of Proxies of this replica.
     * 
     * @param captureProxies if true, this ESimpleExternalizedNetMeshObjectwill contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @return this NetMeshObject as ExSimpleExternalizedNetMeshObject
     */
    public abstract SimpleExternalizedNetMeshObject asExternalizedAndAddProxy(
            boolean captureProxies,
            Proxy   proxyTowardsNewReplica );

    /**
     * Bless a replica MeshObject, as a consequence of the blessing of a master replica.
     *
     * @param types the to-be-blessed EntityTypes
     */
    public abstract void rippleBless(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Unbless a replica MeshObject, as a consequence of the unblessing of a master replica.
     *
     * @param types the to-be-unblessed EntityTypes
     */
    public abstract void rippleUnbless(
            EntityType [] types )
        throws
            RoleTypeRequiresEntityTypeException,
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException;

    /**
     * Relate two replica MeshObjects, as a consequence of relating other replicas.
     * 
     * @param identifierOfOtherSide the Identifier of the NetMeshObject to relate to
     */
    public abstract void rippleRelate(
            NetMeshObjectIdentifier identifierOfOtherSide,
            NetMeshBase             mb )
        throws
            RelatedAlreadyException,
            TransactionException;
    
    /**
     * Unrelate two replica MeshObjects, as a consequence of unrelating other replicas.
     * 
     * @param identifierOfOtherSide the Identifier of the NetMeshObject to unrelate from
     */
    public abstract void rippleUnrelate(
            NetMeshObjectIdentifier identifierOfOtherSide,
            NetMeshBase             mb )
        throws
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
     * Bless the relationship of two replica MeshObjects, as a consequence of blessing the relationship
     * of two other replicas.
     * 
     * @param theTypes the RoleTypes to use for blessing
     * @param identifierOfOtherSide the Identifier of the NetMeshObject that
     *        identifies the relationship that shall be blessed
     */
    public abstract void rippleBless(
            RoleType []             theTypes,
            NetMeshObjectIdentifier identifierOfOtherSide )
        throws
            EntityNotBlessedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Unbless the relationship of two replica MeshObjects, as a consequence of unblessing the relationship
     * of two other replicas.
     * 
     * @param theTypes the RoleTypes to use for unblessing
     * @param identifierOfOtherSide the Identifier of the NetMeshObject that
     *        identifies the relationship that shall be unblessed
     */
    public abstract void rippleUnbless(
            RoleType []             theTypes,
            NetMeshObjectIdentifier identifierOfOtherSide )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;
    
    /**
     * Add a replica as an equivalent, as a consequence of adding a different replica as equivalent.
     * 
     * @param identifierOfEquivalent the Identifier of the replica NetMeshObject
     */
    public abstract void rippleAddAsEquivalent(
            NetMeshObjectIdentifier identifierOfEquivalent )
        throws
            EquivalentAlreadyException,
            TransactionException,
            NotPermittedException;
    
    /**
     * Remove a replica as an equivalent, as a consequence of removing a different replica as equivalent.
     * 
     * @param identifierOfEquivalent the Identifier of the replica NetMeshObject
     */
    public abstract void rippleRemoveAsEquivalent()
        throws
            TransactionException,
            NotPermittedException;

    /**
     * Change the values of Properties on a replica, as a consequence of changing the values of the properties
     * in another replica.
     *
     * @param types the PropertyTypes
     * @param values the new values, in the same sequence as the PropertyTypes
     */
    public abstract void rippleSetPropertyValues(
            PropertyType []  types,
            PropertyValue [] values )
        throws
            IllegalPropertyTypeException,
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException;

    /**
     * Delete a replica as a consequence of deleting another replica.
     */
    public abstract void rippleDelete()
        throws
            TransactionException,
            NotPermittedException;
}
