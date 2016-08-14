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

package org.infogrid.store.sql.postgresql.test;

import org.infogrid.store.sql.postgresql.PostgresqlStore;
import org.infogrid.store.sql.test.AbstractSqlStoreTest;
import org.infogrid.store.test.AbstractStoreIteratorTest4;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

/**
 *
 */
public class PostgresqlStoreIteratorTest4
    extends
        AbstractStoreIteratorTest4
{
    @Before
    public void setup()
    {
        PGSimpleDataSource theDataSource = new PGSimpleDataSource();
        theDataSource.setDatabaseName( AbstractSqlStoreTest.test_DATABASE_NAME );
        theDataSource.setUser( "test" );
        theDataSource.setPassword( "" );

        theTestStore = PostgresqlStore.create( theDataSource, AbstractSqlStoreTest.test_TABLE_NAME );
    }
}
