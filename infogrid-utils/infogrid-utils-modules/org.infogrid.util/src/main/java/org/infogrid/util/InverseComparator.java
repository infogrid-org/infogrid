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

package org.infogrid.util;

import java.util.Comparator;

/**
 * Returns the opposite order than a delegate Comparator.
 *
 * @param <T> the type of objects that may be compared by this comparator
 */
public class InverseComparator<T>
    implements
        Comparator<T>
{
    /**
     * Constructor.
     *
     * @param delegate the delegate Comparator whose results are inverted.
     */
    public InverseComparator(
            Comparator<T> delegate )
    {
        theDelegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(
            T o1,
            T o2 )
    {
        int ret = theDelegate.compare( o1, o2 );
        return -ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(
            Object obj )
    {
        if( !( obj instanceof InverseComparator )) {
            return false;
        }
        InverseComparator realObj = (InverseComparator) obj;

        return theDelegate.equals( realObj.theDelegate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 11 * hash + (this.theDelegate != null ? this.theDelegate.hashCode() : 0);
        return hash;
    }

    /**
     * The delegate Comparator whose results are inverted.
     */
    protected Comparator<T> theDelegate;
}
