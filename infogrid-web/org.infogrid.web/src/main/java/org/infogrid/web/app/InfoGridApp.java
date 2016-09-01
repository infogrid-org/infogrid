/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.infogrid.web.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Deque;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseNameServer;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.m.MMeshBaseNameServer;
import org.infogrid.model.primitives.text.ModelPrimitivesStringRepresentationDirectorySingleton;
import org.infogrid.model.traversal.TraversalTranslator;
import org.infogrid.model.traversal.xpath.XpathTraversalTranslator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.ObjectInContext;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentationDirectory;
import org.infogrid.util.text.StringRepresentationDirectorySingleton;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.DefaultViewletFactory;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.viewlet.ViewletMatcher;
import org.infogrid.web.ServletExceptionWithHttpStatusCode;
import org.infogrid.web.rest.RestfulJeeFormatter;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.security.SafeUnsafePostFilter;
import org.infogrid.web.security.UnsafePostException;
import org.infogrid.web.taglib.viewlet.IncludeViewletTag;
import org.infogrid.web.templates.DefaultStructuredResponseTemplateFactory;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseTemplate;
import org.infogrid.web.templates.StructuredResponseTemplateFactory;
import org.infogrid.web.viewlet.DefaultWebMeshObjectsToViewFactory;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.viewlet.WebMeshObjectsToViewFactory;
import org.infogrid.web.viewlet.WebViewlet;

/**
 * The superclass of all InfoGridApps.
 */
