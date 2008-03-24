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

import org.infogrid.meshbase.MeshBase;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;
import org.infogrid.modelbase.ModelBase;

import org.infogrid.util.event.AbstractExternalizablePropertyChangeEvent;
import org.infogrid.util.event.UnresolvedException;
import org.infogrid.util.logging.Log;

/**
 * This event indicates that a MeshObject has changed its type, i.e. by supporting
 * one more or one less MeshType.
 */
public abstract class AbstractMeshObjectTypeChangeEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject,MeshObjectIdentifier,String,String,EntityType[],MeshTypeIdentifier[]>
        implements
            MeshObjectTypeChangeEvent
{
    private static final Log log = Log.getLogInstance( AbstractMeshObjectTypeChangeEvent.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param meshObject the MeshObject whose type changed
     * @param propertyName the name of the property that changed according to the API of the PropertyChangeEvent superclass
     * @param oldValue the old set of types
     * @param newValue the new set of types
     * @param updateTime the time the MeshObject was updated
     */
    protected AbstractMeshObjectTypeChangeEvent(
            MeshObject           meshObject,
            MeshObjectIdentifier meshObjectIdentifier,
            EntityType []        oldValue,
            MeshTypeIdentifier[] oldValueIdentifiers,
            EntityType []        deltaValue,
            MeshTypeIdentifier[] deltaValueIdentifiers,
            EntityType []        newValue,
            MeshTypeIdentifier[] newValueIdentifiers,
            long                 updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                MeshObject._MESH_OBJECT_TYPES_PROPERTY,
                MeshObject._MESH_OBJECT_TYPES_PROPERTY,
                oldValue,
                oldValueIdentifiers,
                deltaValue,
                deltaValueIdentifiers,
                newValue,
                newValueIdentifiers,
                updateTime );

        if( log.isDebugEnabled() ) {
            log.debug( "created " + this );
        }
    }

    /**
     * Obtain the Identifier of the MeshObject affected by this Change.
     *
     * @return the Identifier of the MeshObject affected by this Change
     */
    public final MeshObjectIdentifier getAffectedMeshObjectIdentifier()
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
     * Obtain the Identifiers of the EntityTypes involved in this Change.
     *
     * @return the Identifiers of the EntityTypes involved in this Change.
     */
    public final MeshTypeIdentifier [] getEntityTypeIdentifiers()
    {
        return getDeltaValueIdentifier();
    }

    /**
     * Obtain the EntityTypes involved in this Change.
     *
     * @return obtain the EntityTypes involved in this Change.
     */
    public EntityType [] getEntityTypes()
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
    protected EntityType [] resolveValue(
            MeshTypeIdentifier [] vid )
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Source( this );
        }
        if( vid == null || vid.length == 0 ) {
            return new EntityType[0];
        }

        ModelBase         modelBase = theResolver.getModelBase();
        EntityType []     ret       = new EntityType[ vid.length ];
        
        for( int i=0 ; i<ret.length ; ++i ) {
            try {
                ret[i] = modelBase.findEntityTypeByIdentifier( vid[i] );

            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                throw new UnresolvedException.Value( this, ex );
            }
        }
        return ret;
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected transient MeshBase theResolver;
}
