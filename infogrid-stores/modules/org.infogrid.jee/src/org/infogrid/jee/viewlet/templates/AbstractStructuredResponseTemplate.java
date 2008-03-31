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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.viewlet.templates;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.rest.AbstractRestfulRequest;
import org.infogrid.jee.rest.RestfulRequest;

/**
 * Factors out common functionality of StructuredResponseTemplates.
 */
public abstract class AbstractStructuredResponseTemplate
        implements
            StructuredResponseTemplate
{
    /**
     * Constructor for subclasses only.
     */
    protected AbstractStructuredResponseTemplate(
            RestfulRequest     restful,
            StructuredResponse structured )
    {
        theRestful           = restful;
        theStructured        = structured;
    }
    
    /**
     * Obtain the incoming request.
     * 
     * @return the incoming request
     */
    public RestfulRequest getRequest()
    {
        return theRestful;
    }

    /**
     * Obtain the StructuredResponse that instantiates this template.
     * 
     * @return the StructuredResponse
     */
    public StructuredResponse getStructuredResponse()
    {
        return theStructured;
    }

    /**
     * Default implentation for how to handle cookies.
     */
    protected void defaultOutputCookies(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        String template = theRestful.getRequestedTemplate();

        Cookie toSend;

        if( template == null ) {
            toSend = new Cookie( AbstractRestfulRequest.LID_TEMPLATE_COOKIE_NAME, "**deleted**" );
            toSend.setMaxAge( 0 ); // delete cookie
        } else {
            toSend = new Cookie( AbstractRestfulRequest.LID_TEMPLATE_COOKIE_NAME, template );
            toSend.setPath( theRestful.getContextPath() );
            toSend.setMaxAge( 60 * 60 * 24 * 365 * 10 ); // 10 years
        }
        delegate.addCookie( toSend );
    }

    /**
     * Default implentation for how to handle the MIME type.
     */
    protected void defaultOutputMimeType(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {        
        String mime = structured.getMimeType();
        if( mime == null ) {
            mime = "text/plain";
        }
        delegate.setContentType( mime );
    }

    /**
     * The incoming request.
     */
    protected RestfulRequest theRestful;

    /**
     * The structured response to process with the dispatcher
     */
    protected StructuredResponse theStructured;
}
