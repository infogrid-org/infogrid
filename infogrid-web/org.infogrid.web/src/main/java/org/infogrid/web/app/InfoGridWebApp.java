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

package org.infogrid.web.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Deque;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.app.AppConfiguration;
import org.infogrid.app.InfoGridApp;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.model.primitives.text.ModelPrimitivesStringRepresentationDirectorySingleton;
import org.infogrid.model.traversal.TraversalTranslator;
import org.infogrid.model.traversal.xpath.XpathTraversalTranslator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentationDirectory;
import org.infogrid.util.text.StringRepresentationDirectorySingleton;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.DefaultViewletFactory;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.web.ProblemReporter;
import org.infogrid.web.ServletExceptionWithHttpStatusCode;
import org.infogrid.web.httpshell.HttpShell;
import org.infogrid.web.rest.RestfulJeeFormatter;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.security.CsrfMitigator;
import org.infogrid.web.security.DefaultCsrfMitigator;
import org.infogrid.web.security.UnsafePostException;
import org.infogrid.web.taglib.viewlet.IncludeViewletTag;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.viewlet.DefaultWebMeshObjectsToViewFactory;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.viewlet.WebMeshObjectsToViewFactory;
import org.infogrid.web.viewlet.WebViewlet;

/**
 * The superclass of all InfoGridWebApps. May also be used directly.
 */
