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

import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.rest.net.DefaultNetRestfulRequest;
import org.infogrid.jee.rest.net.NetRestfulRequest;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.jee.servlet.ViewletDispatcherServlet;
import org.infogrid.jee.viewlet.JeeViewlet;
import org.infogrid.jee.viewlet.templates.JspStructuredResponseTemplate;
import org.infogrid.jee.viewlet.templates.StructuredResponse;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.util.logging.Log;
import org.infogrid.viewlet.CannotViewException;


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
            RestfulRequest     restful,
            StructuredResponse structured )
        throws
            MeshObjectAccessException,
            CannotViewException,
            URISyntaxException,
            IllegalArgumentException,
            ServletException,
            IOException
    {
        NetRestfulRequest realRestful    = (NetRestfulRequest) restful;
        InfoGridWebApp    app            = InfoGridWebApp.getSingleton();

        NetMeshBaseIdentifier proxyIdentifier = realRestful.determineRequestedProxyIdentifier();
        if( proxyIdentifier == null ) {
            // not trying to show a Proxy
            super.performService( restful, structured );
            
            return;
        }
                
        Proxy p = realRestful.determineRequestedProxy();
        if( p == null ) {
            // not finding the proxy
            new IllegalArgumentException( "Cannot find specified Proxy with identifier " + proxyIdentifier );
        }

        // showing a Proxy
        restful.getDelegate().setAttribute( JeeViewlet.SUBJECT_ATTRIBUTE_NAME, p );

        String            servletPath = "/v/org/infogrid/jee/viewlet/meshbase/net/Proxy.jsp";
        RequestDispatcher dispatcher  = app.findLocalizedRequestDispatcher( servletPath, restful.getSaneRequest().acceptLanguageIterator(), getServletContext() );

        StructuredResponse oldStructuredResponse = (StructuredResponse) restful.getDelegate().getAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME );
        restful.getDelegate().setAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME, structured );

        try {
            runRequestDispatcher( dispatcher, restful, structured );
            
        } finally {
            restful.getDelegate().setAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME, oldStructuredResponse );            
        }
    }

    /**
     * Construct a RestfulRequest object that is suitable to the URL-to-MeshObject mapping
     * applied by this application.
     *
     * @param lidRequest the incoming request
     * @param context the context path of the application
     * @param defaultMeshBaseIdentifier String form of the identifier of the default MeshBase
     * @return the created RestfulRequest
     */
    @Override
    protected NetRestfulRequest createRestfulRequest(
            SaneServletRequest lidRequest,
            String             context,
            String             defaultMeshBaseIdentifier )
    {
        DefaultNetRestfulRequest ret = DefaultNetRestfulRequest.create(
                lidRequest,
                context,
                defaultMeshBaseIdentifier );
        return ret;
    }
}
