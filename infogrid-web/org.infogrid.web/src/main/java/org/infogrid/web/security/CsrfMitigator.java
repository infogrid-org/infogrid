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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.web.security;

import org.infogrid.util.http.SaneRequest;
import org.infogrid.web.templates.StructuredResponse;

/**
 * Implements Cross-site request forgery mitigation.
 */
public interface CsrfMitigator
{
    /**
     * Analyze the request, and determine whether it is vulnerable to
     * a CSRF attack.
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @return true if the request is safe, false otherwise
     */
    public boolean isSafeRequest(
            SaneRequest        request,
            StructuredResponse response );

    /**
     * Obtain an HTML fragment to be inserted into HTML forms to make
     * them CSRF resistant.
     * 
     * @param request the incoming request
     * @return the fragment, or null
     */
    public String getHtmlFormFragment(
            SaneRequest request );
}
