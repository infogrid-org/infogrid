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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.text;

/**
 * Stringifier for HTML input fields where double-quotes must be escaped.
 */
public class EscapeQuoteStringStringifier
    extends
        StringStringifier
{
    /**
     * Factory method.
     *
     * @return the created EscapeQuoteStringStringifier
     */
    public static EscapeQuoteStringStringifier create()
    {
        return new EscapeQuoteStringStringifier();
    }

    /**
     * No-op constructor. Use factory method.
     */
    protected EscapeQuoteStringStringifier()
    {
        // no op
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
        return s.replaceAll( "&quot;", "&amp;quot;" ).replaceAll( "\"", "&quot;" );
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
        return s.replaceAll( "&quot;", "\"" ).replaceAll( "&amp;quot;", "&quot;" );
    }
}
