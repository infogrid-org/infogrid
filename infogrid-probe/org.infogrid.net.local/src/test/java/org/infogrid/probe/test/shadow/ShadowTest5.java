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
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;

/**
 * Relates a ShadowMeshBase-producted MeshObject A to another MeshObject B outside of the ShadowMeshBase,
 * and makes sure that the relationship goes away without error if B disappears on the
 * next Probe run. This uses unblessed MeshObjects (compare with ShadowTest6 and
 * ShadowTest7).
 */
public class ShadowTest5
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
        NetMeshObjectIdentifier aId     = base.getMeshObjectIdentifierFactory().fromExternalForm( test_URL, "a" );
        NetMeshObjectIdentifier localId = base.getMeshObjectIdentifierFactory().fromExternalForm( "local" );

        //
        
        log.info( "Creating local object" );
        
        Transaction tx = base.createTransactionAsap();
        
        NetMeshObject local = base.getMeshBaseLifecycleManager().createMeshObject( localId );
        
        tx.commitTransaction();

        checkProxies( local, null, null, null, "local has proxies" );

        //
        
        log.info( "Accessing probe first time" );

        NetMeshObject a1 = base.accessLocally( test_URL, aId, CoherenceSpecification.ONE_TIME_ONLY );

        checkObject( a1, "a1 not there" );
        
        ShadowMeshBase shadow = base.getShadowMeshBaseFor( test_URL );
        checkEquals( shadow.size(), 2, "Wrong number of objects in Shadow" );
        checkProxies( a1, new NetMeshBase[] { shadow }, shadow, shadow, "local has proxies" );

        //
        
        log.info( "Relating objects and checking" );
        
        tx = base.createTransactionAsap();
        
        local.relate( a1 );

        tx.commitTransaction();
        
        checkEquals( local.traverseToNeighborMeshObjects().size(), 1, "neighbor of local not found" );
        checkEquals(    a1.traverseToNeighborMeshObjects().size(), 2, "neighbor of a1 not found" );
        checkEquals( shadow.size(), 2, "Wrong number of objects in Shadow" );
        checkProxies( a1, new NetMeshBase[] { shadow }, shadow, shadow, "local has proxies" );
        checkProxies( local, null, null, null, "local has proxies" );
        
        //
        
        log.info( "Running Probe again" );
        
        base.getShadowMeshBaseFor( test_URL ).doUpdateNow();
        
        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );
        
        //
        
        log.info( "Checking" );
        
        checkEquals( shadow.size(), 1, "Wrong number of objects in Shadow" );
        checkEquals( local.traverseToNeighborMeshObjects().size(), 0, "unexpected neighbor of local found" );
        checkCondition( a1.getIsDead(), "a1 still alive" );
        checkProxies( local, null, null, null, "local has proxies" );        
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
                        test_URL.toExternalForm(),
                        TestApiProbe.class ));
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest5.class );

    /**
     * The URL that we are accessing.
     */
    private static NetMeshBaseIdentifier test_URL;

    static {
        try {
            test_URL = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://shadow.local/remainder" );

        } catch( Exception ex ) {
            log.error( ex );
            
            test_URL = null; // make compiler happy
        }
    }

    /**
     * Counts the number of Probe runs.
     */
    protected static int theProbeRunCounter = 0;

    /**
     * The test Probe.
     */
    public static class TestApiProbe
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
            ++theProbeRunCounter;
            
            MeshBaseLifecycleManager life = mb.getMeshBaseLifecycleManager();

            MeshObject home = mb.getHomeObject();
            
            if( theProbeRunCounter == 1 ) {

                MeshObject a = life.createMeshObject( mb.getMeshObjectIdentifierFactory().fromExternalForm( "a" ) );
                home.relate( a );
            }
        }
    }
}
