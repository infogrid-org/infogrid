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

package org.infogrid.probe.test.forwardreference;

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
                        ForwardReferenceTest1.class.getName() + " fast",
                        ForwardReferenceTest1.class,
                        AbstractTest.fileSystemFileName( ForwardReferenceTest1.class, "ForwardReferenceTest1_1.xml" ),
                                // ForwardReferenceRest1_2.xml included by reference from ForwardReferenceRest1.xml
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest1.class.getName() + " slow",
                        ForwardReferenceTest1.class,
                        AbstractTest.fileSystemFileName( ForwardReferenceTest1.class, "ForwardReferenceTest1_1.xml" ),
                                // ForwardReferenceRest1_2.xml included by reference from ForwardReferenceRest1.xml
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest2.class.getName() + " fast",
                        ForwardReferenceTest2.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest2.class.getName() + " slow",
                        ForwardReferenceTest2.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest3.class.getName() + " fast",
                        ForwardReferenceTest3.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest3.class.getName() + " slow",
                        ForwardReferenceTest3.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest4.class.getName() + " fast",
                        ForwardReferenceTest4.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest4.class.getName() + " slow",
                        ForwardReferenceTest4.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest5.class.getName() + " fast",
                        ForwardReferenceTest5.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest5.class.getName() + " slow",
                        ForwardReferenceTest5.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest6.class.getName() + " fast",
                        ForwardReferenceTest6.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest6.class.getName() + " slow",
                        ForwardReferenceTest6.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest7.class.getName() + " fast",
                        ForwardReferenceTest7.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest7.class.getName() + " slow",
                        ForwardReferenceTest7.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest8.class.getName() + " fast",
                        ForwardReferenceTest8.class,
                        "fast"),
                new TestSpec(
                        ForwardReferenceTest8.class.getName() + " slow",
                        ForwardReferenceTest8.class,
                        "slow"),

                new TestSpec(
                        ForwardReferenceTest9.class.getName() + " fast",
                        ForwardReferenceTest9.class,
                        "fast"),
                new TestSpec( // works
                        ForwardReferenceTest9.class.getName() + " slow",
                        ForwardReferenceTest9.class,
                        "slow"),
        };

        runTests( tests );
    }
}

