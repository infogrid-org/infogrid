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
// on Sat, 2015-04-18 05:30:58 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Probe;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Java interface for EntityType org.infogrid.model.Probe/PeriodicProbeUpdateSpecification.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>PeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Periodic Probe Update Specification</td></tr></table></td></tr>
  * </table>
  */

public interface PeriodicProbeUpdateSpecification
        extends
            org.infogrid.model.Probe.ProbeUpdateSpecification
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Probe" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification" );

    /**
      * <p>Set value for property Delay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_Delay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Delay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>86400000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Delay between the end of a Probe run and the start of the next Probe run.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property Delay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_Delay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Delay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>86400000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Delay between the end of a Probe run and the start of the next Probe run.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getDelay()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Delay property.
      */
    public static final String DELAY_name = "Delay";

    /**
      * The Delay PropertyType.
      */
    public static final PropertyType DELAY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_Delay" );
    public static final org.infogrid.model.primitives.IntegerDataType DELAY_type = (org.infogrid.model.primitives.IntegerDataType) DELAY.getDataType();

}
