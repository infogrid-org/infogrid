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

package org.infogrid.model.Probe;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class ProbeSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Probe" );

    /**
      * A Home Object of a Probe is blessed with a subtype of this ProbeUpdateSpecification.\nIt expresses meta-data such as when the Probe was last run, when it should be run next etc.\nSubtypes may implement different algorithms to determine the respective data.
      */
    public static final EntityType PROBEUPDATESPECIFICATION = org.infogrid.model.Probe.ProbeUpdateSpecification._TYPE;

    /**
      * The time at which to run this Probe next. If this is not given, this indicates\n\"never\". If this is in the past, it means \"as soon as possible\". This value\nis given in Java\'s System.currentTimeMillis() format.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_NEXTPROBERUN = org.infogrid.model.Probe.ProbeUpdateSpecification.NEXTPROBERUN;
    public static final org.infogrid.model.primitives.TimeStampDataType PROBEUPDATESPECIFICATION_NEXTPROBERUN_type = (org.infogrid.model.primitives.TimeStampDataType) PROBEUPDATESPECIFICATION_NEXTPROBERUN.getDataType();

    /**
      * The time that the last Probe run was attempted (successful or unsuccessful).\n                  If this is not given, it means that the Probe was never run. This value\n                  is given in Java\'s System.currentTimeMillis() format.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_LASTPROBERUN = org.infogrid.model.Probe.ProbeUpdateSpecification.LASTPROBERUN;
    public static final org.infogrid.model.primitives.TimeStampDataType PROBEUPDATESPECIFICATION_LASTPROBERUN_type = (org.infogrid.model.primitives.TimeStampDataType) PROBEUPDATESPECIFICATION_LASTPROBERUN.getDataType();

    /**
      * This value is incremented every time the Probe is run.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_PROBERUNCOUNTER = org.infogrid.model.Probe.ProbeUpdateSpecification.PROBERUNCOUNTER;
    public static final org.infogrid.model.primitives.IntegerDataType PROBEUPDATESPECIFICATION_PROBERUNCOUNTER_type = (org.infogrid.model.primitives.IntegerDataType) PROBEUPDATESPECIFICATION_PROBERUNCOUNTER.getDataType();

    /**
      * If this is true, the last Probe run used a WritableProbe.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_LASTRUNUSEDWRITABLEPROBE = org.infogrid.model.Probe.ProbeUpdateSpecification.LASTRUNUSEDWRITABLEPROBE;
    public static final org.infogrid.model.primitives.BooleanDataType PROBEUPDATESPECIFICATION_LASTRUNUSEDWRITABLEPROBE_type = (org.infogrid.model.primitives.BooleanDataType) PROBEUPDATESPECIFICATION_LASTRUNUSEDWRITABLEPROBE.getDataType();

    /**
      * Name of the Probe Class that was used at the last run.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_LASTRUNUSEDPROBECLASS = org.infogrid.model.Probe.ProbeUpdateSpecification.LASTRUNUSEDPROBECLASS;
    public static final org.infogrid.model.primitives.StringDataType PROBEUPDATESPECIFICATION_LASTRUNUSEDPROBECLASS_type = (org.infogrid.model.primitives.StringDataType) PROBEUPDATESPECIFICATION_LASTRUNUSEDPROBECLASS.getDataType();

    /**
      * If this is true, delay calling threads until triggered resynchronization requests have completed.
      */
    public static final PropertyType PROBEUPDATESPECIFICATION_WAITFORONGOINGRESYNCHRONIZATION = org.infogrid.model.Probe.ProbeUpdateSpecification.WAITFORONGOINGRESYNCHRONIZATION;
    public static final org.infogrid.model.primitives.BooleanDataType PROBEUPDATESPECIFICATION_WAITFORONGOINGRESYNCHRONIZATION_type = (org.infogrid.model.primitives.BooleanDataType) PROBEUPDATESPECIFICATION_WAITFORONGOINGRESYNCHRONIZATION.getDataType();

    public static final EntityType ONETIMEONLYPROBEUPDATESPECIFICATION = org.infogrid.model.Probe.OneTimeOnlyProbeUpdateSpecification._TYPE;

    public static final EntityType PERIODICPROBEUPDATESPECIFICATION = org.infogrid.model.Probe.PeriodicProbeUpdateSpecification._TYPE;

    /**
      * Delay between the end of a Probe run and the start of the next Probe run.
      */
    public static final PropertyType PERIODICPROBEUPDATESPECIFICATION_DELAY = org.infogrid.model.Probe.PeriodicProbeUpdateSpecification.DELAY;
    public static final org.infogrid.model.primitives.IntegerDataType PERIODICPROBEUPDATESPECIFICATION_DELAY_type = (org.infogrid.model.primitives.IntegerDataType) PERIODICPROBEUPDATESPECIFICATION_DELAY.getDataType();

    public static final EntityType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION = org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification._TYPE;

    /**
      * The current delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.
      */
    public static final PropertyType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_CURRENTDELAY = org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification.CURRENTDELAY;
    public static final org.infogrid.model.primitives.IntegerDataType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_CURRENTDELAY_type = (org.infogrid.model.primitives.IntegerDataType) ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_CURRENTDELAY.getDataType();

    /**
      * The maximum delay between the end of a Probe run and the start of the next Probe run.\nFIXME: this should use TimePeriodValue not IntegerValue.
      */
    public static final PropertyType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_MAXDELAY = org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification.MAXDELAY;
    public static final org.infogrid.model.primitives.IntegerDataType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_MAXDELAY_type = (org.infogrid.model.primitives.IntegerDataType) ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_MAXDELAY.getDataType();

    /**
      * The fallback delay between the end of a Probe run and the start of the next Probe run.\nCurrentDelay is re-initialized to this value when a Probe run was unsuccessful, or data changed.\nFIXME: this should use TimePeriodValue not IntegerValue.
      */
    public static final PropertyType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_FALLBACKDELAY = org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification.FALLBACKDELAY;
    public static final org.infogrid.model.primitives.IntegerDataType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_FALLBACKDELAY_type = (org.infogrid.model.primitives.IntegerDataType) ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_FALLBACKDELAY.getDataType();

    /**
      * The factor by which the delay increases for each Probe run that was successful and did not produce changed data.
      */
    public static final PropertyType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_ADAPTIVEFACTOR = org.infogrid.model.Probe.AdaptivePeriodicProbeUpdateSpecification.ADAPTIVEFACTOR;
    public static final org.infogrid.model.primitives.FloatDataType ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_ADAPTIVEFACTOR_type = (org.infogrid.model.primitives.FloatDataType) ADAPTIVEPERIODICPROBEUPDATESPECIFICATION_ADAPTIVEFACTOR.getDataType();

}
