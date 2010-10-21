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

package org.infogrid.probe.shadow;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.proxy.ProxyParameters;
import org.infogrid.probe.httpmapping.HttpMappingPolicy;

/**
 * Collects all data that describes the manner in which the ShadowMeshBase shall perform its work.
 */
public class ShadowParameters
        extends
            ProxyParameters
{
    /**
     * Factory method with defaults.
     *
     * @return the created ProxyParameters
     */
    public static ShadowParameters create(
            CoherenceSpecification coherence,
            HttpMappingPolicy      mappingPolicy )
    {
        return new ShadowParameters( coherence, mappingPolicy );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param coherence the requested CoherenceSpecification, if any
     */
    protected ShadowParameters(
            CoherenceSpecification coherence,
            HttpMappingPolicy      mappingPolicy )
    {
        super( coherence );

        theMappingPolicy = mappingPolicy;
    }

    /**
     * Obtain the HttpMappingPolicy.
     *
     * @return the HttpMappingPolicy
     */
    public HttpMappingPolicy getHttpMappingPolicy()
    {
        return theMappingPolicy;
    }

    /**
     * The policy to map HTTP status codes into the InfoGrid world.
     */
    protected HttpMappingPolicy theMappingPolicy;
}
