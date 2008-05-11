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

package org.infogrid.comm.pingpong.TEST;

import org.infogrid.comm.MessageEndpoint;
import org.infogrid.comm.pingpong.PingPongMessageEndpoint;
import org.infogrid.comm.pingpong.m.MPingPongMessageEndpoint;

import org.infogrid.util.logging.Log;

/**
 * Tests remote procedure call (RPC) functionality in regular operating mode.
 */
public class PingPongRpcTest1
        extends
            AbstractPingPongRpcTest
{
    /**
     * Test run.
     *
     * @throws Exception this code may throw any Exception
     */
    public void run()
            throws
                Throwable
    {
        MPingPongMessageEndpoint<PingPongRpcTestMessage> ep1 = MPingPongMessageEndpoint.create( "ep1", 1000L, 500L, 10000L, 0.f, exec );
        MPingPongMessageEndpoint<PingPongRpcTestMessage> ep2 = MPingPongMessageEndpoint.create( "ep2", 1000L, 500L, 10000L, 0.f, exec );
        
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
        PingPongRpcTest1 test = null;
        try {
            if( args.length != 0 ) {
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }
            test = new PingPongRpcTest1( args );
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
    public PingPongRpcTest1(
            String [] args )
        throws
            Exception
    {
        log = Log.getLogInstance( getClass() );
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
    private static Log log = Log.getLogInstance( PingPongRpcTest1.class );

    /**
     * A listener that automatically responds.
     */
    class MyListener
            extends
                AbstractPingPongRpcListener
    {
        /**
         * Constructor.
         * 
         * @param end the endpoint where this listener listens
         */
        public MyListener(
                MessageEndpoint<PingPongRpcTestMessage> end )
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
                MessageEndpoint<PingPongRpcTestMessage> endpoint,
                PingPongRpcTestMessage                  msg )
        {
            long ret = msg.getPayload();
            
            log.debug( "Received message, calculating return for " + ret );

            ret = ret * ret;
            
            PingPongRpcTestMessage returnMessage = new PingPongRpcTestMessage( ret );
            returnMessage.setResponseId( msg.getRequestId() );
            
            theEndpoint.enqueueMessageForSend( returnMessage );
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
            // ignore
        }    
    }
}