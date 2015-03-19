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

package org.infogrid.util.logging.log4j;

import java.io.BufferedInputStream;
import java.util.Map;
import java.util.Properties;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.LogFactory;

/**
 * The initializer class for this Module.
 */
public abstract class ModuleInit
{
    /**
     * Initialize this Module.
     * 
     * @return Module-specific return value
     * @throws Exception may throw a range of Exceptions
     */
    public static Object activate()
        throws
            Exception
    {
        LogFactory factory = new Log4jLogFactory();
        Log.setLogFactory( factory );

        return factory;
    }

    /**
     * Configure this Module.
     *
     * @param parameters the parameters for initialization
     * @param loader ClassLoader to use to resolve resources
     * @throws Exception may throw a range of Exceptions
     */
    public static void configure(
            Map<String,Object> parameters,
            ClassLoader        loader )
        throws
            Exception
    {
        if( parameters == null ) {
            return;
        }

        String configFile  = (String) parameters.get( CONFIG_PROPERTIES_FILE_PARAMETER_NAME );
        if( configFile == null ) {
            throw new Exception(
                    "Missing configuration parameter " + CONFIG_PROPERTIES_FILE_PARAMETER_NAME );
        }

        Properties logProperties = new Properties();
        try {
            logProperties.load( new BufferedInputStream(
                    loader.getResourceAsStream( configFile )));

        } catch( Throwable ex ) {
            throw new Exception(
                    "Log4j configuration file " + configFile + " could not be loaded using ClassLoader " + loader );
        }
        try {
            Log4jLog.configure( logProperties );
            // which logger is being used is defined in the module dependency declaration through parameters
        } catch( Throwable ex ) {
            // This can happen, for example, when a file could not be written
            throw new Exception(
                    "Log4j configuration failed", ex );
        }
    }

    /**
     * Name of the parameter in the ModuleAdvertisement that holds the name of the Log4j
     * properties file. This must be given in a format so it can loaded with
     * <tt>ClassLoader.getResourceAsStream</tt>.
     */
    public static final String CONFIG_PROPERTIES_FILE_PARAMETER_NAME = "org.infogrid.util.logging.log4j.ConfigPropertiesFile";
}
