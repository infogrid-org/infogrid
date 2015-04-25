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
import org.infogrid.util.text.StringStringifier;
import org.infogrid.util.text.StringifierParsingChoice;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests StringStringifier.
 */
public class StringStringifierTest1
        extends
            AbstractTest
{
    @Test
    @SuppressWarnings(value={"unchecked"})
    public void run()
        throws
            Exception
    {
        //

        log.info( "StringStringifier" );
        
        String            data1a = "abc";
        StringStringifier str1   = StringStringifier.create( );
        String            res1a  = str1.format( null, data1a, null );
        
        Assert.assertEquals("not the same", data1a, res1a);

        String temp = str1.unformat( res1a, null );
        Assert.assertEquals("Wrong found value", temp, data1a);

        Iterator<StringifierParsingChoice<String>> iter1    = str1.parsingChoiceIterator( data1a, 0, data1a.length(), Integer.MAX_VALUE, true, null );
        StringifierParsingChoice<String> []        choices1 = ArrayHelper.copyIntoNewArray( iter1, StringifierParsingChoice.class );
        
        Assert.assertEquals("Wrong number of choices", choices1.length, 1);
        Assert.assertEquals("Wrong start index", choices1[0].getStartIndex(), 0);
        Assert.assertEquals("Wrong end index", choices1[0].getEndIndex(),   data1a.length());

        String data1b = "lagnraerg"; // something entirely different
        temp = str1.unformat( data1b, null );
        Assert.assertEquals("Wrong found value", temp, data1b);
        
        //
        
        String            data2a = "ak;wrbg;WRG";
        StringStringifier str2   = StringStringifier.create( );

        Iterator<StringifierParsingChoice<String>> iter2    = str2.parsingChoiceIterator( data2a, 0, data2a.length(), Integer.MAX_VALUE, false, null );
        StringifierParsingChoice<String> []        choices2 = ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, data2a.length());
        for( int i=0 ; i<choices2.length ; ++i ) {
            Assert.assertEquals("Wrong result at index " + i, choices2[i].unformat(), data2a.substring( 0, i ));
        }

        final int MAX = 4;
        iter2 = str2.parsingChoiceIterator( data2a, 0, data2a.length(), MAX, false, null );
        choices2 = ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, MAX);
        for( int i=0 ; i<choices2.length ; ++i ) {
            Assert.assertEquals("Wrong result at index " + i, choices2[i].unformat(), data2a.substring( 0, i ));
        }
    }

    private static final Log log = Log.getLogInstance( StringStringifierTest1.class ); // our own, private logger
}
