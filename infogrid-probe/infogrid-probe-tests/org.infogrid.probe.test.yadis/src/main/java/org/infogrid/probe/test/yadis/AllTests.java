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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.yadis;

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

                // 2000 is less than half the default timeout time of org.infogrid.meshbase.net.proxy.AbstractProxyPolicy!DefaultRpcWaitDuration,
                // otherwise the HTTP request of discovery times out
                new TestSpec(
                        YadisTest1.class.getName() + " (delay 0)",
                        YadisTest1.class, "0" ),
                new TestSpec(
                        YadisTest1.class.getName() + " (delay 2000)",
                        YadisTest1.class, "2000" ),

                new TestSpec(
                        YadisTest2.class.getName() + " (delay 0)",
                        YadisTest2.class, "0" ),
                new TestSpec(
                        YadisTest2.class.getName() + " (delay 2000)",
                        YadisTest2.class, "2000" ),

                new TestSpec(
                        YadisTest3.class.getName() + " (delay 0)",
                        YadisTest3.class, "0" ),
                new TestSpec(
                        YadisTest3.class.getName() + " (delay 2000)",
                        YadisTest3.class, "2000" ),

                new TestSpec(
                        YadisTest4.class.getName() + " (delay 0)",
                        YadisTest4.class, "0" ),
                new TestSpec(
                        YadisTest4.class.getName() + " (delay 2000)",
                        YadisTest4.class, "2000" ),

                new TestSpec(
                        YadisTest5.class.getName() + " (delay 0)",
                        YadisTest5.class,
                        AbstractTest.fileSystemFileName( YadisTest5.class, "YadisTest5.xml" ),
                        "0" ),
                new TestSpec(
                        YadisTest5.class.getName() + " (delay 2000)",
                        YadisTest5.class,
                        AbstractTest.fileSystemFileName( YadisTest5.class, "YadisTest5.xml" ),
                        "2000"),

                new TestSpec(
                        YadisTest6.class.getName() + " (delay 0)",
                        YadisTest6.class,
                        AbstractTest.fileSystemFileName( YadisTest6.class, "YadisTest6.xml" ),
                        "0" ),

                new TestSpec(
                        YadisTest6.class.getName() + " (delay 2000)",
                        YadisTest6.class,
                        AbstractTest.fileSystemFileName( YadisTest6.class, "YadisTest6.xml" ),
                        "2000" ),

                new TestSpec(
                        YadisTest7.class.getName() + " (delay 0)",
                        YadisTest7.class,
                        AbstractTest.fileSystemFileName( YadisTest7.class, "YadisTest7.xml" ),
                        "0" ),

                new TestSpec(
                        YadisTest7.class.getName() + " (delay 2000)",
                        YadisTest7.class,
                        AbstractTest.fileSystemFileName( YadisTest7.class, "YadisTest7.xml" ),
                        "2000" ),
                new TestSpec(
                        YadisTest8.class,
                        "0" )
        };

        runTests( tests );
    }
}

