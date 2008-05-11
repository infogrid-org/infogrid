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

package org.infogrid.probe.TEST;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.IterableNetMeshBaseDifferencer;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.testharness.util.IteratorElementCounter;
import org.infogrid.util.logging.Log;

/**
 * Reads (via the Probe framework) test1.xml into a NetMeshBase.
 */
public class ShadowTest1
        extends
            AbstractProbeTest
{
    /**
     * Run the test.
     *
     * @throws Exception thrown if an Exception occurred during the test
     */
    public void run()
        throws
            Exception
    {
        log.info( "Setting up" );
        
        ProbeDirectory        dir  = MProbeDirectory.create();
        NetMeshBaseIdentifier here = NetMeshBaseIdentifier.create( "http://here.local/" ); // this is not going to work for communications
        LocalNetMMeshBase     base = LocalNetMMeshBase.create( here, theModelBase, null, exec, dir, rootContext );

        //
        
        log.info( "accessing #abc of test file with NetworkedMeshBase" );
        
        MeshObject abc = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        testFile1Id,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.toExternalForm() + "#abc" ),
                        CoherenceSpecification.ONE_TIME_ONLY ));

        checkObject( abc, "Object not found" );
        checkEquals( IteratorElementCounter.countIteratorElements( base.proxies()), 1, "wrong number of proxies in main NetMeshBase" );
        
        //
        
        log.info( "accessing #def of test file with NetworkedMeshBase" );
        
        MeshObject def = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
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
        
        IterableNetMeshBaseDifferencer diff = new IterableNetMeshBaseDifferencer( base );
        ChangeSet changes = diff.determineChangeSet( shadow );

        // these two are here for better debugging
        MeshObject baseHome   = base.getHomeObject();
        MeshObject shadowHome = shadow.getHomeObject();
        
        checkEquals( changes.size(), 3, "wrong number of changes" );
        // These changes should be:
        // 1. Home Object created
        // 2. Home Object deleted
        // 3. ProbeUpdateCounter changed

        if( changes.size() != 2 ) {
            dumpChangeSet( changes, log );
        }
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        ShadowTest1 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new ShadowTest1( args );
            test.run();

        } catch( Throwable ex ) {
            log.error( ex );
            System.exit(1);
        }
        if( test != null ) {
            test.cleanup();
        }
        if( errorCount == 0 ) {
            log.info( "PASS" );
        } else {
            log.info( "FAIL (" + errorCount + " errors)" );
        }
        System.exit( errorCount );
    }

    /**
      * constructor
      */
    public ShadowTest1(
            String [] args )
        throws
            Exception
    {
        super( ShadowTest1.class );

        testFile1 = args[0];

        testFile1Id = NetMeshBaseIdentifier.create( new File( testFile1 ) );

    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        exec.shutdown();
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest1.class);

    /**
     * File name of the first test file.
     */
    protected String testFile1;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );
}