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

package org.infogrid.probe.test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectBreadthFirstIterator;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.probe.m.MProbeDirectory;
import static org.infogrid.probe.test.AbstractProbeTest.theMeshBaseIdentifierFactory;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
  * Tests that all MeshObjects from the same Probe have the same timeCreated and timeUpdated values.
  */
@RunWith(Parameterized.class)
public class ProbeTest4
        extends
            AbstractProbeTest
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
                {
                    AbstractTest.fileSystemFileName( ProbeTest4.class, "ProbeTest4.xml" )
                }
        });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing " + theTestUrl );

        MeshObject top = theMeshBase.accessLocally( theTestUrl );
        checkObject( top, "no object from top" );
        reportOn( "top", top );

        long topCreated = top.getTimeCreated();
        long topUpdated = top.getTimeUpdated();
        
        //

        log.info( "finding all objects -- some multiple times" );

        StringBuilder buf = new StringBuilder();
        
        MeshObjectBreadthFirstIterator iter =  MeshObjectBreadthFirstIterator.create( top, 100 );
        for( int i=0 ; iter.hasNext() ; ++i ) {
            MeshObject current = iter.next();

            log.debug( "Found object: " + current );
            
            StringValue value = (StringValue) current.getPropertyValue( TestSubjectArea.A_X );
            if( value != null ) {
                buf.append( value.getAsString() );
            }

            reportOn( String.valueOf( i ), current );

            checkEquals( topCreated, current.getTimeCreated(), "create time different for object " + current );
            checkEquals( topUpdated, current.getTimeUpdated(), "update time different for object " + current );
        }
        
        checkEquals( buf.toString(), "homeabcdefghi", "wrong constructed value from properties" );
    }

    /**
     * Helper method to report on one found object.
     * @param prefix prefix to print
     * @param obj the MeshObject we report on
     */
    protected void reportOn(
            String     prefix,
            MeshObject obj )
    {
        if( log.isInfoEnabled() ) {
            StringBuilder line = new StringBuilder( 64 );
            if( prefix != null ) {
                line.append( prefix ).append( ": " );
            }
            line.append( obj.getIdentifier() );
            line.append( ", created: " );
            line.append( obj.getTimeCreated() );
            line.append( ", updated: " );
            line.append( obj.getTimeUpdated() );

            log.info( line );
        }
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile test file
     * @throws Exception all sorts of things may happen during a test
     */
    public ProbeTest4(
            String testFile )
        throws
            Exception
    {
        theTestUrl = theMeshBaseIdentifierFactory.obtain( new File( testFile ) );
    }
    
    /**
     * Setup.
     * 
     * @throws Exception all sorts of things may happen during a test
     */
    @Before
    public void setup()
        throws
            Exception
    {
        theProbeDirectory = MProbeDirectory.create();
        exec = createThreadPool( 1 );


        NetMeshBaseIdentifier here = theMeshBaseIdentifierFactory.fromExternalForm( "http://here.local/" ); // this is not going to work for communications

        theMeshBase = LocalNetMMeshBase.create( here, theModelBase, null, theProbeDirectory, exec, rootContext );
    }
    
    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        if( theMeshBase != null ) {
            theMeshBase.die();
            theMeshBase = null;
        }

        exec.shutdown();
        exec = null;
    }

    /**
     * The test URL that we access
     */
    protected NetMeshBaseIdentifier theTestUrl;
    
    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory;

    /**
     * The MeshBase to be tested.
     */
    protected LocalNetMMeshBase theMeshBase;

    // Our Logger
    private static Log log = Log.getLogInstance( ProbeTest4.class);
}