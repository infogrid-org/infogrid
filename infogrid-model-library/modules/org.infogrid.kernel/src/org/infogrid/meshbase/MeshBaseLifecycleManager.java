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

import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.TypedMeshObjectFacade;
import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObjectFactory;
import org.infogrid.mesh.NotPermittedException;

import org.infogrid.model.primitives.EntityType;

import org.infogrid.meshbase.transaction.TransactionException;

/**
 * <p>An object that knows how to manage the life cycle of a MeshObject. It
 *    is affiated with a MeshBase, which provides a MeshBaseLifecycleManager
 *    implementation that is appropriate for its own implementation.</p>
 * <p>To bulk load, use an implementation of {@link BulkLoader BulkLoader} and
 *    instantiate the resulting {@link org.infogrid.mesh.externalized.ExternalizedMeshObject ExternalizedMeshObjects}
 *    using {@link #loadExternalizedMeshObject}.</p>
 */
public interface MeshBaseLifecycleManager
        extends
            ParserFriendlyExternalizedMeshObjectFactory
{
    /**
     * Obtain the MeshBase that this MeshBaseLifecycleManager works on.
     * 
     * @return the MeshBase that this MMeshBaseLifecycleManagerworks on
     */
    public abstract MeshBase getMeshBase();

    /**
      * <p>Create a new MeshObject without a type.
      * This call is a "semantic create" which means that a new, semantically distinct object
      * is to be created.</p>
      *
      * <p>Before this operation can be successfully invoked, a Transaction must be active
      * on this Thread.>/p>
      *
      * @return the created MeshObject
      * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
      */
    public abstract MeshObject createMeshObject()
        throws
            TransactionException;

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
    public abstract MeshObject createMeshObject(
            EntityType type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException;

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
    public abstract MeshObject createMeshObject(
            EntityType [] types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException;

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
     * @throws IdMeshObjectIdentifierNotUniqueExceptionf a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException;

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
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType     type )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException;

    /**
     * <p>This is a convenience method to create a MeshObject with zero or more EntityTypes.</p>
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
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []  types )
        throws
            IsAbstractException,
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException;

    /**
     * <p>Create a new MeshObject without a type.
     * This call is a "semantic create" which means that a new, semantically distinct object
     * is to be created.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws MeshObjectIdentifierNotUniqueException if a MeshObject exists already in this MeshBase with the specified MeshObjectIdentifier
     */
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            long           timeCreated,
            long           timeUpdated,
            long           timeRead,
            long           timeExpires )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException;

    /**
     * <p>This is a convenience method to create a MeshObject with exactly one EntityType.</p>
     * 
     * <p>Before this operation can be successfully invoked, a Transaction must be active
     * on this Thread.>/p>
     * 
     * @param identifier the Identifier of the to-be-created MeshObject. If this is null,
     *                        automatically create a suitable Identifier.
     * @param type the EntityType with which the MeshObject will be blessed
     * @param timeCreated the time when this MeshObject was semantically created, in System.currentTimeMillis() format
     * @param timeUpdated the time when this MeshObject was last updated, in System.currentTimeMillis() format
     * @param timeRead the time when this MeshObject was last read, in System.currentTimeMillis() format
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws MeshObjectIdentifierNotUniqueException a MeshObject exists already in this MeshBase with the specified MeshObjectIdentifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType     type,
            long           timeCreated,
            long           timeUpdated,
            long           timeRead,
            long           timeExpires )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;

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
     * @param timeExpires the time this MeshObject will expire, in System.currentTimeMillis() format
     * @return the created MeshObject
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExternalNIdentifierNotUniqueExceptionf a MeshObject exists already in this MeshBase with the specified Identifier
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract MeshObject createMeshObject(
            MeshObjectIdentifier identifier,
            EntityType []  types,
            long           timeCreated,
            long           timeUpdated,
            long           timeRead,
            long           timeExpires )
        throws
            IsAbstractException,
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException;
    
    /**
     * <p>Semantically delete a MeshObject.</p>
     * 
     * <p>This call is a "semantic delete", which means that an existing
     * object will go away in all its replicas. Due to time lag, the object
     * may still exist in certain replicas in other places for a while, but
     * the request to deleteMeshObjects all objects is in the queue and will get there
     * eventually.</p>
     * 
     * @param theObject the MeshObject to be semantically deleted
     * @throws DoReadOnlyException thrown if theObject does not have update rights, and cannot acquire them
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void deleteMeshObject(
            MeshObject theObject )
        throws
            TransactionException,
            NotPermittedException;

    /**
     * <p>Semantically deleteMeshObjects a number of MeshObjects at the same time.</p>
     * 
     * <p>This call either succeeds or fails in total: if one or more of the specified MeshObject cannot be
     *    deleted for some reason, none of the other MeshObjects will be deleted either.</p>
     * 
     * 
     * @param theObjects the MeshObjects to be semantically deleted
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void deleteMeshObjects(
            MeshObject [] theObjects )
        throws
            TransactionException,
            NotPermittedException;

    /**
     * Create a typed MeshObjectFacade for a MeshObject. This should generally not be invoked
     * by the application programmer. Use {@link MeshObject#getTypedFacadeFor MeshObject.getTypedFacadeFor}.
     *
     * @param object the MeshObject for which to create a TypedMeshObjectFacade
     * @param type the EntityType for the TypedMeshObjectFacade
     * @return the created TypedMeshObjectFacade
     */
    public abstract TypedMeshObjectFacade createTypedMeshObjectFacade(
            MeshObject object,
            EntityType type );

    /**
      * Determine the implementation class for an TypedMeshObjectFacade for a EntityType.
      * As an application developer, you should not usually have any reason to invoke this.
      *
      * @param theObjectType the type object
      * @return the Class
      * @throws ClassNotFoundException thrown if for some reason, this Class could not be found
      */
    public abstract Class getImplementationClass(
            EntityType theObjectType )
        throws
            ClassNotFoundException;

    /**
     * Externally load a MeshObject.
     * 
     * @param theExternalizedObject the externalized representation of the MeshObject
     * @return the created MeshObject
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     */
    public abstract MeshObject loadExternalizedMeshObject(
            ExternalizedMeshObject theExternalizedObject )
        throws
            TransactionException;
}
