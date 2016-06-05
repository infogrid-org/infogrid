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

package org.infogrid.kernel.test.meshbase.m;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.RelationshipType;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests whether we can create more than one relationship between the same
 * two MeshObjects.
 */
public class MeshBaseTest13
        extends
            AbstractSingleMeshBaseTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may go wrong during a test.
     */
    @Test
    public void run()
        throws
            Exception
    {
        EntityType       typeAA = theModelBase.findEntityType( "org.infogrid.model.Test", "AA" );
        EntityType       typeB  = theModelBase.findEntityType( "org.infogrid.model.Test", "B" );
        RelationshipType typeR  = theModelBase.findRelationshipType( "org.infogrid.model.Test", "R" );
        RelationshipType typeRR = theModelBase.findRelationshipType( "org.infogrid.model.Test", "RR" );
        RelationshipType typeS  = theModelBase.findRelationshipType( "org.infogrid.model.Test", "S" );

        //

        log.info( "Setting up objects" );

        Transaction tx = theMeshBase.createTransactionAsap();

        MeshBaseLifecycleManager life1 = theMeshBase.getMeshBaseLifecycleManager();

        MeshObject aa = life1.createMeshObject( typeAA );
        MeshObject b  = life1.createMeshObject( typeB );

        aa.relateAndBless( typeR.getSource(), b );

        tx.commitTransaction();

        //

        log.info( "Trying to create illegal R relationship" );

        try {
            tx = theMeshBase.createTransactionAsap();

            aa.relateAndBless( typeRR.getSource(), b );

            reportError( "Should have thrown exception" );

        } catch( RelatedAlreadyException ex ) {
            // noop
        }
        tx.commitTransaction();

        //

        log.info( "Trying to bless with RR, should downcast" );

        tx = theMeshBase.createTransactionAsap();

        aa.blessRelationship( typeRR.getSource(), b );

        tx.commitTransaction();

        checkEquals( aa.getRoleTypes().length, 1, "wrong number of role types" );
        checkEquals( typeRR.getSource(), aa.getRoleTypes()[0], "wrong role type" );

        //

        log.info( "Trying to create illegal S relationship" );

        try {
            tx = theMeshBase.createTransactionAsap();

            aa.relateAndBless( typeS.getSource(), b );

            reportError( "Should have thrown exception" );

        } catch( RelatedAlreadyException ex ) {
            // noop
        }
        tx.commitTransaction();

        //

        log.info( "Trying to bless instead (S)" );

        tx = theMeshBase.createTransactionAsap();

        aa.blessRelationship( typeS.getSource(), b );

        tx.commitTransaction();
    }

    // Our Logger
    private static Log log = Log.getLogInstance( MeshBaseTest13.class );
}
