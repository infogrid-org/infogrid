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
 * CompoundStringifier tests for fixed-length hex's.
 */
public class MessageStringifierTest3
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

    private static final Log log = Log.getLogInstance( MessageStringifierTest3.class  ); // our own, private logger

    static Dataset [] datasets = {
            new StringDataset(
                    "One",
                    "#{0,hex2}",
                    new Object[] { 0xfe },
                    2,
                    "#fe" ),
            new StringDataset(
                    "Two",
                    "#{0,hex2}{1,hex2}",
                    new Object[] { 0xab, 0xcd },
                    3,
                    "#abcd" ),
            new StringDataset(
                    "Three",
                    "#{0,hex2}{1,hex2}{2,hex2}",
                    new Object[] { 0x12, 0x34, 0x56 },
                    4,
                    "#123456" ),
    };
}
