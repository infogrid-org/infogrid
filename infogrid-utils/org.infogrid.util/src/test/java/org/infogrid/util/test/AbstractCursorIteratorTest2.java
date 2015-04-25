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

import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;

import java.util.NoSuchElementException;
import org.junit.Assert;

/**
 * Tests one-element CursorIterators.
 * All CursorIterators are run through the same test sequence, which is factored out here.
 */
public abstract class AbstractCursorIteratorTest2
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
            T                 testData,
            CursorIterator<T> iter,
            Log               log )
    {
        //

        log.info( "Check at the beginning" );

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( 1 ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( 2 ) );
        Assert.assertEquals("wrong current element", iter.peekNext(), testData);

        //

        log.info( "Check forward iteration" );

        Assert.assertTrue("Not found next", iter.hasNext() );

        Object found = iter.next();
        Assert.assertEquals("Not found element", testData, found);

        //

        log.info( "Check at the end" );

        Assert.assertTrue("has next at the end", !iter.hasNext() );
        Assert.assertTrue("Does not have enough previous", iter.hasPrevious( 1 ) );
        Assert.assertTrue("Has too many previous", !iter.hasPrevious( 2 ) );
        Assert.assertEquals("wrong last element", iter.peekPrevious(), testData);

        try {
            Object found2 = iter.peekNext();
            Assert.fail( "Found element after end: " + found2 );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Check backward iteration" );

        Assert.assertTrue("Not found previous", iter.hasPrevious() );

        Object found3 = iter.previous();
        Assert.assertEquals("Not found element", testData, found3);

        //

        log.info( "Check again at the beginning" );

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( 1 ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( 2 ) );

        try {
            Object found4 = iter.peekPrevious();
            Assert.fail( "Found element before beginning: " + found4 );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Move to element" );

        iter.moveToBefore( testData ); // "e"
        Assert.assertEquals("wrong element found", iter.peekNext(),     testData);
        Assert.assertTrue("previous found", !iter.hasPrevious() );

        //

        log.info( "Copy" );

        CursorIterator<?> copy = iter.createCopy();

        Assert.assertEquals("copied iterator in a different place", iter.peekNext(), copy.peekNext());

        //

        log.info( "Go to past last" );

        iter.moveToAfterLast();

        Assert.assertTrue("has next at the end", !iter.hasNext() );
        Assert.assertTrue("Does not have enough previous", iter.hasPrevious( 1 ) );
        Assert.assertTrue("Has too many previous", !iter.hasPrevious( 2 ) );
        Assert.assertEquals("wrong last element", iter.peekPrevious(), testData);

        try {
            Object found5 = iter.peekNext();
            Assert.fail( "Found element after end: " + found5 );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go before first" );

        iter.moveToBeforeFirst();

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("Does not have enough nexts", iter.hasNext( 1 ) );
        Assert.assertTrue("Has too many nexts", !iter.hasNext( 2 ) );
        Assert.assertEquals("wrong current element", iter.peekNext(), testData);

        try {
            Object found6 = iter.peekPrevious();
            Assert.fail( "Found element before beginning: " + found6 );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }
    }
}
