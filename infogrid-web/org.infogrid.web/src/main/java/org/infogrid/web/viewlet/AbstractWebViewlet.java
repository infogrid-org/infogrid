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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.viewlet.AbstractViewlet;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.security.UnsafePostException;
import org.infogrid.web.taglib.viewlet.IncludeViewletTag;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.TextStructuredResponseSection;

/**
 * Factors out commonly used functionality for WebViewlets.
 */
public abstract class AbstractWebViewlet
        extends
            AbstractViewlet
        implements
            WebViewlet
{
    /**
     * Constructor, for subclasses only.
     * 
     * @param viewed the JeeViewedMeshObjects to use
     * @param c the application context
     */
    protected AbstractWebViewlet(
            WebViewedMeshObjects viewed,
            Context              c )
    {
        super( viewed, c );
    }

    /**
      * Obtain the MeshObjects that this Viewlet is currently viewing, plus
      * context information. This method will return the same instance of ViewedMeshObjects
      * during the lifetime of the Viewlet.
      *
      * @return the ViewedMeshObjects
      */
    @Override
    public WebViewedMeshObjects getViewedMeshObjects()
    {
        return (WebViewedMeshObjects) super.getViewedMeshObjects();
    }

    /**
     * Obtain all possible states of this Viewlet. This may depend on the current MeshObjectsToView
     * (e.g. whether the user may edit a MeshObject or not).
     *
     * @return the possible ViewletStates
     */
    @Override
    public JeeViewletState [] getPossibleViewletStates()
    {
        // FIXME: should take MeshObject access rights into account
        return DefaultWebViewletStateEnum.values();
    }

    /**
     * The current JeeViewletState.
     *
     * @return the current state
     */
    @Override
    public JeeViewletState getViewletState()
    {
        return getViewedMeshObjects().getViewletState();
    }

    /**
     * Obtain the Html class name for this Viewlet that will be used for the enclosing <tt>div</tt> tag.
     * By default, it is the Java class name, having replaced all periods with hyphens.
     * 
     * @return the HTML class name
     */
    @Override
    public String getHtmlClass()
    {
        String ret = getClass().getName();

        ret = ret.replaceAll( "\\.", "-" );
        
        return ret;
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the GET method has been requested.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the GET execution of the servlet.</p>
     * <p>Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @return if true, the result of the viewlet processing has been deposited into the response object
     *         already and regular processing will be skipped. If false, regular processing continues.
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeSafePost
     * @see #performBeforeUnsafePost
     * @see #performAfter
     */
    @Override
    public boolean performBeforeGet(
            SaneRequest        request,
            StructuredResponse response )
        throws
            ServletException
    {
        // no op on this level
        return false;
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and the SafeUnsafePostFilter determined that the incoming POST was safe.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the POST execution of the servlet, e.g. the evaluation of POST commands.</p>
     * <p>Subclasses will often override this.</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @return if true, the result of the viewlet processing has been deposited into the response object
     *         already and regular processing will be skipped. If false, regular processing continues.
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeUnsafePost
     * @see #performAfter
     */
    @Override
    public boolean performBeforeSafePost(
            SaneRequest        request,
            StructuredResponse response )
        throws
            ServletException
    {
        return defaultPerformPost( request, response );
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and the SafeUnsafePostFilter determined that the incoming POST was <b>not</b> safe.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the POST execution of the servlet.</p>
     * <p>It is strongly recommended that JeeViewlets do not regularly process the incoming
     *    POST data, as the request is likely unsafe (e.g. a Cross-Site Request Forgery).</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @return if true, the result of the viewlet processing has been deposited into the response object
     *         already and regular processing will be skipped. If false, regular processing continues.
     * @throws UnsafePostException thrown if the unsafe POST operation was not acceptable
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeSafePost
     * @see #performAfter
     */
    @Override
    public boolean performBeforeUnsafePost(
            SaneRequest        request,
            StructuredResponse response )
        throws
            UnsafePostException,
            ServletException
    {
        throw new UnsafePostException( request );
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and no SafeUnsafePostFilter has been used.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the POST execution of the servlet.</p>
     * <p>It is strongly recommended that JeeViewlets do not regularly process the incoming
     *    POST data, as the request is likely unsafe (e.g. a Cross-Site Request Forgery).</p>
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @return if true, the result of the viewlet processing has been deposited into the response object
     *         already and regular processing will be skipped. If false, regular processing continues.
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeSafePost
     * @see #performAfter
     */
    @Override
    public boolean performBeforeMaybeSafeOrUnsafePost(
            SaneRequest        request,
            StructuredResponse response )
        throws
            ServletException
    {
        return defaultPerformPost( request, response );
    }
    
    /**
     * Default implementation of what happens upon POST.
     *
     * @param request the incoming request
     * @param response the response to be assembled
     * @return if true, the result of the viewlet processing has been deposited into the response object
     *         already and regular processing will be skipped. If false, regular processing continues.
     * @throws ServletException thrown if an error occurred
     */
    protected boolean defaultPerformPost(
            SaneRequest        request,
            StructuredResponse response )
        throws
            ServletException
    {
        if( !response.haveProblemsBeenReported() ) {
            // only if no errors have been reported
            response.setHttpResponseCode( 303 );

            String target = request.getUrlArgument( "lid-target" );
            if( target == null ) {
                target = request.getAbsoluteFullUri();
            }
            response.setLocation( target );
            return true;
        } else {
            return false;
        }
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
     * @see #performBeforeGet
     * @see #performBeforeSafePost
     * @see #performBeforeUnsafePost
     */
    @Override
    public void performAfter(
            SaneRequest        request,
            StructuredResponse response,
            Throwable          thrown )
        throws
            ServletException
    {
        // if no HTML title was set but it's a non-binary html response, set one

        if( response.isStructuredEmpty() ) {
            return;
        }
        TextStructuredResponseSection titleSection = response.obtainTextSection( StructuredResponse.HTML_TITLE_SECTION );
        if( titleSection.isEmpty() ) {
            InfoGridWebApp app = getContext().findContextObjectOrThrow(InfoGridWebApp.class );

            String name                     = getName();
            String userVisibleName          = getUserVisibleName();
            String subjectIdentifierString  = getSubject().getIdentifier().toExternalForm();
            String subjectUserVisibleString = getSubject().getUserVisibleString();
            String appName                  = app.getName();
            String appUserVisibleName       = app.getUserVisibleName();

            if( name == null ) {
                name = "";
            }
            if( userVisibleName == null ) {
                userVisibleName = "";
            }
            if( subjectUserVisibleString == null ) {
                subjectUserVisibleString = "";
            }

            String prefix;
            if( thrown == null && !response.haveProblemsBeenReportedAggregate() ) {
                prefix = "Default";
            } else {
                prefix = "Error";
            }
            String content;
            if( appName != null ) {

                if( appUserVisibleName == null ) {
                    appUserVisibleName = appName;
                }

                content = theResourceHelper.getResourceStringWithArguments(
                        prefix + "TitleWithApp",
                /* 0 */ name,
                /* 1 */ userVisibleName,
                /* 2 */ subjectIdentifierString,
                /* 3 */ subjectUserVisibleString,
                /* 4 */ appName,
                /* 5 */ appUserVisibleName );
            } else {
                content = theResourceHelper.getResourceStringWithArguments(
                        prefix + "TitleWithoutApp",
                /* 0 */ name,
                /* 1 */ userVisibleName,
                /* 2 */ subjectIdentifierString,
                /* 3 */ subjectUserVisibleString );
            }

            titleSection.setContent( content );
        }
    }

    /**
     * Obtain the URL to which forms should be HTTP POSTed.
     * By default, that is the URL of the current Viewlet, but in the "top" pane rather than the "here" pane,
     * concatenated with a lid-target argument with the value of the URL of the current Viewlet, in the "here" pane.
     * The Viewlet state is also set to VIEW.
     * This can be overridden by subclasses.
     *
     * @param viewedMeshObjectsStack the Stack of ViewedMeshObjects of the parent Viewlets, if any
     * @param toView the MeshObjectsToView upon post
     * @return the URL
     */
    @Override
    public String getPostUrl(
            Deque<WebViewedMeshObjects> viewedMeshObjectsStack,
            WebMeshObjectsToView        toView )
    {
        toView.setViewletState( DefaultWebViewletStateEnum.VIEW );

        StringBuilder buf = new StringBuilder();
        buf.append( toView.getAsUrl( (Deque<WebViewedMeshObjects>) null ));
        if( viewedMeshObjectsStack != null && !viewedMeshObjectsStack.isEmpty() ) {
            HTTP.replaceOrAppendArgumentToUrl( buf, "lid-target", toView.getAsUrl( viewedMeshObjectsStack ));
        }
        if( toView.getViewletTypeName() != null ) {
            HTTP.replaceOrAppendArgumentToUrl( buf, WebMeshObjectsToView.LID_FORMAT_ARGUMENT_NAME, WebMeshObjectsToView.VIEWLET_PREFIX + toView.getViewletTypeName() );
        }
        if( toView.getMimeType() != null ) {
            HTTP.replaceOrAppendArgumentToUrl( buf, WebMeshObjectsToView.LID_FORMAT_ARGUMENT_NAME, WebMeshObjectsToView.MIME_PREFIX + toView.getMimeType() );
        }
        return buf.toString();
    }

    /**
     * The ResourceHelper for this class.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance(AbstractWebViewlet.class );
}
