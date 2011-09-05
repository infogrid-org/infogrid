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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module.servlet;

import org.infogrid.module.ModuleException;
import org.infogrid.module.ModuleRequirement;

/**
 * This exception indicates that the root ModuleAdvertisement could not be found.
 * This usually indicates a problem in the the build settings of the root Module.
 */
public class CannotFindRootModuleAdvertisementException
        extends
            ModuleException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
      * Construct one.
      *
      * @param req the ModuleRequirement that could not be met
      * @param foundAdvertisements the zero or more than one ModuleAdvertisements found
      */
    public CannotFindRootModuleAdvertisementException(
             ModuleRequirement req )
    {
        super( null, null );

        theRequirement = req;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage()
    {
        StringBuilder buf = new StringBuilder( 100 ); // fudge
        buf.append( "Could not resolve root ModuleRequirement " );
        if( theRequirement != null ) {
            buf.append( theRequirement.getRequiredModuleName() );
            if( theRequirement.getRequiredModuleVersion() != null ) {
                buf.append( ", version: " );
                buf.append( theRequirement.getRequiredModuleVersion() );
            }

        } else {
            buf.append( "null" );
        }
        buf.append( "." );
        return buf.toString();
    }

    /**
     * The ModuleRequirement that could not be met.
     */
    protected ModuleRequirement theRequirement;
}
