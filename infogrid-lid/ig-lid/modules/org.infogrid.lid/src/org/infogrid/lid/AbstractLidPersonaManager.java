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
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.SimpleStringIdentifierFactory;
import org.infogrid.util.http.SaneRequest;

/**
 * Factors out common functionality of LidPersonaManagers.
 */
public abstract class AbstractLidPersonaManager
        implements
            LidPersonaManager
{
    /**
     * Constructor for subclasses only.
     */
    protected AbstractLidPersonaManager()
    {
        theIdentifierFactory = new SimpleStringIdentifierFactory();
    }

    /**
     * Constructor for subclasses only.
     *
     * @param identifierFactory the IdentifierFactory to use
     */
    protected AbstractLidPersonaManager(
            IdentifierFactory identifierFactory )
    {
        theIdentifierFactory = identifierFactory;
    }

    /**
     * Find the resource requested by this incoming request.
     *
     * @param request the incoming request
     * @return the found resource
     * @throws ParseException thrown if the identifier in the request could not be parsed
     */
    public HasIdentifier findFromRequest(
            SaneRequest request )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException,
            ParseException
    {
        String        identifier = request.getAbsoluteBaseUri();
        Identifier    realId     = theIdentifierFactory.fromExternalForm( identifier );
        HasIdentifier ret        = find( realId );
        return ret;
    }

    /**
     * Provision a LidPersona.
     *
     * @param localIdentifier the Identifier for the to-be-created LidPersona. This may be null, in which case
     *        the LidPersonaManager assigns a localIdentifier
     * @param remotePersonas the remote personas to be associated with the locally provisioned LidPersona
     * @param attributes the attributes for the to-be-created LidPersona
     * @param credentials the credentials for the to-be-created LidPersona
     * @return the LidPersona that was created
     * @throws LidPersonaExistsAlreadyException thrown if a LidPersona with this Identifier exists already
     */
    public LidPersona provisionPersona(
            Identifier                    localIdentifier,
            HasIdentifier []              remotePersonas,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials )
        throws
            LidPersonaExistsAlreadyException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete a LidPersona. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidPersona that will be deleted
     * @throws UnsupportedOperationException thrown if this LidPersonaManager does not permit the deletion of LidPersonas
     */
    public void delete(
            LidPersona toDelete )
        throws
            UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Factory for Identifiers.
     */
    protected IdentifierFactory theIdentifierFactory;
}
