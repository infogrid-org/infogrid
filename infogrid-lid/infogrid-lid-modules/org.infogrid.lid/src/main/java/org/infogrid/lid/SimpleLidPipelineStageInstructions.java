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
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.http.OutgoingSaneCookie;
import org.infogrid.util.http.SaneRequest;

/**
 * Simple general-purpose implementation of LidPipelineStageInstructions.
 */
public class SimpleLidPipelineStageInstructions
        implements
            LidPipelineStageInstructions
{
    /**
     * Factory method for a status code only.
     *
     * @param status the HTTP status to be returned
     * @return the created instructions
     */
    public static SimpleLidPipelineStageInstructions create(
            int status )
    {
        return new SimpleLidPipelineStageInstructions( null, null, status, null, null, null, true );
    }

    /**
     * Factory method for a simple redirect.
     *
     * @param location the URL to redirect to
     * @param status the HTTP status to be returned
     * @return the created instructions
     */
    public static SimpleLidPipelineStageInstructions create(
            String location,
            int    status )
    {
        return new SimpleLidPipelineStageInstructions( null, null, status, location, null, null, true );
    }

    /**
     * Factory method for content to be sent.
     *
     * @param content the content to return
     * @param contentType the MIME type of the content to return
     * @return the created instructions
     */
    public static SimpleLidPipelineStageInstructions createContentOnly(
            String content,
            String contentType )
    {
        return new SimpleLidPipelineStageInstructions( content, contentType, null, null, null, null, true );
    }

    /**
     * Factory method for additional HTTP header to be sent.
     *
     * @param name name of the header
     * @param value content of the header
     * @return the created instructions
     */
    public static SimpleLidPipelineStageInstructions createHeaderOnly(
            String name,
            String value )
    {
        return new SimpleLidPipelineStageInstructions(
                null,
                null,
                null,
                null,
                new String[] { name },
                new String[] { value },
                false );
    }

    /**
     * Constructor.
     *
     * @param content the content to return
     * @param contentType the MIME type of the content to return
     * @param location the URL to redirect to
     * @param status the HTTP status to be returned
     * @param headerNames names of additional HTTP headers to be sent
     * @param headerValues values of additional HTTP headers to be sent
     * @param applyAsRecommendedReturnValue return value from the applyAsRecommended method.
     */
    protected SimpleLidPipelineStageInstructions(
            String    content,
            String    contentType,
            Integer   status,
            String    location,
            String [] headerNames,
            String [] headerValues,
            boolean   applyAsRecommendedReturnValue )
    {
        theContent      = content;
        theContentType  = contentType;
        theStatus       = status;
        theLocation     = location;
        theHeaderNames  = headerNames;
        theHeaderValues = headerValues;
        theApplyAsRecommendedReturnValue = applyAsRecommendedReturnValue;
    }

    /**
     * Obtain the content to be returned.
     *
     * @return the content
     */
    public String getContent()
    {
        return theContent;
    }

    /**
     * Obtain the content type to be returned.
     *
     * @return the content type
     */
    public String getContentType()
    {
        return theContentType;
    }

    /**
     * Obtain the location URL.
     *
     * @return the location URL
     */
    public String getLocation()
    {
        return theLocation;
    }

    /**
     * Obtain the status to be returned.
     *
     * @return the status
     */
    public int getStatus()
    {
        return theStatus;
    }

    /**
     * Obtain the cookies that shall be removed.
     *
     * @return the cookies, if any
     */
    public OutgoingSaneCookie [] getCookiesToRemove()
    {
        return new OutgoingSaneCookie[0];
    }

    /**
     * Obtain the cookies that shall be set.
     *
     * @return the cookies, if any
     */
    public OutgoingSaneCookie [] getCookiesToSet()
    {
        return new OutgoingSaneCookie[0];
    }

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
            IOException
    {
        if( theStatus != null ) {
            response.setStatus( theStatus );
        }
        if( theLocation != null ) {
            response.setHeader( "Location", theLocation );
        }
        if( theContentType != null ) {
            response.setContentType( theContentType );
        }
        if( theContent != null ) {
            Writer w = response.getWriter();
            w.write( theContent );
            w.flush();
        }
        if( theHeaderNames != null ) {
            for( int i=0 ; i<theHeaderNames.length ; ++i ) {
                response.addHeader( theHeaderNames[i], theHeaderValues[i] );
            }
        }
        return theApplyAsRecommendedReturnValue;
    }

    /**
     * The content to be returned.
     */
    protected String theContent;

    /**
     * The content type to be returned.
     */
    protected String theContentType;

    /**
     * The redirect location.
     */
    protected String theLocation;

    /**
     * The HTTP status to be returned.
     */
    protected Integer theStatus;

    /**
     * Additional HTTP header names to be sent.
     */
    protected String [] theHeaderNames;

    /**
     * Additional HTTP header values to be sent.
     */
    protected String [] theHeaderValues;

    /**
     * Return value from the applyAsRecommended method.
     */
    protected boolean theApplyAsRecommendedReturnValue;
}
