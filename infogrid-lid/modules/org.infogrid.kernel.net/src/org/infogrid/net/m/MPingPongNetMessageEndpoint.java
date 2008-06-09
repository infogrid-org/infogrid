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

package org.infogrid.net.m;

import org.infogrid.comm.MessageSendException;
import org.infogrid.comm.pingpong.m.MPingPongMessageEndpoint;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;

import org.infogrid.net.NetMessageEndpoint;

import org.infogrid.util.FactoryException;
import org.infogrid.util.NameServer;
import org.infogrid.util.logging.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
    
/**
 * Subclass of MPingPongMessageEndpoint to be used as the result of the factory method.
 */
public class MPingPongNetMessageEndpoint
        extends
            MPingPongMessageEndpoint<XprisoMessage>
        implements
            NetMessageEndpoint
{
    private static final Log log = Log.getLogInstance( MPingPongNetMessageEndpoint.class ); // our own, private logger

    /**
     * Factory method.
     */
    public static MPingPongNetMessageEndpoint create(
            String                                                  name,
            NetMeshBaseIdentifier                                   partnerIdentifier,
            NetMeshBaseIdentifier                                   myIdentifier,
            long                                                    deltaRespond,
            long                                                    deltaResend,
            long                                                    deltaRecover,
            double                                                  randomVariation,
            NameServer<NetMeshBaseIdentifier,? extends NetMeshBase> nameServer,
            ScheduledExecutorService                                exec )
    {
        MPingPongNetMessageEndpoint ret = new MPingPongNetMessageEndpoint(
                name,
                partnerIdentifier,
                myIdentifier,
                deltaRespond,
                deltaResend,
                deltaRecover,
                randomVariation,
                nameServer,
                exec,
                -1,
                -1,
                null,
                null );
    
        if( log.isDebugEnabled() ) {
            log.debug( "Created " + ret, new RuntimeException( "marker" ));
        }
        return ret;
    }
    
    /**
     * Factory method.
     */
    public static MPingPongNetMessageEndpoint restore(
            String                                                  name,
            NetMeshBaseIdentifier                                   partnerIdentifier,
            NetMeshBaseIdentifier                                   myIdentifier,
            long                                                    deltaRespond,
            long                                                    deltaResend,
            long                                                    deltaRecover,
            double                                                  randomVariation,
            NameServer<NetMeshBaseIdentifier,? extends NetMeshBase> nameServer,
            ScheduledExecutorService                                exec,
            long                                                    lastSentToken,
            long                                                    lastReceivedToken,
            List<XprisoMessage>                                     messagesSentLast,
            List<XprisoMessage>                                     messagesToBeSent )
    {
        MPingPongNetMessageEndpoint ret = new MPingPongNetMessageEndpoint(
                name,
                partnerIdentifier,
                myIdentifier,
                deltaRespond,
                deltaResend,
                deltaRecover,
                randomVariation,
                nameServer,
                exec,
                lastSentToken,
                lastReceivedToken,
                messagesSentLast,
                messagesToBeSent );
    
        if( log.isDebugEnabled() ) {
            log.debug( "Created " + ret, new RuntimeException( "marker" ));
        }
        return ret;
    }
    
    /**
     * Constructor.
     *
     * @param name the name of the PingPongMessageEndpoint (for debugging only)
     * @param deltaRespond the number of milliseconds until this PingPongMessageEndpoint returns the token
     * @param deltaResend  the number of milliseconds until this PingPongMessageEndpoint resends the token if sending the token failed
     * @param deltaRecover the number of milliseconds until this PingPongMessageEndpoint decides that the token
     *                     was not received by the partner PingPongMessageEndpoint, and resends
     * @param exec the ScheduledExecutorService to use for threading
     * @param lastSentToken the last token sent in a previous instantiation of this MessageEndpoint
     * @param lastReceivedToken the last token received in a previous instantiation of this MessageEndpoint
     * @param messagesSentLast the last set of Messages sent in a previous instantiation of this MessageEndpoint
     * @param messageToBeSent the Messages to be sent as soon as possible
     */
    protected MPingPongNetMessageEndpoint(
            String                                                  name,
            NetMeshBaseIdentifier                                   partnerIdentifier,
            NetMeshBaseIdentifier                                   myIdentifier,
            long                                                    deltaRespond,
            long                                                    deltaResend,
            long                                                    deltaRecover,
            double                                                  randomVariation,
            NameServer<NetMeshBaseIdentifier,? extends NetMeshBase> nameServer,
            ScheduledExecutorService                                exec,
            long                                                    lastSentToken,
            long                                                    lastReceivedToken,
            List<XprisoMessage>                                     messagesSentLast,
            List<XprisoMessage>                                     messagesToBeSent )
    {
        super(  name,
                deltaRespond,
                deltaResend,
                deltaRecover,
                randomVariation,
                exec,
                lastSentToken,
                lastReceivedToken,
                messagesSentLast,
                messagesToBeSent );
        
        if( partnerIdentifier.equals( myIdentifier )) {
            throw new IllegalArgumentException( "Cannot talk to myself" );
        }
        
        thePartnerIdentifier = partnerIdentifier;
        theMyIdentifier      = myIdentifier;
        theNameServerRef     = new WeakReference<NameServer<NetMeshBaseIdentifier,? extends NetMeshBase>>( nameServer );
    }

    /**
     * Determine the NetMeshBaseIdentifier of the partner MeshBase.
     * 
     * @return the NetMeshBaseIdentifier of the partner MeshBase
     */
    public NetMeshBaseIdentifier getNetworkIdentifierOfPartner()
    {
        return thePartnerIdentifier;
    }

    /**
     * Do the message send.
     *
     * @param token the token for the message
     * @param content the content to send.
     */
    @Override
    protected void sendMessage(
            long                token,
            List<XprisoMessage> content )
        throws
            MessageSendException
    {
        if( thePartner == null ) {
            NameServer<NetMeshBaseIdentifier, ? extends NetMeshBase> nameServer = theNameServerRef.get();
            if( nameServer == null ) {
                // has been garbage collected, we are done
                return;
            }
            try {
                NetMeshBase partnerBase = nameServer.get( thePartnerIdentifier );
                if( partnerBase == null ) {
                    throw new MessageSendException( content, "Could not find NetMeshBase with identifier " + thePartnerIdentifier );
                }

                Proxy partnerProxy = partnerBase.obtainProxyFor( theMyIdentifier, null ); // FIXME? What is the right CoherenceSpecification here?
                if( partnerProxy == null ) {
                    throw new MessageSendException( content, "Could not obtain proxy for " + theMyIdentifier + " from NetMeshBase with identifier " + thePartnerIdentifier );
                }

                thePartner = (MPingPongNetMessageEndpoint) partnerProxy.getMessageEndpoint();

            } catch( FactoryException ex ) {
                throw new MessageSendException( content, ex );
            }
        }
        super.sendMessage( token, content );
    }
    
    /**
     * Identifier of the local MeshBase.
     */
    protected NetMeshBaseIdentifier theMyIdentifier;

    /**
     * Identifier of the partner MeshBase.
     */
    protected NetMeshBaseIdentifier thePartnerIdentifier;

    /**
     * The NameServer to use to find the partner NetMeshBase. This is a WeakReference so garbage collection
     * is not impeded.
     */
    protected WeakReference<NameServer<NetMeshBaseIdentifier, ? extends NetMeshBase>> theNameServerRef;
}
