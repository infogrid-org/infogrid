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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.sweeper;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.util.ResourceHelper;

/**
 * A SweepPolicy that gets rid of unused non-master/non-home-object replicas.
 */
public class UnnecessaryReplicasSweepPolicy
        extends
           AbstractNetSweepPolicy
{
    /**
     * Factory method.
     *
     * @return the created UnnecessaryReplicasSweepPolicy
     */
    public static UnnecessaryReplicasSweepPolicy create()
    {
        return new UnnecessaryReplicasSweepPolicy( DEFAULT_EXPIRATION );
    }

    /**
     * Factory method.
     *
     * @param unusedSlaveReplicaExpiration for which a slave replica must be unused until it becomes a candidate for sweeping, in milliseconds
     * @return the created UnnecessaryReplicasSweepPolicy
     */
    public static UnnecessaryReplicasSweepPolicy create(
            long unusedSlaveReplicaExpiration )
    {
        return new UnnecessaryReplicasSweepPolicy( unusedSlaveReplicaExpiration );
    }

    /**
     * Constructor.
     * 
     * @param unusedSlaveReplicaExpiration for which a slave replica must be unused until it becomes a candidate for sweeping, in milliseconds
     */
    protected UnnecessaryReplicasSweepPolicy(
            long unusedSlaveReplicaExpiration )
    {
        theUnusedSlaveReplicaExpiration = unusedSlaveReplicaExpiration;
    }
    
    /**
     * Determine whether this candidate MeshObject should be swept, according
     * to this Sweeper.
     *
     * @param candidate the MeshObject that is a candidate for sweeping
     * @return true of the MeshObject should be swept
     */
    public boolean shouldBeDeleted(
            MeshObject candidate )
    {
        return false;
    }

    /**
     * Determine whether this candidate NetMeshObject should be purged, according
     * to this Sweeper.
     *
     * @param candidate the NetMeshObject that is a candidate for purging
     * @return true of the NetMeshObject should be purged
     */
    public boolean shouldBePurged(
            NetMeshObject candidate )
    {
        Proxy towardsHome = candidate.getProxyTowardsHomeReplica();
        if( towardsHome == null ) {
            // we never purge the home replica
            return false;
        }
        if( candidate.hasPropertyChangeListener() ) {
            // we never purge objects that have listeners
            return false;
        }
        MeshObjectIdentifier [] neighbors                = candidate.getNeighborMeshObjectIdentifiers();
        MeshObjectIdentifier [] neighborsAccordingToHome = candidate.getNeighborMeshObjectIdentifiersAccordingTo( towardsHome );

        if(    neighbors != null
            && neighborsAccordingToHome != null
            && neighbors.length > neighborsAccordingToHome.length )
        {
            // have relationships that home doesn't know about
            return false;
        }

        long touched = candidate.getTimeRead();
        boolean ret;
        
        if( touched > 0 ) {
            long now = System.currentTimeMillis();
            long delta =  now - touched;
            if( delta < theUnusedSlaveReplicaExpiration ) {
                ret = false;
            } else {
                ret = true;
            }
        } else {
            ret = true;
        }
        return ret;
    }

    /**
     * The duration for which a slave replica must be unused until it becomes
     * a candidate for sweeping.
     */
    protected long theUnusedSlaveReplicaExpiration;

    /**
     * The default duration for which a slave replica must be unused until it becomes
     * a candidate for sweeping.
     */
    public static final long DEFAULT_EXPIRATION = ResourceHelper.getInstance( UnnecessaryReplicasSweepPolicy.class ).getResourceLongOrDefault(
            "DefaultExpiration",
            24L*60L*60L*1000L ); // 1 day
}
