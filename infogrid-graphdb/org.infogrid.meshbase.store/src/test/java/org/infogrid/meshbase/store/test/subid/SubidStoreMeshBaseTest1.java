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

package org.infogrid.meshbase.store.test.subid;

import java.util.Iterator;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.store.StoreMeshBase;
import org.infogrid.meshbase.store.test.AbstractStoreMeshBaseTest;
import org.infogrid.meshbase.store.test.StoreMeshBaseTest1;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests iterating over those MeshObjects that are "below" in the identifier name space.
 */
public class SubidStoreMeshBaseTest1
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

        log.info( "Creating and populating MeshBase" );

        super.startClock();

        StoreMeshBase mb = StoreMeshBase.create(
                theMeshBaseIdentifierFactory.fromExternalForm( "MeshBase" ),
                theModelBase,
                null,
                theSqlStore,
                rootContext );

        mb.executeNow( (Transaction tx) -> {
            for( int i=0 ; i<MAX ; ++i ) {
                mb.createMeshObject( mb.fromExternalForm( String.format( "id%2d", i )));
                mb.createMeshObject( mb.fromExternalForm( String.format( "id24/%2d", i )));
            }
            return null;
        });

        //

        log.info( "Checking size" );

        checkEquals( mb.size(),                                MAX + MAX + 1, "Wrong size (all)" );
        checkEquals( mb.size( mb.fromExternalForm( "id" ) ),   MAX + MAX,     "Wrong size (id)" );
        checkEquals( mb.size( mb.fromExternalForm( "id24/" )), MAX,           "Wrong size (id24/)" );

        //

        log.info( "Iterating" );

        int count = 0;
        Iterator<MeshObject> iter = mb.iterator();
        while( iter.hasNext() ) {
            iter.next();
            ++count;
        }
        checkEquals( count, MAX + MAX + 1, "Wrong count (all)" );

        int subCount = 0;
        Iterator<MeshObject> subIter = mb.iterator( mb.fromExternalForm( "id24/" ));
        while( subIter.hasNext() ) {
            subIter.next();
            ++subCount;
        }
        checkEquals( subCount, MAX, "Wrong count (sub)" );
    }

    /**
     * The number of MeshObjects to create for the test.
     */
    protected final int MAX = 100;

    // Our Logger
    private static final Log log = Log.getLogInstance( StoreMeshBaseTest1.class );
}
