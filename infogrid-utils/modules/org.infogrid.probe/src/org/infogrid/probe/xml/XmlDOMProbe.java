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

package org.infogrid.probe.xml;

import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;

import org.infogrid.module.ModuleException;

import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;

/**
  * <p>This interface is supported by all Probes that can
  * interpret a DOM object model.</p>
  *
  * <p>Classes supporting this interface need to have a constructor
  * that takes a single parameter of type <tt>ModelBase</tt>. This parameter can
  * be used at construction time to look up necessary MeshTypes.</p>
  *
  * <p>The sequence of invocations is:</p>
  * <pre>
  *   constructor
  *   for( one or more times ) {
  *       readFromStream( ... )
  *   }
  * </pre>
  */
public interface XmlDOMProbe
        extends
            XmlProbe
{
    /**
     * Read from the DOM and instantiate corresponding MeshObjects.
     * 
     * 
     * 
     * @param networkId the NetMeshBaseIdentifier that is being accessed
     * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
     *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
     *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
     *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
     *         in the <code>org.infogrid.model.ProbeModel</code>) that reflects the policy.
     * @param theInputStream the InputStream to read from
     * @param theContentType the content type (MIME) if known
     * @param theFacade the interface through which the Probe instantiates MeshObjects
     * @throws DoNotHaveLockException a Probe can declare to throw this Exception,
     *         which makes programming easier, but if it actually threw it, that would be a programming error
     * @throws IdentiMeshObjectIdentifierNotUniqueExceptions this Exception, it indicates that the
     *         Probe developer incorrectly assigned duplicate Identifiers to created MeshObject
     * @throws RelationshipExistsAlreadyException if a Probe throws this Exception, it indicates that the
     *         Probe developer incorrectly attempted to create another RelationshipType instance between
     *         the same two Entities.
     * @throws TransactionException a Probe can declare to throw this Exception,
     *         which makes programming easier, but if it actually threw it, that would be a programming error
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     * @throws IOException an input/output error occurred during execution of the Probe
     */
    public void parseDocument(
            NetMeshBaseIdentifier  networkId,
            CoherenceSpecification coherence,
            Document               theDocument,
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
