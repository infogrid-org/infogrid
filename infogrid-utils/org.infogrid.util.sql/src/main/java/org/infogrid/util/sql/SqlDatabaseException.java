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

package org.infogrid.util.sql;

import java.sql.SQLException;
import org.infogrid.util.DelegatingIOException;
import org.infogrid.util.text.HasStringRepresentation;

/**
 * <p>An <code>IOException</code> that delegates to a <code>SQLException</code>.</p>
 */
public class SqlDatabaseException
        extends
            DelegatingIOException
        implements
            HasStringRepresentation
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param db the SqlDatabase that threw the exception
     * @param operation name of the operation, such as "put"
     * @param cause the cause
     */
    public SqlDatabaseException(
            SqlDatabase  db,
            String       operation,
            SQLException cause )
    {
        super( "SQL Exception", cause );

        theDb        = db;
        theOperation = operation;
    }

    /**
     * Obtain the underlying cause which we know to be a SQLException.
     *
     * @return the cause
     */
    @Override
    public SQLException getCause()
    {
        return (SQLException) super.getCause();
    }

    /**
     * Obtain the SqlDatabase that threw the Exception.
     *
     * @return the SqlDatabase
     */
    public SqlDatabase getSqlDatabase()
    {
        return theDb;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] {
                theDb.getName(),
                theOperation,
                getCause().getMessage(),
                getCause().getLocalizedMessage() };
    }

    /**
     * The SqlDatabase that threw this exception.
     */
    protected SqlDatabase theDb;

    /**
     * The name of the operation that produced this exception.
     */
    protected String theOperation;
}
