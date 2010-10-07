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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.store.sql;

import java.sql.SQLException;
import org.infogrid.util.DelegatingIOException;
import org.infogrid.util.text.HasStringRepresentation;

/**
 * <p>An <code>IOException</code> that delegates to a <code>SQLException</code>.</p>
 */
public class SqlStoreIOException
        extends
            DelegatingIOException
        implements
            HasStringRepresentation
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param operation name of the operation, such as "put"
     * @param cause the cause
     */
    public SqlStoreIOException(
            String       operation,
            SQLException cause )
    {
        this( operation, null, null, null, cause );
    }
    
    /**
     * Constructor.
     *
     * @param operation name of the operation, such as "put"
     * @param key key of the data element involved in the operation, if any
     * @param cause the cause
     */
    public SqlStoreIOException(
            String       operation,
            String       key,
            SQLException cause )
    {
        this( operation, key, null, null, cause );
    }

    /**
     * Constructor.
     *
     * @param operation name of the operation, such as "put"
     * @param key key of the data element involved in the operation, if any
     * @param encodingId the id of the encoding that was used to encode the data element, if any
     * @param data the data element, expressed as a sequence of bytes, if any
     * @param cause the cause
     */
    public SqlStoreIOException(
            String       operation,
            String       key,
            String       encodingId,
            byte []      data,
            SQLException cause )
    {
        super( "SQL Exception", cause );

        theOperation  = operation;
        theKey        = key;
        theEncodingId = encodingId;
        theData       = data;
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
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] {
                theOperation,
                theKey,
                theEncodingId,
                theData,
                getCause().getMessage(),
                getCause().getLocalizedMessage() };
    }

    /**
     * The name of the operation that produced this exception.
     */
    protected String theOperation;

    /**
     * The key of the data element used in the operation, if any.
     */
    protected String theKey;

    /**
     * The encoding ID of that data element used in the operation, if any.
     */
    protected String theEncodingId;

    /**
     * The data element, expressed as a sequence of bytes, if any.
     */
    protected byte [] theData;
}
