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
import org.infogrid.util.StringHelper;

/**
  * <p>This event indicates that a MeshObject's set of equivalent MeshObjects have changed.</p>
  */
public abstract class AbstractMeshObjectEquivalentsChangeEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject, MeshObjectIdentifier, String, String, MeshObject[], MeshObjectIdentifier[]>
        implements
            MeshObjectEquivalentsChangeEvent
{
    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose equivalents changed
     * @param deltaEquivalents the Identifiers of the equivalents that changed
     * @param newValue the Identifiers of the new set of equivalents
     * @param updateTime the time at which the change occurred
     */
    protected AbstractMeshObjectEquivalentsChangeEvent(
            MeshObject              meshObject,
            MeshObjectIdentifier    meshObjectIdentifier,
            MeshObject []           oldEquivalents,
            MeshObjectIdentifier [] oldEquivalentIdentifiers,
            MeshObject []           deltaEquivalents,
            MeshObjectIdentifier [] deltaEquivalentIdentifiers,
            MeshObject []           newEquivalents,
            MeshObjectIdentifier [] newEquivalentIdentifiers,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                MeshObject._MESH_OBJECT_EQUIVALENTS_PROPERTY,
                MeshObject._MESH_OBJECT_EQUIVALENTS_PROPERTY,
                oldEquivalents,
                oldEquivalentIdentifiers,
                deltaEquivalents,
                deltaEquivalentIdentifiers,
                newEquivalents,
                newEquivalentIdentifiers,
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
     * Determine whether this is an addition or a removal.
     *
     * @return true if this is an addition
     */
    public abstract boolean isAdditionalEquivalentsUpdate();

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
        return getPropertyIdentifier();
    }

    /**
     * Resolve a value of the event.
     *
     * @param vid the value identifier
     * @return a value of the event
     */
    protected MeshObject [] resolveValue(
            MeshObjectIdentifier[] vid )
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Value( this );
        }
        MeshObject [] ret = new MeshObject[ vid.length ];

        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = theResolver.findMeshObjectByIdentifier( vid[i] );
        }
        return ret;
    }

    /**
     * Return in string form, for debugging.
     *
     * @return this instance in string form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "getSourceIdentifier()",
                    "getDeltaValueIdentifier()",
                    "getTimeOccured()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    getDeltaValueIdentifier(),
                    getTimeEventOccurred()
                });
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected MeshBase theResolver;
}
