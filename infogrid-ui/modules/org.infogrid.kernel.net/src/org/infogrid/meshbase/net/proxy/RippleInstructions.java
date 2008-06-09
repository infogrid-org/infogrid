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

package org.infogrid.meshbase.net.proxy;

import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.util.StringHelper;

/**
 * 
 */
public class RippleInstructions
{
    public void setExternalizedNetMeshObject(
            ExternalizedNetMeshObject newValue )
    {
        theExternalizedNetMeshObject = newValue;
    }
    public ExternalizedNetMeshObject getExternalizedNetMeshObject()
    {
        return theExternalizedNetMeshObject;
    }
    
    public void setProxies(
            Proxy [] newValue )
    {
        theProxies = newValue;
    }
    public Proxy [] getProxies()
    {
        return theProxies;
    }

    public void setProxyTowardsLockIndex(
            int newValue )
    {
        theProxyTowardsLockIndex = newValue;
    }
    public int getProxyTowardsLockIndex()
    {
        return theProxyTowardsLockIndex;
    }

    public void setProxyTowardsHomeIndex(
            int newValue )
    {
        theProxyTowardsHomeIndex = newValue;
    }
    public int getProxyTowardsHomeIndex()
    {
        return theProxyTowardsHomeIndex;
    }
    
    /**
     * Convert to String representation, for debugging.
     * 
     * @return String respresentation
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theExternalizedNetMeshObject",
                    "theProxies",
                    "theProxyTowardsLockIndex",
                    "theProxyTowardsHomeIndex"
                },
                new Object[] {
                    theExternalizedNetMeshObject,
                    theProxies,
                    theProxyTowardsLockIndex,
                    theProxyTowardsHomeIndex
                },
                StringHelper.LOG_FLAGS.SHOW_NON_NULL | StringHelper.LOG_FLAGS.SHOW_NON_ZERO );
    }
    protected ExternalizedNetMeshObject theExternalizedNetMeshObject;
    protected Proxy [] theProxies;
    protected int theProxyTowardsLockIndex = -1;
    protected int theProxyTowardsHomeIndex = -1;
}
