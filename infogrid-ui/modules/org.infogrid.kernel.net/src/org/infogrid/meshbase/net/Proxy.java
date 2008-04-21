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

import org.infogrid.mesh.net.NetMeshObject;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.externalized.ExternalizedProxy;
import org.infogrid.meshbase.transaction.Transaction;

import org.infogrid.net.NetMessageEndpoint;

import org.infogrid.util.FactoryCreatedObject;
import org.infogrid.util.RemoteQueryTimeoutException;
import org.infogrid.util.text.StringRepresentation;

/**
 * <p>All communications between a NetMeshBase A and a NetMeshBase B is managed by
 * a pair of Proxies: one owned by and collocated with NetMeshBase A, interacting with
 * NetMeshBase B on behalf of A; the other owned by and collocated with NetMeshBase B,
 * interacting with NetMeshBase A on behalf of A.</p>
 * 
 * <p>The implementation class of NetMessageEndpoint determines how to perform
 * the actual communication.</p>
 */
public interface Proxy
        extends
            FactoryCreatedObject<NetMeshBaseIdentifier,Proxy,CoherenceSpecification>
{
    /**
     * Obtain the NetMeshBase to which this Proxy belongs.
     * 
     * @return the NetMeshBase
     */
    public abstract NetMeshBase getNetMeshBase();

    /**
     * Determine the NetMeshBaseIdentifier of the partner NetMeshBase. The partner
     * NetMeshBase is the NetMeshBase with which this Proxy communicates.
     * 
     * @return the NetMeshBaseIdentifier of the partner NetMeshBase
     */
    public abstract NetMeshBaseIdentifier getPartnerMeshBaseIdentifier();

    /**
     * Obtain the NetMessageEndpoint associated with this Proxy.
     *
     * @return the NetMessageEndpoint
     */
    public abstract NetMessageEndpoint getMessageEndpoint();

    /**
     * Set a new CoherenceSpecification.
     *
     * @param newValue the new value
     */
    public abstract void setCoherenceSpecification(
            CoherenceSpecification newValue );

    /**
     * Obtain the CoherenceSpecification currently in effect.
     *
     * @return the current CoherenceSpecification
     */
    public abstract CoherenceSpecification getCoherenceSpecification();
    
    /**
     * Determine when this Proxy was first created. Often this will refer to a time long before this
     * particular Java object instance was created; this time refers to when the connection between
     * the two logical NetMeshBases was created, which could have been in a previous run prior to, say,
     * a server reboot.
     *
     * @return the time this Proxy was created, in System.currentTimeMillis() format
     */
    public abstract long getTimeCreated();

    /**
     * Determine when information held by this Proxy was last updated.
     *
     * @return the time this Proxy was last updated, in System.currentTimeMillis() format
     */
    public abstract long getTimeUpdated();

    /**
     * Determine when information held by this Proxy was last read.
     *
     * @return the time this Proxy was last read, in System.currentTimeMillis() format
     */
    public abstract long getTimeRead();

    /**
     * Determine when this Proxy will expire, if at all.
     *
     * @return the time this Proxy will expire, in System.currentTimeMillis() format, or -1L if never.
     */
    public abstract long getTimeExpires();

    /**
     * Ask this Proxy to obtain from its partner NetMeshBase replicas with the enclosed
     * specification. Do not acquire the lock; that would be a separate operation. 
     * 
     * @param paths the NetMeshObjectAccessSpecifications specifying which replicas should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws NetMeshObjectAccessException accessing the NetMeshBase and obtaining a replica failed
     */
    public abstract void obtainReplica(
            NetMeshObjectAccessSpecification [] paths,
            long                                timeout )
        throws
            NetMeshObjectAccessException;

    /**
     * Ask this Proxy to obtain the lock for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the lock should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public abstract void tryToObtainLocks(
            NetMeshObject [] localReplicas,
            long             timeout )
        throws
            RemoteQueryTimeoutException;

    /**
     * Ask this Proxy to obtain the home replica status for one or more replicas from the
     * partner NetMeshBase. Unlike many of the other calls, this call is
     * synchronous over the network and either succeeds, fails, or times out.
     *
     * @param localReplicas the local replicas for which the home replica status should be obtained
     * @param timeout the timeout, in milliseconds
     * @throws RemoteQueryTimeoutException thrown if this call times out
     */
    public abstract void tryToObtainHomeReplicas(
            NetMeshObject [] localReplicas,
            long             timeout )
        throws
            RemoteQueryTimeoutException;

    /**
     * Send notification to the partner NetMeshBase that this MeshBase has forcibly taken the
     * lock back for the given NetMeshObjects.
     *
     * @param localReplicas the local replicas for which the lock has been forced back
     */
    public abstract void forceObtainLocks(
            NetMeshObject [] localReplicas );

    /**
     * Tell the partner NetMeshBase that one or more local replicas exist here that
     * would like to be resynchronized.
     *
     * @param localReplicas the NetMeshObjectIdentifiers of the local replicas
     */
    public abstract void resynchronizeDependentReplicas(
            NetMeshObjectIdentifier [] localReplicas );

    /**
     * Invoked by the NetMeshBase that this Proxy belongs to,
     * it causes this Proxy to initiate the "ceasing communication" sequence with
     * the partner NetMeshBase, and then kill itself.
     *
     * @throws RemoteQueryTimeoutException thrown if communications timed out
     */
    public abstract void initiateCeaseCommunications()
        throws
            RemoteQueryTimeoutException;

    /**
     * Tell this Proxy that it is not needed any more. This will invoke
     * {@link #initiateCaseCommunications} if and only if
     * isPermanent is true.
     * 
     * @param isPermanent if true, this Proxy will go away permanently; if false,
     *        it may come alive again some time later, e.g. after a reboot
     */
    public abstract void die(
            boolean isPermanent );

    /**
     * Obtain this Proxy in externalized form.
     *
     * @return the ExternalizedProxy capturing the information in this Proxy
     */
    public abstract ExternalizedProxy asExternalized();

    /**
     * Indicates that a Transaction has been committed. This is invoked by the NetMeshBase
     * without needing a subscription.
     *
     * @param theTransaction the Transaction that was committed
     */
    public void transactionCommitted(
            Transaction theTransaction );

    /**
     * Subscribe to lease-related events, without using a Reference.
     *
     * @param newListener the to-be-added listener
     * @see #addWeakLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public abstract void addDirectLeaseManagementListener(
            LeaseManagementListener newListener );

    /**
     * Subscribe to lease-related events, using a WeakReference.
     *
     * @param newListener the to-be-added listener
     * @see #addDirectLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public abstract void addWeakLeaseManagementListener(
            LeaseManagementListener newListener );

    /**
     * Subscribe to lease-related events, using a SoftReference.
     *
     * @param newListener the to-be-added listener
     * @see #addWeakLeaseManagementListener
     * @see #addDirectLeaseManagementListener
     * @see #removeLeaseManagementListener
     */
    public abstract void addSoftLeaseManagementListener(
            LeaseManagementListener newListener );

    /**
     * Unsubscribe from lease-related events.
     *
     * @param oldListener the to-be-removed listener
     * @see #addDirectLeaseManagementListener
     * @see #addWeakLeaseManagementListener
     * @see #addSoftLeaseManagementListener
     */
    public abstract void removeLeaseManagementListener(
            LeaseManagementListener oldListener );

    /**
     * Obtain a String representation of this Proxy that can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentation(
            StringRepresentation rep,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the start part of a String representation of this Proxy that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkStart(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the end part of a String representation of this Proxy that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkEnd(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );
}
