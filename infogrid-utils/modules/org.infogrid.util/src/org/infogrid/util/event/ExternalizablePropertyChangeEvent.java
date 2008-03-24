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

package org.infogrid.util.event;

/**
 *
 */
public interface ExternalizablePropertyChangeEvent<S,SID,P,PID,V,VID>
        extends
            ExternalizableEvent<S,SID,V,VID>
{
    /**
     * Obtain the property of the event. This may throw an UnresolvedException.
     *
     * @return the property of the event
     */
    public P getProperty()
        throws
            UnresolvedException;
    
    /**
     * Obtain the property identifier of the event.
     *
     * @return the property identifier
     */
    public PID getPropertyIdentifier();

    /**
     * Obtain the old value of the property prior to the event. This may throw an UnresolvedException.
     *
     * @return the old value of the property
     */
    public V getOldValue();
    
    /**
     * Obtain the new value of the property after the event. This may throw an UnresolvedException.
     *
     * @return the new value of the property
     */
    public V getNewValue();
}