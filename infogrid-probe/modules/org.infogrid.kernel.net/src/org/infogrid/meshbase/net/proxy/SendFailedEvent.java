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


package org.infogrid.meshbase.net.proxy;

import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.util.StringHelper;

/**
 * Indicates that an attempt to send a message failed.
 */
public abstract class SendFailedEvent
        extends
            ProxyEvent
{
    /**
     * Constructor.
     * 
     * @param sender the sending Proxy
     * @param message the XprisoMessage that could not be sent
     * @param cause the underlying cause for the vent
     */
    protected SendFailedEvent(
            Proxy         sender,
            XprisoMessage message,
            Throwable     cause )
    {
        super( sender, sender.getPartnerMeshBaseIdentifier(), sender, sender.getPartnerMeshBaseIdentifier(), System.currentTimeMillis() );
        
        theMessage = message;
        theCause   = cause;
    }
    
    /**
     * Obtain the XprisoMessage that could not be sent.
     * 
     * @return the XprisoMessage
     */
    public XprisoMessage getMessage()
    {
        return theMessage;
    }
    
    /**
     * Obtain the underlying cause for why the send failed.
     * 
     * @return the cause
     */
    public Throwable getCause()
    {
        return theCause;
    }

    /**
     * Return in string form, for debugging.
     *
     * @return this instance in string form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theSourceIdentifier",
                    "theDeltaValueIdentifier",
                    "theTimeEventOccurred",
                    "theMessage",
                    "theCause",
                    "theCause.getStackTrace()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    getDeltaValueIdentifier(),
                    getTimeEventOccurred(),
                    theMessage,
                    theCause,
                    theCause != null ? theCause.getStackTrace() : null
                });
    }

    /**
     * The XprisoMessage that could not be sent.
     */
    protected XprisoMessage theMessage;
    
    /**
     * The underlying cause for the event.
     */
    protected Throwable theCause;
}
