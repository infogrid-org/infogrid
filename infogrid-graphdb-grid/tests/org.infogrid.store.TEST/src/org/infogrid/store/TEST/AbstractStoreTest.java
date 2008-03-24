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

package org.infogrid.store.TEST;

import org.infogrid.testharness.AbstractTest;

/**
 * Factors out common functionality for StoreTests.
 */
public abstract class AbstractStoreTest
        extends
            AbstractTest
{
    /**
     * Constructor.
     */
    public AbstractStoreTest(
            Class testClass )
    {
        super( localFile( testClass, "/ResourceHelper" ),
               localFile( testClass, "/Log.properties" ));
    }

    /**
     * The EncodingId for the tests.
     */
    public static final String ENCODING_ID = "TestEncodingId";
}
