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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import org.apache.catalina.loader.WebappClassLoader;
import org.infogrid.module.Module;
import org.infogrid.module.ModuleClassLoader;

/**
 * ClassLoader that acts as the ClassLoader for the root Module.
 */
public class TomcatWebAppClassLoader
        extends
            WebappClassLoader
{
    public TomcatWebAppClassLoader(
            ClassLoader parent )
    {
        super( parent );
    }

    /**
     * Initialize with the parameters given.
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public void initialize(
            Module [] dependencies )
        throws
            MalformedURLException
    {
        theDependencyClassLoaders = new ModuleClassLoader[ dependencies.length ];

        for( int i=0 ; i<dependencies.length ; ++i ) {
            theDependencyClassLoaders[i] = (ModuleClassLoader)dependencies[i].getClassLoader();
        }
    }

    /**
     * Override so Tomcat can pass the right classpaths on to Jasper and javac.
     */
    @Override
    public URL [] getURLs()
    {
        if( theDependencyClassLoaders == null || theDependencyClassLoaders.length == 0 ) {
            return super.getURLs();
        }

        HashSet<URL> set = new HashSet<URL>(); // avoid duplicates

        for( URL current : super.getURLs() ) {
            set.add( current );
        }

        for( ModuleClassLoader loader : theDependencyClassLoaders ) {
            try {
                loader.addModuleJarUrls( set );

            } catch( MalformedURLException ex ) {
                throw new RuntimeException( ex ); // FIXME
            }
        }

        URL [] ret = new URL[ set.size() ];
        int    i   = 0;
        for( URL current : set ) {
            ret[i++] = current;
        }
        return ret;
    }

    /**
     * Find a resource through this ClassLoader. First look for our local resources, then in
     * our dependencyClassLoaders.
     *
     * @param name name of the resource to find
     * @return URL to the resource
     */
    @Override
    public URL getResource(
            String name )
    {
        URL ret = super.getResource( name );
        if( ret != null ) {
            return ret;
        }

        if( theDependencyClassLoaders != null ) {
            for( int i=0 ; i<theDependencyClassLoaders.length ; ++i ) {
                ret = theDependencyClassLoaders[i].getResource( name );
                if( ret != null ) {
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * Obtain an Enumeration of Resources.
     *
     * @param name the name of the Resource
     * @return the Enumeration
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public Enumeration<URL> getResources(
            String name )
        throws
            IOException
    {
        ClassLoader p = getParent();

        URL localResource = getResource( name );
        if( localResource != null ) {
            return new ModuleClassLoader.CompoundIterator<URL>( localResource, p.getResources( name ));
        } else {
            return p.getResources( name );
        }
    }

    /**
     * Find the resource with the given name, and return an input stream
     * that can be used for reading it.  The search order is as described
     * for <code>getResource()</code>, after checking to see if the resource
     * data has been previously cached.  If the resource cannot be found,
     * return <code>null</code>.
     *
     * @param name Name of the resource to return an input stream for
     */
    @Override
    public InputStream getResourceAsStream(
            String name )
    {
        InputStream ret = findLoadedResource( name );
        if( ret != null ) {
            return ret;
        }

        ret = super.getResourceAsStream( name );
        if( ret != null ) {
            return ret;
        }

        for( int i=0 ; i<theDependencyClassLoaders.length ; ++i ) {
            ret = theDependencyClassLoaders[i].getResourceAsStream( name );
            if( ret != null ) {
                return ret;
            }
        }

        return null;
    }

    /**
     * Override loadClass() per comment above.
     *
     * @param name name of the to-be-loaded class
     * @param resolve do we also resolve the class
     * @return the loaded class
     * @throws ClassNotFoundException loading the class failed, it could not be found
     */
    @Override
    public synchronized Class loadClass(
            String  name,
            boolean resolve )
        throws
            ClassNotFoundException
    {
        Class c = findLoadedClass( name );
        if( c == null ) {
            try {
                c = super.loadClass( name, resolve );
            } catch( ClassNotFoundException ex ) {
                // do nothing
            }

            if( c == null ) {
                for( int i=0 ; i<theDependencyClassLoaders.length ; ++i ) {
                    try {
                        c = theDependencyClassLoaders[i].loadClass( name, false );
                    } catch( ClassNotFoundException ex ) {
                        // do nothing
                    }
                    if( c != null ) {
                        break;
                    }
                }
            }
        }
        if( c == null ) {
            throw new ClassNotFoundException( name );
        }

        if( resolve ) {
            resolveClass( c );
        }

        return c;
    }

    /**
     * Find a URL.
     *
     * @param name the name of the resource
     * @return the URL of the resource, if found
     */
    @Override
    public synchronized URL findResource(
            String name )
    {
        URL ret = super.findResource( name );
        return ret;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * The set of ModuleClassLoaders from the dependent Modules. Allocated as needed.
     */
    protected ModuleClassLoader [] theDependencyClassLoaders = null;
}
