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

package org.infogrid.probe;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.ChangeSet;

import org.infogrid.module.ModuleException;

import java.io.IOException;

/**
 * <p>This interface is supported by all Probes that can both
 *    read and write their data. This interface is supported in addition
 *    to whatever other subtype of Probe they support.</p>
 *
 * <p>The sequence of invocations is:</p>
 * <pre>
 *   constructor
 *   for( one or more times ) {
 *       read( ... )
 *       wait for some period of time
 *       write( ... )
 *   }
 * </pre>
 */
public interface WriteableProbe
        extends
            Probe
{
    /**
     * <p>Write the passed-in changes to the data source. The ChangeSet may be null or empty,
     * indicating that no changes need to be written, but the call is made anyway in order to
     * provide consistent invocation behavior.</p>
     * <p>Note: the MeshBase provided to this call is <i>different</i> from the MeshBase that
     * is being passed into the readXXX call on the Probe instance invoked right after: for
     * each Probe run, a fresh, empty StagingMeshBase is created (which by definition is empty
     * except for the home object), while the StagingMeshBase provided to this call contains
     * the content of the last Probe run, plus any modifications since.</p>
     *
     * @param networkId the location of the data source to which we write
     * @param updateSet the set of changes to write, or null
     * @param previousMeshBaseWithUpdates the StagingMeshBase in which the Changes have occurred.
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     * @throws IOException an input/output error occurred during execution of the Probe
     * @throws ModuleException thrown if a Module required by the Probe could not be written
     */
    public void write(
            NetMeshBaseIdentifier networkId,
            ChangeSet             updateSet,
            StagingMeshBase       previousMeshBaseWithUpdates )
        throws
            ProbeException,
            IOException,
            ModuleException;
}
