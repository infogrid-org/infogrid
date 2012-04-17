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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;
import org.infogrid.module.Module;
import org.infogrid.module.ModuleAdvertisement;
import org.infogrid.module.ModuleConfigurationException;
import org.infogrid.module.ModuleErrorHandler;
import org.infogrid.module.ModuleNotFoundException;
import org.infogrid.module.ModuleRegistry;
import org.infogrid.module.ModuleRequirement;
import org.infogrid.module.ModuleResolutionCandidateNotUniqueException;
import org.infogrid.module.ModuleResolutionException;

/**
 * This Tomcat-specific class can be specified as a WebappLoader in a Tomcat context.xml file in order
 * to make the entire Tomcat app use the InfoGrid module framework. Note that the JAR of this project
 * and the JAR of the org.infogrid.module project must be accessible to Tomcat. Apparently bundling them
 * with a web app's WAR file is not sufficient. They must be contained in Tomcat's library path.
 *
 * Use as follows in context.xml:
 *
 * <pre>
 * &lt;Context path='/context-path'>
 *  &lt;Loader className='org.infogrid.module.tomcat.TomcatModuleLoader'
 *          rootmodule='com.example.mywebapp'
 *          moduleadvertisementpath='/usr/share/infogrid/*.adv:/home/~joe/ads/com.example.foo.adv' />
 * &lt;/Context>
 * </pre>
 * where <code>com.example.mywebapp</code> is the the name of the InfoGrid root module for this app,a
 * and <code>moduleadvertisementpath</code> refers to a colon-separated path where ModuleAdvertisements can be found.
 */
public class TomcatModuleLoader
    extends
        WebappLoader
{
    /**
     * Constructor.
     */
    public TomcatModuleLoader()
    {
        this( null );
    }

    /**
     * Constructor.
     *
     * @param parent the parent ClassLoader
     */
    public TomcatModuleLoader(
            ClassLoader parent )
    {
        super( parent );

        setLoaderClass( TomcatWebAppClassLoader.class.getName() );
    }

    /**
     * Set Module config file.
     *
     * @param newValue the value
     * @see #getModuleConfigFile
     */
    public void setModuleConfigFile(
            String newValue )
    {
        theModuleConfigFile = newValue;
    }

    /**
     * Get Module config file.
     *
     * @return the value
     * @see #setModuleConfigFile
     */
    public String getModuleConfigFile()
    {
        return theModuleConfigFile;
    }

    /**
     * Start it.
     *
     * @throws LifecycleException thrown if this TomcatModuleLoader cannot be started
     */
    @Override
    public void start()
        throws
            LifecycleException
    {
        if( theModuleConfigFile == null ) {
            throw new LifecycleException( "moduleConfigFile parameter not set" );
        }

        // I would have liked to invoke super.start() last but that's the only way I can get at our ClassLoader.
        super.start();

        TomcatWebAppClassLoader         myClassLoader          = (TomcatWebAppClassLoader) super.getClassLoader();
        ModuleRequirementsConfigurationXmlParser    theConfigurationParser = null;
        ModuleRequirementsConfiguration configuration          = null;
        InputStream                     theStream              = null;

        try {
           theConfigurationParser = new ModuleRequirementsConfigurationXmlParser();
           // this will throw an exception in a non-JDK 1.4 environment where XML is not built into the JDK
        } catch( Throwable t ) {
            ModuleErrorHandler.informThrowable( t );
        }

        try {
            URL moduleConfigUrl = myClassLoader.getResource( theModuleConfigFile );
            theStream = new BufferedInputStream( moduleConfigUrl.openStream() );

            configuration = theConfigurationParser.readConfiguration( theStream, null ); // FIXME?

        } catch( IOException ex ) {
            ModuleErrorHandler.warn( "Could not read ModuleRegistrySettings from " + theModuleConfigFile, ex );
            throw new LifecycleException( ex );

        } catch( ModuleConfigurationException ex ) {
            ModuleErrorHandler.warn( "Could not read ModuleRegistrySettings from " + theModuleConfigFile, ex );
            throw new LifecycleException( ex );

        } finally {
            if( theStream != null ) {
                try {
                    theStream.close();
                } catch( IOException ex2 ) {
                    // no op
                }
            }
        }

        registries = new ArrayList<ModuleRegistry>();
        try {
            for( Map.Entry<String,File> current : configuration.repositories() ) {
                ModuleRegistry found = ModuleRegistryDirectory.obtainFor( current.getValue(), current.getKey() );
                registries.add( found );
            }
        } catch( Throwable t ) {
            throw new LifecycleException( t );
        }

        ArrayList<Module> dependencies = new ArrayList<Module>();
        for( ModuleRequirement currentRequirement : configuration.requirements() ) {

            Throwable thrown = null;
            for( ModuleRegistry currentRegistry : registries ) {
                try {
                    ModuleAdvertisement foundAd     = currentRegistry.determineSingleResolutionCandidate( currentRequirement );
                    Module              foundModule = currentRegistry.resolve( foundAd );

                    dependencies.add( foundModule );

                    thrown = null;
                    break; // don't to look any further

                } catch( ModuleResolutionCandidateNotUniqueException ex ) {
                    thrown = ex;
                } catch( ModuleNotFoundException ex ) {
                    thrown = ex;
                } catch( ModuleResolutionException ex ) {
                    thrown = ex;
                }
            }
            if( thrown != null ) {
                throw new LifecycleException( thrown );
            }
        }

        Module [] dependencyArray = new Module[ dependencies.size() ];
        dependencies.toArray( dependencyArray );

        try {
            myClassLoader.initialize( dependencyArray );

        } catch( Throwable ex ) {
            throw new LifecycleException( ex );
        }
    }

    /**
     * Stop it.
     *
     * @throws LifecycleException thrown if this TomcatModuleLoader cannot be stopped
     */
    @Override
    public void stop()
        throws
            LifecycleException
    {
        super.stop();
    }

    /**
     * Convert to String, for debugging.
     *
     * @return String form
     */
    @Override
    public String toString()
    {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Name of the file that contains the Module Framework configuration for this app.
     */
    protected String theModuleConfigFile;

    /**
     * Keep a reference to the ModuleRegistries that we are using so they won't be garbage collected.
     */
    protected ArrayList<ModuleRegistry> registries;

    /**
     * Keep a reference to the ModuleRegistryDirectory so it won't be garbage collected.
     */
    protected Class keep = ModuleRegistryDirectory.class;
}
