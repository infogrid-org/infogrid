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

package org.infogrid.lid.account.translate;

import java.util.Map;
import java.util.Set;
import org.infogrid.lid.account.AbstractLidAccount;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.util.Identifier;

/**
 * Implementation of LidAccount for this TranslatingLidAccountManager.
 */
public class TranslatingLidAccount
        extends
            AbstractLidAccount
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param identifier the unique identifier of the persona, e.g. their identity URL
     * @param delegate the underlying LidAccount from/to which we translate
     */
    protected TranslatingLidAccount(
            Identifier identifier,
            LidAccount delegate )
    {
        super( identifier, delegate.getSiteIdentifier() );

        theDelegate = delegate;
    }

    /**
     * Obtain the delegate LidLocalPerson.
     *
     * @return the delegate
     */
    public LidAccount getDelegate()
    {
        return theDelegate;
    }

    /**
     * Determine this LidAccount's status.
     *
     * @return the LidAccount's status
     */
    public LidAccountStatus getAccountStatus()
    {
        return theDelegate.getAccountStatus();
    }

    /**
     * Determine the set of remote Identifiers that are also associated with this LidAccount.
     * The Identifier inherited from HasIdentifier is considered the local Identifier.
     *
     * @return the set of remote Identifiers, if any
     */
    public Identifier [] getRemoteIdentifiers()
    {
        return theDelegate.getRemoteIdentifiers();
    }

    /**
     * Obtain an attribute of the persona.
     *
     * @param key the name of the attribute
     * @return the value of the attribute, or null
     */
    @Override
    public String getAttribute(
            String key )
    {
        return theDelegate.getAttribute( key );
    }

    /**
     * Get the set of keys into the set of attributes.
     *
     * @return the keys into the set of attributes
     */
    @Override
    public Set<String> getAttributeKeys()
    {
        return theDelegate.getAttributeKeys();
    }

    /**
     * Obtain the map of attributes. This breaks encapsulation, but works much better
     * for JSP pages.
     *
     * @return the map of attributes
     */
    public Map<String,String> getAttributes()
    {
        return theDelegate.getAttributes();
    }

    /**
     * Obtain the Identifiers of the set of groups that this LidAccount is a member of.
     *
     * @return the Identifiers
     */
    public Identifier [] getGroupIdentifiers()
    {
        return theDelegate.getGroupIdentifiers();
    }

    /**
     * Obtain the names of the set of groups that this LidAccount is a member of.
     *
     * @return the names
     */
    public String [] getGroupNames()
    {
        return theDelegate.getGroupNames();
    }

    /**
     * The underlying LidAccount from/to which we translate.
     */
    protected LidAccount theDelegate;
}