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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.web.app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import org.infogrid.app.InfoGridAccessory;
import org.infogrid.app.InfoGridApp;
import org.infogrid.app.InfoGridInstallable;
import org.infogrid.util.FactoryException;
import org.infogrid.util.L10MapImpl;
import org.infogrid.util.Pair;
import org.infogrid.web.JeeFormatter;
import static org.infogrid.web.app.InfoGridWebApp.HTML_MIME;
import org.infogrid.web.httpshell.HttpShellHandler;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.templates.DefaultStructuredResponseTemplateFactory;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseSection;
import org.infogrid.web.templates.StructuredResponseTemplate;
import org.infogrid.web.util.SimpleServletConfig;

/**
 * Default implementation of WebAppResourceManager.
 */
public class DefaultWebAppResourceManager
    extends
        AbstractWebAppResourceManager
{
    /**
     * Factory method.
     * 
     * @param formatter the JeeFormatter to use
     * @return the created DefaultWebAppResourceManager
     */
    public static DefaultWebAppResourceManager create(
            JeeFormatter formatter )
    {        
        return new DefaultWebAppResourceManager( DefaultStructuredResponseTemplateFactory.create( formatter ) );
    }
    
    /**
     * Private constructor, subclass or use factory method.
     * 
     * @param responseTemplateFactory the ResponseTemplateFactory to use
     */
    protected DefaultWebAppResourceManager(
            DefaultStructuredResponseTemplateFactory responseTemplateFactory )
    {
        theResponseTemplateFactory = responseTemplateFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAsset(
            String              path,
            URL                 url,
            String              mime,
            Locale              locale,
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
        if( installable instanceof InfoGridApp ) {
            Map<String,L10MapImpl<URL>> appLevel1 = theAppAssets.get( path );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppAssets.put( path, appLevel1 );
            }
            L10MapImpl appLevel2 = appLevel1.get( mime );
            if( appLevel2 == null ) {
                appLevel2 = L10MapImpl.create();
                appLevel1.put( mime, appLevel2 );
            }
            appLevel2.put( locale, url );
            
        } else if( installable instanceof InfoGridAccessory ) {
            Map<String,L10MapImpl<Pair<URL,InfoGridAccessory>>> accLevel1 = theAccessoryAssets.get( path );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryAssets.put( path, accLevel1 );
            }
            L10MapImpl accLevel2 = accLevel1.get( mime );
            if( accLevel2 == null ) {
                accLevel2 = L10MapImpl.create();
                accLevel1.put( mime, accLevel2 );
            }
            accLevel2.put( locale, new Pair<>( url, (InfoGridAccessory) installable ) );

        } else {
            throw new IllegalArgumentException( "Unexpected type: " + installable != null ? installable.getClass().getName() : "null" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerViewletTemplate(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        theResponseTemplateFactory.addTemplateServlet(
                name,
                mime,
                locale,
                servletClass );
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void registerJspf(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        if( installable instanceof InfoGridApp ) {
            Map<String,L10MapImpl<Class<? extends Servlet>>> appLevel1 = theAppJspfs.get( name );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppJspfs.put( name, appLevel1 );
            }
            L10MapImpl<Class<? extends Servlet>> appLevel2 = appLevel1.get( mime );
            if( appLevel2 == null ) {
                appLevel2 = L10MapImpl.create();
                appLevel1.put( mime, appLevel2 );
            }
            appLevel2.put( locale, servletClass );

        } else if( installable instanceof InfoGridAccessory ) {
            Map<String,L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>>> accLevel1 = theAccessoryJspfs.get( name );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryJspfs.put( name, accLevel1 );
            }
            L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel2 = accLevel1.get( mime );
            if( accLevel2 == null ) {
                accLevel2 = L10MapImpl.create();
                accLevel1.put( mime, accLevel2 );
            }
            accLevel2.put( locale, new Pair<>( servletClass, (InfoGridAccessory) installable ));

        } else {
            throw new IllegalArgumentException( "Unexpected type: " + installable != null ? installable.getClass().getName() : "null" );
        }
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerJspo(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        if( installable instanceof InfoGridApp ) {
            Map<String,L10MapImpl<Class<? extends Servlet>>> appLevel1 = theAppJspos.get( name );
            if( appLevel1 == null ) {
                appLevel1 = new HashMap<>();
                theAppJspos.put( name, appLevel1 );
            }
            L10MapImpl<Class<? extends Servlet>> appLevel2 = appLevel1.get( mime );
            if( appLevel2 == null ) {
                appLevel2 = L10MapImpl.create();
                appLevel1.put( mime, appLevel2 );
            }
            appLevel2.put( locale, servletClass );

        } else if( installable instanceof InfoGridAccessory ) {
            Map<String,L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>>> accLevel1 = theAccessoryJspos.get( name );
            if( accLevel1 == null ) {
                accLevel1 = new HashMap<>();
                theAccessoryJspos.put( name, accLevel1 );
            }
            L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>> accLevel2 = accLevel1.get( mime );
            if( accLevel2 == null ) {
                accLevel2 = L10MapImpl.create();
                accLevel1.put( mime, accLevel2 );
            }
            accLevel2.put( locale, new Pair<>( servletClass, (InfoGridAccessory) installable ));

        } else {
            throw new IllegalArgumentException( "Unexpected type: " + installable != null ? installable.getClass().getName() : "null" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void processAsset(
            SaneServletRequest request,
            StructuredResponse response,
            ServletContext     servletContext )
        throws
            ServletException,
            IOException
    {
        String relativeBaseUri = request.getRelativeBaseUri();
        Locale locale          = null; // FIXME

        Pair<String,URL> asset = lookup( relativeBaseUri, request.getContentType(), locale, theAppAssets, theAccessoryAssets );

        if( asset != null ) {
            StructuredResponseSection section = response.getDefaultSection();
            
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

    /**
     * {@inheritDoc}
     */
    @Override
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
        Locale locale = null; // FIXME

        Pair<String,Class<? extends Servlet>> servletInfo = lookup( name, mime, locale, theAppJspfs, theAccessoryJspos );
    
        if( servletInfo == null ) {
            throw new ServletException( "Cannot find JSPF with name " + name );
        }
        
        try {
            Servlet servlet = servletInfo.getValue().newInstance();
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
        Locale locale = null; // FIXME

        Pair<String,Class<? extends Servlet>> servletInfo = lookup( name, mime, locale, theAppJspfs, theAccessoryJspos );
    
        if( servletInfo == null ) {
            throw new ServletException( "Cannot find JSPF with name " + name );
        }
        
        try {
            Servlet servlet = servletInfo.getValue().newInstance();
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
    public void processTemplate(
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

    /**
     * Helper method to look up an entry in the app and accessory data structures.
     * 
     * @param <T> The type of result to be produced
     * @param itemName the path or name of the item to be looked up
     * @param mime the requested MIME type
     * @param locale the requested Locale
     * @param appMap the map of entries provided by the app
     * @param accMap the map of entries provided by the accessories
     * @return 
     */
    protected <T> Pair<String,T> lookup(
            String                                itemName,
            String                                mime,
            Locale                                locale,
            Map<String,Map<String,L10MapImpl<T>>> appMap,
            Map<String,Map<String,L10MapImpl<Pair<T,InfoGridAccessory>>>> accMap )
    {
        // app assets override accessory assets
        Map<String,L10MapImpl<T>> appLevel1 = appMap.get( itemName );
        Pair<String,T>            asset     = null;

        if( appLevel1 != null ) {
            Pair<String,L10MapImpl<T>> appLevel2 = findByMimeType( appLevel1, mime );
            if( appLevel2 != null ) {
                asset = new Pair<>( appLevel2.getName(), appLevel2.getValue().get( locale ));
            }
        }
        if( asset == null ) {
            Map<String,L10MapImpl<Pair<T,InfoGridAccessory>>> accLevel1 = accMap.get( itemName );
            if( accLevel1 != null ) {
                Pair<String,L10MapImpl<Pair<T,InfoGridAccessory>>> accLevel2 = findByMimeType( accLevel1, mime );
                if( accLevel2 != null ) {
                    Pair<T,InfoGridAccessory> accLevel3 = accLevel2.getValue().get( locale );
                    if( accLevel3 != null ) {
                        asset = new Pair<>( accLevel2.getName(), accLevel3.getName() );
                    }
                }
            }
        }
        return asset;
    }

    /**
     * Helper method to look up the value for a mime type in a structure T.
     * 
     * @param <T> the structure type
     * @param map the map containing the structure
     * @param mime the MIME type
     * @return the found value, or null
     */
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

    /**
     * The known assets of the app, keyed by their relative request URLs, then MIME types,
     * then Locale, to the URLs from which they can be obtained.
     */
    protected Map<String,Map<String,L10MapImpl<URL>>> theAppAssets = new HashMap<>();

    /**
     * The known assets of the accessories, keyed by their relative request URLs,
     * then MIME types, then Locale, to the URLs from which they can be obtained.
     */
    protected Map<String,Map<String,L10MapImpl<Pair<URL,InfoGridAccessory>>>> theAccessoryAssets
            = new HashMap<>();

    /**
     * The known JSPFs of the app, keyed by their name, then MIME types, then
     * Locale, to the Servlet Class implementing it.
     */
    protected Map<String,Map<String,L10MapImpl<Class<? extends Servlet>>>> theAppJspfs
            = new HashMap<>();

    /**
     * The known JSPFs of the accessories, keyed by their name, then MIME types, then
     * Locale, to the Servlet Class implementing it and the Accessory that provided it.
     */
    protected Map<String,Map<String,L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>>>> theAccessoryJspfs
            = new HashMap<>();

    /**
     * The known JSPOs of the app, keyed by their name, then MIME types, then Locale,
     * to the Servlet Class implementing it.
     */
    protected Map<String,Map<String,L10MapImpl<Class<? extends Servlet>>>> theAppJspos
            = new HashMap<>();

    /**
     * The known JSPOs of the accessories, keyed by their name, then MIME types, then
     * Locale, to the Servlet Class implementing it and the Accessory that provided it.
     */
    protected Map<String,Map<String,L10MapImpl<Pair<Class<? extends Servlet>,InfoGridAccessory>>>> theAccessoryJspos
            = new HashMap<>();

    /**
     * The known HttpShellHandlers of the app, keyed by their name.
     */
    protected Map<String,HttpShellHandler> theAppHandlers = new HashMap<>();

    /**
     * The known HttpShellHandlers of the accessories, keyed by their name.
     */
    protected Map<String,Pair<HttpShellHandler,InfoGridAccessory>> theAccessoryHandlers = new HashMap<>();

    /**
     * Knows how to find the right response template for the incoming request.
     */
    protected DefaultStructuredResponseTemplateFactory theResponseTemplateFactory;    
}
