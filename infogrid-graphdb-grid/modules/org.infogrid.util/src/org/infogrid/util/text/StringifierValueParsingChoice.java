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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.text;

import org.infogrid.util.StringHelper;

/**
 * A simple implementation of StringifierParsingChoice that holds its own values.
 */
public class StringifierValueParsingChoice<T>
        extends
            StringifierParsingChoice<T>
{
    /**
     * Constructor.
     *
     * @param startIndex the start index (inclusive) in the parsed String
     * @param endIndex the end index (exclusive) in the parsed String
     * @param value the value representing this parsing choice
     */
    public StringifierValueParsingChoice(
            int    startIndex,
            int    endIndex,
            T      value )
    {
        super( startIndex, endIndex );
        
        theValue = value;
    }

    /**
     * Given this StringifierParsingChoice, obtain the Object parsed. This may
     * return null, given that some Stringifiers don't accept Objects into their
     * format method either.
     *
     * @return the Object parsed
     */
    public T unformat()
    {
        return theValue;
    }

    /**
     * Convert to String, for debugging.
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theStartIndex",
                    "theEndIndex",
                    "theValue"
                },
                new Object[] {
                    theStartIndex,
                    theEndIndex,
                    theValue
                });
    }

    /**
     * The value of this choice.
     */
    protected T theValue;
}
