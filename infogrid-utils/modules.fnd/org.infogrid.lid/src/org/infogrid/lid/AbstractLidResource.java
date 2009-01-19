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

package org.infogrid.lid;

import org.infogrid.util.Identifier;

/**
 * Collects features of LidResource that are common to many implementations.
 */
public abstract class AbstractLidResource
        implements
            LidResource
{
    /**
     * Constructor for subclasses only.
     * 
     * @param identifier the unique identifier of the persona, e.g. their identity URL
     */
    protected AbstractLidResource(
            Identifier identifier )
    {
        theIdentifier = identifier;
    }

    /**
     * Obtain the persona's unique identifier.
     * 
     * @return the unique identifier
     */
    public Identifier getIdentifier()
    {
        return theIdentifier;
    }

    /**
     * The unique identifier of the LidResource.
     */
    protected Identifier theIdentifier;
}