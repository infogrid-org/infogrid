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

/**
 * Factors out functionality common to Stringifiers that delegate to other Stringifiers.
 * 
 * @param <T> the type of the Objects to be stringified
 */
public abstract class AbstractDelegatingStringifier<T>
        extends
            AbstractStringifier<T>
{
    /**
     * Private constructor.
     *
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     */
    protected AbstractDelegatingStringifier(
            Stringifier<T> delegate )
    {
        theDelegate = delegate;
    }

    /**
     * Obtain the delegate Stringifier.
     *
     * @return the delegate
     */
    public Stringifier<T> getDelegate()
    {
        return theDelegate;
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
    public String format(
            String                         soFar,
            T                              arg,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String ret = theDelegate.format( soFar, arg, pars );

        ret = escape( ret );
        return ret;
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
    public String attemptFormat(
            String                         soFar,
            Object                         arg,
            StringRepresentationParameters pars )
        throws
            StringifierException,
            ClassCastException
    {
        String ret = theDelegate.attemptFormat( soFar, arg, pars );

        ret = escape( ret );
        return ret;
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
        String descaped = unescape( rawString );

        T ret = theDelegate.unformat( descaped, factory );
        return ret;
    }

    /**
     * Obtain an iterator that iterates through all the choices that exist for this Stringifier to
     * parse the String. The iterator returns zero elements if the String could not be parsed
     * by this Stringifier.
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
            String                     rawString,
            int                        startIndex,
            int                        endIndex,
            int                        max,
            boolean                    matchAll,
            StringifierUnformatFactory factory )
    {
        String descaped = unescape( rawString );

        Iterator<StringifierParsingChoice<T>> ret = theDelegate.parsingChoiceIterator( descaped, startIndex, endIndex, max, matchAll, factory );
        return ret;
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
        return s;
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
        return s;
    }

    /**
     * The underlying Stringifier that knows how to deal with the real type.
     */
    protected Stringifier<T> theDelegate;
}
