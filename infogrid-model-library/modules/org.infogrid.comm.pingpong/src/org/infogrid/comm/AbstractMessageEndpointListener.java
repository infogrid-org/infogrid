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

package org.infogrid.comm;

import java.util.List;

/**
 * Makes implementing MessageEndpointListeners easier.
 */
public abstract class AbstractMessageEndpointListener<T>
        implements
            MessageEndpointListener<T>
{
    /**
     * Called when an incoming message has arrived.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the received message
     */
    public void messageReceived(
            MessageEndpoint<T> endpoint,
            T                  msg )
    {
        // no op
    }
    
    /**
     * Called when an outgoing message has been sent.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the sent message
     */
    public void messageSent(
            MessageEndpoint<T> endpoint,
            T                  msg )
    {
        // no op
    }
    
    /**
     * Called when an outgoing message has enqueued for sending.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the enqueued message
     */
    public void messageEnqueued(
            MessageEndpoint<T> endpoint,
            T                  msg )
    {
        // no op
    }
    
    /**
     * Called when an outoing message failed to be sent.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the outgoing messages, in order
     */
    public void messageSendingFailed(
            MessageEndpoint<T> endpoint,
            List<T>            msg )
    {
        // no op
    }

    /**
     * Called when an error was severe enough that continuing as a MessageEndPoint makes
     * no sense.
     *
     * @param endpoint the MessageEndpoint that sent this event
     * @param msg the status of the outgoing queue
     * @param t the Throwable indicating the error. This may be null if not available
     */
    public void disablingError(
            MessageEndpoint<T> endpoint,
            List<T>            msg,
            Throwable          t )
    {
        // no op
    }
}
