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
public class IntegerStringifierTest1
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

        log.info( "IntegerStringifier" );
        
        int                data1a = 15243;
        IntegerStringifier str1   = IntegerStringifier.create( );
        String             res1a  = str1.format( null, data1a, null );
        
        Assert.assertEquals("not the same",String.valueOf( data1a ), res1a);

        Integer temp = str1.unformat( res1a, null );
        Assert.assertEquals("Wrong found value", temp.intValue(), data1a);

        Iterator<StringifierParsingChoice<Integer>> iter1    = str1.parsingChoiceIterator( res1a, 0, res1a.length(), Integer.MAX_VALUE, true, null );
        StringifierParsingChoice<Integer> []        choices1 = (StringifierParsingChoice<Integer> []) ArrayHelper.copyIntoNewArray( iter1, StringifierParsingChoice.class );
        
        Assert.assertEquals("Wrong number of choices", choices1.length, 1);
        Assert.assertEquals("Wrong start index", choices1[0].getStartIndex(), 0);
        Assert.assertEquals("Wrong end index", choices1[0].getEndIndex(),   res1a.length());

        String res1b = "123"; // something entirely different
        temp = str1.unformat( res1b, null );
        Assert.assertEquals("Wrong found value", temp.toString(), res1b);
    }

    private static final Log log = Log.getLogInstance( IntegerStringifierTest1.class ); // our own, private logger
}
