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

package org.infogrid.meshworld.jetty;

import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.diet4j.core.ModuleDeactivationException;
import org.infogrid.meshworld.MeshWorldApp;

/**
 * Diet4j module activation.
 */
public class Activate
{
    /**
     * Diet4j module activation.
     * 
     * @param thisModule the Module being activated
     * @return the app that was activated
     * @throws ModuleActivationException thrown if module activation failed
     */
    public static MeshWorldApp moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        return new MeshWorldApp();
    }

    /**
     * Diet4j module deactivation.
     * 
     * @param thisModule the Module being deactivated
     * @throws ModuleDeactivationException thrown if module deactivation failed
     */
    public static void moduleDeactivate(
            Module thisModule )
        throws
            ModuleDeactivationException
    {
        System.err.println( "Deactivating " + thisModule );
    }
}
