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
  * <p>Java interface for EntityType org.infogrid.model.Probe/ProbeUpdateSpecification.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>ProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>true</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Probe Update Specification</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>A Home Object of a Probe is blessed with a subtype of this ProbeUpdateSpecification.\nIt expresses meta-data such as when the Probe was last run, when it should be run next etc.\nSubtypes may implement different algorithms to determine the respective data.</td></tr></table></td></tr>
  * </table>
  */

public interface ProbeUpdateSpecification
        extends
            org.infogrid.mesh.TypedMeshObjectFacade
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Probe" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Probe/ProbeUpdateSpecification" );

    /**
      * <p>Set value for property NextProbeRun.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_NextProbeRun</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>NextProbeRun</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Time Stamp</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Time of next Probe run</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The time at which to run this Probe next. If this is not given, this indicates\n\"never\". If this is in the past, it means \"as soon as possible\". This value\nis given in Java\'s System.currentTimeMillis() format.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setNextProbeRun(
            org.infogrid.model.primitives.TimeStampValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property NextProbeRun.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_NextProbeRun</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>NextProbeRun</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Time Stamp</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Time of next Probe run</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The time at which to run this Probe next. If this is not given, this indicates\n\"never\". If this is in the past, it means \"as soon as possible\". This value\nis given in Java\'s System.currentTimeMillis() format.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.TimeStampValue getNextProbeRun()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the NextProbeRun property.
      */
    public static final String NEXTPROBERUN_name = "NextProbeRun";

    /**
      * The NextProbeRun PropertyType.
      */
    public static final PropertyType NEXTPROBERUN = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_NextProbeRun" );
    public static final org.infogrid.model.primitives.TimeStampDataType NEXTPROBERUN_type = (org.infogrid.model.primitives.TimeStampDataType) NEXTPROBERUN.getDataType();

    /**
      * <p>Set value for property LastProbeRun.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastProbeRun</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastProbeRun</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Time Stamp</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Time of most recent Probe run</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The time that the last Probe run was attempted (successful or unsuccessful).\n                  If this is not given, it means that the Probe was never run. This value\n                  is given in Java\'s System.currentTimeMillis() format.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setLastProbeRun(
            org.infogrid.model.primitives.TimeStampValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property LastProbeRun.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastProbeRun</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastProbeRun</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Time Stamp</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Time of most recent Probe run</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The time that the last Probe run was attempted (successful or unsuccessful).\n                  If this is not given, it means that the Probe was never run. This value\n                  is given in Java\'s System.currentTimeMillis() format.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.TimeStampValue getLastProbeRun()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the LastProbeRun property.
      */
    public static final String LASTPROBERUN_name = "LastProbeRun";

    /**
      * The LastProbeRun PropertyType.
      */
    public static final PropertyType LASTPROBERUN = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastProbeRun" );
    public static final org.infogrid.model.primitives.TimeStampDataType LASTPROBERUN_type = (org.infogrid.model.primitives.TimeStampDataType) LASTPROBERUN.getDataType();

    /**
      * <p>Set value for property ProbeRunCounter.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_ProbeRunCounter</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>ProbeRunCounter</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>0</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Probe-run counter</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>This value is incremented every time the Probe is run.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setProbeRunCounter(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property ProbeRunCounter.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_ProbeRunCounter</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>ProbeRunCounter</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>0</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Probe-run counter</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>This value is incremented every time the Probe is run.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getProbeRunCounter()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the ProbeRunCounter property.
      */
    public static final String PROBERUNCOUNTER_name = "ProbeRunCounter";

    /**
      * The ProbeRunCounter PropertyType.
      */
    public static final PropertyType PROBERUNCOUNTER = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_ProbeRunCounter" );
    public static final org.infogrid.model.primitives.IntegerDataType PROBERUNCOUNTER_type = (org.infogrid.model.primitives.IntegerDataType) PROBERUNCOUNTER.getDataType();

    /**
      * <p>Set value for property LastRunUsedWritableProbe.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedWritableProbe</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastRunUsedWritableProbe</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Runs as Writable Probe</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>If this is true, the last Probe run used a WritableProbe.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setLastRunUsedWritableProbe(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property LastRunUsedWritableProbe.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedWritableProbe</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastRunUsedWritableProbe</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Runs as Writable Probe</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>If this is true, the last Probe run used a WritableProbe.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getLastRunUsedWritableProbe()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the LastRunUsedWritableProbe property.
      */
    public static final String LASTRUNUSEDWRITABLEPROBE_name = "LastRunUsedWritableProbe";

    /**
      * The LastRunUsedWritableProbe PropertyType.
      */
    public static final PropertyType LASTRUNUSEDWRITABLEPROBE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedWritableProbe" );
    public static final org.infogrid.model.primitives.BooleanDataType LASTRUNUSEDWRITABLEPROBE_type = (org.infogrid.model.primitives.BooleanDataType) LASTRUNUSEDWRITABLEPROBE.getDataType();

    /**
      * <p>Set value for property LastRunUsedProbeClass.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedProbeClass</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastRunUsedProbeClass</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Runs using Probe Class</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Name of the Probe Class that was used at the last run.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setLastRunUsedProbeClass(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property LastRunUsedProbeClass.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedProbeClass</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>LastRunUsedProbeClass</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Runs using Probe Class</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Name of the Probe Class that was used at the last run.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getLastRunUsedProbeClass()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the LastRunUsedProbeClass property.
      */
    public static final String LASTRUNUSEDPROBECLASS_name = "LastRunUsedProbeClass";

    /**
      * The LastRunUsedProbeClass PropertyType.
      */
    public static final PropertyType LASTRUNUSEDPROBECLASS = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedProbeClass" );
    public static final org.infogrid.model.primitives.StringDataType LASTRUNUSEDPROBECLASS_type = (org.infogrid.model.primitives.StringDataType) LASTRUNUSEDPROBECLASS.getDataType();

    /**
      * <p>Set value for property WaitForOngoingResynchronization.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_WaitForOngoingResynchronization</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>WaitForOngoingResynchronization</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Wait for ongoing resynchronization</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>If this is true, delay calling threads until triggered resynchronization requests have completed.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setWaitForOngoingResynchronization(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property WaitForOngoingResynchronization.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/ProbeUpdateSpecification_WaitForOngoingResynchronization</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>WaitForOngoingResynchronization</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Wait for ongoing resynchronization</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>If this is true, delay calling threads until triggered resynchronization requests have completed.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getWaitForOngoingResynchronization()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the WaitForOngoingResynchronization property.
      */
    public static final String WAITFORONGOINGRESYNCHRONIZATION_name = "WaitForOngoingResynchronization";

    /**
      * The WaitForOngoingResynchronization PropertyType.
      */
    public static final PropertyType WAITFORONGOINGRESYNCHRONIZATION = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Probe/ProbeUpdateSpecification_WaitForOngoingResynchronization" );
    public static final org.infogrid.model.primitives.BooleanDataType WAITFORONGOINGRESYNCHRONIZATION_type = (org.infogrid.model.primitives.BooleanDataType) WAITFORONGOINGRESYNCHRONIZATION.getDataType();

 // #### BEGIN DECLARED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * A successful run was just performed, but the data source did not change and no update was necessary.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public abstract void performedSuccessfulRunNoChange(
            long consumedTime )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;
         
 // #### END DECLARED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN DECLARED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * A successful run was just performed, the data source did change, and this is how long the run took.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public abstract void performedSuccessfulRunWithChange(
            long consumedTime )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;
         
 // #### END DECLARED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN DECLARED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * An attempt to run the Probe was unsuccessful, and no new data could be obtained because a problem occurred.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @param problem the Throwable that was thrown by the Probe, indicating the problem
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public abstract void performedUnsuccessfulRun(
            long      consumedTime,
            Throwable problem )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;
         
 // #### END DECLARED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN DECLARED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * Set the internal state of the Probe to stop automatic updates.
     */
    public abstract void stopUpdating();
         
 // #### END DECLARED METHOD (INSERTED FROM model.xml FILE) ####
}
