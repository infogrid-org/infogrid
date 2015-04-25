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
 */
public class PagingCursorIteratorTest1
        extends
            AbstractCursorIteratorTest1
{
    @Test
    public void run()
        throws
            Exception
    {
        String [] baseData = new String[] {
            "a",  //  [0]   -
            "b",  //  [1]   -
            "c",  //  [2]   -
            "d",  //  [3]  [0]
            "e",  //  [4]  [1]
            "f",  //  [5]  [2]
            "g",  //  [6]  [3]
            "h",  //  [7]  [4]
            "i",  //  [8]  [5]
            "j",  //  [9]  [6]
            "k",  // [10]  [7]
            "l",  // [11]   -
            "m",  // [12]   -
        };
        String [] testData = new String[] {
            "d",  //  [1]  [0]
            "e",  //  [3]  [1]
            "f",  //  [4]  [2]
            "g",  //  [7]  [3]
            "h",  //  [8]  [4]
            "i",  //  [9]  [5]
            "j",  //  [9]  [6]
            "k",  // [10]  [7]
        };

        ArrayCursorIterator<String> baseIterator = ArrayCursorIterator.<String>create( baseData );

        PagingCursorIterator<String> pagingIterator = PagingCursorIterator.<String>create(
                baseData[3],
                testData.length,
                baseIterator,
                String.class );

        runWith( testData, pagingIterator, log );
    }

    private static final Log log = Log.getLogInstance( PagingCursorIteratorTest1.class ); // our own, private logger
}
