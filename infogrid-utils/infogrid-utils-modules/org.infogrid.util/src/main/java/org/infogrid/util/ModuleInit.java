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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.LogFactory;

/**
 * The initializer class for this Module.
 */
public abstract class ModuleInit
{
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

        try {
            configureLogFactory( parameters, loader );
        } finally {
            configureResourceHelper( parameters, loader );
        }
    }

    /**
     * Configure the LogFactory.
     *
     * @param parameters the parameters for initialization
     * @param loader ClassLoader to use to resolve resources
     * @throws Exception may throw a range of Exceptions
     */
    public static void configureLogFactory(
            Map<String,Object> parameters,
            ClassLoader        loader )
        throws
            Exception
    {
        String className  = (String) parameters.get( LOG_FACTORY_CLASS_PARAMETER_NAME );

        if( className == null ) {
            return;
        }

        Class logFactoryClass = Class.forName( className, true, loader );

        Object logFactory = logFactoryClass.newInstance();

        Log.setLogFactory( (LogFactory) logFactory );
    }

    /**
     * Configure the ResourceHelper.
     *
     * @param parameters the parameters for initialization
     * @param loader ClassLoader to use to resolve resources
     * @throws Exception may throw a range of Exceptions
     */
    public static void configureResourceHelper(
            Map<String,Object> parameters,
            ClassLoader        loader )
        throws
            Exception
    {
        String bundleName = (String) parameters.get( RESOURCE_HELPER_APPLICATION_BUNDLE_PARAMETER_NAME );

        if( bundleName == null ) {
            return;
        }

        ResourceHelper.setApplicationResourceBundle(
                ResourceBundle.getBundle( bundleName, Locale.getDefault(), loader ));
    }

    /**
     * Name of the Module configuration parameter that specifies the name of the LogFactory class.
     */
    public static final String LOG_FACTORY_CLASS_PARAMETER_NAME = "org.infogrid.util.logging.LogFactory.Class";

    /**
     * Name of the Module configuration parameter that specifies the application ResourceBundle.
     */
    public static final String RESOURCE_HELPER_APPLICATION_BUNDLE_PARAMETER_NAME = "org.infogrid.util.ResourceHelper.ApplicationResourceBundle";
}
