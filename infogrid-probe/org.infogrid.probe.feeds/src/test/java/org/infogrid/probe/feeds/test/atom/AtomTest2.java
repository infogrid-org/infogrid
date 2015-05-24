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

package org.infogrid.probe.feeds.test.atom;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSelector;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.model.Feeds.FeedsSubjectArea;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.Probe.ProbeSubjectArea;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.feeds.atom.AtomProbe;
import org.infogrid.probe.feeds.test.AbstractFeedTest;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests the Atom Probe with InfoGrid extensions with AtomTest2.xml.
 */
@RunWith(Parameterized.class)
public class AtomTest2
        extends
            AbstractFeedTest
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
                 { "src/test/resources/org/infogrid/probe/feeds/test/atom/AtomTest2.xml", 3 }
        });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may go wrong during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing test file with meshBase" );
        
        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor(theTestFileId, CoherenceSpecification.ONE_TIME_ONLY );

        checkObject( meshBase1, "could not find meshBase1" );
        checkCondition( meshBase1.size() > 1, "meshBase1 is empty" );
        dumpMeshBase( meshBase1, "meshBase1", log );

        MeshObject home1 = meshBase1.getHomeObject();

        checkEqualsOutOfSequence(
                home1.getTypes(),
                new EntityType[] {
                        FeedsSubjectArea.ATOMFEED,
                        TestSubjectArea.C,
                        ProbeSubjectArea.ONETIMEONLYPROBEUPDATESPECIFICATION
                },
                "home object has wrong type" );

        checkEquals( home1.traverseToNeighborMeshObjects().size(), 2, "wrong number of neighbors for home object" );
        MeshObject def = home1.traverseToNeighborMeshObjects().find( new MeshObjectSelector() {
                public boolean accepts(
                        MeshObject candidate )
                {
                    return candidate.getIdentifier().toExternalForm().endsWith( "def" );
                }
        });
        checkObject( def, "no def object found" );
        checkEquals( def.traverseToNeighborMeshObjects().size(), 2, "wrong number of neighbors for def object" );
        
        checkEqualsOutOfSequence(
                def.getTypes(),
                new EntityType[] {
                    FeedsSubjectArea.ATOMFEEDITEM,
                    TestSubjectArea.AA,
                    TestSubjectArea.B
                },
                "home object has wrong type" );
        
        checkEquals( home1.getRoleTypes( def ).length, 1, "Relationship between home and def is unexpectedly more blessed than plain Atom" );
        
        MeshObject abc = def.traverse( TestSubjectArea.RR.getSource() ).getSingleMember();
        checkObject( abc, "no def object found" );
        checkEquals( abc.traverseToNeighborMeshObjects().size(), 2, "wrong number of neighbors for abc object" );
        
        checkEqualsOutOfSequence(
                abc.getTypes(),
                new EntityType[] {
                    FeedsSubjectArea.ATOMFEEDITEM,
                    TestSubjectArea.B
                },
                "home object has wrong type" );
        
        checkEquals( abc.getRoleTypes( def ).length, 1, "Relationship between abc and def is not blessed" );
        
        checkEquals( abc.getPropertyValue( TestSubjectArea.B_Z ), TestSubjectArea.B_Z_type_VALUE3, "wrong property value" );

        checkEquals( meshBase1.size(), theExpectedNumberMeshObjects, "Wrong number of MeshObjects found" );
    }

    /**
     * Constructor that takes parameters.
     * 
     * @param fileName the XRD file to read
     * @param expectedNumberMeshObjects the number of MeshObjects in the file
     * @throws Exception all sorts of things may happen during a test
     */
    public AtomTest2(
            String fileName,
            int    expectedNumberMeshObjects )
        throws
            Exception
    {
        theTestFileId = theMeshBaseIdentifierFactory.obtain( new File( fileName ));

        theExpectedNumberMeshObjects = expectedNumberMeshObjects;
    }

    /**
     * Setup.
     * 
     * @throws Exception all sorts of things may happen during a test
     */
    @Before
    @Override
    public void setup()
        throws
            Exception
    {
        super.setup();

        theProbeDirectory.addXmlDomProbe( new ProbeDirectory.XmlDomProbeDescriptor( null, "http://www.w3.org/2005/Atom", "feed", AtomProbe.class ));
    }

    /**
     * The expected number of MeshObjects in the test file.
     */
    protected int theExpectedNumberMeshObjects;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier theTestFileId;

    // Our Logger
    private static Log log = Log.getLogInstance( AtomTest2.class);
}
