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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.kernel.TEST.differencer;

import org.infogrid.testharness.AbstractTestGroup;

/**
 * Tests the IterableMeshBaseDifferencer.
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
        String [] subArgs = new String[] {};
        
        TestSpec [] tests = {
                new TestSpec( DifferencerTest1.class, subArgs ),
                new TestSpec( DifferencerTest2.class, subArgs ),
                new TestSpec( DifferencerTest3.class, subArgs ),
                new TestSpec( DifferencerTest4.class, subArgs ),
                new TestSpec( DifferencerTest5.class, subArgs ),
                new TestSpec( DifferencerTest6.class, subArgs ),
                new TestSpec( DifferencerTest7.class, subArgs ),
                new TestSpec( DifferencerTest8.class, subArgs ),
                new TestSpec( DifferencerTest9.class, subArgs ),
        };

        runTests( tests );
    }
}

