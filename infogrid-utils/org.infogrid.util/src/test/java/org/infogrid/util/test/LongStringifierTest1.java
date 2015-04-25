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
import org.infogrid.util.text.LongStringifier;
import org.infogrid.util.text.StringifierParsingChoice;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests LongStringifier.
 */
public class LongStringifierTest1
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

        log.info( "LongStringifier" );
        
        long            data1a = 15243L;
        LongStringifier str1   = LongStringifier.create( );
        String          res1a  = str1.format( null, data1a, null );
        
        Assert.assertEquals("not the same",String.valueOf( data1a ), res1a);

        Long temp = str1.unformat( res1a, null );
        Assert.assertEquals("Wrong found value", temp.intValue(), data1a);

        Iterator<StringifierParsingChoice<Long>> iter1    = str1.parsingChoiceIterator( res1a, 0, res1a.length(), Integer.MAX_VALUE, true, null );
        StringifierParsingChoice<Long> []        choices1 = (StringifierParsingChoice<Long> []) ArrayHelper.copyIntoNewArray( iter1, StringifierParsingChoice.class );
        
        Assert.assertEquals("Wrong number of choices", choices1.length, 1);
        Assert.assertEquals("Wrong start index", choices1[0].getStartIndex(), 0);
        Assert.assertEquals("Wrong end index", choices1[0].getEndIndex(),   res1a.length());

        String res1b = "123"; // something entirely different
        temp = str1.unformat( res1b, null );
        Assert.assertEquals("Wrong found value", temp.toString(), res1b);
        
        //
        
        int             data2a = -2347561;
        LongStringifier str2   = LongStringifier.create( );
        String          res2a  = String.valueOf( data2a );

        Iterator<StringifierParsingChoice<Long>> iter2    = str2.parsingChoiceIterator( res2a, 0, res2a.length(), Integer.MAX_VALUE, false, null );
        StringifierParsingChoice<Long> []        choices2 = (StringifierParsingChoice<Long> []) ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, res2a.length()-1); 
        for( int i=0 ; i<choices2.length ; ++i ) {
            
            log.debug( "Found " + i + ": " + choices2[i] );

            Assert.assertEquals("Wrong start index", choices2[i].getStartIndex(), 0);
            Assert.assertEquals("Wrong end index", choices2[i].getEndIndex(),   i+2);

            Assert.assertTrue("Wrong result type at index " + i, choices2[i].unformat() instanceof Long );
            Assert.assertEquals("Wrong result value at index " + i, choices2[i].unformat().toString(), res2a.substring( 0, i+2 ));
        }

        final int MAX = 4;
        iter2 = str2.parsingChoiceIterator( res2a, 0, res2a.length(), MAX, false, null );
        choices2 = ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, MAX);
        for( int i=0 ; i<choices2.length ; ++i ) {
            
            log.debug( "Found " + i + ": " + choices2[i] );
            
            Assert.assertEquals("Wrong start index", choices2[i].getStartIndex(), 0);
            Assert.assertEquals("Wrong end index", choices2[i].getEndIndex(),   i+2);

            Assert.assertTrue("Wrong result type at index " + i, choices2[i].unformat() instanceof Long );
            Assert.assertEquals("Wrong result at index " + i, choices2[i].unformat().toString(), res2a.substring( 0, i+2 ));
        }
    }

    private static final Log log = Log.getLogInstance( IntegerStringifierTest1.class ); // our own, private logger
}
