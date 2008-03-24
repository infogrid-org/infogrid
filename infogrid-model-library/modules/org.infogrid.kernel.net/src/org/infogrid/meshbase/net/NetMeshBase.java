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

import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.meshbase.MeshBase;

import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.NameServer;

/**
 * A MeshBase that is networked, i.e. that can map in data that resides in one
 * or more different locations on the network. A number of methods defined in the
 * supertype {@link org.infogrid.meshbase.MeshBase} are repeated
 * here as they are known to return more specific subtypes.
 */
public interface NetMeshBase
        extends
            MeshBase
{
    /**
     * Obtain the NetMeshBaseIdentifier at which this NetMeshBase is located.
     * 
     * @return the NetMeshBaseIdentifier
     */
    public abstract NetMeshBaseIdentifier getIdentifier();

   /**
     * Obtain a manager for MeshObject lifecycles.
     * 
     * @return a MeshBaseLifecycleManager that works on this MeshBase
     */
    public abstract NetMeshBaseLifecycleManager getMeshBaseLifecycleManager();

    /**
     * Find a MeshObject in this MeshBase by its Identifier. Unlike
     * the accessLocally method, this method does not attempt to contact other
     * MeshBases.
     * 
     * @param identifier the Identifier of the MeshObject that shall be found
     * @return the found MeshObject, or null if not found
     */
    public abstract NetMeshObject findMeshObjectByIdentifier(
            MeshObjectIdentifier identifier );

    /**
     * Find a set of MeshObjects in this MeshBase by their Identifiers. Unlike
     * the accessLocally method, this method purely considers MeshObjects in the
     * MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.
     * 
     * @param identifiers the Identifiers of the MeshObjects that shall be found
     * @return the found MeshObjects, which may contain null values for MeshObjects that were not found
     */
    public abstract NetMeshObject [] findMeshObjectsByIdentifier(
            MeshObjectIdentifier[] identifiers );

    /**
      * Obtain the MeshBase's home object. The home object is
      * the only well-known object in a MeshBase, but it is guaranteed to exist and
      * cannot be deleted.
      *
      * @return the MeshObject that is this MeshBase's home object
      */
    public abstract NetMeshObject getHomeObject();

    /**
      * Obtain a MeshObject whose unique identifier is known.
      *
      * @param nameOfLocalObject the Identifier property of the MeshObject
      * @return the locally found MeshObject, or null if not found locally
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
      */
    public abstract NetMeshObject accessLocally(
            MeshObjectIdentifier nameOfLocalObject )
        throws
            NetMeshObjectAccessException;

    /**
      * Obtain N locally available MeshObjects whose unique identifiers are known.
      *
      * @param nameOfLocalObjects the Identifier properties of the MeshObjects
      * @return array of the same length as nameOfLocalObjects, with the locally found MeshObjects filled
      *         in at the same positions. If one or more of the MeshObjects were not found, the location
      *         in the array will be null.
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
      */
    public abstract NetMeshObject [] accessLocally(
            MeshObjectIdentifier [] nameOfLocalObjects )
        throws
            NetMeshObjectAccessException;

    /**
      * Obtain a MeshObject from a remote NetMeshBaseIdentifier.
      *    This call does not obtain update rights for the obtained replica.</p>
      * 
      * @param networkLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
      * @return the locally replicated MeshObject, or null if not found
      * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
      */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier networkLocation )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier. Specify a non-default timeout.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            long                    timeoutInMillis )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            CoherenceSpecification  coherence )
        throws
            NetMeshObjectAccessException;

    /**
     * Obtain a MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier  remoteLocation,
            CoherenceSpecification coherence )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier. Specify a non-default timeout.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            CoherenceSpecification  coherence,
            long                    timeoutInMillis )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a MeshObject from a remote NetMeshObjectAccessSpecification.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param pathToObject the NetMeshObjectAccessSpecification indicating the location and path to use to access the remote MeshObject
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshObjectAccessSpecification pathToObject )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a MeshObject from a remote NetMeshObjectAccessSpecification.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param pathToObject the NetMeshObjectAccessSpecification indicating the location and path to use to access the remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject accessLocally(
            NetMeshObjectAccessSpecification pathToObject,
            long                             timeoutInMillis )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a number of MeshObjects from one ore more remote NetworkPaths.
     * This call does not obtain update rights for the obtained replicas.</p>
     * 
     * @param pathsToObjects the NetworkPaths indicating the location and path to use to access the remote MeshObjects
     * @return the locally replicated MeshObjects in the same sequence, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject [] accessLocally(
            NetMeshObjectAccessSpecification [] pathsToObjects )
        throws
            NetMeshObjectAccessException;

    /**
     * <p>Obtain a number of MeshObjects from one ore more remote NetworkPaths. Specify a non-default timeout.
     * This call does not obtain update rights for the obtained replicas.</p>
     * 
     * @param pathsToObjects the NetworkPaths indicating the location and path to use to access the remote MeshObjects
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObjects in the same sequence, or null if not found
     * @exception NetMeshObjectAccessException thrown if something went wrong attempting to access the NetMeshObject
     */
    public abstract NetMeshObject [] accessLocally(
            NetMeshObjectAccessSpecification [] pathsToObjects,
            long                                timeoutInMillis )
        throws
            NetMeshObjectAccessException;

    /**
     * Obtain a factory for MeshObjectIdentifiers that is appropriate for this MeshBase.
     *
     * @return the factory
     */
    public abstract NetMeshObjectIdentifierFactory getMeshObjectIdentifierFactory();

    /**
     * Obtain the NetAccessManager associated with this MeshBase, if any.
     *
     * @return the NetAccessManager
     */
    public abstract NetAccessManager getAccessManager();

    /**
     * Set the default value for new NetMeshObject's giveUpLock property if not otherwise specified.
     *
     * @param newValue the new value
     * @see #getDefaultWillGiveUpLock
     */
    public abstract void setDefaultWillGiveUpLock(
            boolean newValue );
    
    /**
     * Obtain the default value for new NetMeshObject's giveUpLock property if not otherwise specified.
     *
     * @param return the default value
     * @see #setDefaultWillGiveUpLock
     */
    public abstract boolean getDefaultWillGiveUpLock();
    
    /**
     * If true, the NetMeshBase will never give up locks, regardless what the individual MeshObjects
     * would like.
     *
     * @return true never give up locks
     */
    public abstract boolean refuseToGiveUpLock();
    
    /**
     * If is set to true, this NetMeshBase prefers that new Replicas create a branch from its own Replicas
     * in the replication graph. If this is set to false, this NetMeshBase prefers that new Replicas create a
     * branch from the Replicas in the third NetMeshBase from which this NetMeshBase has obtained its own
     * Replicas (if it has)
     *
     * @param newValue the new value
     * @see #getPointsReplicasToItself
     */
    public abstract void setPointsReplicasToItself(
            boolean newValue );

    /**
     * If this returns true, this NetMeshBase prefers that new Replicas create a branch from its own Replicas
     * in the replication graph. If this returns false, this NetMeshBase prefers that new Replicas create a
     * branch from the Replicas in the third NetMeshBase from which this NetMeshBase has obtained its own
     * Replicas (if it has)
     *
     * @return true if Replicas are supposed to become Replicas of locally held Replicas
     * @see #setPointsReplicasToItself
     */
    public abstract boolean getPointsReplicasToItself();
    
    /**
     * Obtain or create a Proxy for communication with a NetMeshBase at the specified NetMeshBaseIdentifier.
     * 
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @param coherence the CoherenceSpecification to use, if any
     * @return the Proxy
     * @throws FactoryException thrown if the Proxy could not be created
     * @see #getProxyFor
     */
    public abstract Proxy obtainProxyFor(
            NetMeshBaseIdentifier  networkIdentifier,
            CoherenceSpecification coherence )
         throws
            FactoryException;

    /**
     * Obtain an existing Proxy to the specified NetMeshBaseIdentifier. Return null if no such
     * Proxy exists. Do not attempt to create one.
     *
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @return the Proxy
     * @see #obtainProxyFor
     */
    public abstract Proxy getProxyFor(
            NetMeshBaseIdentifier  networkIdentifier );

    /**
     * <p>Obtain an Iterator over the set of currently active Proxies.</p>
     *
     * @return the CursorIterator
     */
    public abstract CursorIterator<Proxy> proxies();
    
