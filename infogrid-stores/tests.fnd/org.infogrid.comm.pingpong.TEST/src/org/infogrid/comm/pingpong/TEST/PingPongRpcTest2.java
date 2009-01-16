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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.comm.pingpong.TEST;

import org.infogrid.comm.BidirectionalMessageEndpoint;
import org.infogrid.comm.ReceivingMessageEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpoint;
import org.infogrid.comm.pingpong.m.MPingPongMessageEndpoint;

import org.infogrid.util.logging.Log;

/**
 * Tests remote procedure call (RPC) functionality where the servant responds
 * only on the second message back.
 */
public class PingPongRpcTest2
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
        MPingPongMessageEndpoint<PingPongRpcTestMessage> ep1 = MPingPongMessageEndpoint.create( "ep1", 1000L, 1000L, 500L, 10000L, 0.f, exec );
        MPingPongMessageEndpoint<PingPongRpcTestMessage> ep2 = MPingPongMessageEndpoint.create( "ep2", 1000L, 1000L, 500L, 10000L, 0.f, exec );
        
        MyListener l2 = new MyListener( ep2 );
        ep2.addDirectMessageEndpointListener( l2 );

        PingPongRpcClientEndpoint client = new PingPongRpcClientEndpoint( ep1 );
        
        log.info( "Starting to ping-pong" );
        log.debug( "Note that the events seem to be a bit out of order as we only print the event after it was successfully sent (and received)" );

        ep1.setPartnerAndInitiateCommunications( ep2 );

        for( long i=2 ; i<10 ; ++i ) {

            log.debug( "About to invoke RPC for " + i );
            long ret = client.invoke( i );

            checkEquals( ret, i*i, "wrong result for i=" + i );
        }
    }

    /**
      * Main program.
      *
      * @param args command-line arguments
      */
    public static void main(
             String [] args )
    {
        PingPongRpcTest2 test = null;
        try {
            if( args.length != 0 ) {
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }
            test = new PingPongRpcTest2( args );
            test.run();

        } catch( Throwable ex ) {
            log.error( ex );
            ++errorCount;
        }
        if( test != null ) {
            test.cleanup();
        }

        if( errorCount == 0 ) {
            log.info( "PASS" );
        } else {
            log.error( "FAIL (" + errorCount + " errors)" );
        }

        System.exit( errorCount );
    }

    /**
     * Setup.
     *
     * @param args not used
     * @throws Exception any kind of exception
     */
    public PingPongRpcTest2(
            String [] args )
        throws
            Exception
    {
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
    private static Log log = Log.getLogInstance( PingPongRpcTest2.class );

    /**
     * Listener that responds only on the second message.
     */
    class MyListener
            extends
                AbstractPingPongRpcListener
    {
        /**
         * Constructor.
         * 
         * @param end the endpoint to which this listener listenes
         */
        public MyListener(
                BidirectionalMessageEndpoint<PingPongRpcTestMessage> end )
        {
            super( end );
        }

        /**
         * Called when an incoming message has arrived.
         *
         * @param endpoint the PingPongMessageEndpoint that sent this event
         * @param msg the received message
         */
        public void messageReceived(
                ReceivingMessageEndpoint<PingPongRpcTestMessage> endpoint,
                final PingPongRpcTestMessage                     msg )
        {
            if( theOldMessage != null ) {
                long ret = theOldMessage.getPayload();

                log.debug( "Received message, calculating return for old message " + ret );

                ret = ret * ret;

                PingPongRpcTestMessage returnMessage = new PingPongRpcTestMessage( ret );
                returnMessage.setResponseId( theOldMessage.getRequestId() );

                theEndpoint.enqueueMessageForSend( returnMessage );

                theOldMessage = null;

            } else {
                log.debug( "Received message, responding with unrelated message" );
                
                PingPongRpcTestMessage returnMessage = new PingPongRpcTestMessage( 4 ) {
                        @Override
                        public String toString()
                        {
                            return super.toString() + ", UNRELATED: sent when payload " + msg.getPayload() + " came in";
                        }
                };
                if( msg.getPayload() >= 2 ) {
                    returnMessage.setResponseId( msg.getPayload() -2 );
                    // send an unrelated, old response id
                }

                theEndpoint.enqueueMessageForSend( returnMessage );
            }

            theOldMessage = msg;
        }
        
        /**
         * Called when the token has been received.
         *
         * @param endpoint the PingPongMessageEndpoint that sent this event
         * @param token the received token
         */
        public void tokenReceived(
                PingPongMessageEndpoint<PingPongRpcTestMessage> endpoint,
                long                                            token )
        {
            if( theOldMessage != null ) {
                long ret = theOldMessage.getPayload();

                log.debug( "Received token, calculating return for old message " + ret );

                ret = ret * ret;

                PingPongRpcTestMessage returnMessage = new PingPongRpcTestMessage( ret );
                returnMessage.setResponseId( theOldMessage.getRequestId() );

                theEndpoint.enqueueMessageForSend( returnMessage );

                theOldMessage = null;
            }
        }    

        /**
         * Store for the last incoming message, so we can respond to it later.
         */
        protected PingPongRpcTestMessage theOldMessage = null;
    }
}

