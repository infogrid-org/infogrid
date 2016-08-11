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
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.WritableProbe;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that a relationships created by two MeshObjects created by a Probe cannot be removed
 * in the Shadow by propagation, unless the Probe is writeable.
 */
public class ShadowTest11a
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
        log.info( "Accessing non-writable Probe" );

        final MeshObject nonWritableHome     = base.accessLocally( nonwritable_URL, CoherenceSpecification.ONE_TIME_ONLY, 3600000L );
        final MeshObject nonWritableNeighbor = nonWritableHome.traverseToNeighborMeshObjects().getSingleMember();

        checkObject( nonWritableNeighbor, "no neighbor" );

        //

        log.info( "Deleting relationship and checking it's unrelated immediately after" );

        base.executeNow( (Transaction tx) -> {
                nonWritableHome.unrelate( nonWritableNeighbor );
                return null;
        });
        checkCondition( !nonWritableHome.isRelated( nonWritableNeighbor), "Still related" );

        //

        log.info( "Waiting to propagate, should now we related again" );
                // this should not require a subsequent Probe run

        sleepFor( PINGPONG_ROUNDTRIP_DURATION );

        checkCondition( nonWritableHome.isRelated( nonWritableNeighbor), "Not related" );

        //

        log.info( "Checking that Xpriso queues are empty" );

        Proxy baseProxy = base.getProxyFor( nonwritable_URL );
        checkEquals( baseProxy.getMessageEndpoint().messagesToBeSent().size(), 0, "Messages still to be sent from main MeshBase" );

        Proxy shadowProxy = base.getShadowMeshBaseFor( nonwritable_URL ).getProxyFor( base.getIdentifier() );
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
                        nonwritable_URL.toExternalForm(),
                        NonwritableTestApiProbe.class ));
        theProbeDirectory.addExactUrlMatch(
                new ProbeDirectory.ExactMatchDescriptor(
                        writable_URL.toExternalForm(),
                        WritableTestApiProbe.class ));
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( ShadowTest11a.class );

    /**
     * The URLs that we are accessing.
     */
    private static NetMeshBaseIdentifier nonwritable_URL;
    private static NetMeshBaseIdentifier writable_URL;

    static {
        try {
            nonwritable_URL = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://myhost.local/nonwritable" );
            writable_URL    = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://myhost.local/writable" );

        } catch( Exception ex ) {
            log.error( ex );

            nonwritable_URL = null; // make compiler happy
            writable_URL    = null; // make compiler happy
        }
    }

    /**
     * The test Probe, not writable.
     */
    public static class NonwritableTestApiProbe
            implements
                ApiProbe
    {
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
                URISyntaxException,
                ParseException
        {
            MeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();
            MeshObject               home = mb.getHomeObject();

            MeshObject a = life.createMeshObject( mb.getMeshObjectIdentifierFactory().fromExternalForm( "a" ) );
            home.relate( a );
        }
    }

    /**
     * The non-writable test Probe.
     */
    public static class WritableTestApiProbe
            extends
                NonwritableTestApiProbe
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
