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

package org.infogrid.store.test;

import java.io.IOException;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.BeforeClass;

/**
 * Factors out common functionality for tests in this package..
 */
public abstract class AbstractStoreTest
        extends
            AbstractTest
{
    /**
     * Initialize logging.
     */
    @BeforeClass
    public static final void beforeClass()
        throws
            IOException
    {
        Log4jLog.configure( "org/infogrid/store/test/Log.properties", AbstractStoreTest.class.getClassLoader() );
        Log.setLogFactory( new Log4jLogFactory());
    }

    /**
     * The EncodingId for the tests.
     */
    public static final String ENCODING_ID = "TestEncodingId";
}
