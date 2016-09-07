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
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.infogrid.app.AppConfiguration;
import org.infogrid.app.InfoGridAccessory;
import org.infogrid.app.InfoGridApp;
import org.infogrid.app.InfoGridInstallable;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBaseNameServer;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.model.primitives.text.ModelPrimitivesStringRepresentationDirectorySingleton;
import org.infogrid.model.traversal.TraversalTranslator;
import org.infogrid.model.traversal.xpath.XpathTraversalTranslator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Pair;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentationDirectory;
import org.infogrid.util.text.StringRepresentationDirectorySingleton;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.DefaultViewletFactory;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.viewlet.ViewletMatcher;
import org.infogrid.web.ProblemReporter;
import org.infogrid.web.ServletExceptionWithHttpStatusCode;
import org.infogrid.web.httpshell.HttpShell;
import org.infogrid.web.httpshell.HttpShellHandler;
import org.infogrid.web.rest.RestfulJeeFormatter;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.security.SafeUnsafePostFilter;
import org.infogrid.web.security.UnsafePostException;
import org.infogrid.web.taglib.viewlet.IncludeViewletTag;
import org.infogrid.web.templates.DefaultStructuredResponseTemplateFactory;
import org.infogrid.web.templates.SimpleServletConfig;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseSection;
import org.infogrid.web.templates.StructuredResponseTemplate;
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
    @SuppressWarnings( "LeakingThisInConstructor" )
    public InfoGridWebApp()
    {
        theRootContext.addContextObject( this );
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
        initializeModels( config );
        initializeMeshBase( config );
        initializeGraph( config );
        initializeUi( (WebAppConfiguration) config );        
    }
    
    public MeshBaseNameServer<MeshBaseIdentifier,MeshBase> getMeshBaseNameServer()
    {
        return theMeshBaseNameServer;
    }
    
    public MeshBaseIdentifierFactory getMeshBaseIdentifierFactory()
    {
        return theMeshBaseIdentifierFactory;
    }
    
    public MeshBase getMainMeshBase()
    {
        return theMeshBase;
    }

    public StringRepresentationDirectory getStringRepresentationDirectory()
    {
        return theStringRepresentationDirectory;
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
        // no op at this level
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

        // StructuredResponseTemplateFactory
        theResponseTemplateFactory = DefaultStructuredResponseTemplateFactory.create( formatter );
        theRootContext.addContextObject( theResponseTemplateFactory );
        
        theShell = HttpShell.create( this );
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

        // org.infogrid.jee.defaultapp.DefaultInitializationFilter
        // Template
        // SafeUnsafe
        // com.cldstr.cldstr.www.WwwCldstrInitializationFilter
        // AUthenticationFilter

        String relativeBaseUri = request.getRelativeBaseUri();
        if( theAssetRegex.matcher( relativeBaseUri ).matches() ) {
            processAsset( request, response, servletContext );
        } else {
            processHttpShell( request, response );
            processMeshObject( request, response, servletContext );
        }

        processTemplate( request, response, servletContext );
        response.copyTo( (HttpServletResponse) servletResponse );
    }
    
    protected <T> Pair<String,T> findByMimeType(
            Map<String,T> map,
            String        mime )
    {
        T found = map.get( mime ); // FIXME: this may have to become more complex
        if( found == null && mime == null ) {
            Iterator<Map.Entry<String,T>> iter = map.entrySet().iterator();
            if( iter.hasNext() ) { // take anything
                Map.Entry<String,T> found2 = iter.next();
                mime  = found2.getKey();
                found = found2.getValue();
            }
        }
        if( found != null ) {
            return new Pair<>( mime, found );
        } else {
            return null;
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
        
        StructuredResponseSection section = response.getDefaultSection();

        // app assets override accessory assets
        Map<String,URL>  appLevel1 = theAppAssets.get( relativeBaseUri );
        Pair<String,URL> asset     = null;

        if( appLevel1 != null ) {
            asset = findByMimeType( appLevel1, request.getContentType());
        }
        if( asset == null ) {
            Map<String,Pair<URL,InfoGridAccessory>> accLevel1 = theAccessoryAssets.get( relativeBaseUri );
            if( accLevel1 != null ) {
                Pair<String,Pair<URL,InfoGridAccessory>> found = findByMimeType( accLevel1, request.getContentType());
                if( found != null ) {
                    asset = new Pair<>( found.getName(), found.getValue().getName() );
                }
            }
        }
        if( asset != null ) {
            section.setContentType( asset.getName() );
            InputStream inStream = new BufferedInputStream( asset.getValue().openStream() );
            byte []     buf      = new byte[8192];
            while( true ) {
                int read = inStream.read( buf );
                if( read <= 0 ) {
                    break;
                }
                section.appendBinaryContent( buf, read );
            }
        }
    }

    protected void processHttpShell(
            SaneServletRequest request,
            StructuredResponse response )
        throws
            IOException
    {
        theShell.performOperationsIfNeeded( request, response );
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
     * @param servletContext the ServletContext
     * @throws ServletException Servlet processing problem
     * @throws IOException I/O problem
     */
    protected void processTemplate(
            SaneServletRequest  request,
            StructuredResponse  response,
            ServletContext      servletContext )
        throws
            ServletException,
            IOException
    {
        try {
            StructuredResponseTemplate template = theResponseTemplateFactory.obtainFor( request, response );

            template.applyTemplate( servletContext, request, response );

        } catch( FactoryException ex ) {
            throw new ServletException( ex );
        }
    }

    public void processJspf(
            String      name,
            PageContext pageContext )
        throws
            ServletException,
            IOException
    {
        String mime = pageContext.getResponse().getContentType();
        if( mime == null ) {
            mime = HTML_MIME;
        }

        Class<? extends Servlet> servletClass = null;

        Map<String,Class<? extends Servlet>> appLevel1 = theAppJspfs.get( name );
        if( appLevel1 != null ) {
            servletClass = appLevel1.get( mime );
        }
        if( servletClass == null ) {
            Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel1 = theAccessoryJspfs.get( name );
            if( accLevel1 != null ) {
                servletClass = accLevel1.get( mime ).getName();
            }
        }
        if( servletClass == null ) {
            throw new ServletException( "Cannot find JSPF with name " + name );
        }
        
        try {
            Servlet servlet = servletClass.newInstance();
            servlet.init( new SimpleServletConfig( name, pageContext.getServletContext() ));

            servlet.service( pageContext.getRequest(), pageContext.getResponse() );

        } catch( InstantiationException | IllegalAccessException ex ) {
            throw new ServletException( ex );
        }
    }

    public void processJspo(
            String      name,
            PageContext pageContext )
        throws
            ServletException,
            IOException
    {
        String mime = pageContext.getResponse().getContentType();
        if( mime == null ) {
            mime = HTML_MIME;
        }

        Class<? extends Servlet> servletClass = null;

        Map<String,Class<? extends Servlet>> appLevel1 = theAppJspos.get( name );
        if( appLevel1 != null ) {
            servletClass = appLevel1.get( mime );
        }
        if( servletClass == null ) {
            Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel1 = theAccessoryJspos.get( name );
            if( accLevel1 != null ) {
                servletClass = accLevel1.get( mime ).getName();
            }
        }
        if( servletClass == null ) {
            throw new ServletException( "Cannot find JSPF with name " + name );
        }
        
        try {
            Servlet servlet = servletClass.newInstance();
            servlet.init( new SimpleServletConfig( name, pageContext.getServletContext() ));

            servlet.service( pageContext.getRequest(), pageContext.getResponse() );

        } catch( InstantiationException | IllegalAccessException ex ) {
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
     * @param mime the content type of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            URL                 url,
            String              mime,
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
            Map<String,URL> appLevel1 = theAppAssets.get( path );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppAssets.put( path, appLevel1 );
            }
            appLevel1.put( mime, url );
            
        } else if( installable instanceof InfoGridAccessory ) {
            Map<String,Pair<URL,InfoGridAccessory>> accLevel1 = theAccessoryAssets.get( path );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryAssets.put( path, accLevel1 );
            }
            accLevel1.put( mime, new Pair<>( url, (InfoGridAccessory) installable ) );

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
     * @param loader the ClassLoader to resolve the path against
     * @param mime the content type of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            ClassLoader         loader,
            String              mime,
            InfoGridInstallable installable )
    {
        String pathWithoutSlash = path.substring( 1 );

        registerAsset( path, loader.getResource( pathWithoutSlash ), mime, installable );
    }

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets with the
     * app. The app decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served. The ClassLoader is the InfoGridInstallable's.
     * 
     * @param path the relative path of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            InfoGridInstallable installable )
    {
        String mime = determineMimeFromFile( path );
        if( mime == null ) {
            throw new IllegalArgumentException( "Cannot determine content type from file extension, register explicitly: " + path );
        }
        registerAsset( path, installable.getClass().getClassLoader(), mime, installable );
    }

    protected String determineMimeFromFile(
            String path )
    {
        int lastPeriod = path.lastIndexOf( '.' );
        if( lastPeriod > 0 ) {
            switch( path.substring( lastPeriod+1 )) {
                case "txt":
                    return "text/plain";
                case "html":
                case "xhtml":
                    return "text/html";
                case "css":
                    return "text/css";
                case "png":
                    return "image/png";
                case "jpg":
                case "jpeg":
                    return "image/jpg";
                case "gif":
                    return "image/gif";
                case "js":
                    return "application/javascript";
            }
        }
        return null;
    }

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register viewlet templates with the
     * app. The app decides whether or not, or how to make those viewlet templates
     * available. 
     * 
     * @param name the name of the template
     * @param mime the MIME type which this template will emit
     * @param servletClass the Servlet class implementing the viewlet template
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerViewletTemplate(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        theResponseTemplateFactory.addTemplateServlet(
                name,
                mime,
                servletClass );
    } 
    
    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP function servlet.
     * The app decides whether or not, or how to make those JSP function servlets
     * available. 
     * 
     * @param name the name of the JSP function
     * @param mime the MIME type where this JSP function applies
     * @param servletClass the Servlet class implementing the JSP function
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspf(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        if( installable instanceof InfoGridApp ) {
            Map<String,Class<? extends Servlet>> appLevel1 = theAppJspfs.get( name );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppJspfs.put( name, appLevel1 );
            }
            appLevel1.put( mime, servletClass );
        } else {
            Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel1 = theAccessoryJspfs.get( name );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryJspfs.put( name, accLevel1 );
            }
            accLevel1.put( mime, new Pair<>( servletClass, (InfoGridAccessory) installable ));
        }
    } 
    
    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP overlay servlet.
     * The app decides whether or not, or how to make those JSP overlay servlets
     * available. 
     * 
     * @param name the name of the JSP overlay
     * @param mime the MIME type where this JSP overlay applies
     * @param servletClass the Servlet class implementing the JSP overlay
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspo(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        if( installable instanceof InfoGridApp ) {
            Map<String,Class<? extends Servlet>> appLevel1 = theAppJspos.get( name );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppJspos.put( name, appLevel1 );
            }
            appLevel1.put( mime, servletClass );
        } else {
            Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel1 = theAccessoryJspos.get( name );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryJspos.put( name, accLevel1 );
            }
            accLevel1.put( mime, new Pair<>( servletClass, (InfoGridAccessory) installable ));
        }
    } 
    
    /**
     * Register a HttpShellHandler.
     * 
     * @param name name of the HttpShellHandler
     * @param handler the handler
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerHttpShellHandler(
            String              name,
            HttpShellHandler    handler,
            InfoGridInstallable installable )
    {
        if( installable instanceof InfoGridApp ) {
            theAppHandlers.put( name, handler );
        } else {
            theAccessoryHandlers.put( name, new Pair<>( handler, (InfoGridAccessory) installable ));
        }
    }

    /**
     * Find a HttpShellHandler by name.
     * 
     * @param name name of the HttpShellHandler
     * @return the HttpShellHandler or null
     */
    public HttpShellHandler findHttpShellHandler(
            String name )
    {
        HttpShellHandler ret = theAppHandlers.get( name );
        if( ret == null ) {
            Pair<HttpShellHandler,InfoGridAccessory> found = theAccessoryHandlers.get( name );
            if( found != null ) {
                ret = found.getName();
            }
        }
        return ret;
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
     * Knows how to find the right response template for the incoming request.
     */
    protected DefaultStructuredResponseTemplateFactory theResponseTemplateFactory;
    
    /**
     * Knows how to convert to and from String.
     */
    protected StringRepresentationDirectory theStringRepresentationDirectory;

    /**
     * The HttpShell
     */
    protected HttpShell theShell;

    /**
     * The known assets of the app, keyed by their relative request URLs, then MIME types,
     * to the URLs from which they can be obtained.
     */
    protected Map<String,Map<String,URL>> theAppAssets = new HashMap<>();

    /**
     * The known assets of the accessories, keyed by their relative request URLs,
     * then MIME types, to the URLs from which they can be obtained.
     */
    protected Map<String,Map<String,Pair<URL,InfoGridAccessory>>> theAccessoryAssets = new HashMap<>();

    /**
     * The known JSPFs of the app, keyed by their name, then MIME types, to the
     * Servlet Class implementing it.
     */
    protected Map<String,Map<String,Class<? extends Servlet>>> theAppJspfs = new HashMap<>();

    /**
     * The known JSPFs of the accessories, keyed by their name, then MIME types, to the
     * Servlet Class implementing it and the Accessory that provided it
     */
    protected Map<String,Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>>> theAccessoryJspfs = new HashMap<>();

    /**
     * The known JSPOs of the app, keyed by their name, then MIME types, to the
     * Servlet Class implementing it.
     */
    protected Map<String,Map<String,Class<? extends Servlet>>> theAppJspos = new HashMap<>();

    /**
     * The known JSPOs of the accessories, keyed by their name, then MIME types, to the
     * Servlet Class implementing it and the Accessory that provided it.
     */
    protected Map<String,Map<String,Pair<Class<? extends Servlet>,InfoGridAccessory>>> theAccessoryJspos = new HashMap<>();

    /**
     * The known HttpShellHandlers of the app, keyed by their name.
     */
    protected Map<String,HttpShellHandler> theAppHandlers = new HashMap<>();

    /**
     * The known HttpShellHandlers of the accessories, keyed by their name.
     */
    protected Map<String,Pair<HttpShellHandler,InfoGridAccessory>> theAccessoryHandlers = new HashMap<>();

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
     * Name of the String in the RequestContext that is the context path of the application.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path from the root of the current host, not including the host.
     * @see #FULLCONTEXT_PARAMETER
     */
    public static final String CONTEXT_PARAMETER = "CONTEXT";
    
    /**
     * Name of the String in the RequestContext that is the context path of the application.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path including protocol, host and port.
     * @see #CONTEXT_PARAMETER
     */
    public static final String FULLCONTEXT_PARAMETER = "FULLCONTEXT";
    
    /**
     * Name of the String in the RequestContext that is the context path of the application
     * at the Proxy.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path from the root of the current host, not including the host.
     * @see #ORIGINAL_FULLCONTEXT_PARAMETER
     */
    public static final String ORIGINAL_CONTEXT_PARAMETER = "ORIGINAL_CONTEXT";

    /** 
     * Name of the String in the RequestContext that is the context path of the application
     * at the Proxy.
     * Having this makes the development of path-independent JSPs much simpler. This
     * is a fully-qualified path including protocol, host and port.
     * @see #ORIGINAL_CONTEXT_PARAMETER
     */
    public static final String ORIGINAL_FULLCONTEXT_PARAMETER = "ORIGINAL_FULLCONTEXT";
}
    
