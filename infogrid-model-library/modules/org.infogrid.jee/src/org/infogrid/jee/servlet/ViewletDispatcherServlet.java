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

import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.context.Context;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.DefaultRestfulRequest;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.jee.viewlet.JeeViewlet;
import org.infogrid.jee.viewlet.templates.JspStructuredResponseTemplate;
import org.infogrid.jee.viewlet.templates.StructuredResponse;
import org.infogrid.jee.viewlet.templates.StructuredResponseTemplate;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.model.traversal.TraversalDictionary;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.util.FactoryException;
import org.infogrid.util.logging.Log;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.MeshObjectsToView;



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
        InfoGridWebApp      app            = InfoGridWebApp.getSingleton();
        HttpServletRequest  realRequest    = (HttpServletRequest)  request;
        HttpServletResponse realResponse   = (HttpServletResponse) response;
        SaneServletRequest  saneRequest    = (SaneServletRequest)  request.getAttribute( SaneServletRequest.class.getName() );
        RestfulRequest      restfulRequest = createRestfulRequest( saneRequest, realRequest.getContextPath(), app.getDefaultMeshBase().getIdentifier().toExternalForm() );

        realRequest.setAttribute( RestfulRequest.class.getName(), restfulRequest );

        ServletContext     servletContext = getServletContext();
        StructuredResponse structured     = StructuredResponse.create( realResponse, servletContext );

        try {
            performService( restfulRequest, structured );
            
        } catch( Throwable ex ) {
            structured.reportProblem( ex );
        }

        try {
            StructuredResponseTemplate template = app.getStructuredResponseTemplateFactory().obtainFor( restfulRequest, structured );
            template.doOutput( realResponse, structured );

        } catch( FactoryException ex ) {
            throw new ServletException( ex );
        }
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
            RestfulRequest     restful,
            StructuredResponse structured )
        throws
            MeshObjectAccessException,
            CannotViewException,
            URISyntaxException,
            NotPermittedException,
            IllegalArgumentException,
            ServletException,
            IOException
    {
        InfoGridWebApp       app               = InfoGridWebApp.getSingleton();
        MeshObject           subject           = restful.determineRequestedMeshObject();
        MeshObjectIdentifier subjectIdentifier = restful.determineRequestedMeshObjectIdentifier();
        String               viewletClassName  = restful.getRequestedViewletClassName();
        String               traversalString   = restful.getRequestedTraversal();
        TraversalDictionary  traversalDict     = app.getTraversalDictionary();

        TraversalSpecification traversal = ( traversalDict != null ) ? traversalDict.translate( subject, traversalString ) : null;
        MeshObjectsToView      toView    = MeshObjectsToView.create( subject, null, viewletClassName, null, traversal );

        if( subject == null ) {
            throw new CannotViewException.NoSubject( subjectIdentifier );
        }

        Context        c              = app.getApplicationContext();
        JeeViewlet     viewlet        = null;

        if( subject != null ) {
            restful.getDelegate().setAttribute( JeeViewlet.SUBJECT_ATTRIBUTE_NAME, subject );

            try {
                viewlet = (JeeViewlet) app.getViewletFactory().obtainFor( toView, c );

            } catch( CannotViewException ex ) {
                throw ex; // pass on

            } catch( FactoryException ex ) {
                log.info( ex );
            }
        }

//        String            servletPath = null;
//        RequestDispatcher dispatcher  = null;
//
//        if( viewlet != null ) {
//            servletPath = viewlet.getServletPath();
//        }
//
//        if( servletPath != null ) {
//            dispatcher = app.findLocalizedRequestDispatcher(
//                    servletPath,
//                    restful.getSaneRequest().acceptLanguageIterator(),
//                    structured.getServletContext() );
//
//        } else if( viewlet != null ) {
//            log.error( "Viewlet " + viewlet + " returned null servletPath" );
//        }
//
//        if( dispatcher != null ) {
if( viewlet != null ) {
            // create a stack of Viewlets
            JeeViewlet oldViewlet = (JeeViewlet) restful.getDelegate().getAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME );
            restful.getDelegate().setAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME, viewlet );

            StructuredResponse oldStructuredResponse = (StructuredResponse) restful.getDelegate().getAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME );
            restful.getDelegate().setAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME, structured );

            boolean isSafePost   = false;
            boolean isUnsafePost = false;
            
            if( "POST".equalsIgnoreCase( restful.getSaneRequest().getMethod() )) {
                Boolean safeUnsafe = (Boolean) restful.getDelegate().getAttribute( SafeUnsafePostFilter.SAFE_UNSAFE_FLAG );
                if( safeUnsafe != null && !safeUnsafe.booleanValue() ) {
                    isUnsafePost = true;
                } else {
                    isSafePost = true;
                }
            }
            synchronized( viewlet ) {
                Throwable thrown  = null;
                try {
                    viewlet.view( toView );
                    if( isSafePost ) {                        
                        viewlet.performBeforeSafePost( restful, structured );
                    } else if( isUnsafePost ) {
                        viewlet.performBeforeUnsafePost( restful, structured );
                    } else {
                        viewlet.performBeforeGet( restful, structured );
                    }

                    viewlet.processRequest( restful, structured );

                } catch( RuntimeException t ) {
                    thrown = t;
                    throw (RuntimeException) thrown; // notice the finally block

                } catch( CannotViewException t ) {
                    thrown = t;
                    throw (CannotViewException) thrown; // notice the finally block

                } catch( ServletException t ) {
                    thrown = t;
                    throw (ServletException) thrown; // notice the finally block

                } catch( IOException t ) {
                    thrown = t;
                    throw (IOException) thrown; // notice the finally block

                } finally {
                    viewlet.performAfter( restful, structured, thrown );

                    restful.getDelegate().setAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME, oldViewlet );
                    restful.getDelegate().setAttribute( JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME, oldStructuredResponse );
                }
            }

        } else if( viewlet != null ) {
            throw new CannotViewException.InvalidViewlet( viewlet, toView );

        } else {
            throw new CannotViewException.NoViewletFound( toView );
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
    protected RestfulRequest createRestfulRequest(
            SaneServletRequest lidRequest,
            String             context,
            String             defaultMeshBaseIdentifier )
    {
        DefaultRestfulRequest ret = DefaultRestfulRequest.create(
                lidRequest,
                context,
                defaultMeshBaseIdentifier );
        return ret;
    }
}
