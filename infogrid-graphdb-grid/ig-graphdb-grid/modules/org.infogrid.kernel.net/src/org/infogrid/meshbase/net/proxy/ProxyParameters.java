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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
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
        return new ProxyParameters( coherence, true ); // FIXME?
    }

    /**
     * Factory method with defaults.
     *
     * @return the created ProxyParameters
     */
    public static ProxyParameters create(
            CoherenceSpecification coherence,
            boolean                followRedirects )
    {
        return new ProxyParameters( coherence, followRedirects );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param coherence the requested CoherenceSpecification, if any
     * @param followRedirects if true, silently follow HTTP redirects
     */
    protected ProxyParameters(
            CoherenceSpecification coherence,
            boolean                followRedirects )
    {
        theCoherence       = coherence;
        theFollowRedirects = followRedirects;
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
     * If true, silently follow HTTP redirects.
     *
     * @return if true, silently follow HTTP redirects
     */
    public boolean getFollowRedirects()
    {
        return theFollowRedirects;
    }

    /**
     * The CoherenceSpecification.
     */
    protected CoherenceSpecification theCoherence;

    /**
     * The followRedirects property.
     */
    protected boolean theFollowRedirects;
}
