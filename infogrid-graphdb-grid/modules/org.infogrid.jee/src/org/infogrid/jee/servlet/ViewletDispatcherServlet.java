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

import org.infogrid.context.Context;

import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.DefaultRestfulRequest;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.jee.viewlet.JeeViewlet;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.util.FactoryException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.MeshObjectsToView;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * <p>Main JeeViewlet dispatcher to determine the REST subject, the best JeeViewlet, and
 *    the best available localization.</p>
 * </p> This may be subclassed by applications.</p>
 */
public class ViewletDispatcherServlet
        extends
            GenericServlet
{
    private static final Log log = Log.getLogInstance( ViewletDispatcherServlet.class ); // our own, private logger

    /**
     * Main servlet method.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @throws ServletException thrown if an error occurred
     * @throws IOException thrown if an I/O error occurred
     */
    public final void service(
            ServletRequest  request,
            ServletResponse response )
        throws
            ServletException,
            IOException
    {
        HttpServletRequest  realRequest    = (HttpServletRequest)  request;
        HttpServletResponse realResponse   = (HttpServletResponse) response;
        SaneServletRequest  saneRequest    = (SaneServletRequest) request.getAttribute( SaneServletRequest.class.getName() );
        RestfulRequest      restfulRequest = createRestfulRequest( saneRequest, realRequest.getContextPath() );

        realRequest.setAttribute( RestfulRequest.class.getName(), restfulRequest );

        performService( realRequest, realResponse );
    }

    /**
     * Do the work. This may be overridden by subclasses.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @throws ServletException thrown if an error occurred
     * @throws IOException thrown if an I/O error occurred
     */
    protected void performService(
            HttpServletRequest  realRequest,
            HttpServletResponse realResponse )
        throws
            ServletException,
            IOException
    {
        InfoGridWebApp app            = InfoGridWebApp.getSingleton();
        RestfulRequest restfulRequest = (RestfulRequest) realRequest.getAttribute( RestfulRequest.class.getName() );

        try {
            MeshObject           subject           = restfulRequest.determineRequestedMeshObject();
            MeshObjectIdentifier subjectIdentifier = restfulRequest.determineRequestedMeshObjectIdentifier();

            showInViewlet( subject, subjectIdentifier, realRequest, realResponse );

        } catch( MeshObjectAccessException ex ) {
            app.reportProblem( ex );

        } catch( URISyntaxException ex ) {
            app.reportProblem( ex );
        }
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
            MeshObject           subject,
            MeshObjectIdentifier subjectIdentifier,
            HttpServletRequest   realRequest,
            HttpServletResponse  realResponse  )
        throws
            ServletException
    {
        InfoGridWebApp             app            = InfoGridWebApp.getSingleton();
        Context                    c              = app.getApplicationContext();
        SaneServletRequest         lidRequest     = (SaneServletRequest) realRequest.getAttribute( SaneServletRequest.class.getName() );
        ServletContext             servletContext = getServletContext();
        HttpServletResponseWrapper childResponse  = new HttpServletResponseWrapper( realResponse );
        MeshObjectsToView          toView         = null;
        JeeViewlet                 viewlet        = null;

        if( subject != null ) {
            realRequest.setAttribute( JeeViewlet.SUBJECT_ATTRIBUTE_NAME, subject );

            try {
                // Look for the lid-format string
                String lidFormat        = realRequest.getParameter( "lid-format" );
                String viewletClassName = null;

                if( lidFormat != null && lidFormat.startsWith( VIEWLET_PREFIX )) {
                    viewletClassName = lidFormat.substring( VIEWLET_PREFIX.length() );
                }

                toView = MeshObjectsToView.create( subject, viewletClassName );

                viewlet = (JeeViewlet) app.getViewletFactory().obtainFor( toView, c );

            } catch( FactoryException ex ) {
                log.info( ex );
            }
        }

        String            servletPath = null;
        RequestDispatcher dispatcher  = null;

        if( viewlet != null ) {
            servletPath = viewlet.getServletPath();
        }

        if( servletPath != null ) {
            dispatcher = app.findLocalizedRequestDispatcher( servletPath, lidRequest.acceptLanguageIterator(), servletContext );
        } else if( viewlet != null ) {
            log.error( "Viewlet " + viewlet + " returned null servletPath" );
        }

        if( subject == null ) {
            app.reportProblem( new CannotViewException.NoSubject( subjectIdentifier ));

        } else if( dispatcher != null ) {

            // create a stack of Viewlets
            JeeViewlet oldViewlet = (JeeViewlet) realRequest.getAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME );
            realRequest.setAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME, viewlet );

            Throwable thrown = null;

            synchronized( viewlet ) {
                try {
                    viewlet.setSubject( subject );
                    viewlet.performBefore( servletContext, realRequest, childResponse );

                    viewlet.setCurrentRequest( realRequest );

                    dispatcher.include( realRequest, childResponse );

                } catch( Throwable t ) {
                    thrown = t;

                    app.reportProblem( t );

                } finally {
                    viewlet.performAfter( servletContext, realRequest, childResponse, thrown );
                    realRequest.setAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME, oldViewlet );
                }
            }

        } else if( viewlet != null ) {
            app.reportProblem( new CannotViewException.InvalidViewlet( viewlet, toView ));

        } else {
            app.reportProblem( new CannotViewException.NoViewletFound( toView ));
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
    protected RestfulRequest createRestfulRequest(
            SaneServletRequest lidRequest,
            String             context )
    {
        DefaultRestfulRequest ret = DefaultRestfulRequest.create(
                lidRequest,
                context );
        return ret;
    }

    /**
     * The prefix in the lid-format string that indicates the name of a viewlet.
     */
    public static final String VIEWLET_PREFIX = "viewlet:";

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( ViewletDispatcherServlet.class );
}
