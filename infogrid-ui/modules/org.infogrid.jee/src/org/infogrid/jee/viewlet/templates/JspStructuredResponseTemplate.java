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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.rest.RestfulRequest;

/**
 *
 */
public class JspStructuredResponseTemplate
        extends
            AbstractStructuredResponseTemplate
{
    /**
     * Factory method.
     *
     * @return the created JspStructuredResponseTemplate
     */
    public static JspStructuredResponseTemplate create(
            RequestDispatcher  dispatcher,
            RestfulRequest     restful,
            StructuredResponse structured )
    {
        JspStructuredResponseTemplate ret = new JspStructuredResponseTemplate( dispatcher, restful, structured );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     */
    protected JspStructuredResponseTemplate(
            RequestDispatcher  dispatcher,
            RestfulRequest     restful,
            StructuredResponse structured )
    {
        super( restful, structured );

        theRequestDispatcher = dispatcher;
        theRestful           = restful;
    }

    /**
     * Stream a StructuredResponse to an HttpResponse employing this template.
     * 
     * @param delegate the delegate to stream to
     * @param structured the StructuredResponse
     */
    public void doOutput(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            ServletException,
            IOException
    {
        defaultOutputCookies(  delegate, structured );
        defaultOutputMimeType( delegate, structured );

        try {
            theRestful.getDelegate().setAttribute( STRUCTURED_RESPONSE_ATTRIBUTE_NAME, structured );
            theRequestDispatcher.include( theRestful.getDelegate(), delegate );

        } finally {
            theRestful.getDelegate().removeAttribute( STRUCTURED_RESPONSE_ATTRIBUTE_NAME );
        }
    }
    
    /**
     * The dispatcher.
     */
    protected RequestDispatcher theRequestDispatcher;
    
    /**
     * Name of the request attribute that contains the StructuredResponse.
     */
    public static final String STRUCTURED_RESPONSE_ATTRIBUTE_NAME = "SR";
}
