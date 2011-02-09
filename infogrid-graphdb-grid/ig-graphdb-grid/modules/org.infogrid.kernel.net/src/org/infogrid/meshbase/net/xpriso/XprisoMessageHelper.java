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

package org.infogrid.meshbase.net.xpriso;

import java.util.List;

/**
 * Helper methods for Xpriso messages.
 */
public abstract class XprisoMessageHelper
{
    /**
     * Keep this abstract.
     */
    private XprisoMessageHelper() {}

    /**
     * Given a list of XprisoMessages that arrived, construct a semantically equivalent but shorter list of
     * XprisoMessages. If shortening is not possible, this will return the same list.
     *
     * @param candidates the candidates to be consolidated
     * @return the consolidated list
     */
    public static List<XprisoMessage> consolidate(
            List<XprisoMessage> candidates )
    {
        return candidates; // FIXME
    }

}
