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

package org.infogrid.meshbase.transaction;

import org.infogrid.util.ArrayHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
  * <p>A change set is the set of Changes (create, update, delete)
  * that are to be or have been performed during a Transaction.</p>
  *
  * <p>While the Transaction is open, Changes are automatically added to the
  * Transaction's ChangeSet. Then, the ChangeSet is frozen, and potentially
  * compacted.</p>
  */
public class ChangeSet
        implements
            Serializable,
            Iterable<Change>
{
    /**
     * Factory method.
     *
     * @return the created ChangeSet
     */
    public static ChangeSet create()
    {
        return new ChangeSet();
    }

    /**
     * Factory method creating a ChangeSet by copying the content of another ChangeSet.
     *
     * @param first the first component ChangeSet from which to create the new ChangeSet.
     * @return the created ChangeSet.
     */
    public static ChangeSet create(
            ChangeSet first )
    {
        ChangeSet ret = new ChangeSet();
        
        ret.theChanges.addAll( first.theChanges );
        return ret;
    }
    
    /**
     * Factory method creating a ChangeSet by concatenating two other ChangeSets
     * in sequence.
     *
     * @param first the first component ChangeSet from which to create the new ChangeSet.
     * @param second the second component ChangeSet from which to create the new ChangeSet.
     * @return the created ChangeSet.
     */
    public static ChangeSet create(
            ChangeSet first,
            ChangeSet second )
    {
        ChangeSet ret = new ChangeSet();
        
        ret.theChanges.addAll( first.theChanges );
        ret.theChanges.addAll( second.theChanges );
        return ret;
    }

    /**
     * Factory method creating a ChangeSet by concatenating several other ChangeSets
     * in sequence.
     *
     * @param components the component ChangeSets from which to create the new ChangeSet.
     * @return the created ChangeSet.
     */
    public static ChangeSet create(
            ChangeSet [] components )
    {
        ChangeSet ret = new ChangeSet();
        
        for( ChangeSet current : components ) {
            ret.theChanges.addAll( current.theChanges );
        }
        return ret;
    }

    /**
     * Private constructor, use factory method instead.
     */
    protected ChangeSet()
    {
        // noop
    }

    /**
     * Allow us to iterate over the Changes in this ChangeSet.
     *
     * @return Iterator over the Changes in this ChangeSet
     */
    public Iterator<Change> iterator()
    {
        return theChanges.iterator();
    }

    /**
     * Obtain the Changes in this set, in sequence.
     *
     * @return the Changes in this set
     */
    public Change [] getChanges()
    {
        return ArrayHelper.copyIntoNewArray( theChanges, Change.class );
    }

    /**
     * Obtain the number of Changes in the ChangeSet.
     *
     * @return the number of Changes in the ChangeSet
     */
    public int size()
    {
        return theChanges.size();
    }

    /**
      * Find out whether this change set is frozen or whether changes might
      * still be added to it. Being frozen generally indicates the end
      * of a Transaction.
      *
      * @return true if this ChangeSet is frozen
      */
    public boolean isFrozen()
    {
        return theIsFrozen;
    }

    /**
      * Freeze this ChangeSet, indicating that no more Changes can be added to it.
      */
    public void freeze()
    {
        theIsFrozen = true;
    }

    /**
     * Compact this ChangeSet, i.e. remove intermediate Changes that are overwritten
     * by other Changes later in the same ChangeSet.
     *
     * FIXME: currently, this method does nothing.
     */
    public void compact()
    {
    }

    /**
      * This allows additional Changes to be appended to the ChangeSet.
      *
      * @param newChange the Change to be added to the ChangeSet
      */
    public void addChange(
            Change newChange )
    {
        if( theIsFrozen ) {
            throw new IllegalStateException( "frozen change set" );
        }
        theChanges.add( newChange );
    }

    /**
      * This allows additional Changes to be appended to the ChangeSet.
      *
      * @param newChanges the ChangeSet of changes to be added to the ChangeSet
      */
    public void append(
            ChangeSet newChanges )
    {
        if( theIsFrozen ) {
            throw new IllegalStateException( "frozen change set" );
        }
        theChanges.addAll( newChanges.theChanges );
    }

    /**
     * Determine whether this ChangeSet is empty.
     *
     * @return true if this ChangeSet is empty
     */
    public boolean isEmpty()
    {
        return theChanges.isEmpty();
    }

    /**
     * Obtain in string format, for debugging.
     *
     * @return this instance in string format
     */
    @Override
    public String toString()
    {
        StringBuffer ret = new StringBuffer( "< " );
        ret.append( super.toString() );
        ret.append( "{ " );

        int i=0;
        for( Change current : theChanges ) {
            if( i++ > 0 ) {
                ret.append( ", " );
            }
            ret.append( current );
        }
        ret.append( "}>" );
        return ret.toString();
    }

    /**
      * The Changes in their original form.
      */
    protected ArrayList<Change> theChanges = new ArrayList<Change>();

    /**
      * Indicates whether this ChangeSet is frozen.
      */
    protected boolean theIsFrozen = false;
}
