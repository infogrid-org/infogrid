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
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.web.sane.SaneServletRequest;

/**
 * A ResponseTemplate that returns binary content verbatim.
 */
public class BinaryPassThruStructuredResponseTemplate
        extends
            AbstractStructuredResponseTemplate
{
    /**
     * Factory method.
     *
     * @param request the incoming HTTP request
     * @param structured the StructuredResponse that contains the response
     * @param defaultMime the default MIME type for the response
     * @return the created JspStructuredResponseTemplate
     */
    public static BinaryPassThruStructuredResponseTemplate create(
            SaneRequest        request,
            StructuredResponse structured,
            String             defaultMime )
    {
        BinaryPassThruStructuredResponseTemplate ret = new BinaryPassThruStructuredResponseTemplate(
                request,
                structured,
                defaultMime );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param request the incoming HTTP request
     * @param structured the StructuredResponse that contains the response
     * @param defaultMime the default MIME type for the response
     */
    protected BinaryPassThruStructuredResponseTemplate(
            SaneRequest        request,
            StructuredResponse structured,
            String             defaultMime )
    {
        super( request, null, null, structured, defaultMime );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOutput(
            HttpServletResponse delegate,
            ServletContext      servletContext,
            SaneServletRequest  request,
            StructuredResponse  response )
        throws
            IOException
    {
        outputStatusCode(        delegate, response );
        outputLocale(            delegate, response );
        outputCookies(           delegate, response );
        outputMimeType(          delegate, response );
        outputLocation(          delegate, response );
        outputAdditionalHeaders( delegate, response );
        
        byte [] binaryContent = response.getDefaultBinarySection().getContent();
        if( binaryContent != null ) {
            OutputStream o = delegate.getOutputStream();
            o.write( binaryContent );
            o.flush();
        }
    }
}
