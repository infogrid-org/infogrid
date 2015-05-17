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

package org.infogrid.lid;

import org.infogrid.util.HasIdentifier;
import org.infogrid.util.http.SaneRequest;

/**
 * A stage in the LidPipeline.
 */
public interface LidPipelineStage
{
    /**
     * Process this stage.
     *
     * @param lidRequest the incoming request
     * @param requestedResource the requested resource, if any
     * @param instructionsSoFar the instructions assembled by the pipeline so far
     * @return the instructions for constructing a response to the client, if any
     */
    public LidPipelineStageInstructions processStage(
            SaneRequest             lidRequest,
            HasIdentifier           requestedResource,
            LidPipelineInstructions instructionsSoFar );

    /**
     * Name of the lid-meta parameter.
     */
    public static final String LID_META_PARAMETER_NAME = "lid-meta";

    /**
     * Possible value of the lid-meta parameter.
     */
    public static final String LID_META_CAPABILITIES_PARAMETER_VALUE = "capabilities";

}
