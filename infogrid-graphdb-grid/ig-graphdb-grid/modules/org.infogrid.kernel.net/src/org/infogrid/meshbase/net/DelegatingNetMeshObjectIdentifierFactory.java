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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net;

import java.text.ParseException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.util.text.StringRepresentation;

/**
 * Delegates all requests to a delegate NetMeshObjectIdentifierFactory. Overriding
 * methods makes it easy to customize NetMeshObjectIdentifierFactory implementations.
 */
public abstract class DelegatingNetMeshObjectIdentifierFactory
        implements
            NetMeshObjectIdentifierFactory
{
    /**
     * Constructor.
     *
     * @param delegate the delegate NetMeshObjectIdentifierFactory
     */
    protected DelegatingNetMeshObjectIdentifierFactory(
            NetMeshObjectIdentifierFactory delegate )
    {
        theDelegate = delegate;
    }

    /**
     * Obtain the delegate NetMeshObjectIdentifierFactory.
     *
     * @return the delegate NetMeshObjectIdentifierFactory
     */
    public NetMeshObjectIdentifierFactory getDelegate()
    {
        return theDelegate;
    }

    /**
     * Determine the MeshBase to which this MeshObjectIdentifierFactory belongs.
     *
     * @return the MeshBase
     */
    public MeshBase getMeshBase()
    {
        return theDelegate.getMeshBase();
    }

    /**
     * Set the MeshBase to which this MeshObjectIdentifierFactory belongs.
     * This is invoked by the MeshBase's constructor and does not need to invoked
     * by the application programmer. It can only be invoked once; subsequent
     * invocations throw an IllegalStateException.
     *
     * @param mb the MeshBase
     * @throws IllegalStateException thrown if this call is performed more than one on the same instance
     */
    public void setMeshBase(
            MeshBase mb )
        throws
            IllegalStateException
    {
        theDelegate.setMeshBase( mb );
    }

    /**
     * Determine the Identifier of the Home Object.
     *
     * @return the Identifier
     */
    public NetMeshObjectIdentifier getHomeMeshObjectIdentifier()
    {
        return theDelegate.getHomeMeshObjectIdentifier();
    }

    /**
     * Determine the Identifier of the Home Object in a NetMeshBase with the given
     * NetMeshBaseIdentifier.
     *
     * @param mbIdentifier the NetMeshBaseIdentifier of the NetMeshBase
     * @return the Identifier
     */
    public NetMeshObjectIdentifier getHomeMeshObjectIdentifierFor(
            NetMeshBaseIdentifier mbIdentifier )
    {
        return theDelegate.getHomeMeshObjectIdentifierFor( mbIdentifier );
    }

    /**
     * Create a unique Identifier for a MeshObject that can be used to create a MeshObject
     * with the associated MeshBaseLifecycleManager.
     *
     * @return the created Identifier
     */
    public NetMeshObjectIdentifier createMeshObjectIdentifier()
    {
        return theDelegate.createMeshObjectIdentifier();
    }

    /**
     * Create an identifier for a MeshObject held locally at this MeshBase.
     *
     * @param raw the identifier String
     * @return the created NetMeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    public NetMeshObjectIdentifier fromExternalForm(
            String raw )
        throws
            ParseException
    {
        return theDelegate.fromExternalForm( raw );
    }

    /**
     * Create an identifier for a MeshObject held at a different MeshBase.
     *
     * @param meshBaseIdentifier MeshBaseIdentifier of the MeshBase where the object is held
     * @param raw the identifier String
     * @return the created NetMeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    public NetMeshObjectIdentifier fromExternalForm(
            NetMeshBaseIdentifier meshBaseIdentifier,
            String                raw )
        throws
            ParseException
    {
        return theDelegate.fromExternalForm( meshBaseIdentifier, raw );
    }

    /**
     * Recreate a NetMeshObjectIdentifier from an external form. Be lenient about syntax and
     * attempt to interpret what the user meant when entering an invalid or incomplete
     * raw String.
     *
     * @param raw the external form
     * @return the created MeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    public NetMeshObjectIdentifier guessFromExternalForm(
            String raw )
        throws
            ParseException
    {
        return theDelegate.guessFromExternalForm( raw );
    }

    /**
     * Recreate a NetMeshObjectIdentifier from an external form. Be lenient about syntax and
     * attempt to interpret what the user meant when entering an invalid or incomplete
     * raw String.
     *
     * @param contextIdentifier identifier of the NetMeshBase relative to which the external form is to be evaluated
     * @param raw the external form
     * @return the created MeshObjectIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public NetMeshObjectIdentifier guessFromExternalForm(
            NetMeshBaseIdentifier contextIdentifier,
            String                raw )
        throws
            ParseException
    {
        return theDelegate.guessFromExternalForm( contextIdentifier, raw );
    }

    /**
     * Convert this StringRepresentation back to a MeshObjectIdentifier.
     *
     * @param representation the StringRepresentation in which this String is represented
     * @param s the String to parse
     * @return the created MeshObjectIdentifier
     * @throws ParseException thrown if the String could not be successfully parsed
     */
    public NetMeshObjectIdentifier fromStringRepresentation(
            StringRepresentation representation,
            String               s )
        throws
            ParseException
    {
        return theDelegate.fromStringRepresentation( representation, s );
    }

    /**
     * The underlying delegate.
     */
    protected NetMeshObjectIdentifierFactory theDelegate;
}
