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

package org.infogrid.probe.store.TEST;

import org.infogrid.context.Context;
import org.infogrid.context.SimpleContext;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.store.sql.SqlStore;
import org.infogrid.store.sql.SqlStoreIOException;
import org.infogrid.testharness.AbstractTest;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Factors out common functionality of StoreProbeTests.
 */
public abstract class AbstractStoreProbeTest
        extends
            AbstractTest
{
    /**
     * Constructor.
     * 
     * @param testClass identifies the actual test to be run
     */
    public AbstractStoreProbeTest(
            Class testClass )
    {
        super( localFile( testClass, "/ResourceHelper" ),
               localFile( testClass, "/Log.properties" ));

        theDataSource = new MysqlDataSource();
        theDataSource.setDatabaseName( TEST_DATABASE_NAME );
        
        theSqlStore = SqlStore.create( theDataSource, TEST_TABLE_NAME );

        try {
            theSqlStore.deleteStore();
        } catch( SqlStoreIOException ex ) {
            // ignore this one
        }
        try {
            theSqlStore.initialize();
        } catch( SqlStoreIOException ex ) {
            // ignore this one
        }
    }

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();

    /**
     * The Database connection.
     */
    protected MysqlDataSource theDataSource;

    /**
     * The SqlStore to be tested.
     */
    protected SqlStore theSqlStore;

    /**
     * The ProbeDirectory.
     */
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

    /**
     * The name of the database that we use to store test data.
     */
    public static final String TEST_DATABASE_NAME = "test";

    /**
     * The name of the table that we use to store test data.
     */
    public static final String TEST_TABLE_NAME = "StoreProbeTest";

    /**
     * The SQL driver.
     */
    static Object theSqlDriver;
    static {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            theSqlDriver = Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }        
    }
}
