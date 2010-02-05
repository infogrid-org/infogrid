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
import java.util.Set;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;

/**
 * Represents a persona, which could be provisioned either locally or remotely.
 */
public interface LidPersona
        extends
            HasIdentifier
{
    /**
     * Determine the set of remote Identifiers that are also associated with this LidPersona.
     * The Identifier inherited from HasIdentifier is considered the local Identifier.
     *
     * @return the set of remote Identifiers, if any
     */
    public Identifier [] getRemoteIdentifiers();

    /**
     * Convenience method to determine whether this LidPersona is identified by the
     * provided Identifier.
     *
     * @param identifier the Identifier to test
     * @return true if this LidPersona is identified by the provided Identifier
     */
    public boolean isIdentifiedBy(
            Identifier identifier );

    /**
     * Obtain an attribute of the persona.
     * 
     * @param key the name of the attribute
     * @return the value of the attribute, or null
     */
    public String getAttribute(
            String key );

    /**
     * Get the set of keys into the set of attributes.
     * 
     * @return the keys into the set of attributes
     */
    public Set<String> getAttributeKeys();
    
    /**
     * Obtain the map of attributes. This breaks encapsulation, but works much better
     * for JSP pages.
     * 
     * @return the map of attributes
     */
    public Map<String,String> getAttributes();

    /**
     * Obtain the set of available credential types.
     *
     * @return the set of available credential types
     */
    public LidCredentialType [] getCredentialTypes();

    /**
     * Obtain a specific credential.
     *
     * @param type the LidCredentialType for which the credential is to be obtained
     * @return the credential, or null
     */
    public String getCredentialFor(
            LidCredentialType type );

    /**
     * Determine the set of LidCredentialTypes known by this LidPersona given in this request.
     * The LidCredentialTypes will be returned regardless of whether the credentials are
     * valid or invalid.
     *
     * @param request the request
     * @return the recognized LidCredentialTypes
     */
    public LidCredentialType [] findRecognizedCredentialTypesIn(
            SaneRequest request );

    /**
     * Name of the attribute that contains the persona's identifier.
     */
    public static final String IDENTIFIER_ATTRIBUTE_NAME = "Identifier";

    /**
     * Name of the attribute that contains the persona's nickname.
     */
    public static final String NICKNAME_ATTRIBUTE_NAME = "Nickname";
}
