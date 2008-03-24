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

package org.infogrid.jee.servlet.net;

import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.rest.net.NetRestfulRequest;
import org.infogrid.jee.rest.net.DefaultNetRestfulRequest;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.jee.servlet.ViewletDispatcherServlet;
import org.infogrid.jee.viewlet.JeeViewlet;

import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;

import org.infogrid.util.logging.Log;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Extends ViewletDispatcherServlet to also be able to render Shadows and Proxies.
 */
public class NetViewletDispatcherServlet
        extends
            ViewletDispatcherServlet
{
    private static final Log log = Log.getLogInstance( NetViewletDispatcherServlet.class ); // our own, private logger

    /**
     * Do the work. This may be overridden by subclasses.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @throws ServletException thrown if an error occurred
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    protected void performService(
            HttpServletRequest  realRequest,
            HttpServletResponse realResponse )
        throws
            ServletException,
            IOException
    {
        InfoGridWebApp    app            = InfoGridWebApp.getSingleton();
        NetRestfulRequest restfulRequest = (NetRestfulRequest) realRequest.getAttribute( RestfulRequest.class.getName() );

        try {
            NetMeshBaseIdentifier proxyIdentifier = restfulRequest.determineRequestedProxyIdentifier();
            if( proxyIdentifier != null ) {
                // trying to show a Proxy
                Proxy p = restfulRequest.determineRequestedProxy();
                if( p != null ) {
                    showInViewlet( p, proxyIdentifier, realRequest, realResponse );
                } else {
                    app.reportProblem( new IllegalArgumentException( "Cannot find specified Proxy with identifier " + proxyIdentifier ));
                }
            } else {
                super.performService( realRequest, realResponse);
            }
        } catch( MeshObjectAccessException ex ) {
            app.reportProblem( ex );

        } catch( URISyntaxException ex ) {
            app.reportProblem( ex );
        }
    }

    /**
     * Construct a RestfulRequest object that is suitable to the URL-to-MeshObject mapping
     * applied by this application.
     *
     * @param lidRequest the incoming request
     * @param context the context path of the application
     * @return the created RestfulRequest
     */
    @Override
    protected NetRestfulRequest createRestfulRequest(
            SaneServletRequest lidRequest,
            String             context )
    {
        DefaultNetRestfulRequest ret = DefaultNetRestfulRequest.create(
                lidRequest,
                context );
        return ret;
    }

    /**
     * Show a subject, if any, in a Viewlet.
     *
     * @param subject the subject
     * @param subjectIdentifier the MeshObjectIdentifier of the subject that may or may not have been found
     * @param realRequest the incoming request
     * @param realResponse the outgoing response
     * @throws ServletException a problem occurred
     */
    protected void showInViewlet(
            Proxy                 subject,
            NetMeshBaseIdentifier subjectIdentifier,
            HttpServletRequest    realRequest,
            HttpServletResponse   realResponse  )
        throws
            ServletException
    {
        InfoGridWebApp             app            = InfoGridWebApp.getSingleton();
        SaneServletRequest         lidRequest     = (SaneServletRequest) realRequest.getAttribute( SaneServletRequest.class.getName() );
        ServletContext             servletContext = getServletContext();
        HttpServletResponseWrapper childResponse  = new HttpServletResponseWrapper( realResponse );

        if( subject != null ) {
            realRequest.setAttribute( JeeViewlet.SUBJECT_ATTRIBUTE_NAME, subject );
        }

        String            servletPath = "/v/org/infogrid/jee/viewlet/meshbase/net/Proxy.jsp";
        RequestDispatcher dispatcher  = null;

        if( servletPath != null ) {
            dispatcher = app.findLocalizedRequestDispatcher( servletPath, lidRequest.acceptLanguageIterator(), servletContext );
        }

        try {
            dispatcher.include( realRequest, childResponse );

        } catch( Throwable t ) {
            app.reportProblem( t );
        }
    }
}
