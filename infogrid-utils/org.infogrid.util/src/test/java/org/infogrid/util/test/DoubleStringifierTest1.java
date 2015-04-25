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
import org.infogrid.util.text.DoubleStringifier;
import org.infogrid.util.text.StringifierParsingChoice;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests DoubleStringifier.
 */
public class DoubleStringifierTest1
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

        log.info( "DoubleStringifier" );
        
        double            data1a = 15.243;
        DoubleStringifier str1   = DoubleStringifier.create();
        String            res1a  = str1.format( null, data1a, null );
        
        Assert.assertEquals("not the same",String.valueOf( data1a ), res1a);

        Double temp = str1.unformat( res1a, null );
        Assert.assertEquals("Wrong found value", temp.doubleValue(), data1a, 0.000001 );

        Iterator<StringifierParsingChoice<Double>> iter1    = str1.parsingChoiceIterator( res1a, 0, res1a.length(), Integer.MAX_VALUE, true, null );
        StringifierParsingChoice<Double> []        choices1 = (StringifierParsingChoice<Double> []) ArrayHelper.copyIntoNewArray( iter1, StringifierParsingChoice.class );
        
        Assert.assertEquals("Wrong number of choices", choices1.length, 1);
        Assert.assertEquals("Wrong start index", choices1[0].getStartIndex(), 0);
        Assert.assertEquals("Wrong end index", choices1[0].getEndIndex(),   res1a.length());

        String res1b = "1234567.1"; // something entirely different
        temp = str1.unformat( res1b, null );
        Assert.assertEquals("Wrong found value", temp.toString(), res1b);
        
        //
        
        double            data2a = -2347561.123;
        DoubleStringifier str2   = DoubleStringifier.create( );
        String            res2a  = String.valueOf( data2a );

        Iterator<StringifierParsingChoice<Double>> iter2    = str2.parsingChoiceIterator( res2a, 0, res2a.length(), Integer.MAX_VALUE, false, null );
        StringifierParsingChoice<Double> []        choices2 = (StringifierParsingChoice<Double> []) ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, res2a.length()-2); 
        for( int i=0 ; i<choices2.length ; ++i ) {
            
            log.debug( "Found " + i + ": " + choices2[i] );

            Assert.assertEquals("Wrong start index", choices2[i].getStartIndex(), 0);
            Assert.assertEquals("Wrong end index", choices2[i].getEndIndex(),   i+2);

            Assert.assertTrue("Wrong result type at index " + i, choices2[i].unformat() instanceof Double );
            Assert.assertEquals("Wrong result value at index " + i, choices2[i].unformat().toString(), appendPointZero( res2a.substring( 0, i+2 )));
        }

        final int MAX = 4;
        iter2 = str2.parsingChoiceIterator( res2a, 0, res2a.length(), MAX, false, null );
        choices2 = (StringifierParsingChoice<Double> []) ArrayHelper.copyIntoNewArray( iter2, StringifierParsingChoice.class );
        Assert.assertEquals("Wrong number of choices", choices2.length, MAX);
        for( int i=0 ; i<choices2.length ; ++i ) {
            
            log.debug( "Found " + i + ": " + choices2[i] );
            
            Assert.assertEquals("Wrong start index", choices2[i].getStartIndex(), 0);
            Assert.assertEquals("Wrong end index", choices2[i].getEndIndex(),   i+2);

            Assert.assertTrue("Wrong result type at index " + i, choices2[i].unformat() instanceof Double );
            Assert.assertEquals("Wrong result at index " + i, choices2[i].unformat().toString(), appendPointZero( res2a.substring( 0, i+2 )));
        }
    }

    /**
     * Append a ".0" to a Stringified number, if necessary.
     *
     * @param in the String to potentially append to
     * @return the appended String
     */
    String appendPointZero(
            String in )
    {
        if( in.endsWith( "." )) {
            return in + "0";
        } else if( in.indexOf( '.' ) >= 0 ) {
            return in;
        } else {
            return in + ".0";
        }
    }

    private static final Log log = Log.getLogInstance( DoubleStringifierTest1.class ); // our own, private logger
}
