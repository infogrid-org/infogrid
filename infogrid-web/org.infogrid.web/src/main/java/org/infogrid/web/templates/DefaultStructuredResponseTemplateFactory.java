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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import org.infogrid.util.AbstractFactory;
import org.infogrid.util.FactoryException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.web.JeeFormatter;

/**
 * A default implementation for a StructuredResponseTemplateFactory.
 */
public class DefaultStructuredResponseTemplateFactory
        extends
            AbstractFactory<SaneRequest,StructuredResponseTemplate,StructuredResponse>
        implements
            StructuredResponseTemplateFactory
{
    private static final Log log = Log.getLogInstance( DefaultStructuredResponseTemplateFactory.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param formatter the JeeFormatter to use
     * @return the created DefaultStructuredResponseTemplateFactory
     */
    public static DefaultStructuredResponseTemplateFactory create(
            JeeFormatter formatter )
    {
        DefaultStructuredResponseTemplateFactory ret
                = new DefaultStructuredResponseTemplateFactory( DEFAULT_TEMPLATE_NAME, DEFAULT_MIME_TYPE, formatter );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param defaultTemplateName name of the default template
     * @param formatter the JeeFormatter to use
     * @return the created DefaultStructuredResponseTemplateFactory
     */
    public static DefaultStructuredResponseTemplateFactory create(
            String       defaultTemplateName,
            JeeFormatter formatter )
    {
        DefaultStructuredResponseTemplateFactory ret
                = new DefaultStructuredResponseTemplateFactory( defaultTemplateName, DEFAULT_MIME_TYPE, formatter );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param defaultTemplateName name of the default template
     * @param defaultMimeType default mime type of no other is specified.
     * @param formatter the JeeFormatter to use
     * @return the created DefaultStructuredResponseTemplateFactory
     */
    public static DefaultStructuredResponseTemplateFactory create(
            String       defaultTemplateName,
            String       defaultMimeType,
            JeeFormatter formatter )
    {
        DefaultStructuredResponseTemplateFactory ret
                = new DefaultStructuredResponseTemplateFactory( defaultTemplateName, defaultMimeType, formatter );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param defaultTemplateName name of the default template
     * @param defaultMimeType default mime type of no other is specified.
     * @param formatter the JeeFormatter to use
     */
    protected DefaultStructuredResponseTemplateFactory(
            String       defaultTemplateName,
            String       defaultMimeType,
            JeeFormatter formatter )
    {
        theDefaultTemplateName = defaultTemplateName;
        theDefaultMimeType     = defaultMimeType;
        theJeeFormatter        = formatter;
    }

    /**
     * Add a supported template servlet.
     * 
     * @param templateName the name of the template
     * @param mime the MIME type of the request
     * @param servlet the Servlet
     */
    public void addTemplateServlet(
            String                   templateName,
            String                   mime,
            Class<? extends Servlet> servlet )
    {
        Map<String,Class<? extends Servlet>> mimeTable = theTemplateServlets.get( templateName );
        if( mimeTable == null ) {
            mimeTable = new HashMap<>();
            theTemplateServlets.put( templateName, mimeTable );
        }
        if( mimeTable.put( mime, servlet ) != null ) {
            log.error( "Overwriting entry in template servlet table", templateName, mime );
        }
    }
    /**
     * Factory method.
     *
     * @param request the incoming HTTP request for which the response is being created
     * @param structured the StructuredResponse that contains the content to be returned
     * @return the created object
     * @throws FactoryException catch-all Exception, consider its cause
     */
    @Override
    public StructuredResponseTemplate obtainFor(
            SaneRequest        request,
            StructuredResponse structured )
        throws
            FactoryException
    {
        StructuredResponseSection defaultTextSection = structured.getDefaultSection();

        String mime;
        if( !defaultTextSection.isEmpty() ) {
            mime = defaultTextSection.getContentType();
        } else {
            mime = theDefaultMimeType;
        }

        StructuredResponseTemplate ret;

        String requestedTemplateName     = structured.getRequestedTemplateName();
        String userRequestedTemplateName = getUserRequestedTemplate( request );

        if( requestedTemplateName == null ) {
            // internally requested template overrides user-requested template
            requestedTemplateName = userRequestedTemplateName;
        }

        if( NoContentStructuredResponseTemplate.NO_CONTENT_TEMPLATE_NAME.equals( requestedTemplateName )) {
            ret = NoContentStructuredResponseTemplate.create(
                    request,
                    requestedTemplateName,
                    userRequestedTemplateName,
                    structured,
                    mime );

        } else if( VerbatimStructuredResponseTemplate.VERBATIM_TEXT_TEMPLATE_NAME.equals( requestedTemplateName )) {
            ret = VerbatimStructuredResponseTemplate.create(
                    request,
                    requestedTemplateName,
                    userRequestedTemplateName,
                    structured,
                    mime,
                    theJeeFormatter );

        } else {
            Class<? extends Servlet> templateServlet = null;

            if( requestedTemplateName != null ) {
                templateServlet = findTemplateServlet( requestedTemplateName, mime );
            }

            if( templateServlet == null ) {
                // try default template if named template did not work
                templateServlet = findTemplateServlet( theDefaultTemplateName, mime );
            }
            if( templateServlet == null && mime == null ) {
                // if no mime type is specified, default to html
                if( requestedTemplateName != null ) {
                    templateServlet = findTemplateServlet( requestedTemplateName, "text/html" );
                }
                if( templateServlet == null && ( requestedTemplateName != null && requestedTemplateName.length() > 0 )) {
                    // try default template if named template did not work
                    templateServlet = findTemplateServlet( theDefaultTemplateName, "text/html" );
                }
            }

            if( templateServlet != null ) {
                ret = ServletStructuredResponseTemplate.create(
                        templateServlet,
                        request,
                        requestedTemplateName,
                        userRequestedTemplateName,
                        structured,
                        mime );

            } else if( mime != null && !mime.startsWith( "text/" )) {
                // binary content
                ret = PassThruStructuredResponseTemplate.create(
                        request,
                        structured,
                        mime );

            } else {
                // all hope is lost, we have to stream verbatim whatever it is that is in structured
                ret = VerbatimStructuredResponseTemplate.create(
                        request,
                        requestedTemplateName,
                        userRequestedTemplateName,
                        structured,
                        mime,
                        theJeeFormatter );
            }
        }
        return ret;
    }

    /**
     * Obtain the name of the requested layout template, if any.
     *
     * @param request the incoming HTTP request for which the response is being created
     * @return class name of the requested layout template, if any
     */
    public String getUserRequestedTemplate(
            SaneRequest request )
    {
        String ret = request.getUrlArgument( StructuredResponseTemplate.LID_TEMPLATE_PARAMETER_NAME );

        if( ret == null ) {
            ret = request.getCookieValue( StructuredResponseTemplate.LID_TEMPLATE_COOKIE_NAME );
        }

        return ret;
    }

    /**
     * Find a suitable Servlet based on the provided name and MIME type
     * 
     * @param requestedTemplateName name of the requested servlet
     * @param mime requested mime type
     * @return the found Servlet class, or null
     */
    protected Class<? extends Servlet> findTemplateServlet(
            String requestedTemplateName,
            String mime )
    {
        Map<String,Class<? extends Servlet>> mimeAlternatives = theTemplateServlets.get( requestedTemplateName );
        if( mimeAlternatives == null ) {
            return null;
        }
        Class<? extends Servlet> ret = mimeAlternatives.get( mime );
        return ret;
    }

    /**
     * Name of the default template, if no other has been specified in the request.
     */
    protected String theDefaultTemplateName;

    /**
     * The default mime type, if no other has been specified in the request.
     */
    protected String theDefaultMimeType;

    /**
     * The known template servlets.
     * Keyed first by template name, then by MIME type
     */
    protected Map<String,Map<String,Class<? extends Servlet>>> theTemplateServlets = new HashMap<>();

    /**
     * The JeeFormatter to use.
     */
    protected JeeFormatter theJeeFormatter;
    
    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( DefaultStructuredResponseTemplateFactory.class );

    /**
     * Name of the default template, if no other has been specified in the constructor or the request.
     */
    public static final String DEFAULT_TEMPLATE_NAME = theResourceHelper.getResourceStringOrDefault( "DefaultTemplateName", "default" );

    /**
     * Default mime type, if no other has been specified in the constructor or the request.
     */
    public static final String DEFAULT_MIME_TYPE = theResourceHelper.getResourceStringOrDefault( "DefaultMimeType", "text/html" );
}
