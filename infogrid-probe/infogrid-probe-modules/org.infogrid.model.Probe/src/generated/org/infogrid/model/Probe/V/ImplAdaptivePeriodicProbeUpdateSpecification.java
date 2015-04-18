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
// Copyright 1998-2013 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

//
// This file has been generated AUTOMATICALLY. DO NOT MODIFY.
// on Sat, 2015-04-18 05:30:58 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Probe.V;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.model.Probe.*;

/**
  * <p>Java implementation class for EntityType org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Probe/AdaptivePeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>AdaptivePeriodicProbeUpdateSpecification</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Adaptive Period Probe Update Specification</td></tr></table></td></tr>
  * </table>
  */

public class ImplAdaptivePeriodicProbeUpdateSpecification
        extends
            org.infogrid.mesh.TypedMeshObjectFacadeImpl
        implements
            org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification

{
    private static final org.infogrid.util.logging.Log log = org.infogrid.util.logging.Log.getLogInstance( ImplAdaptivePeriodicProbeUpdateSpecification.class ); // our own, private logger

    /**
      * Constructor.
      *
      * @param delegate the underlying MeshObject
      */
    public ImplAdaptivePeriodicProbeUpdateSpecification(
            MeshObject delegate )
    {
        super( delegate );
    }

    /**
      * Initializes the MeshObject to its default values.
      *
      * @param init the TypeInitializer
      * @param skipSettingValuesFor collection of PropertyTypes that already have values and should not be initialized
      * @param timeUpdated the timeUpdated to use
      * @throws org.infogrid.mesh.IllegalPropertyTypeException should not be thrown -- codegenerator faulty
      * @throws org.infogrid.mesh.IllegalPropertyValueException should not be thrown -- codegenerator faulty
      * @throws org.infogrid.mesh.NotPermittedException should not be thrown -- codegenerator faulty
      * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
      * @throws org.infogrid.model.primitives.UnknownEnumeratedValueException should not be thrown -- codegenerator faulty
      */
    public static void initializeDefaultValues(
            TypeInitializer                    init,
            java.util.Collection<PropertyType> skipSettingValuesFor,
            long                               timeUpdated )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.model.primitives.UnknownEnumeratedValueException
    {
        init.setPropertyValues(
                new PropertyType[] {
                    CURRENTDELAY,
                    MAXDELAY,
                    FALLBACKDELAY,
                    ADAPTIVEFACTOR,
                    PROBERUNCOUNTER,
                    WAITFORONGOINGRESYNCHRONIZATION,
                },
                new PropertyValue[] {
                    org.infogrid.model.primitives.IntegerValue.create( 3600000L ),
                    org.infogrid.model.primitives.IntegerValue.create( 604800000L ),
                    org.infogrid.model.primitives.IntegerValue.create( 600000L ),
                    org.infogrid.model.primitives.FloatValue.create( 1.1 ),
                    org.infogrid.model.primitives.IntegerValue.create( 0L ),
                    org.infogrid.model.primitives.BooleanValue.TRUE,
                },
                skipSettingValuesFor,
                timeUpdated );
    }

    /**
      * Set value for property CurrentDelay.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      * @throws IllegalPropertyValueException thrown if the PropertyValue was invalid for this Property
      */
    public void setCurrentDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setCurrentDelay", newValue );
        }

        try {
            the_Delegate.setPropertyValue( CURRENTDELAY, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property CurrentDelay.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.IntegerValue getCurrentDelay()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.IntegerValue) the_Delegate.getPropertyValue( CURRENTDELAY );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property MaxDelay.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      */
    public void setMaxDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setMaxDelay", newValue );
        }

        try {
            the_Delegate.setPropertyValue( MAXDELAY, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property MaxDelay.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.IntegerValue getMaxDelay()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.IntegerValue) the_Delegate.getPropertyValue( MAXDELAY );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property FallbackDelay.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      * @throws IllegalPropertyValueException thrown if the PropertyValue was invalid for this Property
      */
    public void setFallbackDelay(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setFallbackDelay", newValue );
        }

        try {
            the_Delegate.setPropertyValue( FALLBACKDELAY, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property FallbackDelay.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.IntegerValue getFallbackDelay()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.IntegerValue) the_Delegate.getPropertyValue( FALLBACKDELAY );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property AdaptiveFactor.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      * @throws IllegalPropertyValueException thrown if the PropertyValue was invalid for this Property
      */
    public void setAdaptiveFactor(
            org.infogrid.model.primitives.FloatValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setAdaptiveFactor", newValue );
        }

        try {
            the_Delegate.setPropertyValue( ADAPTIVEFACTOR, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property AdaptiveFactor.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.FloatValue getAdaptiveFactor()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.FloatValue) the_Delegate.getPropertyValue( ADAPTIVEFACTOR );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property NextProbeRun.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      */
    public void setNextProbeRun(
            org.infogrid.model.primitives.TimeStampValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setNextProbeRun", newValue );
        }

        try {
            the_Delegate.setPropertyValue( NEXTPROBERUN, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property NextProbeRun.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.TimeStampValue getNextProbeRun()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.TimeStampValue) the_Delegate.getPropertyValue( NEXTPROBERUN );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property LastProbeRun.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      */
    public void setLastProbeRun(
            org.infogrid.model.primitives.TimeStampValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setLastProbeRun", newValue );
        }

        try {
            the_Delegate.setPropertyValue( LASTPROBERUN, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property LastProbeRun.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.TimeStampValue getLastProbeRun()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.TimeStampValue) the_Delegate.getPropertyValue( LASTPROBERUN );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property ProbeRunCounter.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      * @throws IllegalPropertyValueException thrown if the PropertyValue was invalid for this Property
      */
    public void setProbeRunCounter(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setProbeRunCounter", newValue );
        }

        try {
            the_Delegate.setPropertyValue( PROBERUNCOUNTER, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property ProbeRunCounter.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.IntegerValue getProbeRunCounter()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property LastRunUsedWritableProbe.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      */
    public void setLastRunUsedWritableProbe(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setLastRunUsedWritableProbe", newValue );
        }

        try {
            the_Delegate.setPropertyValue( LASTRUNUSEDWRITABLEPROBE, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property LastRunUsedWritableProbe.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.BooleanValue getLastRunUsedWritableProbe()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.BooleanValue) the_Delegate.getPropertyValue( LASTRUNUSEDWRITABLEPROBE );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property LastRunUsedProbeClass.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      */
    public void setLastRunUsedProbeClass(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setLastRunUsedProbeClass", newValue );
        }

        try {
            the_Delegate.setPropertyValue( LASTRUNUSEDPROBECLASS, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property LastRunUsedProbeClass.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.StringValue getLastRunUsedProbeClass()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.StringValue) the_Delegate.getPropertyValue( LASTRUNUSEDPROBECLASS );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
      * Set value for property WaitForOngoingResynchronization.
      *
      * @param newValue the new value for the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      * @throws org.infogrid.meshbase.transaction.TransactionException thrown if invoked outside proper transaction boundaries
      * @throws IllegalPropertyValueException thrown if the PropertyValue was invalid for this Property
      */
    public void setWaitForOngoingResynchronization(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "setWaitForOngoingResynchronization", newValue );
        }

        try {
            the_Delegate.setPropertyValue( WAITFORONGOINGRESYNCHRONIZATION, newValue );

        } catch( IllegalPropertyValueException ex ) {
            log.error( ex );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
        }
    }

    /**
      * Obtain value for property WaitForOngoingResynchronization.
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller did not have permission to perform this operation
      */
    public org.infogrid.model.primitives.BooleanValue getWaitForOngoingResynchronization()
        throws
            org.infogrid.mesh.NotPermittedException
    {
        try {
            return (org.infogrid.model.primitives.BooleanValue) the_Delegate.getPropertyValue( WAITFORONGOINGRESYNCHRONIZATION );
        } catch( IllegalPropertyTypeException ex ) {
            log.error( ex );
            return null;
        }
    }

 // #### BEGIN IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * A successful run was just performed, but the data source did not change and no update was necessary.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public void performedSuccessfulRunNoChange(
            long consumedTime )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();
        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));
        
        double theAdaptiveFactor = ((FloatValue)   the_Delegate.getPropertyValue( ADAPTIVEFACTOR )).value();
        long   theCurrentDelay   = ((IntegerValue) the_Delegate.getPropertyValue( CURRENTDELAY )).value();

        long newDelay = (long) ( theCurrentDelay * theAdaptiveFactor );

        IntegerValue maxDelay = (IntegerValue) the_Delegate.getPropertyValue( MAXDELAY );
        if( maxDelay != null ) {
            newDelay = Math.min( newDelay, maxDelay.value() );
        }
        
        the_Delegate.setPropertyValue( CURRENTDELAY, IntegerValue.create( newDelay ));
        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );
        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( newDelay ));

        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "performedSuccessfulRunNoChange" );
        }
    }
         
 // #### END IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * A successful run was just performed, the data source did change, and this is how long the run took.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public void performedSuccessfulRunWithChange(
            long consumedTime )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        long counter = ((IntegerValue) the_Delegate.getPropertyValue( PROBERUNCOUNTER )).value();
        the_Delegate.setPropertyValue( PROBERUNCOUNTER, IntegerValue.create( ++counter ));
        
        PropertyValue theFallbackDelay = the_Delegate.getPropertyValue( FALLBACKDELAY );
        the_Delegate.setPropertyValue( CURRENTDELAY, theFallbackDelay );
        the_Delegate.setPropertyValue( LASTPROBERUN, TimeStampValue.now() );
        the_Delegate.setPropertyValue( NEXTPROBERUN, TimeStampValue.nowWithOffset( ((IntegerValue)theFallbackDelay).value() ));

        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "performedSuccessfulRunWithChange" );
        }
    }
         
 // #### END IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * An attempt to run the Probe was unsuccessful,  and no new data could be obtained because a problem occurred.
     *
     * @param consumedTime the time in milliseconds that the update took
     * @param problem the Throwable that was thrown by the Probe, indicating the problem
     * @throws org.infogrid.mesh.IllegalPropertyTypeException thrown if the body of this method accessed an invalid PropertyType
     * @throws org.infogrid.mesh.IllegalPropertyValueException, thrown if the body of this method provided an invalid PropertyValue
     * @throws org.infogrid.mesh.NotPermittedException thrown if the caller was not permitted to invoke this operation
     * @throws org.infogrid.meshbase.transaction.TransactionException should not be thrown -- codegenerator faulty
     */
    public void performedUnsuccessfulRun(
            long      consumedTime,
            Throwable problem )
        throws
            org.infogrid.mesh.IllegalPropertyTypeException,
            org.infogrid.mesh.IllegalPropertyValueException,
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException
    {
        performedSuccessfulRunWithChange( consumedTime ); // simpler
    }
         
 // #### END IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####
 // #### BEGIN IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####

    /**
     * Set the internal state of the Probe to stop automatic updates.
     */
    public void stopUpdating()
    {
        try {
            the_Delegate.setPropertyValue( NEXTPROBERUN, null );
            the_Delegate.setPropertyValue( CURRENTDELAY, IntegerValue.create( 3600000 ) ); // back to default
        } catch( Exception ex ) {
            log.error( ex );
        }
    }
         
 // #### END IMPLEMENTED METHOD (INSERTED FROM model.xml FILE) ####
}
