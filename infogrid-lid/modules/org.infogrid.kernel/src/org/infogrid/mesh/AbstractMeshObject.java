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

package org.infogrid.mesh;

import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.mesh.security.PropertyReadOnlyException;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.transaction.MeshObjectBecameDeadStateEvent;
import org.infogrid.meshbase.transaction.MeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectTypeAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.AttributableMeshType;
import org.infogrid.model.primitives.DataType;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.Role;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.traversal.TraversalSpecification;

import org.infogrid.modelbase.MeshTypeNotFoundException;
import org.infogrid.modelbase.PropertyTypeNotFoundException;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.FlexiblePropertyChangeListenerSet;
import org.infogrid.util.IsDeadException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.StringHelper;
import org.infogrid.util.ZeroElementIterator;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class collects functionality that is probably useful to all implementations
 * of MeshObject. Its use by implementations, however, is optional.
 */
public abstract class AbstractMeshObject
        implements
            MeshObject
{
    static final Log log = Log.getLogInstance( AbstractMeshObject.class ); // our own, private logger

    /**
     * Constructor, for subclasses only.
     * 
     * @param identifier the value for the Identifier
     * @param meshBase the MeshBase that this MeshObject belongs to
     * @param created the time this MeshObject was created
     * @param updated the time this MeshObject was last updated
     * @param read the time this MeshObject was last read
     * @param expires the time this MeshObject will expire
     */
    public AbstractMeshObject(
            MeshObjectIdentifier identifier,
            MeshBase             meshBase,
            long                 created,
            long                 updated,
            long                 read,
            long                 expires )
    {
        theIdentifier   = identifier;
        theMeshBase     = meshBase;
        
        theTimeCreated  = created;
        theTimeUpdated  = updated;
        theTimeRead     = read;
        theTimeExpires  = expires;
    }

    /**
      * Obtain the globally unique identifier of this MeshObject.
      *
      * @return the globally unique identifier of this MeshObject
      */
    public MeshObjectIdentifier getIdentifier()
    {
        return theIdentifier;
    }

    /**
      * Obtain the MeshBase that contains this MeshObject.
      *
      * @return the MeshBase that contains this MeshObject.
      */
    public MeshBase getMeshBase()
    {
        return theMeshBase;
    }

    /**
     * Obtain the time of creation of this MeshObject.
     *
     * @return the time the object was created in milliseconds
     */
    public final long getTimeCreated()
    {
        return theTimeCreated;
    }

    /**
     * Obtain the time of last update of this MeshObject.
     *
     * @return the time the object was last updated in milliseconds
     */
    public final long getTimeUpdated()
    {
        return theTimeUpdated;
    }

    /**
     * Obtain the time of last read of this MeshObject.
     *
     * @return the time the object was last read in milliseconds
     */
    public final long getTimeRead()
    {
        return theTimeRead;
    }

    /**
     * Set the time when this MeshObject expires. If -1, it never does.
     *
     * @param newValue the new value, in milliseconds
     */
    public final void setTimeExpires(
            long newValue )
    {
        theTimeExpires = newValue;
    }

    /**
     * Obtain the time when this MeshObject expires. If this returns -1, it never does.
     *
     * @return the time at which this MeshObject expires
     */
    public final long getTimeExpires()
    {
        return theTimeExpires;
    }

    /**
     * Determine whether this MeshObject is dead and should not be used any further.
     *
     * @return true if the MeshObject is dead
     */
    public final boolean getIsDead()
    {
        MeshBase mb = theMeshBase; // this allows us not to synchronize this method
        
        if( mb == null ) {
            return true; // the object itself has been deleted or purged
        }

        if( mb.isDead() ) {
            return true; // the whole MeshBase died
        }

        return false;
    }

    /**
     * Make sure this is a live MeshObject.
     *
     * @throws IsDeadException thrown if this MeshObject is dead already
     */
    public final void checkAlive()
        throws
            IsDeadException
    {
        if( getIsDead() ) {
            throw new IsDeadException( this );
        }
    }

    /**
     * Determine whether this MeshObject is the home object of its MeshBase.
     * 
     * @return true if it is the home object
     */
    public final boolean isHomeObject()
    {
        MeshObject home = theMeshBase.getHomeObject();
        boolean    ret  = home == this;
        return ret;
    }

    /**
     * Obtain the value of a Property, given its PropertyType.
     *
     * @param thePropertyType the PropertyType whose value we want to determine for this MeshObject
     * @return the current value of the PropertyValue
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #setPropertyValue
     */
    public PropertyValue getPropertyValue(
            PropertyType thePropertyType )
        throws
            NotPermittedException
    {
        checkAlive();

        if( thePropertyType == null ) {
            throw new NullPointerException();
        }

        synchronized( this ) {
            EntityType requiredType = (EntityType) thePropertyType.getAttributableMeshType();
            boolean    found        = false;
            
            if( theMeshTypes != null ) {
                for( AttributableMeshType type : theMeshTypes.keySet() ) {
                    if( type.equalsOrIsSupertype( requiredType )) {
                        found = true;
                        break;
                    }
                }
            }
            if( !found ) {
                throw new EntityNotBlessedException( this, requiredType );
            }
            
            checkPermittedGetProperty( thePropertyType );

            updateLastRead();
            if( theProperties != null ) {
                return theProperties.get( thePropertyType );
            } else {
                return null;
            }
        }
    }

    /**
     * Obtain all PropertyValues for the PropertyTypes provided, in the same sequence as the provided
     * PropertyTypes. If a PropertyType does not exist on this MeshObject, or if access to one of the
     * PropertyTypes is not permitted, this will throw an exception. This is a convenience method.
     *
     * @param thePropertyTypes the PropertyTypes
     * @return the PropertyValues, in the same sequence as PropertyTypes
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public PropertyValue [] getPropertyValues(
            PropertyType [] thePropertyTypes )
        throws
            NotPermittedException
    {
        checkAlive();

        if( thePropertyTypes == null ) {
            throw new NullPointerException();
        }

        synchronized( this ) {
            for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
                EntityType requiredType = (EntityType) thePropertyTypes[i].getAttributableMeshType();
                boolean    found        = false;
            
                if( theMeshTypes != null ) {
                    for( AttributableMeshType type : theMeshTypes.keySet() ) {
                        if( type.equalsOrIsSupertype( requiredType )) {
                            found = true;
                            break;
                        }
                    }
                }
                if( !found ) {
                    throw new EntityNotBlessedException( this, requiredType );
                }
            }

            for( PropertyType currentType : thePropertyTypes ) {
                checkPermittedGetProperty( currentType );
            }

            updateLastRead();
            
            PropertyValue [] ret = new PropertyValue[ thePropertyTypes.length ];
            if( theProperties != null ) {
                
                for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
                    ret[i] = theProperties.get( thePropertyTypes[i] );
                }
            }
            return ret;
        }
        
    }
    
    /**
     * Set the value of a Property, given its PropertyType and a PropertyValue.
     * 
     * @param thePropertyType the PropertyType whose value we want to set
     * @param newValue the new value for the PropertyType for this MeshObject
     * @return old value of the Property
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #getPropertyValue
     */
    public PropertyValue setPropertyValue(
            PropertyType  thePropertyType,
            PropertyValue newValue )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        return setPropertyValue( thePropertyType, newValue, -1L );
    }

    /**
     * Set the value of a Property, given its PropertyType and a PropertyValue.
     * 
     * @param thePropertyType the PropertyType whose value we want to set
     * @param newValue the new value for the PropertyType for this MeshObject
     * @return old value of the Property
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #getPropertyValue
     */
    public PropertyValue setPropertyValue(
            PropertyType  thePropertyType,
            PropertyValue newValue,
            long          timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        return internalSetPropertyValues(
                new PropertyType[] { thePropertyType },
                new PropertyValue[] { newValue },
                timeUpdated,
                true )[0];
    }

    /**
     * Internal helper to implement a method. While on this level, it does not appear that factoring out
     * this method makes any sense, subclasses may appreciate it.
     *
     * @param thePropertyTypes the sequence of PropertyTypes to set
     * @param newValues the sequence of PropertyValues for the PropertyTypes
     * @param timeUpdated the time to use as the new timeUpdated
     * @param isMaster if true, check permissions
     */
    protected PropertyValue [] internalSetPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] newValues,
            long             timeUpdated,
            boolean          isMaster )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        checkAlive();

        if( thePropertyTypes.length != newValues.length ) {
            throw new IllegalArgumentException( "PropertyTypes and PropertyValues must have same length" );
        }

        for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
            if( thePropertyTypes[i] == null ) {
                throw new NullPointerException();
            }
            if( thePropertyTypes[i].getIsReadOnly().value() ) {
                throw new PropertyReadOnlyException( this, thePropertyTypes[i] );
            }
            DataType type = thePropertyTypes[i].getDataType();
            if( newValues[i] == null ) {
                if( !thePropertyTypes[i].getIsOptional().value() ) {
                    throw new IllegalPropertyValueException( this, thePropertyTypes[i], newValues[i] );
                }
            } else if( !type.conforms( newValues[i] )) {            
                throw new IllegalPropertyValueException( this, thePropertyTypes[i], newValues[i] );
            }
        }

        PropertyValue [] oldValues;
        synchronized( this ) {
            theMeshBase.checkTransaction();

            if( isMaster ) {
                for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
                    checkPermittedSetProperty( thePropertyTypes[i], newValues[i] );
                }
            }

            for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
                EntityType requiredType = (EntityType) thePropertyTypes[i].getAttributableMeshType();
                boolean    found        = false;

                if( theMeshTypes != null ) {
                    for( AttributableMeshType amt : theMeshTypes.keySet() ) {
                        if( amt.equalsOrIsSupertype( requiredType )) {
                            found = true;
                            break;
                        }
                    }
                }
                if( !found ) {
                    throw new EntityNotBlessedException( this, requiredType );
                }
            }

            if( theProperties == null ) {
                theProperties = createProperties();
            }
            oldValues = new PropertyValue[ thePropertyTypes.length ];
            
            for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
                oldValues[i] = theProperties.put( thePropertyTypes[i], newValues[i] );
            }
            updateLastUpdated( timeUpdated, theTimeUpdated );
        }
        for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
            if( PropertyValue.compare( oldValues[i], newValues[i] ) != 0 ) {
                firePropertyChange( thePropertyTypes[i], oldValues[i], newValues[i], theMeshBase );
            }
        }

        return oldValues;
    }

    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues, in the same sequence.
     * This method sets either all values, or none.
     *
     * @param thePropertyTypes the PropertyTypes whose values we want to set
     * @param thePropertyValues the new values for the PropertyTypes for this MeshObject
     * @return old value of the Properties
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws WrongDataTypeException is thrown if the new value is an instance of the wrong subclass of PropertyType.
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public PropertyValue [] setPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] thePropertyValues )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        return setPropertyValues( thePropertyTypes, thePropertyValues, -1L );
    }
            
    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues,
     * and specify a time when that change happened. This method sets either all values, or none.
     * The caller must have the appropriate rights to invoke this.
     *
     * @param newValues Map of PropertyType to PropertyValue
     * @param timeUpdated the time at which this change occurred
     * @return old value of the Properties
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public void setPropertyValues(
            Map<PropertyType,PropertyValue> newValues )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        // FIXME Not a very smart implementation
        PropertyType  [] types = new PropertyType[ newValues.size() ];
        PropertyValue [] values = new PropertyValue[ types.length ];

        int i=0;
        for( PropertyType currentType : newValues.keySet() ) {
            types[i] = currentType;
            values[i] = newValues.get( currentType );
            ++i;
        }
        setPropertyValues( types, values );
    } 

    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues, in the same sequence,
     * and specify a time when that change happened. This method sets either all values, or none.
     * The caller must have the appropriate rights to invoke this; typical callers
     * do not have the rights because this call is mostly intended for system-internal reasons.
     *
     * @param thePropertyTypes the PropertyTypes whose values we want to set
     * @param thePropertyValues the new values for the PropertyTypes for this MeshObject
     * @param timeUpdated the time at which this change occurred
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws WrongDataTypeException is thrown if the new value is an instance of the wrong subclass of PropertyType.
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public PropertyValue [] setPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] thePropertyValues,
            long             timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        return internalSetPropertyValues( thePropertyTypes, thePropertyValues, timeUpdated, true );
    }
    
    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues,
     * and specify a time when that change happened. This method sets either all values, or none.
     * The caller must have the appropriate rights to invoke this.
     *
     * @param newValues Map of PropertyType to PropertyValue
     * @param timeUpdated the time at which this change occurred
     * @return old value of the Properties
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public void setPropertyValues(
            Map<PropertyType,PropertyValue> newValues,
            long                            timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            TransactionException
    {
        // FIXME Not a very smart implementation
        PropertyType  [] types = new PropertyType[ newValues.size() ];
        PropertyValue [] values = new PropertyValue[ types.length ];

        int i=0;
        for( PropertyType currentType : newValues.keySet() ) {
            types[i] = currentType;
            values[i] = newValues.get( currentType );
            ++i;
        }
        setPropertyValues( types, values, timeUpdated );
    }

    /**
     * This is a convenience method to obtain the value of a property by providing the
     * name of the property. If such a property could be found, return its value. If not,
     * throw an Exception.
     *
     * Warning: sometimes, MeshObjects carry two Properties with the same (short) name.
     * This will only return one of them.
     *
     * @param propertyName the name of the property
     * @return the value of the property
     * @throws MeshTypeNotFoundException thrown if a Property by this name could not be found
     * @throws NotPermittedException thrown if the client was not allowed to access this property
     */
    public PropertyValue getPropertyValueByName(
            String propertyName )
        throws
            MeshTypeNotFoundException,
            NotPermittedException
    {
        // getTypes() will checkAlive()
        
        for( EntityType current : getTypes() ) {
            PropertyType propertyType = current.findPropertyTypeByName( propertyName );
            if( propertyType != null ) {
                PropertyValue ret = getPropertyValue( propertyType );
                return ret;
            }
        }
        throw new PropertyTypeNotFoundException( null, propertyName );
    }

    /**
     * Obtain the set of all PropertyTypes currently used with this MeshObject. This may return only the
     * subset of PropertyTypes that the caller is allowed to see.
     *
     * @return the set of all PropertyTypes
     */
    public synchronized PropertyType [] getAllPropertyTypes()
    {
        checkAlive();

        // we cannot look at the properties table, because it may not contain keys for all of them
        if( theMeshTypes == null || theMeshTypes.isEmpty() ) {
            return new PropertyType[0];
        }
        ArrayList<PropertyType> almostRet = new ArrayList<PropertyType>();
        for( EntityType type : theMeshTypes.keySet() ) {
            PropertyType [] current = type.getAllPropertyTypes();
            for( int i=0 ; i<current.length ; ++i ) {
                if( !almostRet.contains( current[i] )) {
                    try {
                        checkPermittedGetProperty( current[i] );
                        almostRet.add( current[i] );

                    } catch( NotPermittedException ex ) {
                        log.info( ex );
                    }
                }
            }
        }
        PropertyType [] ret = ArrayHelper.copyIntoNewArray( almostRet, PropertyType.class );

        updateLastRead();
        return ret;
    }

    /**
     * Obtain the Identifiers of the neighbors of this MeshObject. This is sometimes a
     * more efficient operation than to traverse to the neighbors and determine the Identifiers
     * from there.
     *
     * @return the Identifiers of the neighbors
     */
    public MeshObjectIdentifier[] getNeighborMeshObjectIdentifiers()
    {
        checkAlive();
        
        MeshObjectIdentifier [] ret = traverseToNeighborMeshObjects().asIdentifiers();
        return ret;
    }
    
    /**
     * Traverse from this MeshObject to all directly related MeshObjects. Directly
     * related MeshObjects are those MeshObjects that are participating in a
     * relationship with this MeshObject. This may only return those neighbor MeshObjects
     * that the caller is allowed to see.
     *
     * @return the set of MeshObjects that are directly related to this MeshObject
     */
    public final MeshObjectSet traverseToNeighborMeshObjects()
    {
        return traverseToNeighborMeshObjects( true );
    }

    /**
     * Make this MeshObject support the provided ByEntityType.
     * 
     * @param type the new ByEntityType to be supported by this MeshObject
     * @throws BEntityBlessedAlreadyException thrown if this MeshObject is blessed already with this type
     * @throws IsAbstractException thrown if one of the EntityTypes is abstract and cannot be instantiated
     * @throws TransactionException thrown if invoked outside of proper transaction boundaries
     * @throws NotPermittedException thrown if the operation was not permitted
     */
    public void bless(
            EntityType type )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        bless( new EntityType[] { type } );
    }

    /**
     * Make this MeshObject support one more more provided EntityTypes. The MeshObject will either be
     * blessed with all of the EntityTypes, or none.
     *
     * @param types the new EntityTypes to be supported by this MeshObject
     * @throws EntityBlessedAlreadyException thrown if this MeshObject is blessed already with this type
     * @throws IsAbstractException thrown if one of the EntityTypes is abstract and cannot be instantiated
     * @throws TransactionException thrown if invoked outside of proper transaction boundaries
     * @throws NotPermittedException thrown if the operation was not permitted
     */
    public void bless(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        internalBless( types, true, true , false );
    }
    
    /**
     * Internal helper to implement a method. While on this level, it does not appear that factoring out
     * this method makes any sense, subclasses may appreciate it.
     */
    protected void internalBless(
            EntityType [] types,
            boolean       isMaster,
            boolean       checkIsAbstract,
            boolean       forgiving )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        checkAlive();

        for( int i=0 ; i<types.length ; ++i ) {
            if( types[i] == null ) {
                throw new IllegalArgumentException( "null EntityType at index " + i );
            }
            if( checkIsAbstract ) {
                if( types[i].getIsAbstract().value() ) {
                    throw new IsAbstractException( types[i] );
                }
            } else {
                if( !types[i].getMayBeUsedAsForwardReference().value() ) {
                    throw new IsAbstractException( types[i] );
                }
            }
        }

        synchronized( this ) {
            theMeshBase.checkTransaction();

            if( isMaster ) {
                checkPermittedBless( types );
            }

            ArrayList<EntityType> oldTypes = new ArrayList<EntityType>();
            
            // throw Exception if the new type is already here, or a supertype of an existing one.
            // However, subtypes are allowed but need to be removed.
            if( theMeshTypes != null ) {
                EntityType toRemove = null;
                Set<EntityType> keySet = theMeshTypes.keySet();
                for( EntityType already : keySet ) {
                    oldTypes.add( already );
                    for( int i=0 ; i<types.length ; ++i ) {
                        if( !forgiving && already.isSubtypeOfOrEquals( types[i] )) {
                            throw new EntityBlessedAlreadyException( this, types[i] );
                        } else if( types[i].isSubtypeOfDoesNotEqual( already )) {
                            toRemove = already;
                        }
                    }
                }
                if( toRemove != null ) {
                    if( theMeshTypes.size() == 1 ) {
                        theMeshTypes = null;
                    } else {
                        theMeshTypes.remove( toRemove );
                    }
                }
            }
            if( theMeshTypes == null ) {
                theMeshTypes = createMeshTypes();
            }
            for( int i=0 ; i<types.length ; ++i ) {
                WeakReference<TypedMeshObjectFacade> already = theMeshTypes.get( types[i] );
                if( already == null ) { // otherwise we have it already
                    theMeshTypes.put( types[i], null );
                }

                updateLastUpdated();
            }
            fireTypesAdded(
                    ArrayHelper.copyIntoNewArray( oldTypes, EntityType.class ),
                    types,
                    ArrayHelper.copyIntoNewArray( theMeshTypes.keySet().iterator(), EntityType.class ),
                    theMeshBase );

            for( int i=0 ; i<types.length ; ++i ) {
                TypeInitializer init = createTypeInitializer( types[i] );
                init.initialize( theTimeUpdated );
            }
        }
    }

    /**
     * Obtain a type initializer. This may be overridden by subclasses.
     *
     * @return the TypeInitializer
     */
    public TypeInitializer createTypeInitializer(
             EntityType type )
    {
         return new TypeInitializer( this, type );
    }

    /**
     * Makes this MeshObject stop supporting the provided ByEntityType.
     * 
     * @param type the ByEntityType that the MeshObject will stop supporting
     * @throws NotBlessedException.ByEntityType thrown if this MeshObject does not currently support this ByEntityType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unbless(
            EntityType type )
        throws
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException
    {
        unbless( new EntityType[] { type } );
    }

    /**
     * Makes this MeshObject stop supporting the provided EntityTypes. The MeshObject will either be
     * unblessed from all of the EntityTypes, or none.
     * 
     * @param types the EntityTypes that the MeshObject will stop supporting
     * @throws NotBlessedException.ByEntityType thrown if this MeshObject does not support at least one of the given EntityTypes
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public void unbless(
            EntityType [] types )
        throws
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException
    {
        internalUnbless( types, true );
    }
    
    /**
     * Internal helper to implement a method. While on this level, it does not appear that factoring out
     * this method makes any sense, subclasses may appreciate it.
     */
    protected synchronized void internalUnbless(
            EntityType [] types,
            boolean       isMaster )
        throws
            RoleTypeRequiresEntityTypeException,
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException
    {
        checkAlive();

        if( types == null || types.length == 0 ) {
            return;
        }
        theMeshBase.checkTransaction();

        if( isMaster ) {
            checkPermittedUnbless( types );
        }

        if( theMeshTypes == null ) {
            throw new EntityNotBlessedException( this, types[0] );
        }
        Set<EntityType> keySet = theMeshTypes.keySet();
        for( int i=0 ; i<types.length ; ++i ) {
            if( !keySet.contains( types[i] )) {
                 throw new EntityNotBlessedException( this, types[i] );
            }
        }
        
        EntityType [] oldTypes = getTypes();
        
        EntityType [] remainingTypes = ArrayHelper.removeIfPresent( oldTypes, types, false, EntityType.class );
        
        for( RoleType rt : getRoleTypes() ) {

            EntityType requiredType = rt.getEntityType();
            if( requiredType != null ) {
                boolean found = false;
                for( int i=0 ; i<remainingTypes.length ; ++i ) {

                    if( remainingTypes[i].equalsOrIsSupertype( requiredType )) {
                        found = true;
                        break;
                    }
                }
                if( !found ) {
                    throw new RoleTypeRequiresEntityTypeException( this, rt );
                }
            }
        }
        for( int i=0 ; i<types.length ; ++i ) {
            keySet.remove( types[i] );
        }

        // remove unnecessary properties
        if( theProperties != null ) {
            PropertyType [] remainingProperties = getAllPropertyTypes();
            ArrayList<PropertyType> toRemove = new ArrayList<PropertyType>();
            for( PropertyType current : theProperties.keySet() ) {
                if( !ArrayHelper.isIn( current, remainingProperties, false )) {
                    toRemove.add( current );
                }
            }
            for( PropertyType current : toRemove ) {
                theProperties.remove( current );
            }
            if( theProperties.isEmpty() ) {
                theProperties = null;
            }
        }
        
        updateLastUpdated();
        fireTypesRemoved(
                oldTypes,
                types,
                getTypes(),
                theMeshBase );
    }

    /**
      * Obtain the EntityTypes that this MeshObject is currently blessed with.
      *
      * @return the types of this MeshObject
      */
    public synchronized EntityType [] getTypes()
    {
        checkAlive();

        updateLastRead();
        if( theMeshTypes != null ) {
            Set<EntityType> keySet = theMeshTypes.keySet();
            EntityType []   ret    = new EntityType[ keySet.size() ];

            int index = 0;
            for( EntityType current : keySet ) {
                try {
                    checkPermittedBlessedBy( current );
                    ret[ index++ ] = current;
                } catch( NotPermittedException ex ) {
                    log.info( ex );
                }
            }
            if( index < ret.length ) {
                ret = ArrayHelper.copyIntoNewArray( ret, 0, index, EntityType.class );
            }
            
            return ret;
        } else {
            return new EntityType[0];
        }
    }

    /**
     * Determine whether this MeshObject currently supports this MeshType. This may return false, even if
     * the MeshObject is blessed by this ByEntityType, if the caller is not allowed to see this ByEntityType.
     * By default, this returns true even if the MeshObject is blessed by a subtype of the provided type.
     * 
     * @param type the ByEntityType to look for
     * @return true if this MeshObject supports this MeshType
     */
    public final boolean isBlessedBy(
            EntityType type )
    {
        return isBlessedBy( type, true );
    }
    
    /**
     * Determine whether this MeshObject currently supports this MeshType. This may return false, even if
     * the MeshObject is blessed by this ByEntityType, if the caller is not allowed to see this ByEntityType.
     * Specify whether or not subtypes of the provided type should be considered.
     * 
     * @param type the ByEntityType to look for
     * @param considerSubtypes if true, return true even if only a subtype matches
     * @return true if this MeshObject supports this MeshType
     */
    public synchronized boolean isBlessedBy(
            EntityType type,
            boolean    considerSubtypes )
    {
        checkAlive();

        try {
            checkPermittedBlessedBy( type );

            updateLastRead();
            if( theMeshTypes != null ) {
                Set<EntityType> keySet = theMeshTypes.keySet();

                if( considerSubtypes ) {
                    for( EntityType actualBlessed : keySet ) {
                        if( actualBlessed.isSubtypeOfOrEquals( type )) {
                            return true;
                        }
                    }
                } else {
                    for( EntityType actualBlessed : keySet ) {
                        if( actualBlessed.equals( type )) {
                            return true;
                        }
                    }
                }
            }
        } catch( NotPermittedException ex ) {
            log.info( ex ); 
            // caller is not allowed to know
        }
        return false;
    }

    /**
     * Determine the specific subtype of the provided EntityType with which this MeshObject has been blessed.
     * If this MeshObject has not been blessed with a subtype of the provided EntityType, return <code>null</code>.
     * If blessed with more than one subtype, throw an Exception.
     *
     * @param type the EntityType
     * @return the sub-type, if any
     * @throws IllegalStateException thrown if the MeshObject is blessed by more than one subtype
     */
    public EntityType determineBlessedSubtype(
            EntityType type )
        throws
            IllegalStateException
    {
        checkAlive();

        try {
            checkPermittedBlessedBy( type );
        } catch( NotPermittedException ex ) {
            log.info( ex ); 
            // caller is not allowed to know
            return null;
        } finally {
            updateLastRead();
        }
        
        if( theMeshTypes != null ) {
            Set<EntityType> keySet = theMeshTypes.keySet();

            EntityType found = null;

            for( EntityType actualBlessed : keySet ) {
                if( actualBlessed.isSubtypeOfDoesNotEqual( type )) {
                    if( found == null ) {
                        found = actualBlessed;
                    } else {
                        throw new IllegalStateException( "Blessed by more than one subtype of " + type.getIdentifier().toExternalForm() );
                    }
                }
            }
            return found;
        }
        return null;
    }

    /**
     * Relate this MeshObject to another MeshObject, and bless the new relationship with the provided ByRoleType.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is instantiated at this object
     * @param otherObject the MeshObject to relate to
     * @see #unrelate
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public final void relateAndBless(
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException
    {
        relate( otherObject );
        blessRelationship( thisEnd, otherObject );
    }

    /**
     * Relate this MeshObject to another MeshObject, and blessRelationship the new relationship with the provided ByRoleType.
     * 
     * 
     * @param thisEnds the ByRoleType of the RelationshipType that is instantiated at this object
     * @param otherObject the MeshObject to relate to
     * @see #unrelate
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public final void relateAndBless(
            RoleType [] thisEnds,
            MeshObject  otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException
    {
        relate( otherObject );
        blessRelationship( thisEnds, otherObject );
    }

    /**
     * If the provided TypedMeshObjectFacade is a facade of this instance, get the ByEntityType
     * that corresponds to this TypedMeshObjectFacade.
     * 
     * 
     * @param obj the TypedMeshObjectFacade
     * @return the ByEntityType that corresponds to this TypedMeshObjectFacade
     * @throws IllegalArgumentException thrown if the TypedMeshObjectFacade is not a facade of this MeshObject
     */
    public synchronized EntityType getTypeFor(
            TypedMeshObjectFacade obj )
        throws
            IllegalArgumentException
    {
        checkAlive();

        // This implementation is very slow (FIXME?)
        if( obj == null ) {
            throw new NullPointerException();
        }
        if( theMeshTypes == null ) {
            return null;
        }

        updateLastRead();
        Iterator<EntityType> iter = theMeshTypes.keySet().iterator();
        while( iter.hasNext() ) {
            EntityType                       key   = iter.next();
            Reference<TypedMeshObjectFacade> value = theMeshTypes.get( key );
            if( value != null ) {
                TypedMeshObjectFacade candidate = value.get();
                if( candidate != null && candidate == obj ) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * Obtain an instance of (a subclass of) TypedMeshObjectFacade that provides the type-safe interface
     * to this MeshObject for a particular ByEntityType. Throw NotBlessedException
     * if this MeshObject does not current support this ByEntityType.
     * 
     * 
     * @param type the ByEntityType
     * @return the TypedMeshObjectFacade for this MeshObject
     * @throws NotBlessedException thrown if this MeshObject does not currently support this ByEntityType
     */
    public synchronized TypedMeshObjectFacade getTypedFacadeFor(
            EntityType type )
        throws
            NotBlessedException
    {
        checkAlive();

        try {
            checkPermittedBlessedBy( type );

            if( theMeshTypes == null ) {
                throw new EntityNotBlessedException( this, type );
            }

            Set<EntityType> keySet = theMeshTypes.keySet();

            EntityType found = null;
            for( EntityType actualBlessed : keySet ) {
                if( actualBlessed.isSubtypeOfOrEquals( type )) {
                    found = actualBlessed;
                    break; // good enough
                }
            }
            if( found == null ) {
                throw new EntityNotBlessedException( this, type );
            }
            WeakReference<TypedMeshObjectFacade> ref = theMeshTypes.get( found );
            TypedMeshObjectFacade                ret = ( ref != null ) ? ref.get() : null;

            if( ret == null ) {
                ret = theMeshBase.getMeshBaseLifecycleManager().createTypedMeshObjectFacade( this, found );
                theMeshTypes.put( found, new WeakReference<TypedMeshObjectFacade>( ret ));
            }

            updateLastRead();

            return ret;

        } catch( NotPermittedException ex ) {
            log.info( ex );
            throw new EntityNotBlessedException( this, type ); // don't leak information, so we must say that it isn't blessed
        }
    }

    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided ByRoleType.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is instantiated at this object
     * @param otherObject the other MeshObject to relate to
     * @throws BlessedAlreadyException thrown if the relationship to the other MeshObject is blessed
     *         already with this type
     */
    public void blessRelationship(
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            BlessedAlreadyException,
            NotRelatedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        blessRelationship( new RoleType[] { thisEnd }, otherObject );
    }

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided ByRoleType.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is removed at this object
     * @param otherObject the other MeshObject to relate to
     * @throws NotBlessedException thrown if the relationship to the other MeshObject does not support the type
     */
    public void unblessRelationship(
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        unblessRelationship( new RoleType[] { thisEnd }, otherObject );
    }

    /**
     * Internal helper to obtain all Roles that this MeshObject currently participates in.
     *
     * @param mb the MeshBase
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
     *        if false, only this MeshObject will be used as the start
     * @return the Roles that this MeshObject currently participates in.
     */
    protected abstract Role [] _getAllRoles(
            MeshBase mb,
            boolean  considerEquivalents );
    
    /**
      * Traverse a TraversalSpecification from this MeshObject to obtain a set of MeshObjects.
      * This will consider all MeshObjects equivalent to this one as the start MeshObject.
      *
      * @param theTraverseSpec the TraversalSpecification to traverse
      * @return the set of MeshObjects found as a result of the traversal
      */
    public final MeshObjectSet traverse(
            TraversalSpecification theTraverseSpec )
    {
        return traverse( theTraverseSpec, true );
    }

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in. This will return only one
     * instance of the same ByRoleType object, even if the MeshObject participates in this ByRoleType
     * multiple times with different other sides. This may only return the subset of RoleTypes
     * that the caller is allowed to see.
     * 
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public final RoleType [] getRoleTypes()
    {
        return getRoleTypes( true );
    }

    /**
     * Obtain the Roles that this MeshObject currently participates in. This may only return the subset
     * of Roles that the caller is allowed to see.
     *
     * @return the Roles that this MeshObject currently participates in.
     */
    public final Role [] getRoles()
    {
        return getRoles( true );
    }

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in with the
     * specified other MeshObject. This may only return the subset of RoleTypes that the caller
     * is allowed to see.
     *
     * @param otherObject the other MeshObject
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public final RoleType [] getRoleTypes(
            MeshObject otherObject )
    {
        return getRoleTypes( otherObject, true );
    }

    /**
     * Delete this MeshObject. This must only be invoked by our MeshObjectLifecycleManager
     * and thus is defined down here, not higher up in the inheritance hierarchy.
     */
    public abstract void delete()
        throws
            TransactionException,
            NotPermittedException;

    /**
     * Obtain the Identifiers of the equivalent MeshObjects. This is sometimes more efficient than
     * traversing to the equivalents, and determining the IdentifierValues.
     *
     * @return the Identifiers of the equivalents
     */
    public MeshObjectIdentifier[] getEquivalentMeshObjectIdentifiers()
    {
        MeshObjectIdentifier [] ret = getEquivalents().asIdentifiers();
        return ret;
    }

    /**
      * Determine equality. This works even if the MeshObject is dead already.
      *
      * @param other the Object to test against
      * @return true if the two Objects are equal
      */
    @Override
    public final boolean equals(
            Object other )
    {
        if( other instanceof MeshObject ) {
            MeshObject realOther = (MeshObject) other;
            if( theIdentifier.equals( realOther.getIdentifier() ) ) {
                return true;
            } else if( getIdentifier().equals( realOther.getIdentifier() ) ) {
                return true;
            }
        }
        return false;
    }

    /**
      * The hash code of this MeshObject is the same as the Identifier's hashCode.
      * This works even if the MeshObject is dead already.
      *
      * @return the hash code of this MeshObject
      */
    @Override
    public final int hashCode()
    {
        return theIdentifier.hashCode();
    }

    /**
      * Subscribe to PropertyChangeEvents.
      *
      * @param newListener the listener to add
      * @see #removePropertyChangeListener
      */
    public synchronized void addDirectPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        checkAlive();

        if( thePropertyChangeListeners == null ) {
            thePropertyChangeListeners = new FlexiblePropertyChangeListenerSet();
        }
        thePropertyChangeListeners.addDirect( newListener );
    }

    /**
      * Subscribe to PropertyChangeEvents.
      *
      * @param newListener the listener to add
      * @see #removePropertyChangeListener
      */
    public synchronized void addWeakPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        checkAlive();

        if( thePropertyChangeListeners == null ) {
            thePropertyChangeListeners = new FlexiblePropertyChangeListenerSet();
        }
        thePropertyChangeListeners.addWeak( newListener );
    }

    /**
      * Subscribe to PropertyChangeEvents.
      *
      * @param newListener the listener to add
      * @see #removePropertyChangeListener
      */
    public synchronized void addSoftPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        checkAlive();

        if( thePropertyChangeListeners == null ) {
            thePropertyChangeListeners = new FlexiblePropertyChangeListenerSet();
        }
        thePropertyChangeListeners.addSoft( newListener );
    }

    /**
     * Unsubscribe from PropertyChangeEvents. This works even if the MeshObject is
     * dead already.
     *
     * @param oldListener the listener to remove
     * @see #addPropertyChangeListener
     */
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener oldListener )
    {
        if( thePropertyChangeListeners != null ) {
            thePropertyChangeListeners.remove( oldListener );

            if( thePropertyChangeListeners.isEmpty() ) {
                thePropertyChangeListeners = null; // cleanup to not take up unnecessary memory
            }
        } else {
            log.error( "trying to remove listener from empty list" );
        }
    }

    /**
     * Determine whether there are local event subscribers to this MeshObject.
     * This works even if the MeshObject is dead already.
     *
     * @return true if there are local subscribers
     */
    public boolean hasPropertyChangeListener()
    {
        Iterator<PropertyChangeListener> iter = propertyChangeListenersIterator();
        return iter.hasNext();
    }

    /**
     * This method returns an Iterator over the currently subscribed PropertyChangeListeners.
     * This works even if the MeshObjet is dead already.
     *
     * @return the iterator over the currently subscribed PropertyChangeListeners
     */
    public Iterator<PropertyChangeListener> propertyChangeListenersIterator()
    {
        FlexiblePropertyChangeListenerSet listeners = thePropertyChangeListeners;
        if( listeners == null || listeners.isEmpty() ) {
            return ZeroElementIterator.<PropertyChangeListener>create();
        }
        return listeners.iterator();
    }

    /**
      * Fire an event indicating a change of a property of this MeshObject. This will actually
      * create a MeshObjectPropertyChangeEvent.
      *
      * @param thePropertyType the PropertyType whose value changed
      * @param oldValue the value of the PropertyValue prior to the change
      * @param newValue the value of the PropertyValue now, after the change
      * @param mb the MeshBase to use
      */
    protected void firePropertyChange(
            PropertyType  thePropertyType,
            PropertyValue oldValue,
            PropertyValue newValue,
            MeshBase      mb )
    {
        MeshObjectPropertyChangeEvent theEvent
                = new MeshObjectPropertyChangeEvent(
                        this,
                        thePropertyType,
                        oldValue,
                        newValue,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating a change in the set of neighbors of this MeshObject.
     * 
     * @param addedRoleTypes the newly added RoleTypes, if any
     * @param oldValue the Identifiers of the neighbors prior to the change
     * @param added the added MeshObjectIdentifier
     * @param newValue the Identifiers of the neighbors now, after the change
     * @param mb the MeshBase to use
     */
    protected void fireNeighborAdded(
            RoleType []             addedRoleTypes,
            MeshObjectIdentifier [] oldValue,
            MeshObjectIdentifier    added,
            MeshObjectIdentifier [] newValue,
            MeshBase                mb )
    {
        if( addedRoleTypes == null ) {
            addedRoleTypes = new RoleType[0];
        }
        MeshObjectNeighborAddedEvent theEvent
                = new MeshObjectNeighborAddedEvent(
                        this,
                        addedRoleTypes,
                        oldValue,
                        added,
                        newValue,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
      * Fire an event indicating a change in the set of neighbors of this MeshObject.
      *
      * @param oldValue the Identifiers of the neighbors prior to the change
      * @param removed the removed Identifier
      * @param newValue the Identifiers of the neighbors now, after the change
      * @param mb the MeshBase to use
      */
    protected void fireNeighborRemoved(
            MeshObjectIdentifier [] oldValue,
            MeshObjectIdentifier    removed,
            MeshObjectIdentifier [] newValue,
            MeshBase                mb )
    {
        MeshObjectNeighborRemovedEvent theEvent
                = new MeshObjectNeighborRemovedEvent(
                        this,
                        oldValue,
                        removed,
                        newValue,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more MeshTypes have been added to this MeshObject.
     *
     * @param addedTypes the added MeshTypes
     * @param mb the MeshBase to use
     */
    protected void fireTypesAdded(
            EntityType [] oldTypes,
            EntityType [] addedTypes,
            EntityType [] newTypes,
            MeshBase      mb )
    {
        MeshObjectTypeAddedEvent theEvent
                = new MeshObjectTypeAddedEvent(
                        this,
                        oldTypes,
                        addedTypes,
                        newTypes,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more MeshTypes have been removed from this MeshObject.
     *
     * @param removedTypes the removed MeshTypes
     * @param mb the MeshBase to use
     */
    protected void fireTypesRemoved(
            EntityType [] oldTypes,
            EntityType [] removedTypes,
            EntityType [] newTypes,
            MeshBase      mb )
    {
        MeshObjectTypeRemovedEvent theEvent
                = new MeshObjectTypeRemovedEvent(
                        this,
                        oldTypes,
                        removedTypes,
                        newTypes,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more RoleTypes were added to the relationship of this
     * MeshObject to another MeshObject.
     *
     * @param thisEnd the RoleTypes that were added
     * @param otherSide the other side of this relationship
     * @param mb the MeshBase to use
     */
    protected void fireTypesAdded(
            RoleType [] oldRoleTypes,
            RoleType [] addedRoleTypes,
            RoleType [] newRoleTypes,
            MeshObject  otherSide,
            MeshBase    mb )
    {
        if( oldRoleTypes == null ) {
            oldRoleTypes = new RoleType[0];
        }
        MeshObjectRoleAddedEvent theEvent
                = new MeshObjectRoleAddedEvent(
                        this,
                        oldRoleTypes,
                        addedRoleTypes,
                        newRoleTypes,
                        otherSide,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that one or more RoleTypes wwere removed from the relationship of this
     * MeshObject to another MeshObject.
     *
     * @param thisEnd the RoleTypes that were removed
     * @param otherSide the other side of this relationship
     * @param mb the MeshBase to use
     */
    protected void fireTypesRemoved(
            RoleType [] oldRoleTypes,
            RoleType [] removedRoleTypes,
            RoleType [] newRoleTypes,
            MeshObject  otherSide,
            MeshBase    mb )
    {
        if( newRoleTypes == null ) {
            newRoleTypes = new RoleType[0];
        }
        MeshObjectRoleRemovedEvent theEvent
                = new MeshObjectRoleRemovedEvent(
                        this,
                        oldRoleTypes,
                        removedRoleTypes,
                        newRoleTypes,
                        otherSide,
                        theTimeUpdated );

        mb.getCurrentTransaction().addChange( theEvent );

        firePropertyChange( theEvent );
    }

    /**
     * Fire an event indicating that this MeshObject was deleted.
     * We pass in the MeshBase, because the member variable has already been zero'd.
     * 
     * @param oldMeshBase the MeshBase this MeshObject used to belong to
     * @param canonicalMeshObjectName the canonical Identifier that this MeshObject used to have
     */
    protected void fireDeleted(
            MeshBase             oldMeshBase,
            MeshObjectIdentifier canonicalMeshObjectName,
            long                 time )
    {
        MeshObjectBecameDeadStateEvent theEvent
                = new MeshObjectBecameDeadStateEvent(
                        this,
                        canonicalMeshObjectName,
                        time );
        
        oldMeshBase.getCurrentTransaction().addChange( theEvent );
        
        firePropertyChange( theEvent );
    }

    /**
     * This internal helper fires PropertyChangeEvents.
     *
     * @param theEvent the PropertyChangeEvent to be fired
     */
    protected final void firePropertyChange(
            PropertyChangeEvent theEvent )
    {
        FlexiblePropertyChangeListenerSet listeners = thePropertyChangeListeners;

        if( listeners != null ) {
            listeners.fireEvent( theEvent );
        }
    }

    /**
     * Helper to check that we are within the proper Transaction boundaries.
     *
     * @throws TransactionException throw if this has been invoked outside of proper Transaction boundaries
     */
    public final void checkTransaction()
        throws
            TransactionException
    {
        checkAlive();

        Transaction tx = theMeshBase.getCurrentTransaction();
        if( tx == null ) {
            throw new TransactionException.NotWithinTransactionBoundaries( theMeshBase );
        }

        tx.checkThreadIsAllowed();
    }

    /**
     * Internal helper to check that we are within the proper Transaction boundaries.
     * We pass in the MeshBase, because this may be invokved when the member variable has been zero'd out already.
     *
     * @param mb the MeshBase
     */
    protected final void internalCheckTransaction(
            MeshBase mb )
        throws
            TransactionException
    {
        Transaction tx = mb.getCurrentTransaction();
        if( tx == null ) {
            throw new TransactionException.NotWithinTransactionBoundaries( mb );
        }

        tx.checkThreadIsAllowed();
    }

    /**
     * Update the lastUpdated property. This does not trigger an event generation -- not necessary.
     * This may be overridden.
     */
    protected void updateLastUpdated()
    {
        updateLastUpdated( -1L, theTimeUpdated );
    }

    /**
     * Update the lastRead property. This does not trigger an event generation -- not necessary.
     * This may be overridden.
     */
    protected void updateLastRead()
    {
        updateLastRead( -1L, theTimeRead );
    }

    /**
     * Update the lastUpdated property. This does not trigger an event generation -- not necessary.
     * This may be overridden.
     *
     * @param timeUpdated the time to set to, or -1L to indicate the current time
     * @param lastTimeUpdated the time this MeshObject was updated last before
     */
    protected abstract void updateLastUpdated(
            long timeUpdated,
            long lastTimeUpdated );

    /**
     * Update the lastRead property. This does not trigger an event generation -- not necessary.
     * This may be overridden.
     *
     * @param timeRead the time to set to, or -1L to indicate the current time
     * @param lastTimeRead the time this MeshObject was read last before
     */
    protected abstract void updateLastRead(
            long timeRead,
            long lastTimeRead );
    
    /**
     * Check whether it is permitted to set this MeshObject's auto-delete time to the given value.
     * Subclasses may override this.
     *
     * @param newValue the proposed new value for the auto-delete time
     */
    public void checkPermittedSetTimeAutoDeletes(
            long newValue )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedSetTimeAutoDeletes( this, newValue );
        }
    }

    /**
     * Check whether it is permitted to set this MeshObject's given property to the given
     * value. Subclasses may override this.
     *
     * @param thePropertyType the PropertyType identifing the property to be modified
     * @param newValue the proposed new value for the property
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedSetProperty(
            PropertyType  thePropertyType,
            PropertyValue newValue )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedSetProperty( this, thePropertyType, newValue );
        }
    }

    /**
     * Check whether it is permitted to obtain this MeshObject's given property. Subclasses
     * may override this.
     *
     * @param thePropertyType the PropertyType identifing the property to be read
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedGetProperty(
            PropertyType thePropertyType )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedGetProperty( this, thePropertyType );
        }
    }

    /**
     * Check whether it is permitted to determine whether or not this MeshObject is blessed with
     * the given type. Subclasses may override this.
     * 
     * @param type the EntityType whose blessing we wish to check
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedBlessedBy(
            EntityType type )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedBlessedBy( this, type );
        }
    }

    /**
     * Check whether it is permitted to bless this MeshObject with the given EntityTypes. Subclasses
     * may override this.
     * 
     * @param types the EntityTypes with which to bless
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedBless(
            EntityType [] types )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedBless( this, types );
        }
    }

    /**
     * Check whether it is permitted to unbless this MeshObject from the given EntityTypes. Subclasses
     * may override this.
     * 
     * @param types the EntityTypes from which to unbless
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedUnbless(
            EntityType [] types )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedUnbless( this, types );
        }
    }    
    
    /**
     * Check whether it is permitted to bless the relationship to the otherObject with the
     * provided RoleTypes. Subclasses
     * may override this.
     * 
     * @param thisEnds the RoleTypes to bless the relationship with
     * @param otherObject the neighbor to which this MeshObject is related
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedBless(
            RoleType [] thisEnds,
            MeshObject  otherObject )
        throws
            NotPermittedException
    {
        checkAlive();
        otherObject.checkAlive();

        if( theMeshBase != otherObject.getMeshBase() ) {
            throw new IllegalArgumentException( "Cannot relate MeshObjects held in different MeshBases" );
        }

        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedBless( this, thisEnds, otherObject );
        }
    }

    /**
     * Check whether it is permitted to unbless the relationship to the otherObject from the
     * provided RoleTypes. Subclasses
     * may override this.
     * 
     * @param thisEnds the RoleTypes to unbless the relationship from
     * @param otherObject the neighbor to which this MeshObject is related
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedUnbless(
            RoleType [] thisEnds,
            MeshObject  otherObject )
        throws
            NotPermittedException
    {
        if( theMeshBase == null ) {
            return; // this is invoked on dead object
        }
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedUnbless( this, thisEnds, otherObject );
        }
    }    
    
    /**
     * Check whether it is permitted to traverse the given ByRoleType from this MeshObject to the
     * given MeshObject. Subclasses
     * may override this.
     * 
     * @param toTraverse the ByRoleType to traverse
     * @param otherObject the reached MeshObject in the traversal
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedTraversal(
            RoleType   toTraverse,
            MeshObject otherObject )
        throws
            NotPermittedException
    {
        checkAlive();
        otherObject.checkAlive();

        if( theMeshBase != otherObject.getMeshBase() ) {
            throw new IllegalArgumentException( "Cannot traverse to MeshObjects held in different MeshBases" );
        }

        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedTraversal( this, toTraverse, otherObject );
        }
    }

    /**
     * Check whether it is permitted to bless the relationship with the given otherObject with
     * the given thisEnds RoleTypes. Subclasses
     * may override this.
     * 
     * @param thisEnds the RoleTypes to bless the relationship with
     * @param otherObject the neighbor to which this MeshObject is related
     * @param roleTypesToAsk the RoleTypes, of the relationship with RoleTypesToAskUsed, which to as
     * @param roleTypesToAskUsed the neighbor MeshObject whose rules may have an opinion on the blessing of the relationship with otherObject
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedBless(
            RoleType [] thisEnds,
            MeshObject  otherObject,
            RoleType [] roleTypesToAsk,
            MeshObject  roleTypesToAskUsed )
        throws
            NotPermittedException
    {
        checkAlive();
        otherObject.checkAlive();

        if( theMeshBase != otherObject.getMeshBase() ) {
            throw new IllegalArgumentException( "Cannot relate MeshObjects held in different MeshBases" );
        }

        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedBless( this, thisEnds, otherObject, roleTypesToAsk, roleTypesToAskUsed );
        }
    }

    /**
     * Check whether it is permitted to unbless the relationship from the given otherObject with
     * the given thisEnds RoleTypes. Subclasses
     * may override this.
     * 
     * @param thisEnds the RoleTypes to unbless the relationship from
     * @param otherObject the neighbor to which this MeshObject is related
     * @param roleTypesToAsk the RoleTypes, of the relationship with RoleTypesToAskUsed, which to as
     * @param roleTypesToAskUsed the neighbor MeshObject whose rules may have an opinion on the blessing of the relationship with otherObject
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedUnbless(
            RoleType [] thisEnds,
            MeshObject  otherObject,
            RoleType [] roleTypesToAsk,
            MeshObject  roleTypesToAskUsed )
        throws
            NotPermittedException
    {
        checkAlive();
        
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedUnbless( this, thisEnds, otherObject, roleTypesToAsk, roleTypesToAskUsed );
        }
    }

    /**
     * Check whether it is permitted to delete this MeshObject. This checks both whether the
     * MeshObject itself may be deleted, and whether the relationships it participates in may
     * be deleted (which in turn depends on whether the relationships may be unblessed).
     * Subclasses may override this.
     *
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedDelete()
        throws
            NotPermittedException
    {
        checkAlive();
        
        // we need to check for unblessing all current relationships, unrelating, and unblessing ourselves
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr != null ) {
            accessMgr.checkPermittedDelete( this );
        }
    }

    /**
     * Internal helper to allocate the theMeshTypes property.
     *
     * @return the object to become the theMeshTypes property
     */
    protected final HashMap<EntityType,WeakReference<TypedMeshObjectFacade>> createMeshTypes()
    {
        return new HashMap<EntityType,WeakReference<TypedMeshObjectFacade>>();
    }

    /**
     * Internal helper to allocate the theProperties property.
     *
     * @return the object to become the theProperties property
     */
    protected final HashMap<PropertyType,PropertyValue> createProperties()
    {
        return new HashMap<PropertyType,PropertyValue>();
    }

    /**
     * Convert into a String representation, for debugging.
     *
     * @return String representation
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theIdentifier",
                    "theTimeCreated",
                    "theTimeUpdated",
                    "theTimeRead",
                    "theTimeExpires",
                    "theProperties",
                    "theMeshTypes"
                },
                new Object[] {
                    theIdentifier,
                    theTimeCreated,
                    theTimeUpdated,
                    theTimeRead,
                    theTimeExpires,
                    theProperties,
                    theMeshTypes != null ? theMeshTypes.keySet() : null
                });
    }

    /**
      * The MeshBase in which this MeshObject lives. If this is null, the MeshObject
      * is dead.
      */
    protected MeshBase theMeshBase;

    /**
      * Storage for the Identifier property.
      */
    protected MeshObjectIdentifier theIdentifier;

    /**
     * The time this MeshObject was created. This is a long in System.currentTimeMillis() format.
     */
    protected long theTimeCreated;

    /**
     * The time this MeshObject was last updated. This is a long in System.currentTimeMillis() format.
     */
    protected long theTimeUpdated;

    /**
     * The time this MeshObject was last read. This is a long in System.currentTimeMillis() format.
     */
    protected long theTimeRead;

    /**
     * The time this MeshObject expires. This is a long in System.currentTimeMillis() format.
     * If -1, it will never expire.
     */
    protected long theTimeExpires;

    /**
     * The set of properties with their types and values. This is allocated as needed.
     */
    protected HashMap<PropertyType,PropertyValue> theProperties;

    /**
     * The set of MeshTypes with their respective facades. This is allocated as needed.
     */
    protected HashMap<EntityType,WeakReference<TypedMeshObjectFacade>> theMeshTypes;

    /**
      * The current set of PropertyChangeListeners.
      */
    protected FlexiblePropertyChangeListenerSet thePropertyChangeListeners;
}
