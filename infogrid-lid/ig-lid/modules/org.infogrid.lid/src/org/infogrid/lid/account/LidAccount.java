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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.account;

import java.util.Map;
import java.util.Set;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Represents a locally provisioned persona.
 */
public interface LidAccount
        extends
            HasIdentifier
{
    /**
     * Determine the Identifier of the site at which this LidAccount exists.
     *
     * @return the Identifier of the site
     */
    public Identifier getSiteIdentifier();

    /**
     * Determine the set of remote Identifiers that are also associated with this LidAccount.
     * The Identifier inherited from HasIdentifier is considered the local Identifier.
     *
     * @return the set of remote Identifiers, if any
     */
    public Identifier [] getRemoteIdentifiers();

    /**
     * Convenience method to determine whether this LidAccount is identified by the
     * provided Identifier.
     *
     * @param identifier the Identifier to test
     * @return true if this LidAccount is identified by the provided Identifier
     */
    public boolean isIdentifiedBy(
            Identifier identifier );

    /**
     * Obtain an attribute of the LidAccount.
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
     * Obtain the subset of credential types applicable to this LidAccount.
     *
     * @param set the set of credential types
     * @return the subset of credential types
     */
    public LidCredentialType [] getApplicableCredentialTypes(
            LidCredentialType [] set );

    /**
     * Obtain a specific credential.
     *
     * @param type the LidCredentialType for which the credential is to be obtained
     * @return the credential, or null
     */
    public String getCredentialFor(
            LidCredentialType type );

    /**
     * Obtain the Identifiers of the set of groups that this LidAccount is a member of.
     *
     * @return the Identifiers
     */
    public Identifier [] getGroupIdentifiers();

    /**
     * Obtain the names of the set of groups that this LidAccount is a member of.
     *
     * @return the names
     */
    public String [] getGroupNames();

    /**
     * Determine this LidAccount's status.
     *
     * @return the LidAccount's status
     */
    public LidAccountStatus getAccountStatus();

    /**
     * Name of the attribute that contains the persona's identifier.
     */
    public static final String IDENTIFIER_ATTRIBUTE_NAME = "Identifier";

    /**
     * Name of the attribute that contains the persona's nickname.
     */
    public static final String NICKNAME_ATTRIBUTE_NAME = "Nickname";

    /**
     * The status of the LidAccount.
     */
    public static enum LidAccountStatus
    {
        APPLIEDFOR,
        ACTIVE,
        DISABLED,
        OBSOLETED,
        REFUSED;
    }
}
