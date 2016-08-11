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

package org.infogrid.probe.test.shadow;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseDifferencer;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.testharness.util.IteratorElementCounter;
import org.infogrid.util.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Reads (via the Probe framework) test1.xml into a NetMeshBase.
 */
@RunWith(Parameterized.class)
public class ShadowTest1
        extends
            AbstractShadowTest
{
    /**
     * Test parameters.
     *
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][] {
                {
                    AbstractTest.fileSystemFileName( ShadowTest1.class, "ShadowTest1.xml" )
                } });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing #abc of test file with NetMeshBase" );

        MeshObject abc = base.accessLocally(
                base.getNetMeshObjectAccessSpecificationFactory().obtain(
                        testFile1Id,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.toExternalForm() + "#abc" ),
                        CoherenceSpecification.ONE_TIME_ONLY ));

        checkObject( abc, "Object not found" );
        checkEquals( IteratorElementCounter.countIteratorElements( base.proxies()), 1, "wrong number of proxies in main NetMeshBase" );

        //

        log.info( "accessing #def of test file with NetMeshBase" );

        MeshObject def = base.accessLocally(
                base.getNetMeshObjectAccessSpecificationFactory().obtain(
                        testFile1Id,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.toExternalForm() + "#def" ),
                        CoherenceSpecification.ONE_TIME_ONLY ));

        checkObject( def, "Object not found" );
        checkEquals( IteratorElementCounter.countIteratorElements( base.proxies()), 1, "wrong number of proxies in main NetMeshBase" );

        //

        log.info( "traverse to related objects" );

        MeshObjectSet abcNeighbors = abc.traverseToNeighborMeshObjects();
        checkEquals( abcNeighbors.size(), 0, "wrong number of neighbors for abc" );

        MeshObjectSet defNeighbors = def.traverseToNeighborMeshObjects();
        checkEquals( defNeighbors.size(), 1, "wrong number of neighbors for def" );

        //

        log.info( "now compare main and shadow base" );

        ShadowMeshBase shadow = base.getShadowMeshBaseFor( testFile1Id );

        NetMeshBaseDifferencer diff = new NetMeshBaseDifferencer( base );
        ChangeSet changes = diff.determineChangeSet( shadow );

        // these two are here for better debugging
        MeshObject baseHome   = base.getHomeObject();
        MeshObject shadowHome = shadow.getHomeObject();

        checkEquals( changes.size(), 6, "wrong number of changes" );
        // These changes should be:
        // 1. Home Object created
        // 2. Home Object deleted
        // 3. ProbeUpdateSpecification#ProbeRunCounter changed
        // 4. ProbeUpdateSpecification#LastRunUsedWritableProbe changed
        // 5. ProbeUpdateSpecification#LastProbeRun changed
        // 6. ProbeUpdateSpecification#LastRunUsedProbeClass changed

        if( changes.size() != 6 ) {
            dumpChangeSet( changes, log );
        }
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile1 the test file
     * @throws Exception all sorts of things can go wrong during a test
     */
    public ShadowTest1(
            String testFile1 )
        throws
            Exception
    {
        testFile1Id = theMeshBaseIdentifierFactory.obtain( new File( testFile1 ) );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest1.class);

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;
}
