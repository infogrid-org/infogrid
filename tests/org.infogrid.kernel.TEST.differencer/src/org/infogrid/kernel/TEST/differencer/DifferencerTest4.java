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

package org.infogrid.kernel.TEST.differencer;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.IterableMeshBaseDifferencer;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.modelbase.ModelBaseSingleton;

import org.infogrid.util.logging.Log;

/**
 * This test tests the MeshBaseDifferencer on IterableMMeshBases.
 *
 * Create related MeshObjects, change relationship graph, make sure appropriate
 * MeshObjectNeighborChangeEvents are created.
 */
public class DifferencerTest4
        extends
            AbstractDifferencerTest
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
        long now = 1234L;

        //

        log.info( "Creating MeshObjects in MeshBase1" );
        
        MeshBaseLifecycleManager life1 = theMeshBase1.getMeshBaseLifecycleManager();

        Transaction tx1 = theMeshBase1.createTransactionNow();

        MeshObject a1_mb1 = createMeshObject( life1, theMeshBase1.getMeshObjectIdentifierFactory().fromExternalForm( "a1" ), now );
        MeshObject a2_mb1 = createMeshObject( life1, theMeshBase1.getMeshObjectIdentifierFactory().fromExternalForm( "a2" ), now );
        MeshObject a3_mb1 = createMeshObject( life1, theMeshBase1.getMeshObjectIdentifierFactory().fromExternalForm( "a3" ), now );
        MeshObject a4_mb1 = createMeshObject( life1, theMeshBase1.getMeshObjectIdentifierFactory().fromExternalForm( "a4" ), now );

        a1_mb1.relate( a2_mb1 );
        a1_mb1.relate( a3_mb1 );

        tx1.commitTransaction();

        //
        
        log.info( "Creating MeshObjects in MeshBase1" );
        
        MeshBaseLifecycleManager life2 = theMeshBase2.getMeshBaseLifecycleManager();

        Transaction tx2 = theMeshBase2.createTransactionNow();

        MeshObject a1_mb2 = createMeshObject( life2, theMeshBase2.getMeshObjectIdentifierFactory().fromExternalForm( "a1" ), now );
        MeshObject a2_mb2 = createMeshObject( life2, theMeshBase2.getMeshObjectIdentifierFactory().fromExternalForm( "a2" ), now );
        MeshObject a3_mb2 = createMeshObject( life2, theMeshBase2.getMeshObjectIdentifierFactory().fromExternalForm( "a3" ), now );
        MeshObject a4_mb2 = createMeshObject( life2, theMeshBase2.getMeshObjectIdentifierFactory().fromExternalForm( "a4" ), now );

        a1_mb2.relate( a2_mb2 );
        a1_mb2.relate( a4_mb2 );
        
        tx2.commitTransaction();

        //

        log.info( "now differencing" );

        IterableMeshBaseDifferencer diffA = new IterableMeshBaseDifferencer( theMeshBase1 );

        ChangeSet theChangeSetA = diffA.determineChangeSet( theMeshBase2, false );

        printChangeSet( log, theChangeSetA );
        
        checkEquals( theChangeSetA.size(), 4, "Not the right number of changes" );
        
        //
        
        log.info( "now differencing in the reverse direction" );
        
        IterableMeshBaseDifferencer diffB = new IterableMeshBaseDifferencer( theMeshBase2 );

        ChangeSet theChangeSetB = diffB.determineChangeSet( theMeshBase1, false );

        printChangeSet( log, theChangeSetB );
        
        checkEquals( theChangeSetB.size(), 4, "Not the right number of changes" );

        //

        log.info( "now applying changes" );

        Transaction tx = diffA.getBaselineMeshBase().createTransactionNow();
        diffA.applyChangeSet( theChangeSetA );
        tx.commitTransaction();

        //

        ChangeSet theEmptyChangeSetA = diffA.determineChangeSet( theMeshBase2 );

        checkEquals( theEmptyChangeSetA.size(), 0, "Change set not empty after applying differences" );

        if( theEmptyChangeSetA.size() > 0 ) {
            log.debug( "ChangeSet is " + theEmptyChangeSetA );
        } else {
            log.debug( "ChangeSet is empty" );
        }
    }

    /*
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        DifferencerTest4 test = null;
        try {
            if( args.length < 0 ) { // well, not quite possible but to stay with the general outline
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new DifferencerTest4( args );
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
      */
    public DifferencerTest4(
            String [] args )
        throws
            Exception
    {
        super( DifferencerTest4.class );

        theMeshBase1 = MMeshBase.create(
                MeshBaseIdentifier.create( "MeshBase1" ),
                ModelBaseSingleton.getSingleton(),
                null,
                rootContext );
        theMeshBase2 = MMeshBase.create(
                MeshBaseIdentifier.create( "MeshBase2" ),
                ModelBaseSingleton.getSingleton(),
                null,
                rootContext );
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        theMeshBase2.die();
        theMeshBase1.die();
    }

    /**
     * The first MeshBase for the test.
     */
    protected IterableMeshBase theMeshBase1;

    /**
     * The second MeshBase for the test.
     */
    protected IterableMeshBase theMeshBase2;

    // Our Logger
    private static Log log = Log.getLogInstance( DifferencerTest4.class );
}