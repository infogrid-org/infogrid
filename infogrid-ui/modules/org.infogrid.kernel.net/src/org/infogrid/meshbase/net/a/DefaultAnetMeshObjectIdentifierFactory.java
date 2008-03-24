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

package org.infogrid.meshbase.net.a;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifier;

import org.infogrid.meshbase.a.DefaultAMeshObjectIdentifierFactory;

import org.infogrid.meshbase.net.NetMeshBaseAccessSpecification;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;

import org.infogrid.util.text.StringifierException;
import org.infogrid.util.text.StringRepresentation;

import java.net.URISyntaxException;

/**
 *
 */
public class DefaultAnetMeshObjectIdentifierFactory
        extends
            DefaultAMeshObjectIdentifierFactory
        implements
            NetMeshObjectIdentifierFactory
{
    /**
     * Factory method.
     *
     * @return the created DefaultAMeshObjectIdentifierFactory
     */
    public static DefaultAnetMeshObjectIdentifierFactory create(
            NetMeshBaseIdentifier meshBaseIdentifier )
    {
        DefaultAnetMeshObjectIdentifierFactory ret = new DefaultAnetMeshObjectIdentifierFactory( meshBaseIdentifier );
        return ret;
    }

    /**
     * Constructor.
     */
    protected DefaultAnetMeshObjectIdentifierFactory(
            NetMeshBaseIdentifier meshBaseIdentifier )
    {
        theNetMeshBaseIdentifier = meshBaseIdentifier;

        HOME_OBJECT = new DefaultAnetMeshObjectIdentifier( theNetMeshBaseIdentifier, null ) {} ; // subclass so we can avoid making it public
    
    }

    /**
     * Create a unique Identifier.
     *
     * @return the unique Identifier
     */
    @Override
    public NetMeshObjectIdentifier createMeshObjectIdentifier()
    {
        return (NetMeshObjectIdentifier) super.createMeshObjectIdentifier();
    }

    /**
     * Create an identifier for a MeshObject at held locallt at this MeshBase.
     *
     * @param raw the identifier String
     */
    @Override
    public DefaultAnetMeshObjectIdentifier fromExternalForm(
            String raw )
        throws
            URISyntaxException
    {
        DefaultAnetMeshObjectIdentifier ret = DefaultAnetMeshObjectIdentifier.fromExternalForm( theNetMeshBaseIdentifier, raw );
        return ret;
    }

    /**
     * Convert this StringRepresentation back to an Identifier.
     *
     * @param representation the StringRepresentation in which this String is represented
     * @param s the String to parse
     * @return the created MeshObjectIdentifier
     */
    @Override
    public DefaultAnetMeshObjectIdentifier fromStringRepresentation(
            StringRepresentation representation,
            String               s )
        throws
            URISyntaxException
    {
        try {
            representation.parseEntry( DefaultAnetMeshObjectIdentifier.RESOURCEHELPER, DefaultAnetMeshObjectIdentifier.HOME_DEFAULT_ENTRY, s );
            return fromExternalForm( "" );

        } catch( StringifierException ex ) {
            // that wasn't it ...
        }
        try {
            Object [] found = representation.parseEntry( DefaultAnetMeshObjectIdentifier.RESOURCEHELPER, DefaultAnetMeshObjectIdentifier.DEFAULT_ENTRY, s );

            DefaultAnetMeshObjectIdentifier ret;
            switch( found.length ) {
                case 1:
                    ret = fromExternalForm( (String) found[0] );
                    break;

                default:
                    throw new URISyntaxException( s, "Cannot parse identifier" );
            }

            return ret;

        } catch( StringifierException ex ) {
            throw new URISyntaxException( s, "Cannot parse identifier" );

        } catch( ClassCastException ex ) {
            throw new URISyntaxException( s, "Cannot parse identifier" );
        }
    }
    
    /**
     * Determine the Identifier of the Home Object.
     *
     * @return the Identifier
     */
    @Override
    public DefaultAnetMeshObjectIdentifier getHomeMeshObjectIdentifier()
    {
        return HOME_OBJECT;
    }
    
    /**
     * Create a NetMeshObjectAccessSpecifiation from an external form.
     *
     * The Syntax is as follows:
     *
     * <code>NetMeshBaseIdentifier!NetMeshBaseIdentifier!NetMeshBaseIdentifier#NetMeshObjectIdentifier</code>
     * where <code>NetMeshBaseIdentifier</code> may contain a # itself.
     *
     * @param raw the external form
     * @return the created NetMeshObjectAccessSpecification
     * @throws URISyntaxException a parsing error occurred
     */
    public NetMeshObjectAccessSpecification createNetMeshObjectAccessSpecificationFromExternalForm(
            String raw )
        throws
            URISyntaxException
    {
        if( raw == null ) {
            return null;
        }

        int hash = raw.indexOf( '#' );
        
        String pathString;
        String objectString;
        if( hash >= 0 ) {
            pathString   = raw.substring( 0, hash );
            objectString = raw.substring( hash+1 );

        } else if( raw.indexOf( '.' ) >= 0 ) {
            pathString   = raw;
            objectString = null;
        } else {
            pathString   = null;
            objectString = raw;
        }

        NetMeshBaseAccessSpecification [] pathElements;
        
        if( pathString != null && pathString.length() > 0 ) {
            String [] pathElementStrings = pathString.split( "!" );

            pathElements = new NetMeshBaseAccessSpecification[ pathElementStrings.length ];
            for( int i=0 ; i<pathElements.length ; ++i ) {
                pathElements[i] = NetMeshBaseAccessSpecification.fromExternalForm( pathElementStrings[i] );
            }
        } else {
            pathElements = new NetMeshBaseAccessSpecification[0];
        }
        
        NetMeshObjectIdentifier object = null;
        if( objectString != null ) {
            object = fromExternalForm( objectString );
        }
        
        NetMeshObjectAccessSpecification ret = NetMeshObjectAccessSpecification.create(
                pathElements,
                object );
        return ret;        
    }
    
    
    /**
     * Identifies the NetMeshBase to which this factory belongs.
     */
    protected NetMeshBaseIdentifier theNetMeshBaseIdentifier;

    /**
     * The home object identifier.
     */
    public final DefaultAnetMeshObjectIdentifier HOME_OBJECT;
}
