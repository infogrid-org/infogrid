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
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.m.NetMMeshBase;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests moving the homeReplica around.
 *
 * FIXME: This seems a misdescription. Or perhaps the test does not test what it is supposed to?
 */
public class XprisoTest14
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
        NetMeshBaseLifecycleManager life1 = mb1.getMeshBaseLifecycleManager();
        mb1.setDefaultWillGiveUpLock( true );

        //
        
        log.info( "Instantiating object in mb1" );

        Transaction tx1 = mb1.createTransactionAsap();

        NetMeshObject obj_mb1 = life1.createMeshObject( TestSubjectArea.AA );

        tx1.commitTransaction();

        checkCondition( obj_mb1.getAllProxies() == null, "obj_mb1 has proxies" );

        //

        log.info( "Accessing object at mb2 from mb1" );

        NetMeshObject obj_mb2 = mb2.accessLocally(
                    mb1.getIdentifier(),
                    obj_mb1.getIdentifier() );

        checkObject( obj_mb2, "object not found in mb2" );

        checkProxies( obj_mb1, new NetMeshBase[] { mb2 }, null, null, "obj_mb1 has wrong proxies" );
        checkProxies( obj_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj_mb2 has wrong proxies" );

        //

        log.info( "Checking where the lock is" );
        
        checkCondition( obj_mb1.hasLock(),  "obj_mb1 does not have the lock" );
        checkCondition( !obj_mb2.hasLock(), "obj_mb2 has the lock" );

        //
        
        log.info( "Moving the lock" );
        
        boolean success = obj_mb2.tryToObtainLock();
        
        checkCondition( success, "Obtaining the lock failed" );
        checkCondition( !obj_mb1.hasLock(),  "obj_mb1 has the lock" );
        checkCondition( obj_mb2.hasLock(),  "obj_mb2 does not have the lock" );

        checkProxies( obj_mb1, new NetMeshBase[] { mb2 }, null, mb2,  "obj_mb1 has wrong proxies" );
        checkProxies( obj_mb2, new NetMeshBase[] { mb1 }, mb1,  null, "obj_mb2 has wrong proxies" );

        success = obj_mb1.tryToObtainLock();
        
        checkCondition( success, "Obtaining the lock failed" );
        checkCondition( obj_mb1.hasLock(),  "obj_mb1 does not have the lock" );
        checkCondition( !obj_mb2.hasLock(), "obj_mb2 has the lock" );
        
        checkProxies( obj_mb1, new NetMeshBase[] { mb2 }, null, null, "obj_mb1 has wrong proxies" );
        checkProxies( obj_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj_mb2 has wrong proxies" );

        //
        
        log.info( "Setting willGiveUpLock=false and trying again" );

        obj_mb1.setWillGiveUpLock( false );
        success = obj_mb2.tryToObtainLock();
        
        checkCondition( !success, "Obtaining the lock should have failed" );
        checkCondition( obj_mb1.hasLock(),  "obj_mb1 does not have the lock" );
        checkCondition( !obj_mb2.hasLock(), "obj_mb2 has the lock" );

        checkProxies( obj_mb1, new NetMeshBase[] { mb2 }, null, null, "obj_mb1 has wrong proxies" );
        checkProxies( obj_mb2, new NetMeshBase[] { mb1 }, mb1,  mb1,  "obj_mb2 has wrong proxies" );
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

        mb1 = NetMMeshBase.create( net1, theModelBase, null, endpointFactory, rootContext );
        mb2 = NetMMeshBase.create( net2, theModelBase, null, endpointFactory, rootContext );

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
    protected ScheduledExecutorService exec = createThreadPool( 2 ); // I think we need three

    // Our Logger
    private static Log log = Log.getLogInstance( XprisoTest14.class );
}
