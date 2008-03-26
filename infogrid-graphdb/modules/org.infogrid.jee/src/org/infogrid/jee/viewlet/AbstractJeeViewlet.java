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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.context.Context;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.model.traversal.TraversalDictionary;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.util.http.HTTP;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.DefaultViewedMeshObjects;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewedMeshObjects;

/**
 * Factors out commonly used functionality for Viewlets.
 */
public abstract class AbstractJeeViewlet
        implements
            JeeViewlet
{
    /**
     * Constructor, for subclasses only.
     * 
     * @param c the application context
     */
    protected AbstractJeeViewlet(
            Context c )
    {
        theContext = c;
    }
    
    /**
      * Obtain a String, to be shown to the user, that identifies this Viewlet to the user.
      *
      * @return a String
      */
    public String getUserVisibleName()
    {
        return getClass().getName();
    }

    /**
      * The Viewlet is being instructed to view certain objects, which are packaged as MeshObjectsToView.
      *
      * @param toView the MeshObjects to view
      * @throws CannotViewException thrown if this Viewlet cannot view these MeshObjects
      */
    public void view(
            MeshObjectsToView toView )
        throws
            CannotViewException
    {
        theViewedMeshObjects.updateFrom( toView );
    }
    
    /**
      * Set the REST-ful subject for this Viewlet. This is a simplified version of {@link #view( MeshObjectsToView )}.
      *
      * @param toView the MeshObject to view
      * @throws CannotViewException thrown if this Viewlet cannot view this MeshObject
      */
    public void setSubject(
            MeshObject toView )
        throws
            CannotViewException
    {
        view( MeshObjectsToView.create( toView ));
    }
    
    /**
     * Obtain the REST-ful subject.
     *
     * @return the subject
     */
    public MeshObject getSubject()
    {
        return theViewedMeshObjects.getSubject();
    }

    /**
     * Obtain the Objects.
     * 
     * @return the set of Objects, which may be empty
     */
    public MeshObjectSet getObjects()
    {
        return theViewedMeshObjects.getObjects();
    }

    /**
      * Obtain the MeshObjects that this Viewlet is currently viewing, plus
      * context information. This method will return the same instance of ViewedMeshObjects
      * during the lifetime of the Viewlet.
      *
      * @return the ViewedMeshObjects
      */
    public ViewedMeshObjects getViewedObjects()
    {
        return theViewedMeshObjects;
    }
    
    /**
     * Obtain the application context.
     *
     * @return the application context
     */
    public final Context getContext()
    {
        return theContext;
    }

    /**
     * <p>Invoked prior to the execution of the Servlet. It is the hook by which
     * the JeeViewlet can perform whatever operations needed prior to the execution of the servlet, e.g.
     * the evaluation of POST commands. Subclasses will often override this.</p>
     * 
     * @param context the ServletContext
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performAfter
     */
    public void performBefore(
            ServletContext      context,
            HttpServletRequest  request,
            HttpServletResponse response )
        throws
            ServletException
    {
        // noop on this level
    }

    /**
     * <p>Invoked after to the execution of the Servlet. It is the hook by which
     * the JeeViewlet can perform whatever operations needed after to the execution of the servlet, e.g.
     * logging. Subclasses will often override this.</p>
     * 
     * @param context the ServletContext
     * @param request the incoming request
     * @param response the response to be assembled
     * @param thrown if this is non-null, it is the Throwable indicating a problem that occurred
     *        either during execution of performBefore or of the servlet.
     * @throws ServletException thrown if an error occurred
     * @see #performBefore
     */
    public void performAfter(
            ServletContext      context,
            HttpServletRequest  request,
            HttpServletResponse response,
            Throwable           thrown )
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
            HttpServletRequest newRequest )
    {
        theCurrentRequest = newRequest;
    }

    /**
     * Obtain the Xpath element.
     *
     * @return the Xpath element
     */
    public String getLidXpath()
    {
        MeshObject             subject   = theViewedMeshObjects.getSubject();
        TraversalSpecification traversal = theViewedMeshObjects.getTraversalSpecification();
        InfoGridWebApp         app       = InfoGridWebApp.getSingleton();
        TraversalDictionary    dict      = app.getTraversalDictionary();

        String ret = dict.translate( subject, traversal );
        return ret;
    }
    
    /**
     * Obtain the URL to which forms should be HTTP post'd. This
     * can be overridden by subclasses.
     *
     * @return the URL
     */
    public String getPostUrl()
    {
        String relativePath  = theCurrentRequest.getRequestURI();

        // we need to replace # -- FIXME, is this right? HTTP.encodeUrl does seem to do too much
        String ret = relativePath.replace( "#", "%23" );
        
        // append lid-xpath
        String xpath = theCurrentRequest.getParameter( "lid-xpath" );
        if( xpath != null ) {
            ret = HTTP.appendArgumentToUrl( ret, "lid-xpath=" + HTTP.encodeUrl( xpath ));
        }
        // append lid-format
        String format = theCurrentRequest.getParameter( "lid-format" );
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
     * The objects being viewed.
     */
    protected DefaultViewedMeshObjects theViewedMeshObjects = new DefaultViewedMeshObjects( this );

    /**
     * The application context
     */
    protected Context theContext;
    
    /**
     * The request currently being processed.
     */
    protected HttpServletRequest theCurrentRequest;
}
