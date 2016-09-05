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

package org.infogrid.web.viewlet;

import java.io.IOException;
import java.util.Deque;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.ZeroElementCursorIterator;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.taglib.viewlet.IncludeViewletTag;
import org.infogrid.web.templates.StructuredResponse;

/**
 * Factors out common functionality of JSP-based Viewlets.
 */
public abstract class AbstractJspViewlet
        extends
            AbstractWebViewlet
{
    private static final Log log = Log.getLogInstance( AbstractJspViewlet.class ); // our own, private logger

    /**
     * Constructor, for subclasses only.
     *
     * @param viewletName the computable name of the Viewlet
     * @param servletClass the Servlet implementing this Viewlet
     * @param viewed the JeeViewedMeshObjects to use
     * @param c the application context
     */
    protected AbstractJspViewlet(
            String                   viewletName,
            Class<? extends Servlet> servletClass,
            WebViewedMeshObjects     viewed,
            Context                  c )
    {
        super( viewed, c );

        theName         = viewletName;
        theServletClass = servletClass;
    }

    /**
     * Obtain the Html class name for this Viewlet. By default, it is the Java class
     * name, having replaced all periods with hyphens.
     *
     * @return the HTML class name
     */
    @Override
    public String getHtmlClass()
    {
        String ret = theServletClass.getName();

        ret = ret.replaceAll( "\\.", "-" );

        return ret;
    }

    /**
     * Obtain the computable name of the Viewlet.
     *
     * @return the Viewet's name
     */
    @Override
    public String getName()
    {
        return theName;
    }

    /**
     * Obtain a String, to be shown to the user, that identifies this Viewlet to the user.
     * This default implementation can be overridden by subclasses.
     *
     * @return a String
     */
    @Override
    public String getUserVisibleName()
    {
        ResourceHelper rh = ResourceHelper.getInstance( theServletClass );
        
        String userVisibleName = rh.getResourceStringOrDefault("UserVisibleName", theServletClass.getName() );
        return userVisibleName;
    }

    /**
     * Obtain the default URL to which forms should be HTTP POSTed.
     *
     * @return the URL
     */
    @Override
    public String getPostUrl()
    {
        @SuppressWarnings("unchecked")
        Deque<WebViewedMeshObjects> parentViewedStack = (Deque<WebViewedMeshObjects>) theCurrentRequest.getAttribute( IncludeViewletTag.PARENT_STACK_ATTRIBUTE_NAME );

        WebMeshObjectsToView currentlyToView = getViewedMeshObjects().getMeshObjectsToView();
        WebMeshObjectsToView newToView       = currentlyToView.createCopy();

        return getPostUrl( parentViewedStack, newToView );
    }

    /**
     * Process the incoming request. Default implementation that can be
     * overridden by subclasses.
     * 
     * @param request the incoming request
     * @param response the StructuredResponse into which to write the result
     * @param servletContext the ServletContext
     * @throws ServletException processing failed
     * @throws IOException I/O error
     */
    @Override
    public void processRequest(
            SaneServletRequest request,
            StructuredResponse response,
            ServletContext     servletContext )
        throws
            ServletException,
            IOException
    {
        synchronized( this ) {
            if( theCurrentRequest != null ) {
                throw new IllegalStateException( "Have current request already: " + theCurrentRequest );
            }
            theCurrentRequest = request;
        }
        
        Servlet     servlet = null;
        ClassLoader old    = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( theServletClass.getClassLoader() );
 
            servlet = theServletClass.newInstance();
            servlet.init( new MyServletConfig( theServletClass.getName(), servletContext ));
            
            servlet.service( request, response );

        } catch( InstantiationException ex ) {
            log.error( ex );

        } catch( IllegalAccessException ex ) {
            log.error( ex );

        } finally {
            Thread.currentThread().setContextClassLoader( old );

            if( servlet != null ) {
                servlet.destroy();
            }
            synchronized( this ) {
                theCurrentRequest = null;
            }
        }        
    }

    /**
     * The computable name of the Viewlet.
     */
    protected String theName;

    /**
     * The Servlet class that implements this Viewlet.
     */
    protected Class<? extends Servlet> theServletClass;

    /**
     * The request currently being processed.
     */
    protected SaneRequest theCurrentRequest;

    /**
     * We use this as the ServletConfig object.
     */
    static class MyServletConfig
            implements
                ServletConfig
    {
        /**
         * Constructor.
         * 
         * @param name name of this servlet instance
         * @param servletContext the ServletContext
         */
        public MyServletConfig(
                String         name,
                ServletContext servletContext )
        {
            theName           = name;
            theServletContext = servletContext;
        }

        @Override
        public String getServletName()
        {
            return theName;
        }

        @Override
        public ServletContext getServletContext()
        {
            return theServletContext;
        }

        @Override
        public String getInitParameter(
                String name )
        {
            return null;
        }

        @Override
        public Enumeration<String> getInitParameterNames()
        {
            return ZeroElementCursorIterator.create();
        }
        
        protected String theName;
        protected ServletContext theServletContext;
    }}
