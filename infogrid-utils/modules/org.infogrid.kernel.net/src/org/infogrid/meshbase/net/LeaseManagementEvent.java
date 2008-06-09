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

import org.infogrid.meshbase.net.proxy.Proxy;
import java.util.EventObject;

/**
  * This indicates a change in a proxy with respect to lease management.
  * Currently, this event carries no data whatsoever as it relates to what
  * actually changed with respect to leases. We can add this later if necessary
  * (FIXME?) but it's not obvious how to represent it because one event may
  * express several changes at at the same time (objects were obtained, granted,
  * lease expirations were updated etc.) and we don't want to create several
  * events instead.
  */
public class LeaseManagementEvent
        extends
            EventObject
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param theSender the sending Proxy
     */
    public LeaseManagementEvent(
            Proxy theSender )
    {
        super( theSender );
    }

    /**
     * Convenience method to determine the Proxy that sent this event.
     *
     * @return the Proxy that sent this event
     */
    public Proxy getProxy()
    {
        return (Proxy) getSource();
    }
}
