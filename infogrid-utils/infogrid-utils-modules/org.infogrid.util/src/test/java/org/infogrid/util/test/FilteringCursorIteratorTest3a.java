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
 * The underlying Iterator has plenty of elements, but the filter accepts none.
 */
public class FilteringCursorIteratorTest3a
        extends
            AbstractCursorIteratorTest3
{
    @Test
    public void run()
        throws
            Exception
    {
        String [] baseData = new String[] {
            "a",    //  [0]   -
            "b",    //  [1]   -
            "c",    //  [2]   -
            "d",    //  [3]   -
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

        runWith( filterIterator, log );
    }

    private static final Log log = Log.getLogInstance( FilteringCursorIteratorTest3a.class ); // our own, private logger
}
