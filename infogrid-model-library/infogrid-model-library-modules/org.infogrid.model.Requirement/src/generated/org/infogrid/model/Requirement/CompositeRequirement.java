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
// Copyright 1998-2013 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

//
// This file has been generated AUTOMATICALLY. DO NOT MODIFY.
// on Thu, 2015-04-16 22:41:02 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Requirement;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Java interface for EntityType org.infogrid.model.Requirement/CompositeRequirement.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Requirement/CompositeRequirement</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>CompositeRequirement</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Composite Requirement</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>A requirement that is composed of other requirements.</td></tr></table></td></tr>
  * </table>
  */

public interface CompositeRequirement
        extends
            org.infogrid.mesh.TypedMeshObjectFacade
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Requirement" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Requirement/CompositeRequirement" );

    /**
      * This MeshType.
      */
    public static final RoleType _CompositeRequirement_Evaluates_Requirement_SOURCE = ModelBaseSingleton.findRoleType( "org.infogrid.model.Requirement/CompositeRequirement_Evaluates_Requirement-S" );

    /**
      * <p>Set value for property Operator.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Requirement/CompositeRequirement_Operator</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Operator</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Operator</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Captures how the evaluated Requirements need to be evaluated\ndo determine whether the CompositeRequirement is met.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setOperator(
            org.infogrid.model.primitives.EnumeratedValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property Operator.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Requirement/CompositeRequirement_Operator</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Operator</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Operator</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Captures how the evaluated Requirements need to be evaluated\ndo determine whether the CompositeRequirement is met.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.EnumeratedValue getOperator()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Operator property.
      */
    public static final String OPERATOR_name = "Operator";

    /**
      * The Operator PropertyType.
      */
    public static final PropertyType OPERATOR = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Requirement/CompositeRequirement_Operator" );
    public static final org.infogrid.model.primitives.EnumeratedDataType OPERATOR_type = (org.infogrid.model.primitives.EnumeratedDataType) OPERATOR.getDataType();

    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue OPERATOR_type_AND = OPERATOR_type.select( "AND" );
    public static final EnumeratedValue OPERATOR_type_OR = OPERATOR_type.select( "OR" );
    public static final EnumeratedValue OPERATOR_type_XOR = OPERATOR_type.select( "XOR" );
}
