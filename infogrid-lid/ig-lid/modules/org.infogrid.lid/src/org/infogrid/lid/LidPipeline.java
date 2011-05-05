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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid;

import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;

/**
 * Processes LID requests.
 */
public interface LidPipeline
{
    /**
     * Process the pipeline.
     * 
     * @param lidRequest the incoming request
     * @param siteIdentifier identifies this site
     * @return the compound instructions
     */
    public LidPipelineInstructions processPipeline(
            SaneRequest        lidRequest,
            Identifier         siteIdentifier );
}
