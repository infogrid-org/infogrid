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

package org.infogrid.kernel.active.TEST.traversalpathset;

import org.infogrid.mesh.set.active.ActiveTraversalPathSet;
import org.infogrid.mesh.set.active.ActiveTraversalPathSetListener;
import org.infogrid.mesh.set.active.OrderedTraversalPathSetReorderedEvent;
import org.infogrid.mesh.set.active.TraversalPathAddedEvent;
import org.infogrid.mesh.set.active.TraversalPathRemovedEvent;
import org.infogrid.util.logging.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class is used to by the ActiveTraversalPathSetTests to receive callbacks
 */
class ActiveTraversalPathSetTestListener
        implements
            ActiveTraversalPathSetListener,
            PropertyChangeListener
{
    /**
    * Constructor that sets a listenerName and the RootEntityRoleMonitor to which this listener registers itself.
    */
    public ActiveTraversalPathSetTestListener(
            String                 listenerName,
            ActiveTraversalPathSet set,
            Log                    logger )
    {
        name   = listenerName;
        theSet = set;
        theSet.addWeakActiveTraversalPathSetListener( this );
        // theSet.addPropertyChangeListener( this );

        log = logger;

        reset();
    }

    /**
    * Obtain the number of addition events that we have received
    */
    public int getAddCounter()
    {
        return addCounter;
    }

    /**
    * Obtain the number of deletion events that we have received
    */
    public int getRemoveCounter()
    {
        return removeCounter;
    }

    /**
     * Obtain the number of reorder events that we have received
     */
    public int getReorderCounter()
    {
        return reorderCounter;
    }

    /**
     * Obtain the number of property change events that we have received
     */
    public int getPropertyCounter()
    {
        return propertyCounter;
    }

    /**
     * callback from the set
     */
    public void traversalPathAdded(
            TraversalPathAddedEvent event )
    {
        ++addCounter;
        log.debug( name + ": The traversalPathAdded method was called: " + event );
    }

    /**
     * callback from the set
     */
    public void traversalPathRemoved(
            TraversalPathRemovedEvent event )
    {
        ++removeCounter;
        log.debug( name + ": traversalPathRemoved method was called: " + event );
    }

    /**
     * callback from the set
     */
    public void orderedTraversalPathSetReordered(
            OrderedTraversalPathSetReorderedEvent event )
    {
        ++reorderCounter;
        log.debug( name + ": traversalPathSetReordered method was called: " + event );
    }

    /**
    * ActiveRootEntitySetListener interface method. This does nothing in this case.
    */
    public void propertyChange(
            PropertyChangeEvent event )
    {
        ++propertyCounter;
        log.debug( name + ": propertyChange was called: " + event );
    }

    /**
     * this resets all the counters
     */
    public void reset()
    {
        addCounter      = 0;
        removeCounter   = 0;
        propertyCounter = 0;
        reorderCounter  = 0;
    }

    /**
     * our name
     */
    String name;

    /**
     * the role monitor that we listen to
     */
    ActiveTraversalPathSet theSet;

    /**
     * the counter for our additions
     */
    private int addCounter;

    /**
     * the counter for our deletions
     */
    private int removeCounter;

    /**
     * the counter for our reorder events
     */
    private int reorderCounter;

    /**
     * the counter for property change events
     */
    private int propertyCounter;

    /**
     * the logger we log to
     */
    private Log log;
}
