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

package org.infogrid.meshbase.a;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.infogrid.mesh.AbstractMeshObject;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.TypeInitializer;
import org.infogrid.mesh.TypedMeshObjectFacade;
import org.infogrid.mesh.a.AMeshObject;
import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.security.MustNotDeleteHomeObjectException;
import org.infogrid.meshbase.AbstractMeshBaseLifecycleManager;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.primitives.DataType;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.EnumeratedDataType;
import org.infogrid.model.primitives.EnumeratedValue;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.primitives.SubjectArea;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;

/**
 * A MeshBaseLifecycleManager appropriate for the AMeshBase implementation of MeshBase. 
 */
public class AMeshBaseLifecycleManager
        extends
            AbstractMeshBaseLifecycleManager
{
    private static final Log log = Log.getLogInstance( AMeshBaseLifecycleManager.class ); // our own, private logger

    /**
     * Constructor. The application developer should not call this or a subclass constructor; use
     * MeshBase.getMeshObjectLifecycleManager() instead.
     * 
     * @param base the AMeshBase on which this MeshBaseLifecycleManager works
     */
    protected AMeshBaseLifecycleManager(
            AMeshBase base )
    {
        super( base );
    }

    /**
     * <p>Create a new MeshObject without a type
     * and an automatically created MeshObjectIdentifier.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is created.</p>
     *
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     *
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public AMeshObject createMeshObject()
        throws
            TransactionException,
            NotPermittedException
    {
        MeshObjectIdentifier identifier   = theMeshBase.getMeshObjectIdentifierFactory().createMeshObjectIdentifier();
        long                 time         = determineCreationTime();
        long                 autoExpires;
        
        if( DEFAULT_RELATIVE_TIME_AUTO_DELETES > 0 ) {
            autoExpires = time + DEFAULT_RELATIVE_TIME_AUTO_DELETES;
        } else {
            autoExpires = DEFAULT_RELATIVE_TIME_AUTO_DELETES;
        }
        try {
            return createMeshObject( identifier, time, time, time, autoExpires );

        } catch( MeshObjectIdentifierNotUniqueException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
     * <p>Create a new MeshObject without a type, but with provided time stamps
     * and a provided MeshObjectIdentifier.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the identifier of the to-be-created MeshObject. This must not be null.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws MeshObjectIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public synchronized AMeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            long                 timeCreated,
            long                 timeUpdated,
            long                 timeRead,
            long                 timeExpires )
        throws
            MeshObjectIdentifierNotUniqueException,
            TransactionException,
            NotPermittedException
    {
        if( identifier == null ) {
            throw new IllegalArgumentException( "null Identifier" );
        }

        AccessManager access = theMeshBase.getAccessManager();
        if( access != null ) {
            access.checkPermittedCreate( identifier );
        }

        long now = determineCreationTime();
        if( timeCreated < 0 ) {
            timeCreated = now;
        }
        if( timeUpdated < 0 ) {
            timeUpdated = now;
        }
        if( timeRead < 0 ) {
            timeRead = now;
        }
        // don't need to check timeExpires

        AMeshBase realBase = (AMeshBase) theMeshBase;

        Transaction tx = realBase.checkTransaction();

        MeshObject existing = findInStore( identifier );
        if( existing != null ) {
            throw new MeshObjectIdentifierNotUniqueException( existing );
        }

        AMeshObject ret = instantiateMeshObjectImplementation(
                identifier,
                timeCreated,
                timeUpdated,
                timeRead,
                timeExpires );

        putIntoStore( ret );

        tx.addChange( createCreatedEvent( ret ));

        assignOwner( ret );

        return ret;
    }

    /**
     * <p>Semantically delete several MeshObjects at the same time.</p>
     * 
     * <p>This call is a "semantic delete", which means that an existing
     * MeshObject will go away in all its replicas. Due to time lag, the MeshObject
     * may still exist in certain replicas in other places for a while, but
     * the request to deleteMeshObjects all objects is in the queue and will get there
     * eventually.</p>
     * 
     * <p>This call either succeeds or fails in total: if one or more of the specified MeshObject cannot be
     *    deleted for some reason, none of the other MeshObjects will be deleted either.</p>
     * 
     * @param theObjects the MeshObjects to be semantically deleted
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public synchronized void deleteMeshObjects(
            MeshObject [] theObjects )
        throws
            TransactionException,
            NotPermittedException
    {
        AMeshBase realBase = (AMeshBase) theMeshBase;
        long      now      = System.currentTimeMillis();
        
        Transaction tx = realBase.checkTransaction();

        MeshObject home = realBase.getHomeObject();

        for( int i=0 ; i<theObjects.length ; ++i ) {
            if( theObjects[i] == null ) {
                throw new NullPointerException( "MeshObject at index " + i + " is null" );
            }
            if( theObjects[i].getMeshBase() != realBase ) {
                throw new IllegalArgumentException( "cannot delete MeshObjects in a different MeshBases" );
            }
            if( theObjects[i] == home ) {
                throw new MustNotDeleteHomeObjectException( home );
            }
        }
        for( int i=0 ; i<theObjects.length ; ++i ) {
            ((AMeshObject)theObjects[i]).checkPermittedDelete(); // this may throw NotPermittedException
        }
        for( int i=0 ; i<theObjects.length ; ++i ) {
            AMeshObject     current           = (AMeshObject) theObjects[i];
            MeshObjectIdentifier currentIdentifier = current.getIdentifier();
            
            current.delete();
            removeFromStore( current.getIdentifier() );

            tx.addChange( createDeletedEvent( current, currentIdentifier, now ));
        }
    }

    /**
     * Create a typed {@link TypedMeshObjectFacade} for a MeshObject. This should generally
     * not be invoked by the application programmer. Use
     * {@link MeshObject#getTypedFacadeFor MeshObject.getTypedFacadeFor}.
     *
     * @param object the MeshObject for which to create a TypedMeshObjectFacade
     * @param type the EntityType for the TypedMeshObjectFacade
     * @return the created TypedMeshObjectFacade
     */
    public TypedMeshObjectFacade createTypedMeshObjectFacade(
            MeshObject object,
            EntityType type )
    {
        if( object == null ) {
            throw new IllegalArgumentException( "null MeshObject" );
        }
        if( type == null ) {
            throw new IllegalArgumentException( "null MeshType" );
        }
        
        if( !object.isBlessedBy( type )) {
            throw new IllegalArgumentException( "this MeshObject is not currently blessed with this MeshType" );
        }

        // now instantiate
        Class theClass = null;
        try {
            theClass = getImplementationClass( type );

            Constructor theConstructor = theClass.getDeclaredConstructor(
                        new Class [] {
                                MeshObject.class
                        } );

            TypedMeshObjectFacade ret = (TypedMeshObjectFacade) theConstructor.newInstance(
                    new Object [] {
                            object
                    } );
            
            return ret;

        } catch( ClassNotFoundException ex ) {
            log.error( "Could not find class for type " + type, ex );
        } catch( IllegalAccessException ex ) {
            log.error( "Could not invoke constructor of class " + theClass, ex );
        } catch( InstantiationException ex ) {
            log.error( "Could not instantiate class " + theClass, ex );
        } catch( InvocationTargetException ex ) {

            log.error(
                    "Invocation target exc in constructor of class "
                            + theClass
                            + ", original follows",
                    ex );
            log.error( ex.getTargetException() );

        } catch( NoSuchMethodException ex ) {
            log.error( "Did not find constructor of class " + theClass, ex );
        }
        log.error( "cannot find an implementation class for the ObjectType with Identifier: " + type.getIdentifier() );
        return null;
    }
    
    /**
      * Determine the implementation class for an TypedMeshObjectFacade for an EntityType.
      * As an application developer, you should not usually have any reason to invoke this.
      *
      * @param theObjectType the type object
      * @return the Class
      * @throws ClassNotFoundException thrown if for some reason, this Class could not be found
      */
    @SuppressWarnings( "unchecked" )
    public Class<? extends TypedMeshObjectFacade> getImplementationClass(
            EntityType theObjectType )
        throws
            ClassNotFoundException
    {
        SubjectArea theSa = theObjectType.getSubjectArea();

        StringBuffer className = new StringBuffer( 64 );
        className.append( theSa.getName().value() );
        className.append( ".V" );
        if( theSa.getVersionNumber() != null ) {
            className.append( theSa.getVersionNumber().value() );
        }
        className.append( ".Impl" );
        className.append( theObjectType.getName().value() );

        Class ret = Class.forName( className.toString(), true, theObjectType.getClassLoader() );
        return (Class<? extends TypedMeshObjectFacade>) ret;
        // this cast is correct by construction
    }

    /**
     * Helper method to instantiate the right subclass of MeshObject. This makes the creation
     * of subclasses of AMeshBase and this class much easier.
     * 
     * @param identifier the identifier of the to-be-created MeshObject. This must not be null.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     */
    protected AMeshObject instantiateMeshObjectImplementation(
            MeshObjectIdentifier identifier,
            long                 timeCreated,
            long                 timeUpdated,
            long                 timeRead,
            long                 timeExpires )
    {
        AMeshObject ret = new AMeshObject(
                identifier,
                (AMeshBase) theMeshBase,
                timeCreated,
                timeUpdated,
                timeRead,
                timeExpires );
        
        return ret;
    }

    /**
     * Recreate a MeshObject that had been garbage-collected earlier
     * 
     * @param theObjectBeingParsed external form of the to-be-recreated MeshObject
     * @return the recreated AMeshObject
     */
    public AMeshObject recreateMeshObject(
            ExternalizedMeshObject theObjectBeingParsed )
    {
        AMeshBase realBase = (AMeshBase) theMeshBase;
        ModelBase mb       = realBase.getModelBase();

        MeshTypeIdentifier [] meshTypeNames = theObjectBeingParsed.getExternalTypeIdentifiers();
        EntityType         [] types         = new EntityType[ meshTypeNames.length ];

        int typeCounter = 0;
        for( int i=0 ; i<meshTypeNames.length ; ++i ) {
            try {
                // it's possible that the schema changed since we read this object. Try to recover.
                types[typeCounter] = (EntityType) mb.findMeshTypeByIdentifier( meshTypeNames[i] );
                typeCounter++; // make sure we do the increment after an exception might have been thrown
            } catch( Exception ex ) {
                log.warn( ex );
            }
        }
        if( typeCounter < types.length ) {
            types = ArrayHelper.copyIntoNewArray( types, 0, typeCounter, EntityType.class );
        }

        MeshTypeIdentifier [] propertyTypeNames = theObjectBeingParsed.getPropertyTypes();
        PropertyValue      [] propertyValues    = theObjectBeingParsed.getPropertyValues();
        
        HashMap<PropertyType,PropertyValue> properties = new HashMap<PropertyType,PropertyValue>();
        for( int i=0 ; i<propertyTypeNames.length ; ++i ) {
            try {
                // it's possible that the schema changed since we read this object. Try to recover.
                PropertyType propertyType = (PropertyType) mb.findMeshTypeByIdentifier( propertyTypeNames[i] );
                
                // now we patch EnumeratedValues
                DataType type = propertyType.getDataType();
                if( type instanceof EnumeratedDataType && propertyValues[i] instanceof EnumeratedValue ) {
                    EnumeratedDataType realType  = (EnumeratedDataType) type;
                    EnumeratedValue    realValue = realType.select( ((EnumeratedValue)propertyValues[i]).value() );
                    properties.put( propertyType, realValue );
                } else {
                    properties.put( propertyType, propertyValues[i] );
                }

            } catch( Exception ex ) {
                log.warn( ex );
            }
        }

        MeshObjectIdentifier [] otherSides = theObjectBeingParsed.getNeighbors();
        RoleType [][]     roleTypes  = new RoleType[ otherSides.length ][];

        for( int i=0 ; i<otherSides.length ; ++i ) {
            MeshTypeIdentifier [] currentRoleTypes = theObjectBeingParsed.getRoleTypesFor( otherSides[i] );
            
            roleTypes[i] = new RoleType[ currentRoleTypes.length ];
            typeCounter = 0;

            for( int j=0 ; j<currentRoleTypes.length ; ++j ) {
                try {
                    roleTypes[i][typeCounter] = mb.findRoleTypeByIdentifier( currentRoleTypes[j] );
                    typeCounter++; // make sure we do the increment after an exception might have been thrown
                } catch( Exception ex ) {
                    log.warn( ex );
                }
                if( typeCounter < roleTypes[i].length ) {
                    roleTypes[i] = ArrayHelper.copyIntoNewArray( roleTypes[i], 0, typeCounter, RoleType.class );
                }
            }
        }
                
        MeshObjectIdentifier [] equivalents = theObjectBeingParsed.getEquivalents();
        
        MeshObjectIdentifier [] leftRight = AMeshObjectEquivalenceSetComparator.SINGLETON.findLeftAndRightEquivalents(
                theObjectBeingParsed.getIdentifier(),
                equivalents );
        
        AMeshObject ret = instantiateRecreatedMeshObject(
                theObjectBeingParsed.getIdentifier(),
                theObjectBeingParsed.getTimeCreated(),
                theObjectBeingParsed.getTimeUpdated(),
                theObjectBeingParsed.getTimeRead(),
                theObjectBeingParsed.getTimeExpires(),
                properties,
                types,
                leftRight,
                otherSides,
                roleTypes,
                theObjectBeingParsed );

        return ret;
    }

    /**
     * Factored out method to instantiate a recreated MeshObject. This exists to make
     * it easy to override it in subclasses.
     * 
     * @param identifier the identifier of the MeshObject
     * @param timeCreated the time it was created
     * @param timeUpdated the time it was last udpated
     * @param timeRead the time it was last read
     * @param timeExpires the time it will expire
     * @param properties the properties of the MeshObject
     * @param types the EntityTypes of the MeshObject
     * @param equivalents either an array of length 2, or null. If given, contains the left and right equivalence pointers.
     * @param otherSides the identifiers of the MeshObject's neighbors, if any
     * @param roleTypes the RoleTypes in which this MeshObject participates with its neighbors
     * @param theObjectBeingParsed the externalized representation of the MeshObject
     * @return the recreated AMeshObject
     */
    protected AMeshObject instantiateRecreatedMeshObject(
            MeshObjectIdentifier                identifier,
            long                                timeCreated,
            long                                timeUpdated,
            long                                timeRead,
            long                                timeExpires,
            HashMap<PropertyType,PropertyValue> properties,
            EntityType []                       types,
            MeshObjectIdentifier []             equivalents,
            MeshObjectIdentifier []             otherSides,
            RoleType [][]                       roleTypes,
            ExternalizedMeshObject              theObjectBeingParsed )
    {
        AMeshObject ret = new AMeshObject(
                identifier,
                (AMeshBase) theMeshBase,
                timeCreated,
                timeUpdated,
                timeRead,
                timeExpires,
                properties,
                types,
                equivalents,
                otherSides,
                roleTypes );

        return ret;
    }

    /**
     * Externally load a MeshObject.
     * 
     * @param theExternalizedObject the externalized representation of the MeshObject
     * @return the created MeshObject
     * @throws TransactionException thrown if invoked outside of proper Transaction boundaries
     */
    public AbstractMeshObject loadExternalizedMeshObject(
            ExternalizedMeshObject theExternalizedObject )
        throws
            TransactionException
    {
        Transaction tx = theMeshBase.checkTransaction();

        AbstractMeshObject ret = recreateMeshObject( theExternalizedObject );

        // re-initialize default values
        EntityType [] types = ret.getTypes();

        for( int i=0 ; i<types.length ; ++i ) {
            TypeInitializer init = ret.createTypeInitializer( types[i] );
            init.initialize( ret.getTimeUpdated() );
        }

        putIntoStore( ret );
        tx.addChange( createCreatedEvent( ret ));
        
        return ret;
    }
}
