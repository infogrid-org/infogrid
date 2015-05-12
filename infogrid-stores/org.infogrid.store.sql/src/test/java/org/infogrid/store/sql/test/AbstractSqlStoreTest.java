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

package org.infogrid.store.sql.test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.infogrid.store.IterableStore;
import org.infogrid.store.encrypted.IterableEncryptedStore;
import org.infogrid.store.sql.AbstractSqlStore;
import org.infogrid.testharness.AbstractTest;
import org.junit.Test;

/**
 * Factors out common functionality of SqlStoreTests.
 */
public abstract class AbstractSqlStoreTest
        extends
            AbstractTest
{
    /**
     * Run the test encrypted.
     * 
     * @throws Exception all sorts of things can go wrong in a test
     */
    @Test
    public void runEncrypted()
        throws
            Exception
    {
        String    transformation = "DES";
        SecretKey key            = KeyGenerator.getInstance( transformation ).generateKey();
        
        theEncryptedStore = IterableEncryptedStore.create( transformation, key, theSqlStore );
        theTestStore      = theEncryptedStore;
        
        run();
    }
    
    /**
     * Run the test.
     * 
     * @throws Exception all sorts of things can go wrong in a test
     */
    public abstract void run()
        throws
            Exception;

    /**
     * The AbstractSqlStore to be tested.
     */
    protected AbstractSqlStore theSqlStore;
    
    /**
     * The actual Store to be tested. This may or may not be pointed to theSqlStore
     * by subclasses.
     */
    protected IterableStore theTestStore;

    /**
     * Encrypted Store, if the encrypted test is being run.
     */
    protected IterableStore theEncryptedStore;

    /**
     * The name of the database that we use to store test data.
     */
    public static final String test_DATABASE_NAME = "test";

    /**
     * The name of the table that we use to store test data.
     */
    public static final String test_TABLE_NAME = "SqlStoreTest";
    /**
     * The EncodingId for the tests.
     */
    public static final String ENCODING_ID = "TestEncodingId";
}
