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

package org.infogrid.mesh.set.active;

import org.infogrid.model.traversal.TraversalPath;

import org.infogrid.util.StringHelper;

/**
  * This event indicates that a TraversalPath instance was removed to a TraversalPathSet.
  */
public class TraversalPathRemovedEvent
        extends
            ActiveTraversalPathSetEvent
{
    /**
      * Construct one.
      *
      * @param _theSet the set that changed
      * @param _theRemovedTraversalPath the TraversalPath that was removed from the set
      */
    public TraversalPathRemovedEvent(
            ActiveTraversalPathSet _theSet,
            TraversalPath          _theRemovedTraversalPath )
    {
        super(_theSet);

        theRemovedIndex         = -1;
        theRemovedTraversalPath = _theRemovedTraversalPath;
    }

    /**
      * Construct one, specifying the index of the TraversalPath that was removed.
      * Given that only ordered set define the notion of an index into the set, this
      * shall only be used for ordered TraversalPathSets.
      *
      * @param _theSet the set that changed
      * @param _removedIndex the index of the TraversalPath that was removed
      * @param _theRemovedTraversalPath the TraversalPath that was removed from the set
      */
    public TraversalPathRemovedEvent(
            ActiveTraversalPathSet _theSet,
            int                    _removedIndex,
            TraversalPath          _theRemovedTraversalPath )
    {
        super(_theSet);

        theRemovedIndex         = _removedIndex;
        theRemovedTraversalPath = _theRemovedTraversalPath;
    }

    /**
     * Obtain the index of the TraversalPath in the set before it was removed.
     *
     * @return the index of the TraversalPath in th set before it was removed
     */
    public int getRemovedTraversalPathIndex()
    {
        return theRemovedIndex;
    }

    /**
      * Obtain the removed TraversalPath.
      *
      * @return the removed TraversalPath
      */
    public TraversalPath getRemovedTraversalPath()
    {
        return theRemovedTraversalPath;
    }

    /**
     * Obtain string representation, for debugging.
     *
     * @return a string representation of this instance
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "removed",
                    "index"
                },
                new Object[] {
                    theRemovedTraversalPath,
                    theRemovedIndex
                });
    }

    /**
     * The index of the TraversalPath that was removed, before it was removed.
     */
    protected int theRemovedIndex;

    /**
      * The removed TraversalPath.
      */
    protected TraversalPath theRemovedTraversalPath;
}
