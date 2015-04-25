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

import org.infogrid.util.ArrayCursorIterator;
import org.infogrid.util.PagingCursorIterator;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests the PagingCursorIterator.
 * The underlying Iterator has only one element, and the window is larger than 1
 */
public class PagingCursorIteratorTest2b
        extends
            AbstractCursorIteratorTest2
{
    @Test
    public void run()
        throws
            Exception
    {
        String [] baseData = new String[] {
            "a",  //  [0]  [0]
        };
        String [] testData = new String[] {
            "a",  //  [0]  [0]
        };

        ArrayCursorIterator<String> baseIterator = ArrayCursorIterator.<String>create( baseData );

        PagingCursorIterator<String> pagingIterator = PagingCursorIterator.<String>create(
                baseData[0],
                10, // bigger window
                baseIterator,
                String.class );

        runWith( testData[0], pagingIterator, log );
    }

    private static final Log log = Log.getLogInstance( PagingCursorIteratorTest2b.class ); // our own, private logger
}
