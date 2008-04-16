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

import org.infogrid.model.primitives.RoleType;

import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;
import org.infogrid.modelbase.ModelBase;

import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.util.event.AbstractExternalizablePropertyChangeEvent;
import org.infogrid.util.event.UnresolvedException;
import org.infogrid.util.StringHelper;

/**
 * <p>This indicates a change in a MeshObject's participation in a role of
 * a certain RoleType. In other words, a relationship between the MeshObject and
 * another MeshObject was blessed or unblessed.</p>
 *
 * <p>This extends PropertyChangeEvent so we can keep the well-known JavaBeans
 * event generation model that programmers are used to.</p>
 */
public abstract class AbstractMeshObjectRoleChangeEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject, MeshObjectIdentifier, String, String, RoleType[], MeshTypeIdentifier[]>
        implements
            MeshObjectRoleChangeEvent
{
    /**
     * Constructor.
     * 
     * @param source the MeshObject that is the source of the event
     * @param sourceIdentifier the identifier representing the source MeshObject of the event
     * @param oldValues the old values of the RoleType, prior to the event
     * @param oldValueIdentifiers the identifier representing the old values of the RoleType, prior to the event
     * @param deltaValues the RoleTypes that changed
     * @param deltaValueIdentifiers the identifiers of the RoleTypes that changed
     * @param newValues the new values of the RoleType, after the event
     * @param newValueIdentifiers the identifier representing the new values of the RoleType, after the event
     * @param neighbor the MeshObject that identifies the other end of the affected relationship
     * @param neighborIdentifier the identifier representing the MeshObject that identifies the other end of the affected relationship
     * @param timeEventOccurred the time at which the event occurred, in <code>System.currentTimeMillis</code> format
     */
    protected AbstractMeshObjectRoleChangeEvent(
            MeshObject            source,
            MeshObjectIdentifier  sourceIdentifier,
            RoleType []           oldValues,
            MeshTypeIdentifier [] oldValueIdentifiers,
            RoleType []           deltaValues,
            MeshTypeIdentifier [] deltaValueIdentifiers,
            RoleType []           newValues,
            MeshTypeIdentifier [] newValueIdentifiers,
            MeshObject            neighbor,
            MeshObjectIdentifier  neighborIdentifier,
            long                  timeEventOccurred )
    {
        super(  source,
                sourceIdentifier,
                MeshObject._MESH_OBJECT_ROLES_PROPERTY,
                MeshObject._MESH_OBJECT_ROLES_PROPERTY,
                oldValues,
                oldValueIdentifiers,
                deltaValues,
                deltaValueIdentifiers,
                newValues,
                newValueIdentifiers,
                timeEventOccurred );

        theNeighbor             = neighbor;
        theNeighborIdentifier = neighborIdentifier;
    }

   /**
     * Obtain the identifier of the MeshObject affected by this Change.
     *
     * @return the identifier of the MeshObject affected by this Change
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
     * Obtain the identifier of the neighbor MeshObject that identifies the relationship affected by this Change.
     *
     * @return the identifier of the neighbor MeshObject that identifies the relationship affected by this Change
     */
    public MeshObjectIdentifier getNeighborMeshObjectIdentifier()
    {
        return theNeighborIdentifier;
    }

    /**
     * Obtain the neighbor MeshObject that identifies the relationship affected by this Change.
     *
     * @return obtain the neighbor MeshObject that identifies the relationship affected by this Change
     */
    public synchronized MeshObject getNeighborMeshObject()
    {
        if( theNeighbor == null ) {
            theNeighbor = resolveNeighbor();
        }
        return theNeighbor;
    }

    /**
     * Obtain the identifiers of the affected RoleTypes.
     *
     * @return the identifiers of the affected RoleTypes
     */
    public MeshTypeIdentifier [] getAffectedRoleTypeIdentifiers()
    {
        return getDeltaValueIdentifier();
    }

    /**
     * Obtain the affected RoleTypes.
     *
     * @return the RoleTypes
     */
    public RoleType [] getAffectedRoleTypes()
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
     * Resolve the neighbor MeshObject.
     *
     * @return the neighbor MeshObject
     */
    protected MeshObject resolveNeighbor()
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Other( this );
        }
        
        MeshObject ret = theResolver.findMeshObjectByIdentifier( getNeighborMeshObjectIdentifier() );
        return ret;
    }

    /**
     * Resolve a value of the event.
     *
     * @param vid the value identifier
     * @return a value of the event
     */
    protected RoleType [] resolveValue(
            MeshTypeIdentifier [] vid )
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Value( this );
        }
        if( vid == null || vid.length == 0 ) {
            return new RoleType[0];
        }

        RoleType [] ret       = new RoleType[ vid.length ];
        ModelBase   modelBase = theResolver.getModelBase();

        for( int i=0 ; i<vid.length ; ++i ) {
            try {
                ret[i] = modelBase.findRoleTypeByIdentifier( vid[i] );

            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                throw new UnresolvedException.Value( this, ex );
            }
        }
        return ret;
    }

    /**
     * Clear cached objects to force a re-resolve.
     */
    @Override
    protected void clearCachedObjects()
    {
        theNeighbor = null;

        super.clearCachedObjects();
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
                    "getPropertyIdentifier()",
                    "theNeighborIdentifier",
                    "theResolver",
                    "getDeltaValueIdentifier()",
                    "getTimeEventOccurred()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    getPropertyIdentifier(),
                    theNeighborIdentifier,
                    theResolver,
                    getDeltaValueIdentifier(),
                    getTimeEventOccurred()
                });
    }
    
    /**
     * The resolver of identifiers carried by this event.
     */
    protected transient MeshBase theResolver;
    
    /**
     * Identifier of the neighbor MeshObject.
     */
    protected MeshObjectIdentifier theNeighborIdentifier;
    
    /**
     * Neighbor MeshObject.
     */
    protected transient MeshObject theNeighbor;
}
