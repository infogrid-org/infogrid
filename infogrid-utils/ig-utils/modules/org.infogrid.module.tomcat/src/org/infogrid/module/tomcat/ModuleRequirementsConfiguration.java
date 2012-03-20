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
import java.util.HashMap;
import java.util.Map;
import org.infogrid.module.ModuleRequirement;

/**
 * Captures a set of ModuleRequirements and a map of repositories where Modules may be found.
 */
public class ModuleRequirementsConfiguration
{
    /**
     * Factory method.
     *
     * @param repositories map of share name to location of a Module repository
     * @param requirements the set of ModuleRequirements
     * @return the created ModuleRequirementsConfiguration
     */
    public static ModuleRequirementsConfiguration create(
            HashMap<String,File> repositories,
            ModuleRequirement [] requirements )
    {
        return new ModuleRequirementsConfiguration( repositories, requirements );
    }

    /**
     * Private onstructor, use factory method.
     *
     * @param repositories map of share name to location of a Module repository
     * @param requirements the set of ModuleRequirements
     */
    protected ModuleRequirementsConfiguration(
            HashMap<String,File> repositories,
            ModuleRequirement [] requirements )
    {
        theRepositories = repositories;
        theRequirements = requirements;
    }

    /**
     * Obtain the set of repositories and their share names.
     *
     * @return return the set
     */
    public Iterable<Map.Entry<String,File>> repositories()
    {
        return theRepositories.entrySet();
    }

    /**
     * Obtain the set of ModuleRequirements.
     *
     * @return the ModuleRequirements
     */
    public ModuleRequirement [] requirements()
    {
        return theRequirements;
    }

    /**
     * The repositories.
     */
    protected HashMap<String,File> theRepositories;

    /**
     * The ModuleRequirements.
     */
    protected ModuleRequirement [] theRequirements;
}
