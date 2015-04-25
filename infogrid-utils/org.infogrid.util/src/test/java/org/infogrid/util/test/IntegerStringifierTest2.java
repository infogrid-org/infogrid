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

import java.util.Iterator;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.IntegerStringifier;
import org.infogrid.util.text.StringifierParsingChoice;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests IntegerStringifier.
 */
public class IntegerStringifierTest2
        extends
            AbstractTest
{
    @Test
    @SuppressWarnings(value={"unchecked"})
    public void run()
        throws
            Exception
    {
        int                data2a = -2347561;
        IntegerStringifier str2   = IntegerStringifier.create( );
        String             res2a  = String.valueOf( data2a );

        Iterator<StringifierParsingChoice<Integer>> iter2    = str2.parsingChoiceIterator( res2a, 0, res2a.length(), Integer.MAX_VALUE, false, null );
        StringifierParsingChoice<Integer> []        choices2 = (StringifierParsingChoice<Integer> []) ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, res2a.length()-1); 
        for( int i=0 ; i<choices2.length ; ++i ) {

            log.debug( "Found " + i + ": " + choices2[i] );

            Assert.assertEquals("Wrong start index", choices2[i].getStartIndex(), 0);
            Assert.assertEquals("Wrong end index", choices2[i].getEndIndex(),   i+2);

            Assert.assertTrue("Wrong result type at index " + i, choices2[i].unformat() instanceof Integer );
            Assert.assertEquals("Wrong result value at index " + i, choices2[i].unformat().toString(), res2a.substring( 0, i+2 ));
        }
    }

    private static final Log log = Log.getLogInstance( IntegerStringifierTest2.class ); // our own, private logger
}
