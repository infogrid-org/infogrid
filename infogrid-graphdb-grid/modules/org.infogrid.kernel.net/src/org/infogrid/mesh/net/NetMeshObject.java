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
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;
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
 * The subtype of MeshObject used in NetMeshBases.
 */
public interface NetMeshObject
        extends
            MeshObject
{
    /**
     * Obtain the globally unique identifier of this NetMeshObject.
     *
     * @return the globally unique identifier of this NetMeshObject
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
      * Determine whether this replica has update rights.
      *
      * @return returns true if this is replica has the update rights
      */
    public abstract boolean hasLock();

    /**
     * Attempt to obtain update rights.
     *
     * @return returns true if we have update rights, or we were successful obtaining them.
     * @throws RemoteQueryTimeoutException thrown if the replica that has the lock could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToObtainLock()
        throws
            RemoteQueryTimeoutException;

    /**
     * Attempt to obtain update rights. Specify a timeout in milliseconds.
     *
     * @param timeout the timeout in milliseconds
     * @return returns true if we have update rights, or we were successful obtaining them.
     * @throws RemoteQueryTimeoutException thrown if the replica that has the lock could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToObtainLock(
            long timeout )
        throws
            RemoteQueryTimeoutException;

    /**
     * Attempt to move update rights to the NetMeshBase that can be found through the
     * specified Proxy. This requires this NetMeshObject to have the update rights.
     * 
     * @param outgoingProxy the Proxy
     * @return returns true if the update rights were moved
     * @throws DoNotHaveLockException thrown if this NetMeshObject does not have update rights
     * @throws RemoteQueryTimeoutException thrown if the NetMeshBase could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToPushLock(
            Proxy outgoingProxy )
        throws
            DoNotHaveLockException,
            RemoteQueryTimeoutException;
            
    /**
     * Attempt to move update rights to the NetMeshBase that can be found through the
     * specified Proxy. Specify a timeout in milliseconds. This requires this NetMeshObject to have the update rights.
     * 
     * @param outgoingProxy the Proxy
     * @param timeout the timeout in milliseconds
     * @return returns true if the update rights were moved
     * @throws DoNotHaveLockException thrown if this NetMeshObject does not have update rights
     * @throws RemoteQueryTimeoutException thrown if the NetMeshBase could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToPushLock(
            Proxy outgoingProxy,
            long  timeout )
        throws
            DoNotHaveLockException,
            RemoteQueryTimeoutException;

    /**
     * Forced recovery of the lock. This must only be invoked on the home replica.
     */
    public abstract void forceObtainLock();
    
    /**
      * Determine whether this replica is going to give up update rights if it has them,
      * in case someone asks. This only says "if this replica has update rights, it will
      * give them up when asked". This call makes no statement about whether this replica
      * currently does or does not have update rights.
      *
      * @return if true, this replica will give up update rights when asked
      * @see #getWillGiveUpLock
      */
    public abstract boolean getWillGiveUpLock();

    /**
      * Set whether this replica will allow update rights to be given up or not.
      * However, if this is not the home replica and a lease for the replica expires, the
      * home replica will still reclaim the lock. Setting this value will not
      * prevent that.
      *
      * @param yesNo if true, this replica will give update rights when asked
      * @see #getWillGiveUpLock
      */
    public abstract void setWillGiveUpLock(
            boolean yesNo );

    /**
     * Obtain the Proxy in the direction of the update rights for this replica.
     * This may return null, indicating that this replica has the update rights.
     *
     * @return the Proxy in the direction of the update rights
     */
    public abstract Proxy getProxyTowardsLockReplica();
    
    /**
     * Determine whether this the home replica.
     *
     * @return returns true if this is the home replica
     */
    public abstract boolean isHomeReplica();

    /**
     * Attempt to obtain the home replica status.
     *
     * @return returns true if we have home replica status, or we were successful obtaining it.
     * @throws RemoteQueryTimeoutException thrown if the replica that has home replica status could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToObtainHomeReplica()
        throws
            RemoteQueryTimeoutException;

    /**
     * Attempt to obtain the home replica status. Specify a timeout in milliseconds.
     *
     * @param timeout the timeout in milliseconds
     * @return returns true if we have home replica status, or we were successful obtaining it.
     * @throws RemoteQueryTimeoutException thrown if the replica that has home replica status could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToObtainHomeReplica(
            long timeout )
        throws
            RemoteQueryTimeoutException;

    /**
     * Attempt to move the home replica status to the NetMeshBase that can be found through the
     * specified Proxy. This requires this NetMeshObject to have home replica status.
     * 
     * @param outgoingProxy the Proxy
     * @return returns true if the home replica status was moved
     * @throws NotHomeReplicaException thrown if this NetMeshObject does not have home replica status
     * @throws RemoteQueryTimeoutException thrown if the NetMeshBase could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToPushHomeReplica(
            Proxy outgoingProxy )
        throws
            NotHomeReplicaException,
            RemoteQueryTimeoutException;
            
    /**
     * Attempt to move the home replica status to the NetMeshBase that can be found through the
     * specified Proxy. Specify a timeout in milliseconds. This requires this NetMeshObject to have home replica status.
     * 
     * @param outgoingProxy the Proxy
     * @param timeout the timeout in milliseconds
     * @return returns true if the home replica status was moved
     * @throws NotHomeReplicaException thrown if this NetMeshObject does not have home replica status
     * @throws RemoteQueryTimeoutException thrown if the NetMeshBase could not be contacted or did not reply in the time alloted
     */
    public abstract boolean tryToPushHomeReplica(
            Proxy outgoingProxy,
            long  timeout )
        throws
            NotHomeReplicaException,
            RemoteQueryTimeoutException;
    
    /**
     * Determine whether this replica is going to give up home replica status if it has it,
     * in case someone asks. This only says "if this replica is the home replica, it
     * will give it up when asked". This call makes no statement about whether this replica
     * currently does or does not have home replica status.
     * 
     * @return if true, this replica will give up home replica status when asked
     * @see #setWillGiveUpHomeReplica
     */
    public abstract boolean getWillGiveUpHomeReplica();

    /**
     * Set whether this replica will allow home replica status to be given up or not.
     * 
     * @param yesNo if true, this replica will give up home replica status when asked
     * @see #getWillGiveUpHomeReplica
     */
    public abstract void setWillGiveUpHomeReplica(
            boolean yesNo );

    /**
     * Obtain the Proxy in the direction of the home replica.
     * This may return null, indicating that this replica is the home replica.
     *
     * @return the Proxy in the direction of the home replica
     */
    public abstract Proxy getProxyTowardsHomeReplica();
    
    /**
     * Obtain all Proxies applicable to this replica.
     *
     * @return all Proxies. This may return null for efficiency reasons
     */
    public abstract Proxy [] getAllProxies();

    /**
     * Obtain an Iterator over all Proxies applicable to this replica.
     *
     * @return the CursorIterator
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
    public abstract boolean proxyInternalSurrenderLock(
            Proxy theProxy );

    /**
      * Push update rights to this replica. This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void proxyInternalPushLock(
            Proxy theProxy );

    /**
     * Surrender home replica status when invoked.  This shall not be called by the application
     * programmer. This is called only by Proxies that identify themselves to this call.
     *
     * @param theProxy the Proxy invoking this method
     * @return true if successful, false otherwise.
     */
    public abstract boolean proxyInternalSurrenderHomeReplica(
            Proxy theProxy );
    
    /**
     * Push home replica status to this replica. This shall not be called by the application
     * programmer. This is called only by Proxies that identify themselves to this call.
     * 
     * @param theProxy the Proxy invoking this method
     */
    public abstract void proxyInternalPushHomeReplica(
            Proxy theProxy );

    /**
      * Tell the NetMeshObject to make a note of the fact that a new replica of the
      * NetMeshObject is being created in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void proxyInternalRegisterReplicationTowards(
            Proxy theProxy );

    /**
      * Tell the NetMeshObject to remove the note of the fact that a replica of the
      * NetMeshObject exists in the direction of the provided Proxy.
      * This shall not be called by the application
      * programmer. This is called only by Proxies that identify themselves to this call.
      *
      * @param theProxy the Proxy invoking this method
      */
    public abstract void proxyInternalUnregisterReplicationTowards(
            Proxy theProxy );

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * 
     * @param captureProxies if true, the SimpleExternalizedNetMeshObject contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @return this NetMeshObject as SimpleExternalizedNetMeshObject
     */
    public abstract SimpleExternalizedNetMeshObject asExternalized(
            boolean captureProxies );

    /**
     * Obtain the same NetMeshObject as SimpleExternalizedNetMeshObject so it can be easily serialized.
     * At the same time, add the provided Proxy to the list of Proxies of this replica.
     * 
     * @param captureProxies if true, the SimpleExternalizedNetMeshObject contain entries for the
     *        Proxies as held in this replica. If false, that information will be left out.
     * @param proxyTowardsNewReplica if given, add this Proxy to the list of Proxies of this replica as a side effect
     * @return this NetMeshObject as SimpleExternalizedNetMeshObject
     */
    public abstract SimpleExternalizedNetMeshObject asExternalizedAndAddProxy(
            boolean captureProxies,
            Proxy   proxyTowardsNewReplica );

    /**
     * Bless a replica NetMeshObject, as a consequence of the blessing of a master replica.
     *
     * @param types the to-be-blessed EntityTypes
     * @throws EntityBlessedAlreadyException thrown if this MeshObject is already blessed with one or more of the EntityTypes
     * @throws IsAbstractException thrown if one or more of the EntityTypes were abstract and could not be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleBless(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Unbless a replica NetMeshObject, as a consequence of the unblessing of a master replica.
     *
     * @param types the to-be-unblessed EntityTypes
     * @throws RoleTypeRequiresEntityTypeException thrown if this MeshObject plays one or more roles that requires the MeshObject to remain being blessed with at least one of the EntityTypes
     * @throws EntityNotBlessedException thrown if this MeshObject does not support at least one of the given EntityTypes
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleUnbless(
            EntityType [] types )
        throws
            RoleTypeRequiresEntityTypeException,
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException;

    /**
     * Relate two replica NetMeshObjects, as a consequence of relating other replicas.
     * 
     * @param newNeighborIdentifier the identifier of the NetMeshObject to relate to
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the newNeighbor
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public abstract void rippleRelate(
            NetMeshObjectIdentifier newNeighborIdentifier )
        throws
            RelatedAlreadyException,
            TransactionException;
    
    /**
     * Unrelate two replica NetMeshObjects, as a consequence of unrelating other replicas.
     * 
     * @param neighborIdentifier the identifier of the NetMeshObject to unrelate from
     * @param mb the MeshBase that this MeshObject does or used to belong to
     * @throws NotRelatedException thrown if this MeshObject is not related to the neighbor
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleUnrelate(
            NetMeshObjectIdentifier neighborIdentifier,
            NetMeshBase             mb )
        throws
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
     * Bless the relationship of two replica NetMeshObjects, as a consequence of blessing the relationship
     * of two other replicas.
     * 
     * @param theTypes the RoleTypes to use for blessing
     * @param neighborIdentifier the identifier of the NetMeshObject that
     *        identifies the relationship that shall be blessed
     * @throws RoleTypeBlessedAlreadyException thrown if the relationship to the other MeshObject is blessed
     *         already with one ore more of the given RoleTypes
     * @throws EntityNotBlessedException thrown if this MeshObject is not blessed by a requisite EntityType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws IsAbstractException thrown if one of the RoleTypes belong to an abstract RelationshipType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleBless(
            RoleType []             theTypes,
            NetMeshObjectIdentifier neighborIdentifier )
        throws
            RoleTypeBlessedAlreadyException,
            EntityNotBlessedException,
            NotRelatedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Unbless the relationship of two replica NetMeshObjects, as a consequence of unblessing the relationship
     * of two other replicas.
     * 
     * @param theTypes the RoleTypes to use for unblessing
     * @param neighborIdentifier the identifier of the NetMeshObject that
     *        identifies the relationship that shall be unblessed
     * @throws RoleTypeNotBlessedException thrown if the relationship to the other MeshObject does not support the RoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleUnbless(
            RoleType []             theTypes,
            NetMeshObjectIdentifier neighborIdentifier )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;
    
    /**
     * Add a replica NetMeshObject as an equivalent, as a consequence of adding a different replica
     * as equivalent.
     * 
     * @param identifierOfEquivalent the Identifier of the replica NetMeshObject
     * @throws EquivalentAlreadyException thrown if the provided MeshObject is already an equivalent of this MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleAddAsEquivalent(
            NetMeshObjectIdentifier identifierOfEquivalent )
        throws
            EquivalentAlreadyException,
            TransactionException,
            NotPermittedException;
    
    /**
     * Remove this replica NetMeshObject as an equivalent from the current set of equivalents, as a consequence of removing
     * a different replica as equivalent.
     * 
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleRemoveAsEquivalent()
        throws
            TransactionException,
            NotPermittedException;

    /**
     * Change the values of Properties on a replica NetMeshObject, as a consequence of changing the values of the properties
     * in another replica.
     *
     * @param types the PropertyTypes
     * @param values the new values, in the same sequence as the PropertyTypes
     * @throws IllegalPropertyTypeException thrown if one PropertyType does not exist on this MeshObject
     *         because the MeshObject has not been blessed with a MeshType that provides this PropertyType
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this Property
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleSetPropertyValues(
            PropertyType []  types,
            PropertyValue [] values )
        throws
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            NotPermittedException,
            TransactionException;

    /**
     * Delete a replica NetMeshObject as a consequence of deleting another replica.
     * 
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void rippleDelete()
        throws
            TransactionException,
            NotPermittedException;
}
