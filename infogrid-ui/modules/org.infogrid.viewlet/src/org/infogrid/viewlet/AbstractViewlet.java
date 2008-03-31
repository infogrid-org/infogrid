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

package org.infogrid.viewlet;

import org.infogrid.context.Context;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;

/**
 * Factors out commonly used functionality for Viewlets.
 */
public abstract class AbstractViewlet
        implements
            Viewlet
{
    /**
     * Constructor, for subclasses only.
     * 
     * @param c the application context
     */
    protected AbstractViewlet(
            Context c )
    {
        theContext = c;
    }
    
    /**
     * Obtain a String, to be shown to the user, that identifies this Viewlet to the user.
     * This default implementation can be overridden by subclasses.
     *
     * @return a String
     */
    public String getUserVisibleName()
    {
        return getClass().getName();
    }

    /**
      * The Viewlet is being instructed to view certain objects, which are packaged as MeshObjectsToView.
      *
      * @param toView the MeshObjects to view
      * @throws CannotViewException thrown if this Viewlet cannot view these MeshObjects
      */
    public void view(
            MeshObjectsToView toView )
        throws
            CannotViewException
    {
        theViewedMeshObjects.updateFrom( toView );
    }
    
    /**
      * Set the REST-ful subject for this Viewlet. This is a simplified version of {@link #view( MeshObjectsToView )}.
      *
      * @param toView the MeshObject to view
      * @throws CannotViewException thrown if this Viewlet cannot view this MeshObject
      */
    public void setSubject(
            MeshObject toView )
        throws
            CannotViewException
    {
        view( MeshObjectsToView.create( toView ));
    }
    
    /**
     * Obtain the REST-ful subject.
     *
     * @return the subject
     */
    public MeshObject getSubject()
    {
        return theViewedMeshObjects.getSubject();
    }

    /**
     * Obtain the Objects.
     * 
     * @return the set of Objects, which may be empty
     */
    public MeshObjectSet getObjects()
    {
        return theViewedMeshObjects.getObjects();
    }

    /**
      * Obtain the MeshObjects that this Viewlet is currently viewing, plus
      * context information. This method will return the same instance of ViewedMeshObjects
      * during the lifetime of the Viewlet.
      *
      * @return the ViewedMeshObjects
      */
    public ViewedMeshObjects getViewedObjects()
    {
        return theViewedMeshObjects;
    }
    
    /**
     * Obtain the application context.
     *
     * @return the application context
     */
    public final Context getContext()
    {
        return theContext;
    }

    /**
     * The objects being viewed.
     */
    protected DefaultViewedMeshObjects theViewedMeshObjects = new DefaultViewedMeshObjects( this );

    /**
     * The application context
     */
    protected Context theContext;
}
