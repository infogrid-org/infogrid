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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.app;

import org.infogrid.util.ResourceHelper;

/**
 * Superclass of InfoGridApp and InfoGridAccessory.
 */
public abstract class InfoGridInstallable
{
    /**
     * Obtain the name of the installable.
     *
     * @return the name
     */
    public String getName()
    {
        return ResourceHelper.getInstance( getClass() ).getResourceStringOrNull( "Name" );
    }

    /**
     * Obtain the user-visible name of the installable.
     *
     * @return the user-visible name
     */
    public String getUserVisibleName()
    {
        return ResourceHelper.getInstance( getClass() ).getResourceStringOrNull( "UserVisibleName" );
    }
}
