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

package org.infogrid.kernel.net.test.xpriso;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.m.NetMMeshBase;
import org.infogrid.meshbase.net.proxy.NiceAndTrustingProxyPolicyFactory;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that a "fast" CoherenceSpecification access does not block accessLocally until the
 * resynchronization is done.
 */
public class XprisoTest12
    extends
        AbstractXprisoTest
{
    /**
     * Run the test.
     *
     * @throws Exception all kinds of things can go wrong in tests
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "Instantiating objects in mb1" );

        Transaction tx1 = mb1.createTransactionAsap();

        NetMeshBaseLifecycleManager life1 = mb1.getMeshBaseLifecycleManager();

        NetMeshObject obj1_mb1 = life1.createMeshObject( TestSubjectArea.AA );

        tx1.commitTransaction();

        //

        log.info( "Accessing replica at mb2 from mb1" );

        NetMeshObject obj1_mb2 = mb2.accessLocally(
                mb2.getNetMeshObjectAccessSpecificationFactory().obtain(
                        mb1.getIdentifier(),
                        obj1_mb1.getIdentifier(),
                        CoherenceSpecification.ONE_TIME_ONLY_FAST ),
                        600000L ); // give us enough time for debugging
        
        checkObject( obj1_mb2, "obj1_mb2 not found" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 }, null, null,  "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 },  mb1,  mb1,  "obj1_mb2 has wrong proxies" );

        //

        log.info( "Accessing replica at mb3 from mb2" );

        NetMeshObject obj1_mb3 = mb3.accessLocally(
                mb3.getNetMeshObjectAccessSpecificationFactory().obtain(
                        mb2.getIdentifier(),
                        obj1_mb1.getIdentifier(),
                        CoherenceSpecification.ONE_TIME_ONLY_FAST ));
        checkObject( obj1_mb3, "C not found" );

        //

        log.info( "Immediate check section" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 },      null, null,  "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1, mb3 },  mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb2 },       mb2,  mb2,  "obj1_mb3 has wrong proxies" );

        //

        log.info( "Now wait and check again later" );

        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION * 4L ); // make sure background resync works -- we used a "fast" CoherenceSpecification

        // slow section
        checkProxies( obj1_mb1, new NetMeshBase[] { mb2, mb3 }, null, null,  "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 },       mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb1 },       mb1,  mb1,  "obj1_mb3 has wrong proxies" );
    }

    /**
     * Setup.
     *
     * @throws Exception all kinds of things can go wrong in tests
     */
    @Before
    @Override
    public void setup()
        throws
            Exception
    {
        super.setup();
        
        net1 = theMeshBaseIdentifierFactory.fromExternalForm( "test://one.local" );
        net2 = theMeshBaseIdentifierFactory.fromExternalForm( "test://two.local" );
        net3 = theMeshBaseIdentifierFactory.fromExternalForm( "test://three.local" );
        
        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( theNameServer );

        NiceAndTrustingProxyPolicyFactory proxyPolicyFactory = NiceAndTrustingProxyPolicyFactory.create( false );

        mb1 = NetMMeshBase.create( net1, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb2 = NetMMeshBase.create( net2, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb3 = NetMMeshBase.create( net3, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );

        theNameServer.put( mb1.getIdentifier(), mb1 );
        theNameServer.put( mb2.getIdentifier(), mb2 );
        theNameServer.put( mb3.getIdentifier(), mb3 );

        if( log.isDebugEnabled() ) {
            mb1.setXprisoMessageLogger( theXprisoMessageLogger );
            mb2.setXprisoMessageLogger( theXprisoMessageLogger );
            mb3.setXprisoMessageLogger( theXprisoMessageLogger );
        }
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        mb1.die();
        mb2.die();
        mb3.die();
        
        exec.shutdown();
    }

    /**
     * The first NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net1;

    /**
     * The second NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net2;

    /**
     * The third NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net3;

    /**
     * The first NetMeshBase.
     */
    protected NetMeshBase mb1;

    /**
     * The second NetMeshBase.
     */
    protected NetMeshBase mb2;

    /**
     * The third NetMeshBase.
     */
    protected NetMeshBase mb3;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 3 ); // gotta have two threads

    // Our Logger
    private static Log log = Log.getLogInstance( XprisoTest12.class );
}
