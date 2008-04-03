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

package org.infogrid.probe.TEST;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.transaction.Transaction;
// import org.infogrid.model.Blob.BlobSubjectArea;
import org.infogrid.probe.ProbeDirectory.StreamProbeDescriptor;
import org.infogrid.probe.blob.BlobProbe;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.util.logging.Log;


/**
 * Probes an HTML document with an X-XRDS-Location header, then relates this to a local
 * object.
 */
public class ShadowTest3
        extends
            AbstractProbeTest
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
        log.info( "Setting up" );
        
        MProbeDirectory theProbeDirectory = MProbeDirectory.create();
        theProbeDirectory.addStreamProbe( new StreamProbeDescriptor( "text/html", BlobProbe.class ));

        NetMeshBaseIdentifier here = NetMeshBaseIdentifier.create( "http://here.local/" ); // this is not going to work for communications
        LocalNetMMeshBase     base = LocalNetMMeshBase.create( here, theModelBase, null, exec, theProbeDirectory, rootContext );

        //
        
        log.info( "accessing #abc of test file with NetworkedMeshBase" );
        
        MeshObject home = base.accessLocally( testFile1Id );

        checkObject( home, "home not found" );
//        checkCondition( home.isBlessedBy( BlobSubjectArea.BLOB_OBJECT ), "Not blessed as a Blob" );
        
        //

        log.info( "relating to other object" );
        
        Transaction tx = base.createTransactionNow();
        
        MeshObject other = base.getMeshBaseLifecycleManager().createMeshObject();
        
        tx.commitTransaction();

        
        tx = base.createTransactionNow();
        
        other.relate( home );
        
        tx.commitTransaction();
        
        //
        
        log.info( "Traversing from other object to Yadis services" );
        
        MeshObjectSet found = other.traverseToNeighborMeshObjects().traverseToNeighborMeshObjects();
        
        checkEquals( found.size(), 10, "Wrong number of objects found" ); // that's the 9 Yadis services and other itself
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        ShadowTest3 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new ShadowTest3( args );
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
      * constructor
      */
    public ShadowTest3(
            String [] args )
        throws
            Exception
    {
        super( ShadowTest3.class );

        testFile1 = args[0];

        testFile1Id = NetMeshBaseIdentifier.create( new File( testFile1 ) );
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        exec.shutdown();
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest3.class);

    /**
     * File name of the first test file.
     */
    protected String testFile1;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );
}
