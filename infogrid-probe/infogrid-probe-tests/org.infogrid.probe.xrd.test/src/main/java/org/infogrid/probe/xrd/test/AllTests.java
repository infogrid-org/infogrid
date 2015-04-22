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

package org.infogrid.probe.xrd.test;

import org.infogrid.testharness.AbstractTest;
import org.infogrid.testharness.AbstractTestGroup;
import org.infogrid.testharness.AbstractTestGroup.TestSpec;

/**
 * Runs all tests in this package.
 */
public class AllTests
        extends
            AbstractTestGroup
{
    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        TestSpec [] tests = {

                new TestSpec(
                        "XrdTest1 with XrdTest1a.xml",
                        XrdTest1.class,
                        AbstractTest.fileSystemFileName( XrdTest1.class, "XrdTest1a.xml" ), "6" ),
                new TestSpec(
                        "XrdTest1 with XrdTest1b.xml",
                        XrdTest1.class,
                        AbstractTest.fileSystemFileName( XrdTest1.class, "XrdTest1b.xml" ), "6" ),
                new TestSpec(
                        "XrdTest1 with XrdTest1c.xml",
                        XrdTest1.class,
                        AbstractTest.fileSystemFileName( XrdTest1.class, "XrdTest1c.xml" ), "14" ),
                new TestSpec(
                        "XrdTest1 with XrdTest1d.xml",
                        XrdTest1.class,
                        AbstractTest.fileSystemFileName( XrdTest1.class, "XrdTest1d.xml" ), "2" ),
        };

        runTests( tests );
    }
}


