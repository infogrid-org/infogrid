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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.kernel.net.test.xpriso;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.m.NetMMeshBase;
import org.infogrid.meshbase.net.proxy.NiceAndTrustingProxyPolicyFactory;
import org.infogrid.meshbase.net.proxy.ProxyPolicyFactory;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that newly created relationships propagate correctly.
 * Here the created relationship is between non-home replicas.
 */
public class XprisoTest6_5b
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

        NetMeshObject obj1_mb1 = life1.createMeshObject(
                mb1.getMeshObjectIdentifierFactory().fromExternalForm( "obj1" ),
                TestSubjectArea.AA );
        obj1_mb1.setPropertyValue( TestSubjectArea.A_X, StringValue.create( "This is a obj1." ));

        NetMeshObject obj2_mb1 = life1.createMeshObject(
                mb1.getMeshObjectIdentifierFactory().fromExternalForm( "obj2" ),
                TestSubjectArea.AA );
        obj2_mb1.setPropertyValue( TestSubjectArea.A_X, StringValue.create( "This is a obj2." ));

        obj1_mb1.setWillGiveUpLock( false );
        obj2_mb1.setWillGiveUpLock( false );

        tx1.commitTransaction();

        log.debug( "Checking proxies (1)" );

        checkProxies( obj1_mb1, null, null, null, "obj1_mb1 has proxies" );
        checkProxies( obj2_mb1, null, null, null, "obj2_mb1 has proxies" );

        //

        log.info( "Accessing objects from mb2" );

        NetMeshObject obj1_mb2 = mb2.accessLocally(
                mb1.getIdentifier(),
                obj1_mb1.getIdentifier() );
        checkObject( obj1_mb2, "mb2 fails to access obj1." );

        NetMeshObject obj2_mb2 = mb2.accessLocally(
                mb1.getIdentifier(),
                obj2_mb1.getIdentifier() );
        checkObject( obj2_mb2, "mb2 fails to access obj2." );

        log.debug( "Checking proxies (2)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 }, null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );

        checkNotObject( obj1_mb1.getAllRelationshipProxies(), "unexpectedly found relationship proxies in obj1_mb1" );
        checkNotObject( obj2_mb1.getAllRelationshipProxies(), "unexpectedly found relationship proxies in obj2_mb1" );
        checkNotObject( obj1_mb2.getAllRelationshipProxies(), "unexpectedly found relationship proxies in obj1_mb2" );
        checkNotObject( obj2_mb2.getAllRelationshipProxies(), "unexpectedly found relationship proxies in obj2_mb2" );

        //

        log.info( "Creating relationship between obj1 and obj2 in mb2." );

        Transaction tx2 = mb2.createTransactionAsap();
        obj1_mb2.relateAndBless( TestSubjectArea.AR1A.getSource(), obj2_mb2 );

        tx2.commitTransaction();

        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );

        //

        log.debug( "Checking proxies (3)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 }, null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );

        checkRelationshipProxies( obj1_mb1, obj2_mb1.getIdentifier(), new NetMeshBase[] { mb2 }, "obj1-obj2 has wrong relationship proxies in mb1" );
        checkRelationshipProxies( obj2_mb1, obj1_mb1.getIdentifier(), new NetMeshBase[] { mb2 }, "obj2-obj1 has wrong relationship proxies in mb1" );
        checkRelationshipProxies( obj1_mb2, obj2_mb2.getIdentifier(), null, "obj1-obj2 has wrong relationship proxies in mb2" );
        checkRelationshipProxies( obj2_mb2, obj1_mb2.getIdentifier(), null, "obj2-obj1 has wrong relationship proxies in mb2" );

        //

        log.info( "Checking mb2 relationship." );

        MeshObjectSet neighbors1_mb2 = obj1_mb2.traverseToNeighborMeshObjects();
        MeshObjectSet related1_mb2   = obj1_mb2.traverse( TestSubjectArea.AR1A.getSource());
        MeshObjectSet neighbors2_mb2 = obj2_mb2.traverseToNeighborMeshObjects();
        MeshObjectSet related2_mb2   = obj2_mb2.traverse( TestSubjectArea.AR1A.getDestination());

        checkEquals( neighbors1_mb2.size(), 1, "obj1 in mb2 has wrong number of neighbors" );
        checkEquals( related1_mb2.size(),   1, "obj1 in mb2 has wrong number of relationships" );
        checkEquals( neighbors2_mb2.size(), 1, "obj2 in mb2 has wrong number of neighbors" );
        checkEquals( related2_mb2.size(),   1, "obj2 in mb2 has wrong number of relationships" );

        //

        log.info( "Checking mb1 relationship." );

        MeshObjectSet neighbors1_mb1 = obj1_mb1.traverseToNeighborMeshObjects();
        MeshObjectSet related1_mb1   = obj1_mb1.traverse( TestSubjectArea.AR1A.getSource());
        MeshObjectSet neighbors2_mb1 = obj2_mb1.traverseToNeighborMeshObjects();
        MeshObjectSet related2_mb1   = obj2_mb1.traverse( TestSubjectArea.AR1A.getDestination());

        checkEquals( neighbors1_mb1.size(), 1, "obj1 in mb1 has wrong number of neighbors" );
        checkEquals( related1_mb1.size(),   1, "obj1 in mb1 has wrong number of relationships" );
        checkEquals( neighbors2_mb1.size(), 1, "obj2 in mb1 has wrong number of neighbors" );
        checkEquals( related2_mb1.size(),   1, "obj2 in mb1 has wrong number of relationships" );
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

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( theNameServer );

        ProxyPolicyFactory proxyPolicyFactory = NiceAndTrustingProxyPolicyFactory.create( true );

        mb1 = NetMMeshBase.create( net1, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb2 = NetMMeshBase.create( net2, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );

        theNameServer.put( mb1.getIdentifier(), mb1 );
        theNameServer.put( mb2.getIdentifier(), mb2 );
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        mb1.die();
        mb2.die();

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
     * The first NetMeshBase.
     */
    protected NetMMeshBase mb1;

    /**
     * The second NetMeshBase.
     */
    protected NetMMeshBase mb2;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 3 ); // I think we need three

    // Our Logger
    private static final Log log = Log.getLogInstance( XprisoTest6_5b.class  );
}
