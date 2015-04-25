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

package org.infogrid.comm.pingpong.test;

import java.util.List;
import org.infogrid.comm.MessageEndpointIsDeadException;
import org.infogrid.comm.MessageSendException;
import org.infogrid.comm.ReturnSynchronizerEndpoint;
import org.infogrid.comm.pingpong.m.MPingPongMessageEndpoint;
import org.infogrid.util.ReturnSynchronizer;
import org.infogrid.util.logging.Log;
import org.junit.Assert;

/**
 * Tests several RPC calls in parallel.
 */
public class PingPongReturnSynchronizerTest2
        extends
            AbstractPingPongRpcTest
{
    /**
     * Test run.
     *
     * @throws Throwable this code may throw any Exception
     */
    public void run()
            throws
                Throwable
    {
        int MAX = 100;

        ReturnSynchronizer<Long,TestMessage> synchronizer = ReturnSynchronizer.create();
        TestMessage [] msgToSend = new TestMessage[ MAX ];
        @SuppressWarnings("unchecked")
        ReturnSynchronizerEndpoint<TestMessage> [] clients = new ReturnSynchronizerEndpoint[ MAX ];

        synchronizer.beginTransaction();
        
        for( int i=2 ; i<MAX ; ++i ) {
            MPingPongMessageEndpoint<TestMessage> ep1 = new MPingPongMessageEndpoint<TestMessage>( "ep1-" + i, 1000L, 1000L, 500L, 10000L, 0.f, exec ) {
                    /**
                     * Invoked when the timer triggers.
                     *
                     * @param task the TimedTask that invokes this handler
                     */
                    @Override
                    protected synchronized void doAction(
                            TimedTask task )
                    {
                        // overridden so breakpoints can be set
                        if( theMessagesToBeSent != null && ! theMessagesToBeSent.isEmpty() ) {
                            super.doAction( task );
                        } else {
                            super.doAction( task );
                        }
                    }
                    /**
                     * Invoked by subclasses to provide the content of a received message.
                     *
                     * @param token the integer representing the token
                     * @param content the content of a received message
                     * @throws MessageEndpointIsDeadException thrown if the MessageEndpoint is dead
                     * @throws MessageSendException thrown if the message could not be sent
                     */
                    @Override
                    protected void incomingMessage(
                            long              token,
                            List<TestMessage> content )
                        throws
                            MessageEndpointIsDeadException,
                            MessageSendException
                    {
                        if( content != null && content.size() > 0 ) {
                            super.incomingMessage( token, content );
                        } else {
                            super.incomingMessage( token, content );
                        }
                    }
            };
            MPingPongMessageEndpoint<TestMessage> ep2 = new MPingPongMessageEndpoint<TestMessage>( "ep2-" + i, 1000L, 1000L, 500L, 10000L, 0.f, exec ) {
                    /**
                     * Invoked when the timer triggers.
                     *
                     * @param task the TimedTask that invokes this handler
                     */
                    @Override
                    protected synchronized void doAction(
                            TimedTask task )
                    {
                        // overridden so breakpoints can be set
                        if( theMessagesToBeSent != null && ! theMessagesToBeSent.isEmpty() ) {
                            super.doAction( task );
                        } else {
                            super.doAction( task );
                        }
                    }
                    /**
                     * Invoked by subclasses to provide the content of a received message.
                     *
                     * @param token the integer representing the token
                     * @param content the content of a received message
                     * @throws MessageEndpointIsDeadException thrown if the MessageEndpoint is dead
                     * @throws MessageSendException thrown if the message could not be sent
                     */
                    @Override
                    protected void incomingMessage(
                            long              token,
                            List<TestMessage> content )
                        throws
                            MessageEndpointIsDeadException,
                            MessageSendException
                    {
                        if( content != null && content.size() > 0 ) {
                            super.incomingMessage( token, content );
                        } else {
                            super.incomingMessage( token, content );
                        }
                    }
            };

            MultiplyingResponder l2 = new MultiplyingResponder( ep2, this );
            ep2.addDirectMessageEndpointListener( l2 );

            clients[i] = ReturnSynchronizerEndpoint.create( synchronizer, ep1 );

            ep1.setPartnerAndInitiateCommunications( ep2 );

            msgToSend[i] = new TestMessage( i );

            clients[i].call( msgToSend[i] );
        }

        log.debug( "joining" );
        synchronizer.join();

        //
        
        log.debug( "checking results" );
        for( int i=2 ; i<MAX ; ++i ) {
            TestMessage msgReceived = synchronizer.getResultFor( msgToSend[i].getRequestId() );

            long result = msgReceived.getPayload();

            Assert.assertEquals( "wrong result for i=" + i, result, i*i );
        }

        synchronizer.endTransaction();
    }

    /**
     * Cleanup.
     */
    @Override
    public void cleanup()
    {
        done = true;

        exec.shutdown();
    }

    // Our Logger
    private static Log log = Log.getLogInstance( PingPongReturnSynchronizerTest2.class );
}
