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

package org.infogrid.meshbase.net;

import org.infogrid.util.AbstractLocalizedException;

/**
 * Indicates that a NetMeshBase is not found at a given NetMeshBaseIdentifier any more, but that
 * a new NetMeshBaseIdentifier shall be used instead.
 */
public class NetMeshBaseRedirectException
        extends
            AbstractLocalizedException
{
    /**
     * Constructor.
     *
     * @param oldId the old NetMeshBaseIdentifier
     * @param newId the new NetMeshBaseIdentifier
     */
    public NetMeshBaseRedirectException(
            NetMeshBaseIdentifier oldId,
            NetMeshBaseIdentifier newId )
    {
        this( oldId, newId, null );
    }

    /**
     * Constructor.
     *
     * @param oldId the old NetMeshBaseIdentifier
     * @param newId the new NetMeshBaseIdentifier
     * @param cause the cause for this Exception, if any
     */
    public NetMeshBaseRedirectException(
            NetMeshBaseIdentifier oldId,
            NetMeshBaseIdentifier newId,
            Throwable             cause )
    {
        super( null, cause );
        
        theOldId = oldId;
        theNewId = newId;
    }

    /**
     * Obtain the old NetMeshBaseIdentifier.
     *
     * @return the NetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getOldId()
    {
        return theOldId;
    }

    /**
     * Obtain the new NetMeshBaseIdentifier.
     *
     * @return the NetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getNewId()
    {
        return theNewId;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] {
            theOldId,
            theNewId
        };
    }

    /**
     * The original NetMeshBaseIdentifier before the redirect.
     */
    protected NetMeshBaseIdentifier theOldId;

    /**
     * The new NetMeshBaseIdentifier after the redirect.
     */
    protected NetMeshBaseIdentifier theNewId;
}
