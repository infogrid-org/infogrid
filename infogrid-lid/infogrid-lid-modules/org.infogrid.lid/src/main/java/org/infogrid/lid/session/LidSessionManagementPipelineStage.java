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

package org.infogrid.lid.session;

import org.infogrid.lid.LidPipelineStage;

/**
 * Knows how to manage the session.
 */
public interface LidSessionManagementPipelineStage
        extends
            LidPipelineStage
{
    /**
     * Name of the LID argument for the account.
     */
    public static final String LID_ACCOUNT_PARAMETER_NAME = "lid-account";
}
