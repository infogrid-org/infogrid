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

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.http.OutgoingSaneCookie;
import org.infogrid.util.http.SaneRequest;

/**
 * Instructions obtained from a LidPipelineStage.
 */
public interface LidPipelineStageInstructions
{
    /**
     * Obtain the content to be returned.
     *
     * @return the content
     */
    public String getContent();

    /**
     * Obtain the content type to be returned.
     *
     * @return the content type
     */
    public String getContentType();

    /**
     * Obtain the cookies that shall be removed.
     *
     * @return the cookies, if any
     */
    public OutgoingSaneCookie [] getCookiesToRemove();

    /**
     * Obtain the cookies that shall be set.
     *
     * @return the cookies, if any
     */
    public OutgoingSaneCookie [] getCookiesToSet();

    /**
     * Apply all instructions as recommended.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @return true if no further action needs to be taken by the pipeline
     * @throws IOException thrown if an I/O error occurred
     */
    public boolean applyAsRecommended(
            SaneRequest         request,
            HttpServletResponse response )
        throws
            IOException;
}
