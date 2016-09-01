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
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.ZeroElementCursorIterator;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;
import org.infogrid.web.sane.SaneServletRequest;

/**
 * A ResponseTemplate that processes a Servlet (e.g. compiled from a JSP)
 * with placeholders, in which the named
 * sections of the StructuredResponse are inserted.
 */
public class ServletStructuredResponseTemplate
        extends
            AbstractStructuredResponseTemplate
{
    private static final Log log = Log.getLogInstance( ServletStructuredResponseTemplate.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param dispatcher identifies the JSP file
     * @param request the incoming HTTP request
     * @param requestedTemplate the requested ResponseTemplate that will be used, if any
     * @param userRequestedTemplate the ResponseTemplate requested by the user, if any
     * @param structured the StructuredResponse that contains the response
     * @param defaultMime the default MIME type for the response
     * @return the created JspStructuredResponseTemplate
     */
    public static ServletStructuredResponseTemplate create(
            Class<? extends Servlet> templateClass,
            SaneRequest              request,
            String                   requestedTemplate,
            String                   userRequestedTemplate,
            StructuredResponse       structured,
            String                   defaultMime )
    {
        ServletStructuredResponseTemplate ret = new ServletStructuredResponseTemplate(
                templateClass,
                request,
                requestedTemplate,
                userRequestedTemplate,
                structured,
                defaultMime );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param dispatcher identifies the JSP file
     * @param request the incoming HTTP request
     * @param requestedTemplate the requested ResponseTemplate that will be used, if any
     * @param userRequestedTemplate the ResponseTemplate requested by the user, if any
     * @param structured the StructuredResponse that contains the response
     * @param defaultMime the default MIME type for the response
     */
    protected ServletStructuredResponseTemplate(
            Class<? extends Servlet> templateClass,
            SaneRequest              request,
            String                   requestedTemplate,
            String                   userRequestedTemplate,
            StructuredResponse       structured,
            String                   defaultMime )
    {
        super( request, requestedTemplate, userRequestedTemplate, structured, defaultMime );

        theTemplateClass = templateClass;
    }

    /**
     * Stream a StructuredResponse to an HttpResponse employing this template.
     *
     * @param delegate the delegate to stream to
     * @param response the StructuredResponse
     * @throws ServletException exception passed on from underlying servlet output
     * @throws IOException exception passed on from underlying servlet output
     */
    @Override
    public void doOutput(
            HttpServletResponse delegate,
            ServletContext      servletContext,
            SaneServletRequest  request,
            StructuredResponse  response )
        throws
            ServletException,
            IOException
    {
        outputStatusCode(  delegate, response );
        outputLocale(      delegate, response );
        outputCookies(     delegate, response );
        outputMimeType(    delegate, response );
        outputLocation(    delegate, response );
        outputAdditionalHeaders( delegate, response );

        Servlet servlet = null;
        try {
            theRequest.setAttribute( StructuredResponse.STRUCTURED_RESPONSE_ATTRIBUTE_NAME, response );

            servlet = theTemplateClass.newInstance();
            servlet.init( new MyServletConfig( theTemplateClass.getName(), servletContext ));
            
            servlet.service( request, delegate );

        } catch( InstantiationException ex ) {
            log.error( ex );

        } catch( IllegalAccessException ex ) {
            log.error( ex );

        } finally {
            theRequest.removeAttribute( StructuredResponse.STRUCTURED_RESPONSE_ATTRIBUTE_NAME );

            if( servlet != null ) {
                servlet.destroy();
            }
        }
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "theRequest",
                    "theStructured",
                    "theRequestedTemplate",
                    "theUserRequestedTemplate",
                    "theTemplateClass"
                },
                new Object[] {
                    theRequest,
                    theStructured,
                    theRequestedTemplate,
                    theUserRequestedTemplate,
                    theTemplateClass
                });
    }

    /**
     * The dispatcher.
     */
    protected Class<? extends Servlet> theTemplateClass;

    static class MyServletConfig
            implements
                ServletConfig
    {
        public MyServletConfig(
                String name,
                ServletContext servletContext )
        {
            theName = name;
            theServletContext = servletContext;
        }

        public String getServletName()
        {
            return theName;
        }

        public ServletContext getServletContext()
        {
            return theServletContext;
        }

        public String getInitParameter( String name )
        {
            return null;
        }

        public Enumeration<String> getInitParameterNames()
        {
            return ZeroElementCursorIterator.create();
        }
        
        protected String theName;
        protected ServletContext theServletContext;
    }
}
