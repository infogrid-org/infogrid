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

package org.infogrid.jee.viewlet.meshbase;

import org.infogrid.context.Context;
import org.infogrid.jee.viewlet.AbstractCursorIterableViewlet;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.util.CursorIterator;

/**
 * A Viewlet that shows all MeshObjects in a MeshBase.
 */ 
public class AllMeshObjectsViewlet
        extends
            AbstractCursorIterableViewlet<MeshObject>
{
    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created AllMeshObjectsViewlet
     */
    public static AllMeshObjectsViewlet create(
            Context c )
    {
        return new AllMeshObjectsViewlet( c );
    }

    /**
     * Constructor.
     *
     * @param c the application context
     */
    protected AllMeshObjectsViewlet(
            Context c )
    {
        super( c );
    }

    /**
     * Obtain the MeshObjectSet to display
     *
     * @return the MeshObjectSet
     */
    public CursorIterator<MeshObject> getCursorIterator()
    {
        IterableMeshBase meshBase = (IterableMeshBase) theSubject.getMeshBase(); // derive from the subject, so we can do any MeshBase
        
        return meshBase.iterator();
    }
}
