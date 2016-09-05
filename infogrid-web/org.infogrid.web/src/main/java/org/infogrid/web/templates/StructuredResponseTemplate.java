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

package org.infogrid.web.templates;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.infogrid.web.sane.SaneServletRequest;

/**
 * A template for a StructuredResponse.
 */
public interface StructuredResponseTemplate
{
    /**
     * Apply this template to the StructuredResponse.
     * 
     * @param servletContext the ServletContext to be passed to Servlets
     * @param request the incoming request
     * @param response the structured response
     * @throws ServletException exception passed on from underlying servlet output
     * @throws IOException exception passed on from underlying servlet output
     */
    public void applyTemplate(
            ServletContext      servletContext,
            SaneServletRequest  request,
            StructuredResponse  response )
        throws
            ServletException,
            IOException;

    /**
     * Name of the LID template parameter.
     */
    public static final String LID_TEMPLATE_PARAMETER_NAME = "lid-template";
    
    /**
     * Name of the cookie representing the LID template.
     */
    public static final String LID_TEMPLATE_COOKIE_NAME = "org-netmesh-lid-template";
}
