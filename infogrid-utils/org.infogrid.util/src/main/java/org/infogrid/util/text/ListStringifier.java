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

import java.util.List;

/**
 * A Stringifier that processes lists.
 *
 * @param <T> the type of the Objects to be stringified
 */
public class ListStringifier<T>
        extends
             AbstractStringifier<List<T>>
{
    /**
     * Factory method. This creates an ListStringifier that merely appends the
     * individual components after each other.
     *
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created ListStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> ListStringifier<T> create(
            Stringifier<T> delegate )
    {
        return new ListStringifier<T>( null, null, null, null, delegate );
    }

    /**
     * Factory method. This creates an ListStringifier that joins the
     * individual components after each other with a string in the middle.
     * This is similar to Perl's join.
     *
     * @param middle the string to insert in the middle
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created ListStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> ListStringifier<T> create(
            String         middle,
            Stringifier<T> delegate )
    {
        return new ListStringifier<T>( null, middle, null, null, delegate );
    }

    /**
     * Factory method. This creates an ListStringifier that joins the
     * individual components after each other with a string in the middle,
     * prepends a start and appends an end.
     *
     * @param start the string to insert at the beginning
     * @param middle the string to insert in the middle
     * @param end the string to append at the end
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created ListStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> ListStringifier<T> create(
            String         start,
            String         middle,
            String         end,
            Stringifier<T> delegate )
    {
        return new ListStringifier<T>( start, middle, end, null, delegate );
    }

    /**
     * Factory method. This creates an ListStringifier that joins the
     * individual components after each other with a string in the middle,
     * prepends a start and appends an end, or uses a special empty String if
     * the array is empty.
     *
     * @param start the string to insert at the beginning
     * @param middle the string to insert in the middle
     * @param end the string to append at the end
     * @param empty what to emit instead if the array is empty
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @return the created ListStringifier
     * @param <T> the type of the Objects to be stringified
     */
    public static <T> ListStringifier<T> create(
            String         start,
            String         middle,
            String         end,
            String         empty,
            Stringifier<T> delegate )
    {
        return new ListStringifier<T>( start, middle, end, empty, delegate );
    }

    /**
     * Constructor.
     *
     * @param start the string to insert at the beginning
     * @param middle the string to insert in the middle
     * @param end the string to append at the end
     * @param delegate the underlying Stringifier that knows how to deal with the real type
     * @param empty what to emit instead if the array is empty
     */
    protected ListStringifier(
            String         start,
            String         middle,
            String         end,
            String         empty,
            Stringifier<T> delegate )
    {
        theStart       = start;
        theMiddle      = middle;
        theEnd         = end;
        theEmptyString = empty;
        theDelegate    = delegate;
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
     * Obtain the middle String, if any.
     *
     * @return the middle String, if any
     */
    public String getMiddle()
    {
        return theMiddle;
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
    @Override
    public String format(
            String                         soFar,
            List<T>                        arg,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        if( arg == null || arg.isEmpty() ) {
            if( theEmptyString != null ) {
                return theEmptyString;
            } else if( theStart != null ) {
                if( theEnd != null ) {
                    return theStart+theEnd;
                } else {
                    return theStart;
                }
            } else if( theEnd != null ) {
                return theEnd;
            } else {
                return "";
            }
        }

        StringBuilder ret  = new StringBuilder();
        String        sep  = theStart;

        for( Object current : arg ) {
            if( sep != null ) {
                ret.append( sep );
            }
            String childInput = theDelegate.attemptFormat( soFar, current, pars );
            if( childInput != null ) {
                ret.append( childInput );
            }
            sep = theMiddle;
        }
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
        return format( soFar, (List<T>) arg, pars );
    }

    /**
     * The String to insert at the beginning. May be null.
     */
    protected String theStart;

    /**
     * The String to insert when joining two elements. May be null.
     */
    protected String theMiddle;

    /**
     * The String to append at the end. May be null.
     */
    protected String theEnd;

    /**
     * The String to emit if the array if empty. May be null, in which case it is assumed to theStart+theEnd.
     */
    protected String theEmptyString;

    /**
     * The underlying Stringifier that knows how to deal with the real type.
     */
    protected Stringifier<T> theDelegate;
}
