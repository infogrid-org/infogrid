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

import java.util.ArrayList;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.StringHelper;

/**
 *
 */
public class ResynchronizeInstructions
{
    public void addNetMeshObjectIdentifier(
            NetMeshObjectIdentifier toAdd )
    {
        theNetMeshObjectIdentifiers.add( toAdd );
    }
    
    public NetMeshObjectIdentifier [] getNetMeshObjectIdentifiers()
    {
        return ArrayHelper.copyIntoNewArray( theNetMeshObjectIdentifiers, NetMeshObjectIdentifier.class );
    }
    
    public void setProxyIdentifier(
            NetMeshBaseIdentifier newValue )
    {
        theProxyIdentifier = newValue;
    }
    public NetMeshBaseIdentifier getProxyIdentifier()
    {
        return theProxyIdentifier;
    }
    
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theProxyIdentifier",
                    "theNetMeshObjectIdentifiers"
                },
                new Object[] {
                    theProxyIdentifier,
                    theNetMeshObjectIdentifiers
                },
                StringHelper.LOG_FLAGS.SHOW_ALL);
    }
            
    protected ArrayList<NetMeshObjectIdentifier> theNetMeshObjectIdentifiers = new ArrayList<NetMeshObjectIdentifier>();
    protected NetMeshBaseIdentifier theProxyIdentifier;
}

