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

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.Role;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.model.traversal.TraversalSpecification;

import org.infogrid.util.IsDeadException;

import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * This is the abstract superclass of all type-safe facades for MeshObjects.
 * This replicates man of the methods on MeshObject, and implements by delegating there.
 */
public abstract class TypedMeshObjectFacade
{
    /**
     * Constructor, for subclasses only. This is invoked by InfoGrid as a result
     * of MeshObject.getTypedFacadeFor invocations.
     *
     * @param delegate the MeshObject that this is a facade for
     */
    protected TypedMeshObjectFacade(
            MeshObject delegate )
    {
        the_Delegate = delegate;
    }
    
    /**
     * Obtain the underlying MeshObject. It is named with an underscore, so code-generated
     * code is less likely to interfere with it.
     *
     * @return the underlying MeshObject
     */
    public MeshObject get_Delegate()
    {
        return the_Delegate;
    }

    /**
     * Obtain the MeshObjectType for which this is the facade. It is named with an underscore, so code-generated
     * code is less likely to interfere with it.
     *
     * @return the MeshObjectType
     */
    public EntityType get_Type()
    {
        return the_Delegate.getTypeFor( this );
    }

    /**
     * Obtain the globally unique identifier of this MeshObject.
     *
     * @return the globally unique identifier of this MeshObject
     */
    public final MeshObjectIdentifier getIdentifier()
    {
        return the_Delegate.getIdentifier();
    }

    /**
     * Obtain the MeshBase that contains this MeshObject. This is immutable for the
     * lifetime of this instance.
     *
     * @return the MeshBase that contains this MeshObject.
     */
    public final MeshBase getMeshBase()
    {
        return the_Delegate.getMeshBase();
    }
 
    /**
     * Obtain the time of creation of this MeshObject. This is immutable for the
     * lifetime of the MeshObject.
     *
     * @return the time this MeshObject was created in milliseconds
     */
    public final long getTimeCreated()
    {
        return the_Delegate.getTimeCreated();
    }

    /**
     * Obtain the time of last update of this MeshObject. This changes automatically
     * every time the MeshObject is changed.
     *
     * @return the time this MeshObject was last updated in milliseconds
     */
    public final long getTimeUpdated()
    {
        return the_Delegate.getTimeUpdated();
    }

    /**
     * Obtain the time of the last reading operation of this MeshObject. This changes automatically
     * every time the MeshObject is read.
     *
     * @return the time this MeshObject was last read in milliseconds
     */
    public final long getTimeRead()
    {
        return the_Delegate.getTimeRead();
    }

    /**
     * Set the time when this MeshObject expires. If -1, it never does.
     *
     * @param newValue the new value, in milliseconds
     */
    public final void setTimeExpires(
            long newValue )
    {
        the_Delegate.setTimeExpires( newValue );
    }

    /**
     * Obtain the time when this MeshObject expires. If this returns -1, it never does.
     *
     * @return the time at which this MeshObject expires
     */
    public final long getTimeExpires()
    {
        return the_Delegate.getTimeExpires();
    }

    /**
     * Determine whether this MeshObject is dead and should not be used any further.
     *
     * @return true if the MeshObject is dead
     */
    public final boolean getIsDead()
    {
        return the_Delegate.getIsDead();
    }

    /**
     * Throw an IsDeadException if this MeshObject is dead and should not be used any further.
     * Do nothing if this MeshObject is alive.
     *
     * @throws IsDeadException thrown if this MeshObject is dead already
     */
    public void checkAlive()
        throws
            IsDeadException
    {
        the_Delegate.checkAlive();
    }

// --

    /**
     * Traverse from this MeshObject to all directly related MeshObjects. Directly
     * related MeshObjects are those MeshObjects that are participating in a
     * relationship with this MeshObject.
     *
     * @return the set of MeshObjects that are directly related to this MeshObject
     */
    public final MeshObjectSet traverseToNeighborMeshObjects()
    {
        return the_Delegate.traverseToNeighborMeshObjects();
    }

    /**
     * Traverse from this MeshObject to all directly related MeshObjects. Directly
     * related MeshObjects are those MeshObjects that are participating in a
     * relationship with this MeshObject. Specify whether to consider equivalents
     * as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well
     * @return the set of MeshObjects that are directly related to this MeshObject
     */
    public final MeshObjectSet traverseToNeighborMeshObjects(
            boolean considerEquivalents )
    {
        return the_Delegate.traverseToNeighborMeshObjects( considerEquivalents );
    }

