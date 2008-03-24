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
import org.infogrid.util.event.AbstractExternalizableEvent;
import org.infogrid.util.event.UnresolvedException;

/**
  * This is the abstract supertype for all events indicating
  * a lifecycle event in the life of a MeshObject.
  */
public abstract class AbstractMeshObjectLifecycleEvent
        extends
            AbstractExternalizableEvent<MeshBase, MeshBase, MeshObject, MeshObjectIdentifier>
        implements
            Change<MeshBase,MeshBase,MeshObject,MeshObjectIdentifier>
{
    /**
      * Private constructor, use subclasses.
      *
      * @param meshBase the MeshBase that sent out this event
      * @param canonicalMeshObjectName the canonical Identifier of the MeshObject that experienced a lifecycle event
      * @param meshObject the MeshObject that experienced a lifecycle event
      */
    protected AbstractMeshObjectLifecycleEvent(
            MeshBase       meshBase,
            MeshObject     meshObject,
            MeshObjectIdentifier canonicalMeshObjectName,
            long           updateTime )
    {
        super( meshBase, null, meshObject, canonicalMeshObjectName, updateTime );
    }

    /**
     * Obtain the Identifier of the MeshObject affected by this Change.
     *
     * @return the Identifier of the MeshObject affected by this Change
     */
    public final MeshObjectIdentifier getAffectedMeshObjectIdentifier()
    {
        return getDeltaValueIdentifier();
    }

    /**
     * Obtain the MeshObject affected by this Change.
     *
     * @return obtain the MeshObject affected by this Change
     */
    public MeshObject getAffectedMeshObject()
    {
        return getDeltaValue();
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
    protected MeshBase resolveSource()
    {
        return getSourceIdentifier();
    }

    /**
     * Resolve a value of the event.
     *
     * @param vid the value identifier
     * @return a value of the event
     */
    protected MeshObject resolveValue(
            MeshObjectIdentifier vid )
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Value( this );
        }
        MeshObject ret = theResolver.findMeshObjectByIdentifier( vid );
        return ret;
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected transient MeshBase theResolver;
}
