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

import java.beans.PropertyChangeEvent;

/**
 *
 */
public abstract class AbstractExternalizablePropertyChangeEvent<S,SID,P,PID,V,VID>
        extends
            PropertyChangeEvent
        implements
            ExternalizablePropertyChangeEvent<S,SID,P,PID,V,VID>
{
    /**
     * Constructor.
     */
    protected AbstractExternalizablePropertyChangeEvent(
            S    source,
            SID  sourceIdentifier,
            P    property,
            PID  propertyIdentifier,
            V    oldValue,
            VID  oldValueIdentifier,
            V    deltaValue,
            VID  deltaValueIdentifier,
            V    newValue,
            VID  newValueIdentifier,
            long timeEventOccurred )
    {
        super( DUMMY_SENDER, null, null, null );

        theSource               = source;
        theSourceIdentifier     = sourceIdentifier;
        theProperty             = property;
        thePropertyIdentifier   = propertyIdentifier;
        theOldValue             = oldValue;
        theOldValueIdentifier   = oldValueIdentifier;
        theDeltaValue           = deltaValue;
        theDeltaValueIdentifier = deltaValueIdentifier;
        theNewValue             = newValue;
        theNewValueIdentifier   = newValueIdentifier;
        theTimeEventOccurred    = timeEventOccurred;
    }

    /**
     * Obtain the source of the event. This may throw an UnresolvedException.
     *
     * @return the source of the event
     */
    @Override
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
     * Obtain the new-value identifier of the event.
     *
     * @return the new-value identifier
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
     * @param vid the identifier for a value of the event
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
     * Obtain the property of the event. This may throw an UnresolvedException.
     *
     * @return the property of the event
     */
    public final synchronized P getProperty()
        throws
            UnresolvedException
    {
        if( theProperty == null ) {
            theProperty = resolveProperty();
        }
        return theProperty;
    }
    
    /**
     * Obtain the property identifier of the event.
     *
     * @return the property identifier
     */
    public final PID getPropertyIdentifier()
    {
        return thePropertyIdentifier;
    }

    /**
     * Enable subclass to resolve the property of the event.
     *
     * @return the property of the event
     */
    protected abstract P resolveProperty();
    
    /**
     * Obtain the old value of the property prior to the event. This may throw an UnresolvedException.
     *
     * @return the old value of the property
     */
    @Override
    public final synchronized V getOldValue()
    {
        if( theOldValue == null ) {
            theOldValue = resolveValue( getOldValueIdentifier() );
        }
        return theOldValue;
    }
    
    /**
     * Obtain the new value of the property after the event. This may throw an UnresolvedException.
     *
     * @return the new value of the property
     */
    @Override
    public final synchronized V getNewValue()
    {
        if( theNewValue == null ) {
            theNewValue = resolveValue( getNewValueIdentifier() );
        }
        return theNewValue;
    }
    
    /**
     * Obtain the old-value identifier of the event.
     *
     * @return the old-value identifier
     */
    public VID getOldValueIdentifier()
    {
        return theOldValueIdentifier;
    }

    /**
     * Obtain the new-value identifier of the event.
     *
     * @return the new-value identifier
     */
    public VID getNewValueIdentifier()
    {
        return theNewValueIdentifier;
    }

    /**
     * Clear cached objects to force a re-resolve.
     */
    protected void clearCachedObjects()
    {
        theSource     = null;
        theOldValue   = null;
        theDeltaValue = null;
        theNewValue   = null;
        theProperty   = null;
    }

    /**
     * Return in string form, for debugging.
     *
     * @return this instance in string form
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "getSourceIdentifier()",
                    "theProperty",
                    "thePropertyIdentifier",
                    "getOldValueIdentifier()",
                    "getNewValueIdentifier()",
                    "getTimeEventOccurred()"
                },
                new Object[] {
                    getSourceIdentifier(),
                    theProperty,
                    thePropertyIdentifier,
                    getOldValueIdentifier(),
                    getNewValueIdentifier(),
                    getTimeEventOccurred()
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
     * The old value of the data item whose change triggered the event.
     */
    private transient V theOldValue;

    /**
     * The identifier for the old value of the data item whose change triggered the event.
     */
    private VID theOldValueIdentifier;

    /**
     * The delta value of the data item whose change triggered the event.
     */
    private transient V theDeltaValue;

    /**
     * The identifier for the delta value of the data item whose change triggered the event.
     */
    private VID theDeltaValueIdentifier;

    /**
     * The new value of the data item whose change triggered the event.
     */
    private transient V theNewValue;

    /**
     * The identifier for the new value of the data item whose change triggered the event.
     */
    private VID theNewValueIdentifier;

    /**
     * The time at which the event occurred, in System.currentTimeMillis format.
     */
    private long theTimeEventOccurred;
    
    /**
     * The property of the event.
     */
    private transient P theProperty;
    
    /**
     * The identifier for the property of the event.
     */
    private PID thePropertyIdentifier;
    
    /**
     * Object we use as a source for java.util.EventObject instead of the real one,
     * because java.util.EventObject is rather broken.
     */
    private static final Object DUMMY_SENDER = new Object();
}
