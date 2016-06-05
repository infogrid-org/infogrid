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

package org.infogrid.lid.credential;

import org.infogrid.util.http.SaneRequest;

/**
 * Represents a a password credential type.
 */
public abstract class AbstractLidPasswordCredentialType
        extends
            AbstractLidCredentialType
        implements
            LidSimplePasswordCredentialType
{
    /**
     * Constructor, for subclasses only.
     */
    protected AbstractLidPasswordCredentialType()
    {
        // nothing
    }

    /**
     * Determine whether this LidCredentialType is contained in this request.
     *
     * @param request the request
     * @return true if this LidCredentialType is contained in this request
     */
    public boolean isContainedIn(
            SaneRequest request )
    {
        if( request.matchPostedArgument( LID_CREDTYPE_PARAMETER_NAME, LID_SIMPLE_PASSWORD_CREDTYPE_PARAMETER_VALUE )) {
            return true;
        }

        return false;
    }
}
