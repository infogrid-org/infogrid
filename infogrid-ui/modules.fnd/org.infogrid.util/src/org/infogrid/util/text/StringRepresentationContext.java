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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.text;

import java.util.Iterator;

/**
 * Captures the context of a StringRepresentation.
 */
public interface StringRepresentationContext
{
    /**
     * Obtain an iterator over the keys.
     *
     * @return iterator over the keys
     */
    public Iterator<String> keyIterator();

    /**
     * Obtain a specific value.
     *
     * @param key the key
     * @return the value with this key
     */
    public Object get(
            String key );

    /**
     * Add or change a specific value.
     *
     * @param key the key
     * @param value the new value
     * @return the old value, if any
     */
    public Object put(
            String key,
            Object value );

    /**
     * The key that represents a web application's context path.
     */
    public static final String WEB_CONTEXT_KEY = "web-context-path";
}