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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.shadow;

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
                        ShadowTest1.class,
                        AbstractTest.fileSystemFileName( ShadowTest1.class, "ShadowTest1.xml" )),

                new TestSpec(
                        ShadowTest2.class ),

//                new TestSpec(
//                        ShadowTest3.class,
//                        AbstractTest.fileSystemFileName( ShadowTest3.class, "ShadowTest3a.html" )), // ShadowTest3b.xml referenced from ShadowTest3a.html
// FIXME: need to move from file: to http: otherwise WebResource not set

                new TestSpec(
                        ShadowTest4.class ),

                new TestSpec(
                        ShadowTest5.class ),

                new TestSpec(
                        ShadowTest6.class ),

                new TestSpec(
                        ShadowTest7.class ),

                new TestSpec(
                        ShadowTest8.class ),

                new TestSpec(
                        ShadowTest9.class ),

                new TestSpec(
                        ShadowTest10.class ),

                new TestSpec(
                        ShadowTest11a.class ),

                new TestSpec(
                        ShadowTest11b.class ),

                new TestSpec(
                        ProbeUpdateCalculatorTest1.class ),

                new TestSpec(
                        ProbeMatchTest1.class ),

                new TestSpec(
                        RedirectTest1.class ),

                new TestSpec(
                        RedirectTest2.class ),
        };

        runTests( tests );
    }
}

