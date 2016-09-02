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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.app.AppConfiguration;
import org.infogrid.app.InfoGridAccessory;
import org.infogrid.app.InfoGridApp;
import org.infogrid.app.InfoGridInstallable;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.model.primitives.text.ModelPrimitivesStringRepresentationDirectorySingleton;
import org.infogrid.model.traversal.TraversalTranslator;
import org.infogrid.model.traversal.xpath.XpathTraversalTranslator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Pair;
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
import org.infogrid.web.templates.BinaryStructuredResponseSection;
import org.infogrid.web.templates.DefaultStructuredResponseTemplateFactory;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseTemplate;
import org.infogrid.web.templates.StructuredResponseTemplateFactory;
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
    private static final Log log = Log.getLogInstance(InfoGridWebApp.class ); // our own, private logger

    /**
     * This constructor can be used directly, or the class may be subclassed.
     */
    public InfoGridWebApp()
    {
    }
    
    /**
     * Invoked by the framework, run the various initialization methods with
     * the configuration options provided.
     * 
     * @param config the configuration options
     */
    @Override
    public void initialize(
            AppConfiguration config )
    {
        registerResources( config );
        initializeMeshBase( config );
        initializeUi( (WebAppConfiguration) config );
    }

    /**
     * Overridable method to register available resources with this app.
     * 
     * @param config the configuration options
     */
    protected void registerResources(
            AppConfiguration config )
    {
        
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
     * An incoming request for the App has arrived; produce the response.
     * 
     * @param servletRequest the incoming request
     * @param servletResponse the response to be assembled
     * @param servletContext the ServletContext
     * @throws ServletException a Servlet problem
     * @throws IOException an I/O problem
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

        String relativeBaseUri = request.getRelativeBaseUri();
        if( theAssetRegex.matcher( relativeBaseUri ).matches() ) {
            processAsset( request, response, servletContext );
        } else {
            processMeshObject( request, response, servletContext );
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
    
    protected void processAsset(
            SaneServletRequest request,
            StructuredResponse response,
            ServletContext     servletContext )
        throws
            ServletException,
            IOException
    {
        String relativeBaseUri = request.getRelativeBaseUri();
        
        BinaryStructuredResponseSection section = response.getDefaultBinarySection();

        // app assets override accessory assets
        URL assetUrl = theAppAssets.get( relativeBaseUri );
        if( assetUrl == null ) {
            Pair<URL,InfoGridAccessory> found = theAccessoryAssets.get( relativeBaseUri );
            if( found != null ) {
                assetUrl = found.getName();
            }
        }
        if( assetUrl != null ) {
            InputStream inStream = new BufferedInputStream( assetUrl.openStream() );
            byte []     buf      = new byte[8192];
            while( true ) {
                int read = inStream.read( buf );
                if( read <= 0 ) {
                    break;
                }
                section.appendContent( buf, read );
            }
        }
    }

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
            request.setAttribute( WebViewlet.SUBJECT_ATTRIBUTE_NAME, toView.getSubject() );
            request.setAttribute( WebViewlet.VIEWLET_ATTRIBUTE_NAME, viewlet );
        }

        if( viewlet != null ) {
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
            response.reportProblem( thrown );

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
     * @param servletContext the ServletContext
     * @throws ServletException Servlet processing problem
     * @throws IOException I/O problem
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
     * {@inheritDoc}
     */
    @Override
    public void registerViewlet(
            ViewletMatcher      matcher,
            InfoGridInstallable installable )
    {
        theViewletFactory.registerViewlet( matcher, installable );
    }

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets with the
     * app. The app decides whether or not, or how to make those assets
     * available. 
     * 
     * @param path the relative path of the asset
     * @param url the URL of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            URL                 url,
            InfoGridInstallable installable )
    {
        if( path == null || path.length() < 3 || path.charAt( 0 ) != '/' ) {
            throw new IllegalArgumentException( "Invalid path when attempting to register asset: " + path );
        }
        if( url == null ) {
            throw new IllegalArgumentException( "Cannot register asset with null URL: " + path + ", " + installable.getName() );
        }
        if( installable == null ) {
            throw new IllegalArgumentException( "Cannot register asset with unidentified installable: " + path + ", " + url.toExternalForm() );
        }
        if( installable == this ) {
            theAppAssets.put( path, url );
            
        } else if( installable instanceof InfoGridAccessory ) {
            theAccessoryAssets.put( path, new Pair<>( url, (InfoGridAccessory) installable ) );

        } else {
            throw new IllegalArgumentException( "Cannot register asset from a different app: " + installable.getName() + " vs " + getName() );
        }
    }

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets with the
     * app. The app decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served.
     * 
     * @param path the relative path of the asset
     * @param loader the ClassLoader to resove the path against
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            ClassLoader         loader,
            InfoGridInstallable installable )
    {
        String pathWithoutSlash = path.substring( 1 );

        registerAsset( path, loader.getResource( pathWithoutSlash ), installable );
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
    
    /**
     * The known assets of the app, keyed by their relative request URLs and
     * mapped to the URLs from which they can be obtained.
     */
    protected Map<String,URL> theAppAssets = new HashMap<>();

    /**
     * The known assets of the accessories, keyed by their relative request URLs
     * and mapped to the URLs from which they can be obtained.
     */
    protected Map<String,Pair<URL,InfoGridAccessory>> theAccessoryAssets = new HashMap<>();

    /**
     * The regular expression that distinguishes between assets and MeshObject URLs.
     * This is used as an exact match.
     */
    protected static final Pattern theAssetRegex = Pattern.compile( "/[a-z]/.*" );
}
    