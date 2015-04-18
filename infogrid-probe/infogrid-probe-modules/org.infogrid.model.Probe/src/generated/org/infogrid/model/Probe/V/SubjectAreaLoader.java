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

package org.infogrid.model.Probe.V;

import org.infogrid.modelbase.*;
import org.infogrid.model.primitives.*;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.model.traversal.TraversalToPropertySpecification;
import java.io.IOException;

/**
  * This class loads the model of the Probe Subject Area Subject Area.
  */
public class SubjectAreaLoader
        extends
            ModelLoader
{
    /**
      * Constructor.
      *
      * @param modelBase the ModelBase into which the SubjectArea will be loaded
      * @param loader the ClassLoader to be used for resolving resources
      */
    public SubjectAreaLoader(
            ModelBase   modelBase,
            ClassLoader loader )
    {
        super( modelBase );
        theLoader = loader;
    }

    /**
     * Instruct this object to instantiate its model.
     *
     * @param theInstantiator the MeshTypeLifecycleManager which shall be used to instantiate
     * @param now time the run was started
     * @return the instantiated SubjectArea(s)
     * @throws MeshTypeNotFoundException thrown if there was an undeclared dependency in the model that could not be resolved
     * @throws InheritanceConflictException thrown if there was a conflict in the inheritance hierarchy of the newly loaded model
     * @throws IOException thrown if reading the model failed
     */
    protected SubjectArea [] loadModel(
            MeshTypeLifecycleManager theInstantiator,
            TimeStampValue           now )
        throws
            MeshTypeNotFoundException,
            InheritanceConflictException,
            IOException,
            org.infogrid.model.primitives.UnknownEnumeratedValueException
    {
        org.infogrid.util.logging.Log log = org.infogrid.util.logging.Log.getLogInstance( getClass() );
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "loadModel" );
        }

        SubjectArea theSa = theInstantiator.createSubjectArea(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe" ),   // IdentifierValue           theIdentifier,
                org.infogrid.model.primitives.StringValue.create( "org.infogrid.model.Probe" ),   // StringValue               theName,
                null,   // StringValue               theVersion,
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Probe Subject Area" ) ),   // L10Map                    theUserNames,
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "Captures the information necessary for the management of Probes." , "text/html" ) ),   // L10Map                    theUserDescriptions,
                new SubjectArea[] { },   // SubjectArea []            theSubjectAreaDependencies,
                theLoader,   // ClassLoader               theClassLoader,
                org.infogrid.model.primitives.BooleanValue.TRUE,   // BooleanValue              doGenerateInterfaceCode,
                org.infogrid.model.primitives.BooleanValue.TRUE ); // BooleanValue              doGenerateImplementationCode )

        // generateLoadOneEntity section

        EntityType obj0 = theInstantiator.createEntityType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification" ),
                org.infogrid.model.primitives.StringValue.create( "ProbeUpdateSpecification" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Probe Update Specification" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "A Home Object of a Probe is blessed with a subtype of this ProbeUpdateSpecification.\nIt expresses meta-data such as when the Probe was last run, when it should be run next etc.\nSubtypes may implement different algorithms to determine the respective data." , "text/html" ) ),
                null,
                theSa,
                new AttributableMeshType[] { },
                new MeshTypeIdentifier [] {
},
                null,
                new String[] {},
                new StringValue[] { org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, but the data source did not change and no update was necessary.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public abstract void performedSuccessfulRunNoChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException;\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, the data source did change, and this is how long the run took.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public abstract void performedSuccessfulRunWithChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException;\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * An attempt to run the Probe was unsuccessful, and no new data could be obtained because a problem occurred.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @param problem the Throwable that was thrown by the Probe, indicating the problem\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public abstract void performedUnsuccessfulRun(\n            long      consumedTime,\n            Throwable problem )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException;\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * Set the internal state of the Probe to stop automatic updates.\n     */\n    public abstract void stopUpdating();\n         " ) },
                new StringValue[0],
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE );

        org.infogrid.model.primitives.TimeStampDataType obj0_pt0_type = org.infogrid.model.primitives.TimeStampDataType.theDefault;
        PropertyType obj0_pt0 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_NextProbeRun" ),
                org.infogrid.model.primitives.StringValue.create( "NextProbeRun" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Time of next Probe run" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The time at which to run this Probe next. If this is not given, this indicates\n\"never\". If this is in the past, it means \"as soon as possible\". This value\nis given in Java\'s System.currentTimeMillis() format." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt0_type,
                null,
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.TimeStampDataType obj0_pt1_type = org.infogrid.model.primitives.TimeStampDataType.theDefault;
        PropertyType obj0_pt1 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastProbeRun" ),
                org.infogrid.model.primitives.StringValue.create( "LastProbeRun" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Time of most recent Probe run" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The time that the last Probe run was attempted (successful or unsuccessful).\n                  If this is not given, it means that the Probe was never run. This value\n                  is given in Java\'s System.currentTimeMillis() format." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt1_type,
                null,
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.IntegerDataType obj0_pt2_type = org.infogrid.model.primitives.IntegerDataType.theDefault;
        PropertyType obj0_pt2 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_ProbeRunCounter" ),
                org.infogrid.model.primitives.StringValue.create( "ProbeRunCounter" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Probe-run counter" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "This value is incremented every time the Probe is run." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt2_type,
                org.infogrid.model.primitives.IntegerValue.create( 0L ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.BooleanDataType obj0_pt3_type = org.infogrid.model.primitives.BooleanDataType.theDefault;
        PropertyType obj0_pt3 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedWritableProbe" ),
                org.infogrid.model.primitives.StringValue.create( "LastRunUsedWritableProbe" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Runs as Writable Probe" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "If this is true, the last Probe run used a WritableProbe." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt3_type,
                null,
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.StringDataType obj0_pt4_type = org.infogrid.model.primitives.StringDataType.theDefault;
        PropertyType obj0_pt4 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_LastRunUsedProbeClass" ),
                org.infogrid.model.primitives.StringValue.create( "LastRunUsedProbeClass" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Runs using Probe Class" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "Name of the Probe Class that was used at the last run." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt4_type,
                null,
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.BooleanDataType obj0_pt5_type = org.infogrid.model.primitives.BooleanDataType.theDefault;
        PropertyType obj0_pt5 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification_WaitForOngoingResynchronization" ),
                org.infogrid.model.primitives.StringValue.create( "WaitForOngoingResynchronization" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Wait for ongoing resynchronization" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "If this is true, delay calling threads until triggered resynchronization requests have completed." , "text/html" ) ),
                obj0,
                theSa,
                obj0_pt5_type,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        EntityType obj1 = theInstantiator.createEntityType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/OneTimeOnlyProbeUpdateSpecification" ),
                org.infogrid.model.primitives.StringValue.create( "OneTimeOnlyProbeUpdateSpecification" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Once-ever Probe Update Specification" ) ),
                null,
                null,
                theSa,
                new AttributableMeshType[] { theModelBase.findAttributableMeshTypeByIdentifier( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification" ) ) },
                new MeshTypeIdentifier [] {
},
                null,
                new String[] {},
                new StringValue[0],
                new StringValue[] { org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, but the data source did not change and no update was necessary.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunNoChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n        the_Delegate.setPropertyValue( NEXTPROBERUN, null ); // never again\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunNoChange\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, the data source did change, and this is how long the run took.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunWithChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n        the_Delegate.setPropertyValue( NEXTPROBERUN, null ); // never again\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunWithChange\" );\n        }\n     }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * An attempt to run the Probe was unsuccessful,  and no new data could be obtained because a problem occurred.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @param problem the Throwable that was thrown by the Probe, indicating the problem\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedUnsuccessfulRun(\n            long      consumedTime,\n            Throwable problem )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n        the_Delegate.setPropertyValue( NEXTPROBERUN, null ); // never again\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedUnsuccessfulRun\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * Set the internal state of the Probe to stop automatic updates.\n     */\n    public void stopUpdating()\n    {\n        // do nothing, this runs only once\n    }\n         " ) },
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE );

        EntityType obj2 = theInstantiator.createEntityType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification" ),
                org.infogrid.model.primitives.StringValue.create( "PeriodicProbeUpdateSpecification" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Periodic Probe Update Specification" ) ),
                null,
                null,
                theSa,
                new AttributableMeshType[] { theModelBase.findAttributableMeshTypeByIdentifier( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification" ) ) },
                new MeshTypeIdentifier [] {
},
                null,
                new String[] {},
                new StringValue[0],
                new StringValue[] { org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, but the data source did not change and no update was necessary.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunNoChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        \n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n\n        long theDelay = ((IntegerValue) the_Delegate.getPropertyValue( DELAY )).value();\n        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( theDelay ));\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunNoChange\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, the data source did change, and this is how long the run took.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunWithChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        \n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n\n        long theDelay = ((IntegerValue) the_Delegate.getPropertyValue( DELAY )).value();\n        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( theDelay ));\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunWithChange\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * An attempt to run the Probe was unsuccessful,  and no new data could be obtained because a problem occurred.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @param problem the Throwable that was thrown by the Probe, indicating the problem\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedUnsuccessfulRun(\n            long      consumedTime,\n            Throwable problem )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        \n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n\n        long theDelay = ((IntegerValue) the_Delegate.getPropertyValue( DELAY )).value();\n        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( theDelay ));\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedUnsuccessfulRun\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * Set the internal state of the Probe to stop automatic updates.\n     */\n    public void stopUpdating()\n    {\n        try {\n            the_Delegate.setPropertyValue( NEXTPROBERUN, null );\n        } catch( Exception ex ) {\n            log.error( ex );\n        }\n    }\n         " ) },
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE );

        org.infogrid.model.primitives.IntegerDataType obj2_pt0_type = org.infogrid.model.primitives.IntegerDataType.theDefault;
        PropertyType obj2_pt0 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_Delay" ),
                org.infogrid.model.primitives.StringValue.create( "Delay" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Delay between Probe runs" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "Delay between the end of a Probe run and the start of the next Probe run." , "text/html" ) ),
                obj2,
                theSa,
                obj2_pt0_type,
                org.infogrid.model.primitives.IntegerValue.create( 86400000L ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        EntityType obj3 = theInstantiator.createEntityType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification" ),
                org.infogrid.model.primitives.StringValue.create( "AdaptivePeriodicProbeUpdateSpecification" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Adaptive Period Probe Update Specification" ) ),
                null,
                null,
                theSa,
                new AttributableMeshType[] { theModelBase.findAttributableMeshTypeByIdentifier( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/ProbeUpdateSpecification" ) ) },
                new MeshTypeIdentifier [] {
},
                null,
                new String[] {},
                new StringValue[0],
                new StringValue[] { org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, but the data source did not change and no update was necessary.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunNoChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        \n        double theAdaptiveFactor = ((FloatValue)   the_Delegate.getPropertyValue( ADAPTIVEFACTOR )).value();\n        long   theCurrentDelay   = ((IntegerValue) the_Delegate.getPropertyValue( CURRENTDELAY )).value();\n\n        long newDelay = (long) ( theCurrentDelay * theAdaptiveFactor );\n\n        IntegerValue maxDelay = (IntegerValue) the_Delegate.getPropertyValue( MAXDELAY );\n        if( maxDelay != null ) {\n            newDelay = Math.min( newDelay, maxDelay.value() );\n        }\n        \n        the_Delegate.setPropertyValue( CURRENTDELAY, IntegerValue.create( newDelay ));\n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( newDelay ));\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunNoChange\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * A successful run was just performed, the data source did change, and this is how long the run took.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedSuccessfulRunWithChange(\n            long consumedTime )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();\n        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));\n        \n        PropertyValue theFallbackDelay = the_Delegate.getPropertyValue( FALLBACKDELAY );\n        the_Delegate.setPropertyValue( CURRENTDELAY, theFallbackDelay );\n        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );\n        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( ((IntegerValue)theFallbackDelay).value() ));\n\n        if( log.isTraceEnabled() ) {\n            log.traceMethodCallEntry( this, \"performedSuccessfulRunWithChange\" );\n        }\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * An attempt to run the Probe was unsuccessful,  and no new data could be obtained because a problem occurred.\n     *\n     * @param consumedTime the time in milliseconds that the update took\n     * @param problem the Throwable that was thrown by the Probe, indicating the problem\n     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType\n     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue\n     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation\n     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty\n     */\n    public void performedUnsuccessfulRun(\n            long      consumedTime,\n            Throwable problem )\n        throws\n            org.infogrid.mesh.IllegalPropertyTypeException,\n            org.infogrid.mesh.IllegalPropertyValueException,\n            org.infogrid.mesh.NotPermittedException,\n            org.infogrid.meshbase.transaction.TransactionException\n    {\n        performedSuccessfulRunWithChange( consumedTime ); // simpler\n    }\n         " ), org.infogrid.model.primitives.StringValue.create( "\n    /**\n     * Set the internal state of the Probe to stop automatic updates.\n     */\n    public void stopUpdating()\n    {\n        try {\n            the_Delegate.setPropertyValue( NEXTPROBERUN, null );\n            the_Delegate.setPropertyValue( CURRENTDELAY, IntegerValue.create( 3600000 ) ); // back to default\n        } catch( Exception ex ) {\n            log.error( ex );\n        }\n    }\n         " ) },
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE );

        org.infogrid.model.primitives.IntegerDataType obj3_pt0_type = org.infogrid.model.primitives.IntegerDataType.theDefault;
        PropertyType obj3_pt0 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_CurrentDelay" ),
                org.infogrid.model.primitives.StringValue.create( "CurrentDelay" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Current delay between Probe runs" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The current delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue." , "text/html" ) ),
                obj3,
                theSa,
                obj3_pt0_type,
                org.infogrid.model.primitives.IntegerValue.create( 3600000L ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.IntegerDataType obj3_pt1_type = org.infogrid.model.primitives.IntegerDataType.theDefault;
        PropertyType obj3_pt1 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_MaxDelay" ),
                org.infogrid.model.primitives.StringValue.create( "MaxDelay" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Maximum delay between Probe runs" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The maximum delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue." , "text/html" ) ),
                obj3,
                theSa,
                obj3_pt1_type,
                org.infogrid.model.primitives.IntegerValue.create( 604800000L ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.IntegerDataType obj3_pt2_type = org.infogrid.model.primitives.IntegerDataType.theDefault;
        PropertyType obj3_pt2 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_FallbackDelay" ),
                org.infogrid.model.primitives.StringValue.create( "FallbackDelay" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Fallback delay between Probe runs" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The fallback delay between the end of a Probe run and the start of the next Probe run.\nCurrentDelay is re-initialized to this value when a Probe run was unsuccessful, or data changed.\nFIXME: this should use TimePeriodValue not IntegerValue." , "text/html" ) ),
                obj3,
                theSa,
                obj3_pt2_type,
                org.infogrid.model.primitives.IntegerValue.create( 600000L ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        org.infogrid.model.primitives.FloatDataType obj3_pt3_type = org.infogrid.model.primitives.FloatDataType.theDefault;
        PropertyType obj3_pt3 = theInstantiator.createPropertyType(
                theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( "org.infogrid.model.Probe/PeriodicProbeUpdateSpecification_AdaptiveFactor" ),
                org.infogrid.model.primitives.StringValue.create( "AdaptiveFactor" ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.StringValue.create( "Adaptive Factor" ) ),
                org.infogrid.model.primitives.L10PropertyValueMapImpl.create( org.infogrid.model.primitives.BlobDataType.theTextAnyType.createBlobValue( "The factor by which the delay increases for each Probe run that was successful and did not produce changed data." , "text/html" ) ),
                obj3,
                theSa,
                obj3_pt3_type,
                org.infogrid.model.primitives.FloatValue.create( 1.1 ),
                null,
                new String[] {},
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.FALSE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.BooleanValue.TRUE,
                org.infogrid.model.primitives.FloatValue.create( 0.0 ) );

        // generateLoadOneRelationshipType section

        // generateFixOneEntityType section

        return new SubjectArea[] { theSa };
    }

    /**
      * The ClassLoader to be used for resources.
      */
    protected ClassLoader theLoader;

}
