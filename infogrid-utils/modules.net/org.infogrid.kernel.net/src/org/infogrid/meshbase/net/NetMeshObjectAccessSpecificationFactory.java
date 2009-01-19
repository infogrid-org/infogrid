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

package org.infogrid.meshbase.net;

import java.net.URI;
import java.net.URISyntaxException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 * Factory for NetMeshObjectAccessSpecifications.
 */
public interface NetMeshObjectAccessSpecificationFactory
{
    /**
     * Obtain the underlying NetMeshObjectIdentiferFactory.
     * 
     * @return the NetMeshObjectIdentifierFactory
     */
    public NetMeshObjectIdentifierFactory getNetMeshObjectIdentifierFactory();

    /**
     * Obtain the underlying NetMeshBaseIdentifierFactory.
     * 
     * @return the NetMeshBaseIdentifierFactory
     */
    public NetMeshBaseIdentifierFactory getNetMeshBaseIdentifierFactory();
    
    /**
     * Obtain the underlying NetMeshBaseAccessSpecificationFactory.
     * 
     * @return the NetMeshBaseAccessSpecificationFactory
     */
    public NetMeshBaseAccessSpecificationFactory getNetMeshBaseAccessSpecificationFactory();

    /**
     * Convert a String into a NetMeshObjectAccessSpecification.
     * 
     * @param raw the String
     * @return the created NetMeshObjectAccessSpecification
     * @throws URISyntaxException thrown if the String could not be parsed
     */
    public NetMeshObjectAccessSpecification fromExternalForm(
            String raw )
        throws
            URISyntaxException;
    
    /**
     * Factory method to obtain a NetMeshObjectAccessSpecification to a locally available MeshObject. This
     * is a degenerate form of NetMeshObjectAccessSpecification, but useful for API consistency.
     * 
     * @param extName Identifier of the local NetMeshObject
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtainToLocalObject(
            NetMeshObjectIdentifier extName );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting the home object at the NetMeshBaseIdentifier.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier oneElementName );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting a non-default NetMeshObject.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param extName Identifier of the remote non-default NetMeshObject
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier    oneElementName,
            NetMeshObjectIdentifier  extName );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting the home object at the NetMeshBaseIdentifier,
     * specifying a non-default ScopeSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param scope the ScopeSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier oneElementName,
            ScopeSpecification    scope );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting the home object at the NetMeshBaseIdentifier.
     * specifying a non-default CoherenceSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier  oneElementName,
            CoherenceSpecification coherence );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting a non-default NetMeshObject,
     * specifying a non-default ScopeSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param extName Identifier of the remote non-default NetMeshObject
     * @param scope the ScopeSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier   oneElementName,
            NetMeshObjectIdentifier extName,
            ScopeSpecification      scope );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting a non-default MeshObject,
     * specifying a non-default CoherenceSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param extName Identifier of the remote non-default MeshObject
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier   oneElementName,
            NetMeshObjectIdentifier extName,
            CoherenceSpecification  coherence );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting the home object at the NetMeshBaseIdentifier,
     * specifying a non-default ScopeSpecification and a non-default CoherenceSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param scope the ScopeSpecification
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier  oneElementName,
            ScopeSpecification     scope,
            CoherenceSpecification coherence );

    /**
     * Factory method to obtain a single-element NetMeshObjectAccessSpecification from a NetMeshBaseIdentifier,
     * requesting a non-default MeshObject,
     * specifying a non-default ScopeSpecification and a non-default CoherenceSpecification.
     * 
     * @param oneElementName the NetMeshBaseIdentifier
     * @param extName Identifier of the remote non-default MeshObject
     * @param scope the ScopeSpecification
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier   oneElementName,
            NetMeshObjectIdentifier extName,
            ScopeSpecification      scope,
            CoherenceSpecification  coherence );

    /**
     * Factory method to obtain a multi-element NetMeshObjectAccessSpecification from
     * a sequence of NetMeshBaseAccessSpecification.
     * 
     * @param elements the NetMeshBaseAccessSpecifications, in sequence
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseAccessSpecification [] elements );

    /**
     * Factory method to obtain a multi-element NetMeshObjectAccessSpecification from
     * a sequence of NetMeshBaseAccessSpecifications,
     * requesting a non-default MeshObject.
     * 
     * @param elements the NetMeshBaseAccessSpecifications, in sequence
     * @param extName Identifier of the remote non-default MeshObject
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseAccessSpecification [] elements,
            NetMeshObjectIdentifier           extName );

    /**
     * Factory method to obtain a multi-element NetMeshObjectAccessSpecification
     * from a series of NetMeshBaseIdentifiers.
     * 
     * @param elements the NetMeshBaseIdentifier, in sequence
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier [] elements );

    /**
     * Factory method to obtain a multi-element NetMeshObjectAccessSpecification from
     * a series of NetMeshBaseIdentifiers,
     * requesting a non-default MeshObject.
     * 
     * @param elements the NetMeshBaseIdentifiers, in sequence
     * @param extName Identifier of the remote non-default MeshObject
     * @return created NetMeshObjectAccessSpecification
     */
    public NetMeshObjectAccessSpecification obtain(
            NetMeshBaseIdentifier [] elements,
            NetMeshObjectIdentifier  extName );
    
    /**
     * Convenience factory method to obtain a single-element NetMeshObjectAccessSpecification
     * by converting a URI into a NetMeshBaseIdentifier, requesting its home MeshObject.
     * 
     * @param remoteLocation the URI
     * @return created NetMeshObjectAccessSpecification
     * @throws URISyntaxException thrown if the syntax could not be parsed
     */
    public NetMeshObjectAccessSpecification obtain(
            URI remoteLocation )
        throws
            URISyntaxException;

    /**
     * Convenience factory method to obtain a single-element NetMeshObjectAccessSpecification
     * by converting a URI into a NetMeshBaseIdentifier, requesting its home MeshObject,
     * specifying a non-default CoherenceSpecification.
     * 
     * @param remoteLocation the URI
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     * @throws URISyntaxException thrown if the syntax could not be parsed
     */
    public NetMeshObjectAccessSpecification obtain(
            URI                    remoteLocation,
            CoherenceSpecification coherence )
        throws
            URISyntaxException;

    /**
     * Convenience factory method to obtain a single-element NetMeshObjectAccessSpecification
     * by converting a URI into a NetMeshBaseIdentifier, requesting its home MeshObject,
     * specifying a non-default ScopeSpecification.
     * 
     * @param remoteLocation the URI
     * @param scope the ScopeSpecification
     * @return created NetMeshObjectAccessSpecification
     * @throws URISyntaxException thrown if the syntax could not be parsed
     */
    public NetMeshObjectAccessSpecification obtain(
            URI                remoteLocation,
            ScopeSpecification scope )
        throws
            URISyntaxException;

    /**
     * Convenience factory method to obtain a single-element NetMeshObjectAccessSpecification
     * by converting a URI into a NetMeshBaseIdentifier, requesting its home MeshObject,
     * specifying a non-default ScopeSpecification and a non-default CoherenceSpecification.
     * 
     * @param remoteLocation the URI
     * @param scope the ScopeSpecification
     * @param coherence the CoherenceSpecification
     * @return created NetMeshObjectAccessSpecification
     * @throws URISyntaxException thrown if the syntax could not be parsed
     */
    public NetMeshObjectAccessSpecification obtain(
            URI                    remoteLocation,
            ScopeSpecification     scope,
            CoherenceSpecification coherence )
        throws
            URISyntaxException;
}