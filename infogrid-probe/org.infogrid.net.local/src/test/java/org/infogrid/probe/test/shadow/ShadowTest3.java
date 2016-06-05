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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.Blob.BlobSubjectArea;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.probe.ProbeDirectory.StreamProbeDescriptor;
import org.infogrid.probe.blob.BlobProbe;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;

/**
 * Probes an HTML document with an X-XRDS-Location header, then relates this to a local
 * object.
 * FIXME: need to move from file: to http: otherwise WebResource not set
 */
// @RunWith(Parameterized.class)
public class ShadowTest3
        extends
            AbstractShadowTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    // @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][] {
                {
                    AbstractTest.fileSystemFileName( ShadowTest1.class, "ShadowTest3a.html.xml" )
                         // ShadowTest3b.html referenced from ShadowTest3a.html
                } });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    // @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing #abc of test file with NetworkedMeshBase" );
        
        MeshObject home = base.accessLocally( testFile1Id );

        checkObject( home, "home not found" );
        checkCondition( home.isBlessedBy( BlobSubjectArea.BLOBOBJECT ), "Not blessed as a Blob" );
        
        //

        log.info( "Creating some other objects" );
        
        Transaction tx = base.createTransactionNow();

        MeshObject other1 = base.getMeshBaseLifecycleManager().createMeshObject( base.getMeshObjectIdentifierFactory().fromExternalForm( "other1" ));
        MeshObject other2 = base.getMeshBaseLifecycleManager().createMeshObject( base.getMeshObjectIdentifierFactory().fromExternalForm( "other2" ));
        
        tx.commitTransaction();

        //
        
        log.info( "Incorrectly relating to other object" );

        try {
            tx = base.createTransactionNow();
        
            other1.relateAndBless( TestSubjectArea.RR.getSource(), home ); // source and dest entities are invalid for this relationship
            
            reportError( "Should have thrown an Exception" );
            
        } catch( EntityNotBlessedException ex ) {
            log.debug( "Corrently thrown exception", ex );

        } finally {
            tx.commitTransaction();
        }
        //
        
        log.info( "correctly relating to other object" );
        
        tx = base.createTransactionNow();
        
        other2.relate( home );
        
        tx.commitTransaction();
        
        //
        
        log.info( "Traversing from other object to Yadis services" );
        
        MeshObjectSet found1 = other2.traverseToNeighborMeshObjects();
        MeshObjectSet found2 = found1.traverseToNeighborMeshObjects();

        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION * 3L ); // allow ForwardReference resolution to work

        MeshObjectSet found3 = found2.traverseToNeighborMeshObjects();
        
        checkEquals( found3.size(), 10, "Wrong number of objects found" ); // that's the 9 Yadis services, plus home
    }

    /**
     * Constructor with parameters.
     * 
     * @param testFile1 the test file
     * @throws Exception all sorts of things can go wrong during a test
     */
    public ShadowTest3(
            String testFile1 )
        throws
            Exception
    {
        testFile1Id = theMeshBaseIdentifierFactory.obtain( new File( testFile1 ) );
    }

    /**
     * Setup.
     * 
     * @throws Exception all sorts of things may go wrong in tests
     */
    // @Before
    @Override
    public void setup()
        throws
            Exception
    {
        super.setup();

        theProbeDirectory.addStreamProbe( new StreamProbeDescriptor( "text/html", BlobProbe.class ));
    }


    // Our Logger
    private static Log log = Log.getLogInstance( ShadowTest3.class);

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;
}
