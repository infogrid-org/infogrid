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
import org.infogrid.mesh.externalized.ExternalizedMeshObject;

import org.infogrid.meshbase.MeshBase;

import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;

import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;

import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.util.event.UnresolvedException;
import org.infogrid.util.logging.Log;
import org.infogrid.util.StringHelper;

/**
 * This event indicates that a new MeshObject was created. If the MeshObject was created
 * and blessed at the same time, only this event (and no PropertyChangeEvents reflecting the blessing)
 * will be sent.
 */
public class MeshObjectCreatedEvent
        extends
            AbstractMeshObjectLifecycleEvent
{
    private static final long serialVersionUID = 1l; // helps with serialization
    private static final Log  log              = Log.getLogInstance( MeshObjectCreatedEvent.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param source the MeshBase that is the source of the event
     * @param sourceIdentifier the MeshBaseIdentifier representing the source of the event
     * @param createdObject the MeshObject that was created
     * @param timeEventOccurred the time at which the event occurred, in <code>System.currentTimeMillis</code> format
     */
    public MeshObjectCreatedEvent(
            MeshBase           source,
            MeshBaseIdentifier sourceIdentifier,
            MeshObject         createdObject,
            long               timeEventOccurred )
    {
        super(  source,
                sourceIdentifier,
                createdObject,
                createdObject.getIdentifier(),
                timeEventOccurred );
        
        theExternalizedMeshObject = createdObject.asExternalized();
    }

    /**
     * Constructor.
     *
     * @param source the MeshBase that is the source of the event
     * @param sourceIdentifier the MeshBaseIdentifier representing the source of the event
     * @param createdObject the MeshObject that was created
     * @param timeEventOccurred the time at which the event occurred, in <code>System.currentTimeMillis</code> format
     */
    public MeshObjectCreatedEvent(
            MeshBase               source,
            MeshBaseIdentifier     sourceIdentifier,
            ExternalizedMeshObject createdObject,
            long                   timeEventOccurred )
    {
        super(  source,
                sourceIdentifier,
                null,
                createdObject.getIdentifier(),
                timeEventOccurred );
        
        theExternalizedMeshObject = createdObject;
    }

    /**
     * Obtain the ExternalizedMeshObject that captures the newly created MeshObject.
     * 
     * @return the ExternalizedMeshObject
     */
    public ExternalizedMeshObject getExternalizedMeshObject()
    {
        return theExternalizedMeshObject;
    }

    /**
     * Obtain the EntityTypes with which the created MeshObject was blessed upon creation.
     *
     * @return the MeshTypes
     */
    public synchronized EntityType [] getEntityTypes()
    {
        if( theEntityTypes == null ) {
            theEntityTypes = resolveEntityTypes();
        }
        return theEntityTypes;
    }

    /**
     * Resolve the EntityTypes.
     *
     * @return the resolved EntityTypes
     */
    protected EntityType [] resolveEntityTypes()
    {
        MeshTypeIdentifier [] typeNames = theExternalizedMeshObject.getExternalTypeIdentifiers();
        if( typeNames == null || typeNames.length == 0 ) {
            return new EntityType[0];
        }
        EntityType [] ret = new EntityType[ typeNames.length ];
        if( theResolver == null ) {
            throw new UnresolvedException.Other( this );
        }

        ModelBase modelBase = theResolver.getModelBase();
        for( int i=0 ; i<typeNames.length ; ++i ) {
            try {
                ret[i] = modelBase.findEntityTypeByIdentifier( typeNames[i] );

            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                throw new UnresolvedException.Other( this, ex );
            }
        }
        return ret;
    }

    /**
     * <p>Apply this Change to a MeshObject in this MeshBase. This method
     *    is intended to make it easy to reproduce Changes that were made in
     *    one MeshBase to MeshObjects in another MeshBase.</p>
     *
     * <p>This method will attempt to create a Transaction if none is present on the
     * current Thread.</p>
     *
     * @param base the MeshBase in which to apply the Change
     * @return the MeshObject to which the Change was applied
     * @throws CannotApplyChangeException thrown if the Change could not be applied, e.g because
     *         the affected MeshObject did not exist in MeshBase base
     * @throws TransactionException thrown if a Transaction didn't exist on this Thread and
     *         could not be created
     */
    public MeshObject applyTo(
            MeshBase otherMeshBase )
        throws
            CannotApplyChangeException,
            TransactionException
    {
        setResolver( otherMeshBase );

        Transaction tx = null;
        Throwable   t = null;

        ModelBase modelBase = otherMeshBase.getModelBase();

        resolveEntityTypes();

        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            MeshObject newObject = otherMeshBase.getMeshBaseLifecycleManager().createMeshObject(
                        getDeltaValueIdentifier(),
                        getEntityTypes(),
                        theExternalizedMeshObject.getTimeCreated(),
                        theExternalizedMeshObject.getTimeUpdated(),
                        theExternalizedMeshObject.getTimeRead(),
                        theExternalizedMeshObject.getTimeExpires() );

            for( int i=0 ; i<theExternalizedMeshObject.getPropertyTypes().length ; ++i ) {
                try {
                    PropertyType  propertyType  = modelBase.findPropertyTypeByIdentifier( theExternalizedMeshObject.getPropertyTypes()[i] );
                    PropertyValue propertyValue = theExternalizedMeshObject.getPropertyValues()[i];

                    newObject.setPropertyValue( propertyType, propertyValue );

                } catch( Throwable ex ) {
                    if( t == null ) {
                        t = ex;
                    } else {
                        log.warn( "Second or later Exception", ex );
                    }
                }
            }
            return newObject;

        } catch( TransactionException ex ) {
            throw ex;

        } catch( Throwable ex ) {
            if( t == null ) {
                t = ex;
            } else {
                log.warn( "Second or later Exception", ex );
            }
                
        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }
        if( t != null ) {
            throw new CannotApplyChangeException.ExceptionOccurred( otherMeshBase, t );
        }
        return null; // I don't think this can happen, but let's make the compiler happy.
    }
    
    /**
     * Clear cached objects to force a re-resolve.
     */
    @Override
    protected void clearCachedObjects()
    {
        theEntityTypes = null;

        super.clearCachedObjects();
    }

    /**
     * Determine equality.
     *
     * @param other the Object to compare against
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof MeshObjectCreatedEvent )) {
            return false;
        }
        MeshObjectCreatedEvent realOther = (MeshObjectCreatedEvent) other;

        if( !getDeltaValueIdentifier().equals( realOther.getDeltaValueIdentifier() )) {
            return false;
        }
        if( !theExternalizedMeshObject.equals( realOther.theExternalizedMeshObject )) {
            return false;
        }
        if( getTimeEventOccurred() != realOther.getTimeEventOccurred() ) {
            return false;
        }
        return true;
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
                    "getNewValueIdentifier()",
                    "theExternalizedMeshObject",
                    "getTimeOccured()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    getDeltaValueIdentifier(),
                    theExternalizedMeshObject,
                    getTimeEventOccurred()
                });
    }

    /**
     * Externalized version.
     */
    protected ExternalizedMeshObject theExternalizedMeshObject;
    
    /**
     * The EntityTypes, once resolved.
     */
    protected transient EntityType [] theEntityTypes;
}
