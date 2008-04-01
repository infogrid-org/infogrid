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

import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.Role;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.modelbase.MeshTypeNotFoundException;

import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.util.IsDeadException;
import org.infogrid.util.text.StringRepresentation;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
  * This interface is implemented by all MeshObjects. MeshObjects are all
  * independent objects that are managed by InfoGrid. Pairs of MeshObjects
  * may define Relationships, which are dependent objects as each relationship
  * depends on the MeshObjects that it connects.
  */
public interface MeshObject
{
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
    public abstract MeshBase getMeshBase();
 
    /**
     * Obtain the time of creation of this MeshObject. This is immutable for the
     * lifetime of the MeshObject.
     *
     * @return the time this MeshObject was created in milliseconds
     */
    public abstract long getTimeCreated();

    /**
     * Obtain the time of last update of this MeshObject. This changes automatically
     * every time the MeshObject is changed.
     *
     * @return the time this MeshObject was last updated in milliseconds
     */
    public abstract long getTimeUpdated();

    /**
     * Obtain the time of the last reading operation of this MeshObject. This changes automatically
     * every time the MeshObject is read.
     *
     * @return the time this MeshObject was last read in milliseconds
     */
    public abstract long getTimeRead();

    /**
     * Set the time when this MeshObject expires. If -1, it never does.
     *
     * @param newValue the new value, in milliseconds
     */
    public abstract void setTimeExpires(
            long newValue );

    /**
     * Obtain the time when this MeshObject expires. If this returns -1, it never does.
     *
     * @return the time at which this MeshObject expires
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

    /**
     * Determine whether this MeshObject is the home object of its MeshBase.
     * 
     * @return true if it is the home object
     */
    public abstract boolean isHomeObject();

// --

    /**
     * Obtain the value of a Property, given its PropertyType.
     *
     * @param thePropertyType the PropertyType whose value we want to determine for this MeshObject
     * @return the current value of the PropertyValue
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @see #setPropertyValue
     */
    public abstract PropertyValue getPropertyValue(
            PropertyType thePropertyType )
        throws
            NotPermittedException,
            EntityNotBlessedException;

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
    public abstract PropertyValue setPropertyValue(
            PropertyType  thePropertyType,
            PropertyValue newValue )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

    /**
     * Set the value of a Property, given its PropertyType and a PropertyValue, and specify a time when
     * that change happened. The caller must have the appropriate rights to invoke this; typical callers
     * do not have the rights because this call is mostly intended for system-internal reasons.
     * 
     * @param thePropertyType the PropertyType whose value we want to set
     * @param newValue the new value for the PropertyType for this MeshObject
     * @param timeUpdated the time at which this change occurred
     * @return old value of the Property
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #getPropertyValue
     */
    public abstract PropertyValue setPropertyValue(
            PropertyType  thePropertyType,
            PropertyValue newValue,
            long          timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues, in the same sequence.
     * This method sets either all values, or none.
     *
     * @param thePropertyTypes the PropertyTypes whose values we want to set
     * @param thePropertyValues the new values for the PropertyTypes for this MeshObject
     * @return old value of the Properties
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public abstract PropertyValue [] setPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] thePropertyValues )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

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
    public abstract void setPropertyValues(
            Map<PropertyType,PropertyValue> newValues )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

    /**
     * Set the value of several Properties, given their PropertyTypes and PropertyValues, in the same sequence,
     * and specify a time when that change happened. This method sets either all values, or none.
     * The caller must have the appropriate rights to invoke this.
     *
     * @param thePropertyTypes the PropertyTypes whose values we want to set
     * @param thePropertyValues the new values for the PropertyTypes for this MeshObject
     * @param timeUpdated the time at which this change occurred
     * @return old value of the Properties
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalPropertyValueException thrown if the new value is an illegal value for this PropertyType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     */
    public abstract PropertyValue [] setPropertyValues(
            PropertyType []  thePropertyTypes,
            PropertyValue [] thePropertyValues,
            long             timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

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
    public abstract void setPropertyValues(
            Map<PropertyType,PropertyValue> newValues,
            long                            timeUpdated )
        throws
            NotPermittedException,
            IllegalPropertyValueException,
            EntityNotBlessedException,
            TransactionException;

    /**
     * Obtain the set of all PropertyTypes currently used with this MeshObject. This may return only the
     * subset of PropertyTypes that the caller is allowed to see.
     *
     * @return the set of all PropertyTypes
     */
    public abstract PropertyType [] getAllPropertyTypes();

    /**
     * Obtain all PropertyValues for the PropertyTypes provided, in the same sequence as the provided
     * PropertyTypes. If a PropertyType does not exist on this MeshObject, or if access to one of the
     * PropertyTypes is not permitted, this will throw an exception. This is a convenience method.
     *
     * @param thePropertyTypes the PropertyTypes
     * @return the PropertyValues, in the same sequence as PropertyTypes
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     */
    public abstract PropertyValue [] getPropertyValues(
            PropertyType [] thePropertyTypes )
        throws
            NotPermittedException,
            EntityNotBlessedException;

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
    public abstract PropertyValue getPropertyValueByName(
            String propertyName )
        throws
            MeshTypeNotFoundException,
            NotPermittedException,
            EntityNotBlessedException;

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
     * Obtain the Identifiers of the neighbors of this MeshObject. This is sometimes a
     * more efficient operation than to traverse to the neighbors and determine the Identifiers
     * from there.
     *
     * @return the Identifiers of the neighbors
     */
    public abstract MeshObjectIdentifier [] getNeighborMeshObjectIdentifiers();
    
    /**
     * Obtain the RoleTypes that this MeshObject plays with a given neighbor MeshObject identified
     * by its Identifier.
     * 
     * @param neighborIdentifier the Identifier of the neighbor MeshObject
     * @return the RoleTypes
     */
    public abstract MeshTypeIdentifier [] getRoleTypeIdentifiers(
            MeshObjectIdentifier neighborIdentifier );
    
    /**
     * Relate this MeshObject to another MeshObject.
     *
     * @param otherObject the MeshObject to relate to
     * @throws RelatedAlreadyException thrown to indicate that this MeshObject is already related
     *         to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @see #unrelate
     */
    public abstract void relate(
            MeshObject otherObject )
        throws
            RelatedAlreadyException,
            TransactionException;

    /**
     * Unrelate this MeshObject from another MeshObject. This will also remove all blessings from the relationship.
     *
     * @param otherObject the MeshObject to unrelate from
     * @throws NotRelatedException thrown if this MeshObject is not already related to the otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     * @see #relate
     */
    public abstract void unrelate(
            MeshObject otherObject )
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
            MeshObject otherObject );

