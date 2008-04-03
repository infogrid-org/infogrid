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
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.WriteableProbe;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.probe.m.MProbeDirectory;

/**
  * The simplest WritableProbeTest that I could think of: modify a single property.
  */
public class WritableProbeTest1
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
        
        log.info( "Initial accessLocally" );

        MeshObject home = (MeshObject) base.accessLocally( TEST1_URL );

        ShadowMeshBase shadow = base.getShadowMeshBaseFor( TEST1_URL );
        checkObject( shadow, "could not find shadow" );

        //
        
        log.info( "Attempting to change object in NetMeshBase" );

        Transaction tx = base.createTransactionAsap();

        try {
            home.setPropertyValue( TestSubjectArea.A_X, NEW_VALUE );

            checkCondition( home.getPropertyValue( TestSubjectArea.A_X ).equals( NEW_VALUE ), "Could not change property" );

        } catch( NotPermittedException ex ) {
            reportError( "Was not permitted to change", ex );
        }

        tx.commitTransaction();

        Thread.sleep( 2500L );
        
//
        
        log.info( "Now doing a manual update (1)" );
        
        checkCondition( theChangeSet == null, "ChangeSet not null, is " + theChangeSet );
        
        shadow.doUpdateNow();

        checkObject( theChangeSet, "ChangeSet is still null" );
        checkEquals( theChangeSet.size(), 1, "Wrong size ChangeSet" );
        
        //
        
        log.info( "Waiting for replication to happen" );
        
        Thread.sleep( 2500L );
        
        checkEquals( home.getPropertyValue( TestSubjectArea.A_X ), OLD_VALUE, "did not revert to old value" );
    }
    
    /*
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        WritableProbeTest1 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new WritableProbeTest1( args );
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
    public WritableProbeTest1(
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
    private static Log log = Log.getLogInstance( WritableProbeTest1.class );

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

    /**
     * the test protocol, in the real world this would be something like "jdbc"
     */
    private static final String PROTOCOL_NAME = "WritableProbeTest1Protocol";

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

    protected static final StringValue OLD_VALUE = StringValue.create( "old" );
    protected static final StringValue NEW_VALUE = StringValue.create( "new" );

    /**
     * Once the WritableProbe runs, it deposits the found ChangeSet here.
     */
    protected static ChangeSet theChangeSet;
    
    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

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
         *         in the <code>org.infogrid.model.ProbeModel</code>) that reflects the policy.
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
                IOException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: readFromApi()" );
            }

            NetMeshObject home = mb.getHomeObject();
            home.bless( TestSubjectArea.AA );
            
            home.setWillGiveUpLock( true );

            home.setPropertyValue( TestSubjectArea.A_X, OLD_VALUE );

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

            if( theChangeSet != null ) {
                throw new IllegalStateException( "Have a ChangeSet already: " + theChangeSet );
            }

            theChangeSet = updateSet;
        }
    }
}
