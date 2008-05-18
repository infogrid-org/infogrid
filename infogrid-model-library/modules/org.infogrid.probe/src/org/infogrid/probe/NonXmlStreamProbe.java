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
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;

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
     * @param dataSourceIdentifier identifies the data source that is being accessed
     * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
     *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
     *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
     *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
     *         in the <code>org.infogrid.model.Probe</code> Subject Area) that reflects the policy.
     * @param theInputStream the InputStream to read from
     * @param theContentType the content type (MIME) if known
     * @param freshMeshBase the StagingMeshBase in which the corresponding MeshObjects are to be instantiated by the Probe.
     *         This MeshBase is empty when passed into this call, except for the home object.
     * @throws IsAbstractException thrown if an EntityType or a Relationship could not be instantiated because
     *         it was abstract. Throwing this typically indicates a programming error.
     * @throws EntityBlessedAlreadyException thrown if a MeshObject was incorrectly blessed twice with the same
     *         EntityType. Throwing this typically indicates a programming error.
     * @throws EntityNotBlessedException thrown if a MeshObject was not blessed with a required EntityType.
     *         Throwing this typically indicates a programming error.
     * @throws IllegalPropertyTypeException thrown if a MeshObject did not carry a PropertyType that it needed
     *         to carry. Throwing this typically indicates a programming error.
     * @throws IllegalPropertyValueException thrown if a PropertyValue was assigned to a property that was
     *         outside of the allowed range. Throwing this typically indicates a programming error.
     * @throws IOException an input/output error occurred during execution of the Probe
     * @throws MeshObjectIdentifierNotUniqueException thrown if the Probe developer incorrectly
     *         assigned duplicate MeshObjectsIdentifiers to created MeshObjects.
     *         Throwing this typically indicates a programming error.
     * @throws ModuleException thrown if a Module required by the Probe could not be loaded
     * @throws NotPermittedException thrown if an operation performed by the Probe was not permitted
     * @throws NotRelatedException thrown if a relationship was supposed to be blessed, but the relationship
     *         did not exist. Throwing this typically indicates a programming error.
     * @throws RelatedAlreadyException thrown if the Probe developer incorrectly attempted to
     *         relate two already-related MeshObjects. Throwing this typically indicates a programming error.
     * @throws RoleTypeBlessedAlreadyException thrown if a relationship was incorrectly blessed twice with the same
     *         RelationshipType, in the same direction. Throwing this typically indicates a programming error.
     * @throws TransactionException this Exception is declared to make programming easier,
     *         although actually throwing it would be a programming error. Throwing this typically indicates a programming error.
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     * @throws URISyntaxException thrown if a URI was constructed in an invalid way
     */
    public void readFromStream(
            NetMeshBaseIdentifier  dataSourceIdentifier,
            CoherenceSpecification coherenceSpecification,
            InputStream            theInputStream,
            String                 theContentType,
            StagingMeshBase        freshMeshBase )
        throws
            IsAbstractException,
            EntityBlessedAlreadyException,
            EntityNotBlessedException,
            RelatedAlreadyException,
            RoleTypeBlessedAlreadyException,
            NotRelatedException,
            MeshObjectIdentifierNotUniqueException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            TransactionException,
            NotPermittedException,
            ProbeException,
            IOException,
            ModuleException,
            URISyntaxException;
}