public class InfoGridWebApp
    extends
        InfoGridApp
{
    private static Log log = null; // our own, private logger; do not initialize here

    /**
     * This constructor can be used directly, or the class may be subclassed.
     */
    public InfoGridWebApp()
    {
    }

    /**
     * Invoked by the InfoGrid daemon, run the various initialization methods with
     * the configuration options provided.
     * 
     * @param config the configuration options
     */
    @Override
    public void initialize(
            AppConfiguration config )
    {
        initializeResourceHelper();

        registerResources( config );
        initializeModels( config );
        initializeMeshBase( config );
        initializeGraph( config );
        initializeUi( (WebAppConfiguration) config );        
    }
    
    /**
     * Helper method to activate the application-level ResourceHelper.
     */
    private void initializeResourceHelper()
    {
        try {
            ResourceHelper.setApplicationResourceBundle(
                    ResourceBundle.getBundle( getClass().getName(), Locale.getDefault(), getClass().getClassLoader() ));
            
            ResourceHelper.initializeLogging();

            log = Log.getLogInstance( getClass() );
            
        } catch( Throwable ex ) {
            ResourceHelper.initializeLogging();
            
            log.error( ex );
        }
    }

    /**
     * Obtain the StringRepresentationDirectory used by this app.
     * 
     * @return the StringRepresentationDirectory
     */
    public StringRepresentationDirectory getStringRepresentationDirectory()
    {
        return theStringRepresentationDirectory;
    }

    /**
     * Obtain the WebAppResourceManager.
     * 
     * @return the WebAppResourceManager
     */
    public WebAppResourceManager getResourceManager()
    {
        return theResourceManager;
    }
                        
    /**
     * Obtain the ViewletFactory.
     * 
     * @return the ViewletFactory
     */
    public ViewletFactory getViewletFactory()
    {
        return theViewletFactory;
    }

    /**
     * Obtain the CsrfMitigator.
     * 
     * @return the CsrfMitiator, if any
     */
    public CsrfMitigator getCsrfMitigator()
    {
        return theCsrfMitigator;
    }

    /**
     * Overridable method to initialize available models with this app.
     * 
     * @param config the configuration options
     */
    protected void initializeModels(
            AppConfiguration config )
    {
        
    }

    /**
     * Overridable method to initialize the object graph with this app.
     * 
     * @param config the configuration options
     */
    protected void initializeGraph(
            AppConfiguration config )
    {
        // no op at this level
    }

    /**
     * Overridable method to register available resources with this app.
     * 
     * @param config the configuration options
     */
    protected void registerResources(
            AppConfiguration config )
    {
        // nothing on this level
    }
    
    /**
     * Overridable method to initialize the user interface.
     * 
     * @param config the configuration options
     */
    protected void initializeUi(
            WebAppConfiguration config )
    {
        theViewletFactory = DefaultViewletFactory.create( WebViewlet.class.getName() );
        theRootContext.addContextObject( theViewletFactory );
        
        theTraversalTranslator = XpathTraversalTranslator.create( theMeshBase );
        theRootContext.addContextObject( theTraversalTranslator );

        theToViewFactory = DefaultWebMeshObjectsToViewFactory.create(
                theMeshBase.getIdentifier(),
                DefaultMeshBaseIdentifierFactory.create(),
                theMeshBaseNameServer,
                theTraversalTranslator,
                config.getAppContextPath(),
                theRootContext );
        theRootContext.addContextObject( theToViewFactory );

        ModelPrimitivesStringRepresentationDirectorySingleton.initialize();

        theStringRepresentationDirectory = StringRepresentationDirectorySingleton.getSingleton();
        theRootContext.addContextObject( theStringRepresentationDirectory );

        RestfulJeeFormatter formatter = RestfulJeeFormatter.create( theMeshBase, theStringRepresentationDirectory );
        theRootContext.addContextObject( formatter );

        theResourceManager = DefaultWebAppResourceManager.create( formatter );

        theShell = HttpShell.create( this );
        
        theCsrfMitigator = new DefaultCsrfMitigator();
    }

    /**
     * An incoming request for the App has arrived; produce the response.
     * 
     * @param servletRequest the incoming request
     * @param servletResponse the response to be assembled
     * @param servletContext the ServletContext
     * @throws ServletException a Servlet problem
     * @throws IOException an I/O problem
     */
    public void serviceIncomingRequest(
            HttpServletRequest  servletRequest,
            HttpServletResponse servletResponse,
            ServletContext      servletContext )
        throws
            ServletException,
            IOException
    {
        SaneServletRequest request  = createSaneServletRequest( servletRequest );
        StructuredResponse response = createStructuredResponse( request, servletContext );

        if( theCsrfMitigator != null ) {
            Boolean isSafe = theCsrfMitigator.isSafeRequest( request, response ); // 3-valued
            request.setIsSafe( isSafe );
        }

        String relativeBaseUri = request.getRelativeBaseUri();
        if( theAssetRegex.matcher( relativeBaseUri ).matches() ) {
            theResourceManager.processAsset( request, response, servletContext );
        } else {
            processHttpShell( request, response );
            processMeshObject( request, response, servletContext );
        }

        theResourceManager.processTemplate( request, response, servletContext );

        response.copyTo( (HttpServletResponse) servletResponse );
    }

    /**
     * Process the commands contained in the request for the HttpShell.
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws IOException an I/O problem
     */
    protected void processHttpShell(
            SaneServletRequest request,
            StructuredResponse response )
        throws
            IOException
    {
        theShell.performOperationsIfNeeded( request, response );
    }

    /**
     * Create the StructuredResponse suitable for the requested MeshObject contained
     * in the request.
     * 
     * @param request the incoming request
     * @param response the response to be assembled
     * @param servletContext the ServletContext
     * @throws ServletException a Servlet problem
     * @throws IOException an I/O problem
     */
    protected void processMeshObject(
            SaneServletRequest request,
            StructuredResponse response,
            ServletContext     servletContext )
        throws
            ServletException,
            IOException
    {
        // org.infogrid.jee.viewlet.servlet.ViewletDispatcherServlet
        Deque<? extends MeshObjectsToView> toViewStack;
        WebMeshObjectsToView               toView = null; // make compiler happy
        try {
            toViewStack = theToViewFactory.obtainStackFor( request );
            toView      = (WebMeshObjectsToView) toViewStack.pop();

            request.setAttribute( IncludeViewletTag.TO_INCLUDE_STACK_ATTRIBUTE_NAME, toViewStack );

        } catch( FactoryException ex ) {
            handleMeshObjectsToViewFactoryException( ex ); // always throws
        }

        WebViewlet viewlet = null;
        if( toView != null ) {

            try {
                viewlet = (WebViewlet) theViewletFactory.obtainFor( toView );

            } catch( FactoryException ex ) {
                throw new ServletException( ex ); // pass on
            }
            request.setAttribute( WebViewlet.TO_VIEW_ATTRIBUTE_NAME, toView );
            request.setAttribute( WebViewlet.SUBJECT_ATTRIBUTE_NAME, toView.getSubject() );
        }

        if( viewlet != null ) {
            request.setAttribute( WebViewlet.VIEWLET_ATTRIBUTE_NAME, viewlet );

            processViewlet( toView, viewlet, request, response, servletContext );
        }
    }
    
    /**
     * Overridable method to perform Viewlet processing.
     * 
     * @param toView the objects to view
     * @param viewlet the Viewlet to use
     * @param request the incoming SaneRequest
     * @param response the structured response
     * @param servletContext the ServletContext
     * @throws ServletException Servlet processing problem
     * @throws IOException I/O problem
     */
    protected void processViewlet(
            WebMeshObjectsToView toView,
            WebViewlet           viewlet,
            SaneServletRequest   request,
            StructuredResponse   response,
            ServletContext       servletContext )
        throws
            ServletException,
            IOException
    {
        boolean   done;
        Throwable thrown = null;
        try {
            viewlet.view( toView );
            if( request.isSafePost() ) {
                done = viewlet.performBeforeSafePost( request, response );

            } else if( request.isUnsafePost() ) {
                done = viewlet.performBeforeUnsafePost( request, response );

            } else if( request.mayBeSafeOrUnsafePost() ) {
                done = viewlet.performBeforeMaybeSafeOrUnsafePost( request, response );

            } else {
                done = viewlet.performBeforeGet( request, response );
            }

            if( !done ) {
                viewlet.processRequest( request, response, servletContext );
            }

        } catch( RuntimeException | ServletException | IOException t ) {
            thrown = t;
            response.reportProblem( thrown );

            throw t;

        } catch( UnsafePostException | CannotViewException ex ) {
            log.error( ex );

        } finally {
            viewlet.performAfter( request, response, thrown );
        }
    }


    /**
     * Handle exceptions thrown when attempting to create a MeshObjectsToView. This method is
     * factored out so subclasses can easily override.
     * 
     * @param t the thrown exception
     * @throws ServletException
     */
    protected void handleMeshObjectsToViewFactoryException(
            Throwable t )
        throws
            ServletException
    {
        if( t instanceof MalformedURLException ) {
            throw new ServletException( t );
        }

        Throwable cause = t.getCause();

        if( cause instanceof CannotViewException.NoSubject ) {
            throw new ServletExceptionWithHttpStatusCode( cause, HttpServletResponse.SC_NOT_FOUND ); // 404

        } else if( cause instanceof MeshObjectAccessException ) {
            throw new ServletExceptionWithHttpStatusCode( cause, HttpServletResponse.SC_NOT_FOUND ); // 404

        } else if( cause instanceof NotPermittedException ) {
            throw new ServletExceptionWithHttpStatusCode( cause, HttpServletResponse.SC_FORBIDDEN ); // 402

        } else {
            throw new ServletExceptionWithHttpStatusCode( cause, HttpServletResponse.SC_BAD_REQUEST ); // 400
        }
    }

    /**
     * Factory method for creating a SaneServletRequest.
     * 
     * @param servletRequest the incoming request
     * @return the created SaneServletRequest
     */
    public SaneServletRequest createSaneServletRequest(
            HttpServletRequest servletRequest )
    {
        SaneServletRequest ret = SaneServletRequest.create( servletRequest );
        ret.setAttribute( CONTEXT_PARAMETER, ret.getContextPath() );

        SaneRequest originalRequest = ret.getOriginalSaneRequest();

        ret.setAttribute( FULLCONTEXT_PARAMETER, ret.getAbsoluteContextUri() );

        ret.setAttribute( ORIGINAL_CONTEXT_PARAMETER,     originalRequest.getContextPath() );
        ret.setAttribute( ORIGINAL_FULLCONTEXT_PARAMETER, originalRequest.getAbsoluteContextUri() );
        
        return ret;
    }

    /**
     * Factory method for creating the StructuredResponse.
     * 
     * @param request the incoming request
     * @param servletContext the ServletContext
     * @return the created StructuredResponse
     */
    protected StructuredResponse createStructuredResponse(
            SaneServletRequest request, 
            ServletContext     servletContext )
    {        
        StructuredResponse ret = StructuredResponse.create( servletContext );

        request.setAttribute( ProblemReporter.PROBLEM_REPORTER_ATTRIBUTE_NAME, ret );

        return ret;
    }

    /**
     * Knows how to parse an incoming HTTP request into MeshObjectsToView.
     */    
    protected WebMeshObjectsToViewFactory theToViewFactory;

    /**
     * Knows how to instantiate the correct Viewlet based on the incoming MeshObjectsToView.
     */
    protected ViewletFactory theViewletFactory;

    /**
     * Knows how to map traversal specifications given as Strings into actual TraversalSpecification objects.
     */
    protected TraversalTranslator theTraversalTranslator;
    
    /**
     * Knows how to convert to and from String.
     */
    protected StringRepresentationDirectory theStringRepresentationDirectory;

    /**
     * Registers and knows how to find resources such as assets and JSPFs.
     */
    protected WebAppResourceManager theResourceManager;

    /**
     * The HttpShell.
     */
    protected HttpShell theShell;

    /**
     * Knows how to detect and perhaps mitigate cross-site request forgery attacks.
     */
    protected CsrfMitigator theCsrfMitigator;
    
    /**
     * The regular expression that distinguishes between assets and MeshObject URLs.
     * This is used as an exact match.
     */
    protected static final Pattern theAssetRegex = Pattern.compile( "/[a-z]/.*" );
    
    /**
     * The HTML mime type.
     */
    public static final String HTML_MIME = "text/html";
    
    /**
     * Name of the String in the incoming request that is the context path of the application.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path from the root of the current host, not including the host.
     * @see #FULLCONTEXT_PARAMETER
     */
    public static final String CONTEXT_PARAMETER = "CONTEXT";
    
    /**
     * Name of the String in the incoming request that is the context path of the application.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path including protocol, host and port.
     * @see #CONTEXT_PARAMETER
     */
    public static final String FULLCONTEXT_PARAMETER = "FULLCONTEXT";
    
    /**
     * Name of the String in the incoming request that is the context path of the application
     * at the Proxy.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path from the root of the current host, not including the host.
     * @see #ORIGINAL_FULLCONTEXT_PARAMETER
     */
    public static final String ORIGINAL_CONTEXT_PARAMETER = "ORIGINAL_CONTEXT";

    /** 
     * Name of the String in the incoming request that is the context path of the application
     * at the Proxy.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path including protocol, host and port.
     * @see #ORIGINAL_CONTEXT_PARAMETER
     */
    public static final String ORIGINAL_FULLCONTEXT_PARAMETER = "ORIGINAL_FULLCONTEXT";
}
