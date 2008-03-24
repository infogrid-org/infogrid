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

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.meshbase.MeshBase;

import org.infogrid.util.event.AbstractExternalizablePropertyChangeEvent;
import org.infogrid.util.event.UnresolvedException;

/**
  * This indicates a change in the state of a MeshObject, such as
  * "the MeshObject died". Subclasses implement specific state changes.
  */
public abstract class MeshObjectStateEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject, MeshObjectIdentifier, String, Void, MeshObjectStateEvent.MeshObjectState, String>
        implements
            Change<MeshObject,MeshObjectIdentifier,MeshObjectStateEvent.MeshObjectState,String>
{
    /**
      * Constructor.
      *
      * @param canonicalMeshObjectName the canonical name of the MeshObject whose state changed. We pass this in because
      *        case of a dead MeshObject, we might not be able to call getIdentifier() any more. (FIXME? Is that still right?)
      * @param meshObject the MeshObject whose state changed
      * @param oldValue the old state prior to the Change
      * @param newValue the new state after the Change
      */
    protected MeshObjectStateEvent(
            MeshObject      meshObject,
            MeshObjectIdentifier meshObjectIdentifier,
            MeshObjectState oldValue,
            MeshObjectState newValue,
            long            updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                MeshObject._MESH_OBJECT_STATE_PROPERTY,
                null,
                oldValue,
                oldValue.toString(),
                newValue, // delta = new
                newValue.toString(),
                newValue,
                newValue.toString(),
                updateTime );
    }
    
    /**
     * Obtain the Identifier of the MeshObject affected by this Change.
     *
     * @return the Identifier of the MeshObject affected by this Change
     */
    public MeshObjectIdentifier getAffectedMeshObjectIdentifier()
    {
        return getSourceIdentifier();
    }

    /**
     * Obtain the MeshObject affected by this Change.
     *
     * @return obtain the MeshObject affected by this Change
     */
    public MeshObject getAffectedMeshObject()
    {
        return getSource();
    }

    /**
     * Set the MeshBase that can resolve the identifiers carried by this event.
     *
     * @param mb the MeshBase
     */
    public void setResolver(
            MeshBase mb )
    {
        theResolver = mb;
        clearCachedObjects();
    }

    /**
     * Resolve the source of the event.
     *
     * @return the source of the event
     */
    protected MeshObject resolveSource()
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Source( this );
        }
        
        MeshObject ret = theResolver.findMeshObjectByIdentifier( getSourceIdentifier() );
        return ret;
    }

    /**
     * Resolve the property of the event.
     *
     * @return the property of the event
     */
    protected String resolveProperty()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected MeshBase theResolver;

    /**
     * Common super-interface for all MeshObjectStates.
     */
    public static interface MeshObjectState
    {}
}
