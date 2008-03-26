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

import java.util.Map;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.util.StringHelper;

/**
 * This is the default implementation of ViewedMeshObjects. This is useful for
 * a wide variety of Viewlets.
 */
public class DefaultViewedMeshObjects
        implements
            ViewedMeshObjects
{
    /**
      * Constructor. Initializes to empty content.
      *
      * @param v the Viewlet that we belong to.
      */
    public DefaultViewedMeshObjects(
            Viewlet v )
    {
        theViewlet = v;
    }

    /**
     * Through this method, the Viewlet that this object belongs to updates this object.
     *
     * @param subject the new subject of the Viewlet
     * @param subjectParameters the parameters of the newly selected subject, if any
     * @param viewletParameters the parameters of the Viewlet, if any
     */
    public void update(
            MeshObject             subject,
            Map<String,Object>     subjectParameters,
            Map<String,Object>     viewletParameters,
            TraversalSpecification traversal )
    {
        theSubject                = subject;
        theSubjectParameters      = subjectParameters;
        theViewletParameters      = viewletParameters;
        theTraversalSpecification = traversal;
    }

    /**
     * Through this convenience method, the Viewlet that this object belongs to updates this object.
     *
     * @param newObjectsToView the new objects accepted to be viewed by the Viewlet
     */
    public void updateFrom(
            MeshObjectsToView newObjectsToView )
    {
        update( newObjectsToView.getSubject(),
                newObjectsToView.getSubjectParameters(),
                newObjectsToView.getViewletParameters(),
                newObjectsToView.getTraversalSpecification() );
    }

    /**
     * Obtain the current subject of the Viewlet. As long as the Viewlet
     * displays any information whatsoever, this is non-null.
     *
     * @return the subject MeshObject
     */
    public final MeshObject getSubject()
    {
        return theSubject;
    }

    /**
     * Obtain the parameters for the subject.
     *
     * @return the parameters for the subject, if any.
     */
    public Map<String,Object> getSubjectParameters()
    {
        return theSubjectParameters;
    }

    /**
      * Obtain the Viewlet by which these objects are viewed.
      *
      * @return the Viewlet by which these objects are viewed.
      */
    public Viewlet getViewlet()
    {
        return theViewlet;
    }

    /**
     * Obtain the parameters of the viewing Viewlet.
     *
     * @return the parameters of the viewing Viewlet, if any.
     */
    public Map<String,Object> getViewletParameters()
    {
        return theViewletParameters;
    }

    /**
     * Obtain the TraversalSpecification that the Viewlet currently uses.
     * 
     * @eturn the TraversalSpecification that the Viewlet currently uses
     */
    public TraversalSpecification getTraversalSpecification()
    {
        return theTraversalSpecification;
    }

    /**
     * Obtain the Objects, i.e. the MeshObjects reached by traversing from the
     * Subject via the TraversalSpecification.
     * 
     * @return the Objects
     */
    public MeshObjectSet getObjects()
    {
        if( theObjects == null ) {
            if( theTraversalSpecification != null ) {
                theObjects = theSubject.traverse( theTraversalSpecification );
            } else {
                theObjects = theSubject.getMeshBase().getMeshObjectSetFactory().obtainEmptyImmutableMeshObjectSet();
            }
        }
        return theObjects;
    }
    
    /**
     * Convert to String, for debugging.
     *
     * @return String representation of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String [] {
                    "subject",
                    "viewlet",
                    "onjects" },
                new Object [] {
                    theSubject,
                    theViewlet,
                    theObjects} );
    }

    /**
     * The Viewlet that this object belongs to.
     */
    protected Viewlet theViewlet;

    /**
     * The current subject of the Viewlet.
     */
    protected MeshObject theSubject;

    /**
     * The parameters for the subject, if any.
     */
    protected Map<String,Object> theSubjectParameters;

    /**
     * The Viewlet parameters, if any.
     */
    protected Map<String,Object> theViewletParameters;
    
    /**
     * The TraversalSpecification, if any.
     */
    protected TraversalSpecification theTraversalSpecification;

    /**
     * The set of Objects.
     */
    protected MeshObjectSet theObjects;
}
