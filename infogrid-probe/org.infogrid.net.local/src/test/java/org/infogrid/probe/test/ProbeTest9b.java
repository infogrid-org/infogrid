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

import java.io.IOException;
import java.net.URISyntaxException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.module.ModuleException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.util.instrument.InstrumentedThread;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentationParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests concurrent access to the same Probe that fails.
 */
public class ProbeTest9b
    extends
        AbstractProbeTest9
{
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
        probeInvocationCounter = 0;

        //


        log.info( "Creating threads" );

        Runnable [] runnables = new Runnable[] {
                new MyRunnable( test1_URL ) {
                    protected void recordResult(
                            MeshObject result )
                    {
                        foundObject1 = result;
                    }
                    protected void recordException(
                            Throwable t )
                    {
                        throwable1 = t;
                    }
                },
                new MyRunnable( test1_URL ) {
                    protected void recordResult(
                            MeshObject result )
                    {
                        foundObject2 = result;
                    }
                    protected void recordException(
                            Throwable t )
                    {
                        throwable2 = t;
                    }
                },
        };

        InstrumentedThread [] threads = createThreads( runnables, new String[] {
                    "one",
                    "two"
                } );

        checkNotObject( foundObject1, "unexpected object 1" );
        checkNotObject( foundObject2, "unexpected object 2" );
        checkNotObject( throwable1,   "exception 1 too early" );
        checkNotObject( throwable2,   "exception 2 too early" );

        sleepFor( 100L );

        //

        log.info( "Advancing first thread" );

        threads[0].advanceTo( location1 );
        checkNotObject( foundObject1, "unexpected object 1" );
        checkNotObject( foundObject2, "unexpected object 2" );
        checkNotObject( throwable1,   "exception 1 too early" );
        checkNotObject( throwable2,   "exception 2 too early" );

        super.sleepFor( PINGPONG_ROUNDTRIP_DURATION );

        log.info( "Advancing second thread" );

        boolean successful = threads[1].advanceTo( location1, 2000L );
        checkCondition( !successful, "thread2 should not have been able to advance to breakpoint" );
        checkNotObject( foundObject1, "unexpected object 1" );
        checkNotObject( foundObject2, "unexpected object 2" );
        checkNotObject( throwable1,   "exception 1 too early" );
        checkNotObject( throwable2,   "exception 2 too early" );

        //

        log.info( "second try for thread2" );

        successful = threads[1].advanceTo( location1, 2000L );
        checkCondition( !successful, "thread2 should not have been able to advance to breakpoint" );

        checkNotObject( foundObject1, "unexpected object 1" );
        checkNotObject( foundObject2, "unexpected object 2" );
        checkNotObject( throwable1,   "exception 1 too early" );
        checkNotObject( throwable2,   "exception 2 too early" );

        //

        log.info( "Completing threads" );

        threads[0].completeThreadAndWait();
        threads[1].completeThreadAndWait();

        Thread.sleep( 2000L ); // give it a little so threads can finish

        checkNotObject( foundObject1, "unexpected object 1" );
        checkNotObject( foundObject2, "unexpected object 2" );
        checkObject( throwable1,   "no exception 1" );
        checkObject( throwable2,   "no exception 2" ); // FIXME: This should have thrown an exception, but currently does not

        checkEquals( probeInvocationCounter, 1, "invoked wrong number of times" );

        //

        log.info( "Make sure we have no Shadow" );

        checkEquals( theMeshBase.getShadowMeshBases().size(),         0, "wrong number of shadows" );
        checkEquals( countFromIterator( theMeshBase.proxies(), log ), 0, "wrong number of proxies" );
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
        super.setup( ProbeTest9b.TestApiProbeB.class );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ProbeTest9b.class );

    /**
     * The test Probe superclass.
     */
    public static class TestApiProbeB
        implements
            ApiProbe
    {
        public void readFromApi(
                NetMeshBaseIdentifier  networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        mb )
            throws
                MeshObjectIdentifierNotUniqueException,
                RelatedAlreadyException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                ModuleException,
                URISyntaxException,
                StringRepresentationParseException
        {
            log.debug( getClass().getName() + ".readFromApi()" );

            ++probeInvocationCounter;

            try {
                location1.reached();

            } catch( InterruptedException ex ) {
                throw new ProbeException.Other( networkId, ex );
            }

            throw new IOException( "The problem occurred as predicted" );
        }
    }
}