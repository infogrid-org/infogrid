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

import org.infogrid.mesh.MeshObject;
import org.infogrid.util.StringHelper;

/**
  * This indicates that a MeshObject was removed from an ActiveMeshObjectSet.
  */
public class MeshObjectRemovedEvent
        extends
            ActiveMeshObjectSetEvent
{
    /**
      * Constructor.
      *
      * @param _theSet the set that changed
      * @param _theRemovedEntity the MeshObject that was removed from the set
      */
    public MeshObjectRemovedEvent(
            ActiveMeshObjectSet _theSet,
            MeshObject          _theRemovedMeshObject )
    {
        super(_theSet);

        theRemovedMeshObject = _theRemovedMeshObject;
        theIndexOfRemoved    = -1;
    }

    /**
      * Constructor.
      *
      * @param _theSet the set that changed,
      * @param _theRemovedEntity the Entity that was removed from the set
      * @param _indexOfRemoved the index in the set from which the Entity was removed
      */
    public MeshObjectRemovedEvent(
            ActiveMeshObjectSet _theSet,
            MeshObject          _theRemovedMeshObject,
            int                 _indexOfRemoved )
    {
        super(_theSet);

        theRemovedMeshObject = _theRemovedMeshObject;
        theIndexOfRemoved    = _indexOfRemoved;
    }

    /**
      * Obtain the removed MeshObject.
      *
      * @return the removed MeshObject
      */
    public MeshObject getRemovedMeshObject()
    {
        return theRemovedMeshObject;
    }

    /**
     * Obtain the index at which the removed MeshObject used to live before it was
     * removed from the set. This applies only to ordered sets.
     *
     * @return the index at which the removed MeshObject used to live before it was removed
     * @throws IllegalStateException thrown if applied on an unordered set
     */
    public int getIndexOfRemoved()
    {
        if( theIndexOfRemoved == -1 ) {
            throw new IllegalStateException( "unordered sets do not have indices" );
        }
        return theIndexOfRemoved;
    }

    /**
     * Obtain string form, for debugging.
     *
     * @return this object in string form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theRemovedMeshObject"
                },
                new Object[] {
                    theRemovedMeshObject
                });
    }

    /**
      * The removed MeshObject.
      */
    protected MeshObject theRemovedMeshObject;

    /**
     * The index from which the MeshObject was removed.
     */
    protected int theIndexOfRemoved;
}
