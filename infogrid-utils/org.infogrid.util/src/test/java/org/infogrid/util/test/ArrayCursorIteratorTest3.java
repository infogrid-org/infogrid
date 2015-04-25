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
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests the ArrayCursorIterator.
 */
public class ArrayCursorIteratorTest3
        extends
            AbstractCursorIteratorTest3
{
    @Test
    public void run()
        throws
            Exception
    {
        ArrayCursorIterator<String> iter = ArrayCursorIterator.<String>create( new String[0] );

        runWith( iter, log );
    }

    private static final Log log = Log.getLogInstance( ArrayCursorIteratorTest3.class ); // our own, private logger
}
