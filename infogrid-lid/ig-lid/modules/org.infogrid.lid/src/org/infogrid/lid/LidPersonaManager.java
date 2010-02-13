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

import java.util.Map;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.HasIdentifierFinder;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;

/**
 * Manages locally provisioned accounts.
 */
public interface LidPersonaManager
        extends
            HasIdentifierFinder,
            LidResourceFinder
{
    /**
     * Obtain a HasIdentifier, given its Identifier. This will either return a LidPersona
     * or not. If it returns a LidPersona, the identifier referred to that locally provisioned
     * LidPersona. If it returns something other than a LidPersona, it refers to a remote
     * persona. To determine the LidPersona that may be associated with the remote persona,
     * call determineLidPersonaFromRemoteIdentifier.
     *
     * @param identifier the Identifier for which the HasIdentifier will be retrieved
     * @return the found HasIdentifier
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     */
    public abstract HasIdentifier find(
            Identifier identifier )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException;

    /**
     * Given a remote persona, determine the locally provisioned corresponding
     * LidPersona. May return null if none has been provisioned.
     * 
     * @param remote the remote persona
     * @return the found LidPersona, or null
     */
    public abstract LidPersona determineLidPersonaFromRemotePersona(
            HasIdentifier remote );

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
            LidPersonaExistsAlreadyException;

    /**
     * Delete a LidPersona. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidPersona that will be deleted
     * @throws UnsupportedOperationException thrown if this LidPersonaManager does not permit the deletion of LidPersonas
     */
    public void delete(
            LidPersona toDelete );
}
