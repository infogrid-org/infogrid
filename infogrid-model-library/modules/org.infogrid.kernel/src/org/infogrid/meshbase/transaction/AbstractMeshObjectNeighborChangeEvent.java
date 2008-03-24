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

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;

import org.infogrid.util.event.AbstractExternalizablePropertyChangeEvent;
import org.infogrid.util.event.UnresolvedException;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

/**
 * <p>This event indicates that the set of neighbor MeshObjects of a MeshObject has changed.</p>
 *
 * <p>This extends PropertyChangeEvent so we can keep the well-known JavaBeans
 * event generation model that programmers are used to.</p>
 */
public abstract class AbstractMeshObjectNeighborChangeEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject, MeshObjectIdentifier, RoleType[], MeshTypeIdentifier[], MeshObject[], MeshObjectIdentifier[]>
        implements
            MeshObjectNeighborChangeEvent
{
    private static final Log log = Log.getLogInstance(AbstractMeshObjectNeighborChangeEvent.class); // our own, private logger

    /**
     * Constructor.
     *
     * @param meshObject the MeshObject that is the source of the event (optional)
     * @param meshObjectIdentifier Identifier of the MeshObject that is the source of the event (required)
     * @param roleTypes the RoleTypes affected on the source MeshObject, with respect to the deltaNeighbors (optional)
     * @param roleTypeIdentifiers Identifiers of the RoleTypes affected on the source MeshObject, with respect to the deltaNeighbors (required)
     * @param oldNeighbors the set of neighbor MeshObjects prior to the event (optional)
     * @param oldNeighborIdentifiers the Identifiers of the neighbor MeshObjects prior to the event (required)
     * @param deltaNeighbors the set of neighbor MeshObjects affected by this event (optional)
     * @param deltaNeighborIdentifiers the Identifiers of the neighbor MeshObjects affected by this event (required)
     * @param newNeighbors the set of neighbor MeshObjects after the event (optional)
     * @param newNeighborIdentifiers the Identifiers of the neighbor MeshObjects after the event (required)
     * @param updateTime the time at which the change was made, in System.currentTimeMillis() format
     */
    protected AbstractMeshObjectNeighborChangeEvent(
            MeshObject              meshObject,
            MeshObjectIdentifier    meshObjectIdentifier,
            RoleType []             roleTypes,
            MeshTypeIdentifier[]    roleTypeIdentifiers,
            MeshObject []           oldNeighbors,
            MeshObjectIdentifier [] oldNeighborIdentifiers,
            MeshObject []           deltaNeighbors,
            MeshObjectIdentifier [] deltaNeighborIdentifiers,
            MeshObject []           newNeighbors,
            MeshObjectIdentifier [] newNeighborIdentifiers,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                roleTypes,
                roleTypeIdentifiers,
                oldNeighbors,
                oldNeighborIdentifiers,
                deltaNeighbors,
                deltaNeighborIdentifiers,
                newNeighbors,
                newNeighborIdentifiers,
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
     * Obtain the Identifier of the neighbor that changed.
     *
     * @return the Identifier of the neighbor that changed.
     */
    public MeshObjectIdentifier getNeighborMeshObjectIdentifier()
    {
        MeshObjectIdentifier [] delta = getDeltaValueIdentifier();
        if( delta.length == 1 ) {
            return delta[0];
        } else {
            log.error( "unexpected number of neighbors: " + ArrayHelper.join( ", ", delta ));
            return null;
        }
    }

    /**
     * Obtain the neighbor that changed.
     *
     * @return obtain the neighbor that changed.
     */
    public MeshObject getNeighborMeshObject()
    {
        MeshObject [] delta = getDeltaValue();
        if( delta.length == 1 ) {
            return delta[0];
        } else {
            log.error( "unexpected number of neighbors: " + ArrayHelper.join( ", ", delta ));
            return null;
        }
    }

    /**
     * Obtain the Identifiers of the affected RoleTypes.
     *
     * @return the Identifiers of the affected RoleTypes
     */
    public MeshTypeIdentifier[] getAffectedRoleTypeIdentifiers()
    {
        return super.getPropertyIdentifier();
    }

    /**
     * Obtain the affected RoleTypes.
     *
     * @return the RoleTypes
     */
    public RoleType [] getAffectedRoleTypes()
    {
        return getProperty();
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
    protected RoleType [] resolveProperty()
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Property( this );
        }
        
        MeshTypeIdentifier [] refs = getPropertyIdentifier();
        if( refs == null || refs.length == 0 ) {
            return new RoleType[0];
        }

        ModelBase   modelBase = theResolver.getModelBase();
        RoleType [] ret       = new RoleType[ refs.length ];

        for( int i=0 ; i<ret.length ; ++i ) {
            try {
                ret[i] = modelBase.findRoleTypeByIdentifier( refs[i] );

            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                throw new UnresolvedException.Property( this, ex );
            }
        }
        return ret;
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
                    "getPropertyIdentifier()",
                    "getDeltaValueIdentifier()",
                    "getTimeOccured()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    getPropertyIdentifier(),
                    getDeltaValueIdentifier(),
                    getTimeEventOccurred()
                });
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected transient MeshBase theResolver;
}
