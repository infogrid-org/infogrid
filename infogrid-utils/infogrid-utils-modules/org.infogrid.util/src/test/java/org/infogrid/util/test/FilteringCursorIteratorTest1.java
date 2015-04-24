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
import org.infogrid.util.FilteringCursorIterator;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests the FilteringCursorIterator.
 */
public class FilteringCursorIteratorTest1
        extends
            AbstractCursorIteratorTest1
{
    @Test
    public void run()
        throws
            Exception
    {
        String [] baseData = new String[] {
            "a",    //  [0]   -
            "b+A",  //  [1]  [0]
            "c",    //  [2]   -
            "d+B",  //  [3]  [1]
            "e+C",  //  [4]  [2]
            "f",    //  [5]   -
            "g",    //  [6]   -
            "h+D",  //  [7]  [3]
            "i+E",  //  [8]  [4]
            "j+F",  //  [9]  [5]
            "k+G",  // [10]  [6]
            "l+H",  // [11]  [7]
        };
        String [] testData = new String[] {
            "b+A",  //  [1]  [0]
            "d+B",  //  [3]  [1]
            "e+C",  //  [4]  [2]
            "h+D",  //  [7]  [3]
            "i+E",  //  [8]  [4]
            "j+F",  //  [9]  [5]
            "k+G",  // [10]  [6]
            "l+H",  // [11]  [7]
        };

        ArrayCursorIterator<String> baseIterator = ArrayCursorIterator.<String>create( baseData );
        
        FilteringCursorIterator<String> filterIterator = FilteringCursorIterator.<String>create(
                baseIterator,
                new FilteringCursorIterator.Filter<String>() {
                    public boolean accept(
                            String s )
                    {
                        boolean ret = s.indexOf( "+" ) >= 0;
                        return ret;
                    }
                },
                String.class );

        runWith( testData, filterIterator, log );
    }

    private static final Log log = Log.getLogInstance( FilteringCursorIteratorTest1.class ); // our own, private logger
}
