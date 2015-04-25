
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
 * Tests zero-element CursorIterators.
 * All CursorIterators are run through the same test sequence, which is factored out here.
 */
public abstract class AbstractCursorIteratorTest3
        extends
            AbstractTest
{
    /**
     * Run the test.
     *
     * @param iter the to-be-tested iterator
     * @param log the Logger to use
     * @param <T> the type of Iterator to test
     */
    protected <T> void runWith(
            CursorIterator<T> iter,
            Log               log )
    {
        //

        log.info( "Check at the beginning" );

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("has next at the end", !iter.hasNext() );

        try {
            Object found = iter.peekNext();
            Assert.fail( "Found element after end: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }
        try {
            Object found = iter.peekPrevious();
            Assert.fail( "Found element before beginning: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Copy" );

        CursorIterator<?> copy = iter.createCopy();

        Assert.assertTrue("has previous at the beginning", !copy.hasPrevious() );
        Assert.assertTrue("has next at the end", !copy.hasNext() );

        try {
            Object found = copy.peekNext();
            Assert.fail( "Found element after end: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }
        try {
            Object found = copy.peekPrevious();
            Assert.fail( "Found element before beginning: " + found );
        } catch( NoSuchElementException t ) {
            log.debug( "Correctly received exception" );
        }

        //

        log.info( "Go to past last" );

        iter.moveToAfterLast();

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("has next at the end", !iter.hasNext() );

        //

        log.info( "Go before first" );

        iter.moveToBeforeFirst();

        Assert.assertTrue("has previous at the beginning", !iter.hasPrevious() );
        Assert.assertTrue("has next at the end", !iter.hasNext() );
    }
}
