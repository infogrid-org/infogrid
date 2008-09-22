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

package org.infogrid.lid.gpg;

import org.infogrid.lid.gpg.LidKeyPair;
import org.infogrid.util.AbstractFactory;
import org.infogrid.util.FactoryException;

/**
 * Obtains and manages LID key pairs for given identities that are maintained locally.
 */
public class LidGpgKeyPairFactory
        extends
            AbstractFactory<String,LidKeyPair,Void>
{
    /**
     * Factory method.
     *
     * @param key the key information required for object creation, if any
     * @param argument any argument-style information required for object creation, if any
     * @return the created object
     * @throws FactoryException catch-all Exception, consider its cause
     */
    public LidKeyPair obtainFor(
            String key,
            Void   argument )
        throws
            FactoryException
    {
        LidKeyPair ret = LidGpg.createKeyPair( key );
        return ret;
    }
}
