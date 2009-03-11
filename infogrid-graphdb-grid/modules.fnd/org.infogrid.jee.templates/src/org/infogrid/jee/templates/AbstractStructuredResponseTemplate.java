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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.templates;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.CarriesHttpStatusCodeException;
import org.infogrid.util.StringHelper;
import org.infogrid.util.context.AbstractObjectInContext;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;

/**
 * Factors out common functionality of StructuredResponseTemplates.
 * By default, we consider the cookies, redirects etc. of the template first and the default section in it
 * next. All other section's attributes of that kind are ignored
 */
public abstract class AbstractStructuredResponseTemplate
        extends
            AbstractObjectInContext
        implements
            StructuredResponseTemplate
{
    /**
     * Constructor for subclasses only.
     * 
     * @param request the incoming HTTP request
     * @param requestedTemplate the requested ResponseTemplate that will be used, if any
     * @param userRequestedTemplate the ResponseTemplate requested by the user, if any
     * @param structured the StructuredResponse that contains the response
     * @param c the Context to use
     */
    protected AbstractStructuredResponseTemplate(
            SaneRequest        request,
            String             requestedTemplate,
            String             userRequestedTemplate,
            StructuredResponse structured,
            Context            c )
    {
        super( c );

        theRequest               = request;
        theStructured            = structured;
        theRequestedTemplate     = requestedTemplate;
        theUserRequestedTemplate = userRequestedTemplate;
    }
    
    /**
     * Obtain the incoming request.
     * 
     * @return the incoming request
     */
    public SaneRequest getRequest()
    {
        return theRequest;
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
     * Determine which components of the StructuredResponse to consider for cookies etc.
     * 
     * @param structured the StructuredResponse
     * @return the components to consider, most important first
     */
    protected HasHeaderPreferences [] toConsider(
            StructuredResponse structured )
    {
        HasHeaderPreferences [] toConsider = {
                structured,
                structured.obtainTextSection(   StructuredResponse.TEXT_DEFAULT_SECTION ),
                structured.obtainBinarySection( StructuredResponse.BINARY_DEFAULT_SECTION )
        };
        return toConsider;
    }

    /**
     * Default implementation for how to handle the HTTP status code.
     * 
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputStatusCode(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        // If an explicit response code has been set, we use that one.
        // If not, the response code of the first exception that has one wins

        int status = -1;
        outer: for( HasHeaderPreferences current : toConsider( structured ) ) {
            status = current.getHttpResponseCode();
            if( status > 0 ) {
                break;
            }
            for( Throwable t : current.problems() ) {
                if( current instanceof CarriesHttpStatusCodeException ) {
                    status = ((CarriesHttpStatusCodeException)current).getDesiredHttpStatusCode();
                    if( status > 0 ) {
                        break outer;
                    }
                }
            }
        }
        if( status <= 0 ) {
            status = 200;
        }
        delegate.setStatus( status );
    }
    
    /**
     * Default implementation for how to handle the Locale.
     * 
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputLocale(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        Locale found = null;
        for( HasHeaderPreferences current : toConsider( structured ) ) {
            found = current.getLocale();
            if( found != null ) {
                delegate.setLocale( found );
                break;
            }
        }
    }

    /**
     * Default implentation for how to handle getCookies.
     * 
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputCookies(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        for( HasHeaderPreferences current : toConsider( structured ) ) {
            for( Cookie c : current.getCookies()) {
                delegate.addCookie( c );
            }
        }        
    }

    /**
     * Default implentation for how to handle the MIME type.
     * 
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputMimeType(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        String mime = null;
        for( HasHeaderPreferences current : toConsider( structured ) ) {
            mime = current.getMimeType();
            if( mime != null ) {
                delegate.setContentType( mime );
                break;
            }
        }
    }

    /**
     * Default implementation for how to handle a location header.
     * 
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputLocation(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {        
        String location = null;
        for( HasHeaderPreferences current : toConsider( structured ) ) {
            location = current.getLocation();
            if( location != null ) {
                delegate.setHeader( "Location", location );
                // don't use delegate.sendRedirect as it also sets the status code
                break;
            }
        }
    }
    
    /**
     * Default implementation for how to emit a Yadis header.
     *
     * @param delegate the underlying HttpServletResponse
     * @param structured the StructuredResponse that contains the response
     * @throws IOException thrown if an I/O error occurred
     */
    protected void outputYadisHeader(
            HttpServletResponse delegate,
            StructuredResponse  structured )
        throws
            IOException
    {
        String yadisHeader = structured.getYadisHeader();
        if( yadisHeader != null ) {
            delegate.setHeader( "X-XRDS-Location", yadisHeader );
        }
    }

    /**
     * Convert to String form, for debugging.
     *
     * @return String form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theRequest",
                    "theStructured",
                    "theRequestedTemplate",
                    "theUserRequestedTemplate"
                },
                new Object[] {
                    theRequest,
                    theStructured,
                    theRequestedTemplate,
                    theUserRequestedTemplate
                });
    }

    /**
     * The incoming request.
     */
    protected SaneRequest theRequest;

    /**
     * The structured response to process with the dispatcher
     */
    protected StructuredResponse theStructured;

    /**
     * The requested formatting template, if any.
     */
    protected String theRequestedTemplate;
    
    /**
     * The formatting template requested by the user, if any.
     */
    protected String theUserRequestedTemplate;
}
