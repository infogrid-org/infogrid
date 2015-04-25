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

/**
 * A delegating Stringifier that prepends and appends Strings.
 *
 * @param <T> the type of the Objects to be stringified
 */
public class PrePostfixDelegatingStringifier<T>
        extends
             AbstractDelegatingStringifier<T>
{
    /**
     * Factory method.
     *
     * @param start the string to insert at the beginning
     * @param end the string to append at the end
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created PrePostfixDelegatingStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> PrePostfixDelegatingStringifier<T> create(
            String         start,
            String         end,
            Stringifier<T> delegate )
    {
        return new PrePostfixDelegatingStringifier<T>( start, end, delegate );
    }

    /**
     * Constructor.
     *
     * @param start the string to insert at the beginning
     * @param end the string to append at the end
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     */
    protected PrePostfixDelegatingStringifier(
            String         start,
            String         end,
            Stringifier<T> delegate )
    {
        super( delegate );

        theStart       = start;
        theEnd         = end;
    }

    /**
     * Obtain the start String, if any.
     *
     * @return the start String, if any
     */
    public String getStart()
    {
        return theStart;
    }

    /**
     * Obtain the end String, if any.
     *
     * @return the end String, if any
     */
    public String getEnd()
    {
        return theEnd;
    }

    /**
     * Format an Object using this Stringifier.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return the formatted String
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    @Override
    public String format(
            String                         soFar,
            T                              arg,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        StringBuilder ret  = new StringBuilder();
        
        if( theStart != null ) {
            ret.append( theStart );
        }
        String childInput = theDelegate.attemptFormat( soFar, arg, pars );
        ret.append( childInput );

        if( theEnd != null ) {
            ret.append( theEnd );
        }

        return potentiallyShorten( ret.toString(), pars );
    }

    /**
     * Format an Object using this Stringifier. This may be null.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return the formatted String
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @throws ClassCastException thrown if this Stringifier could not format the provided Object
     *         because the provided Object was not of a type supported by this Stringifier
     */
    @SuppressWarnings("unchecked")
    @Override
    public String attemptFormat(
            String                         soFar,
            Object                         arg,
            StringRepresentationParameters pars )
        throws
            StringifierException,
            ClassCastException
    {
        return format( soFar, (T) arg, pars );
    }

    /**
     * The String to insert at the beginning. May be null.
     */
    protected String theStart;

    /**
     * The String to append at the end. May be null.
     */
    protected String theEnd;
}
