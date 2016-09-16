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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.store.test;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.store.StoreMeshBase;
import org.infogrid.meshbase.transaction.CommitFailedException;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Test that invalid multiplicities roll back transactions.
 */
public class MultiplicityTest1
        extends
            AbstractStoreMeshBaseTest
{
    /**
     * Run the test.
     *
     * @throws Exception thrown if an Exception occurred during the test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();

        //

        log.info( "Creating MeshBase" );

        super.startClock();

        StoreMeshBase mb = StoreMeshBase.create(
                theMeshBaseIdentifierFactory.fromExternalForm( "MeshBase" ),
                theModelBase,
                null,
                theSqlStore,
                rootContext );

        MeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();

        //

        log.info( "Attempt one" );

        Transaction tx;
        try {
            tx = mb.createTransactionNow();

            MeshObject one = life.createMeshObject( TestSubjectArea.ONE );
            MeshObject three = life.createMeshObject( TestSubjectArea.THREE );


            tx.commitTransaction();
            reportError( "Should have thrown exception (one)" );

        } catch( CommitFailedException ex ) {
            log.info( "Correctly thrown", ex );
        } catch( Throwable t ) {
            log.error( "FINALLY CAUGHT", t );
        }
        tx = null;

        checkEquals( mb.size(), 1, "Wrong number of MeshObjects" );

        mb.clearMemoryCache();
        collectGarbage();

        checkEquals( mb.size(), 1, "Wrong number of MeshObjects" );

        //

        log.info( "Attempt two" );

        try {
            tx = mb.createTransactionNow();

            MeshObject one = life.createMeshObject( TestSubjectArea.ONE );
            MeshObject three = life.createMeshObject( TestSubjectArea.THREE );
            one.relateAndBless( TestSubjectArea.ONETHREE.getSource(), three );

            tx.commitTransaction();
            reportError( "Should have thrown exception (two)" );

        } catch( CommitFailedException ex ) {
            log.info( "Correctly thrown", ex );
        }
        tx = null;

        checkEquals( mb.size(), 1, "Wrong number of MeshObjects" );

        mb.clearMemoryCache();
        collectGarbage();

        checkEquals( mb.size(), 1, "Wrong number of MeshObjects" );

        //

        log.info( "Attempt three" );

        try {
            tx = mb.createTransactionNow();

            MeshObject one1 = life.createMeshObject( TestSubjectArea.ONE );
            MeshObject one2 = life.createMeshObject( TestSubjectArea.ONE );
            MeshObject one3 = life.createMeshObject( TestSubjectArea.ONE );
            MeshObject three = life.createMeshObject( TestSubjectArea.THREE );
            one1.relateAndBless( TestSubjectArea.ONETHREE.getSource(), three );
            one2.relateAndBless( TestSubjectArea.ONETHREE.getSource(), three );
            one3.relateAndBless( TestSubjectArea.ONETHREE.getSource(), three );

            tx.commitTransaction();

            log.info( "No exception here is correct (three)" );

        } catch( CommitFailedException ex ) {
            reportError( "Should have been no exception", ex );
        }
        tx = null;

        checkEquals( mb.size(), 5, "Wrong number of MeshObjects" );

        mb.clearMemoryCache();
        collectGarbage();

        checkEquals( mb.size(), 5, "Wrong number of MeshObjects" );
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( MultiplicityTest1.class );
}
