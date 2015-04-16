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
// on Thu, 2015-04-16 22:41:05 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.VCard;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Java interface for EntityType org.infogrid.model.VCard/Address.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>Address</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>true</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Address</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>An address on a VCard</td></tr></table></td></tr>
  * </table>
  */

public interface Address
        extends
            org.infogrid.mesh.TypedMeshObjectFacade
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.VCard" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.VCard/Address" );

    /**
      * <p>Set value for property IsPreferred.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsPreferred</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsPreferred</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is preferred</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setIsPreferred(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property IsPreferred.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsPreferred</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsPreferred</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is preferred</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsPreferred()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsPreferred property.
      */
    public static final String ISPREFERRED_name = "IsPreferred";

    /**
      * The IsPreferred PropertyType.
      */
    public static final PropertyType ISPREFERRED = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/Address_IsPreferred" );
    public static final org.infogrid.model.primitives.BooleanDataType ISPREFERRED_type = (org.infogrid.model.primitives.BooleanDataType) ISPREFERRED.getDataType();

    /**
      * <p>Set value for property IsHome.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsHome</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsHome</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a home address?</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setIsHome(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property IsHome.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsHome</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsHome</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a home address?</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsHome()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsHome property.
      */
    public static final String ISHOME_name = "IsHome";

    /**
      * The IsHome PropertyType.
      */
    public static final PropertyType ISHOME = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/Address_IsHome" );
    public static final org.infogrid.model.primitives.BooleanDataType ISHOME_type = (org.infogrid.model.primitives.BooleanDataType) ISHOME.getDataType();

    /**
      * <p>Set value for property IsWork.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsWork</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsWork</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a work address?</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setIsWork(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property IsWork.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/Address_IsWork</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsWork</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a work address?</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsWork()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsWork property.
      */
    public static final String ISWORK_name = "IsWork";

    /**
      * The IsWork PropertyType.
      */
    public static final PropertyType ISWORK = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/Address_IsWork" );
    public static final org.infogrid.model.primitives.BooleanDataType ISWORK_type = (org.infogrid.model.primitives.BooleanDataType) ISWORK.getDataType();

}
