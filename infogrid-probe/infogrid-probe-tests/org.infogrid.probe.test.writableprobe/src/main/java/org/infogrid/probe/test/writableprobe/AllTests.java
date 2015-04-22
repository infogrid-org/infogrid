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

package org.infogrid.probe.test.writableprobe;

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
                new TestSpec( // property updates
                            WritableProbeTest1.class ),

                new TestSpec( // blessing
                            WritableProbeTest2.class ),

                new TestSpec( // unblessing
                            WritableProbeTest3.class ),

                new TestSpec( // deletion
                            WritableProbeTest4.class ),


// FIXME: The remaining WritableProbeTests have not been written yet.
//                new TestSpec( // relationship delete
//                        WritableProbeTest5.class,
//                        noArgs ),
//                new TestSpec( // bless relationship
//                        WritableProbeTest6.class,
//                        noArgs ),
//                new TestSpec( // unbless relationship
//                        WritableProbeTest7.class,
//                        noArgs ),
//                new TestSpec( // create relationship between objects instantiated by probe but unrelated
//                        WritableProbeTest8.class,
//                        noArgs ),
//                new TestSpec( // create object -- BROKEN: need new API call to "createAndPush" to avoid triggering the non-local NetMeshObjectIdentifier exception
//                        WritableProbeTest9.class,
//                        noArgs ),
//                new TestSpec( // create and relate object
//                        WritableProbeTest10.class,
//                        noArgs ),
        };

        runTests( tests );
    }
}

