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
 * <p>Supported by events that are potentially serialized / externalized. May be used
 *    in addition or as an alternative to <code>java.util.EventObject</code>.</p>
 * <p>Similarly to <code>java.util.EventObject</code>, <code>ExternalizableEvent</code>s
 *    has a source, of type <code>S</code>. The <code>DeltaValue</code> property
 *    holds the difference that the event indicates, which is of type <code>T</code>.</p>
 * <p>Unlike <code>java.util.EventObject</code>, objects supporting this interface
 *    also carry two identifiers, of type <code>SID</code> and <code>VID</code>,
 *    respectively. These are identifiers for the source and the delta value,
 *    which enables a receiver of this event to re-create source and/or delta value
 *    if those could not be serialized directly.</p>
 *
 */
public interface ExternalizableEvent<S,SID,V,VID>
{
    /**
     * Obtain the source of the event.
     * 
     * @throws UnresolvedException if this ExternalizableEvent was serialized/deserialized, and no resolver has been set.
     */
    public S getSource()
        throws
            UnresolvedException.Source;
    
    /**
     * Obtain the source identifier of the event.
     *
     * @return the source identifier
     */
    public SID getSourceIdentifier();
    
    /**
     * Obtain the delta value of the data item whose change triggered the event.
     * 
     * @return the delta value
     * @throws UnresolvedException if this ExternalizableEvent was serialized/deserialized, and no resolver has been set.
     */
    public V getDeltaValue()
        throws
            UnresolvedException.Value;

    /**
     * Obtain the delta-value identifier of the event.
     *
     * @return the delta-value identifier
     */
    public VID getDeltaValueIdentifier();

    /**
     * Obtain the time at which the event occurred.
     *
     * @return the time at which the event occurred, in System.currentTimeMillis() format
     */
    public long getTimeEventOccurred();
}

