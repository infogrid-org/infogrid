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

import org.infogrid.util.ArrayFacade;

import java.util.Iterator;

/**
 * A component in the CompoundStringifier. This is not quite symmetric: the <code>format</code> argument
 * takes all arguments of the CompoundStringifier (and picks out the ones that apply), while the
 * Iterator only returns this CompoundStringifierComponent's contribution.
 */
public interface CompoundStringifierComponent<T>
{
    /**
     * Format zero or one Objects.
     *
     * @param arg the Object to format
     * @return the formatted String
     */
    public String format(
            ArrayFacade<T> arg );
    
    /**
     * Obtain an iterator that goes with this CompoundStringifierComponent.
     * 
     * @return the iterator
     */
    public Iterator<? extends StringifierParsingChoice<? extends T>> parsingChoiceIterator(
            String  rawString,
            int     startIndex,
            int     endIndex,
            int     max,
            boolean matchAll );
}
