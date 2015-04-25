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

package org.infogrid.util.test;

import java.util.NoSuchElementException;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;
import org.junit.Assert;

/**
 * Tests regular, several-element CursorIterators.
 * All CursorIterators are run through the same test sequence, which is factored out here.
 */
public abstract class AbstractCursorIteratorTest1
        extends
            AbstractTest
{
    /**
     * Run the test.
     * 
     * @param testData the provided test data
     * @param iter the to-be-tested iterator
     * @param log the Logger to use
     * @param <T> the type of Iterator to test
     */
    protected <T> void runWith(
            T []              testData,
            CursorIterator<T> iter,
            Log               log )
    {
        //
        
        log.info( "Check at the beginning" );
        
        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( testData.length ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( testData.length + 1 ) );
        Assert.assertEquals("wrong current element", iter.peekNext(), testData[0]);

        //
        
        log.info( "Check forward iteration" );

        for( int i=0 ; i<testData.length ; ++i ) {
            Assert.assertTrue("Not found next: " + i, iter.hasNext() );
            
            Object found = iter.next();
            Assert.assertEquals("Not found element: " + i, testData[i], found);
        }

        //
        
        log.info( "Check at the end" );

        Assert.assertTrue("has next at the end", !iter.hasNext() );
        Assert.assertTrue("Does not have enough previous", iter.hasPrevious( testData.length ) );
        Assert.assertTrue("Has too many previous", !iter.hasPrevious( testData.length + 1 ) );
        Assert.assertEquals("wrong last element", iter.peekPrevious(), testData[testData.length-1]);

        try {
            Object found = iter.peekNext();
            Assert.fail( "Found element after end: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }
        
        //
        
        log.info( "Check backward iteration" );

        for( int i=testData.length-1 ; i>=0 ; --i ) {
            Assert.assertTrue("Not found previous: " + i, iter.hasPrevious() );
            
            Object found = iter.previous();
            Assert.assertEquals("Not found element: " + i, testData[i], found);
        }
        
        //
        
        log.info( "Check again at the beginning" );
        
        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( testData.length ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( testData.length + 1 ) );
        
        try {
            Object found = iter.peekPrevious();
            Assert.fail( "Found element before beginning: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }
        
        //
        
        log.info( "Move to element" );
        
        iter.moveToBefore( testData[ 4 ] ); // "e"
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData[4]);
        Assert.assertEquals("wrong element found", iter.peekPrevious(), testData[3]);

        //
        
        log.info( "Move by positive number" );
        
        iter.moveBy( 2 ); // "g"
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData[6]);
        Assert.assertEquals("wrong element found", iter.peekPrevious(), testData[5]);

        //
        
        log.info( "Move by negative number" );
        
        iter.moveBy( -3 ); // "d"
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData[3]);
        Assert.assertEquals("wrong element found", iter.peekPrevious(), testData[2]);
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData[3]); 
        Assert.assertEquals("wrong element found", iter.peekPrevious(), testData[2]);

        //
        
        log.info( "Copy" );
        
        CursorIterator<?> copy = iter.createCopy();
        
        Assert.assertEquals("copied iterator in a different place", iter.peekNext(), copy.peekNext());
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData[3]);
        Assert.assertEquals("wrong element found", iter.peekPrevious(), testData[2]);
        Assert.assertEquals("wrong element found", copy.peekNext(),     testData[3]);
        Assert.assertEquals("wrong element found", copy.peekPrevious(), testData[2]);
        
        //
        
        log.info( "Look backward" );

        Object [] before = iter.previous( 100 );

        Assert.assertEquals("wrong number of elements before", before.length, 3);
        for( int i=0 ; i<3 ; ++i ) {
            Assert.assertEquals("wrong data at index " + i, testData[i], before[ before.length-1-i ]);
        }

        //
        
        log.info( "Look forward" );
        
        Object [] after  = copy.next( 100 );

        Assert.assertEquals("wrong number of elements after", after.length, testData.length - 3);
        for( int i=3 ; i<testData.length ; ++i ) {
            Assert.assertEquals("wrong data at index " + i, testData[i], after[ i-3 ]);
        }

        //

        log.info( "Go to past last" );

        iter.moveToAfterLast();

        Assert.assertTrue("has next at the end", !iter.hasNext() );
        Assert.assertTrue("Does not have enough previous", iter.hasPrevious( testData.length ) );
        Assert.assertTrue("Has too many previous", !iter.hasPrevious( testData.length + 1 ) );
        Assert.assertEquals("wrong last element", iter.peekPrevious(), testData[testData.length-1]);

        try {
            Object found = iter.peekNext();
            Assert.fail( "Found element after end: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go before first" );

        iter.moveToBeforeFirst();
        
        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( testData.length ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( testData.length + 1 ) );
        Assert.assertEquals("wrong current element", iter.peekNext(), testData[0]);

        try {
            Object found = iter.peekPrevious();
            Assert.fail( "Found element before beginning:" + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go to the middle, and get all next elements" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        Assert.assertTrue("not enough elements left", iter.hasNext( testData.length - 2 ) );
        Assert.assertTrue("too many elements left", !iter.hasNext( testData.length - 1 ) );
        Assert.assertEquals("elements not found", iter.next( testData.length - 2 ).length, testData.length - 2);

        //

        log.info( "Go to the middle, and get all previous elements" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        Assert.assertTrue("not enough elements left", iter.hasPrevious( 2 ) );
        Assert.assertTrue("too many elements left", !iter.hasPrevious( 3 ) );
        Assert.assertEquals("elements not found", iter.previous( 2 ).length, 2);

        //

        log.info( "Go to the middle, and move to the end" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        Assert.assertTrue("not enough elements left", iter.hasNext( testData.length - 2 ) ); 
        iter.moveBy( testData.length - 2 );
        Assert.assertTrue("has next at the end", !iter.hasNext() );
        Assert.assertTrue("Does not have previous", iter.hasPrevious() );

        //

        log.info( "Go to the middle, and move to the start" );

        iter.moveToBeforeFirst();
        iter.moveBy( 2 );
        Assert.assertTrue("not enough elements left", iter.hasPrevious( 2 ) ); 
        iter.moveBy( -2 );
        Assert.assertTrue("Does not have next at the beginning", iter.hasNext() );
        Assert.assertTrue("Has previous at the beginning", !iter.hasPrevious() );
    }
}
