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
 *
 * @author jernst
 */
public class ModuleRegistryMeta
{
    public static ModuleRegistryMeta create(
            File              source,
            ArrayList<String> paths )
    {
        return new ModuleRegistryMeta( source, paths );
    }

    protected ModuleRegistryMeta(
            File              source,
            ArrayList<String> paths )
    {
        theSource = source;
        thePaths  = paths;
    }

    public File getSource()
    {
        return theSource;
    }

    public Iterable<String> getModuleAdvertisementPaths()
    {
        return thePaths;
    }

    protected File theSource;
    protected ArrayList<String> thePaths;
}
