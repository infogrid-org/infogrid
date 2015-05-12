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

package org.infogrid.store.sql.mysql.test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.infogrid.store.sql.mysql.MysqlStore;
import org.infogrid.store.sql.test.SqlStoreTest3;
import org.junit.Before;

/**
 *
 */
public class MysqlStoreTest3
    extends
        SqlStoreTest3
{
    @Before
    public void setup()
    {
        MysqlDataSource theDataSource = new MysqlDataSource();
        theDataSource.setDatabaseName( test_DATABASE_NAME );

        theSqlStore  = MysqlStore.create( theDataSource, test_TABLE_NAME );
        theTestStore = theSqlStore;
    }
}
