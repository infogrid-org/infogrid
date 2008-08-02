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

package org.infogrid.kernel.net.TEST.xpriso;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.m.NetMMeshBase;
import org.infogrid.meshbase.net.proxy.NiceAndTrustingProxyPolicyFactory;
import org.infogrid.meshbase.net.proxy.ProxyPolicyFactory;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.util.logging.Log;

/**
 * Tests multi-hop access to MeshObjects with NetMeshBases that point replicas to themselves.
 * Compare XprisoTest2a.
 */
public class XprisoTest2b
    extends
        AbstractXprisoTest
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

        obj1_mb1.relate( obj2_mb1 );

        tx1.commitTransaction();

        checkProxies( obj1_mb1, null, null, null, "obj1_mb1 has proxies" );
        checkProxies( obj2_mb1, null, null, null, "obj2_mb1 has proxies" );

        //

        log.info( "Accessing obj2 at mb3 via mb2 from mb1" );

        NetMeshObject obj2_mb3 = mb3.accessLocally(
                NetMeshObjectAccessSpecification.create(
                        new NetMeshBaseIdentifier[] {
                                mb2.getIdentifier(),
                                mb1.getIdentifier(),
                        },
                        obj2_mb1.getIdentifier() ));
        checkObject( obj2_mb3, "mb3 fails to access obj2." );

        Thread.sleep( 12000L );

        log.info( "Finding obj2_mb2" );

        NetMeshObject obj2_mb2 = mb2.findMeshObjectByIdentifier( obj2_mb1.getIdentifier() );
        
        checkProxies( obj2_mb1, new NetMeshBase[] { mb2 },      null, null, "obj2_mb1 has wrong proxies" );
        checkProxies( obj2_mb2, new NetMeshBase[] { mb1, mb3 }, mb1,  mb1,  "obj2_mb2 has wrong proxies" );
        checkProxies( obj2_mb3, new NetMeshBase[] { mb2 },      mb2,  mb2,  "obj2_mb3 has wrong proxies" );
    }

    /*
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        XprisoTest2b test = null;
        try {
            if( args.length != 0 ) {
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new XprisoTest2b( args );
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
     * @throws Exception all kinds of things can go wrong in tests
     */
    public XprisoTest2b(
            String [] args )
        throws
            Exception
    {
        super( XprisoTest2b.class );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( theNameServer );

        ProxyPolicyFactory proxyPolicyFactory = NiceAndTrustingProxyPolicyFactory.create( true );
        
        mb1 = NetMMeshBase.create( net1, endpointFactory, proxyPolicyFactory, theModelBase, null, rootContext );
        mb2 = NetMMeshBase.create( net2, endpointFactory, proxyPolicyFactory, theModelBase, null, rootContext );
        mb3 = NetMMeshBase.create( net3, endpointFactory, proxyPolicyFactory, theModelBase, null, rootContext );

        theNameServer.put( mb1.getIdentifier(), mb1 );
        theNameServer.put( mb2.getIdentifier(), mb2 );
        theNameServer.put( mb3.getIdentifier(), mb3 );
    }

    /**
     * Clean up after the test.
     */
    @Override
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
    protected NetMeshBaseIdentifier net1 = NetMeshBaseIdentifier.createUnresolvable( "one.local" );

    /**
     * The second NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net2 = NetMeshBaseIdentifier.createUnresolvable( "two.local" );

    /**
     * The third NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier net3 = NetMeshBaseIdentifier.createUnresolvable( "three.local" );

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
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 3 ); // I think we need three
    
    // Our Logger
    private static Log log = Log.getLogInstance( XprisoTest2b.class );
}