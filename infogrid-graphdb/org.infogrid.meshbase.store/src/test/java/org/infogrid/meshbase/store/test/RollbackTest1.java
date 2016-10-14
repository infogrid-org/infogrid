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

package org.infogrid.meshbase.store.test;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MultiplicityException;
import org.infogrid.meshbase.store.IterableStoreMeshBase;
import org.infogrid.meshbase.transaction.TransactionAction;
import org.infogrid.meshbase.transaction.TransactionActionException;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Test that rollbacks don't leave stuff in the database.
 */
public class RollbackTest1
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
        //
        
        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();
        
        //

        log.info( "Creating MeshBase" );

        IterableStoreMeshBase mb = IterableStoreMeshBase.create(
                theMeshBaseIdentifierFactory.fromExternalForm( "MeshBase" ),
                theModelBase,
                null,
                theSqlStore,
                rootContext );

        checkEquals( mb.size(), 1, "Wrong number of objects" );

        //

        log.info( "Creating MeshObjects in a way that makes commit fail" );
        
        try {
            mb.executeNow( new TransactionAction<Void>() {
                @Override
                public Void execute()
                    throws
                        Throwable
                {
                    life.createMeshObject( idFact.fromExternalForm( "aa" ), TestSubjectArea.AA );

                    MeshObject one1 = life.createMeshObject( idFact.fromExternalForm( "one1" ), TestSubjectArea.ONE );
                    MeshObject three = life.createMeshObject( idFact.fromExternalForm( "three" ), TestSubjectArea.THREE );

                    one1.relateAndBless( TestSubjectArea.ONETHREE.getSource(), three ); // violates cardinality

                    return null;
                }
            });

        } catch( TransactionActionException ex ) {
            Throwable cause = ex.getCause();
            if( !( cause instanceof MultiplicityException )) {
                reportError( "Wrong exception", cause );
            }
        }
        
        mb.die( false );

        //
        
        log.info( "Recreating MeshBase" );
        
        mb = IterableStoreMeshBase.create(
                theMeshBaseIdentifierFactory.fromExternalForm( "MeshBase" ),
                theModelBase,
                null,
                theSqlStore,
                rootContext );

        checkEquals( mb.size(), 1, "Wrong number of objects" );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( RollbackTest1.class );
}
