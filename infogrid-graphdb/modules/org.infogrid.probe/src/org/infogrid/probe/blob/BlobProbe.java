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

package org.infogrid.probe.blob;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Blob.BlobSubjectArea;
import org.infogrid.model.primitives.BlobValue;
import org.infogrid.model.primitives.StringValue;

import org.infogrid.probe.NonXmlStreamProbe;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;

import org.infogrid.util.StreamUtils;

import java.io.InputStream;
import java.io.IOException;

/**
 * This is a Probe for arbitrary Blob (Binary Large Objects) objects.
 * It has a little bit of smarts to be a little bit more precise than just
 * Blobs, but not much (eg distinguish images).
 */
public class BlobProbe
    implements
        NonXmlStreamProbe
{
    /**
     * Constructor.
     */
    public BlobProbe()
    {
    }

    /**
     * Read from the InputStream and instantiate corresponding MeshObjects.
     * 
     * @param networkId the NetMeshBaseIdentifier for the location that we access
     * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
     *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
     *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
     *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
     *         in the <code>org.infogrid.model.Probe</code>) that reflects the policy.
     * @param input the InputStream to read from
     * @param contentType the content type (MIME) if known
     * @param facade the interface through which the Probe instantiates MeshObjects
     * @throws IdentMeshObjectIdentifierNotUniqueExceptione throws this Exception, it indicates that the
     *         Probe developer incorrectly assigned duplicate Identifiers to created MeshObject
     * @throws RelationshipExistsAlreadyException if a Probe throws this Exception, it indicates that the
     *         Probe developer incorrectly attempted to create another instance of the same RelationshipType
     *         between the same two Entities.
     * @throws TransactionException a Probe can declare to throw this Exception,
     *         which makes programming easier, but if it actually threw it, that would be a programming error
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     * @throws IOException an input/output error occurred during execution of the Probe
     */
    public void readFromStream(
            NetMeshBaseIdentifier  networkId,
            CoherenceSpecification coherence,
            InputStream            input,
            String                 contentType,
            StagingMeshBase        mb )
        throws
            MeshObjectIdentifierNotUniqueException,
            TransactionException,
            NotPermittedException,
            ProbeException,
            IOException
    {
        byte [] buf = StreamUtils.slurp( input );

        // create a new Blob object
        MeshObject theBlobObject = mb.getHomeObject();
        
        theBlobObject.bless( BlobSubjectArea.BLOBOBJECT );

        theBlobObject.setPropertyValue( BlobSubjectArea.BLOBOBJECT_CONTENT,  BlobValue.create( buf, contentType ));
        theBlobObject.setPropertyValue( BlobSubjectArea.BLOBOBJECT_CODEBASE, StringValue.create( networkId.toExternalForm() ));
    }
}
