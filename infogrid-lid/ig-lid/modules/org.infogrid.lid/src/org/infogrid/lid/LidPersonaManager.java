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

package org.infogrid.lid;

import java.text.ParseException;
import java.util.Map;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.HasIdentifierFinder;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.http.SaneRequest;

/**
 * Manages identities.
 */
public interface LidPersonaManager
        extends
            HasIdentifierFinder
{
    /**
     * Obtain a LidPersona, given its Identifier.
     *
     * @param identifier the Identifier for which the LidPersona will be retrieved
     * @return the found LidPersona
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this Identifier
     * @throws InvalidIdentifierException thrown if an invalid Identifier was provided
     */
    public abstract LidPersona find(
            Identifier identifier )
        throws
            LidPersonaUnknownException,
            InvalidIdentifierException;

    /**
     * Find the LidPersona, or null.
     *
     * @param request the incoming request
     * @return the found LidPersona, or null
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this Identifier
     * @throws InvalidIdentifierException thrown if an invalid Identifier was provided
     * @throws ParseException if the request could not be parsed
     */
    public LidPersona findFromRequest(
            SaneRequest request )
        throws
            LidPersonaUnknownException,
            InvalidIdentifierException,
            ParseException;

    /**
     * Provision a LidPersona.
     *
     * @param localIdentifier the Identifier for the to-be-created LidPersona
     * @param attributes the attributes for the to-be-created LidPersona
     * @param credentials the credentials for the to-be-created LidPersona
     * @return the LidPersona that was created
     * @throws LidPersonaExistsAlreadyException thrown if a LidPersona with this Identifier exists already
     */
    public LidPersona provisionPersona(
            Identifier                    localIdentifier,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials )
        throws
            LidPersonaExistsAlreadyException;

    /**
     * Delete a LidPersona, given its Identifier.
     *
     * @param identifier the Identifier of the LidPersona that will be deleted
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this Identifier
     */
    public abstract void delete(
            Identifier identifier )
        throws
            LidPersonaUnknownException;
}
