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

package org.infogrid.kernel.active.TEST.objectset;

import org.infogrid.mesh.set.active.ActiveMeshObjectSet;
import org.infogrid.mesh.set.active.ActiveMeshObjectSetListener;
import org.infogrid.mesh.set.active.MeshObjectAddedEvent;
import org.infogrid.mesh.set.active.MeshObjectRemovedEvent;
import org.infogrid.mesh.set.active.OrderedActiveMeshObjectSetReorderedEvent;
import org.infogrid.meshbase.transaction.MeshObjectNeighborChangeEvent;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleChangeEvent;
import org.infogrid.util.logging.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 *
 */
public class ActiveMeshObjectSetTestListener
        implements
            ActiveMeshObjectSetListener,
            PropertyChangeListener
{
    /**
    * Constructor that sets a listenerName and the ActiveMeshObjectSet to which this listener registers itself.
    */
    public ActiveMeshObjectSetTestListener(
            String              listenerName,
            ActiveMeshObjectSet set,
            Log                 logger )
    {
        name   = listenerName;
        theSet = set;
        theSet.addWeakActiveMeshObjectSetListener( this );
        theSet.addWeakContentPropertyChangeListener( this );

        log = logger;

        reset();
    }

    /**
    * Obtain the number of addition events that we have received
    */
    public int getAddCounter()
    {
        return addedEvents.size();
    }

    /**
    * Obtain the number of deletion events that we have received
    */
    public int getRemoveCounter()
    {
        return removedEvents.size();
    }

    /**
     * Obtain the number of PropertyChangeEvents that we have received
     */
    public int getPropertyChangesCounter()
    {
        return propertyChangeEvents.size();
    }

    /**
     * Obtain the number of RoleChangeEvents that we have received
     */
    public int getRoleChangesCounter()
    {
        return roleChangeEvents.size();
    }

    /**
     * ActiveMeshObjectSetListener interface method that gets called by the
     * ActiveMeshObjectSet whenever an add happens.
     */
    public void meshObjectAdded(
            MeshObjectAddedEvent event )
    {
        addedEvents.add( event );
        log.info(
                name
                + ": The meshObjectAdded method was called: "
//                + event
//                + ", added name: "
                + event.getAddedMeshObject() );
    }

    /**
     * ActiveMeshObjectSetListener interface method that gets called by the
     * ActiveMeshObjectSet whenever an remove happens.
     */
    public void meshObjectRemoved(
            MeshObjectRemovedEvent event )
    {
        removedEvents.add( event );
        log.info(
                name
                + ": the meshObjectRemoved method was called: "
//                + event
//                + ", removed name: "
                + event.getRemovedMeshObject() );
    }

    /**
    * ActiveRootEntitySetListener interface method.
    */
    public void propertyChange(
            PropertyChangeEvent event )
    {
        if( event instanceof MeshObjectPropertyChangeEvent ) {
            propertyChangeEvents.add( (MeshObjectPropertyChangeEvent) event );
        } else if( event instanceof MeshObjectRoleChangeEvent ) {
            roleChangeEvents.add( (MeshObjectRoleChangeEvent) event );
        } else if( event instanceof MeshObjectNeighborChangeEvent ) {
            // noop
        } else {
            log.error( "unexpected event: " + event );
        }

        log.info(name + ": propertyChange was called: " + event );
    }

    /**
     * ActiveMeshObjectSetListener interface method.
     */
    public void orderedMeshObjectSetReordered(
            OrderedActiveMeshObjectSetReorderedEvent event )
    {
        log.info( name + ": orderedMeshObjectSetReordered was called: " + event );
    }

    /**
     * this resets all the counters
     */
    public void reset()
    {
        addedEvents          = new ArrayList<MeshObjectAddedEvent>();
        removedEvents        = new ArrayList<MeshObjectRemovedEvent>();
        propertyChangeEvents = new ArrayList<MeshObjectPropertyChangeEvent>();
        roleChangeEvents     = new ArrayList<MeshObjectRoleChangeEvent>();
    }

    /**
     * Our name
     */
    String name;

    /**
     * The ActiveMeshObjectSet that we listen to
     */
    ActiveMeshObjectSet theSet;

    /**
     * we store all added events here
     */
    private ArrayList<MeshObjectAddedEvent> addedEvents;

    /**
     * we store all removed events here
     */
    private ArrayList<MeshObjectRemovedEvent> removedEvents;

    /**
     * we store all property update events here
     */
    private ArrayList<MeshObjectPropertyChangeEvent> propertyChangeEvents;

    /**
     * we store all role player update events here
     */
    private ArrayList<MeshObjectRoleChangeEvent> roleChangeEvents;

    /**
     * the logger we log to
     */
    private Log log;
}
