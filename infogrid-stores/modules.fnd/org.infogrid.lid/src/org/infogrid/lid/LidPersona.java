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

package org.infogrid.lid;

import java.util.Map;
import java.util.Set;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.util.http.SaneRequest;

/**
 * Represents a persona, which could be provisioned either locally or remotely.
 */
public interface LidPersona
        extends
            LidResource
{
    /**
     * Determine whether this LidPersona is hosted locally or remotely.
     * 
     * @return true if the LidPersona is hosted locally
     */
    public boolean isHostedLocally();

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
     * Perform a check of the validity of a presented credential.
     *
     * @param credType the LidCredentialType to check
     * @param request the incoming request carrying the presented credential
     * @throws LidInvalidCredentialException thrown if the credential was invalid
     */
    public void checkCredential(
            LidCredentialType credType,
            SaneRequest       request )
        throws
            LidInvalidCredentialException;

    /**
     * Obtain the credential types available.
     *
     * @return the credential types
     */
    public Set<LidCredentialType> getCredentialTypes();

    /**
     * Name of the attribute that contains the persona's identifier.
     */
    public static final String IDENTIFIER_ATTRIBUTE_NAME = "identifier";
}
