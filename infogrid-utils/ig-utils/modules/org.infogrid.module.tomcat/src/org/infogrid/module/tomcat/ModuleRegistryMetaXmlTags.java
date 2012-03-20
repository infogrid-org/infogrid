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

/**
 * Collects the tag names in the ModuleRegistrySettings' XML format.
 */
public interface ModuleRegistryMetaXmlTags
{
    /**
     * XML tag: moduleregistry.
     */
    public static final String MODULEREGISTRY_TAG = "moduleregistry";

    /**
     * XML tag: moduleadvertisementpath.
     */
    public static final String MODULEADVERTISEMENTPATH_TAG = "moduleadvertisementpath";

    /**
     * XML tag: path.
     */
    public static final String PATH_TAG = "path";
}
