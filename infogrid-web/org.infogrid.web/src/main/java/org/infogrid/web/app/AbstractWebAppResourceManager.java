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

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.Servlet;
import org.infogrid.app.InfoGridInstallable;

/**
 * Factors out functionality common to implementations of WebAppResourceManager.
 */
public abstract class AbstractWebAppResourceManager
    implements
        WebAppResourceManager
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAsset(
            String              path,
            URL                 url,
            String              mime,
            InfoGridInstallable installable )
    {
        registerAsset( path, url, mime, null, installable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void registerAsset(
            String              path,
            ClassLoader         loader,
            String              mime,
            Locale              locale, 
            InfoGridInstallable installable )
    {
        String pathWithoutSlash = path.substring( 1 );

        registerAsset( path, loader.getResource( pathWithoutSlash ), mime, locale, installable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAsset(
            String              path,
            Locale              locale,
            InfoGridInstallable installable )
    {
        String mime = determineMimeFromFile( path );
        if( mime == null ) {
            throw new IllegalArgumentException( "Cannot determine content type from file extension, register explicitly: " + path );
        }
        registerAsset( path, installable.getClass().getClassLoader(), mime, locale, installable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerJspf(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        registerJspf( name, mime, null, servletClass, installable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerJspo(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable )
    {
        registerJspo( name, mime, null, servletClass, installable );
    }

    /**
     * Determine the MIME type from the name of a file. This implements a certain,
     * simple, heuristic. It is easiest for apps and accessories to follow this
     * heuristic, but if not, this method can be overridden.
     * 
     * @param name file name
     * @return MIME type, or null
     */
    @Override
    public String determineMimeFromFile(
            String name )
    {
        int lastSlash  = name.lastIndexOf( '/' );
        if( lastSlash > 0 ) {
            name = name.substring( lastSlash + 1 );
        }
        int lastPeriod = name.lastIndexOf( '.' );
        if( lastPeriod > 0 ) {
            String ret = theExtensionMimeType.get( name.substring( lastPeriod+1 ));
            return ret;
        }
        return null;
    }
    
    /**
     * Table that maps extensions to MIME types.
     */
    protected Map<String,String> theExtensionMimeType = new HashMap<>();
    {
        theExtensionMimeType.put( "txt",  "text/plain");
        theExtensionMimeType.put( "html", "text/html");
        theExtensionMimeType.put( "css",  "text/css" );
        theExtensionMimeType.put( "png",  "image/png");
        theExtensionMimeType.put( "jpg",  "image/jpg");
        theExtensionMimeType.put( "gif",  "image/gif");
        theExtensionMimeType.put( "js",   "application/javascript");
    }
}
