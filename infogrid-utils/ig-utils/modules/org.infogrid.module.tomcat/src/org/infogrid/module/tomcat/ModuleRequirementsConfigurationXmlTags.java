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

import org.infogrid.module.ModuleDependenciesXmlTags;

/**
 * Collects the XML tags used to specify ModuleRequirementsConfigurations.
 */
public interface ModuleRequirementsConfigurationXmlTags
    extends
        ModuleDependenciesXmlTags
{
    /**
     * XML tag: modulesettings.
     */
    public static final String MODULESETTINGS_TAG = "modulesettings";

    /**
     * XML tag: repositories.
     */
    public static final String REPOSITORIES_TAG   = "repositories";

    /**
     * XML tag: repository.
     */
    public static final String REPOSITORY_TAG     = "repository";

    /**
     * XML tag: settingspath.
     */
    public static final String SETTINGSPATH_ATT   = "settingspath";

    /**
     * XML tag: sharename.
     */
    public static final String SHARENAME_ATT      = "sharename";
}
