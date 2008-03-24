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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.taglib.mesh;

import org.infogrid.mesh.MeshObject;

/**
 * <p>Tag that shows the duration since a <code>MeshObject</code> was last udpated.</p>
 */
public class MeshObjectSinceTimeUpdatedTag
    extends
        AbstractMeshObjectDurationTag
{
    /**
     * Constructor.
     */
    public MeshObjectSinceTimeUpdatedTag()
    {
        // noop
    }

    /**
     * This method is implemented by our respective subclasses, to obtain
     * the time that's applicable to that subclass, such as timeCreated, timeUpdated etc.
     *
     * @param obj the MeshObject
     * @return the time, in Systems.currentTimeMillis() format
     */
    protected long getRespectiveTime(
            MeshObject obj )
    {
        return obj.getTimeUpdated();
    }
}
