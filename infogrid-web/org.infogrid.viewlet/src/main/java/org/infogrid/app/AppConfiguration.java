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

package org.infogrid.app;

import java.util.Properties;
import org.diet4j.core.ModuleRequirement;

/**
 * Carries information about the configuration of an application.
 */
public abstract class AppConfiguration
{
    /**
     * The module for the app, expressed as a ModuleRequirement.
     * 
     * @return the root module requirement
     */
    public ModuleRequirement getAppModuleRequirement()
    {
        return theAppModuleRequirement;
    }
    
    /**
     * The set of accessory modules for the app, expressed as ModuleRequirments.
     * 
     * @return the set of accessory module requirements
     */
    public ModuleRequirement [] getAccessoryModuleRequirements()
    {
        return theAccessoryModuleRequirements;
    }

    /**
     * Determine the database connection string to use to connect to the
     * primary database.
     * 
     * @return the connection string to connect to the main database.
     */
    public String getDatabaseConnectionString()
    {
        return theDatabaseConnectionString;
    }
    
    /**
     * Determine the name of the table in the main database containing the
     * main MeshBase's MeshObjects.
     * 
     * @return the main MeshBase's database table
     */
    public String getMeshBaseTable()
    {
        return theMeshBaseTable;
    }

    /**
     * Obtain the value of a named property in this configuration.
     * 
     * @param name name of the property
     * @return value of the property, or null
     */
    public String getProperty(
            String name )
    {
        return theProperties.getProperty( name );
    }

    /**
     * Obtain the value of a named property in this configuration, or the provided
     * default value.
     * 
     * @param name name of the property
     * @param defaultValue default value of the property
     * @return value of the property, or the default value
     */
    public String getProperty(
            String name,
            String defaultValue )
    {
        return theProperties.getProperty( name, defaultValue );
    }

    /**
     * Set of properties known by this AppConfiguration.
     */
    protected Properties theProperties = new Properties();

    /**
     * Application module.
     */
    protected ModuleRequirement theAppModuleRequirement;
    
    /**
     * Accessory modules, if any.
     */
    protected ModuleRequirement [] theAccessoryModuleRequirements;

    /**
     * The connection string to connect to the main database.
     */
    protected String theDatabaseConnectionString;
    
    /**
     * The name of the table in the main database containing the main MeshBase's
     * MeshObjects.
     */
    protected String theMeshBaseTable;
}
