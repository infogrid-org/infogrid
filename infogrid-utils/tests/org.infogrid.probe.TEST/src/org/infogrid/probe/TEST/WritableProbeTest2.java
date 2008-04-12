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

package org.infogrid.probe.TEST;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.WriteableProbe;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.module.ModuleException;
import org.infogrid.probe.m.MProbeDirectory;

/**
  * Tests whether Property updates propagate into the WritableProbe and back to
  * to the NetMeshBase. 
  */
public class WritableProbeTest2
        extends
            AbstractProbeTest
{
    /**
     * Run the test.
     *
     * @throws Exception thrown if an Exception occurred during the test
     */
    public void run()
        throws
            Exception
    {
        NetMeshBaseIdentifier    here = NetMeshBaseIdentifier.create( "http://here.local/" ); // this is not going to work for communications
        LocalNetMMeshBase        base = LocalNetMMeshBase.create( here, theModelBase, null, exec, theProbeDirectory, rootContext );

        //
        
        log.info( "Initial accessLocally" );

        MeshObject home = (MeshObject) base.accessLocally( TEST1_URL );

        ShadowMeshBase shadow = base.getShadowMeshBaseFor( TEST1_URL );
        checkObject( shadow, "could not find shadow" );

        checkProbeRun();

        log.info( "Traversing to neighbors" );
        
        MeshObjectSet set = home.traverseToNeighborMeshObjects();

        checkProbeRun();

        //
        
        log.info( "Attempting to change objects in NetMeshBase" );

        Transaction tx = base.createTransactionAsap();

        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject = set.get( i );

            try {
                thisMeshObject.setPropertyValue( TestSubjectArea.B_U, StringValue.create( NEW_DESC ));

                checkCondition( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ).equals( NEW_DESC ), "Could not change Description of " + thisMeshObject.getIdentifier().toString());
                checkCondition(!thisMeshObject.getIdentifier().toString().endsWith("_Unchangeable"), thisMeshObject.getIdentifier().toString() + " is supposed to be unchangeable");

                descriptionChangeCountInQueue++;
                probeHasRun = false;

            } catch( NotPermittedException ex ) {
                checkCondition( !(thisMeshObject.getPropertyValue( TestSubjectArea.B_U ).equals( NEW_DESC )), "Could change Description of " + thisMeshObject.getIdentifier().toString());
                checkCondition( !thisMeshObject.getIdentifier().toString().endsWith("_Changeable"), thisMeshObject.getIdentifier().toString() + " is supposed to be changeable");
            }
        }

        tx.commitTransaction();

        checkProbeRun();

        //
        
        log.info( "Checking (1)" );

        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject     = set.get( i );
            String     thisMeshObjectName = thisMeshObject.getIdentifier().toExternalForm();

            if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_1 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_1 + 'a', PROJ_COMP_DESC_1 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_2 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), NEW_DESC, PROJ_COMP_DESC_2 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_3 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_3 + 'c', PROJ_COMP_DESC_3 );

            } else {
                reportError( "unexpected object " + thisMeshObject );
            }
        }

        //
        
        log.info( "Now doing a manual update (1)" );
        
        shadow.doUpdateNow();

        checkProbeRun();

        log.info( "Checking (2)" );

        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject     = set.get( i );
            String     thisMeshObjectName = thisMeshObject.getIdentifier().toExternalForm();

            if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_1 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_1 + "aa", PROJ_COMP_DESC_1 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_2 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), NEW_DESC + "b", PROJ_COMP_DESC_2 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_3 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_3 + "cc", PROJ_COMP_DESC_3 );

            } else {
                reportError( "unexpected object " + thisMeshObject );
            }
        }

        //
                
        log.info( "Now doing a manual update (2)" );

        shadow.doUpdateNow();

        checkProbeRun();

        log.info( "Checking (3)" );

        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject     = set.get( i );
            String     thisMeshObjectName = thisMeshObject.getIdentifier().toExternalForm();

            if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_1 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_1 + "aaa", PROJ_COMP_DESC_1 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_2 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), NEW_DESC + "bb", PROJ_COMP_DESC_2 );

            } else if( thisMeshObjectName.endsWith( PROJ_COMP_NAME_3 )) {
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_3 + "ccc", PROJ_COMP_DESC_3 );

            } else {
                reportError( "unexpected object " + thisMeshObject );
            }
        }
    }

    private void checkProbeRun()
    {
        if ( probeHasRun ) {
            checkEquals( descriptionChangeCountInQueue,
                         descriptionChangeCountDone,
                         "Check change list sent to the probe." );
            descriptionChangeCountInQueue = 0;
            descriptionChangeCountDone = 0;
            probeHasRun = false;
        }
    }

    /*
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        WritableProbeTest2 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new WritableProbeTest2( args );
            test.run();

        } catch( Throwable ex ) {
            log.error( ex );
            System.exit(1);
        }
        if( test != null ) {
            test.cleanup();
        }
        if( errorCount == 0 ) {
            log.info( "PASS" );
        } else {
            log.info( "FAIL (" + errorCount + " errors)" );
        }
        System.exit( errorCount );
    }

    /**
     * Constructor.
     *
     * @param args the command-line arguments
     */
    public WritableProbeTest2(
            String [] args )
        throws
            Exception
    {
        super( WritableProbeTest2.class );

        theProbeDirectory.addExactUrlMatch(
                new ProbeDirectory.ExactMatchDescriptor(
                        TEST1_URL.toExternalForm(),
                        TestApiProbe.class ));
    }
    
    /**
     * Cleanup.
     */
    @Override
    public void cleanup()
    {
        exec.shutdown();
    }

    // Our Logger
    private static Log log = Log.getLogInstance( WritableProbeTest2.class );

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

    /**
     * the test protocol, in the real world this would be something like "jdbc"
     */
    private static final String PROTOCOL_NAME = "WritableProbeTest1Protocol";

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

    /**
     * The first URL that we are accessing.
     */
    private static final NetMeshBaseIdentifier TEST1_URL;
    static {
        NetMeshBaseIdentifier temp;
        try {
            temp = NetMeshBaseIdentifier.createUnresolvable( PROTOCOL_NAME + "://some.where/one" );
        } catch( Exception ex ) {
            log.error( ex );
            temp = null; // make compiler happy
        }
        TEST1_URL = temp;
    }

    /**
     * The following simulates the data that TestApiProbe would be getting from a database
     */
    protected static final String PROJ_COMP_NAME_1 = "TestObject1_Unchangeable";
    protected static final String PROJ_COMP_NAME_2 = "TestObject2_Changeable";
    protected static final String PROJ_COMP_NAME_3 = "TestObject3_Unchangeable";
    protected static final String PROJ_COMP_DESC_1 = "This is projComp 1.  The explicitly unchangeable one.";
    protected static final String PROJ_COMP_DESC_2 = "This is projComp 2.  The changeable one.";
    protected static final String PROJ_COMP_DESC_3 = "This is projComp 3.  The default unchangeable one.";
    protected static final String NEW_DESC = "Got you";

    /**
     * The following simulates the data that TestApiProbe would be getting from a database
     */
    protected static StringValue description1 = StringValue.create( PROJ_COMP_DESC_1 );
    protected static StringValue description2 = StringValue.create( PROJ_COMP_DESC_2 );
    protected static StringValue description3 = StringValue.create( PROJ_COMP_DESC_3 );

    protected static int descriptionChangeCountInQueue = 0;
    protected static int descriptionChangeCountDone = 0;
    protected static boolean probeHasRun = false;

    /**
     * The test Probe superclass.
     */
    public static class TestApiProbe
        implements
            ApiProbe,
            WriteableProbe
    {
        /**
         * Read from the API and instantiate corresponding MeshObjects.
         * 
         * @param networkId the NetMeshBaseIdentifier that is being accessed
         * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
         *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
         *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
         *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
         *         in the <code>org.infogrid.model.Probe</code>) that reflects the policy.
         * @param mb the StagingMeshBase in which the corresponding MeshObjects are instantiated by the Probe
         * @throws IdeMeshObjectIdentifierNotUniqueExceptionrown if the Probe developer incorrectly
         *         assigned duplicate Identifiers to created MeshObjects
         * @throws RelatedAlreadyException thrown if the Probe developer incorrectly attempted to
         *         relate two already-related MeshObjects
         * @throws TransactionException this Exception is declared to make programming easier,
         *         although actually throwing it would be a programming error
         * @throws NotPermittedException thrown if an operation performed by the Probe was not permitted
         * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
         * @throws IOException an input/output error occurred during execution of the Probe
         * @throws ModuleException thrown if a Module required by the Probe could not be loaded
         */
        public void readFromApi(
                NetMeshBaseIdentifier  networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        mb )
            throws
                IsAbstractException,
                EntityBlessedAlreadyException,
                EntityNotBlessedException,
                RelatedAlreadyException,
                NotRelatedException,
                MeshObjectIdentifierNotUniqueException,
                IllegalPropertyTypeException,
                IllegalPropertyValueException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                ModuleException,
                URISyntaxException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: readFromApi()" );
            }

            MeshObject home = mb.getHomeObject();
            home.bless( TestSubjectArea.AA );

            description1 = StringValue.create( description1.value() + "a" );
            description2 = StringValue.create( description2.value() + "b" );
            description3 = StringValue.create( description3.value() + "c" );

            NetMeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();
            
            // unchangeable
            NetMeshObject projComp1 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_1 ),
                    TestSubjectArea.B );
            projComp1.setWillGiveUpLock( false );
            projComp1.setPropertyValue( TestSubjectArea.B_U, description1 );

            // changeable
            NetMeshObject projComp2 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_2 ),
                    TestSubjectArea.B );
            projComp2.setWillGiveUpLock( true );
            projComp2.setPropertyValue( TestSubjectArea.B_U, description2 );
            
            // unchangeable -- but, not explicitly set
            NetMeshObject projComp3 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_3 ),
                    TestSubjectArea.B );
            projComp3.setPropertyValue( TestSubjectArea.B_U, description3 );

            // relate them
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp1 );
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp2 );
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp3 );
        }

        /**
         * Write to the API.
         */
         public void write(
                NetMeshBaseIdentifier networkId,
                ChangeSet         updateSet )
            throws
                ProbeException,
                IOException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: write( " + updateSet + " )" );
            }

            for( Change current : updateSet.getChanges() ) {

                if( current instanceof MeshObjectPropertyChangeEvent ) {

                    MeshObjectPropertyChangeEvent theChange = (MeshObjectPropertyChangeEvent) current;

                    String       theChangedMeshObject   = theChange.getAffectedMeshObjectIdentifier().toExternalForm();
                    PropertyType theChangedPropertyType = theChange.getProperty();

                    if ( !TestSubjectArea.A_X.equals( theChangedPropertyType )) {
                        continue;
                    }

                    if( theChangedMeshObject.endsWith( "#" + PROJ_COMP_NAME_1 )) {
                        description1 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObject.endsWith( "#" + PROJ_COMP_NAME_2 )) {

                        description2 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObject.endsWith( "#" + PROJ_COMP_NAME_3 )) {
                        description3 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;
                    }
                }
            }
            probeHasRun = true;
        }
    }
}
