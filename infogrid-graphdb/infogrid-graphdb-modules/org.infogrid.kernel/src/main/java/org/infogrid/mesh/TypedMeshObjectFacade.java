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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh;

import java.beans.PropertyChangeListener;
import java.util.Iterator;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.Role;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.IsDeadException;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * The abstract interface of all type-safe facades for MeshObjects that are typically
 * generated by the InfoGrid code generator.
 */
public interface TypedMeshObjectFacade
        extends
            HasIdentifier,
            CanBeDumped
{
    /**
     * Obtain the underlying MeshObject. It is named with an underscore, so code-generated
     * code is less likely to interfere with it.
     *
     * @return the underlying MeshObject
     */
    public abstract MeshObject get_Delegate();

    /**
     * Obtain the MeshObjectType for which this is the facade. It is named with an underscore, so code-generated
     * code is less likely to interfere with it.
     *
     * @return the MeshObjectType
     */
    public abstract EntityType get_Type();

    /**
     * Obtain the globally unique identifier of this MeshObject.
     *
     * @return the globally unique identifier of this MeshObject
     */
    public abstract MeshObjectIdentifier getIdentifier();

    /**
     * Obtain the MeshBase that contains this MeshObject. This is immutable for the
     * lifetime of this instance.
     *
     * @return the MeshBase that contains this MeshObject.
     */
    public abstract MeshBase get_MeshBase();

    /**
     * Obtain the time of creation of this MeshObject. This is immutable for the
     * lifetime of the MeshObject.
     *
     * @return the time this MeshObject was created, in System.currentTimeMillis() format
     */
    public abstract long getTimeCreated();

    /**
     * Obtain the time of last update of this MeshObject. This changes automatically
     * every time the MeshObject is changed.
     *
     * @return the time this MeshObject was last updated, in System.currentTimeMillis() format
     */
    public abstract long getTimeUpdated();

    /**
     * Obtain the time of the last reading operation of this MeshObject. This changes automatically
     * every time the MeshObject is read.
     *
     * @return the time this MeshObject was last read, in System.currentTimeMillis() format
     */
    public abstract long getTimeRead();

    /**
     * Set the time when this MeshObject expires. If -1, it never does.
     *
     * @param newValue the new value, in <code>System.currentTimeMillis()</code> format
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void setTimeExpires(
            long newValue )
        throws
            NotPermittedException;

    /**
     * Obtain the time when this MeshObject expires. If this returns -1, it never does.
     *
     * @return the time at which this MeshObject expires, in System.currentTimeMillis() format
     */
    public abstract long getTimeExpires();

    /**
     * Determine whether this MeshObject is dead and should not be used any further.
     *
     * @return true if the MeshObject is dead
     */
    public abstract boolean getIsDead();

    /**
     * Throw an IsDeadException if this MeshObject is dead and should not be used any further.
     * Do nothing if this MeshObject is alive.
     *
     * @throws IsDeadException thrown if this MeshObject is dead already
     */
    public abstract void checkAlive()
        throws
            IsDeadException;

// --

    /**
     * Traverse from this MeshObject to all directly related MeshObjects. Directly
     * related MeshObjects are those MeshObjects that are participating in a
     * relationship with this MeshObject.
     *
     * @return the set of MeshObjects that are directly related to this MeshObject
     */
    public abstract MeshObjectSet traverseToNeighborMeshObjects();

    /**
     * Traverse from this MeshObject to all directly related MeshObjects. Directly
     * related MeshObjects are those MeshObjects that are participating in a
     * relationship with this MeshObject. Specify whether to consider equivalents
     * as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well
     * @return the set of MeshObjects that are directly related to this MeshObject
     */
    public abstract MeshObjectSet traverseToNeighborMeshObjects(
            boolean considerEquivalents );

    /**
     * Relate this MeshObject to another MeshObject. This does not bless the relationship.
     *
     * @param otherObject the MeshObject to relate to
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #unrelate
     * @see #relateAndBless
     */
    public abstract void relate(
            TypedMeshObjectFacade otherObject )
        throws
            RelatedAlreadyException,
            TransactionException;

    /**
     * Unrelate this MeshObject from another MeshObject. This will also remove all blessings from the relationship.
     *
     * @param otherObject the MeshObject to unrelate from
     * @throws NotRelatedException thrown if this MeshObject is not already related to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #relate
     */
    public abstract void unrelate(
            TypedMeshObjectFacade otherObject )
        throws
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
     * Determine whether this MeshObject is related to another MeshObject.
     *
     * @param otherObject the MeshObject to which this MeshObject may be related
     * @return true if this MeshObject is currently related to otherObject
     */
    public abstract boolean isRelated(
            TypedMeshObjectFacade otherObject );

    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided RoleType.
     *
     * @param thisEnd the RoleType of the RelationshipType that is instantiated at the end that this MeshObject is attached to
     * @param otherObject the MeshObject whose relationship to this MeshObject shall be blessed
     * @throws RoleTypeBlessedAlreadyException thrown if the relationship to the other MeshObject is blessed
     *         already with this RoleType
     * @throws EntityNotBlessedException thrown if this MeshObject is not blessed by a requisite EntityType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws IsAbstractException thrown if the RoleType belongs to an abstract RelationshipType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #relate
     * @see #relateAndBless
     * @see #unrelate
     */
    public abstract void blessRelationship(
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeBlessedAlreadyException,
            EntityNotBlessedException,
            NotRelatedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided RoleTypes.
     * As a result, this relationship will support either all RoleTypes or none.
     *
     * @param thisEnd the RoleTypes of the RelationshipTypes that are instantiated at the end that this MeshObject is attached to
     * @param otherObject the MeshObject whose relationship to this MeshObject shall be blessed
     * @throws RoleTypeBlessedAlreadyException thrown if the relationship to the other MeshObject is blessed
     *         already with one ore more of the given RoleTypes
     * @throws EntityNotBlessedException thrown if this MeshObject is not blessed by a requisite EntityType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws IsAbstractException thrown if one of the RoleTypes belong to an abstract RelationshipType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #relate
     * @see #relateAndBless
     * @see #unrelate
     */
    public abstract void blessRelationship(
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeBlessedAlreadyException,
            EntityNotBlessedException,
            NotRelatedException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Convenience method to relate this MeshObject to another MeshObject, and bless the new relationship
     * with the provided RoleType.
     *
     * @param thisEnd the RoleType of the RelationshipType that is instantiated at the end that this MeshObject is attached to
     * @param otherObject the MeshObject to which a relationship is to be created and blessed
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws EntityNotBlessedException thrown if this MeshObject is not blessed by a requisite EntityType
     * @throws IsAbstractException thrown if the provided RoleType belongs to an abstract RelationshipType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #relate
     * @see #blessRelationship
     * @see #unrelate
     */
    public abstract void relateAndBless(
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            EntityNotBlessedException,
            RelatedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Convenience method to relate this MeshObject to another MeshObject, and bless the new relationship
     * with all of the provided RoleTypes. As a result, this relationship will support either all RoleTypes or none.
     *
     * @param thisEnd the RoleTypes of the RelationshipTypes that are to be instantiated at the end that this MeshObject is attached to
     * @param otherObject the MeshObject to which a relationship is to be created and blessed
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws EntityNotBlessedException thrown if this MeshObject is not blessed by a requisite EntityType
     * @throws IsAbstractException thrown if one of the provided RoleTypes belongs to an abstract RelationshipType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #relate
     * @see #blessRelationship
     * @see #unrelate
     */
    public abstract void relateAndBless(
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            EntityNotBlessedException,
            RelatedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided RoleType.
     *
     * @param thisEnd the RoleType of the RelationshipType at the end that this MeshObject is attached to, and that shall be removed
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws RoleTypeNotBlessedException thrown if the relationship to the other MeshObject does not support the RoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void unblessRelationship(
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided RoleTypes.
     * As a result, either all RoleTypes will be unblessed or none.
     *
     * @param thisEnd the RoleTypes of the RelationshipTypes at the end that this MeshObject is attached to, and that shall be removed
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws RoleTypeNotBlessedException thrown if the relationship to the other MeshObject does not support at least one of the RoleTypes
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void unblessRelationship(
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
      * Traverse a TraversalSpecification from this MeshObject to obtain a set of MeshObjects.
      * This will consider all MeshObjects equivalent to this one as the start MeshObject.
      *
      * @param theTraverseSpec the TraversalSpecification to traverse
      * @return the set of MeshObjects found as a result of the traversal
      */
    public abstract MeshObjectSet traverse(
            TraversalSpecification theTraverseSpec );

    /**
      * Traverse a TraversalSpecification from this MeshObject to obtain a set of MeshObjects.
      * Specify whether relationships of equivalent MeshObjects should be considered as well.
      *
      * @param theTraverseSpec the TraversalSpecification to traverse
      * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
      *        if false, only this MeshObject will be used as the start
      * @return the set of MeshObjects found as a result of the traversal
      */
    public abstract MeshObjectSet traverse(
            TraversalSpecification theTraverseSpec,
            boolean                considerEquivalents );

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in. This will return only one
     * instance of the same RoleType object, even if the MeshObject participates in this RoleType
     * multiple times with different other MeshObjects.
     *
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public abstract RoleType [] get_RoleTypes();

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in. This will return only one
     * instance of the same RoleType object, even if the MeshObject participates in this RoleType
     * multiple times with different other MeshObjects. Specify whether equivalent MeshObjects
     * should be considered as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
     *        if false, only this MeshObject will be used as the start
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public abstract RoleType [] get_RoleTypes(
            boolean considerEquivalents );

    /**
     * Obtain the Roles that this MeshObject currently participates in.
     *
     * @return the Roles that this MeshObject currently participates in.
     */
    public abstract Role [] get_Roles();

    /**
     * Obtain the Roles that this MeshObject currently participates in.
     * Specify whether relationships of equivalent MeshObjects
     * should be considered as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well
     *        if false, only this MeshObject will be used as the start
     * @return the Roles that this MeshObject currently participates in.
     */
    public abstract Role [] get_Roles(
            boolean considerEquivalents );

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in with the
     * specified other MeshObject.
     *
     * @param otherObject the other MeshObject
     * @return the RoleTypes that this MeshObject currently participates in.
     * @throws NotRelatedException thrown if this MeshObject and otherObject are not related
     */
    public abstract RoleType [] get_RoleTypes(
            TypedMeshObjectFacade otherObject )
        throws
            NotRelatedException;

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in with the
     * specified other MeshObject.
     * Specify whether relationships of equivalent MeshObjects should be considered
     * as well.
     *
     * @param otherObject the other MeshObject
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
     *        if false, only this MeshObject will be used as the start
     * @return the RoleTypes that this MeshObject currently participates in.
     * @throws NotRelatedException thrown if this MeshObject and otherObject are not related
     */
    public abstract RoleType [] get_RoleTypes(
            TypedMeshObjectFacade otherObject,
            boolean               considerEquivalents )
        throws
            NotRelatedException;

    /**
     * Add another MeshObject as an equivalent. All MeshObjects that are already equivalent
     * to this MeshObject, and all MeshObjects that are already equivalent to the newly
     * added MeshObject, are now equivalent.
     *
     * @param equiv the new equivalent
     * @throws EquivalentAlreadyException thrown if the provided MeshObject is already an equivalent of this MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void addAsEquivalent(
            TypedMeshObjectFacade equiv )
        throws
            EquivalentAlreadyException,
            TransactionException,
            NotPermittedException;

    /**
     * Obtain the set of MeshObjects, including this one, that are equivalent.
     * This always returns at least this MeshObject.
     *
     * @return the set of MeshObjects that are equivalent
     */
    public abstract MeshObjectSet get_Equivalents();

    /**
     * Remove this MeshObject as an equivalent from the set of equivalents. If this MeshObject
     * is not currently equivalent to any other MeshObject, this does nothing.
     *
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract void removeAsEquivalent()
        throws
            TransactionException,
            NotPermittedException;

    /**
     * Add a PropertyChangeListener.
     * This listener is added directly to the listener list, which prevents the
     * listener from being garbage-collected before this Object is being garbage-collected.
     *
     * @param newListener the to-be-added PropertyChangeListener
     * @see #addWeakPropertyChangeListener
     * @see #addSoftPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    public abstract void addDirectPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
     * Add a PropertyChangeListener.
     * This listener is added to the listener list using a <code>java.lang.ref.SoftReference</code>,
     * which allows the listener to be garbage-collected before this Object is being garbage-collected
     * according to the semantics of Java references.
     *
     * @param newListener the to-be-added PropertyChangeListener
     * @see #addDirectPropertyChangeListener
     * @see #addSoftPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    public abstract void addWeakPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
     * Add a PropertyChangeListener.
     * This listener is added to the listener list using a <code>java.lang.ref.WeakReference</code>,
     * which allows the listener to be garbage-collected before this Object is being garbage-collected
     * according to the semantics of Java references.
     *
     * @param newListener the to-be-added PropertyChangeListener
     * @see #addDirectPropertyChangeListener
     * @see #addWeakPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    public abstract void addSoftPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
     * Remove a PropertyChangeListener.
     *
     * @param oldListener the to-be-removed PropertyChangeListener
     * @see #addDirectPropertyChangeListener
     * @see #addWeakPropertyChangeListener
     * @see #addSoftPropertyChangeListener
     */
    public abstract void removePropertyChangeListener(
            PropertyChangeListener oldListener );

    /**
     * Determine whether there is at least one currently subscribed PropertyChangeListener.
     *
     * @return true if there is at least one currently subscribed PropertyChangeListener.
     */
    public abstract boolean hasPropertyChangeListener();

    /**
     * This method returns an Iterator over the currently subscribed PropertyChangeListeners.
     *
     * @return the Iterator over the currently subscribed PropertyChangeListeners
     */
    public abstract Iterator<PropertyChangeListener> propertyChangeListenersIterator();

    /**
     * Overridable method that enables an EntityType to define how its instances should be
     * rendered to the user. By default, this returns null.
     *
     * It is recommended to invoke MeshObject.getUserVisibleString() instead with a
     * suitable list of EntityTypes.
     *
     * @return the user-visible String representing this instance
     */
    public abstract String get_UserVisibleString();
}
