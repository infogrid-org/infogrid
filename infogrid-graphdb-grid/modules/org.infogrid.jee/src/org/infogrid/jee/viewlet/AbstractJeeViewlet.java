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

package org.infogrid.jee.viewlet;

import javax.servlet.ServletException;
import org.infogrid.context.Context;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.viewlet.templates.StructuredResponse;
import org.infogrid.util.http.HTTP;
import org.infogrid.viewlet.AbstractViewedMeshObjects;
import org.infogrid.viewlet.AbstractViewlet;

/**
 * Factors out commonly used functionality for JeeViewlets.
 */
public abstract class AbstractJeeViewlet
        extends
            AbstractViewlet
        implements
            JeeViewlet
{
    /**
     * Constructor, for subclasses only.
     * 
     * @param viewed the AbstractViewedMeshObjects implementation to use
     * @param c the application context
     */
    protected AbstractJeeViewlet(
            AbstractViewedMeshObjects viewed,
            Context                   c )
    {
        super( viewed, c );
    }
    
    /**
     * <p>Invoked prior to the execution of the Servlet if the GET method has been requested.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the GET execution of the servlet.</p>
     * <p>Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeSafePost
     * @see #performBeforeUnsafePost
     * @see #performAfter
     */
    public void performBeforeGet(
            RestfulRequest     request,
            StructuredResponse response )
        throws
            ServletException
    {
        // no op on this level
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and the FormTokenService determined that the incoming POST was safe.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the POST execution of the servlet, e.g. the evaluation of POST commands.</p>
     * <p>Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeUnsafePost
     * @see #performAfter
     */
    public void performBeforeSafePost(
            RestfulRequest     request,
            StructuredResponse response )
        throws
            ServletException
    {
        // no op on this level
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and the FormTokenService determined that the incoming POST was <b>not</b> safe.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the GET execution of the servlet.</p>
     * <p>It is strongly recommended that JeeViewlets do not regularly process the incoming
     *    POST data, as the request is likely unsafe (e.g. a Cross-Site Request Forgery).</p>
     * <p>Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeSafePost
     * @see #performAfter
     */
    public void performBeforeUnsafePost(
            RestfulRequest     request,
            StructuredResponse response )
        throws
            ServletException
    {
        throw new ServletException( "Unsafe POST" ); // FIXME what about better error reporting ;-)
    }

    /**
     * <p>Invoked after to the execution of the Servlet. It is the hook by which
     * the JeeViewlet can perform whatever operations needed after to the execution of the servlet, e.g.
     * logging. Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @param thrown if this is non-null, it is the Throwable indicating a problem that occurred
     *        either during execution of performBefore or of the servlet.
     * @throws ServletException thrown if an error occurred
     * @see #performBefore
     */
    public void performAfter(
            RestfulRequest     request,
            StructuredResponse response,
            Throwable          thrown )
        throws
            ServletException
    {
        // noop on this level
    }

    /**
     * Obtain the path to the Servlet for this JeeViewlet. JeeViewlet may implement this in different ways,
     * such as be returning a path to a JSP. This returns the generic path, not a localized version.
     * The localized version is constructed by the caller from its information about preferred Locales.
     * 
     * @return the Servlet path
     */
    public String getServletPath()
    {
        String ret = constructDefaultDispatcherUrl( getClass() );
        return ret;
    }
    
    /**
     * Set the current request.
     *
     * @param newRequest the new request
     */
    public void setCurrentRequest(
            RestfulRequest newRequest )
    {
        theCurrentRequest = newRequest;
    }

//    /**
//     * Obtain the TraversalSpecification, if any
//     *
//     * @return the Xpath element
//     */
//    public String getLidXpath()
//    {
//        MeshObject             subject   = theViewedMeshObjects.getSubject();
//        TraversalSpecification traversal = theViewedMeshObjects.getTraversalSpecification();
//        InfoGridWebApp         app       = InfoGridWebApp.getSingleton();
//        TraversalDictionary    dict      = app.getTraversalDictionary();
//
//        String ret = dict.translate( subject, traversal );
//        return ret;
//    }
    
    /**
     * Obtain the URL to which forms should be HTTP post'd. This
     * can be overridden by subclasses.
     *
     * @return the URL
     */
    public String getPostUrl()
    {
        String relativePath  = theCurrentRequest.getDelegate().getRequestURI();

        // we need to replace # -- FIXME, is this right? HTTP.encodeUrl does seem to do too much
        String ret = relativePath.replace( "#", "%23" );
        
        // append lid-xpath
        String xpath = theCurrentRequest.getDelegate().getParameter( "lid-xpath" );
        if( xpath != null ) {
            ret = HTTP.appendArgumentToUrl( ret, "lid-xpath=" + HTTP.encodeUrl( xpath ));
        }
        // append lid-format
        String format = theCurrentRequest.getDelegate().getParameter( "lid-format" );
        if( format != null ) {
            ret = HTTP.appendArgumentToUrl( ret, "lid-format=" + HTTP.encodeUrl( format ));
        }
        return ret;
    }

    /**
     * This method converts a Class (subclass of this one) into the default request URL
     * for the RequestDispatcher.
     * 
     * 
     * @param viewletClass the class of the JeeViewlet
     * @return the JSP URL.
     */
    protected static String constructDefaultDispatcherUrl(
            Class viewletClass )
    {
        String viewletClassName = viewletClass.getName();
        StringBuilder almost = new StringBuilder();
        almost.append( "/v/" ).append( viewletClassName.replace( '.', '/' )).append( ".jsp" );
        return almost.toString();
    }

    /**
     * The request currently being processed.
     */
    protected RestfulRequest theCurrentRequest;
}
