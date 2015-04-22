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

package org.infogrid.lid.account;

import java.util.Map;
import java.util.Set;
import org.infogrid.util.AbstractHasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Collects features of LidAccount that are common to many implementations.
 */
public abstract class AbstractLidAccount
        extends
            AbstractHasIdentifier
        implements
            LidAccount
{
    /**
     * Constructor for subclasses only.
     * 
     * @param identifier the unique identifier of the LidAccount, e.g. their local handle.
     * @param siteIdentifier the identifier of the site at which the LidAccount exists
     * @param timeCreated the time at which this LidAccount was created, in System.getTimeMillis() format.
     */
    protected AbstractLidAccount(
            Identifier identifier,
            Identifier siteIdentifier,
            long       timeCreated )
    {
        super( identifier );

        theSiteIdentifier = siteIdentifier;
        theTimeCreated    = timeCreated;
    }

    /**
     * Determine the Identifier of the site at which this LidAccount exists.
     *
     * @return the Identifier of the site
     */
    public Identifier getSiteIdentifier()
    {
        return theSiteIdentifier;
    }

    /**
     * Convenience method to determine whether this LidAccount is identified by the
     * provided Identifier.
     *
     * @param identifier the Identifier to test
     * @return true if this LidAccount is identified by the provided Identifier
     */
    @Override
    public boolean isIdentifiedBy(
            Identifier identifier )
    {
        if( getIdentifier().equals( identifier )) {
            return true;
        }
        Identifier [] remote = getRemoteIdentifiers();
        if( remote == null ) {
            return false;
        }
        for( int i=0 ; i<remote.length ; ++i ) {
            if( remote[i].equals( identifier )) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain an attribute of the LidAccount.
     *
     * @param key the name of the attribute
     * @return the value of the attribute, or null
     */
    public String getAttribute(
            String key )
    {
        Map<String,String> atts = getAttributes();
        if( atts == null ) {
            return null;
        }
        String ret = atts.get( key );
        return ret;
    }

    /**
     * Get the set of keys into the set of attributes.
     *
     * @return the keys into the set of attributes
     */
    public Set<String> getAttributeKeys()
    {
        Map<String,String> atts = getAttributes();
        if( atts == null ) {
            return null;
        }
        Set<String> ret = atts.keySet();
        return ret;
    }

    /**
     * Obtain the time of creation of this LidAccount. This is immutable for the
     * lifetime of the LidAccount.
     *
     * @return the time this LidAccount was created in <code>System.currentTimeMillis()</code> format
     */
    public long getTimeCreated()
    {
        return theTimeCreated;
    }

    /**
     * The Identifier of the site at which this LidAccount exists.
     */
    protected Identifier theSiteIdentifier;
    
    /*
     * The time at which this LidAccount was created, in System.getTimeMillis() format.
     */
    protected long theTimeCreated;
}
