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

package org.infogrid.daemon;

import java.io.FileInputStream;
import java.io.IOException;
import org.diet4j.core.ModuleRequirement;
import org.infogrid.web.app.AppConfiguration;

/**
 * Encapsulates the configuration of the InfoGrid daemon.
 */
public final class DaemonConfiguration
    extends
        AppConfiguration
{
    /**
     * Factory method.
     * 
     * @param configFile the config file containing the configuration
     * @return the configuration object
     * @throws IOException the config file could not be read
     */
    public static DaemonConfiguration create(
            String configFile )
        throws
            IOException
    {
        if( theSingleton == null ) {
            theSingleton = new DaemonConfiguration( configFile );
        }
        return theSingleton;
    }

    /**
     * Constructor.
     * 
     * @param configFile the config file containing the configuration
     * @throws IOException the config file could not be read
     */
    private DaemonConfiguration(
            String configFile )
        throws
            IOException
    {
        if( configFile != null ) {
            theProperties.load( new FileInputStream( configFile ));
        }

        theHttpServerPort    = Integer.parseInt( theProperties.getProperty( "HttpServerPort",    "8081" ));
        theHttpServerThreads = Integer.parseInt( theProperties.getProperty( "HttpServerThreads", "200" )); // same as in Jetty default
        theAppVirtualHost    = theProperties.getProperty( "AppVirtualHost" );                              // any
        theAppContextPath    = theProperties.getProperty( "AppContextPath", "" );                          // root of site

        theAppModuleRequirement = ModuleRequirement.create( theProperties.getProperty( "AppModule" ));

        String [] accReqs = theProperties.getProperty( "AccessoryModules", "" ).trim().split( "\\s+,\\s+" );
        theAccessoryModuleRequirements = new ModuleRequirement[ accReqs.length ];
        for( int i=0 ; i<accReqs.length ; ++i ) {
            theAccessoryModuleRequirements[i] = ModuleRequirement.create( accReqs[i] );
        }
    }

    /**
     * Obtain the HTTP server port to run the daemon at.
     * 
     * @return the port number
     */
    public int getHttpServerPort()
    {
        return theHttpServerPort;
    }

    /**
     * Obtain the number of threads to start for the HTTP server.
     * 
     * @return the number of threads
     */
    public int getHttpServerThreads()
    {
        return theHttpServerThreads;
    }

    private final int theHttpServerPort;
    private final int theHttpServerThreads;

    private static DaemonConfiguration theSingleton;
}
