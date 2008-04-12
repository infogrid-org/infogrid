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

package org.infogrid.comm.pingpong;

import org.infogrid.comm.MessageEndpoint;
import org.infogrid.comm.MessageEndpointIsDeadException;
import org.infogrid.comm.MessageEndpointListener;
import org.infogrid.comm.MessageSendException;

import org.infogrid.util.AbstractListenerSet;
import org.infogrid.util.FlexibleListenerSet;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>Endpoint for bidirectional communications using the ping-pong protocol.
 *    This is abstract: subclasses need to implement the atual message transfer mechanism.</p>
 * <p>In order to avoid difficult timing conditions, all time constants are continually modified with
 *    a slight, random delta.</p>
 * <p>This class supports a regular and a low-level logger, which reflect application-developer
 *    vs. protocol-developer-centric views of logging.</p>
 * 
 * @param T the message type
 */
public abstract class PingPongMessageEndpoint<T>
        implements
            MessageEndpoint<T>
{
    private static final Log logHigh = Log.getLogInstance( PingPongMessageEndpoint.class ); // our own, private logger for high-level events
    private static final Log logLow  = Log.getLogInstance( PingPongMessageEndpoint.class.getName() + "-lowlevel" ); // our own, private logger for low-level events

    /**
     * Constructor for subclasses only.
     *
     * @param name the name of the PingPongMessageEndpoint (for debugging only)
     * @param deltaRespond the number of milliseconds until this PingPongMessageEndpoint returns the token
     * @param deltaResend  the number of milliseconds until this PingPongMessageEndpoint resends the token if sending the token failed
     * @param deltaRecover the number of milliseconds until this PingPongMessageEndpoint decides that the token
     *                     was not received by the partner PingPongMessageEndpoint, and resends
     * @param randomVariation the random component to add to the various times
     * @param exec the ScheduledExecutorService to schedule timed tasks
     * @param lastSentToken the last token sent in a previous instantiation of this MessageEndpoint
     * @param lastReceivedToken the last token received in a previous instantiation of this MessageEndpoint
     * @param messagesSentLast the last set of Messages sent in a previous instantiation of this MessageEndpoint
     * @param messagesToBeSent the Messages to be sent as soon as possible
     */
    protected PingPongMessageEndpoint(
            String                   name,
            long                     deltaRespond,
            long                     deltaResend,
            long                     deltaRecover,
            double                   randomVariation,
            ScheduledExecutorService exec,
            long                     lastSentToken,
            long                     lastReceivedToken,
            List<T>                  messagesSentLast,
            List<T>                  messagesToBeSent )
    {
        theName            = name;
        theDeltaRespond    = deltaRespond;
        theDeltaResend     = deltaResend;
        theDeltaRecover    = deltaRecover;
        theRandomVariation = randomVariation;

        theExecutorService = exec;

        theLastSentToken     = lastSentToken;
        theLastReceivedToken = lastReceivedToken;
        theMessagesSentLast  = messagesSentLast;
        theMessagesToBeSent  = messagesToBeSent != null ? messagesToBeSent : new ArrayList<T>();
    }

    /**
     * Send a message via the next ping or pong.
     *
     * @param msg the Message to send.
     */
    public void enqueueMessageForSend(
            T msg )
    {
        if( logHigh.isDebugEnabled() ) {
            logHigh.debug( this + ".enqueueMessageForSend( " + msg + " )" );
        }
        
        synchronized( theMessagesToBeSent ) {
            theMessagesToBeSent.add( msg );
        }
        theListeners.fireEvent( msg, EventType.MESSAGE_ENQUEUED );
    }

    /**
     * Start the ping-pong.
     */
    public void startCommunicating()
    {
        if( theFuture == null ) {
            doAction( "first" );
        }
    }

    /**
     * Stop the ping-pong.
     */
    public void stopCommunicating()
    {
        ScheduledFuture<?> future = theFuture;
        if( future != null ) {
            future.cancel( false );
            theFuture = null;
        }
    }

    /**
     * Calculate the next time of something, given a base and the random variation.
     *
     * @param base the base value of the property, e.g. theDeltaRespond
     * @return the next time
     */
    protected long calculateFuture(
            long base )
    {
        double almost = ( ( Math.random() - 0.5f ) * theRandomVariation + 1. ) * base;
        long   ret    = (long) almost;
        
        return ret;
    }

    /**
     * Schedule a future task.
     *
     * @param tag identify the kind of task, for debugging (only)
     * @param base the base value of the time delay, e.g. theDeltaRespond
     */
    protected void schedule(
            String tag,
            long   base )
    {
        long actual = calculateFuture( base );

        if( logLow.isDebugEnabled() ) {
            logLow.debug( this + ".schedule( " + tag + ", " + base + " ) in " + actual + " msec" );
        }

        theFuture = theExecutorService.schedule( new TimedTask( this, tag ), actual, TimeUnit.MILLISECONDS );
    }
    
    /**
     * Invoked when the timer triggers.
     *
     * @param tag the tag, for debugging
     */
    protected void doAction(
            String tag )
    {
        if( logLow.isDebugEnabled() ) {
            logLow.debug( this + ".doAction( " + tag + " ): queue has length " + theMessagesToBeSent.size() );
        }        

        // determine whether this is a regular response, a resend, or a recover. Resend and regular
        // response are treated by the same code, the only difference is when it is invoked by the timer.
        
        long    tokenToSend;
        List<T> toBeSent = null; // send nothing unless something is set

        synchronized( this ) {
            if( theLastReceivedToken < 0 ) {
                // never received anything
                if( theLastReceivedToken < 0 ) {
                    // never sent anything either
                    tokenToSend = 1; // very first token
                } else {
                    tokenToSend = theLastSentToken; // resend
                }

            } else if( ( theLastSentToken < 0 ) || ( theLastReceivedToken == theLastSentToken + 1 )) {
                // regular response
                tokenToSend = theLastReceivedToken + 1;

            } else if( theLastReceivedToken + 1 == theLastSentToken ) {
                // resend
                tokenToSend = theLastSentToken;
            } else {
                logLow.error( "No idea how we got here: " + this );
                return;
            }

            if( tokenToSend == theLastSentToken ) {
                // resend
                toBeSent = theMessagesSentLast;

            } else {
                // regular response
                synchronized( theMessagesToBeSent ) {
                    if( !theMessagesToBeSent.isEmpty() ) {
                        toBeSent = new LinkedList<T>();
                        toBeSent.addAll( theMessagesToBeSent );
                        theMessagesToBeSent.clear(); // we do this so the synchronized statement always works on the same object
                    }
                }
            }

            theLastSentToken    = tokenToSend;
            theMessagesSentLast = toBeSent;
        }

        // make sure we use local variables here, not member variables, in order to support concurrency
        if( logLow.isDebugEnabled() ) {
            logLow.debug( this + " doRespond: about to send message( " + toBeSent + " )" );
        }        
        try {
            schedule( "recover", theDeltaRecover );
                // schedule a recover event prior to sending and firing events to listeners:
                // if the sending takes a long time, we don't want to block

        } catch( RejectedExecutionException ex ) {
            // underlying ThreadPool does not like this task
            if( logLow.isInfoEnabled() ) {
                logLow.info( this + " cannot schedule (" + tokenToSend + "): " + ( toBeSent != null ? toBeSent : "<empty>" ), ex );
            }
            
            theListeners.fireEvent( toBeSent, new MessageEndpointIsDeadException() );

            if( theFuture != null ) {
                // this is the recover future, we don't want to recover if message send failed, but resend
                if( logLow.isDebugEnabled() ) {
                    logLow.debug( this + " canceling future (RejectedExecutionException)" );
                }
                theFuture.cancel( false );
            }
        }
        
        try {
            // do not reschedule the future, we stop here

            sendMessage( tokenToSend, toBeSent );

            if( logHigh.isDebugEnabled() ) {
                logHigh.debug( this + " sent message (" + tokenToSend + ") successfully: " + ( toBeSent != null ? toBeSent : "<empty>" ));
            }

            theListeners.fireEvent( tokenToSend, EventType.TOKEN_SENT );
            if( toBeSent != null ) {
                for( T current : toBeSent ) {
                    theListeners.fireEvent( current, EventType.MESSAGE_SENT );
                }
            }
            
        } catch( MessageEndpointIsDeadException ex ) {
            if( logHigh.isInfoEnabled() ) {
                logHigh.info( this + " Endpoint is dead (" + tokenToSend + "): " + ( toBeSent != null ? toBeSent : "<empty>" ), ex );
            }
            
            theListeners.fireEvent( toBeSent, ex );

            if( theFuture != null ) {
                // this is the recover future, we don't want to recover if message send failed, but resend
                if( logLow.isDebugEnabled() ) {
                    logLow.debug( this + " canceling future (MessageEndpointIsDeadException)" );
                }
                theFuture.cancel( false );
            }
            
            // do not reschedule the future, we stop here
            
        } catch( MessageSendException ex ) {

            if( logHigh.isInfoEnabled() ) {
                logHigh.info( this + " failed to send message (" + tokenToSend + "): " + ( toBeSent != null ? toBeSent : "<empty>" ), ex );
            }

            if( theFuture != null ) {
                // this is the recover future, we don't want to recover if message send failed, but resend
                if( logLow.isDebugEnabled() ) {
                    logLow.debug( this + " canceling future (MessageSendException)" );
                }
                theFuture.cancel( false );
            }

            schedule( "resend", theDeltaResend );
                // schedule a resend event prior to firing events to listeners

            theListeners.fireEvent( toBeSent, EventType.MESSAGE_SENDING_FAILED );

        } catch( Throwable t ) {
            // catch-all
            logHigh.error( "Unexpected exception", t );
        }
    }

    /**
     * Implemented by subclasses, this performs the actual message send.
     *
     * @param token the integer representing the token
     * @param content the payload, if any
     * @throws MessageSendException thrown if the message could not be sent
     */
    protected abstract void sendMessage(
            long    token,
            List<T> content )
        throws
            MessageSendException;

    /**
     * Invoked by subclasses to provide the content of a received message.
     *
     * @param token the integer representing the token
     * @param content the content of a received message
     * @throws MessageEndpointIsDeadException thrown if the MessageEndpoint is dead
     */
    protected void incomingMessage(
            long    token,
            List<T> content )
        throws
            MessageEndpointIsDeadException
    {
        try {
            if( content != null && ! content.isEmpty() ) {
                if( logHigh.isInfoEnabled() ) {
                    logHigh.info( this + ".incomingMessage( " + token + ", " + content + " )" );
                }
            } else {
                if( logHigh.isDebugEnabled() ) {
                    logHigh.debug( this + ".incomingMessage( " + token + ", " + content + " )" );
                }
            }

            // ignore if we received this one already
            if( theLastReceivedToken == token ) {
                logLow.warn( this + " ignoring duplicate incoming message(" + token + "): " + content );
                return;
            }

            if( theFuture != null ) {
                if( logLow.isDebugEnabled() ) {
                    logLow.debug( this + " canceling future (regular response required)" );
                }
                theFuture.cancel( false );
            }
            theLastReceivedToken = token;

            theListeners.fireEvent( token, EventType.TOKEN_RECEIVED );
            if( content != null ) {
                for( T current : content ) {
                    theListeners.fireEvent( current, EventType.MESSAGE_RECEIVED );
                }
            }
        } catch( Throwable t ) {
            logHigh.error( t );

        } finally {
            schedule( "respond", theDeltaRespond );
        }
    }

    /**
     * Obtain the token that was last sent.
     *
     * @return the token
     */
    public long getLastSentToken()
    {
        return theLastSentToken;
    }

    /**
     * Obtain the token that was last received.
     *
     * @return the token
     */
    public long getLastReceivedToken()
    {
        return theLastReceivedToken;
    }
    
    /**
     * Add a MessageEndpointListener.
     *
     * @param newListener the listener to add
     */
    public void addDirectMessageEndpointListener(
            MessageEndpointListener<T> newListener )
    {
        theListeners.addDirect( newListener );
    }
    
    /**
     * Add a MessageEndpointListener.
     *
     * @param newListener the listener to add
     */
    public void addWeakMessageEndpointListener(
            MessageEndpointListener<T> newListener )
    {
        theListeners.addWeak( newListener );
    }
    
    /**
     * Remove a MessageEndpointListener.
     *
     * @param oldListener the listener to remove
     */
    public void removeMessageEndpointListener(
            MessageEndpointListener<T> oldListener )
    {
        theListeners.remove( oldListener );
    }
    
    /**
     * Convert to String form, for debugging.
     *
     * @return String form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theName",
                    "theLastReceivedToken",
                    "theLastSentToken",
                    "theFuture",
                    "theMessagesToBeSent"
                },
                new Object[] {
                    theName,
                    theLastReceivedToken,
                    theLastSentToken,
                    theFuture != null ? theFuture.getDelay( TimeUnit.MILLISECONDS ) : -1L,
                    theMessagesToBeSent
                });
    }

    /**
     * Name of the endpoint (for debugging).
     */
    protected String theName;
    
    /**
     * The time until we respond with a ping to an incoming pong.
     */
    protected long theDeltaRespond;
    
    /**
     * The time until we retry to send the token if sending the token failed.
     */
    protected long theDeltaResend;
    
    /**
     * The time util we attempt to recover after having sent a ping whose pong did not arrive.
     */
    protected long theDeltaRecover;
    
    /**
     * Percentage of random variation on theDeltaResponse and theDeltaRecover, in order to avoid
     * having repeated "collisions".
     */
    protected double theRandomVariation;
    
    /**
     * The outgoing queue of Messages to send.
     */
    protected List<T> theMessagesToBeSent;
    
    /**
     * Cached content of the last sent message, in order to be able to resend it upon
     * recover.
     */
    protected List<T> theMessagesSentLast;
    
    /**
     * The last token that was sent, in order to be able to resend it upon recover.
     */
    protected long theLastSentToken;
    
    /**
     * The last token that was received.
     */
    protected long theLastReceivedToken;

    /**
     * The means by which to execute tasks.
     */
    protected ScheduledExecutorService theExecutorService;
    
    /**
     * The Future representing the TimedTask.
     */
    protected ScheduledFuture<?> theFuture;
    
    /**
     * Captures the types of events that can be sent to the listeners.
     */
    @SuppressWarnings(value={"unchecked"})
    static enum EventType {
        
        MESSAGE_SENT {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                listener.messageSent( sender, (T) event );
            }
        },
        MESSAGE_RECEIVED {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                listener.messageReceived( sender, (T) event );
            }
        },
        MESSAGE_ENQUEUED {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                listener.messageEnqueued( sender, (T) event );
            }
        },
        TOKEN_SENT {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                if( listener instanceof PingPongMessageEndpointListener ) {
                    PingPongMessageEndpointListener realListener = (PingPongMessageEndpointListener) listener;
                    realListener.tokenSent( sender, (Long) event );
                }
            }
        },
        TOKEN_RECEIVED {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                if( listener instanceof PingPongMessageEndpointListener ) {
                    PingPongMessageEndpointListener realListener = (PingPongMessageEndpointListener) listener;
                    realListener.tokenReceived( sender, (Long) event );
                }
            }
        },
        MESSAGE_SENDING_FAILED {
            public <T> void fireEvent(
                    PingPongMessageEndpoint<T> sender,
                    MessageEndpointListener<T> listener,
                    Object                     event )
            {
                listener.messageSendingFailed( sender, (List<T>) event );
            }
        };
        
        /**
         * Send the event.
         * 
         * @param sender the endpoint that sent the event
         * @param listener the listener to which the event should be sent
         * @param event the event Object itself
         */
        abstract public <T> void fireEvent(
                PingPongMessageEndpoint<T> sender,
                MessageEndpointListener<T> listener,
                Object                     event );
    }

    /**
     * The current set of MessageEndpointListeners.
     */
    protected AbstractListenerSet<MessageEndpointListener<T>,Object,Object> theListeners
            = new FlexibleListenerSet<MessageEndpointListener<T>,Object,Object>()
    {
        /**
         * Fire the event to one contained object.
         *
         * @param listener the receiver of this event
         * @param event the sent event
         * @param parameter dispatch parameter
         */
        @SuppressWarnings(value={"unchecked"})
        protected void fireEventToListener(
                MessageEndpointListener<T> listener,
                Object                     event,
                Object                     parameter )
        {
            try {
                if( parameter instanceof EventType ) {
                    EventType realParameter = (EventType) parameter;
                    
                    realParameter.fireEvent( PingPongMessageEndpoint.this, listener, event );

                } else if( parameter == null || parameter instanceof Throwable ) {
                    listener.disablingError( PingPongMessageEndpoint.this, (List<T>) event, (Throwable) parameter );

                } else {
                    logLow.error( "unknown parameter: " + parameter );
                }
            } catch( Throwable t ) {
                logLow.error( t );
            }
        }
    };
    
    /**
     * The task to respond.
     */    
    static class TimedTask
            implements
                Runnable
    {
        /**
         * Constructor.
         *
         * @param ep the endpoint that is supposed to respond. This is kept internally as a WeakReference
         * @param tag for debugging
         */
        public TimedTask(
                PingPongMessageEndpoint ep,
                String                  tag )
        {
            theEndpointRef = new WeakReference<PingPongMessageEndpoint>( ep );
            theTag         = tag;
        }

        /**
         * Run the task.
         */
        public void run()
        {
            PingPongMessageEndpoint ep = theEndpointRef.get();
            
            if( ep != null ) {
                if( logLow.isDebugEnabled() ) {
                    logLow.debug( "TimedTask about to invoke " + ep );
                }
                try {
                    ep.doAction( theTag );

                } catch( Throwable t ) {
                    logLow.error( t );
                }
            } else {
                logLow.debug( "TimedTask cannot execute, PingPongMessageEndpoint has gone away" );
            }
        }
        
        /**
         * Reference to the endpoint. This is a WeakReference as we don't want to get in
         * the way of the endpoint being garbage collected.
         */
        protected WeakReference<PingPongMessageEndpoint> theEndpointRef;

        /**
         * The tag, for debugging.
         */
        protected String theTag;
    }
}
