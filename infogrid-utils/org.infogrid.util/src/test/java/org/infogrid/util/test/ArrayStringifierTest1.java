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

import org.infogrid.util.ArrayFacade;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.ArrayStringifier;
import org.infogrid.util.text.LongStringifier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests ArrayStringifier.
 */
public class ArrayStringifierTest1
        extends
            AbstractTest
{
    @Test
    public void run()
        throws
            Exception
    {
        //

        log.info( "ArrayStringifier" );

        ArrayStringifier<Long> str = ArrayStringifier.create( "aaa", "bbb", "ccc", LongStringifier.create() );
        Long [] data = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 0L };
        
        String result = str.format( null, ArrayFacade.create( data ), null );
        
        Assert.assertEquals("wrong length of result string", result.length(), str.getStart().length() + str.getEnd().length() + ( data.length-1 ) * str.getMiddle().length() + data.length);
        Assert.assertTrue("does not start right", result.startsWith( str.getStart() ) );
        Assert.assertTrue("does not end right", result.endsWith( str.getEnd() ) );

        int count = 0;
        int pos   = -1;
        while( ( pos = result.indexOf( str.getMiddle(), pos+1 )) > 0 ) {
            ++count;
        }
        Assert.assertEquals("wrong number of middles", count, data.length-1);
        
        log.debug( "Found result '" + result + "'." );
    }

    private static final Log log = Log.getLogInstance( ArrayStringifierTest1.class ); // our own, private logger
}
