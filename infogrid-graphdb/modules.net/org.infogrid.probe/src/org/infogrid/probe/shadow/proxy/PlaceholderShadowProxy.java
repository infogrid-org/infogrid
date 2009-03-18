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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.shadow.proxy;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.proxy.AbstractProxy;
import org.infogrid.meshbase.net.proxy.ProxyMessageEndpoint;
import org.infogrid.meshbase.net.proxy.ProxyPolicy;
import org.infogrid.meshbase.net.xpriso.XprisoSynchronizer;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.util.RemoteQueryTimeoutException;

/**
 * A placeholder proxy that does not communicate, for use in ShadowMeshBases and
 * StagingMeshBases.
 */
public class PlaceholderShadowProxy
    extends
        AbstractProxy
{
    /**
     * Factory method.
     *
     * @param mb the NetMeshBase that this Proxy belongs to
     * @param policy the ProxyPolicy to use
     * @param partnerIdentifier identifier of the NetMeshBase that this Proxy would communicate with if it were not a placeholder
     * @return the created PlaceholderShadowProxy
     */
    public static PlaceholderShadowProxy create(
            NetMeshBase           mb,
            ProxyPolicy           policy,
            NetMeshBaseIdentifier partnerIdentifier )
    {
        PlaceholderShadowProxy ret = new PlaceholderShadowProxy( mb, policy, partnerIdentifier );

        return ret;
    }

    /**
     * Factory method to restore from storage.
     *
     * @param mb the MeshBase this Proxy belongs to
     * @param policy the ProxyPolicy to use
     * @param partnerIdentifier identifier of the partner NetMeshBase with which this Proxy communicates
     * @return the created DefaultShadowProxy
     */
    public static PlaceholderShadowProxy restoreProxy(
            NetMeshBase           mb,
            ProxyPolicy           policy,
            NetMeshBaseIdentifier partnerIdentifier )
    {
        PlaceholderShadowProxy ret = new PlaceholderShadowProxy( mb, policy, partnerIdentifier );

        return ret;
    }

    /**
     * Constructor.
     *
     * @param mb the NetMeshBase that this Proxy belongs to
     * @param policy the ProxyPolicy to use
     * @param partnerIdentifier identifier of the NetMeshBase that this Proxy would communicate with if it were not a placeholder
     */
    protected PlaceholderShadowProxy(
            NetMeshBase           mb,
            ProxyPolicy           policy,
            NetMeshBaseIdentifier partnerIdentifier )
    {
        super( mb, policy, partnerIdentifier );
    }

    /**
     * Obtain the ProxyMessageEndpoint associated with this Proxy.
     *
     * @return the ProxyMessageEndpoint
     */
    public ProxyMessageEndpoint getMessageEndpoint()
    {
        return null;
    }

    /**
     * Ask this Proxy to obtain from its partner NetMeshBase replicas with the enclosed
     * specification. Do not acquire the lock; that would be a separate operation.
     * This call must return immediately; the caller must wait instead. (This is necessary
     * to be able to perform accessLocally() via several Proxies in parallel.)
     *
     * @param paths the NetMeshObjectAccessSpecifications specifying which replicas should be obtained
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long obtainReplicas(
            NetMeshObjectAccessSpecification [] paths,
            long                                duration,
            XprisoSynchronizer                  synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Ask this Proxy to obtain the lock for one or more replicas from the
     * partner NetMeshBase.
     *
     * @param localReplicas the local replicas for which the lock should be obtained
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long tryToObtainLocks(
            NetMeshObject []   localReplicas,
            long               duration,
            XprisoSynchronizer synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Ask this Proxy to push the locks for one or more replicas to the partner
     * NetMeshBase.
     *
     * @param localReplicas the local replicas for which the lock should be pushed
     * @param isNewProxy if true, the the NetMeshObject did not replicate via this Proxy prior to this call.
     *         The sequence in the array is the same sequence as in localReplicas.
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long tryToPushLocks(
            NetMeshObject []   localReplicas,
            boolean []         isNewProxy,
            long               duration,
            XprisoSynchronizer synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Ask this Proxy to obtain the home replica status for one or more replicas from the
     * partner NetMeshBase.
     *
     * @param localReplicas the local replicas for which the home replica status should be obtained
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long tryToObtainHomeReplicas(
            NetMeshObject []   localReplicas,
            long               duration,
            XprisoSynchronizer synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
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
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long tryToPushHomeReplicas(
            NetMeshObject []   localReplicas,
            boolean []         isNewProxy,
            long               duration,
            XprisoSynchronizer synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Send notification to the partner NetMeshBase that this MeshBase has forcibly taken the
     * lock back for the given NetMeshObjects.
     *
     * @param localReplicas the local replicas for which the lock has been forced back
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     */
    public void forceObtainLocks(
            NetMeshObject []   localReplicas,
            XprisoSynchronizer synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Tell the partner NetMeshBase that one or more local replicas would like to be
     * resynchronized. This call uses NetMeshObjectIdentifier instead of NetMeshObject
     * as sometimes the NetMeshObjects have not been instantiated when this call is
     * most naturally made.
     *
     * @param identifiers the identifiers of the NetMeshObjects
     * @param duration the duration, in milliseconds, that the caller is willing to wait to perform the request. -1 means "use default".
     * @param synchronizer the synchronizer to use. It is the caller's responsibility to execute a join() some time after this call.
     * @return the duration, in milliseconds, that the Proxy believes this operation will take
     */
    public long tryResynchronizeReplicas(
            NetMeshObjectIdentifier [] identifiers,
            long                       duration,
            XprisoSynchronizer         synchronizer )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Ask this Proxy to cancel the leases for the given replicas from its partner NetMeshBase.
     *
     * @param localReplicas the local replicas for which the lease should be canceled
     */
    public void cancelReplicas(
            NetMeshObject [] localReplicas )
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Invoked by the NetMeshBase that this Proxy belongs to,
     * it causes this Proxy to initiate the "ceasing communication" sequence with
     * the partner NetMeshBase, and then kill itself.
     *
     * @throws RemoteQueryTimeoutException thrown if communications timed out
     */
    public void initiateCeaseCommunications()
        throws
            RemoteQueryTimeoutException
    {
        throw new UnsupportedOperationException( "should never be called" );
    }

    /**
     * Tell this Proxy that it is not needed any more. This will invoke
     * {@link #initiateCeaseCommunications} if and only if
     * isPermanent is true.
     *
     * @param isPermanent if true, this Proxy will go away permanently; if false,
     *        it may come alive again some time later, e.g. after a reboot
     */
    public void die(
            boolean isPermanent )
    {
        // no nothing
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
        // no nothing
    }
}
