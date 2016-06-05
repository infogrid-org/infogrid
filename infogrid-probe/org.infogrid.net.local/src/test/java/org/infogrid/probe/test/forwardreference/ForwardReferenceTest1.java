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

package org.infogrid.probe.test.forwardreference;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.testharness.util.IteratorElementCounter;
import org.infogrid.util.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests resolving ForwardReferences in external files.
 */
@RunWith(Parameterized.class)
public class ForwardReferenceTest1
        extends
            AbstractForwardReferenceTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][]
            {
                {
                    true,
                    AbstractTest.fileSystemFileName( ForwardReferenceTest1.class, "ForwardReferenceTest1_1.xml" )
                    // ForwardReferenceRest1_2.xml included by reference from ForwardReferenceRest1.xml

                },
                {
                    false,
                    AbstractTest.fileSystemFileName( ForwardReferenceTest1.class, "ForwardReferenceTest1_1.xml" )
                    // ForwardReferenceRest1_2.xml included by reference from ForwardReferenceRest1.xml
                }
            });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things can go wrong during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing test file 1" );
        
        MeshObject abc = base.accessLocally( testFile1Id, theCoherence );

        checkObject( abc, "abc not found" );

        checkEquals( IteratorElementCounter.countIteratorElements( base.proxies()), 1, "wrong number of proxies in main NetMeshBase" );
        // if wait, this is the proxy to the old Shadow, if !wait, the proxy to the new Shadow

        //
        
        log.info( "Finding ForwardReference" );

        NetMeshObject fwdReference = (NetMeshObject) abc.traverseToNeighborMeshObjects().getSingleMember();
        checkObject( fwdReference, "fwdReference not found" );

        if( theWait ) {
            checkCondition(  fwdReference.isBlessedBy( TestSubjectArea.A, false ), "Not blessed by right type" );
            checkCondition( !fwdReference.isBlessedBy( TestSubjectArea.B, false ), "Blessed by wrong type" );
            checkEquals( fwdReference.getPropertyValue( TestSubjectArea.A_X ), StringValue.create( "forwardreference" ), "wrong property value" );
            checkEquals( fwdReference.getNeighborMeshObjectIdentifiers().length, 1, "wrong number of neighbors" );

            // wait some
        
            Thread.sleep( PINGPONG_ROUNDTRIP_DURATION*3L );
        }

        checkEquals( fwdReference.getPropertyValue( TestSubjectArea.A_X ), StringValue.create( "resolved" ), "ForwardReference was not successfully resolved: " + fwdReference.getIdentifier().toExternalForm() );

        checkEquals(    fwdReference.getAllProxies().length, 1, "Wrong number of proxies on forward reference" );
        checkCondition( fwdReference.getAllProxies()[0].getPartnerMeshBaseIdentifier().toExternalForm().endsWith( "ForwardReferenceTest1_2.xml" ), "Wrong proxy on forward reference" );
        checkCondition( !fwdReference.isBlessedBy( TestSubjectArea.A,  false ), "Blessed still by old type" );
        checkCondition(  fwdReference.isBlessedBy( TestSubjectArea.AA, false ), "Not blessed by the right type (AA)" );
        checkCondition(  fwdReference.isBlessedBy( TestSubjectArea.B,  false ), "Not blessed by the right type (B)" );
        checkEquals( fwdReference.getNeighborMeshObjectIdentifiers().length, 2, "wrong number of neighbors" );
    }

    /**
     * Constructor with parameters.
     * 
     * @param wait if true, wait for the Probe
     * @param testFile1 the test file
     * @throws Exception all sorts of things can go wrong during a test
     */
    public ForwardReferenceTest1(
            boolean wait,
            String  testFile1 )
        throws
            Exception
    {
        super( wait );

        testFile1Id = theMeshBaseIdentifierFactory.obtain( new File( testFile1 ) );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ForwardReferenceTest1.class);

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;
}
