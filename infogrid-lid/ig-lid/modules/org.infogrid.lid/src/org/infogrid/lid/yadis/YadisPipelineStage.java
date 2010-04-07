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

import org.infogrid.lid.LidPipelineStage;
import org.infogrid.util.context.ObjectInContext;

/**
 * Knows how to process Yadis requests.
 */
public interface YadisPipelineStage
        extends
            LidPipelineStage,
            ObjectInContext
{
    /**
     * Name of the Yadis HTTP header.
     */
    public static final String YADIS_HTTP_HEADER = "X-XRDS-Location";

    /**
     * Yadis MIME type.
     */
    public static final String XRDS_MIME_TYPE = "application/xrds+xml";
}
