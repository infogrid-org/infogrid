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

package org.infogrid.kernel.TEST.meshbase.m;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.Sweeper;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.sweeper.ExpiresSweeper;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.util.logging.Log;

/**
 * Tests whether MeshObjects respect their expires property. Compare with StoreMeshBaseTest4.
 */
public class SweeperTest2
        extends
            AbstractMeshBaseTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may go wrong during a test.
     */
    public void run()
        throws
            Exception
    {
        MeshBase  theMeshBase  = MMeshBase.create( theMeshBaseIdentifierFactory.fromExternalForm( "MeshBase"), theModelBase, null, rootContext );

        MeshBaseLifecycleManager life = theMeshBase.getMeshBaseLifecycleManager();

        Sweeper theSweeper = ExpiresSweeper.create();
        theMeshBase.setSweeper( theSweeper );

        //
        
        log.info( "Create a few MeshObjects" );
        
        long now = System.currentTimeMillis();

        MeshObjectIdentifier extName1 = theMeshBase.getMeshObjectIdentifierFactory().fromExternalForm( "obj1" );
        MeshObjectIdentifier extName2 = theMeshBase.getMeshObjectIdentifierFactory().fromExternalForm( "obj2" );
        MeshObjectIdentifier extName3 = theMeshBase.getMeshObjectIdentifierFactory().fromExternalForm( "obj3" );

        Transaction tx = theMeshBase.createTransactionNow();
        
        MeshObject obj1Never = life.createMeshObject( extName1, now, now, now, -1L );
        MeshObject obj2Sec  = life.createMeshObject( extName2, now, now, now, now + 1000L );
        MeshObject obj3Sec  = life.createMeshObject( extName3, now, now, now, now + 2000L );
        
        obj1Never.relate( obj2Sec );

        obj1Never = obj2Sec = obj3Sec = null;
        
        tx.commitTransaction();

        //
        
        log.info( "Checking they are all still there" );

        obj1Never = theMeshBase.findMeshObjectByIdentifier( extName1 );
        obj2Sec   = theMeshBase.findMeshObjectByIdentifier( extName2 );
        obj3Sec   = theMeshBase.findMeshObjectByIdentifier( extName3 );
        
        checkCondition( obj1Never != null, "objNever is dead" );
        checkCondition( obj2Sec   != null, "obj2Sec is dead" );
        checkCondition( obj3Sec   != null, "obj3Sec is dead" );
        checkEquals( obj1Never.traverseToNeighborMeshObjects().size(), 1, "objNever has wrong number of neighbors" );

        obj1Never = obj2Sec = obj3Sec = null;

        //
        
        log.info( "Waiting 1 second and checking that one is gone" );
        
        sleepFor( 1001L );

        now = System.currentTimeMillis();

        obj1Never = theMeshBase.findMeshObjectByIdentifier( extName1 );
        obj2Sec   = theMeshBase.findMeshObjectByIdentifier( extName2 );
        obj3Sec   = theMeshBase.findMeshObjectByIdentifier( extName3 );

        checkCondition( obj1Never != null, "objNever is dead: " + now + " vs. " + obj1Never );
        checkCondition( obj2Sec   == null, "obj2Sec is still alive: " + now + " vs. " + obj2Sec );
        checkCondition( obj3Sec   != null, "obj3Sec is dead: " + now + " vs. " + obj3Sec );
        checkEquals( obj1Never.traverseToNeighborMeshObjects().size(), 0, "objNever has wrong number of neighbors" );
        
        obj1Never = obj2Sec = obj3Sec = null;

        //
        
        log.info( "Waiting 2 seconds and checking that another is gone" );
        
        sleepFor( 1001L );

        now = System.currentTimeMillis();

        obj1Never = theMeshBase.findMeshObjectByIdentifier( extName1 );
        obj2Sec = theMeshBase.findMeshObjectByIdentifier( extName2 );
        obj3Sec = theMeshBase.findMeshObjectByIdentifier( extName3 );

        checkCondition( obj1Never != null, "objNever is dead: " + now + " vs. " + obj1Never );
        checkCondition( obj2Sec   == null, "obj2Sec is still alive: " + now + " vs. " + obj2Sec );
        checkCondition( obj3Sec   == null, "obj3Sec is still alive: " + now + " vs. " + obj3Sec );
        checkEquals( obj1Never.traverseToNeighborMeshObjects().size(), 0, "objNever has wrong number of neighbors" );
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        SweeperTest2 test = null;
        try {
            if( args.length < 0 ) { // well, not quite possible but to stay with the general outline
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new SweeperTest2( args );
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
     * Constructor.
     *
     * @param args command-line arguments
     * @throws Exception all sorts of things may go wrong during a test.
     */
    public SweeperTest2(
            String [] args )
        throws
            Exception
    {
        super( SweeperTest2.class );

    }

    // Our Logger
    private static Log log = Log.getLogInstance( SweeperTest2.class );
}
