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

package org.infogrid.util.text;

import org.infogrid.util.StringHelper;

/**
 * Takes the output of another Stringifier and makes it valid HTML. For example, this replaces
 * <code>&gt;</code> with <code>&amp;gt;</code>.
 *
 * @param <T> the type of the Objects to be stringified
 */
public class HtmlifyingDelegatingStringifier<T>
        extends
            AbstractDelegatingStringifier<T>
{
    /**
     * Factory method.
     *
     * @return the created HtmlifyingDelegatingStringifier
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> HtmlifyingDelegatingStringifier<T> create(
            Stringifier<T> delegate )
    {
        return new HtmlifyingDelegatingStringifier<T>( delegate );
    }

    /**
     * No-op constructor. Use factory method.
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     */
    protected HtmlifyingDelegatingStringifier(
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
    @Override
    protected String escape(
            String s )
    {
        String ret = StringHelper.stringToHtml( s );
        return ret;
    }

    /**
     * Overridable method to possibly unescape a String first.
     *
     * @param s the String to be unescaped
     * @return the unescaped String
     */
    @Override
    protected String unescape(
            String s )
    {
        String ret = StringHelper.htmlToString( s );
        return ret;
    }
}
