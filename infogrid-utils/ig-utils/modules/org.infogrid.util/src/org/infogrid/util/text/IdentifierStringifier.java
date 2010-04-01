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

import java.text.ParseException;
import java.util.Iterator;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.OneElementIterator;
import org.infogrid.util.ZeroElementCursorIterator;

/**
 * Stringifies an Identifier.
 */
public class IdentifierStringifier
        extends
            AbstractStringifier<Identifier>
{
    /**
     * Factory method without prefix or postfix.
     *
     * @return the created IdentifierStringifier
     */
    public static IdentifierStringifier create()
    {
        return new IdentifierStringifier( true, null, null );
    }

    /**
     * Factory method with prefix or postfix.
     *
     * @param processColloquial if true, process the colloquial parameter (if given). If false, leave identifier as is.
     * @param prefix the prefix for the identifier, if any
     * @param postfix the postfix for the identifier, if any
     * @return the created IdentifierStringifier
     */
    public static IdentifierStringifier create(
            boolean processColloquial,
            String  prefix,
            String  postfix )
    {
        return new IdentifierStringifier( processColloquial, prefix, postfix );
    }

    /**
     * Constructor. Use factory method.
     *
     * @param processColloquial if true, process the colloquial parameter (if given). If false, leave identifier as is.
     * @param prefix the prefix for the identifier, if any
     * @param postfix the postfix for the identifier, if any
     */
    protected IdentifierStringifier(
            boolean processColloquial,
            String  prefix,
            String  postfix )
    {
        theProcessColloquial = processColloquial;
        thePrefix            = prefix;
        thePostfix           = postfix;
    }

    /**
     * Format an Object using this Stringifier.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation
     * @return the formatted String
     */
    public String format(
            String                         soFar,
            Identifier                     arg,
            StringRepresentationParameters pars )
    {
        if( arg == null ) {
            return formatToNull( soFar, pars );
        }

        String ext = arg.toExternalForm();

        ext = potentiallyProcessColloquial( ext, pars );
        ext = potentiallyShorten( ext, pars );
        ext = escape( ext );

        String ret = processPrefixPostfix( ext );
        return ret;
    }

    /**
     * Format an Object using this Stringifier. This may be null.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param pars collects parameters that may influence the String representation
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
        return format( soFar, (Identifier) arg, pars );
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
     * Format an a null Object using this Stringifier.
     *
     * @param soFar the String so far, if any
     * @param pars collects parameters that may influence the String representation
     * @return the formatted String
     */
    protected String formatToNull(
            String                         soFar,
            StringRepresentationParameters pars )
    {
        return "null"; // FIXME?
    }

    /**
     * Process the string according to the value of theProcessColloquial
     * and the parameter.
     *
     * @param in the input String
     * @param pars collects parameters that may influence the String representation
     * @return the output String
     */
    protected String potentiallyProcessColloquial(
            String                         in,
            StringRepresentationParameters pars )
    {
        String ret = in;

        if( theProcessColloquial && pars != null ) {
            Boolean colloquial = (Boolean) pars.get( StringRepresentationParameters.COLLOQUIAL );
            if( colloquial != null && colloquial.booleanValue() ) {

                ret = toColloquialIdentifier( ret );
            }
        }
        return ret;
    }

    /**
     * Factored out colloquial processing for easier reusability.
     *
     * @param identifier
     * @return colloquial form of identifier
     */
    public static String toColloquialIdentifier(
            String identifier )
    {
        String ret = identifier;

        if( ret != null ) {
            final String PREFIX = "http://";
            if( ret.startsWith( PREFIX )) {
                ret = ret.substring( PREFIX.length() );
            }
            if( ret.charAt( ret.length()-1 ) == '/' ) {
                ret = ret.substring( 0, ret.length()-1 );
            }
        }
        return ret;
    }

    /**
     * Perform prefix and postfix processing, if applicable
     *
     * @param in the input String
     * @return the output String
     */
    protected String processPrefixPostfix(
            String in )
    {
        if( thePrefix == null && thePostfix == null ) {
            return in;

        } else {
            StringBuilder buf = new StringBuilder();
            if( thePrefix != null ) {
                buf.append( thePrefix );
            }
            buf.append( in );
            if( thePostfix != null ) {
                buf.append( thePrefix );
            }
            return buf.toString();
        }
    }

    /**
     * Parse out the Object in rawString that were inserted using this Stringifier.
     * This default implementation simply throws an UnsupportedOperationException.
     *
     * @param rawString the String to parse
     * @param factory the factory needed to create the parsed values, if any
     * @return the found Object
     * @throws StringifierParseException thrown if a parsing problem occurred
     */
    @Override
    public Identifier unformat(
            String                     rawString,
            StringifierUnformatFactory factory )
        throws
            StringifierParseException
    {
        IdentifierFactory realFactory = (IdentifierFactory) factory;

        try {
            Identifier found = realFactory.fromExternalForm( rawString );
            return found;

        } catch( ParseException ex ) {
            throw new StringifierParseException( this, rawString, ex );
        }
    }

    /**
     * Obtain an iterator that iterates through all the choices that exist for this Stringifier to
     * parse the String. The iterator returns zero elements if the String could not be parsed
     * by this Stringifier.
     * This default implementation simply throws an UnsupportedOperationException.
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
    public Iterator<StringifierParsingChoice<Identifier>> parsingChoiceIterator(
            String                     rawString,
            int                        startIndex,
            int                        endIndex,
            int                        max,
            boolean                    matchAll,
            StringifierUnformatFactory factory )
    {
        IdentifierFactory realFactory = (IdentifierFactory) factory;

        try {
            Identifier found = realFactory.fromExternalForm( rawString.substring( startIndex, endIndex ));

            return OneElementIterator.<StringifierParsingChoice<Identifier>>create(
                    new StringifierValueParsingChoice<Identifier>( startIndex, endIndex, found ));

        } catch( ParseException ex ) {
            return ZeroElementCursorIterator.create();
        }
    }

    /**
     * The prefix, if any.
     */
    protected String thePrefix;

    /**
     * The postfix, if any.
     */
    protected String thePostfix;

    /**
     * If true, and colloquial is specified, do the colloquial processing. If false, keep as is.
     */
    protected boolean theProcessColloquial;
}
