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

package org.infogrid.store;

/**
 * Thrown if decoding of a <code>StoreValue</code> is not possible.
 */
public class StoreValueDecodingException
        extends
            Exception
{
    /**
     * Constructor.
     *
     * @param message the error message
     */
    public StoreValueDecodingException(
            String message )
    {
        super( message );
    }
    
    /**
     * Constructor.
     *
     * @param cause the underlying cause
     */
    public StoreValueDecodingException(
            Throwable cause )
    {
        super( cause );
    }
    
    /**
     * Constructor.
     *
     * @param message the error message
     * @param cause the underlying cause
     */
    public StoreValueDecodingException(
            String    message,
            Throwable cause )
    {
        super( message, cause );
    }
}
