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

package org.infogrid.store.sql.TEST;

import org.infogrid.testharness.AbstractTestGroup;

/**
 * Tests the SQL implementation of Store.
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
        String [] subArgs          = new String[] {};
        String [] keyStoreSubArgs  = new String[] {
            "test-keystore.key",
            "asdfgh"
        };
        String [] encryptedSubArgs = new String[] { "DES" };
        
        TestSpec [] tests = {
                new TestSpec( SqlStoreTest1.class, subArgs ),
                new TestSpec( SqlStoreTest2.class, subArgs ),
                new TestSpec( SqlStoreTest3.class, subArgs ),
                new TestSpec( SqlStoreTest4.class, subArgs ),
                new TestSpec( SqlStoreTest5.class, subArgs ),

                new TestSpec( SqlKeyStoreTest1.class, keyStoreSubArgs ),
                new TestSpec( SqlStorePerformanceTest1.class, subArgs ),

                new TestSpec( EncryptedSqlStoreTest1.class, encryptedSubArgs ),
                new TestSpec( EncryptedSqlStoreTest2.class, encryptedSubArgs ),
                new TestSpec( EncryptedSqlStoreTest3.class, encryptedSubArgs ),

                new TestSpec( EncryptedSqlStorePerformanceTest1.class, encryptedSubArgs ),
                new TestSpec( SqlStoreTest6.class, subArgs )
        };

        runTests( tests );
    }
}