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

package org.infogrid.module;

/**
 * Collects the tag names in the dependencies of the ModuleAdvertisements' XML format.
 */
public interface ModuleDependenciesXmlTags
{
    /**
     * XML tag: dependencies.
     */
    public static final String DEPENDENCIES_TAG = "dependencies";

    /**
     * XML tag: requires.
     */
    public static final String REQUIRES_TAG = "requires";

    /**
     * XML tag: parameter.
     */
    public static final String PARAMETER_TAG = "parameter";

    /**
     * XML parameter: name.
     */
    public static final String NAME_PAR = "name";

    /**
     * XML parameter: version.
     */
    public static final String VERSION_PAR = "version";

    /**
     * XML parameter: mode.
     */
    public static final String MODE_PAR = "mode";

    /**
     * XML parameter value: both.
     */
    public static final String MODE_PAR_BOTH = "both";

    /**
     * XML parameter value: buildtime.
     */
    public static final String MODE_PAR_BUILDTIME = "buildtime";

    /**
     * XML parameter value: runtime.
     */
    public static final String MODE_PAR_RUNTIME = "runtime";

    /**
     * XML parameter: default.
     */
    public static final String DEFAULT_PAR = "default";

    /**
     * XML parameter: value.
     */
    public static final String VALUE_PAR = "value";
}