public class InfoGridApp
    extends
        InfoGridInstallable
    implements
        ObjectInContext
{
    private static final Log log = Log.getLogInstance( InfoGridApp.class ); // our own, private logger

    /**
     * This constructor can be used directly, or the class may be subclassed.
     */
    public InfoGridApp()
    {
    }
    
    /**
     * Invoked by the framework, run the various initialization methods with
     * the configuration options provided.
     * 
     * @param config the configuration options
     */
    public void initialize(
            AppConfiguration config )
    {
        initializeMeshBase( config );
        initializeUi( config );
    }
    
    /**
     * Overridable method to initialize the MeshBase and related.
     * 
     * @param config the configuration options
     */
    protected void initializeMeshBase(
            AppConfiguration config )
    {
        theMeshBase = MMeshBase.create();

        theMeshBaseNameServer = MMeshBaseNameServer.create();
        ((MMeshBaseNameServer)theMeshBaseNameServer).put( theMeshBase.getIdentifier(), theMeshBase );
    }

    /**
     * Overridable method to initialize the user interface.
     * 
     * @param config the configuration options
     */
    protected void initializeUi(
            AppConfiguration config )
    {
        theViewletFactory = DefaultViewletFactory.create( WebViewlet.class.getName() );
        
        theTraversalTranslator = XpathTraversalTranslator.create( theMeshBase );

        theToViewFactory = DefaultWebMeshObjectsToViewFactory.create(
                theMeshBase.getIdentifier(),
                DefaultMeshBaseIdentifierFactory.create(),
                theMeshBaseNameServer,
                theTraversalTranslator,
                config.getAppContextPath(),
                theRootContext );

        ModelPrimitivesStringRepresentationDirectorySingleton.initialize();

        StringRepresentationDirectory srepdir = StringRepresentationDirectorySingleton.getSingleton();
        theRootContext.addContextObject( srepdir );

        RestfulJeeFormatter formatter = RestfulJeeFormatter.create( theMeshBase, srepdir );
        theRootContext.addContextObject( formatter );

        // StructuredResponseTemplateFactory
        theResponseTemplateFactory = DefaultStructuredResponseTemplateFactory.create( formatter );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext()
    {
        return theRootContext;
    }

    /**
     * {@inheritDoc}
     */
    public void service(
            HttpServletRequest  servletRequest,
            HttpServletResponse servletResponse,
            ServletContext      servletContext )
        throws
            ServletException,
            IOException
    {
        SaneServletRequest request  = SaneServletRequest.create( servletRequest );
        StructuredResponse response = StructuredResponse.create( servletContext);

        // org.infogrid.jee.defaultapp.DefaultInitializationFilter
        // Template
        // SafeUnsafe
        // com.cldstr.cldstr.www.WwwCldstrInitializationFilter
        // AUthenticationFilter
        // HttpShellFilter
        // RegexDispatcherFilter

        // org.infogrid.jee.viewlet.servlet.ViewletDispatcherServlet
        Deque<? extends MeshObjectsToView> toViewStack = null; // make compiler happy
        try {
            toViewStack = theToViewFactory.obtainStackFor( request );
            
        } catch( FactoryException ex ) {
            handleMeshObjectsToViewFactoryException( ex );
        }

        request.setAttribute( IncludeViewletTag.TO_INCLUDE_STACK_ATTRIBUTE_NAME, toViewStack );
        
        WebMeshObjectsToView toView = (WebMeshObjectsToView) toViewStack.pop();

        WebViewlet viewlet = null;
        if( toView != null ) {
            request.setAttribute( WebViewlet.SUBJECT_ATTRIBUTE_NAME, toView.getSubject() );

            try {
                viewlet = (WebViewlet) theViewletFactory.obtainFor( toView );

            } catch( FactoryException ex ) {
                throw new ServletException( ex ); // pass on
            }
            request.setAttribute( WebViewlet.VIEWLET_ATTRIBUTE_NAME, viewlet );
        }

        Throwable thrown = null;

        if( viewlet != null ) {
            processViewlet( toView, viewlet, request, response, servletContext );
        }
        if( thrown != null ) {
            response.reportProblem( thrown );
        }
        if( response.isStructuredEmpty() ) {
            // traditional processing, it ignored the StructuredResponse. We simply copy.
            response.copyTo( (HttpServletResponse) servletResponse );

        } else {
            // process structured response

            for( Map.Entry<String,String[]> entry : response.getHeaders().entrySet() ) {
                for( String current : entry.getValue() ) {
                    servletResponse.addHeader( entry.getKey(), current );
                }
            }
            for( Cookie current : response.getCookies() ) {
                servletResponse.addCookie( current );
            }

            processTemplate( request, response, servletResponse, servletContext );
        }
    }
    
    /**
     * Overridable method to perform Viewlet processing.
     * 
     * @param viewlet the Viewlet to process
     * @param request the incoming SaneRequest
     * @param response the structured response
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
            if( SafeUnsafePostFilter.isSafePost( request ) ) {
                done = viewlet.performBeforeSafePost( request, response );

            } else if( SafeUnsafePostFilter.isUnsafePost( request ) ) {
                done = viewlet.performBeforeUnsafePost( request, response );

            } else if( SafeUnsafePostFilter.mayBeSafeOrUnsafePost( request ) ) {
                done = viewlet.performBeforeMaybeSafeOrUnsafePost( request, response );

            } else {
                done = viewlet.performBeforeGet( request, response );
            }

            if( !done ) {
                viewlet.processRequest( request, response, servletContext );
            }

        } catch( RuntimeException | ServletException | IOException t ) {
            thrown = t;
            throw t;

        } catch( UnsafePostException | CannotViewException ex ) {
            log.error( ex );

        } finally {
            viewlet.performAfter( request, response, thrown );
        }
    }

    /**
     * Overridable method that performs template processing.
     * 
     * @param request the incoming SaneRequest
     * @param response the structured response
     * @param servletResponse the ultimate response
     */
    protected void processTemplate(
            SaneServletRequest  request,
            StructuredResponse  response,
            HttpServletResponse servletResponse,
            ServletContext      servletContext )
        throws
            ServletException,
            IOException
    {
        try {
            StructuredResponseTemplate template = theResponseTemplateFactory.obtainFor( request, response );

            template.doOutput( servletResponse, servletContext, request, response );

        } catch( FactoryException ex ) {
            throw new ServletException( ex );
        }
    }

    /**
     * Allows an InfoGridAccessory to register additional Viewlets with the
     * app. The app decides whether or not, or how to make those Viewlets
     * available. 
     * 
     * @param matcher the ViewletMatcher leading to the Viewlet being registered
     */
    public void registerViewlet(
            ViewletMatcher matcher )
    {
        theViewletFactory.registerViewlet( matcher );
    }

    public void registerAsset(
            String      path,
            ClassLoader loader )
    {
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
     * The root context.
     */
    protected Context theRootContext = SimpleContext.createRoot( "root context" );

    /**
     * The main MeshBase for this app.
     */
    protected MeshBase theMeshBase;

    /**
     * If there are several MeshBases in this app, allows lookup of MeshBases
     * by their identifier.
     */
    protected MeshBaseNameServer<MeshBaseIdentifier,MeshBase> theMeshBaseNameServer;

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
     * Knows how to find the right response template for the incoming request.
     */
    protected StructuredResponseTemplateFactory theResponseTemplateFactory;
}
    