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

package org.infogrid.util.TEST;

import org.infogrid.testharness.AbstractTestGroup;
import org.infogrid.testharness.AbstractTestGroup.TestSpec;

/**
 * Tests the Util package.
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
                new TestSpec( ArrayHelperTest1.class, subArgs ),

                new TestSpec( DereferencingIteratorTest1.class, subArgs ),
                new TestSpec( ArrayCursorIteratorTest1.class, subArgs ),
                new TestSpec( FileTreeFacadeCursorIteratorTest1.class, subArgs ),
                new TestSpec( FilteringCursorIteratorTest1.class, subArgs ),

                new TestSpec( ReturnSynchronizerTest1.class, subArgs ),

                new TestSpec( SmartFactoryTest1.class, subArgs ),
                new TestSpec( SmartFactoryTest2.class, subArgs ),
                new TestSpec( SmartFactoryTest3.class, subArgs ),
                new TestSpec( SmartFactoryTest4.class, subArgs ),

                new TestSpec( StringStringifierTest1.class, subArgs ),
                new TestSpec( IntegerStringifierTest1.class, subArgs ),
                new TestSpec( LongStringifierTest1.class, subArgs ),
                new TestSpec( DoubleStringifierTest1.class, subArgs ),
                new TestSpec( CompoundStringifierTest1.class, subArgs ),
                new TestSpec( ArrayStringifierTest1.class, subArgs ),
        };

        runTests( tests );
    }
}

