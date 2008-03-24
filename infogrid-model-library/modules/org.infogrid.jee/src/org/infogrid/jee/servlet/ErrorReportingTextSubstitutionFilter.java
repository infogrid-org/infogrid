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

package org.infogrid.jee.servlet;

import org.infogrid.jee.app.InfoGridWebApp;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Extends TextSubstitutionFilter to obtain reported errors, and insert them
 * into the output.
 */
public class ErrorReportingTextSubstitutionFilter
        extends
            TextSubstitutionFilter
{
    /**
     * Constructor.
     */
    public ErrorReportingTextSubstitutionFilter()
    {
        // no op
    }
    
    /**
     * Obtain the Map of name-value pairs to substitute.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @return the Map
     */
    @Override
    protected Map<String,String> getReplacementMap(
            HttpServletRequest  request,
            HttpServletResponse response )
    {
        Map<String,String> ret = super.getReplacementMap( request, response );
        
        String errorsString = InfoGridWebApp.getSingleton().clearErrorString( request, response );
        
        if( theErrorToken != null ) {
            ret.put( theErrorToken, errorsString );
        }
        
        return ret;
    }
    
    /**
     * }
     * Initialize the Filter.
     *
     * @param filterConfig the Filter configuration object
     * @throws ServletException thrown if misconfigured
     */
    @Override
    public void init(
            FilterConfig filterConfig )
        throws
            ServletException
    {
        super.init( filterConfig );
        
        theErrorToken = theReplacementMap.remove( ERROR_FILTER_PARAMETER );
        
        if( theErrorToken == null || theErrorToken.length() == 0 ) {
            throw new ServletException( "Filter " + getClass().getName() + " is misconfigured: parameter " + ERROR_FILTER_PARAMETER + " is required." );
        }
    }
    
    /**
     * Name of the error filter parameter.
     */
    public static final String ERROR_FILTER_PARAMETER = "ERROR_FILTER_PARAMETER";

    /**
     * Name of the error filter parameter value.
     */
    public String theErrorToken;
}
