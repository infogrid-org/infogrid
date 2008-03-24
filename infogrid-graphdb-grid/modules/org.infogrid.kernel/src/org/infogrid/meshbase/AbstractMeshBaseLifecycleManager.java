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

package org.infogrid.meshbase;

import org.infogrid.mesh.AbstractMeshObject;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObject;
import org.infogrid.meshbase.security.AccessManager;

import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.MeshObjectCreatedEvent;
import org.infogrid.meshbase.transaction.MeshObjectDeletedEvent;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;

import org.infogrid.util.logging.Log;

/**
 * Collects implementation methods common to various implementations of MeshBaseLifecycleManager.
 */
public abstract class AbstractMeshBaseLifecycleManager
        implements
            MeshBaseLifecycleManager
{
    private static final Log log = Log.getLogInstance( AbstractMeshBaseLifecycleManager.class ); // our own, private logger

    /**
     * Constructor. The application developer should not call this or a subclass constructor; use
     * MeshBase.getMeshObjectLifecycleManager() instead.
     * 
     * @param base the MeshBase on which this MeshBaseLifecycleManager works
     */
    protected AbstractMeshBaseLifecycleManager(
            MeshBase base )
    {
        theMeshBase = base;
    }

    /**
     * Obtain the MeshBase that this MeshBaseLifecycleManager works on.
     * 
     * @return the MeshBase that this MMeshBaseLifecycleManagerworks on
     */
    public MeshBase getMeshBase()
    {
        return theMeshBase;
    }

    /**
      * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
      *
      * <p>Before this operation can be successfully invoked, a Transaction must be active
      * on this Thread.>/p>
      *
      * @param type the EntityType with which the MeshObject will be blessed
      * @return the created MeshObject
      * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
      * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
      * @throws NotPermittedException thrown if the blessing operation is not permitted
      */
    public MeshObject createMeshObject(
            EntityType type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        MeshObject ret = createMeshObject();
        if( type != null ) {
            try {
                ret.bless( type );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }

    /**
      * <p>This is a convenience method to create a MeshObject with zero or more EntityTypes.</p>
      *
      * <p>Before this operation can be successfully invoked, a Transaction must be active
      * on this Thread.>/p>
      *
      * @param types the EntityTypes with which the MeshObject will be blessed
      * @return the created MeshObject
      * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
      * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
      * @throws NotPermittedException thrown if the blessing operation is not permitted
      */
    public MeshObject createMeshObject(
            EntityType [] types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException
    {
        MeshObject ret = createMeshObject();

        if( types != null && types.length > 0 ) {
            try {
                ret.bless( types );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }

    /**
     * <p>Create a new MeshObject without a type.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExtIdentifierNotUniqueExceptionrown if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public MeshObject createMeshObject(
            MeshObjectIdentifier identifier )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        long time = determineCreationTime();
        long autoExpires;
        
        if( DEFAULT_RELATIVE_TIME_AUTO_DELETES > 0 ) {
            autoExpires = time + DEFAULT_RELATIVE_TIME_AUTO_DELETES;
        } else {
            autoExpires = DEFAULT_RELATIVE_TIME_AUTO_DELETES;
        }

        return createMeshObject( identifier, time, time, time, autoExpires );
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param type the EntityType with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType      type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = createMeshObject( identifier );
        if( type != null ) {
            try {
                ret.bless( type );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []  types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException
    {
        MeshObject ret = createMeshObject( identifier );
        if( types != null && types.length > 0 ) {
            try {
                ret.bless( types );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * 
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param type the EntityType with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-deletedeleteMeshObjectystem.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNIdentifierNotUniqueExceptionf a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType     type,
            long           timeCreated,
            long           timeUpdated,
            long           timeRead,
            long           timeAutoDeletes )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        MeshObject ret = createMeshObject( identifier, timeCreated, timeUpdated, timeRead, timeAutoDeletes );
        
        if( type != null ) {
            try {
                ret.bless( type );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }

    /**
     * <p>This is a convenience method to create a MeshObject with zero or more EntityTypes.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeAutoDeletes the time this MeshObject will auto-delete, in MeshObjectystem.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNIdentifierNotUniqueExceptionf a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []  types,
            long           timeCreated,
            long           timeUpdated,
            long           timeRead,
            long           timeAutoDeletes )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        MeshObject ret = createMeshObject( identifier, timeCreated, timeUpdated, timeRead, timeAutoDeletes );
        
        if( types != null && types.length > 0 ) {
            try {
                ret.bless( types );

            } catch( EntityBlessedAlreadyException ex ) {
                log.error( ex );
            }
        }
        return ret;
    }
    
    /**
     * <p>Semantically deleteMeshObjects a MeshObject.</p>
     * 
     * <p>This call is a "semantic deleteMeshObjects", which means that an existing
     * object will go away in all its replicas. Due to time lag, the object
     * may still exist in certain replicas in other places for a while, but
     * the request to deleteMeshObjects all objects is in the queue and will get there
     * eventually.</p>
     * 
     * 
     * @param theObject the MeshObject to be semantically deleted
     * @throws DoReadOnlyException thrown if theObject does not have update rights, and cannot acquire them
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public void deleteMeshObject(
            MeshObject theObject )
        throws
            TransactionException,
            NotPermittedException
    {
        deleteMeshObjects( new MeshObject[] { theObject } );
    }


    /**
     * Instantiate an ExternalizedMeshObject that is appropriate to capture the information held by
     * the subtype of MeshObject used by this MeshBase. This is factored out so it can easily be
     * overridden in subclasses.
     * 
     * @return the ParserFriendlyExternalizedMeshObject.
     */
    public ParserFriendlyExternalizedMeshObject createParserFriendlyExternalizedMeshObject()
    {
        return new ParserFriendlyExternalizedMeshObject();
    }

    /**
     * Assign owner MeshObject to a newly created MeshObject. For subclasses only.
     *
     * @param obj the newly created MeshObject
     * @throws TransactionException thrown if invoked outside of proper Transaction boundaries
     */
    protected void assignOwner(
            MeshObject obj )
        throws
            TransactionException
    {
        AccessManager accessMgr = theMeshBase.getAccessManager();
        if( accessMgr == null ) {
            return; // no AccessManager
        }

        MeshObject caller = accessMgr.getCaller();
        if( caller == null ) {
            return; // no owner
        }

        accessMgr.assignOwner( obj, caller );
    }

    /**
     * Determine the current time for the purpose of assigning the right creation time to a
     * to-be-created MeshObject. This may be overridden in subclasses.
     *
     * @return the creation time to use, in System.currentTimeMillis() format
     */
    protected long determineCreationTime()
    {
        return System.currentTimeMillis();
    }

    /**
     * Overridable helper to create a MeshObjectCreatedEvent.
     *
     * @param createdObject the created MeshObject
     * @return the created event
     */
    protected Change createCreatedEvent(
            MeshObject createdObject )
    {
        Change ret = new MeshObjectCreatedEvent( theMeshBase, createdObject, createdObject.getTimeCreated() );
        return ret;
    } 

    /**
     * Overridable helper to create a MeshObjectDeletedEvent.
     * 
     * @param canonicalIdentifier the canonical Identifier of the deleted MeshObject
     * @param deletedObject the deleted MeshObject
     * @return the created event
     */
    protected Change createDeletedEvent(
            MeshObjectIdentifier canonicalIdentifier,
            MeshObject      deletedObject,
            long            time )
    {
        Change ret = new MeshObjectDeletedEvent( theMeshBase, canonicalIdentifier, deletedObject, time );
        return ret;
    }

    /**
     * Helper method that allows our subclasses to access the Store without having to expose it publicly.
     * 
     * @param identifier the Identifier of the MeshObject
     * @return the found MeshObject, or none.
     */
    protected MeshObject findInStore(
            MeshObjectIdentifier identifier )
    {
        AbstractMeshBase realBase = (AbstractMeshBase) theMeshBase;
        MeshObject       ret      = realBase.theCache.get( identifier );
        return ret;
    }

    /**
     * Helper method that allows our subclasses to access the Store without having to expose it publicly.
     *
     * @param obj the MeshObject to put into the store
     * @return the MeshObject in the store previously with the same Identifier
     */
    protected MeshObject putIntoStore(
            AbstractMeshObject obj )
    {
        AbstractMeshBase realBase = (AbstractMeshBase) theMeshBase;
        MeshObject       ret      = realBase.theCache.put( obj.getIdentifier(), obj );
        return ret;
    }

    /**
     * Helper method that allows our subclasses to access the Store without having to expose it publicly.
     * 
     * @param identifier the Identifier of the MeshObject
     * @return the found MeshObject, or none.
     */
    protected MeshObject removeFromStore(
            MeshObjectIdentifier identifier )
    {
        AbstractMeshBase realBase = (AbstractMeshBase) theMeshBase;
        MeshObject       ret      = realBase.theCache.remove( identifier );
        return ret;
    }

    /**
     * The MeshBase that we work on.
     */
    protected final MeshBase theMeshBase;
    
    /**
     * The default time at which MeshObjects auto-deleteMeshObjects, unless otherwise specified.
     * This is given as a relative time, from the current time. If -1 is given, it means
     * "never".
     */
    public static final long DEFAULT_RELATIVE_TIME_AUTO_DELETES = -1L;
}
