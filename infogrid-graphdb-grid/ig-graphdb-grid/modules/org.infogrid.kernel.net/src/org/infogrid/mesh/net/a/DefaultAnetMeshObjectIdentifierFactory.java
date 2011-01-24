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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh.net.a;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.mesh.a.DefaultAMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.util.InvalidCharacterParseException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.StringTooShortParseException;
import org.infogrid.util.text.StringRepresentation;

/**
 * The default NetMeshObjectIdentifierFactory in the "A" implementation.
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
     * @param meshBaseIdentifier the NetMeshBaseIdentifier of the owning NetMeshBase
     * @param meshBaseIdentifierFactory factory for NetMeshBaseIdentifiers
     * @return the created DefaultAMeshObjectIdentifierFactory
     */
    public static DefaultAnetMeshObjectIdentifierFactory create(
            NetMeshBaseIdentifier        meshBaseIdentifier,
            NetMeshBaseIdentifierFactory meshBaseIdentifierFactory )
    {
        DefaultAnetMeshObjectIdentifierFactory ret
                = new DefaultAnetMeshObjectIdentifierFactory( meshBaseIdentifier, meshBaseIdentifierFactory );
        return ret;
    }

    /**
     * Constructor.
     * 
     * @param meshBaseIdentifier the NetMeshBaseIdentifier of the owning NetMeshBase
     * @param meshBaseIdentifierFactory factory for NetMeshBaseIdentifiers
     */
    protected DefaultAnetMeshObjectIdentifierFactory(
            NetMeshBaseIdentifier        meshBaseIdentifier,
            NetMeshBaseIdentifierFactory meshBaseIdentifierFactory )
    {
        theMeshBaseIdentifier        = meshBaseIdentifier;
        theMeshBaseIdentifierFactory = meshBaseIdentifierFactory;

        NET_HOME_OBJECT = new HomeObject( this, theMeshBaseIdentifier );
    }

    /**
     * Create a unique NetMeshObjectIdentifier.
     *
     * @return the unique NetMeshObjectIdentifier
     */
    @Override
    public NetMeshObjectIdentifier createMeshObjectIdentifier()
    {
        return (NetMeshObjectIdentifier) super.createMeshObjectIdentifier();
    }

    /**
     * Create an identifier for a MeshObject at held locally at this MeshBase.
     *
     * @param raw the identifier String
     * @return the created DefaultAnetMeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    @Override
    public DefaultAnetMeshObjectIdentifier fromExternalForm(
            String raw )
        throws
            ParseException
    {
        DefaultAnetMeshObjectIdentifier ret = obtain( theMeshBaseIdentifier, raw, false );
        return ret;
    }

    /**
     * Re-construct a DefaultAnetMeshObjectIdentifier from an external form.
     *
     * @param contextIdentifier identifier of the NetMeshBase relative to which the external form is to be evaluated
     * @param raw the external form of the DefaultAnetMeshObjectIdentifier
     * @return the created DefaultAnetMeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    public DefaultAnetMeshObjectIdentifier fromExternalForm(
            NetMeshBaseIdentifier contextIdentifier,
            String                raw )
        throws
            ParseException
    {
        return obtain( contextIdentifier, raw, false );
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
    @Override
    public DefaultAnetMeshObjectIdentifier guessFromExternalForm(
            NetMeshBaseIdentifier contextIdentifier,
            String                raw )
        throws
            ParseException
    {
        return obtain( contextIdentifier, raw, true );
    }

    /**
     * Recreate a NetMeshObjectIdentifier from an external form. Be lenient about syntax and
     * attempt to interpret what the user meant when entering an invalid or incomplete
     * raw String.
     *
     * @param raw the external form
     * @return the created MeshObjectIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    @Override
    public DefaultAnetMeshObjectIdentifier guessFromExternalForm(
            String raw )
        throws
            ParseException
    {
        return obtain( theMeshBaseIdentifier, raw, true );
    }

    /**
     * Re-construct a DefaultAnetMeshObjectIdentifier from an external form.
     *
     * @param contextIdentifier identifier of the NetMeshBase relative to which the external form is to be evaluated
     * @param raw the external form of the DefaultAnetMeshObjectIdentifier
     * @param guess if true, attempt to guess the protocol if none was given
     * @return the created DefaultAnetMeshObjectIdentifier
     * @throws ParseException a parsing error occurred
     */
    protected DefaultAnetMeshObjectIdentifier obtain(
            NetMeshBaseIdentifier contextIdentifier,
            String                raw,
            boolean               guess )
        throws
            ParseException
    {
        if( raw == null ) {
            raw = "";
        }
        if( raw.length() == 0 ) {
            return new HomeObject( this, contextIdentifier );
        }
        
        NetMeshBaseIdentifier meshBase;
        String                localId;
        
        DefaultAnetMeshObjectIdentifier ret;
        
        int hash = raw.indexOf( DefaultAnetMeshObjectIdentifier.SEPARATOR );
        if( hash == 0 ) {
            meshBase = contextIdentifier;
            localId  = raw.substring( hash+1 );
        } else if( hash > 0 ) {
            if( guess ) {
                meshBase = theMeshBaseIdentifierFactory.guessFromExternalForm( contextIdentifier, raw.substring( 0, hash ));
            } else {
                meshBase = theMeshBaseIdentifierFactory.fromExternalForm( contextIdentifier, raw.substring( 0, hash ));
            }
            localId = raw.substring( hash+1 );
        } else if( treatAsGlobalIdentifier( raw )) {
            if( guess ) {
                meshBase = theMeshBaseIdentifierFactory.guessFromExternalForm( raw );
            } else {
                meshBase = theMeshBaseIdentifierFactory.fromExternalForm( raw );
            }
            localId = null;
        } else {
            meshBase = contextIdentifier;
            localId    = raw;
        }

        if( meshBase == null ) {
            throw new NullPointerException();
        }

        if( localId != null && treatAsGlobalIdentifier( localId )) {
            throw new IllegalArgumentException( "DefaultAnetMeshObjectIdentifier's localId must not contain a period: " + localId );
        }

        if( localId == null ) {
            localId = "";
        }

        checkRawId( meshBase, localId );

        ret = DefaultAnetMeshObjectIdentifier.create(
                this,
                meshBase,
                localId,
                raw );
        return ret;
    }
    
    /**
     * Factory method.
     *
     * @param file the localId File whose NetMeshObjectIdentifier we obtain
     * @return the created NetMeshObjectIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public DefaultAnetMeshObjectIdentifier obtain(
            File file )
        throws
            ParseException
    {
        return obtain( file.toURI() );
    }

    /**
     * Factory method.
     *
     * @param url the URL whose NetMeshObjectIdentifier we obtain
     * @return the created NetMeshObjectIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public DefaultAnetMeshObjectIdentifier obtain(
            URL url )
        throws
            ParseException
    {
        return obtain( theMeshBaseIdentifier, url.toExternalForm(), false );
    }

    /**
     * Factory method.
     *
     * @param uri the URI whose NetMeshObjectIdentifier we obtain
     * @return the created NetMeshObjectIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public DefaultAnetMeshObjectIdentifier obtain(
            URI uri )
        throws
            ParseException
    {
        return obtain( theMeshBaseIdentifier, uri.toASCIIString(), false );
    }

    /**
     * Determine whether a given String is to be treated as a global identifier. This
     * method encodes our policy if the String is ambiguous.
     *
     * @param raw the String
     * @return true if the String is to be treated as a global identifier
     */
    public boolean treatAsGlobalIdentifier(
            String raw )
    {
        if( raw.indexOf( '.' ) >= 0 || raw.indexOf( "localhost" ) >= 0 ) {
            return true;
        }
        try {
            MeshBaseIdentifier found = theMeshBaseIdentifierFactory.fromExternalForm( raw );
            return true;

        } catch( ParseException ex ) {
            // ignore
        }
        return false;
    }

    /**
     * Convert this StringRepresentation back to an Identifier.
     *
     * @param representation the StringRepresentation in which this String is represented
     * @param s the String to parse
     * @return the created MeshObjectIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    @Override
    public DefaultAnetMeshObjectIdentifier fromStringRepresentation(
            StringRepresentation representation,
            String               s )
        throws
            ParseException
    {
        Object [] found = representation.parseEntry( DefaultAnetMeshObjectIdentifier.class, StringRepresentation.DEFAULT_ENTRY, s, this );
        return (DefaultAnetMeshObjectIdentifier) found[0];
    }

    /**
     * Determine the MeshObjectIdentifier of the Home Object.
     *
     * @return the MeshObjectIdentifier
     */
    @Override
    public DefaultAnetMeshObjectIdentifier getHomeMeshObjectIdentifier()
    {
        return NET_HOME_OBJECT;
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
        return new HomeObject( this, mbIdentifier );
    }

    /**
     * Check whether the proposed String for a NetMeshObjectIdentifier is valid.
     * Subclasses can override this.
     *
     * @param mbId the MeshBaseIdentifier component
     * @param rawLocalId the proposed local ID component
     * @throws ParseException thrown if the String is not valid
     */
    protected void checkRawId(
            NetMeshBaseIdentifier mbId,
            String                rawLocalId )
        throws
            ParseException
    {
        if( rawLocalId == null ) {
            throw new NullPointerException();
        }
        if( rawLocalId.length() == 0 ) {
            return;
        }

        for( int i=0 ; i<DISALLOWED_LOCAL_ID_STRINGS.length ; ++i ) {
            if( rawLocalId.indexOf( DISALLOWED_LOCAL_ID_STRINGS[i] ) >= 0 ) {
                throw new InvalidCharacterParseException( rawLocalId, null, rawLocalId.indexOf( DISALLOWED_LOCAL_ID_STRINGS[i] ), DISALLOWED_LOCAL_ID_STRINGS[i] );
            }
        }
        if( MINIMUM_LOCAL_ID_LENGTH > 0 && rawLocalId.length() < MINIMUM_LOCAL_ID_LENGTH ) {
            throw new StringTooShortParseException( rawLocalId, null, MINIMUM_LOCAL_ID_LENGTH );
        }
    }

    /**
     * Identifies the NetMeshBase to which this factory belongs.
     */
    protected NetMeshBaseIdentifier theMeshBaseIdentifier;
    
    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory;
    
    /**
     * The home object identifier.
     */
    public final DefaultAnetMeshObjectIdentifier NET_HOME_OBJECT;

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( DefaultAnetMeshObjectIdentifierFactory.class );

    /**
     * The minimum length for a local id.
     */
    public final static int MINIMUM_LOCAL_ID_LENGTH = theResourceHelper.getResourceIntegerOrDefault( "MinimumLocalIdLength", 4 );

    /**
     * The disallowed character strings in a local id.
     */
    public final static String [] DISALLOWED_LOCAL_ID_STRINGS = theResourceHelper.getResourceStringArrayOrDefault(
            "DisallowedLocalIdString",
            new String [] {
                    ".",
                    "" + DefaultAnetMeshObjectIdentifier.SEPARATOR
            } );

    /**
     * This subclass of DefaultAnetMeshObjectIdentifier is only used for identifiers
     * of home objects.
     */
    private static class HomeObject
            extends
                DefaultAnetMeshObjectIdentifier
    {
        /**
         * Constructor.
         * 
         * @param factory the DefaultAnetMeshObjectIdentifierFactory that created this identifier
         * @param meshBaseIdentifier the NetMeshBaseIdentifier of the owning NetMeshBase
         */
        HomeObject(
                DefaultAnetMeshObjectIdentifierFactory factory,
                NetMeshBaseIdentifier                  meshBaseIdentifier )
        {
            super( factory, meshBaseIdentifier, "", null );
        }
    }
}
