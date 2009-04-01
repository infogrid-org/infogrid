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

import org.infogrid.util.StringHelper;

/**
 * Factors out common functionality for Stringifiers that process numbers.
 */
public abstract class NumberStringifier
{
    /**
     * Constructor.
     *
     * @param digits the number of digits
     */
    protected NumberStringifier(
            int digits )
    {
        theDigits = digits;
    }
    
    /**
     * Format a numeric value using this Stringifier.
     *
     * @param soFar the String so far, if any
     * @param arg the numerica value, as long
     * @param maxLength maximum length of emitted String. -1 means unlimited.
     * @param colloquial if applicable, output in colloquial form
     * @return the formatted String
     */
    protected String format(
            String  soFar,
            long    arg,
            int     maxLength,
            boolean colloquial )
    {
        String ret;
        if( theDigits <= 0 ) {
            ret = String.valueOf( arg );

        } else {
            boolean negative;

            if( arg >= 0 ) {
                ret = String.valueOf( arg );
                negative = false;
            } else {
                ret = String.valueOf( -arg );
                negative = true;
            }
            StringBuilder prepend = new StringBuilder();
            if( negative ) {
                prepend.append( '-' );
            }
            for( int i=ret.length() ; i<theDigits ; ++i ) {
                prepend.append( '0' ); // leading zer0
            }
            prepend.append( ret );
            ret = prepend.toString();
        }
        ret = StringHelper.potentiallyShorten( ret, maxLength );
        return ret;

    }

    /**
     * Format an Object using this Stringifier. This may be null.
     *
     * @param soFar the String so far, if any
     * @param arg the Object to format, or null
     * @param maxLength maximum length of emitted String. -1 means unlimited.
     * @param colloquial if applicable, output in colloquial form
     * @return the formatted String
     * @throws ClassCastException thrown if this Stringifier could not format the provided Object
     *         because the provided Object was not of a type supported by this Stringifier
     */
    public String attemptFormat(
            String  soFar,
            Object  arg,
            int     maxLength,
            boolean colloquial )
        throws
            ClassCastException
    {
        if( arg instanceof Short ) {
            return format( soFar, ((Short)arg).longValue(), maxLength, colloquial );
        } else if( arg instanceof Long ) {
            return format( soFar, ((Long)arg).longValue(), maxLength, colloquial );
        } else {
            return format( soFar, ((Integer)arg).longValue(), maxLength, colloquial );
        }
    }
    
    /**
     * Is this a valid char for this Stringifier.
     *
     * @param pos position
     * @param min limits the considered String by this minimum position (inclusive)
     * @param max limits the considered String by this maximum position (exclusive)
     * @param s the String on whose position we find the char
     * @return true or false
     */
    boolean validChar(
            int    pos,
            int    min,
            int    max,
            String s )
    {
        int length = s.length();
        if( max > length ) {
            return false;
        }
        if( pos >= length ) {
            return false;
        }
        if( pos < min ) {
            return false;
        }

        char c = s.charAt( pos );

        if( pos == min && length > min ) {
            if( c == '+' || c == '-' ) {
                if( validChar( pos+1, min, max, s )) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        boolean ret = Character.isDigit( c );
        return ret;
    }
    
    /**
     * The number of digits to make. -1 means "don't pay any attention".
     */
    protected int theDigits;
}
