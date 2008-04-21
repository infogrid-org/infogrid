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

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;

/**
 * Relates a Shadow-producted MeshObject A to another MeshObject B outside of the Shadow,
 * and makes sure that the relationship goes away without error if B disappears on the
 * next Probe run.
 */
public class ShadowTest4
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

        NetMeshObjectIdentifier aId     = base.getMeshObjectIdentifierFactory().fromExternalForm( TEST_URL, "a" );
        NetMeshObjectIdentifier localId = base.getMeshObjectIdentifierFactory().fromExternalForm( "local" );

        //
        
        log.info( "Creating local object" );
        
        Transaction tx = base.createTransactionAsap();
        
        MeshObject local = base.getMeshBaseLifecycleManager().createMeshObject( localId );
        
        tx.commitTransaction();

        //
        
        log.info( "Accessing probe first time" );
        

        MeshObject a1 = base.accessLocally( TEST_URL, aId, CoherenceSpecification.ONE_TIME_ONLY );

        checkObject( a1, "a1 not there" );
        
        ShadowMeshBase shadow = base.getShadowMeshBaseFor( TEST_URL );
        checkEquals( shadow.size(), 2, "Wrong number of objects in Shadow" );

        //
        
        log.info( "Relating objects and checking" );
        
        tx = base.createTransactionAsap();
        
        local.relate( a1 );
        
        tx.commitTransaction();
        
        checkEquals( local.traverseToNeighborMeshObjects().size(), 1, "neighbor of local not found" );
        checkEquals(    a1.traverseToNeighborMeshObjects().size(), 2, "neighbor of a1 not found" );
        checkEquals( shadow.size(), 2, "Wrong number of objects in Shadow" );
        
        //
        
        log.info( "Running Probe again" );
        
        base.getShadowMeshBaseFor( TEST_URL ).doUpdateNow();
        
        Thread.sleep( 3100L );
        
        //
        
        log.info( "Checking" );
        
        checkEquals( shadow.size(), 1, "Wrong number of objects in Shadow" );
        checkEquals( local.traverseToNeighborMeshObjects().size(), 0, "unexpected neighbor of local found" );
        checkCondition( a1.getIsDead(), "a1 still alive" );
        
    }

    /*
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        ShadowTest4 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new ShadowTest4( args );
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
     * @throws Exception all kinds of things may happen in a test
     */
    public ShadowTest4(
            String [] args )
        throws
            Exception
    {
        super( ShadowTest4.class );

        theProbeDirectory.addExactUrlMatch(
                new ProbeDirectory.ExactMatchDescriptor(
                        TEST_URL.toExternalForm(),
                        TestApiProbe.class ));
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        exec.shutdown();
    }
    
    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest2.class );

    /**
     * The test protocol. In the real world this would be something like "jdbc".
     */
    private static final String PROTOCOL_NAME = "ShadowTest4Protocol";

    /**
     * The URL that we are accessing.
     */
    private static NetMeshBaseIdentifier TEST_URL;

    static {
        try {
            TEST_URL = NetMeshBaseIdentifier.createUnresolvable( PROTOCOL_NAME + "://myhost.local/remainder" );

        } catch( Exception ex ) {
            log.error( ex );
            
            TEST_URL = null; // make compiler happy
        }
    }

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

    /**
     * Counts the number of Probe runs.
     */
    protected static int theProbeRunCounter = 0;

    /**
     * The test Probe superclass.
     */
    public static class TestApiProbe
            implements
                ApiProbe
    {
        /**
         * Read from the API and instantiate corresponding MeshObjects.
         * 
         * @param dataSourceIdentifier identifies the data source that is being accessed
         * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
         *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
         *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
         *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
         *         in the <code>org.infogrid.model.Probe</code> Subject Area) that reflects the policy.
         * @param mb the StagingMeshBase in which the corresponding MeshObjects are to be instantiated by the Probe
         * @throws IsAbstractException thrown if an EntityType or a Relationship could not be instantiated because
         *         it was abstract. Throwing this typically indicates a programming error.
         * @throws EntityBlessedAlreadyException thrown if a MeshObject was incorrectly blessed twice with the same
         *         EntityType. Throwing this typically indicates a programming error.
         * @throws EntityNotBlessedException thrown if a MeshObject was not blessed with a required EntityType.
         *         Throwing this typically indicates a programming error.
         * @throws IllegalPropertyTypeException thrown if a MeshObject did not carry a PropertyType that it needed
         *         to carry. Throwing this typically indicates a programming error.
         * @throws IllegalPropertyValueException thrown if a PropertyValue was assigned to a property that was
         *         outside of the allowed range. Throwing this typically indicates a programming error.
         * @throws MeshObjectIdentifierNotUniqueException thrown if the Probe developer incorrectly
         *         assigned duplicate MeshObjectsIdentifiers to created MeshObjects.
         *         Throwing this typically indicates a programming error.
         * @throws NotPermittedException thrown if an operation performed by the Probe was not permitted
         * @throws NotRelatedException thrown if a relationship was supposed to be blessed, but the relationship
         *         did not exist. Throwing this typically indicates a programming error.
         * @throws RelatedAlreadyException thrown if the Probe developer incorrectly attempted to
         *         relate two already-related MeshObjects. Throwing this typically indicates a programming error.
         * @throws TransactionException this Exception is declared to make programming easier,
         *         although actually throwing it would be a programming error. Throwing this typically indicates a programming error.
         * @throws URISyntaxException thrown if a URI was constructed in an invalid way
         */
        public void readFromApi(
                NetMeshBaseIdentifier  dataSourceIdentifier,
                CoherenceSpecification coherenceSpecification,
                StagingMeshBase        mb )
            throws
                IsAbstractException,
                EntityBlessedAlreadyException,
                EntityNotBlessedException,
                IllegalPropertyTypeException,
                IllegalPropertyValueException,
                MeshObjectIdentifierNotUniqueException,
                NotPermittedException,
                NotRelatedException,
                RelatedAlreadyException,
                TransactionException,
                TransactionException,
                URISyntaxException
        {
            ++theProbeRunCounter;
            
            MeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();

            MeshObject home = mb.getHomeObject();
            home.bless( TestSubjectArea.AA );
            
            if( theProbeRunCounter == 1 ) {
                MeshObject a = life.createMeshObject( mb.getMeshObjectIdentifierFactory().fromExternalForm( "a" ), TestSubjectArea.B );
            
                home.relateAndBless( TestSubjectArea.RR.getSource(), a );
            }
        }
    }
}
