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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.shadow;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.WritableProbe;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that a relationships created by two MeshObjects created by a Probe can be removed
 * if the Probe is writable.
 */
public class ShadowTest11b
        extends
            AbstractShadowTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "Accessing writable Probe" );

        final MeshObject writableHome     = base.accessLocally( writable_URL, CoherenceSpecification.ONE_TIME_ONLY, 3600000L );
        final MeshObject writableNeighbor = writableHome.traverseToNeighborMeshObjects().getSingleMember();

        checkObject( writableNeighbor, "no neighbor" );

        //

        log.info( "Deleting relationship and checking it's unrelated immediately after" );

        base.executeNow( (Transaction tx ) -> {
                writableHome.unrelate( writableNeighbor );
                return null;
        });
        checkCondition( !writableHome.isRelated( writableNeighbor), "Still related" );

        //

        log.info( "Waiting to propagate, should still be unrelated" );
                // this should not require a subsequent Probe run

        sleepFor( PINGPONG_ROUNDTRIP_DURATION );

        checkCondition( !writableHome.isRelated( writableNeighbor), "Related again" );


        //

        log.info( "Checking that Xpriso queues are empty" );

        Proxy baseProxy = base.getProxyFor( writable_URL );
        checkEquals( baseProxy.getMessageEndpoint().messagesToBeSent().size(), 0, "Messages still to be sent from main MeshBase" );

        Proxy shadowProxy = base.getShadowMeshBaseFor( writable_URL ).getProxyFor( base.getIdentifier() );
        checkEquals( shadowProxy.getMessageEndpoint().messagesToBeSent().size(), 0, "Messages still to be sent from ShadowMeshBase" );

        // just to make sure we don't have a timing problem
        checkEquals( baseProxy.getMessageEndpoint().messagesToBeSent().size(), 0, "Messages still to be sent from main MeshBase" );
        checkEquals( shadowProxy.getMessageEndpoint().messagesToBeSent().size(), 0, "Messages still to be sent from ShadowMeshBase" );
    }

    /**
     * Setup.
     *
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Before
    @Override
    public void setup()
        throws
            Exception
    {
        super.setup();

        theProbeDirectory.addExactUrlMatch(
                new ProbeDirectory.ExactMatchDescriptor(
                        writable_URL.toExternalForm(),
                        WritableTestApiProbe.class ));
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( ShadowTest11b.class );

    /**
     * The URLs that we are accessing.
     */
    private static NetMeshBaseIdentifier writable_URL;

    static {
        try {
            writable_URL    = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://myhost.local/writable" );

        } catch( Exception ex ) {
            log.error( ex );

            writable_URL = null; // make compiler happy
        }
    }

    /**
     * The non-writable test Probe.
     */
    public static class WritableTestApiProbe
            extends
                ShadowTest11a.NonwritableTestApiProbe
            implements
                WritableProbe
    {
        public void write(
                NetMeshBaseIdentifier dataSourceIdentifier,
                ChangeSet             updateSet,
                StagingMeshBase       previousMeshBaseWithUpdates )
        {
            // do nothing
        }
    }

}
