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

import org.infogrid.util.RemoteQueryTimeoutException;

import org.infogrid.util.logging.Log;

import java.lang.reflect.InvocationTargetException;

/**
 * An communications endpoint for Remote-Procedure-Style communicayions.
 */
public abstract class RpcClientEndpoint<A,R,T extends CarriesInvocationId>
        extends
            WaitForResponseEndpoint<T>
{
    private static final Log log = Log.getLogInstance( RpcClientEndpoint.class ); // our own, private logger
    
    /**
     * Constructor.
     */
    protected RpcClientEndpoint(
            MessageEndpoint<T> messageEndpoint )
    {
        super( messageEndpoint );
        
        messageEndpoint.addWeakMessageEndpointListener( this );
    }

    /**
     * Invoke the remote procedure call.
     *
     * @param arg the argument
     * @return the return value
     * @throws RemoteQueryTimeoutException thrown if the invocation timed out
     */
    public R invoke(
            A arg )
        throws
            RemoteQueryTimeoutException,
            InvocationTargetException
    {
        return invoke( arg, defaultTimeout );
    }
    
    /**
     * Invoke the remote procedure call.
     *
     * @param arg the argument
     * @param timeout the timeout, in milliseconds
     * @return the return value
     * @throws RemoteQueryTimeoutException thrown if the invocation timed out
     */
    public R invoke(
            A    arg,
            long timeout )
        throws
            RemoteQueryTimeoutException,
            InvocationTargetException
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".invoke( " + arg + ", " + timeout + " )" );
        }
        T outgoing = marshal( arg );

        T incoming = call( outgoing, timeout );
        
        R ret = unmarshal( incoming );
        
        if( log.isDebugEnabled() ) {
            log.debug( this + ".invoke( " + arg + ", " + timeout + " ) returned " + ret );
        }
        
        return ret;
    }
    
    /**
     * Marshal an RPC argument into an outgoing message.
     */
    protected abstract T marshal(
            A arg );
    
    /**
     * Unmarshal an incoming message into an RPC result
     */
    protected abstract R unmarshal(
            T msg );
}
