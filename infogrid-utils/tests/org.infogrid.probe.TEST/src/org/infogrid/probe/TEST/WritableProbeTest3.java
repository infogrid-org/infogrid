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
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.MeshObjectDeletedEvent;
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

/**
  * Tests whether deletions propagate into the WritableProbe and back to
  * to the NetMeshBase. 
  */
public class WritableProbeTest3
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

        MeshBaseLifecycleManager life = base.getMeshBaseLifecycleManager();

        //
        
        log.info( "Running" );

        MeshObject home = (MeshObject) base.accessLocally( TEST1_URL );

        ShadowMeshBase shadow = base.getShadowMeshBaseFor( TEST1_URL );
        checkObject( shadow, "could not find shadow" );

        checkProbeRun();

        Transaction tx = base.createTransactionAsap();

        MeshObject deleteMeshObject = null;

        // try to delete ProjComp1
        tryCanNotDelete( base, PROJ_COMP_NAME_1 );

        // delete ProjComp2
        deleteMeshObject = base.accessLocally(
                base.getMeshObjectIdentifierFactory().fromExternalForm(
                        TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_2 ) );

        checkObject( deleteMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_2);
        life.deleteMeshObject( deleteMeshObject );

        imorHasProjComp2 = false;

        // try to delete ProjComp3
        tryCanNotDelete( base, PROJ_COMP_NAME_3 );

        // try to delete ProjComp4
        tryCanNotDelete( base, PROJ_COMP_NAME_4 );

        // try to delete ProjComp5
        tryCanNotDelete( base, PROJ_COMP_NAME_5 );

        // try to delete ProjComp6
        tryCanNotDelete( base, PROJ_CONTAINS_NAME_6 );

        // delete ProjComp7
        deleteMeshObject = base.accessLocally(
                base.getMeshObjectIdentifierFactory().fromExternalForm(
                        TEST1_URL.toExternalForm() + "#" + PROJ_CONTAINS_NAME_7 ) );
                
        checkObject( deleteMeshObject, "Could not accessLocally " +  PROJ_CONTAINS_NAME_7);
        life.deleteMeshObject( deleteMeshObject );

