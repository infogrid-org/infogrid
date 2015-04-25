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
 * CompoundStringifier tests. There are many "unchecked cast" exceptions, but somehow I can't figure it out better right now.
 */
public class MessageStringifierTest1
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

            runOne( current, true );
        }
    }
    
    private static final Log log = Log.getLogInstance( MessageStringifierTest1.class  ); // our own, private logger
    

    static Dataset [] datasets = {
            new StringDataset(
                    "One",
                    "Abc {0,int}",
                    new Object[] { 12 },
                    2,
                    "Abc 12" ),
            new StringDataset(
                    "Two",
                    "Abc {0,int} def{1,string}",
                    new Object[] { 12, "XXX-X" },
                    4,
                    "Abc 12 defXXX-X" ),
            new StringDataset(
                    "Three",
                    "Abc {0,int} def",
                    new Object[] { 987 },
                    3,
                    "Abc 987 def" ),
            new StringDataset(
                    "Four",
                    "Abc {0,int} def{2,string}  ghi kl{0,int}mno {1,int}",
                    new Object[] { 0, 111, "222" },
                    8,
                    "Abc 0 def222  ghi kl0mno 111" ),
    };
}
