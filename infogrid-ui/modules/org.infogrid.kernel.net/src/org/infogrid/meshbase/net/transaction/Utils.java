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

package org.infogrid.meshbase.net.transaction;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

/**
 * Some utility methods commonly used in this package.
 */
public abstract class Utils
{
    /**
     * Private constructor to keep this abstract.
     */
    private Utils()
    {
        // noop
    }

    /**
     * Determine whether proxy points away from the source of the change.
     *
     * @param change the NetChange
     * @param proxy the Proxy
     * @return true if the Proxy points away from the source of the change
     */
    public static boolean awayFromLock(
            NetChange change,
            Proxy     proxy )
    {
        NetMeshObject affectedMeshObject = change.getAffectedMeshObject();
        if( affectedMeshObject == null ) {
            // can happen if neighbors aren't replicated
            return false;
        }

        Proxy [] proxies = affectedMeshObject.getAllProxies();
        if( proxies == null ) {
            return false;
        }
        if( affectedMeshObject.getProxyTowardsLockReplica() == proxy ) {
            return false;
        }
        for( Proxy p : proxies ) {
            if( p == proxy ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determine whether there is a replica of a NetMeshObject in the direction of
     * a given proxy, exluding another.
     */
    public static boolean hasReplicaInDirection(
            NetChange         change,
            Proxy             direction,
            NetMeshBaseIdentifier exclude )
    {
        if( direction.getPartnerMeshBaseIdentifier().equals( exclude )) {
            return false;
        }

        NetMeshObject affectedMeshObject = change.getAffectedMeshObject();

        return hasReplicaInDirection( affectedMeshObject, direction );
    }
    
    /**
     * Determine whether a NetMeshObject has a replica in the direction of a given Proxy.
     */
    public static boolean hasReplicaInDirection(
            NetMeshObject obj,
            Proxy         direction )
    {
        Proxy [] proxies = obj.getAllProxies();
        if( proxies == null ) {
            return false;
        }

        for( Proxy p : proxies ) {
            if( p == direction ) {
                return true;
            }
        }
        return false;
    }
}
