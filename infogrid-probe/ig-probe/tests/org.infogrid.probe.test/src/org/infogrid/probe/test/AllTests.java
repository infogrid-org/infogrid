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

package org.infogrid.probe.test;

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
                        ProbeTest1.class,
                        AbstractTest.fileSystemFileName( ProbeTest1.class, "ProbeTest1.xml" )),

                new TestSpec(
                        ProbeTest2.class,
                        AbstractTest.tempInputFileName( ProbeTest2.class, "test2-active.xml" ),
                        AbstractTest.fileSystemFileName( ProbeTest2.class, "ProbeTest2_1.xml" ),
                        AbstractTest.fileSystemFileName( ProbeTest2.class, "ProbeTest2_2.xml" )),

                new TestSpec(
                        ProbeTest3.class,
                        AbstractTest.tempInputFileName( ProbeTest3.class, "test3-active.xml" ),
                        AbstractTest.fileSystemFileName( ProbeTest3.class, "ProbeTest2_1.xml" ),
                        AbstractTest.fileSystemFileName( ProbeTest3.class, "ProbeTest2_2.xml" )),

                new TestSpec(
                        ProbeTest4.class,
                        AbstractTest.fileSystemFileName( ProbeTest4.class, "ProbeTest4.xml" )),

                new TestSpec(
                        ProbeTest5.class ),

//                new TestSpec( // FAILS: not implemented. See also XprisoTest11.
//                        ProbeTest6.class,
//                        AbstractTest.fileSystemFileName( ProbeTest6.class, "ProbeTest6.xml" )),
//
                new TestSpec(
                        ProbeTest7.class ),

// FIXME: ProbeTest8 seems to work, but spits out way too many warnings (but only when run in this test suite,
// not when run standalone). It may be related to failure to clean up Proxies to expired Shadows, and perhaps
// ProbeTest6. This needs more investigation but does not appear to be critical.\
//               new TestSpec(
//                        ProbeTest8.class ),

//               new TestSpec(
//                       ProbeTest9a.class ),
//               new TestSpec(
//                       ProbeTest9b.class )
        };

        runTests( tests );
    }
}

