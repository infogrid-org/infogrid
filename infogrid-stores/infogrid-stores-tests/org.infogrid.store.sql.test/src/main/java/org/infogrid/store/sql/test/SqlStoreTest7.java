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

package org.infogrid.store.sql.test;

import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.util.logging.Log;

/**
 * Tests that keys are case-sensitive.
 */
public class SqlStoreTest7
        extends
            AbstractSqlStoreTest
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
        long now = System.currentTimeMillis();

        String key1  = "abc";
        String key2  = "ABC";
        String enc   = "enc";
        byte [] data = bytes( "some data" );

        //

        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();

        checkEquals( theSqlStore.size(), 0, "Store not empty" );

        //

        log.info( "Inserting key1 and checking it's there" );

        theTestStore.put( key1, enc, now, now, now, -1L, data );
        checkEquals( theSqlStore.size(), 1, "Wrong stuff in store" );

        //

        log.info( "Check that we can't retrieve with wrong key" );

        checkEqualByteArrays( theTestStore.get( key1 ).getData(), data, "Wrong data retrieved" );
        try {
            StoreValue found = theTestStore.get( key2 );
            reportError( "Could retrieve data with wrong key", found );
            
        } catch( StoreKeyDoesNotExistException ex ) {
            // ok
        }

        //

        log.info( "Inserting key2 and checking it's there" );
        
        theTestStore.put( key2, enc, now, now, now, -1L, data );
        checkEquals( theSqlStore.size(), 2, "Wrong stuff in store" );
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        SqlStoreTest7 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <database engine>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new SqlStoreTest7( args );
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
     * @throws Exception all sorts of things may go wrong in a test
     */
    public SqlStoreTest7(
            String [] args )
        throws
            Exception
    {
        super( args[0], SqlStoreTest7.class );

        theTestStore = theSqlStore;
    }

    /**
     * Constructor for subclasses.
     *
     * @param dataBaseEngine the name of the database engine to use for testing
     * @param c test class
     * @throws Exception all sorts of things may go wrong in a test
     */
    protected SqlStoreTest7(
            String dataBaseEngine,
            Class  c )
        throws
            Exception
    {
        super( dataBaseEngine, c );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( SqlStoreTest7.class);

}