// --

    /**
     * Make this MeshObject support the provided EntityType.
     * 
     * @param type the new ByEntityType to be supported by this MeshObject
     * @throws BlessedAlreadyException.ByEntityType thrown if this MeshObject is blessed already with this ByEntityType
     * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract void bless(
            EntityType type )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Make this MeshObject support one more more provided EntityTypes. The MeshObject will either be
     * blessed with all of the EntityTypes, or none.
     * 
     * @param types the new EntityTypes to be supported by this MeshObject
     * @throws BlessedAlreadyException.ByEntityType thrown if this MeshObject is blessed already with at least one of these EntityTypes
     * @throws IsAbstractException thrown if one of the EntityTypes is abstract and cannot be instantiated
     * @throws TransactionException thrown if invoked outside of proper transaction boundaries
     * @throws NotPermittedException thrown if the blessing operation is not permitted
     */
    public abstract void bless(
            EntityType [] types )
        throws
            EntityBlessedAlreadyException,
            IsAbstractException,
            TransactionException,
            NotPermittedException;

    /**
     * Makes this MeshObject stop supporting the provided ByEntityType. This may fail with an
     * RoleTypeRequiresEntityTypeException because the RoleType of a relationship in which this MeshObject participates
     * requires this MeshObject to have the EntityType that is supposed to be unblessed. To avoid this,
     * unbless relationships first.
     * 
     * @param type the ByEntityType that the MeshObject will stop supporting
     * @throws RoleTypeRequiresEntityTypeException if this MeshObject plays a role that requires the MeshObject to remain being blessed with this type
     * @throws EntityNotBlessedException thrown if this MeshObject does not currently support this ByEntityType
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unbless(
            EntityType type )
        throws
            RoleTypeRequiresEntityTypeException,
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException;

    /**
     * Makes this MeshObject stop supporting the provided EntityTypes. The MeshObject will either be
     * unblessed from all of the EntityTypes, or none.  This may fail with an
     * RoleTypeRequiresEntityTypeException because the RoleType of a relationship in which this MeshObject participates
     * requires this MeshObject to have the EntityType that is supposed to be unblessed. To avoid this,
     * unbless relationships first.
     * 
     * @param types the EntityTypes that the MeshObject will stop supporting
     * @throws RoleTypeRequiresEntityTypeException if this MeshObject plays a role that requires the MeshObject to remain being blessed with this type
     * @throws NotBlessedException.ByEntityType thrown if this MeshObject does not support at least one of the given EntityTypes
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unbless(
            EntityType [] types )
        throws
            RoleTypeRequiresEntityTypeException,
            EntityNotBlessedException,
            TransactionException,
            NotPermittedException;

    /**
     * Obtain the EntityTypes that this MeshObject is currently blessed with. This may only return
     * the subset of EntityTypes that the caller is allowed to see.
     *
     * @return the types of this MeshObject
     */
    public abstract EntityType [] getTypes();

    /**
     * Determine whether this MeshObject currently supports this MeshType. This may return false, even if
     * the MeshObject is blessed by this ByEntityType, if the caller is not allowed to see this ByEntityType.
     * By default, this returns true even if the MeshObject is blessed by a subtype of the provided type.
     * 
     * @param type the ByEntityType to look for
     * @return true if this MeshObject supports this MeshType
     */
    public abstract boolean isBlessedBy(
            EntityType type );

    /**
     * Determine whether this MeshObject currently supports this MeshType. This may return false, even if
     * the MeshObject is blessed by this ByEntityType, if the caller is not allowed to see this ByEntityType.
     * Specify whether or not subtypes of the provided type should be considered.
     * 
     * @param type the ByEntityType to look for
     * @param considerSubtypes if true, return true even if only a subtype matches
     * @return true if this MeshObject supports this MeshType
     */
    public abstract boolean isBlessedBy(
            EntityType type,
            boolean    considerSubtypes );

    /**
     * Determine the specific subtype of the provided EntityType with which this MeshObject has been blessed.
     * If this MeshObject has not been blessed with a subtype of the provided EntityType, return <code>null</code>.
     * If blessed with more than one subtype, throw an Exception.
     *
     * @param type the EntityType
     * @return the sub-type, if any
     * @throws IllegalStateException thrown if the MeshObject is blessed by more than one subtype
     */
    public abstract EntityType determineBlessedSubtype(
            EntityType type )
        throws
            IllegalStateException;

    /**
     * If the provided TypedMeshObjectFacade is a facade of this instance, get the ByEntityType
     * that corresponds to this TypedMeshObjectFacade.
     * 
     * 
     * @param obj the TypedMeshObjectFacade
     * @return the ByEntityType that corresponds to this TypedMeshObjectFacade
     * @throws IllegalArgumentException thrown if the TypedMeshObjectFacade is not a facade of this MeshObject
     */
    public abstract EntityType getTypeFor(
            TypedMeshObjectFacade obj )
        throws
            IllegalArgumentException;

    /**
     * Obtain an instance of (a subclass of) TypedMeshObjectFacade that provides the type-safe interface
     * to this MeshObject for a particular ByEntityType. Throw NotBlessedException
     * if this MeshObject does not current support this ByEntityType, or if the caller is not allowed to
     * see this ByEntityType.
     * 
     * 
     * @param type the ByEntityType
     * @return the TypedMeshObjectFacade for this MeshObject
     * @throws NotBlessedException thrown if this MeshObject does not currently support this ByEntityType
     */
    public abstract TypedMeshObjectFacade getTypedFacadeFor(
            EntityType type )
        throws
            NotBlessedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided ByRoleType.
     * 
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
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            RoleTypeBlessedAlreadyException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;
    
    /**
     * Make a relationship of this MeshObject to another MeshObject support the provided RoleTypes.
     * This is either successful as a whole or not at all.
     * 
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
            RoleType [] thisEnd,
            MeshObject  otherObject )
        throws
            RoleTypeBlessedAlreadyException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;
    
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
    public abstract void relateAndBless(
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException;

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
    public abstract void relateAndBless(
            RoleType [] thisEnd,
            MeshObject  otherObject )
        throws
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided ByRoleType.
     * 
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is removed at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws NotBlessedException.ByRoleType thrown if the relationship to the other MeshObject does not support the ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unblessRelationship(
            RoleType   thisEnd,
            MeshObject otherObject )
        throws
            RoleTypeNotBlessedException,
            NotRelatedException,
            TransactionException,
            NotPermittedException;

    /**
     * Make a relationship of this MeshObject to another MeshObject stop supporting the provided RoleTypes.
     * This is either successful as a whole or not at all.
     * 
     * 
     * @param thisEnd the ByRoleType of the RelationshipType that is removed at this object
     * @param otherObject the other MeshObject whose relationship to this MeshObject shall be unblessed
     * @throws NotBlessedException.ByRoleType thrown if the relationship to the other MeshObject does not support the ByRoleType
     * @throws NotRelatedException thrown if this MeshObject is not currently related to otherObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void unblessRelationship(
            RoleType [] thisEnd,
            MeshObject  otherObject )
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
     * instance of the same ByRoleType object, even if the MeshObject participates in this ByRoleType
     * multiple times with different other sides. This may only return the subset of RoleTypes
     * that the caller is allowed to see.
     * 
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public abstract RoleType [] getRoleTypes();
    
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
    public abstract RoleType [] getRoleTypes(
            boolean considerEquivalents );
    
    /**
     * Obtain the Roles that this MeshObject currently participates in. This may only return the subset
     * of Roles that the caller is allowed to see.
     *
     * @return the Roles that this MeshObject currently participates in.
     */
    public abstract Role [] getRoles();
    
    /**
     * Obtain the Roles that this MeshObject currently participates in. This may only return the subset
     * of Roles that the caller is allowed to see. Specify whether relationships of equivalent MeshObjects
     * should be considered as well.
     *
     * @param considerEquivalents if true, all equivalent MeshObjects are considered as well
     *        if false, only this MeshObject will be used as the start
     * @return the Roles that this MeshObject currently participates in.
     */
    public abstract Role [] getRoles(
            boolean considerEquivalents );
    
    /**
     * Obtain the RoleTypes that this MeshObject currently participates in with the
     * specified other MeshObject. This may only return the subset of RoleTypes that the caller
     * is allowed to see.
     *
     * @param otherObject the other MeshObject
     * @return the RoleTypes that this MeshObject currently participates in.
     */
    public abstract RoleType [] getRoleTypes(
            MeshObject otherObject );

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
    public abstract RoleType [] getRoleTypes(
            MeshObject otherObject,
            boolean    considerEquivalents );

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
            MeshObject equiv )
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
    public MeshObjectSet getEquivalents();
    
    /**
     * Obtain the Identifiers of the equivalent MeshObjects. This is sometimes more efficient than
     * traversing to the equivalents, and determining the IdentifierValues.
     *
     * @return the Identifiers of the equivalents
     */
    public MeshObjectIdentifier [] getEquivalentMeshObjectIdentifiers();
    
    /**
     * Remove this MeshObject as an equivalent from the set of equivalents.
     *
     * @param remainingEquivalentSetRepresentative one of the MeshObjects that remain in the set of equivalents
     * @throws NotEquivalentException thrown if the provided MeshBase in not equivalent
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws NotPermittedException thrown if the operation is not permitted
     */
    public void removeAsEquivalent(
            MeshObject remainingEquivalentSetRepresentative )
        throws
            NotEquivalentException,
            TransactionException,
            NotPermittedException;

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addDirectPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addWeakPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addSoftPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Remove a PropertyChangeListener.
      *
      * @param oldListener the to-be-removed PropertyChangeListener
      * @see #addPropertyChangeListener
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
     * This method returns an iterator over the currently subscribed PropertyChangeListeners.
     *
     * @return the Iterator over the currently subscribed PropertyChangeListeners
     */
    public abstract Iterator<PropertyChangeListener> propertyChangeListenersIterator();

    /**
     * Obtain the same MeshObject as ExternalizedMeshObject so it can be easily serialized.
     * 
     * @return this MeshObject as ExternalizedMeshObject
     */
    public abstract ExternalizedMeshObject asExternalized();

    /**
     * Obtain a String representation of this MeshObject that can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentation(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the start part of a String representation of this MeshObject that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkStart(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the end part of a String representation of this MeshObject that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the enclosing MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkEnd(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );

    /**
     * The name of a pseudo-property that indicates the state of the object, such as
     * "alive" or "dead". This pseudo-property changes when the MeshObject dies.
     */
    public static final String _MESH_OBJECT_STATE_PROPERTY = "_MeshObjectState";

    /**
     * The name of a pseudo-property that indicates the MeshTypes of this MeshObject.
     * This pseudo-property changes when the MeshObject is blessed or unblessed.
     */
    public static final String _MESH_OBJECT_TYPES_PROPERTY = "_MeshObjectTypes";
    
    /**
     * The name of a pseudo-property that indicates the RoleTypes in which this MeshObject
     * participates. This pseudo-property changes when the MeshObject's participation in
     * a relationship changes.
     */
    public static final String _MESH_OBJECT_ROLES_PROPERTY = "_MeshObjectRoles";
    
    /**
     * The name of a pseudo-property that indicates that current set of neighbor MeshTypes.
     */
    public static final String _MESH_OBJECT_NEIGHBOR_PROPERTY = "_MeshObjectNeighbors";
    
    /**
     * The name of a pseudo-property that indicates the current set of equivalents.
     */
    public static final String _MESH_OBJECT_EQUIVALENTS_PROPERTY = "_MeshObjectEquivalents";
}
