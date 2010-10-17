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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.sweeper;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.IterableNetMeshBase;
import org.infogrid.meshbase.net.NetSweepPolicy;
import org.infogrid.meshbase.sweeper.DefaultIterableSweeper;

/**
 * Adds functionality to DefaultIterableSweeper that deals with replicas.
 */
public class DefaultNetIterableSweeper
        extends
            DefaultIterableSweeper
{
    /**
     * Factory method if the Sweeper is only supposed to be invoked manually.
     *
     * @param mb the IterableMeshBase on which this Sweeper works
     * @param policy the SweepPolicy to use
     * @return the created DefaultIterableSweeper
     */
    public static DefaultNetIterableSweeper create(
            IterableNetMeshBase      mb,
            NetSweepPolicy           policy )
    {
        return new DefaultNetIterableSweeper( mb, policy, null );
    }

    /**
     * Factory method.
     *
     * @param mb the IterableMeshBase on which this Sweeper works
     * @param policy the SweepPolicy to use
     * @param scheduler the scheduler to use, if any
     * @return the created DefaultIterableSweeper
     */
    public static DefaultNetIterableSweeper create(
            IterableNetMeshBase      mb,
            NetSweepPolicy           policy,
            ScheduledExecutorService scheduler )
    {
        return new DefaultNetIterableSweeper( mb, policy, scheduler );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param mb the IterableMeshBase on which this Sweeper works
     * @param policy the NetSweepPolicy to use
     */
    protected DefaultNetIterableSweeper(
            IterableNetMeshBase      mb,
            NetSweepPolicy           policy,
            ScheduledExecutorService scheduler )
    {
        super( mb, policy, scheduler );
    }

    /**
     * Perform a sweep on every single MeshObject in this InterableMeshBase.
     * This may take a long time; using background sweeping is almost always
     * a better alternative.
     */
    @Override
    public synchronized void sweepAllNow()
    {
        NetSweepPolicy realPolicy = (NetSweepPolicy) thePolicy;

        for( MeshObject candidate : theMeshBase ) {
            boolean done = thePolicy.potentiallyDelete( candidate );
            if( !done ) {
                realPolicy.potentiallyPurge( (NetMeshObject) candidate );
            }
        }
    }
}
