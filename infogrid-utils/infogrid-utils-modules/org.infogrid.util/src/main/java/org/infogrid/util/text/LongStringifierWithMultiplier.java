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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.OneElementIterator;
import org.infogrid.util.SingleElementCursorIterator;
import org.infogrid.util.ZeroElementCursorIterator;
import org.infogrid.util.logging.Log;

/**
 * Stringifies a single Long, supporting suitable multipliers such as kilo, Mega, etc.
 * Overriding NumberStringifier does not seem to work.
 */
public class LongStringifierWithMultiplier
        extends
            AbstractStringifier<Long>
{
    private static final Log log = Log.getLogInstance( LongStringifierWithMultiplier.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @return the created LongStringifier
     */
    public static LongStringifierWithMultiplier create()
    {
        return new LongStringifierWithMultiplier( true );
    }

    /**
     * Constructor. Use factory method.
     * 
     * @param useThousands if true, consider prefixes multiples of 1000; if false, of 1024.
     */
    protected LongStringifierWithMultiplier(
            boolean useThousands )
    {
        theUseThousands = useThousands;
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
            Long                           arg,
            StringRepresentationParameters pars )
    {
        // from http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java

        int unit = theUseThousands ? 1000 : 1024;
        String ret;
        
        if( arg < unit ) {
            ret = String.valueOf( arg ) + " "; // no prefix, but for consistent formatting
            
        } else {
            int exp = (int) (Math.log( arg ) / Math.log( unit ));
            String pre;
            if( theUseThousands ) {
                pre = theThousandsMultipliers[exp-1];
            } else {
                pre = theTwoToTheTenMultipliers[exp-1];
            }
            double mant     = arg / Math.pow( unit, exp );
            long   mantLong = (long) ( mant + .5d );
            
            if( mantLong < 100 ) {
                ret = String.format( "%.1f %s", mant, pre );
            } else {
                ret = String.format( "%d %s", mantLong, pre );
                
            }
        }
        
        ret = potentiallyShorten( ret, pars );
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
        if( arg instanceof Short ) {
            return format( soFar, ((Short)arg).longValue(), pars );
        } else if( arg instanceof Long ) {
            return format( soFar, ((Long)arg).longValue(), pars );
        } else {
            return format( soFar, ((Integer)arg).longValue(), pars );
        }
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
    public Long unformat(
            String                     rawString,
            StringifierUnformatFactory factory )
        throws
            StringifierParseException
    {
        Matcher m = thePattern.matcher( rawString );
        if( !m.matches() ) {
            throw new StringifierParseException( this, rawString );
        }
        String beforePeriodString = m.group( 1 );
        String afterPeriodString  = m.group( 2 );
        
        long   beforePeriod = ( beforePeriodString != null && !beforePeriodString.isEmpty() ) ? Integer.parseInt( beforePeriodString ) : 0L;
        long   afterPeriod  = ( afterPeriodString  != null && !afterPeriodString.isEmpty()  ) ? Integer.parseInt( afterPeriodString )  : 0L;
        String prefix       = m.group( 3 );
    
        if( prefix == null || prefix.isEmpty() ) {
            return beforePeriod;
        }
        
        double n = Double.parseDouble( String.format( "%d.%d", beforePeriod, afterPeriod ) );
        
        long factor = 1000L;
        for( int i=0 ; i<theThousandsMultipliers.length ; ++i ) {
            if( theThousandsMultipliers[i].equals( prefix )) {
                return (long) ( n * factor + .5d );
            }
            factor *= 1000L;
        }

        factor = 1024L;
        for( int i=0 ; i<theTwoToTheTenMultipliers.length ; ++i ) {
            if( theTwoToTheTenMultipliers[i].equals( prefix )) {
                return (long) ( n * factor + .5d );
            }
            factor *= 1024L;
        }
        throw new StringifierParseException(  this, rawString );
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
    public Iterator<StringifierParsingChoice<Long>> parsingChoiceIterator(
            final String                     rawString,
            final int                        startIndex,
            final int                        endIndex,
            final int                        max,
            final boolean                    matchAll,
            final StringifierUnformatFactory factory )
    {
        if( matchAll ) {
            try {
                final Long result = unformat( rawString.substring( startIndex, endIndex ), factory );
                
                return OneElementIterator.<StringifierParsingChoice<Long>>create(
                        new StringifierParsingChoice<Long>( startIndex, startIndex + rawString.length() ) {
                            public Long unformat() {
                                // this doesn't return any value
                                return result;
                            }
                        }
                    );

            } catch( StringifierParseException ex ) {
                return ZeroElementCursorIterator.create();
            }
        } else {
            return ZeroElementCursorIterator.create();
        }
    }

    /**
     * If true, consider prefixes multipliers of 1000.
     * If false, of 1024.
     */
    protected boolean theUseThousands;
    
    /**
     * Supported thousands multipliers.
     */
    protected static final String [] theThousandsMultipliers = {
        "k",
        "M",
        "G",
        "T"
    };
    
    /**
     * Supported 2**10 multipliers.
     */
    protected static final String [] theTwoToTheTenMultipliers = {
        "Ki",
        "Mi",
        "Gi",
        "Ti"
    };
    
    /**
     * Regular expression that makes it easier to parse.
     */
    protected static final Pattern thePattern = Pattern.compile(
            "^\\s*(\\d*)(?:\\.(\\d*))?(?:\\s*("
            + ArrayHelper.join( "|", theThousandsMultipliers )
            + "|"
            + ArrayHelper.join( "|", theTwoToTheTenMultipliers )
            + "))?\\s*$" );
}
