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
 * Same as MessageStringifierTest3 for long instead of int.
 */
public class MessageStringifierTest4
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

    private static final Log log = Log.getLogInstance( MessageStringifierTest4.class  ); // our own, private logger

    static Dataset [] datasets = {
            new StringDataset(
                    "One",
                    "#{0,longhex4}",
                    new Object[] { 0xfedcL },
                    2,
                    "#fedc" ),
            new StringDataset(
                    "Two",
                    "#{0,longhex4}{1,longhex4}",
                    new Object[] { 0xabcdL, 0xcdefL },
                    3,
                    "#abcdcdef" ),
            new StringDataset(
                    "Three",
                    "#{0,longhex4}{1,longhex4}{2,longhex4}",
                    new Object[] { 0x1234L, 0x5678L, 0x9abcL },
                    4,
                    "#123456789abc" ),
    };
}
