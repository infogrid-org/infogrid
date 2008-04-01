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

/**
 * A message endpoint for bidirectional communications. The method 
 * enqueueMessageForSend is used for asynchronous sending of messages.
 * Incoming responses are received by subscribed MessageEndpointListeners.
 */
public interface MessageEndpoint<T>
{
    /**
     * Send a message via the next ping or pong.
     *
     * @param msg the Message to send.
     */
    public void enqueueMessageForSend(
            T msg );

    /**
     * Add a MessageEndpointListener as a listener.
     *
     * @param newListener the listener to add
     */
    public void addDirectMessageEndpointListener(
            MessageEndpointListener<T> newListener );
    
    /**
     * Add a WeakReference to a MessageEndpointListener as a listener.
     *
     * @param newListener the listener to add
     */
    public void addWeakMessageEndpointListener(
            MessageEndpointListener<T> newListener );
    
    /**
     * Remove a MessageEndpointListener.
     *
     * @param oldListener the listener to remove
     */
    public void removeMessageEndpointListener(
            MessageEndpointListener<T> oldListener );
}
