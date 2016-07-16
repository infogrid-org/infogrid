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
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that we can create relationships between
 * MeshObjects which are held by other MeshBases, and the correct propagation.
 * More complex version of XprisoTest7.
 */
public class XprisoTest7_5
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
        obj1_mb1.setWillGiveUpHomeReplica( false );
        obj2_mb1.setWillGiveUpLock( false );
        obj2_mb1.setWillGiveUpHomeReplica( false );

        tx1.commitTransaction();

        //

        log.debug( "Checking proxies (1)" );

        checkProxies( obj1_mb1, null, null, null, "obj1_mb1 has proxies" );
        checkProxies( obj2_mb1, null, null, null, "obj2_mb1 has proxies" );

        //

        log.info( "Accessing obj1 at mb2 from mb1" );

        NetMeshObject obj1_mb2 = mb2.accessLocally(
                mb1.getIdentifier(),
                obj1_mb1.getIdentifier() );
        checkObject( obj1_mb2, "mb2 fails to access obj1." );

        log.debug( "Checking proxies (2)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj2_mb1, null,                      null, null, "obj2_mb1 has proxies" );

        //

        log.info( "Accessing obj1 at mb3 from mb1" );

        NetMeshObject obj1_mb3 = mb3.accessLocally(
                mb1.getIdentifier(),
                obj1_mb1.getIdentifier() );
        checkObject( obj1_mb3, "mb3 fails to access obj1." );

        log.debug( "Checking proxies (3)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2, mb3 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 },      mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb1 },      mb1,  mb1,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj2_mb1, null,                           null, null, "obj2_mb1 has proxies" );

        //

        log.info( "Accessing obj2 at mb3 via mb2 from mb1" );

        NetMeshObject obj2_mb3 = mb3.accessLocally(
                mb3.getNetMeshObjectAccessSpecificationFactory().obtain(
                        new NetMeshBaseIdentifier[] {
                                mb2.getIdentifier(),
                                mb1.getIdentifier(),
                        },
                        obj2_mb1.getIdentifier() ));
        checkObject( obj2_mb3, "mb3 fails to access obj2." );
        NetMeshObject obj2_mb2 = mb2.findMeshObjectByIdentifier( obj2_mb1.getIdentifier() );
        checkObject( obj2_mb2, "mb2 does not have obj2." );

        log.debug( "Checking proxies (4)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2, mb3 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1 },      mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb1 },      mb1,  mb1,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 },      null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb3 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb2 },      mb2,  mb2,  "obj2_mb3 has wrong proxies" );

        //

        log.info( "Accessing obj1 at mb4 from mb2" );

        NetMeshObject obj1_mb4 = mb4.accessLocally(
                mb2.getIdentifier(),
                obj1_mb1.getIdentifier() );
        checkObject( obj1_mb4, "mb4 fails to access obj1." );

        log.debug( "Checking proxies (5)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2, mb3 }, null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1, mb4 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb1 },      mb1,  mb1,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj1_mb4, new NetMeshBase[] { mb2 },      mb2,  mb2,  "obj1_mb4 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 },      null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb3 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb2 },      mb2,  mb2,  "obj2_mb3 has wrong proxies" );

        //


        log.info( "Accessing obj2 at mb4 from mb2" );

        NetMeshObject obj2_mb4 = mb4.accessLocally(
                mb2.getIdentifier(),
                obj2_mb1.getIdentifier() );
        checkObject( obj2_mb4, "mb4 fails to access obj1." );

        log.debug( "Checking proxies (6)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2, mb3 },      null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1, mb4 },      mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb1 },           mb1,  mb1,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj1_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj1_mb4 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 },           null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb3, mb4 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj2_mb3 has wrong proxies" );
        checkProxies( obj2_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj2_mb4 has wrong proxies" );

        //

        log.info( "Creating relationship between obj1 and obj2 in mb2." );

        Transaction tx2 = mb2.createTransactionAsap();
        obj1_mb2.relateAndBless( TestSubjectArea.AR1A.getSource(), obj2_mb2 );

        tx2.commitTransaction();

        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION * 5L );

        log.debug( "Checking proxies (7)" );

        // For better or worse, forwarding the relationship from mb2 to mb3 causes:
        // 1. obj1 in mb3 to cancel the lease to mb1 and create one with mb2.
        // 2. obj2 in mb3 to cancel the lease to mb2 and create one with mb1
        // This is as designed. Whether the design is right is a different question ;-)

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 },           null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1, mb3, mb4 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj1_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj1_mb4 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2, mb3 },      null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb4 },      mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb1 },           mb1,  mb1,  "obj2_mb3 has wrong proxies" );
        checkProxies( obj2_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj2_mb4 has wrong proxies" );

        checkRelationshipProxies( obj1_mb1, obj2_mb1, new NetMeshBase[] { mb2 },      "obj1_mb1-obj2_mb1 has wrong relationship proxies" );
        // The following two are commented out, they seem to be non-deterministic. (FIXME?)
        // checkRelationshipProxies( obj1_mb2, obj2_mb2, null,                           "obj1_mb2-obj2_mb2 has wrong relationship proxies" );
        // checkRelationshipProxies( obj1_mb3, obj2_mb3, new NetMeshBase[] { mb1, mb2 }, "obj1_mb3-obj2_mb3 has wrong relationship proxies" );
        checkRelationshipProxies( obj1_mb4, obj2_mb4, new NetMeshBase[] { mb2 },      "obj1_mb4-obj2_mb4 has wrong relationship proxies" );

        //

        log.info( "Checking mb2 relationship." );

        MeshObjectSet neighbors1_mb2 = obj1_mb2.traverseToNeighborMeshObjects();
        MeshObjectSet rsReplica_mb2  = obj1_mb2.traverse( TestSubjectArea.AR1A.getSource() );

        checkEquals( neighbors1_mb2.size(), 1, "obj1 in mb2 has wrong number of neighbors" );
        checkEquals( rsReplica_mb2.size(),  1, "obj1 in mb2 has wrong number of relationships" );

        //

        log.info( "Checking mb1 relationship." );

        MeshObjectSet neighbors1_mb1 = obj1_mb1.traverseToNeighborMeshObjects();
        MeshObjectSet rsReplica_mb1  = obj1_mb1.traverse( TestSubjectArea.AR1A.getSource() );

        checkEquals( neighbors1_mb1.size(), 1, "obj1 in mb1 has wrong number of neighbors" );
        checkEquals( rsReplica_mb1.size(),  1, "obj1 in mb1 has wrong number of relationships" );

        //

        log.info( "Checking mb3 relationship." );

        // now do it the other way round
        MeshObjectSet rsReplica_mb3  = obj1_mb3.traverse( TestSubjectArea.AR1A.getSource() );
        MeshObjectSet neighbors1_mb3 = obj1_mb3.traverseToNeighborMeshObjects();

        checkEquals( rsReplica_mb3.size(),  1, "obj1 in mb3 has wrong number of relationships" );
        checkEquals( neighbors1_mb3.size(), 1, "obj1 in mb3 has wrong number of neighbors" );

        //

        log.info( "Checking mb4 relationship." );

        MeshObjectSet rsReplica_mb4  = obj1_mb4.traverse( TestSubjectArea.AR1A.getSource() );
        MeshObjectSet neighbors1_mb4 = obj1_mb4.traverseToNeighborMeshObjects();

        checkEquals( rsReplica_mb4.size(),  1, "obj1 in mb4 has wrong number of relationships" );
        checkEquals( neighbors1_mb4.size(), 1, "obj1 in mb4 has wrong number of neighbors" );

        //

        log.debug( "Checking proxies (8)" );

        checkProxies( obj1_mb1, new NetMeshBase[] { mb2 },           null, null, "obj1_mb1 has wrong proxies" );
        checkProxies( obj1_mb2, new NetMeshBase[] { mb1, mb3, mb4 }, mb1,  mb1,  "obj1_mb2 has wrong proxies" );
        checkProxies( obj1_mb3, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj1_mb3 has wrong proxies" );
        checkProxies( obj1_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj1_mb4 has wrong proxies" );
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2, mb3 },      null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb4 },      mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb1 },           mb1,  mb1,  "obj2_mb3 has wrong proxies" );
        checkProxies( obj2_mb4, new NetMeshBase[] { mb2 },           mb2,  mb2,  "obj2_mb4 has wrong proxies" );
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
        net4 = theMeshBaseIdentifierFactory.fromExternalForm( "test://four.local" );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( theNameServer );

        ProxyPolicyFactory proxyPolicyFactory = NiceAndTrustingProxyPolicyFactory.create( true );

        mb1 = NetMMeshBase.create( net1, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb2 = NetMMeshBase.create( net2, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb3 = NetMMeshBase.create( net3, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );
        mb4 = NetMMeshBase.create( net4, theModelBase, null, endpointFactory, proxyPolicyFactory, rootContext );

        theNameServer.put( mb1.getIdentifier(), mb1 );
        theNameServer.put( mb2.getIdentifier(), mb2 );
        theNameServer.put( mb3.getIdentifier(), mb3 );
        theNameServer.put( mb4.getIdentifier(), mb4 );
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
        mb4.die();

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
     * The fourth NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net4;

    /**
     * The first NetMeshBase.
     */
    protected NetMMeshBase mb1;

    /**
     * The second NetMeshBase.
     */
    protected NetMMeshBase mb2;

    /**
     * The third NetMeshBase.
     */
    protected NetMMeshBase mb3;

    /**
     * The fourth NetMeshBase.
     */
    protected NetMMeshBase mb4;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 3 ); // I think we need three

    // Our Logger
    private static final Log log = Log.getLogInstance( XprisoTest7_5.class );
}
