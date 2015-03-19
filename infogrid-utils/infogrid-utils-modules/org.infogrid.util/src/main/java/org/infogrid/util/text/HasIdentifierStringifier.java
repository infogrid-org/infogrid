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

import org.infogrid.util.HasIdentifier;

/**
 * Stringifies a HasIdentifier.
 */
public class HasIdentifierStringifier
        extends
            AbstractStringifier<HasIdentifier>
{
    /**
     * Factory method.
     *
     * @param delegate the underlying IdentifierStringifier to use
     * @return the created HasIdentifierStringifier
     */
    public static HasIdentifierStringifier create(
            IdentifierStringifier delegate )
    {
        return new HasIdentifierStringifier( delegate );
    }

    /**
     * Constructor. Use factory method.
     *
     * @param delegate the underlying IdentifierStringifier to use
     */
    protected HasIdentifierStringifier(
            IdentifierStringifier delegate )
    {
        theDelegate = delegate;
    }

    /**
     * Format an Object using this Stringifier.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return the formatted String
     */
    public String format(
            String                         soFar,
            HasIdentifier                  arg,
            StringRepresentationParameters pars )
    {
        String ret = theDelegate.format( soFar, arg.getIdentifier(), pars );
        return ret;
    }

    /**
     * Format an Object using this Stringifier. This may be null.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return the formatted String
     * @throws ClassCastException thrown if this Stringifier could not format the provided Object
     *         because the provided Object was not of a type supported by this Stringifier
     */
    public String attemptFormat(
            String                         soFar,
            Object                         arg,
            StringRepresentationParameters pars )
        throws
            ClassCastException
    {
        return format( soFar, (HasIdentifier) arg, pars );
    }

    /**
     * The underlying IdentifierStringifier to use.
     */
    protected IdentifierStringifier theDelegate;
}
