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

package org.infogrid.util.text;

import java.util.Iterator;

/**
 * A degenerate Stringifier that throws Exceptions only indicating that the use of this Stringifier
 * is invalid in a particular context.
 *
 * @param <T> can by used for any type
 */
public class InvalidStringifier<T>
        extends
            AbstractStringifier<T>
{
    /**
     * Factory method.
     *
     * @return the created InvalidStringifier
     * @param <T> can by used for any type
     */
    public static <T> InvalidStringifier<T> create()
    {
        return new InvalidStringifier<T>();
    }

    /**
     * No-op constructor. Use factory method.
     */
    protected InvalidStringifier()
    {
        // no op
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
            T                              arg,
            StringRepresentationParameters pars )
    {
        throw new UnsupportedOperationException( "Cannot use: InvalidStringifier!" );
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
        throw new UnsupportedOperationException( "Cannot use: InvalidStringifier!" );
    }

    /**
     * Parse out the Object in rawString that were inserted using this Stringifier.
     *
     * @param rawString the String to parse
     * @param factory the factory needed to create the parsed values, if any
     * @return the found Object
     * @throws StringifierParseException thrown if a parsing problem occurred
     */
    @Override
    public T unformat(
            String                     rawString,
            StringifierUnformatFactory factory )
        throws
            StringifierParseException
    {
        throw new UnsupportedOperationException( "Cannot use: InvalidStringifier!" );
    }

    /**
     * Obtain an iterator that iterates through all the choices that exist for this Stringifier to
     * parse the String. The iterator returns zero elements if the String could not be parsed
     * by this Stringifier.
     * FIXME: This doesn't work correctly for escaped Strings.
     *
     * @param rawString the String to parse
     * @param startIndex the position at which to parse rawString
     * @param endIndex the position at which to end parsing rawString
     * @param max the maximum number of choices to be returned by the Iterator.
     * @param matchAll if true, only return those matches that match the entire String from startIndex to endIndex.
     *                 If false, return other matches that only match the beginning of the String.
     * @param factory the factory needed to create the parsed values, if any
     * @return the Iterator
     */
    @Override
    public Iterator<StringifierParsingChoice<T>> parsingChoiceIterator(
            final String                     rawString,
            final int                        startIndex,
            final int                        endIndex,
            final int                        max,
            final boolean                    matchAll,
            final StringifierUnformatFactory factory )
    {
        throw new UnsupportedOperationException( "Cannot use: InvalidStringifier!" );
    }
}
