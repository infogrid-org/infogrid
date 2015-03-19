package org.infogrid.util.sql;

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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Factors out Database access, so it can be reused by code not going through the
 * SqlStore abstraction.
 */
public class SqlDatabase
{
    /**
     * Factory method.
     * 
     * @param name the name of the SqlDatabase, for debugging purposes
     * @param ds the DataSource to the database
     * @return the created SqlDatabase
     */
    public static SqlDatabase create(
            String     name,
            DataSource ds )
    {
        return new SqlDatabase( name, ds, null );
    }
    
    /**
     * Factory method.
     * 
     * @param name the name of the SqlDatabase, for debugging purposes
     * @param ds the DataSource to the database
     * @param autoCommit set auto-commit on the Connection, don't set it, or leave default
     * @return the created SqlDatabase
     */
    public static SqlDatabase create(
            String     name,
            DataSource ds,
            Boolean    autoCommit )
    {
        return new SqlDatabase( name, ds, autoCommit );
    }
    
    /**
     * Private constructor, use factory method.
     * 
     * @param ds the DataSource to the database
     * @param autoCommit set auto-commit on the Connection, don't set it, or leave default
     */
    protected SqlDatabase(
            String     name,
            DataSource ds,
            Boolean    autoCommit )
    {
        theName       = name;
        theDataSource = ds;
        theAutoCommit = autoCommit;
    }
    
    /**
     * Get the name of this SqlDatabase, for debugging purposes.
     * 
     * @return the name
     */
    public String getName()
    {
        return theName;
    }

    /**
     * Determine whether this Database uses auto-commit.
     *
     * @return true if it uses auto-commit
     */
    public Boolean getAutoCommit()
    {
        return theAutoCommit;
    }

    /**
     * Obtain a connection to the database. This is a smart factory method,
     * returning an already-existing one if there is one instead of creating
     * a new one.
     *
     * @return the Connection
     * @throws SQLException thrown if the database could not be contacted
     */
    public synchronized Connection obtainConnection()
        throws
            SQLException
    {
        if( theConnection == null ) {
            theConnection = theDataSource.getConnection();

            if( theConnection != null && theAutoCommit != null ) {
                theConnection.setAutoCommit( theAutoCommit.booleanValue() );
            }
        }
        return theConnection;
    }

    /**
     * Obtain a new connection to the database. This discards whatever old
     * Connection there may be already.
     *
     * @return the Connection
     * @throws SQLException thrown if the database could not be contacted
     */
    public synchronized Connection obtainNewConnection()
        throws
            SQLException
    {
        if( theConnection != null ) {
            try {
                theConnection.close();
            } catch( Throwable t ) {
                // might be closed already -- ignore
            }
        }
        theConnection = theDataSource.getConnection();

        if( theConnection != null && theAutoCommit != null ) {
            theConnection.setAutoCommit( theAutoCommit.booleanValue() );
        }
        return theConnection;
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection()
    {
        if( theConnection != null ) {
            try {
                theConnection.close();
            } catch( SQLException ex2 ) {
                // noop
            }
            theConnection = null;
        }        
    }
    
    /**
     * Name of this instance, for debugging purposes.
     */
    protected String theName;
    
    /**
     * The JDBC data source.
     */
    protected DataSource theDataSource;
    
    /**
     * The most-recently used Connection, if any. This is private, so we are forced to
     * go through factory methods.
     */
    private Connection theConnection;

    /**
     * The value for the autoCommit property on Connections. If this is null, we do not set it.
     */
    protected Boolean theAutoCommit;
}