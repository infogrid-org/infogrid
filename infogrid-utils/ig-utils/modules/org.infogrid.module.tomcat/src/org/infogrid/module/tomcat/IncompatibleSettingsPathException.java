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

import java.io.File;

/**
 * Thrown if a second app is attempting to share a ModuleRegistry created by a first app, but they have been
 * configured with incompatible settings paths.
 */
public class IncompatibleSettingsPathException
    extends
        Exception
{
    /**
     * Constructor.
     *
     * @param requestedSettingsPath the settings path specified by the second path
     * @param requestedShareName the name of the share
     * @param foundModuleRegistry the ModuleRegistry found, which had been instantiated by the first app
     */
    public IncompatibleSettingsPathException(
            File                 requestedSettingsPath,
            String               requestedShareName,
            TomcatModuleRegistry foundModuleRegistry )
    {
        theRequestedSettingsPath = requestedSettingsPath;
        theRequestedShareName    = requestedShareName;
        theFoundModuleRegistry   = foundModuleRegistry;
    }

    /**
     * Obtain the requested settings path.
     *
     * @return the path
     */
    public File getRequestedSettingsPath()
    {
        return theRequestedSettingsPath;
    }

    /**
     * Obtain the requested share name.
     *
     * @return the share name
     */
    public String getRequestedShareName()
    {
        return theRequestedShareName;
    }

    /**
     * Obtain the found TomcatModuleRegistry.
     *
     * @return the TomcatModuleRegistry
     */
    public TomcatModuleRegistry getFoundModuleRegistry()
    {
        return theFoundModuleRegistry;
    }

    /**
     * Convert to String, for error reporting and debugging.
     *
     * @return String format
     */
    @Override
    public String toString()
    {
        return "IncompatibleSettings: requested share " + theRequestedShareName + " with settings path " + theRequestedSettingsPath
                + " found incompatible TomcatModuleRegistry with settings path " + theFoundModuleRegistry.getSettingsPath();
    }

    /**
     * The requested settings path.
     */
    protected File theRequestedSettingsPath;

    /**
     * The requested share name.
     */
    protected String theRequestedShareName;

    /**
     * The found TomcatModuleRegistry.
     */
    protected TomcatModuleRegistry theFoundModuleRegistry;
}
