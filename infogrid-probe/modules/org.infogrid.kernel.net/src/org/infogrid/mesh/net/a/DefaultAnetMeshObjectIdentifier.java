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

package org.infogrid.mesh.net.a;

import org.infogrid.mesh.a.DefaultAMeshObjectIdentifier;

import org.infogrid.util.text.StringRepresentation;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.util.ResourceHelper;

import java.net.URISyntaxException;

/**
 * Implements NetMeshObjectIdentifier for the in-memory NetMeshBase.
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
     * @param localId the localId of the to-be-created ReferenceValue
     * @return the created ReferenceValue
     * @throws IllegalArgumentException thrown if a non-null localId contains a period.
     */
    public static DefaultAnetMeshObjectIdentifier create(
            NetMeshBaseIdentifier baseIdentifier,
            String                localId )
    {
        if( baseIdentifier == null ) {
            throw new NullPointerException();
        }
        
        if( localId != null && localId.indexOf( '.' ) >= 0 ) {
            throw new IllegalArgumentException( "DefaultAnetMeshObjectIdentifier's localId must not contain a period: " + localId );
        }
        
        if( localId != null && localId.length() == 0 ) {
            localId = null;
        }
        
        return new DefaultAnetMeshObjectIdentifier( baseIdentifier, localId );
    }

    /**
     * Private constructor.
     * 
     * @param localId the localId of the to-be-created MeshObjectIdentifier
     */
    protected DefaultAnetMeshObjectIdentifier(
            NetMeshBaseIdentifier baseIdentifier,
            String                localId )
    {
        super( localId );

        theNetMeshBaseIdentifier = baseIdentifier;
    }

    /**
     * Obtain the Identifier of the MeshBase in which this NetMeshObjectIdentifier was allocated.
     *
     * @return the Identifier of the MeshBase
     */
    public NetMeshBaseIdentifier getNetMeshBaseIdentifier()
    {
        return theNetMeshBaseIdentifier;
    }

    /**
     * Obtain an external form for this ReferenceValue, similar to
     * URL's getExternalForm(). This returns an empty String for local home objects.
     *
     * @return external form of this ReferenceValue
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
     * Re-construct a ReferenceValue from an external form.
     *
     * @param raw the external form of the ReferenceValue
     * @return the created ReferenceValue
     */
    public static DefaultAnetMeshObjectIdentifier fromExternalForm(
            NetMeshBaseIdentifier contextIdentifier,
            String                raw )
        throws
            URISyntaxException
    {
        if( raw == null ) {
            return null;
        }
        
        NetMeshBaseIdentifier meshBase;
        String                local;
        
        DefaultAnetMeshObjectIdentifier ret;
        
        int hash = raw.indexOf( SEPARATOR );
        if( hash == 0 ) {
            meshBase = contextIdentifier;
            local    = raw.substring( hash+1 );
        } else if( hash > 0 ) {
            meshBase = NetMeshBaseIdentifier.fromExternalForm( raw.substring( 0, hash ));
            local    = raw.substring( hash+1 );
        } else if( raw.indexOf( '.' ) >= 0 ) {
            meshBase = NetMeshBaseIdentifier.fromExternalForm( raw );
            local    = null;
        } else {
            meshBase = contextIdentifier;
            local    = raw;
        }
        ret = DefaultAnetMeshObjectIdentifier.create(
                meshBase,
                local );
        return ret;
    }

    /**
     * Convert this PropertyValue to its String representation, using the representation scheme.
     *
     * @param representation the representation scheme
     * @return the String representation
     */
    @Override
    public String toStringRepresentation(
            StringRepresentation representation )
    {
        //  just like in superclass
        return super.toStringRepresentation( representation );
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
     * Our ResourceHelper.
     */
    public static final ResourceHelper RESOURCEHELPER = ResourceHelper.getInstance( DefaultAnetMeshObjectIdentifier.class );
    
    /**
     * Separator between NetMeshBaseIdentifier and local id.
     */
    public static final char SEPARATOR = '#';
}
