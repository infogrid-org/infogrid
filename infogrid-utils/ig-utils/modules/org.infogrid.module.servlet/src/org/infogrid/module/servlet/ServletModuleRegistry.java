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

package org.infogrid.module.servlet;

import java.io.IOException;
import java.util.ArrayList;
import org.infogrid.module.Module;
import org.infogrid.module.ModuleAdvertisement;
import org.infogrid.module.ModuleAdvertisementInstantiator;
import org.infogrid.module.ModuleErrorHandler;
import org.infogrid.module.ModuleRegistry;
import org.infogrid.module.ModuleRequirement;

/**
 * A ModuleRegistry particularly appropriate for servlet environments.
 */
public class ServletModuleRegistry
    extends
        ModuleRegistry
{
    /**
      * Factory method to construct a ModuleRegistry from the information provided
      * by this SoftwareInstallation.
      *
      * @param theInstallation the SoftwareInstallation that knows all the parameters
      * @return the created ModuleRegistry
      * @throws CannotFindRootModuleAdvertisementException thrown if the root ModuleAdvertisement cannot be found
      * @throws IOException thrown when we were unsuccessful reading a specified ModuleAdvertisement file
      * @throws ClassNotFoundException thrown when we were trying to read a ModuleAdvertisement subclass that was not available locally
      */
    public static ServletModuleRegistry create(
            ServletSoftwareInstallation theInstallation )
        throws
            CannotFindRootModuleAdvertisementException,
            IOException,
            ClassNotFoundException
    {
        ServletModuleRegistry registry = new ServletModuleRegistry();

        ModuleAdvertisement rootAd = registry.loadModuleAdvertisementRecursively( theInstallation.getRootModuleRequirement() );
        // rootAd only there for debugging

        if( rootAd == null ) {
            throw new CannotFindRootModuleAdvertisementException( theInstallation.getRootModuleRequirement() );
        }
        return registry;
    }

    /**
     * Private constructor, use factory method.
     */
    protected ServletModuleRegistry()
    {
        super( new ArrayList<ModuleAdvertisement>() );
    }
    
    /**
     * Recursively load a ModuleAdvertisement by looking in the right places.
     *
     * @param req the ModuleRequirement to meet
     * @return the found ModuleAdvertisement, or null
     */
    public ModuleAdvertisement loadModuleAdvertisementRecursively(
            ModuleRequirement req )
    {
        ModuleAdvertisement ret = null;
        Class               theClass;
        StringBuilder       className = new StringBuilder();
        try {

            className.append( req.getRequiredModuleName() );
            className.append( ".module.V" );
            if( req.getRequiredModuleVersion() != null ) {
                className.append( req.getRequiredModuleVersion() );
            }
            
            theClass = Class.forName( className.toString() );
            
            ModuleAdvertisementInstantiator instantiator = (ModuleAdvertisementInstantiator) theClass.newInstance();
            
            ret = instantiator.create();

        } catch( Exception ex ) {
            ModuleErrorHandler.warn( "Could not load ModuleAdvertisementInstantiator " + className, ex );
            // FIXME? Should this be an exception? We do catch the same problem later when trying to activate the Module.
        }
        if( ret != null ) {
            for( ModuleRequirement dependentReq : ret.getRunTimeModuleRequirements() ) {
                loadModuleAdvertisementRecursively( dependentReq );
            }
            
            addAdvertisement( ret );
        }
        return ret;
    }

    /**
     * ModuleRegistry also acts as a factory for the Modules' ClassLoaders.
     *
     * @param module the Module for which to create a ClassLoader
     * @param parentClassLoader the ClassLoader to use as the parent ClassLoader
     * @return the ClassLoader to use with the Module
     */
    @Override
    public ClassLoader createClassLoader(
            Module      module,
            ClassLoader parentClassLoader )
    {
        return parentClassLoader; // keep the same ClassLoader for all of them
    }
}
