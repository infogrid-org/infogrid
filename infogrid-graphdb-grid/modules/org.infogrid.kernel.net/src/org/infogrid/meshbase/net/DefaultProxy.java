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

package org.infogrid.meshbase.net;

import org.infogrid.comm.MessageEndpoint;

import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.net.NetMessageEndpoint;

import org.infogrid.util.logging.Log;

/**
 * The default implementation of Proxy.
 */
public class DefaultProxy
        extends
            AbstractProxy
{
    private static final Log log = Log.getLogInstance( DefaultProxy.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param ep the communications endpoint
     * @param mb the NetMeshBase this Proxy belongs to
     * @return the created DefaultProxy
     */
    public static DefaultProxy create(
            NetMessageEndpoint ep,
            NetMeshBase        mb )
    {
        DefaultProxy ret = new DefaultProxy( ep, mb );

        if( log.isDebugEnabled() ) {
            log.debug( "Created " + ret, new RuntimeException( "marker" ));
        }
        return ret;
    }

    /**
     * Factory method to restore a Proxy from storage.
     *
     * @param ep the communications endpoint
     * @param mb the NetMeshBase this Proxy belongs to
     * @param timeCreated the timeCreated to use
     * @param timeUpdated the timeUpdated to use
     * @param timeRead the timeRead to use
     * @param timeExpires the timeExpires to use
     * @return the restored DefaultProxy
     */
    public static DefaultProxy restoreProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb,
            long               timeCreated,
            long               timeUpdated,
            long               timeRead,
            long               timeExpires )
    {
        DefaultProxy ret = new DefaultProxy( ep, mb, timeCreated, timeUpdated, timeRead, timeExpires );

        if( log.isDebugEnabled() ) {
            log.debug( "Created " + ret, new RuntimeException( "marker" ));
        }
        return ret;
    }

    /**
     * Constructor.
     *
     * @param ep the communications endpoint
     * @param mb the NetMeshBase this Proxy belongs to
     */
    protected DefaultProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb )
    {
        super( ep, mb );
    }

    /**
     * Constructor.
     *
     * @param ep the communications endpoint
     * @param mb the NetMeshBase this Proxy belongs to
     * @param timeCreated the timeCreated to use
     * @param timeUpdated the timeUpdated to use
     * @param timeRead the timeRead to use
     * @param timeExpires the timeExpires to use
     */
    protected DefaultProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb,
            long               timeCreated,
            long               timeUpdated,
            long               timeRead,
            long               timeExpires )
    {
        super( ep, mb );
        
        theTimeCreated = timeCreated;
        theTimeUpdated = timeUpdated;
        theTimeRead    = timeRead;
        theTimeExpires = timeExpires;
    }

    /**
     * Internal implementation method for messageReceived. Overriding this makes
     * debugging easier as we only get breakpoints from instances of this class.
     *
     * @param endpoint the MessageEndpoint through which the message arrived
     * @param incoming the incoming message
     */
    @Override
    protected void internalMessageReceived(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  incoming )
    {
        super.internalMessageReceived( endpoint, incoming );
    }
}
