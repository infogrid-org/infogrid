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
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
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
  * Tests whether Property updates propagate into the WritableProbe and back to
  * to the NetMeshBase. 
  */
public class WritableProbeTest4
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
        NetMeshBaseIdentifier here = NetMeshBaseIdentifier.create( "http://here.local/" ); // this is not going to work for communications
        LocalNetMMeshBase     base = LocalNetMMeshBase.create( here, theModelBase, null, exec, theProbeDirectory, rootContext );

        //

        MeshObject home = base.accessLocally( TEST1_URL );

        ShadowMeshBase shadow = base.getShadowMeshBaseFor( TEST1_URL );
        checkObject( shadow, "could not find shadow" );
        MeshObjectSet set = home.traverseToNeighborMeshObjects();

        checkProbeRun();

        Transaction tx = null;

        checkProbeRun();

        tx = base.createTransactionAsap();

        MeshObject currentMeshObject = null;

        currentMeshObject = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        TEST1_URL,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_4 )));

        checkObject( currentMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_4);
        imorDescription4 = StringValue.create( "How things go on Tuesday" );
        currentMeshObject.setPropertyValue( TestSubjectArea.B_U, imorDescription4 );
        descriptionChangeCountInQueue++;

        currentMeshObject = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        TEST1_URL,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_4 )));

        checkObject( currentMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_4);
        imorDescription4 = StringValue.create( "Music for today" );
        currentMeshObject.setPropertyValue( TestSubjectArea.B_U, imorDescription4 );
        descriptionChangeCountInQueue++;

        currentMeshObject = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        TEST1_URL,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_5 )));

        checkObject( currentMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_5);
        imorDescription5 = StringValue.create( "Noone should underestimate the resolve of the United States" );
        currentMeshObject.setPropertyValue( TestSubjectArea.B_U, imorDescription5 );
        descriptionChangeCountInQueue++;

        probeHasRun = false;
        tx.commitTransaction();

        tx = base.createTransactionAsap();
        currentMeshObject = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        TEST1_URL,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_6 )));

        checkObject( currentMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_6);
        imorDescription6 = StringValue.create( "Let our people go" );
        currentMeshObject.setPropertyValue( TestSubjectArea.B_U, imorDescription6 );
        descriptionChangeCountInQueue++;

        currentMeshObject = base.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        TEST1_URL,
                        base.getMeshObjectIdentifierFactory().fromExternalForm( TEST1_URL.toExternalForm() + "#" + PROJ_COMP_NAME_4 )));

        checkObject( currentMeshObject, "Could not accessLocally " +  PROJ_COMP_NAME_4);
        imorDescription4 = StringValue.create( "The bases have responsibility" );
        currentMeshObject.setPropertyValue( TestSubjectArea.B_U, imorDescription4 );
        descriptionChangeCountInQueue++;

        probeHasRun = false;

        tx.commitTransaction();

        checkObjectStatusAndDescription( set, 1 );
        checkProbeRun();

        shadow.doUpdateNow();

        checkObjectStatusAndDescription( set, 2 );
        checkProbeRun();

        shadow.doUpdateNow();

        checkObjectStatusAndDescription( set, 3 );
        checkProbeRun();
    }

    private void checkObjectStatusAndDescription(
            MeshObjectSet set,
            int           probeRunCount )
        throws
            Exception
    {
        if( !probeHasRun ) {
            return;
        }

        for( int i=set.size()-1 ; i>=0 ; --i ) {

            MeshObject thisMeshObject = set.get( i );

            String thisMeshObjectName = thisMeshObject.getIdentifier().toExternalForm();

            if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_1 )) {
                
                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription1.value() + buildRepeatString("a", probeRunCount)), PROJ_COMP_DESC_1 );
                checkEquals( description1, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_1 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_2 )) {
                
                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription2.value() + buildRepeatString("b", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description2, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_2 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_3 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription3.value() + buildRepeatString("c", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description3, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_3 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_4 )) {
                
                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription4.value() + buildRepeatString("d", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description4, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_4 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_5 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription5.value() + buildRepeatString("e", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description5, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_5 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_6 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription6.value() + buildRepeatString("f", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description6, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_6 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_7 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription7.value() + buildRepeatString("g", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description7, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_7 );

            } else if( thisMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_8 )) {

                checkCondition( !thisMeshObject.getIsDead(), "" + thisMeshObject.getIdentifier() +  " should be alive." );
                checkEquals( thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), StringValue.create( imorDescription8.value() + buildRepeatString("h", probeRunCount)), PROJ_COMP_DESC_2 );
                checkEquals( description8, thisMeshObject.getPropertyValue( TestSubjectArea.B_U ), PROJ_COMP_DESC_8 );

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
        WritableProbeTest4 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new WritableProbeTest4( args );
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
    public WritableProbeTest4(
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
    private static Log log = Log.getLogInstance(WritableProbeTest4.class);

    /**
     * the test protocol, in the real world this would be something like "jdbc"
     */
    private static final String PROTOCOL_NAME = "WritableProbeTest3Protocol";

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

    /**
     * The imor copy of the descriptions
     */
    protected StringValue imorDescription1 = description1;
    protected StringValue imorDescription2 = description2;
    protected StringValue imorDescription3 = description3;
    protected StringValue imorDescription4 = description4;
    protected StringValue imorDescription5 = description5;
    protected StringValue imorDescription6 = description6;
    protected StringValue imorDescription7 = description7;
    protected StringValue imorDescription8 = description8;

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

            String [] names = {
                PROJ_COMP_NAME_1,
                PROJ_COMP_NAME_2,
                PROJ_COMP_NAME_3,
                PROJ_COMP_NAME_4,
                PROJ_COMP_NAME_5,
                PROJ_COMP_NAME_6,
                PROJ_COMP_NAME_7,
                PROJ_COMP_NAME_8,
            };
            StringValue [] descriptions = {
                description1,
                description2,
                description3,
                description4,
                description5,
                description6,
                description7,
                description8,
            };

            for( int i=0 ; i<names.length ; ++i ) {
                NetMeshObject current = life.createMeshObject(
                        mb.getMeshObjectIdentifierFactory().fromExternalForm( names[i] ),
                        TestSubjectArea.B );
                current.setWillGiveUpLock( true );
                current.setPropertyValue( TestSubjectArea.B_U, descriptions[i] );
                
                home.relateAndBless( TestSubjectArea.RR.getSource(), current );
            }
        }

        /**
         * Write to the API and instantiate corresponding model objects.
         *
         * @param updateSet the set of changes to write
         * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
         * @throws IOException an input/output error occurred during execution of the Probe
         */
         public void write(
                NetMeshBaseIdentifier networkId,
                ChangeSet updateSet )
            throws
                ProbeException,
                IOException
        {
            for( int i = 0; i < updateSet.getChanges().length; ++i ) {
                Change current = updateSet.getChanges()[i];

                if( current instanceof MeshObjectDeletedEvent ) {

                    MeshObjectDeletedEvent theChange = (MeshObjectDeletedEvent) current;

                    throw new ProbeException.ErrorInProbe( networkId, this.getClass() );
                }

                if( current instanceof MeshObjectPropertyChangeEvent )
                {
                    MeshObjectPropertyChangeEvent theChange = (MeshObjectPropertyChangeEvent) current;

                    String theChangedMeshObjectName = theChange.getAffectedMeshObjectIdentifier().toExternalForm();

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

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_4 )) {
                        description4 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_5 )) {
                        description5 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_6 )) {
                        description6 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_7 )) {
                        description7 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;

                    } else if( theChangedMeshObjectName.endsWith( "#" + PROJ_COMP_NAME_8 )) {
                        description8 = (StringValue) theChange.getDeltaValue();
                        descriptionChangeCountDone++;
                    }
                }
            }
            probeHasRun = true;
        }
    }
}
