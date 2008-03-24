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

package org.infogrid.meshbase.store.TEST;

import org.infogrid.testharness.AbstractTestGroup;

/**
 * Tests the Store implementation of MeshBase.
 */
public abstract class AllTests
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
        String [] subArgs1 = new String[] { "123" };
        String [] emptyArgs  = new String[] {};

        TestSpec [] tests = {
                new TestSpec( StoreMeshBaseTest1.class, subArgs1 ),
                new TestSpec( StoreMeshBaseTest2.class, subArgs1 ),
                new TestSpec( StoreMeshBaseTest3.class, emptyArgs ),
                new TestSpec( StoreMeshBaseTest4.class, new String[] { "10000" } ),
                new TestSpec( StoreMeshBaseTest5.class, emptyArgs ),
                new TestSpec( StoreMeshBaseTest6.class, emptyArgs ),
                
                new TestSpec( StoreBulkLoaderTest1.class, emptyArgs ),

                new TestSpec( StoreSweeperTest1.class, emptyArgs ),
                new TestSpec( StoreSweeperTest2.class, emptyArgs ),
        };

        runTests( tests );
    }
}
