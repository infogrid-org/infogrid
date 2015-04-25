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

package org.infogrid.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.util.logging.Log;

/**
 * Factors out functionality common to many SendingMessageEndpoint implementations
 * that use a "fire and forget" model.
 * 
 * @param <T> the message type
 */
public abstract class AbstractFireAndForgetSendingMessageEndpoint<T>
        extends
            AbstractSendingMessageEndpoint<T>
{
    private static final Log log = Log.getLogInstance( AbstractFireAndForgetSendingMessageEndpoint.class ); // our own, private logger

    /**
     * Constructor for subclasses only.
     * 
     * @param name the name of the MessageEndpoint (for debugging only)
     * @param deltaResend  the number of milliseconds until this endpoint resends the message if sending the message failed
     * @param randomVariation the random component to add to the various times
     * @param exec the ScheduledExecutorService to schedule timed tasks
     * @param messagesToBeSent outgoing message queue (may or may not be empty)
     */
    protected AbstractFireAndForgetSendingMessageEndpoint(
            String                   name,
            long                     deltaResend,
            double                   randomVariation,
            ScheduledExecutorService exec,
            List<T>                  messagesToBeSent )
    {
        super( name, randomVariation, exec, messagesToBeSent );

        theDeltaResend = deltaResend;
    }

    /**
     * Send a message.
     *
     * @param msg the Message to send.
     */
    @Override
    public void enqueueMessageForSend(
            T msg )
    {
        super.enqueueMessageForSend( msg );
        
        synchronized( this ) {
            if( theFutureTask != null ) {
                theFutureTask.cancel(); // This is pessimistic if enqueueMessageForSend is invoked N times very rapidly
            }
            super.schedule( new SendTask( this ), 0 ); // immediately
        }
    }

    /**
     * Invoked when the timer triggers.
     *
     * @param task the TimedTask that invokes this method
     */
    protected void doAction(
            TimedTask task )
    {
        List<T> toSend = new ArrayList<T>();
        
        synchronized( theMessagesToBeSent ) {
            toSend.addAll( theMessagesToBeSent );
        }

        // we can send messages out of order with SMTP
        List<T> failed = new ArrayList<T>();
        for( T current : toSend ) {

            try {
                if( log.isDebugEnabled() ) {
                    log.debug( "Attempting to send", current );
                }
                sendMessage( current );

                synchronized( theMessagesToBeSent ) {
                    theMessagesToBeSent.remove( current );
                }
                theListeners.fireEvent( current, MESSAGE_SENT );

            } catch( MessageSendException ex ) {
                failed.add( current );
                
                theListeners.fireEvent( current, MESSAGE_SENDING_FAILED );

                log.warn( "Could not send", current, ex );
            }
        }
        
        if( !failed.isEmpty() ) {
            synchronized( this ) {
                if( theFutureTask == null || theFutureTask.isCancelled() ) {
                    schedule( new ResendTask( this ), theDeltaResend );
                }
            }
        }
    }

    /**
     * Implemented by subclasses, this performs the actual message send.
     *
     * @param msg the payload
     * @throws MessageSendException thrown if the message could not be sent
     */
    protected abstract void sendMessage(
            T msg )
        throws
            MessageSendException;

    /**
     * The time until we retry to send the message if sending the message failed.
     */
    protected long theDeltaResend;

    /**
     * The send task.
     */
    protected static class SendTask
            extends
                TimedTask
    {
        /**
         * Constructor.
         *
         * @param ep the endpoint that is supposed to respond
         */
        public SendTask(
                AbstractSendingMessageEndpoint ep )
        {
            super( ep );
        }        
    }
    
    /**
     * The resend task.
     */
    protected static class ResendTask
            extends
                TimedTask
    {
        /**
         * Constructor.
         *
         * @param ep the endpoint that is supposed to respond
         */
        public ResendTask(
                AbstractSendingMessageEndpoint ep )
        {
            super( ep );
        }        
    }
}
