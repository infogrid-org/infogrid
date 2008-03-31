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
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.rest.AbstractRestfulRequest;
import org.infogrid.jee.rest.RestfulRequest;

/**
 *
 */
public class VerbatimResponseTemplate
        extends
            AbstractStructuredResponseTemplate
{
    /**
     * Factory method.
     *
     * @return the created JspStructuredResponseTemplate
     */
    public static VerbatimResponseTemplate create(
            RestfulRequest     restful,
            StructuredResponse structured )
    {
        VerbatimResponseTemplate ret = new VerbatimResponseTemplate( restful, structured );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     */
    protected VerbatimResponseTemplate(
            RestfulRequest     restful,
            StructuredResponse structured )
    {
        super( restful, structured );
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
            IOException
    {
        defaultOutputCookies(  delegate, structured );
        defaultOutputMimeType( delegate, structured );
        
        // stream default section(s)
            
        String errorContent = structured.getSectionContent( TextStructuredResponseSection.ERROR_SECTION );
        if( errorContent != null ) {
            Writer w = delegate.getWriter();
            w.write( errorContent );
            w.flush();
        }

        String textContent = structured.getSectionContent( TextStructuredResponseSection.DEFAULT_SECTION );
        if( textContent != null ) {
            Writer w = delegate.getWriter();
            w.write( textContent );
            w.flush();
        }

        byte [] binaryContent = structured.getSectionContent( BinaryStructuredResponseSection.DEFAULT_SECTION );
        if( binaryContent != null ) {
            OutputStream o = delegate.getOutputStream();
            o.write( binaryContent );
            o.flush();
        }
    }
}
