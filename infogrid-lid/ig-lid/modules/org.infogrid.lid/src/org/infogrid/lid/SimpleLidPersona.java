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
import org.infogrid.util.Identifier;

/**
 * Simple implementation of LidPersona.
 */
public class SimpleLidPersona
        extends
            AbstractLidPersona
{
    /**
     * Factory method.
     *
     * @param identifier the unique identifier of the persona, e.g. their identity URL
     * @param remoteIdentifiers set of remote Identifiers that are also associated with this LidPersona
     * @param attributes attributes of the persona, e.g. first name
     * @param credentialTypes the types of credentials available to locally authenticate this LidPersona
     * @param credentials the values for the credentials available to locally authenticate this LidPersona
     * @return the created SimpleLidPersona
     */
    public static SimpleLidPersona create(
            Identifier           identifier,
            Identifier []        remoteIdentifiers,
            Map<String,String>   attributes,
            LidCredentialType [] credentialTypes,
            String []            credentials )
    {
        return new SimpleLidPersona( identifier, remoteIdentifiers, attributes, credentialTypes, credentials );
    }

    /**
     * Constructor, use factory.
     *
     * @param identifier the unique identifier of the persona, e.g. their identity URL
     * @param remoteIdentifiers set of remote Identifiers that are also associated with this LidPersona
     * @param attributes attributes of the persona, e.g. first name
     * @param credentialTypes the types of credentials available to locally authenticate this LidPersona
     * @param credentials the values for the credentials available to locally authenticate this LidPersona
     */
    protected SimpleLidPersona(
            Identifier           identifier,
            Identifier []        remoteIdentifiers,
            Map<String,String>   attributes,
            LidCredentialType [] credentialTypes,
            String []            credentials )
    {
        super( identifier );

        theRemoteIdentifiers = remoteIdentifiers;
        theAttributes        = attributes;
        theCredentialTypes   = credentialTypes;
        theCredentials       = credentials;
    }

    /**
     * Determine the set of remote Identifiers that are also associated with this LidPersona.
     * The Identifier inherited from HasIdentifier is considered the local Identifier.
     *
     * @return the set of remote Identifiers, if any
     */
    public Identifier [] getRemoteIdentifiers()
    {
        return theRemoteIdentifiers;
    }

    /**
     * Set an attribute of the persona.
     *
     * @param key the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(
            String key,
            String value )
    {
        theAttributes.put(  key, value );
    }

    /**
     * Obtain the map of attributes. This breaks encapsulation, but works much better
     * for JSP pages.
     *
     * @return the map of attributes
     */
    public Map<String,String> getAttributes()
    {
        return theAttributes;
    }

    /**
     * Obtain the subset of credential types applicable to this LidPersona.
     *
     * @param set the set of credential types
     * @return the subset of credential types
     */
    public LidCredentialType [] getApplicableCredentialTypes(
            LidCredentialType [] set )
    {
        return theCredentialTypes; // this presumes that these credential types are always a subset -- reasonable assumption
    }

    /**
     * Obtain a specific credential.
     *
     * @param type the LidCredentialType for which the credential is to be obtained
     * @return the credential, or null
     */
    public String getCredentialFor(
            LidCredentialType type )
    {
        for( int i=0 ; i<theCredentialTypes.length ; ++i ) {
            if( theCredentialTypes[i].equals( type )) {
                return theCredentials[i];
            }
        }
        return null;
    }

    /**
     * Remote Identifiers also associated with this LidPersona.
     */
    protected Identifier [] theRemoteIdentifiers;

    /**
     * Attributes of the LidPersona.
     */
    protected Map<String,String> theAttributes;

    /**
     * Supported types of credentials.
     */
    protected LidCredentialType [] theCredentialTypes;

    /**
     * Actual credentials. Same order as theCredentialTypes.
     */
    protected String [] theCredentials;
}
