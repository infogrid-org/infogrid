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
import org.infogrid.store.sql.test.SqlStoreTest2;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

/**
 *
 */
public class PostgresqlStoreTest2
    extends
        SqlStoreTest2
{
    @Before
    public void setup()
    {
        PGSimpleDataSource theDataSource = new PGSimpleDataSource();
        theDataSource.setDatabaseName( test_DATABASE_NAME );
        theDataSource.setUser( "test" );
        theDataSource.setPassword( "" );

        theSqlStore  = PostgresqlStore.create( theDataSource, test_TABLE_NAME );
        theTestStore = theSqlStore;
    }
}
