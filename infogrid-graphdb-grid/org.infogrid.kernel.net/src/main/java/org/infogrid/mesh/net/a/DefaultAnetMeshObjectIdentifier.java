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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh.net.a;

import org.infogrid.mesh.a.DefaultAMeshObjectIdentifier;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.util.http.HTTP;

/**
 * Implements NetMeshObjectIdentifier for the Anet implementation.
 */
public class DefaultAnetMeshObjectIdentifier
        extends
            DefaultAMeshObjectIdentifier
        implements
            NetMeshObjectIdentifier
{
    /**
     * Factory method.
     *
     * @param factory the DefaultAnetMeshObjectIdentifierFactory that created this identifier
     * @param baseIdentifier identifier of the NetMeshBase relative to which a localId is specified
     * @param localId the localId of the to-be-created DefaultAnetMeshObjectIdentifier
     * @param asEnteredByUser String form of this Identifier as entered by the user, if any. This helps with error messages.
     * @return the created DefaultAnetMeshObjectIdentifier
     * @throws IllegalArgumentException thrown if a non-null localId contains a period.
     */
    static DefaultAnetMeshObjectIdentifier create(
            DefaultAnetMeshObjectIdentifierFactory factory,
            NetMeshBaseIdentifier                  baseIdentifier,
            String                                 localId,
            String                                 asEnteredByUser )
    {
        // all correctness checking is being moved into the factory.
        
        return new DefaultAnetMeshObjectIdentifier( factory, baseIdentifier, localId, asEnteredByUser );
    }

    /**
     * Private constructor.
     * 
     * @param factory the DefaultAnetMeshObjectIdentifierFactory that created this identifier
     * @param baseIdentifier identifier of the NetMeshBase relative to which a localId is specified
     * @param localId the localId of the to-be-created MeshObjectIdentifier
     * @param asEnteredByUser String form of this Identifier as entered by the user, if any. This helps with error messages.
     */
    protected DefaultAnetMeshObjectIdentifier(
            DefaultAnetMeshObjectIdentifierFactory factory,
            NetMeshBaseIdentifier                  baseIdentifier,
            String                                 localId,
            String                                 asEnteredByUser )
    {
        super( factory, localId, asEnteredByUser );

        theNetMeshBaseIdentifier = baseIdentifier;
    }

    /**
     * Obtain the factory that created this identifier.
     *
     * @return the factory
     */
    @Override
    public DefaultAnetMeshObjectIdentifierFactory getFactory()
    {
        return (DefaultAnetMeshObjectIdentifierFactory) super.getFactory();
    }

    /**
     * Obtain the external form of the MeshObjectIdentifier relative to some path.
     *
     * @param relativePath the relative path
     * @param assembleAsPartOfLongerId if true, escape properly so that the produced String can become part of a longer identifier
     * @return the local external form
     */
    @Override
    public String toLocalExternalForm(
            String  relativePath,
            boolean assembleAsPartOfLongerId )
    {
        if( relativePath == null ) {
            return theLocalId;
        }
        String baseId = theNetMeshBaseIdentifier.toExternalForm();
        if( relativePath.equals( baseId )) {
            return theLocalId;
        }
        // Escape URL arguments
        String ret = toExternalForm();
        if( assembleAsPartOfLongerId ) {
            ret = HTTP.encodeToValidUrlArgument( ret );
        }
        return ret;
    }

    /**
     * Obtain the identifier of the NetMeshBase in which this NetMeshObjectIdentifier was allocated.
     *
     * @return the dentifier of the NetMeshBase
     */
    public NetMeshBaseIdentifier getNetMeshBaseIdentifier()
    {
        return theNetMeshBaseIdentifier;
    }

    /**
     * Obtain an external form for this NetMeshObjectIdentifier, similar to
     * URL's getExternalForm(). This returns an empty String for local home objects.
     *
     * @return external form of this NetMeshObjectIdentifier
     */
    @Override
    public String toExternalForm()
    {
        if( theLocalId != null && theLocalId.length() > 0 ) {
            StringBuilder buf = new StringBuilder();
            buf.append( theNetMeshBaseIdentifier.toExternalForm() );
            buf.append( SEPARATOR );
            buf.append( theLocalId );
            return buf.toString();
        } else {
            return theNetMeshBaseIdentifier.toExternalForm();
        }
    }

    /**
     * Obtain the external form just of the local part of the NetMeshObjectIdentifier.
     * 
     * @return the local external form
     */
    public String toLocalExternalForm()
    {
        if( theLocalId == null || theLocalId.length() == 0 ) {
            return "";
        } else {
            return SEPARATOR + theLocalId;
        }
    }

    /**
     * The Identifier for the NetMeshBase in which this NetMeshObjectIdentifier was allocated.
     */
    protected NetMeshBaseIdentifier theNetMeshBaseIdentifier;

    /**
     * Separator between NetMeshBaseIdentifier and local id.
     */
    public static final char SEPARATOR = '#';
}
