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

package org.infogrid.meshbase.net.proxy;

import org.infogrid.meshbase.net.CoherenceSpecification;

/**
 * Collects all data that describes the manner in which a updates shall be performed.
 */
public class ProxyParameters
{
    /**
     * Factory method with defaults.
     *
     * @return the created ProxyParameters
     */
    public static ProxyParameters create(
            CoherenceSpecification coherence )
    {
        return new ProxyParameters( coherence );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param coherence the requested CoherenceSpecification, if any
     */
    protected ProxyParameters(
            CoherenceSpecification coherence )
    {
        theCoherence  = coherence;
    }

    /**
     * Obtain the CoherenceSpecification.
     *
     * @return the CoherenceSpecification
     */
    public CoherenceSpecification getCoherenceSpecification()
    {
        return theCoherence;
    }

    /**
     * The CoherenceSpecification.
     */
    protected CoherenceSpecification theCoherence;
}
