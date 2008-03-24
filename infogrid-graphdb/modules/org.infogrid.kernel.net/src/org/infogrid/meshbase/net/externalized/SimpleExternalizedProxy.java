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

package org.infogrid.meshbase.net.externalized;

import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import java.util.List;

/**
 *
 */
public class SimpleExternalizedProxy
        extends
            AbstractExternalizedProxy
{
    /**
     * Factory method.
     *
     * @param proxy the Proxy to externalized
     * @return the ExternalizedProxy
     */
    public static SimpleExternalizedProxy create(
            Proxy proxy )
    {
        return new SimpleExternalizedProxy(
                proxy.getTimeCreated(),
                proxy.getTimeUpdated(),
                proxy.getTimeRead(),
                proxy.getTimeExpires(),
                proxy.getMessageEndpoint().getLastSentToken(),
                proxy.getMessageEndpoint().getLastReceivedToken(),
                proxy.getNetMeshBase().getIdentifier(),
                proxy.getPartnerMeshBaseIdentifier(),
                proxy.getMessageEndpoint().messagesToBeSent(),
                proxy.getMessageEndpoint().messagesLastSent() );
    }

    /**
     * Constructor.
     */
    protected SimpleExternalizedProxy(
            long                timeCreated,
            long                timeUpdated,
            long                timeRead,
            long                timeExpires,
            long                lastSentToken,
            long                lastReceivedToken,
            NetMeshBaseIdentifier   networkIdentifier,
            NetMeshBaseIdentifier   networkIdentifierOfPartner,
            List<XprisoMessage> messagesToSend,
            List<XprisoMessage> messagesLastSent )
    {
        theTimeCreated = timeCreated;
        theTimeUpdated = timeUpdated;
        theTimeRead    = timeRead;
        theTimeExpires = timeExpires;
        
        theLastSentToken     = lastSentToken;
        theLastReceivedToken = lastReceivedToken;
        
        theNetworkIdentifier          = networkIdentifier;
        theNetworkIdentifierOfPartner = networkIdentifierOfPartner;
        
        theMessagesToSend   = messagesToSend;
        theMessagesLastSent = messagesLastSent;
    }
}
