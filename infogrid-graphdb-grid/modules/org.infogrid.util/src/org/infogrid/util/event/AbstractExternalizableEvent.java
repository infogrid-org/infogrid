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

import org.infogrid.util.StringHelper;

import java.util.EventObject;

/**
 * A general-purpose event implementation. It inherits from EventObject in
 * order to be compatible with the Java APIs.
 */
public abstract class AbstractExternalizableEvent<S,SID,V,VID>
        extends
            EventObject
        implements
            ExternalizableEvent<S,SID,V,VID>
{
    /**
     * Constructor.
     */
    protected AbstractExternalizableEvent(
            S    source,
            SID  sourceIdentifier,
            V    deltaValue,
            VID  deltaValueIdentifier,
            long timeEventOccurred )
    {
        super( DUMMY_SENDER );

        theSource             = source;
        theSourceIdentifier   = sourceIdentifier;
        theDeltaValue           = deltaValue;
        theDeltaValueIdentifier = deltaValueIdentifier;
        theTimeEventOccurred  = timeEventOccurred;
    }

    /**
     * Obtain the source of the event. This may throw an UnresolvedException.
     *
     * @return the source of the event
     */
    public final synchronized S getSource()
        throws
            UnresolvedException
    {
        if( theSource == null ) {
            theSource = resolveSource();
        }
        return theSource;
    }
    
    /**
     * Obtain the source identifier of the event.
     *
     * @return the source identifier
     */
    public final SID getSourceIdentifier()
    {
        return theSourceIdentifier;
    }
    
    /**
     * Obtain the new value of the data item whose change triggered the event.
     *
     * @return the new value of the data item
     */
    public final synchronized V getDeltaValue()
    {
        if( theDeltaValue == null ) {
            theDeltaValue = resolveValue( getDeltaValueIdentifier() );
        }
        return theDeltaValue;
    }
    
    /**
     * Obtain the delta-value identifier of the event.
     *
     * @return the delta-value identifier
     */
    public final VID getDeltaValueIdentifier()
    {
        return theDeltaValueIdentifier;
    }

    /**
     * Enable subclass to resolve the source of the event.
     *
     * @return the source of the event
     */
    protected abstract S resolveSource();
    
    /**
     * Enable subclass to resolve a value of the event.
     *
     * @return a value of the event
     */
    protected abstract V resolveValue(
            VID vid );
    
    /**
     * Obtain the time at which the event occurred.
     *
     * @return the time at which the event occurred, in System.currentTimeMillis() format
     */
    public final long getTimeEventOccurred()
    {
        return theTimeEventOccurred;
    }

    /**
     * Clear cached objects to force a re-resolve.
     */
    protected void clearCachedObjects()
    {
        theSource     = null;
        theDeltaValue = null;
    }

    /**
     * Return in string form, for debugging.
     *
     * @return this instance in string form
     */
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theSource",
                    "theSourceIdentifier",
                    "theDeltaValue",
                    "theDeltaValueIdentifier",
                    "theTimeEventOccurred"
                },
                new Object[] {
                    theSource,
                    theSourceIdentifier,
                    theDeltaValue,
                    theDeltaValueIdentifier,
                    theTimeEventOccurred
                });
    }

    /**
     * The source of the event.
     */
    private transient S theSource;
    
    /**
     * The identifier for the source of the event.
     */
    private SID theSourceIdentifier;
    
    /**
     * The delta value of the data item whose change triggered the event.
     */
    private transient V theDeltaValue;
    
    /**
     * The identifier for the delta value of the data item whose change triggered the event.
     */
    private VID theDeltaValueIdentifier;

    /**
     * The time at which the event occurred, in System.currentTimeMillis format.
     */
    private long theTimeEventOccurred;
    
    /**
     * Object we use as a source for java.util.EventObject instead of the real one,
     * because java.util.EventObject is rather broken.
     */
    private static final Object DUMMY_SENDER = new Object();
}
