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

package org.infogrid.util;

/**
 * This interface is implemented by objects that support the factory pattern.
 */
public interface Factory<K,V,A>
{
    /**
     * Factory method.
     *
     * @param key the key information required for object creation, if any
     * @param argument any argument-style information required for object creation, if any
     * @return the created object
     * @throws FactoryException catch-all Exception, consider its cause
     */
    public abstract V obtainFor(
            K key,
            A argument )
        throws
            FactoryException;
}