    /**
     * Relate this MeshObject to another MeshObject.
     *
     * @param otherObject the MeshObject to relate to
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #unrelate
     */
    public final void relate(
            TypedMeshObjectFacade otherObject )
        throws
            RelatedAlreadyException,
            TransactionException
    {
        the_Delegate.relate( otherObject.get_Delegate() );
    }

    /**
     * Unrelate this MeshObject from another MeshObject. This will also remove all blessings from the relationship.
     *
     * @param otherObject the MeshObject to unrelate from
     * @throws NotRelatedException thrown if this MeshObject is not already related to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     * @see #relate
     */
    public final void unrelate(
            TypedMeshObjectFacade otherObject )
        throws
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.unrelate( otherObject.get_Delegate() );
    }

    /**
     * Determine whether this MeshObject is related to another MeshObject.
     *
     * @param otherObject the MeshObject to which this MeshObject may be related
     * @return true if this MeshObject is currently related to otherObject
     */
    public final boolean isRelated(
            TypedMeshObjectFacade otherObject )
    {
        return the_Delegate.isRelated( otherObject.get_Delegate() );
    }

    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided ByRoleType.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is instantiated at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be blessed
     * @throws BlessedAlreadyException.ByRoleType thrown if the relationship to the other MeshObject is blessed
     *         already with this ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void blessRelationship(
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeBlessedAlreadyException,
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.blessRelationship( thisEnd, otherObject.get_Delegate() );
    }
    
    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided RoleTypes.
     * This is either successful as a whole or not at all.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is instantiated at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be blessed
     * @throws BlessedAlreadyException.ByRoleType thrown if the relationship to the other MeshObject is blessed
     *         already with this ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void blessRelationship(
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
RoleTypeBlessedAlreadyException,             NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.blessRelationship( thisEnd, otherObject.get_Delegate() );
    }
    
    /**
     * Relate this MeshObject to another MeshObject, and bless the new relationship with the provided ByRoleType.
     * This is either successful as a whole or not at all. This is a convenience method.
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
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.relateAndBless( thisEnd, otherObject.get_Delegate() );
    }

    /**
     * Relate this MeshObject to another MeshObject, and bless the new relationship with the provided RoleTypes.
     * This is either successful as a whole or not at all. This is a convenience method.
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
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.relateAndBless( thisEnd, otherObject.get_Delegate() );
    }

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided ByRoleType.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is removed at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws NotBlessedException.ByRoleType thrown if the relationship to the other MeshObject does not support the ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unblessRelationship(
            RoleType              thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.unblessRelationship( thisEnd, otherObject.get_Delegate() );
    }

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided RoleTypes.
     * This is either successful as a whole or not at all.
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is removed at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws NotBlessedException.ByRoleType thrown if the relationship to the other MeshObject does not support the ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unblessRelationship(
            RoleType []           thisEnd,
            TypedMeshObjectFacade otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.unblessRelationship( thisEnd, otherObject.get_Delegate() );
    }

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
        return the_Delegate.traverse( theTraverseSpec );
    }

    /**
      * Traverse a TraversalSpecification from this MeshObject to obtain a set of MeshObjects.
      * Specify whether relationships of equivalent MeshObjects should be considered as well.
      *
      * @param theTraverseSpec the TraversalSpecification to traverse
      * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
      *        if false, only this MeshObject will be used as the start
      * @return the set of MeshObjects found as a result of the traversal
      */
    public final MeshObjectSet traverse(
            TraversalSpecification theTraverseSpec,
            boolean                considerEquivalents )
    {
        return the_Delegate.traverse( theTraverseSpec, considerEquivalents );
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
        return the_Delegate.getRoleTypes();
    }
    
    /**
     * Obtain the RoleTypes that this MeshObject currently participates in. This will return only one
     * instance of the same ByRoleType object, even if the MeshObject participates in this ByRoleType
     * multiple times with different other sides. This may only return the subset of RoleTypes
     * that the caller is allowed to see. Specify whether relationships of equivalent MeshObjects
     * should be considered as well.
     * 
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
     *        if false, only this MeshObject will be used as the start
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public final RoleType [] getRoleTypes(
            boolean considerEquivalents )
    {
        return the_Delegate.getRoleTypes( considerEquivalents );
    }
    
    /**
     * Obtain the Roles that this MeshObject currently participates in. This may only return the subset
     * of Roles that the caller is allowed to see.
     *
     * @return the Roles that this MeshObject currently participates in.
     */
    public final Role [] getRoles()
    {
        return the_Delegate.getRoles();
    }
    
    /**
     * Obtain the Roles that this MeshObject currently participates in. This may only return the subset
     * of Roles that the caller is allowed to see. Specify whether relationships of equivalent MeshObjects
     * should be considered as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well
     *        if false, only this MeshObject will be used as the start
     * @return the Roles that this MeshObject currently participates in.
     */
    public final Role [] getRoles(
            boolean considerEquivalents )
    {
        return the_Delegate.getRoles( considerEquivalents );
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
            TypedMeshObjectFacade otherObject )
    {
        return the_Delegate.getRoleTypes( otherObject.get_Delegate() );
    }

    /**
     * Obtain the RoleTypes that this MeshObject currently participates in with the
     * specified other MeshObject. This may only return the subset of RoleTypes that the caller
     * is allowed to see. Specify whether relationships of equivalent MeshObjects should be considered
     * as well.
     *
     * @param otherObject the other MeshObject
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well;
     *        if false, only this MeshObject will be used as the start
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public final RoleType [] getRoleTypes(
            TypedMeshObjectFacade otherObject,
            boolean               considerEquivalents )
    {
        return the_Delegate.getRoleTypes( otherObject.get_Delegate(), considerEquivalents );
    }

    /**
     * Add another MeshObject as an equivalent. All MeshObjects that are already equivalent
     * to this MeshObject, and all MeshObjects that are already equivalent to the newly
     * added MeshObject, are now equivalent.
     *
     * @param equiv the new equivalent
     * @throws EquivalentAlreadyException thrown if the provided MeshObject is already an equivalent
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void addAsEquivalent(
            TypedMeshObjectFacade equiv )
        throws
            EquivalentAlreadyException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.addAsEquivalent( equiv.get_Delegate() );
    }
    
    /**
     * Obtain the set of MeshObjects, including this one, that are equivalent.
     * This always returns at least this MeshObject.
     *
     * @return the set of MeshObjects that are equivalent
     */
    public MeshObjectSet getEquivalents()
    {
        return the_Delegate.getEquivalents();
    }
    
    /**
     * Remove this MeshObject as an equivalent from the set of equivalents.
     *
     * @param remainingEquivalentSetRepresentative one of the MeshObjects that remain in the set of equivalents
     * @throws NotEquivalentException thrown if the provided MeshBase in not equivalent
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void removeAsEquivalent(
            TypedMeshObjectFacade remainingEquivalentSetRepresentative )
        throws
            NotEquivalentException,
            TransactionException,
            NotPermittedException
    {
        the_Delegate.removeAsEquivalent( remainingEquivalentSetRepresentative.get_Delegate() );
    }

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public final void addDirectPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        the_Delegate.addDirectPropertyChangeListener( newListener );
    }

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public final void addWeakPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        the_Delegate.addWeakPropertyChangeListener( newListener );
    }

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public final void addSoftPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        the_Delegate.addSoftPropertyChangeListener( newListener );
    }

    /**
      * Remove a PropertyChangeListener.
      *
      * @param oldListener the to-be-removed PropertyChangeListener
      * @see #addPropertyChangeListener
      */
    public final void removePropertyChangeListener(
            PropertyChangeListener oldListener )
    {
        the_Delegate.removePropertyChangeListener( oldListener );
    }

    /**
     * Determine whether there is at least one currently subscribed PropertyChangeListener.
     *
     * @return true if there is at least one currently subscribed PropertyChangeListener.
     */
    public final boolean hasPropertyChangeListener()
    {
        return the_Delegate.hasPropertyChangeListener();
    }

    /**
     * This method returns an iterator over the currently subscribed PropertyChangeListeners.
     *
     * @return the Iterator over the currently subscribed PropertyChangeListeners
     */
    public final Iterator<PropertyChangeListener> propertyChangeListenersIterator()
    {
        return the_Delegate.propertyChangeListenersIterator();
    }

    /**
     * The underlying MeshObject. It is named with an underscore, so code-generated
     * code is less likely to interfere with it.
     */
    protected final MeshObject the_Delegate;
}
