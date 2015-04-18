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
  * <p>Java interface for EntityType org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>AdaptivePeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Adaptive Period Probe Update Specification</td></tr></table></td></tr>
  * </table>
  */

public interface AdaptivePeriodicProbeUpdateSpecification
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
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification" );

    /**
      * <p>Set value for property CurrentDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_CurrentDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>CurrentDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>3600000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Current delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The current delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setCurrentDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property CurrentDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_CurrentDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>CurrentDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>3600000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Current delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The current delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getCurrentDelay()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the CurrentDelay property.
      */
    public static final String CURRENTDELAY_name = "CurrentDelay";

    /**
      * The CurrentDelay PropertyType.
      */
    public static final PropertyType CURRENTDELAY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_CurrentDelay" );
    public static final org.infogrid.model.primitives.IntegerDataType CURRENTDELAY_type = (org.infogrid.model.primitives.IntegerDataType) CURRENTDELAY.getDataType();

    /**
      * <p>Set value for property MaxDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_MaxDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>MaxDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>604800000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Maximum delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The maximum delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setMaxDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property MaxDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_MaxDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>MaxDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>604800000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Maximum delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The maximum delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getMaxDelay()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the MaxDelay property.
      */
    public static final String MAXDELAY_name = "MaxDelay";

    /**
      * The MaxDelay PropertyType.
      */
    public static final PropertyType MAXDELAY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_MaxDelay" );
    public static final org.infogrid.model.primitives.IntegerDataType MAXDELAY_type = (org.infogrid.model.primitives.IntegerDataType) MAXDELAY.getDataType();

    /**
      * <p>Set value for property FallbackDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_FallbackDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>FallbackDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>600000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Fallback delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The fallback delay between the end of a Probe run and the start of the next Probe run.\nCurrentDelay is re-initialized to this value when a Probe run was unsuccessful, or data changed.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setFallbackDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property FallbackDelay.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_FallbackDelay</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>FallbackDelay</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>600000</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Fallback delay between Probe runs</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The fallback delay between the end of a Probe run and the start of the next Probe run.\nCurrentDelay is re-initialized to this value when a Probe run was unsuccessful, or data changed.\nFIXME: this should use TimePeriodValue not IntegerValue.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getFallbackDelay()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the FallbackDelay property.
      */
    public static final String FALLBACKDELAY_name = "FallbackDelay";

    /**
      * The FallbackDelay PropertyType.
      */
    public static final PropertyType FALLBACKDELAY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_FallbackDelay" );
    public static final org.infogrid.model.primitives.IntegerDataType FALLBACKDELAY_type = (org.infogrid.model.primitives.IntegerDataType) FALLBACKDELAY.getDataType();

    /**
      * <p>Set value for property AdaptiveFactor.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_AdaptiveFactor</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>AdaptiveFactor</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Float (default: 0.0, min: -Infinity, max: Infinity)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>1.1</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Adaptive Factor</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The factor by which the delay increases for each Probe run that was successful and did not produce changed data.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setAdaptiveFactor(
            org.infogrid.model.primitives.FloatValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property AdaptiveFactor.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_AdaptiveFactor</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>AdaptiveFactor</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Float (default: 0.0, min: -Infinity, max: Infinity)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>1.1</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Adaptive Factor</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The factor by which the delay increases for each Probe run that was successful and did not produce changed data.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.FloatValue getAdaptiveFactor()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the AdaptiveFactor property.
      */
    public static final String ADAPTIVEFACTOR_name = "AdaptiveFactor";

    /**
      * The AdaptiveFactor PropertyType.
      */
    public static final PropertyType ADAPTIVEFACTOR = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_AdaptiveFactor" );
    public static final org.infogrid.model.primitives.FloatDataType ADAPTIVEFACTOR_type = (org.infogrid.model.primitives.FloatDataType) ADAPTIVEFACTOR.getDataType();

}
