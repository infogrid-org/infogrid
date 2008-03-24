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
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;

import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;

import org.infogrid.util.event.AbstractExternalizablePropertyChangeEvent;
import org.infogrid.util.event.UnresolvedException;


/**
  * This event indicates that one of a MeshObject's properties has changed its value.
  *
  * This extends PropertyChangeEvent so we can keep the well-known JavaBeans
  * event generation model that programmers are used to.
  *
  * Because the JavaBeans model really does not work with object serialization, we
  * have to do some tricks. FIXME: override serialization code to not serialize the
  * source property in the superclass.
  *
  * If you change this, watch out for serialization.
  */
public class MeshObjectPropertyChangeEvent
        extends
            AbstractExternalizablePropertyChangeEvent<MeshObject, MeshObjectIdentifier, PropertyType, MeshTypeIdentifier, PropertyValue, PropertyValue>
        implements
            Change<MeshObject,MeshObjectIdentifier,PropertyValue,PropertyValue>
{    /**
     * Constructor.
     * 
     * 
     * 
     * @param meshObject the MeshObject whose Property changed
     * @param propertyType the PropertyType whose value changed
     * @param oldValue the old value of the property
     * @param newValue the new value of the property
     * @param updateTime the time at which the change occurred
     */
    public MeshObjectPropertyChangeEvent(
            MeshObject     meshObject,
            PropertyType   propertyType,
            PropertyValue  oldValue,
            PropertyValue  newValue,
            long           updateTime )
    {
        super(  meshObject,
                meshObject.getIdentifier(),
                propertyType,
                propertyType.getIdentifier(),
                oldValue,
                oldValue,
                newValue, // delta = new
                newValue,
                newValue,
                newValue,
                updateTime );
    }

    /**
     * Constructor for the case where we don't have an old value, only the new value.
     * This perhaps should trigger some exception if it is attempted to read old 
     * values later. (FIXME?)
     */
    public MeshObjectPropertyChangeEvent(
            MeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier   propertyTypeIdentifier,
            PropertyValue        newValue,
            long                 updateTime )
    {
        super(  null,
                meshObjectIdentifier,
                null,
                propertyTypeIdentifier,
                null,
                null,
                null,
                null,
                newValue,
                newValue,
                updateTime );
    }
    
    /**
      * Constructor.
      *
      * @param meshObject the MeshObject whose Property changed
      * @param thePropertyType the PropertyType whose value changed
      * @param theOldValue the old value of the property
      * @param theNewValue the new value of the property
      * @param updateTime the time at which the change occurred
      */
    public MeshObjectPropertyChangeEvent(
            MeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier   propertyTypeIdentifier,
            PropertyValue        oldValue,
            PropertyValue        newValue,
            long                 updateTime )
    {
        super(  (MeshObject) null,
                meshObjectIdentifier,
                (PropertyType) null,
                propertyTypeIdentifier,
                oldValue,
                oldValue,
                newValue, // delta = new
                newValue,
                newValue,
                newValue,
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
     * Apply this Change to a MeshObject in this MeshBase. This method
     * is intended to make it easy to reproduce Changes that were made in
     * one MeshBase to MeshObjects in another MeshBase.
     *
     * This method will attempt to create a Transaction if none is present on the
     * current Thread.
     *
     * @param otherMeshBase the other MeshBase in which to apply the change
     * @throws CannotApplyChangeException thrown if the Change could not be applied, e.g because
     *         the affected MeshObject did not exist in the other MeshBase
     * @throws TransactionException thrown if a Transaction didn't exist on this Thread and could not be created
     */
    public MeshObject applyTo(
            MeshBase otherMeshBase )
        throws
            CannotApplyChangeException,
            TransactionException
    {
        setResolver( otherMeshBase );

        Transaction tx = null;

        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            MeshObject otherObject = getSource();

            PropertyType  affectedProperty = getProperty();
            PropertyValue newValue         = getNewValue();
            long          updateTime       = getTimeEventOccurred();

            otherObject.setPropertyValue(
                    affectedProperty,
                    newValue,
                    updateTime );

            return otherObject;

        } catch( TransactionException ex ) {
            throw ex;

        } catch( Throwable ex ) {
            throw new CannotApplyChangeException.ExceptionOccurred( otherMeshBase, ex );

        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }
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
            throw new UnresolvedException.Property( this );
        }
        
        MeshObject ret = theResolver.findMeshObjectByIdentifier( getSourceIdentifier() );
        return ret;
    }

    /**
     * Resolve the property of the event.
     *
     * @return the property of the event
     */
    protected PropertyType resolveProperty()
    {
        if( theResolver == null ) {
            throw new UnresolvedException.Property( this );
        }
        
        try {
            PropertyType ret = theResolver.getModelBase().findPropertyTypeByIdentifier( getPropertyIdentifier() );
            return ret;

        } catch( MeshTypeWithIdentifierNotFoundException ex ) {
            throw new UnresolvedException.Property( this, ex );
        }
    }
    
    /**
     * Resolve the new value of the event.
     *
     * @return the new value of the event
     */
    protected PropertyValue resolveValue(
            PropertyValue vid )
    {
        return vid;
    }
    
    /**
     * Determine equality.
     *
     * @param other the Object to compare with
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof MeshObjectPropertyChangeEvent )) {
            return false;
        }
        MeshObjectPropertyChangeEvent realOther = (MeshObjectPropertyChangeEvent) other;

        if( !getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
            return false;
        }
        if( !getPropertyIdentifier().equals( realOther.getPropertyIdentifier())) {
            return false;
        }
        if( !getNewValueIdentifier().equals( realOther.getNewValueIdentifier())) {
            return false;
        }
        if( getTimeEventOccurred() != realOther.getTimeEventOccurred() ) {
            return false;
        }
        return true;
    }

    /**
     *  Determine hash code.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return getSourceIdentifier().hashCode();
    }

    /**
     * The resolver of identifiers carried by this event.
     */
    protected transient MeshBase theResolver;
}
