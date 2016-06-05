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
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.m.NetMMeshBase;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests when a remote object cannot be accessed because it does not exist.
 * This tests only one level of access. Compare XprisoTest1b.
 */
public class XprisoTest1a
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
        NetMeshObjectIdentifier identifier = mb2.getMeshObjectIdentifierFactory().fromExternalForm( "Missing object" );
        
        log.info( "Accessing missing object" );

        NetMeshObject missing_mb2 = mb2.accessLocally(
                mb1.getIdentifier(),
                identifier );

        checkCondition( missing_mb2 == null, "Object was unexpectedly found" );
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
    protected NetMeshBase mb1;

    /**
     * The second NetMeshBase.
     */
    protected NetMeshBase mb2;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 1 );

    // Our Logger
    private static Log log = Log.getLogInstance( XprisoTest1a.class );
}
