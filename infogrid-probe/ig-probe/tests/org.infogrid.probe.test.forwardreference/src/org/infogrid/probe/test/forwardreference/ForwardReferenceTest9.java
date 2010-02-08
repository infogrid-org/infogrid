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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.forwardreference;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
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
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.module.ModuleException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.StagingMeshBaseLifecycleManager;
import org.infogrid.util.logging.Log;

/**
 * Tests resolving ForwardReferences in API Probes that only show up on the second run.
 */
public class ForwardReferenceTest9
        extends
            AbstractForwardReferenceTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things can go wrong during a test
     */
    public void run()
        throws
            Exception
    {
        theInstantiateForwardReference = false;

        log.info( "accessing outer probe (1)" );

        NetMeshObject home1 = base.accessLocally( OUTER_URL, theCoherence );

        checkObject( home1, "home1 not found" );
        checkEquals( home1.traverseToNeighborMeshObjects().size(), 0, "has neighbors already" );

        //

        theInstantiateForwardReference = true;

        log.info( "accessing outer probe (2)" );

        home1.getProxyTowardsLockReplica().setCoherenceSpecification( theCoherence.withWaitForOngoingResynchronization( true ));

        base.freshenNow( new NetMeshObject[] { home1 } ); // this must wait, otherwise the test will fail.

        checkEquals( home1.traverseToNeighborMeshObjects().size(), 1, "no neighbors" );

        NetMeshObject fwdRef = (NetMeshObject) home1.traverseToNeighborMeshObjects().getSingleMember();

        //

        log.info( "Checking that ForwardReference resolved" );

        checkEquals( fwdRef.getPropertyValue( TestSubjectArea.A_X ), StringValue.create( "resolved" ), "not resolved" );

        sleepFor( 2000L );
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        ForwardReferenceTest9 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: \"fast\"|\"slow\"" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new ForwardReferenceTest9( args );
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
     * @param args command-line arguments
     * @throws Exception all sorts of things can go wrong during a test
     */
    public ForwardReferenceTest9(
            String [] args )
        throws
            Exception
    {
        super( ForwardReferenceTest9.class, args[0] );

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                OUTER_URL.toExternalForm(),
                OuterTestApiProbe.class ));

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                INNER_URL.toExternalForm(),
                InnerTestApiProbe.class ));
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ForwardReferenceTest9.class);

    /**
     * URL for the outer Probe.
     */
    public static final NetMeshBaseIdentifier OUTER_URL;
    static {
        NetMeshBaseIdentifier temp = null;
        try {
            temp = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://some.example.com/outer" );
            // temp = NetMeshBaseIdentifier.create( "=foo" );

        } catch( ParseException ex ) {
            log.error( ex );
        }
        OUTER_URL = temp;
    }

    /**
     * URL for the inner Probe.
     */
    public static final NetMeshBaseIdentifier INNER_URL;
    static {
        NetMeshBaseIdentifier temp = null;
        try {
            temp = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://some.example.com/inner" );

        } catch( ParseException ex ) {
            log.error( ex );
        }
        INNER_URL = temp;
    }

    /**
     * Flag that tells the OUTER probe to instantiate the ForwardReference.
     */
    protected static boolean theInstantiateForwardReference;

    /**
     * The Probe to the outer data feed.
     */
    public static class OuterTestApiProbe
            implements
                ApiProbe
    {
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
            MeshObject home = mb.getHomeObject();
            home.bless( TestSubjectArea.AA );

            if( theInstantiateForwardReference ) {
                StagingMeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();

                MeshObject fwdRef = life.createForwardReference( INNER_URL, TestSubjectArea.A );
                fwdRef.setPropertyValue( TestSubjectArea.A_X, StringValue.create( "forwardreference" ));

                home.relateAndBless( TestSubjectArea.AR1A.getSource(), fwdRef );
            }
        }
    }

    /**
     * The Probe to the inner data feed.
     */
    public static class InnerTestApiProbe
            implements
                ApiProbe
    {
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
            MeshObject home = mb.getHomeObject();

            home.bless( TestSubjectArea.AA );
            home.bless( TestSubjectArea.B );

            home.setPropertyValue( TestSubjectArea.A_X, StringValue.create( "resolved" ) );
        }
    }
}

