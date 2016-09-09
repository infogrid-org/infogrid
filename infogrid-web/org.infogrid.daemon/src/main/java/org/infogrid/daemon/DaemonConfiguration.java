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

package org.infogrid.daemon;

import java.io.FileInputStream;
import java.io.IOException;
import org.diet4j.core.ModuleRequirement;
import org.infogrid.web.app.WebAppConfiguration;

/**
 * Encapsulates the configuration of the InfoGrid daemon.
 */
public class DaemonConfiguration
    extends
        WebAppConfiguration
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
        return new DaemonConfiguration( configFile );
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

        theHttpServerPort    = Integer.parseInt( theProperties.getProperty( HTTP_SERVER_PORT_KEYWORD,    DEFAULT_HTTP_SERVER_PORT ));
        theHttpServerThreads = Integer.parseInt( theProperties.getProperty( HTTP_SERVER_THREADS_KEYWORD, DEFAULT_HTTP_SERVER_THREADS ));

        theAppVirtualHost    = theProperties.getProperty( APP_VIRTUAL_HOST_KEYWORD );
        theAppContextPath    = theProperties.getProperty( APP_CONTEXT_PATH_KEYWORD, DEFAULT_CONTEXT_PATH );

        theAppModuleRequirement = ModuleRequirement.create( theProperties.getProperty( APP_MODULE_KEYWORD ));

        String accString  = theProperties.getProperty( ACCESSORY_MODULES_KEYWORD, "" ).trim();
        String [] accReqs;
        if( accString.isEmpty() ) {
            // split of empty string does not return empty array
            accReqs = new String[0];
        } else {
            accReqs = accString.split( "\\s*,\\s*" );
        }
        theAccessoryModuleRequirements = new ModuleRequirement[ accReqs.length ];
        for( int i=0 ; i<accReqs.length ; ++i ) {
            theAccessoryModuleRequirements[i] = ModuleRequirement.create( accReqs[i] );
        }
        
        theDatabaseConnectionString = theProperties.getProperty( MAIN_DB_CONNECTION_KEYWORD );
        theMeshBaseTable            = theProperties.getProperty( MAIN_MESHBASE_DB_TABLE_KEYWORD, DEFAULT_MESHBASE_DB_TABLE );
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

    /**
     * Requested HTTP server port.
     */
    private final int theHttpServerPort;
    
    /**
     * Requested number of threads for the HTTP server.
     */
    private final int theHttpServerThreads;

    /**
     * Keyword in the config file indicating the port at which to run the HTTP server.
     */    
    public static final String HTTP_SERVER_PORT_KEYWORD = "HttpServerPort";
    
    /**
     * Default HTTP server port.
     */
    public static final String DEFAULT_HTTP_SERVER_PORT = "8081";
    
    /**
     * Keyword in the config file indicating the number of threads to use for the
     * HTTP server.
     */
    public static final String HTTP_SERVER_THREADS_KEYWORD = "HttpServerThreads";

    /**
     * Default number of threads to use for the HTTP server.
     * Use the same as Jetty's default.
     */
    public static final String DEFAULT_HTTP_SERVER_THREADS = "200";

    /**
     * Keyword in the config file indicating the virtual host for the app.
     */
    public static final String APP_VIRTUAL_HOST_KEYWORD = "AppVirtualHost";

    /**
     * Keyword in the config file indicating the context path for the app.
     */    
    public static final String APP_CONTEXT_PATH_KEYWORD = "AppContextPath";
    
    /**
     * Default context path.
     */
    public static final String DEFAULT_CONTEXT_PATH = "";

    /**
     * Keyword in the config file indicating the name of the app's top module.
     */
    public static final String APP_MODULE_KEYWORD = "AppModule";

    /**
     * Keyword in the config file indicating the names of the app's accessories,
     * separated by commas.
     */
    public static final String ACCESSORY_MODULES_KEYWORD = "AccessoryModules";

    /**
     * Keyword in the config file indicating the connection string to the main
     * database.
     */
    public static final String MAIN_DB_CONNECTION_KEYWORD = "MainDbConnection";
    
    /**
     * Keyword in the config file indicating the name of the table in the main
     * database that contains the main MeshBase's MeshObjects.
     */
    public static final String MAIN_MESHBASE_DB_TABLE_KEYWORD = "MainMeshBaseDbTable";
    
    /**
     * Default name of the table that contains the main MeshBase's MeshObjects.
     */
    public static final String DEFAULT_MESHBASE_DB_TABLE = "MeshObjects";
}
