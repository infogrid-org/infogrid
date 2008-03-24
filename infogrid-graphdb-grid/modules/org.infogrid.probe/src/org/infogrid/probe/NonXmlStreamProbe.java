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

import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.module.ModuleException;

import java.io.InputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * <p>This interface is supported by all Probes that can
 * read some kind of non-XML file format from a stream.</p>
 *
 * <p>Classes supporting this interface need to have a constructor
 *    that does not take any parameters.</p>
 *
 * <p>The sequence of invocations is:</p>
 * <pre>
 *   constructor
 *   for( one or more times ) {
 *       readFromStream( ... )
 *       wait for some period of time
 *   }
 * </pre>
 * <p>If a class also supports {@link org.infogrid.probe.WritableProbe WritableProbe},
 *    the sequence of invocation is:</p>
 * <pre>
 *   constructor
 *   for( one or more times ) {
 *       readFromStream( ... )
 *       wait for some period of time
 *       writeFromApi( ...)
 *   }
 * </pre>
 */
public interface NonXmlStreamProbe
        extends
            Probe
{
    /**
     * Read from the InputStream and instantiate corresponding MeshObjects.
     * 
     * @param networkId the NetMeshBaseIdentifier that is being accessed
     * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
     *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
     *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
     *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
     *         in the <code>org.infogrid.model.Probe</code>) that reflects the policy.
     * @param theInputStream the InputStream to read from
     * @param theContentType the content type (MIME) if known
     * @param mb the StagingMeshBase in which the corresponding MeshObjects are instantiated by the Probe
     * @throws IdentMeshObjectIdentifierNotUniqueExceptionhe Probe developer incorrectly
     *         assigned duplicate Identifiers to created MeshObjects
     * @throws RelatedAlreadyException thrown if the Probe developer incorrectly attempted to
     *         relate two already-related MeshObjects
     * @throws TransactionException this Exception is declared to make programming easier,
     *         although actually throwing it would be a programming error
     * @throws NotPermittedException thrown if an operation performed by the Probe was not permitted
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     * @throws IOException an input/output error occurred during execution of the Probe
     * @throws ModuleException thrown if a Module required by the Probe could not be loaded
     */
    public void readFromStream(
            NetMeshBaseIdentifier      networkId,
            CoherenceSpecification coherence,
            InputStream            theInputStream,
            String                 theContentType,
            StagingMeshBase        mb )
        throws
            MeshObjectIdentifierNotUniqueException,
            RelatedAlreadyException,
            TransactionException,
            NotPermittedException,
            ProbeException,
            IOException,
            ModuleException,
            URISyntaxException;
}
