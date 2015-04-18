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

package org.infogrid.lid.yadis;

import org.infogrid.lid.LidPipelineInstructions;
import org.infogrid.lid.LidPipelineStageInstructions;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.context.AbstractObjectInContext;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;

/**
 * Factors out functionality common to YadisPipelineStage implementations.
 */
public abstract class AbstractYadisPipelineStage
        extends
            AbstractObjectInContext
        implements
            YadisPipelineStage
{
    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param c the context containing the available services.
     */
    protected AbstractYadisPipelineStage(
            Context c )
    {
        super( c );
    }

    /**
     * Process this stage.
     *
     * @param lidRequest the incoming request
     * @param requestedResource the requested resource, if any
     * @param instructionsSoFar the instructions assembled by the pipeline so far
     * @return the instructions for constructing a response to the client, if any
     */
    public LidPipelineStageInstructions processStage(
            SaneRequest                       lidRequest,
            HasIdentifier                     requestedResource,
            LidPipelineInstructions instructionsSoFar )
    {
        String meta = lidRequest.getUrlArgument( LID_META_PARAMETER_NAME );
        if( meta == null ) {
            meta = lidRequest.getUrlArgument( "meta" );
        }
        String acceptHeader = lidRequest.getAcceptHeader();

        LidPipelineStageInstructions ret;
        if(    LID_META_CAPABILITIES_PARAMETER_VALUE.equals( meta )
            || ( acceptHeader != null && acceptHeader.indexOf( XRDS_MIME_TYPE ) >= 0 ))
        {
            ret = processYadisRequest( lidRequest, requestedResource );

        } else {
            ret = addYadisHeader( lidRequest, requestedResource );
        }
        return ret;
    }

    /**
     * It was discovered that this is a Yadis request. Process.
     *
     * @param lidRequest the incoming request
     * @param requestedResource the resource to which the request refers, if any
     * @return the instructions for constructing a response to the client, if any
     */
    protected abstract LidPipelineStageInstructions processYadisRequest(
            SaneRequest        lidRequest,
            HasIdentifier      requestedResource );

    /**
     * Add a suitable Yadis HTTP header. This method does this by putting an attribute
     * into the incoming request, which needs to be processed by the calling logic.
     *
     * @param lidRequest the incoming request
     * @param requestedResource the resource to which the request refers, if any
     * @return the instructions for constructing a response to the client, if any
     */
    protected abstract LidPipelineStageInstructions addYadisHeader(
            SaneRequest        lidRequest,
            HasIdentifier      requestedResource );
}
