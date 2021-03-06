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

import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * CompoundStringifier tests for stack traces. There are many "unchecked cast" exceptions, but somehow I can't figure it out better right now.
 */
public class MessageStringifierTest2
        extends
            AbstractMessageStringifierTest
{
    @Test
    @SuppressWarnings(value={"unchecked"})
    public void run()
        throws
            Exception
    {
        for( int i=0 ; i<datasets.length ; ++i ) {
            Dataset current = datasets[i];

            runOne( current, false );
        }
    }

    private static void f1()
    {
        f2();
    }
    private static void f2()
    {
        f3();
    }
    private static void f3()
    {
        f4();
    }
    private static void f4()
    {
        throw new RuntimeException( "XXX" ) {
            private static final long serialVersionUID = 1L;
            @Override
            public String getLocalizedMessage()
            {
                return "YYY";
            }
        }; // subclass to test the embedded $
    }
    
    private static final Log log = Log.getLogInstance( MessageStringifierTest2.class  ); // our own, private logger
    private static final Throwable t1;
    static {
        Throwable caught = null;
        try {
            f1();
        } catch( Throwable ex ) {
            caught = ex;
        }
        t1 = caught;
    }
    
    static Dataset [] datasets = {
            new RegexDataset(
                    "One",
                    "Abc {0,string} def {1,string} ghi {2,stacktrace} jkl",
                    new Object[] { t1.getMessage(), t1.getLocalizedMessage(), t1 },
                    7,
                    "Abc XXX def YYY ghi " + MessageStringifierTest2.class.getName().replaceAll(  "\\.", "\\\\." ) + "\\.f4\\(\\w+\\.java:\\d*\\)(\n[\\w\\.\\$<>]+\\(((\\w+\\.java:\\d+)|(Native Method))\\)){4,} jkl" ),
                    // "Abc XXX def YYY ghi " + MessageStringifierTest2.class.getName().replaceAll(  "\\.", "\\\\." ) + "\\.f4\\(.*\\) jkl" ),
    };
}