// I think: FIXME?
MeshObjectSet set = home.traverseToNeighborMeshObjects();
        
        imorHasContains7 = false;

        // try to delete ProjComp8
        tryCanNotDelete( base, PROJ_CONTAINS_NAME_8 );

        probeHasRun = false;

        tx.commitTransaction();

        checkProbeRun();
        checkObjectStatusAndDescription( set, 1 );

        shadow.doUpdateNow();

        checkProbeRun();
        checkObjectStatusAndDescription( set, 2 );

        shadow.doUpdateNow();

        checkProbeRun();
        checkObjectStatusAndDescription( set, 3 );
    }

    private void checkObjectStatusAndDescription(
            MeshObjectSet set,
            int           probeRunCount )
        throws
            Exception
    {
        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject = set.get( i );

            String thisMeshObjectName = thisMeshObject.getIdentifier().toExternalForm();

            if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_1 )) {
                
                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_1 + buildRepeatString("a", probeRunCount), PROJ_COMP_DESC_1 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_2 )) {
                
                checkCondition( thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be dead." );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_3 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_3 + buildRepeatString("c", probeRunCount), PROJ_COMP_DESC_3 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_4 )) {
                
                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_4, PROJ_COMP_DESC_4 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_5 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_5, PROJ_COMP_DESC_5 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_6 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_6, PROJ_COMP_DESC_6 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_7 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_7, PROJ_COMP_DESC_7 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_8 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.A_X ), PROJ_COMP_DESC_8, PROJ_COMP_DESC_8 );

            } else {
                reportError( "unexpected object " + thisMeshObject );
            }
        }
    }

    private String buildRepeatString(
            String toRepeat,
            int    appendixCount )
    {
        StringBuilder appendixString = new StringBuilder();
        for( int i=0 ; i<appendixCount ; ++i ) {
            appendixString.append( toRepeat );
        }
        return appendixString.toString();
    }

    private void tryCanNotDelete(
            MeshBase mb,
            String   localName )
        throws
            MeshObjectAccessException,
            TransactionException,
            URISyntaxException
    {
        // try to delete
        try {
            MeshObject deleteRootObject = mb.accessLocally(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm(
                            TEST1_URL.toExternalForm() + "#" + localName ) );

            checkObject( deleteRootObject, "Could not accessLocally " + localName );

            mb.getMeshBaseLifecycleManager().deleteMeshObject( deleteRootObject );

            reportError( "Should not be able to delete " + localName );

        } catch ( NotPermittedException e ) {
            // no op
        }
    }

    private void checkProbeRun()
    {
        if ( probeHasRun ) {
            checkEquals ( imorHasProjComp2, probeHasProjComp2, "Imor and probe out of synch." );
            checkEquals ( imorHasProjComp2, probeHasContains2, "Imor and probe out of synch." );
            checkEquals ( imorHasContains7, probeHasContains7, "Imor and probe out of synch." );

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
    public WritableProbeTest3(
            String [] args )
        throws
            Exception
    {
        super( WritableProbeTest3.class );

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
    private static Log log = Log.getLogInstance( WritableProbeTest3.class );

    /**
    /**
     * the test protocol, in the real world this would be something like "jdbc"
     */
    private static final String PROTOCOL_NAME = "WritableProbeTest2Protocol";

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
    protected static final String PROJ_COMP_NAME_4 = "TestObject4_Undeletable";
    protected static final String PROJ_COMP_NAME_5 = "TestObject5_Undeletable";
    protected static final String PROJ_COMP_NAME_6 = "TestObject6_Undeletable";
    protected static final String PROJ_COMP_NAME_7 = "TestObject7_Undeletable";
    protected static final String PROJ_COMP_NAME_8 = "TestObject8_Undeletable";
    protected static final String PROJ_COMP_DESC_1 = "This is projComp 1.  The explicitly unchangeable one.";
    protected static final String PROJ_COMP_DESC_2 = "This is projComp 2.  The changeable one.";
    protected static final String PROJ_COMP_DESC_3 = "This is projComp 3.  The default unchangeable one.";
    protected static final String PROJ_COMP_DESC_4 = "This is projComp 4.  MeshObject deletable, but relationship not.";
    protected static final String PROJ_COMP_DESC_5 = "This is projComp 5.  Relationship deletable, but MeshObject not.";
    protected static final String PROJ_COMP_DESC_6 = "This is projComp 6.  MeshObject undeletable, relationship undeletable.";
    protected static final String PROJ_COMP_DESC_7 = "This is projComp 7.  MeshObject undeletable, relationship deletable.";
    protected static final String PROJ_COMP_DESC_8 = "This is projComp 8.  MeshObject undeletable, relationship undeletable by default.";
    protected static final String NEW_DESC = "Got you";
    protected static final String PROJ_CONTAINS_NAME_1 = "contains1";
    protected static final String PROJ_CONTAINS_NAME_2 = "contains2";
    protected static final String PROJ_CONTAINS_NAME_3 = "contains3";
    protected static final String PROJ_CONTAINS_NAME_4 = "contains4";
    protected static final String PROJ_CONTAINS_NAME_5 = "contains5";
    protected static final String PROJ_CONTAINS_NAME_6 = "contains6";
    protected static final String PROJ_CONTAINS_NAME_7 = "contains7";
    protected static final String PROJ_CONTAINS_NAME_8 = "contains8";

    /**
     * The following simulates the data that TestApiProbe would be getting from a database
     */
    protected static StringValue description1 = StringValue.create( PROJ_COMP_DESC_1 );
    protected static StringValue description2 = StringValue.create( PROJ_COMP_DESC_2 );
    protected static StringValue description3 = StringValue.create( PROJ_COMP_DESC_3 );
    protected static StringValue description4 = StringValue.create( PROJ_COMP_DESC_4 );
    protected static StringValue description5 = StringValue.create( PROJ_COMP_DESC_5 );
    protected static StringValue description6 = StringValue.create( PROJ_COMP_DESC_6 );
    protected static StringValue description7 = StringValue.create( PROJ_COMP_DESC_7 );
    protected static StringValue description8 = StringValue.create( PROJ_COMP_DESC_8 );

    protected static boolean probeHasProjComp2 = true;
    protected static boolean probeHasContains2 = true;
    protected static boolean imorHasProjComp2 = true;

    protected static boolean probeHasContains7 = true;
    protected static boolean imorHasContains7 = true;

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
                NetMeshBaseIdentifier      networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        mb )
            throws
                MeshObjectIdentifierNotUniqueException,
                RelatedAlreadyException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                URISyntaxException
        {
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
            NetMeshObject projComp2 = null;
            if ( probeHasProjComp2 ) {

                projComp2 = life.createMeshObject(
                        mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_2 ),
                        TestSubjectArea.B );
                projComp2.setWillGiveUpLock( true );
                projComp2.setPropertyValue( TestSubjectArea.B_U, description2 );
            }

            // unchangeable -- but, not explicitly set
            NetMeshObject projComp3 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_3 ),
                    TestSubjectArea.B );
            projComp3.setPropertyValue( TestSubjectArea.B_U, description3 );

            // undeletable -- MeshObject lock can move, but relationship is undeletable
            NetMeshObject projComp4 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_4 ),
                    TestSubjectArea.B );
            projComp4.setWillGiveUpLock( true );
            projComp4.setPropertyValue( TestSubjectArea.B_U, description4 );

            // undeletable ProjectComponent  -- but relationship is deletable
            NetMeshObject projComp5 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_5 ),
                    TestSubjectArea.B );
            projComp5.setWillGiveUpLock( false );
            projComp5.setPropertyValue( TestSubjectArea.B_U, description5 );

            // undeletable -- and relationship is undeletable
            NetMeshObject projComp6 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_6 ),
                    TestSubjectArea.B );
            projComp6.setWillGiveUpLock( false );
            projComp6.setPropertyValue( TestSubjectArea.B_U, description6 );

            // undeletable -- but relationship is deletable
            NetMeshObject projComp7 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_7 ),
                    TestSubjectArea.B );
            projComp7.setWillGiveUpLock( false );
            projComp7.setPropertyValue( TestSubjectArea.B_U, description7 );

            // undeletable -- but relationship is undeletable by default
            NetMeshObject projComp8 = life.createMeshObject(
                    mb.getMeshObjectIdentifierFactory().fromExternalForm( PROJ_COMP_NAME_8 ),
                    TestSubjectArea.B );
            projComp8.setWillGiveUpLock( false );
            projComp8.setPropertyValue( TestSubjectArea.B_U, description8 );

            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp1 );
            
            if ( probeHasProjComp2 && probeHasContains2 ) {

                home.relateAndBless( TestSubjectArea.RR.getSource(), projComp2 );                
            }

            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp3 );
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp4 );
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp5 );
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp6 );

            if ( probeHasContains7 ) {                
                home.relateAndBless( TestSubjectArea.RR.getSource(), projComp7 );
            }
            home.relateAndBless( TestSubjectArea.RR.getSource(), projComp8 );
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
            for ( int i = 0; i < updateSet.size(); ++i ) {
                Change current = updateSet.getChanges()[i];

                if( current instanceof MeshObjectDeletedEvent ) {

                    MeshObjectDeletedEvent theChange = (MeshObjectDeletedEvent) current;

                    String theChangedMeshObjectName = theChange.getAffectedMeshObjectIdentifier().toExternalForm();


                    if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_2 )) {
                        probeHasProjComp2 = false;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_CONTAINS_NAME_2 ) ) {
                        probeHasContains2 = false;

                    } else if ( theChangedMeshObjectName.endsWith( "#" + PROJ_CONTAINS_NAME_7 ) ) {
                        probeHasContains7 = false;

                    } else {
                        throw new ProbeException.ErrorInProbe( networkId, this.getClass() );
                    }
                }

                if( current instanceof MeshObjectPropertyChangeEvent ) {

                    MeshObjectPropertyChangeEvent theChange = (MeshObjectPropertyChangeEvent) current;

                    String     theChangedMeshObjectName = theChange.getAffectedMeshObjectIdentifier().toExternalForm();

                    PropertyType theChangedPropertyType = theChange.getProperty();

                    if ( !theChangedPropertyType.equals( TestSubjectArea.B_U )) {
                        continue;
                    }

                    if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_1 )) {
                        description1 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_2 )) {
                        description2 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_3 )) {
                        description3 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;
                    }
                }
            }
            probeHasRun = true;
        }
    }
}
