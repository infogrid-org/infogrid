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

package org.infogrid.store.test;

import java.util.NoSuchElementException;
import org.infogrid.store.Store;
import org.infogrid.store.StoreValue;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests the filter-by-prefix functionality of any StoreIterator. Subclasses define the Store implementation.
 */
public abstract class AbstractStoreIteratorTest4
        extends
            AbstractTest
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

        log.info( "Deleting old database and creating the Store" );

        theTestStore.initializeHard();

        //

        log.info( "Inserting data" );

        for( int i=0 ; i<allData.length ; ++i ) {
            StoreValue current = allData[i];
            theTestStore.put(
                    current.getKey(),
                    current.getEncodingId(),
                    current.getTimeCreated(),
                    current.getTimeUpdated(),
                    current.getTimeRead(),
                    current.getTimeExpires(),
                    current.getData() );
        }

        //

        log.info( "Checking sizes" );

        checkEquals( theTestStore.size(),               allData.length,   "Wrong size of store" );
        checkEquals( theTestStore.size( "aa" ),         allData.length,   "Wrong size of store with prefix aa" );
        checkEquals( theTestStore.size(testPrefix ), testData.length, "Wrong size of store with prefix aa2" );

        CursorIterator<StoreValue> iter = theTestStore.iterator( testPrefix );

        //

        log.info( "Check at the beginning" );

        checkCondition( !iter.hasPrevious(), "has previous at the beginning" );
        checkCondition( iter.hasNext( testData.length ), "Does not have enough nexts" );
        checkCondition( !iter.hasNext( testData.length + 1 ), "Has too many nexts" );
        checkEquals( iter.peekNext(), testData[0], "wrong current element" );

        //

        log.info( "Check forward iteration" );

        for( int i=0 ; i<testData.length ; ++i ) {
            checkCondition( iter.hasNext(), "Not found next: " + i );

            StoreValue found = iter.next();
            checkEquals( testData[i], found, "Not found element: " + i );
        }

        //

        log.info( "Check at the end" );

        checkCondition( !iter.hasNext(), "has next at the end" );
        checkCondition( iter.hasPrevious( testData.length ), "Does not have enough previous" );
        checkCondition( !iter.hasPrevious( testData.length + 1 ), "Has too many previous" );
        checkEquals( iter.peekPrevious(), testData[testData.length-1], "wrong last element" );

        try {
            StoreValue found = iter.peekNext();
            reportError( "Found element after end", found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Check backward iteration" );

        for( int i=testData.length-1 ; i>=0 ; --i ) {
            checkCondition( iter.hasPrevious(), "Not found previous: " + i );

            StoreValue found = iter.previous();
            checkEquals( testData[i], found, "Not found element: " + i );
        }

        //

        log.info( "Check again at the beginning" );

        checkCondition( !iter.hasPrevious(), "has previous at the beginning" );
        checkCondition( iter.hasNext( testData.length ), "Does not have enough nexts" );
        checkCondition( !iter.hasNext( testData.length + 1 ), "Has too many nexts" );

        try {
            StoreValue found = iter.peekPrevious();
            reportError( "Found element before beginning", found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Move to element" );

        iter.moveToBefore( testData[ 4 ] ); // "aa04"
        checkEquals( iter.peekNext(),     testData[4], "wrong element found" );
        checkEquals( iter.peekPrevious(), testData[3], "wrong element found" );

        //

        log.info( "Move by positive number" );

        iter.moveBy( 2 ); // "g"
        checkEquals( iter.peekNext(),     testData[6], "wrong element found" );
        checkEquals( iter.peekPrevious(), testData[5], "wrong element found" );

        //

        log.info( "Move by negative number" );

        iter.moveBy( -3 ); // "d"
        checkEquals( iter.peekNext(),     testData[3], "wrong element found" );
        checkEquals( iter.peekPrevious(), testData[2], "wrong element found" );
        checkEquals( iter.peekNext(),     testData[3], "wrong element found" ); // make sure we can move about a bit, so repeat
        checkEquals( iter.peekPrevious(), testData[2], "wrong element found" );

        //

        log.info( "Copy" );

        CursorIterator<StoreValue> copy = iter.createCopy();

        checkEquals( iter.peekNext(), copy.peekNext(), "copied iterator in a different place" );
        checkEquals( iter.peekNext(),     testData[3], "wrong element found" );
        checkEquals( iter.peekPrevious(), testData[2], "wrong element found" );
        checkEquals( copy.peekNext(),     testData[3], "wrong element found" );
        checkEquals( copy.peekPrevious(), testData[2], "wrong element found" );

        //

        log.info( "Look backward" );

        StoreValue [] before = iter.previous( 100 );

        checkEquals( before.length, 3, "wrong number of elements before" );
        for( int i=0 ; i<3 ; ++i ) {
            checkEquals( testData[i], before[ before.length-1-i ], "wrong data at index " + i );
        }

        //

        log.info( "Look forward" );

        StoreValue [] after  = copy.next( 100 );

        checkEquals( after.length, testData.length - 3, "wrong number of elements after" );
        for( int i=3 ; i<testData.length ; ++i ) {
            checkEquals( testData[i], after[ i-3 ], "wrong data at index " + i );
        }

        //

        log.info( "Go to past last" );

        iter.moveToAfterLast();

        checkCondition( !iter.hasNext(), "has next at the end" );
        checkCondition( iter.hasPrevious( testData.length ), "Does not have enough previous" );
        checkCondition( !iter.hasPrevious( testData.length + 1 ), "Has too many previous" );
        checkEquals( iter.peekPrevious(), testData[testData.length-1], "wrong last element" );

        try {
            StoreValue found = iter.peekNext();
            reportError( "Found element after end", found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go before first" );

        iter.moveToBeforeFirst();

        checkCondition( !iter.hasPrevious(), "has previous at the beginning" );
        checkCondition( iter.hasNext( testData.length ), "Does not have enough nexts" );
        checkCondition( !iter.hasNext( testData.length + 1 ), "Has too many nexts" );
        checkEquals( iter.peekNext(), testData[0], "wrong current element" );

        try {
            StoreValue found = iter.peekPrevious();
            reportError( "Found element before beginning", found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go to the middle, and get all next elements" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        checkCondition( iter.hasNext( testData.length - 2 ), "not enough elements left" );
        checkCondition( !iter.hasNext( testData.length - 1 ), "too many elements left" );
        checkEquals( iter.next( testData.length - 2 ).length, testData.length - 2, "elements not found" );

        //

        log.info( "Go to the middle, and get all previous elements" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        checkCondition( iter.hasPrevious( 2 ), "not enough elements left" );
        checkCondition( !iter.hasPrevious( 3 ), "too many elements left" );
        checkEquals( iter.previous( 2 ).length, 2, "elements not found" );

        //

        log.info( "Go to the middle, and move to the end" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        checkCondition( iter.hasNext( testData.length - 2 ), "not enough elements left" ); // tested earlier
        iter.moveBy( testData.length - 2 );
        checkCondition( !iter.hasNext(), "has next at the end" );
        checkCondition( iter.hasPrevious(), "Does not have previous" );

        //

        log.info( "Go to the middle, and move to the start" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        checkCondition( iter.hasPrevious( 2 ), "not enough elements left" ); // tested earlier
        iter.moveBy( -2 );
        checkCondition( iter.hasNext(), "Does not have next at the beginning" );
        checkCondition( !iter.hasPrevious(), "Has previous at the beginning" );
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( AbstractStoreIteratorTest1.class );

    /**
     * The actual Store to be tested. This may or may not be pointed to theSqlStore
     * by subclasses.
     */
    protected Store theTestStore;

    /**
     * Test data.
     */
    protected static final long now = System.currentTimeMillis();
    protected static final StoreValue[] allData    = new StoreValue[ 100 ];
    protected static final StoreValue[] testData   = new StoreValue[ 10 ];
    protected static final String       testPrefix = "aa2";

    static {
        for( int i=0 ; i<allData.length ; ++i ) {
            allData[i] = new StoreValue(
                String.format( "aa%02dbb", i ),
                "enc",
                0L,
                0L,
                0L,
                -1L,
                bytes( "value for " + i ));
            if( i>=20 && i<30 ) {
                testData[i-20] = allData[i];
            }
        }

    };
}