//    /**
//     * Instruct this NetMeshBase to recreate a Proxy according to the provided specification.
//     *
//     * @param externalized the ExternalizedProxy containing the specification
//     * @return the recreated Proxy
//     */
//    public Proxy recreateProxy(
//            ExternalizedProxy externalized )
//        throws
//            FactoryException;
//
    /**
     * Obtain this NetMeshBase as a NameServer for its Proxies, keyed by the NetMeshBaseIdentifiers
     * of the partner NetMeshBases.
     * 
     *  @return the NameServer mapping NetMeshBaseIdentifiers to Proxies.
     */
    public NameServer<NetMeshBaseIdentifier,Proxy> getAsProxyNameServer();

    /**
     * Determine the Proxy, if any, that originated the current Thread.
     *
     * @return the Proxy
     */
    public abstract Proxy determineIncomingProxy();

    /**
     * Set the incoming Proxy for this Thread. To be called only by Proxies.
     *
     * @param incomingProxy the incoming Proxy for this Thread
     */
    public abstract void registerIncomingProxy(
            Proxy incomingProxy );
    
    /**
     * Unregister the incoming Proxy for this Thread. To be called only be Proxies.
     */
    public abstract void unregisterIncomingProxy();

    /**
     * The name of the bound property we use to express "the timeout for accessLocally has changed".
     */
    public static final String ACCESS_LOCALLY_TIMES_OUT_AFTER_PROPERTY = "AccessLocallyTimesOutAfter";
}
