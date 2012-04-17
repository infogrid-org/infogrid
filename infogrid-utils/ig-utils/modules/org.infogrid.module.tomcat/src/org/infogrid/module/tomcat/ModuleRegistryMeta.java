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
import java.util.ArrayList;

/**
 * Collects the ModuleRegistryMeta information. It captures the paths where ModuleAdvertisements
 * may be found.
 */
public class ModuleRegistryMeta
{
    /**
     * Factory method.
     *
     * @param source the file that was read to create this instance of ModuleRegistryMeta
     * @param paths the paths to ModuleAdvertisments that were found
     * @return the created ModuleRegistryMeta
     */
    public static ModuleRegistryMeta create(
            File              source,
            ArrayList<String> paths )
    {
        return new ModuleRegistryMeta( source, paths );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param source the file that was read to create this instance of ModuleRegistryMeta
     * @param paths the paths to ModuleAdvertisments that were found
     */
    protected ModuleRegistryMeta(
            File              source,
            ArrayList<String> paths )
    {
        theSource = source;
        thePaths  = paths;
    }

    /**
     * Obtain the file that was read to create this object.
     *
     * @return the File
     */
    public File getSource()
    {
        return theSource;
    }

    /**
     * Return the paths at which ModuleAdvertisements may be found
     *
     * @return an Iterable over the paths
     */
    public Iterable<String> getModuleAdvertisementPaths()
    {
        return thePaths;
    }

    /**
     * The file that was read to create this object.
     */
    protected File theSource;

    /**
     * The paths to ModuleAdvertisments that were found. This may contain wildcards.
     */
    protected ArrayList<String> thePaths;
}
