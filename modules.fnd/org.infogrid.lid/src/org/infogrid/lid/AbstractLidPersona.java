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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Collects features of LidPersona that are common to many implementations.
 */
public abstract class AbstractLidPersona
        extends
            AbstractLidResource
        implements
            LidPersona,
            Serializable
{
    /**
     * Constructor for subclasses only.
     * 
     * @param identifier the unique identifier of the persona, e.g. their identity URL
     * @param attributes attributes of the persona, e.g. first name
     */
    protected AbstractLidPersona(
            String                 identifier,
            Map<String,String>     attributes )
    {
        super( identifier );

        theAttributes = attributes;
    }

    /**
     * Obtain an attribute of the persona.
     * 
     * @param key the name of the attribute
     * @return the value of the attribute, or null
     */
    public String getAttribute(
            String key )
    {
        String ret = theAttributes.get( key );
        return ret;
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
     * Get the set of keys into the set of attributes.
     * 
     * @return the keys into the set of attributes
     */
    public Set<String> getAttributeKeys()
    {
        return theAttributes.keySet();
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
     * Attributes of the persona.
     */
    protected Map<String,String> theAttributes;    
}