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

package org.infogrid.probe.vcard.test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.model.VCard.VCardSubjectArea;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
  * Tests the VCard Probe. FIXME: This is not a very complete test at all.
  */
@RunWith(Parameterized.class)
public class VCardProbeTest1
    extends
        AbstractVCardProbeTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][] {
                 { "src/test/resources/org/infogrid/probe/vcard/test/VCardProbeTest1.vcf", 6 }
                                                                    // 1 VCard
                                                                    // 1 PhysicalAddress
                                                                    // 3 Phone numbers
        });
    }

    /**
     * Run the test.
     * 
     * @throws Exception all kinds of things may go wrong during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing test file" );
        
        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor(theTestFileId, CoherenceSpecification.ONE_TIME_ONLY );

        checkObject( meshBase1, "could not find meshBase1" );
        checkCondition( meshBase1.size() > 1, "meshBase1 is empty" );

        MeshObject home = meshBase1.getHomeObject();
        checkCondition( home.isBlessedBy( VCardSubjectArea.VCARD ), "Not blessed with a VCard" );
        
        checkEquals( meshBase1.size(), theExpectedNumberMeshObjects, "Wrong number of objects found" );
    }

    /**
     * Constructor that takes parameters.
     * 
     * @param fileName the XRD file to read
     * @param expectedNumberMeshObjects the number of MeshObjects in the file
     * @throws Exception all sorts of things may happen during a test
     */
    public VCardProbeTest1(
            String fileName,
            int    expectedNumberMeshObjects )
        throws
            Exception
    {
        theTestFileId = theMeshBaseIdentifierFactory.obtain( new File( fileName ));

        theExpectedNumberMeshObjects = expectedNumberMeshObjects;
    }

    /**
     * File name of the first test file.
     */
    protected String testFile1;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier theTestFileId;

    /**
     * The expected number of MeshObjects in the test file.
     */
    protected int theExpectedNumberMeshObjects;

    // Our Logger
    private static Log log = Log.getLogInstance( VCardProbeTest1.class );
}
