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

package org.infogrid.util.text;

import java.util.Iterator;
import org.infogrid.util.http.HTTP;

/**
 * Stringifies a String by escaping all characters necessary to make the String a valid URL argument.
 * For example, it replaces ? and &.
 *
 * @param <T> the type of the Objects to be stringified
 */
public class ToValidUrlArgumentStringifier<T>
        extends
            AbstractDelegatingStringifier<T>
{
    /**
     * Factory method.
     *
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created StringStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> ToValidUrlArgumentStringifier<T> create(
            Stringifier<T> delegate )
    {
        return new ToValidUrlArgumentStringifier<T>( delegate );
    }

    /**
     * Private constructor. Use factory method.
     *
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     */
    protected ToValidUrlArgumentStringifier(
            Stringifier<T> delegate )
    {
        super( delegate );
    }

    /**
     * Overridable method to possibly escape a String first.
     *
     * @param s the String to be escaped
     * @return the escaped String
     */
    protected String escape(
            String s )
    {
        String ret = HTTP.encodeToValidUrlArgument( s );
        return ret;
    }

    /**
     * Overridable method to possibly unescape a String first.
     *
     * @param s the String to be unescaped
     * @return the unescaped String
     */
    protected String unescape(
            String s )
    {
        String ret = HTTP.decodeUrlArgument( s );
        return ret;
    }
}
