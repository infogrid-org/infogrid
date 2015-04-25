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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.comm.MessageEndpoint;
import org.infogrid.comm.MessageEndpointIsDeadException;
import org.infogrid.comm.MessageEndpointListener;
import org.infogrid.comm.MessageSendException;
import org.infogrid.comm.ReceivingMessageEndpoint;
import org.infogrid.comm.SendingMessageEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpoint;
import org.infogrid.comm.pingpong.m.MPingPongMessageEndpoint;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that messages that could not be delivered are re-sent.
 */
public class PingPongTest5
        extends
            AbstractPingPongTest
{
    @Test
    public void run()
            throws
                Exception
    {
        MPingPongMessageEndpoint<String> ep1 = MPingPongMessageEndpoint.create( "ep1", 1000L, 1000L, 500L, 5000L, 0.f, exec );
        MPingPongMessageEndpoint<String> ep2 = new FailingMessageEndpoint(      "ep2", 1000L, 1000L, 500L, 5000L, 0.f, exec );
        
        MyListener l1 = new MyListener( ep1, 'A' );
        MyListener l2 = new MyListener( ep2, '0' );
        ep1.addDirectMessageEndpointListener( l1 );
        ep2.addDirectMessageEndpointListener( l2 );

        log.info( "Starting to ping-pong" );
        log.debug( "Note that the events seem to be a bit out of order as we only print the event after it was successfully sent (and received)" );

        ep1.setPartnerAndInitiateCommunications( ep2 );
        
        ep2.enqueueMessageForSend( "(seed)" );
        
        Thread.sleep( 10000L ); // four ping and five pongs

        log.info( "Stopping communicating" );
        
        ep1.stopCommunicating();
        ep2.stopCommunicating();
        
        String lastMessage1 = l1.lastMessageReceived;
        String lastMessage2 = l2.lastMessageReceived;
        
        Assert.assertEquals( lastMessage1, "3 D 2 C 1 B 0 A (seed)" );
        Assert.assertEquals( lastMessage2, "D 2 C 1 B 0 A (seed)" );
    }

    @Before
    public void setup() 
    {
        exec = createThreadPool( 1 );
    }

    @After
    public void cleanup()
    {
        done = true;

        exec.shutdown();        
    }

    // Our Logger
    private static Log log = Log.getLogInstance( PingPongTest5.class );

    /**
     * Our ThreadPool
     */
    protected ScheduledExecutorService exec;
        
    /**
     * Set to true if the test is done, so listeners won't report an error.
     */
    protected boolean done = false;
    
    /**
     * Listener.
     */
    class MyListener
            implements
                MessageEndpointListener<String>
    {
        public MyListener(
                PingPongMessageEndpoint<String> end,
                char                            prefix )
        {
            theEndpoint = end;
            thePrefix   = prefix;
        }

        /**
         * Called when one more more incoming messages have arrived.
         *
         * @param endpoint the MessageEndpoint that sent this event
         * @param msgs the received messages
         */
        public void messageReceived(
                ReceivingMessageEndpoint<String> endpoint,
                List<String>                     msgs )
        {
            log.traceMethodCallEntry( this, "messageReceived", msgs );
            Assert.assertEquals( msgs.size(), 1 );

            lastMessageReceived = msgs.get( 0 );
            theEndpoint.enqueueMessageForSend( thePrefix + " " + lastMessageReceived );
            ++thePrefix;
        }

        /**
         * Called when an outgoing message has been sent.
         *
         * @param endpoint the MessageEndpoint that sent this event
         * @param msg the sent message
         */
        public void messageSent(
                SendingMessageEndpoint<String> endpoint,
                String                         msg )
        {
            log.traceMethodCallEntry( this, "messageSent", msg );
        }

        /**
         * Called when an outgoing message has enqueued for sending.
         *
         * @param endpoint the MessageEndpoint that sent this event
         * @param msg the enqueued message
         */
        public void messageEnqueued(
                SendingMessageEndpoint<String> endpoint,
                String                         msg )
        {
            log.traceMethodCallEntry( this, "messageEnqueued", msg );
        }
    
        /**
         * Called when an outoing message failed to be sent.
         *
         * @param endpoint the MessageEndpoint that sent this event
         * @param msg the outgoing message
         */
        public void messageSendingFailed(
                SendingMessageEndpoint<String> endpoint,
                String                         msg )
        {
            // do not report an error here in this test
        }

        /**
         * Called when the receiving endpoint threw the EndpointIsDeadException.
         *
         * @param endpoint the MessageEndpoint that sent this event
         * @param msg the status of the outgoing queue
         * @param t the error
         */
        public void disablingError(
                MessageEndpoint<String> endpoint,
                List<String>            msg,
                Throwable               t )
        {
            if( !done ) {
                Assert.fail( "Receiving endpoint is dead: " + msg );
            }
        }

        public void clear()
        {
            received = 0;
            sent     = 0;
        }

        PingPongMessageEndpoint<String> theEndpoint;
        char                            thePrefix;
        String                          lastMessageReceived;
        
        int received;
        int sent;
    }
    
    /**
     * Occasionally failing MessageEndpoint for testing.
     */
    static class FailingMessageEndpoint
            extends
                MPingPongMessageEndpoint<String>
    {
        public FailingMessageEndpoint(
                String                   name,
                long                     deltaRespondNoMessage,
                long                     deltaRespondWithMessage,
                long                     deltaResend,
                long                     deltaRecover,
                double                   randomVariation,
                ScheduledExecutorService exec )
        {
            super(  name,
                    deltaRespondNoMessage,
                    deltaRespondWithMessage,
                    deltaResend,
                    deltaRecover,
                    randomVariation,
                    exec,
                    -1,
                    -1,
                    null,
                    new ArrayList<String>() );
        }
        
        /**
         * Insert error.
         * 
         * @param token the integer representing the token
         * @param content the content of a received message
         * @throws MessageEndpointIsDeadException thrown if the MessageEndpoint is dead
         * @throws MessageSendException thrown if message delivery failed
         */
        @Override
        protected void incomingMessage(
                long         token,
                List<String> content )
            throws
                MessageEndpointIsDeadException,
                MessageSendException
        {
            if( !hasThrownError && token == 3 ) {
                hasThrownError = true;
                throw new MessageSendException( content, "error inserted by " + getClass().getName() );
            }
            super.incomingMessage( token, content );
        }
        
        protected boolean hasThrownError = false;
    }
}
