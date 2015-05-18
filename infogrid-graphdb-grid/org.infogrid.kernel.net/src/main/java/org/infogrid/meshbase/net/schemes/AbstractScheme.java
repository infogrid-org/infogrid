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

package org.infogrid.meshbase.net.schemes;

/**
 * Factors out functionality common to schemes.
 */
public abstract class AbstractScheme
        implements
            Scheme
{
    /**
     * Constructor for subclasses only.
     *
     * @param name the name of the scheme
     */
    protected AbstractScheme(
            String name )
    {
        theName = name;
    }

    /**
     * Obtain the name of this scheme.
     *
     * @return the name of the scheme
     */
    public String getName()
    {
        return theName;
    }

    /**
     * The name of the scheme.
     */
    protected String theName;
}
