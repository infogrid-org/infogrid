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

package org.infogrid.meshbase.net;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import org.infogrid.meshbase.net.schemes.AcctScheme;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.HttpsScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.XriScheme;
import org.infogrid.util.InvalidObjectNumberFoundParseException;
import org.infogrid.util.InvalidObjectTypeFoundParseException;
import org.infogrid.util.StringTooShortParseException;
import org.infogrid.util.text.StringRepresentation;

/**
 * Default implementation of NetMeshBaseIdentifierFactory. This class uses an array of Schemes
 * that are supported, such as http, acct, xri and the like. These schemes are tried in
 * sequence, first strictly, and then with educated guesses (if guessing is enabled). This
 * sequence is important if an identifier is valid in several schemes (e.g. \@foo could be
 * both a Twitter handle and an XRI), or could be guessed correctly in several schemes
 * (e.g. foo@example.com could be an acct, but also http and https).
 */
public class DefaultNetMeshBaseIdentifierFactory
        implements
            NetMeshBaseIdentifierFactory
{
    /**
     * Factory method for default protocols.
     * 
     * @return the created DefaultNetMeshBaseIdentifierFactory
     */
    public static DefaultNetMeshBaseIdentifierFactory create()
    {
        DefaultNetMeshBaseIdentifierFactory ret = new DefaultNetMeshBaseIdentifierFactory(
                DEFAULT_SCHEMES );
        return ret;
    }
    
    /**
     * Factory method for a specified set of schemes. The schemes array must be ordered
     * in the series in which the Schemes are tried.
     * 
     * @param schemes the supported Schemes
     * @return the created DefaultNetMeshBaseIdentifierFactory
     */
    public static DefaultNetMeshBaseIdentifierFactory create(
            Scheme [] schemes )
    {
        DefaultNetMeshBaseIdentifierFactory ret = new DefaultNetMeshBaseIdentifierFactory( schemes );

        return ret;
    }
    
    /**
     * Constructor.
     * 
     * @param schemes the supported protocols
     */
    protected DefaultNetMeshBaseIdentifierFactory(
            Scheme [] schemes )
    {
        theSchemes = schemes;
    }

    /**
     * Obtain all Schemes known to this MeshBaseIdentifierFactory.
     *
     * @return the schemes
     */
    public Scheme [] getKnownSchemes()
    {
        return theSchemes;
    }

    /**
     * Factory method to obtain a NetMeshBaseIdentifier that is resolvable into a stream,
     * e.g. http URL. This method attempts to guess the protocol if none has been provided.
     * 
     * @param string the (potentially incomplete) String form of this NetMeshBaseIdentifier
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public NetMeshBaseIdentifier guessFromExternalForm(
            String string )
        throws
            ParseException
    {
        return obtain( null, string, true );
    }

    /**
     * Factory method to obtain a NetMeshBaseIdentifier specified in relative form in the
     * context of another NetMeshBaseIdentifier.
     * 
     * @param context the NetMeshBaseIdentifier that forms the context
     * @param string the (potentially incomplete) String form of this NetMeshBaseIdentifier
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public NetMeshBaseIdentifier guessFromExternalForm(
            NetMeshBaseIdentifier context,
            String                string )
        throws
            ParseException
    {
        return obtain( context, string, true );
    }

    /**
     * Fully-specified factory method.
     * 
     * @param context the NetMeshBaseIdentifier that forms the context
     * @param string the (potentially incomplete) String form of this NetMeshBaseIdentifier
     * @param guess if true, attempt to guess the protocol if none was given
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    protected NetMeshBaseIdentifier obtain(
            NetMeshBaseIdentifier context,
            String                string,
            boolean               guess )
        throws
            ParseException
    {
        if( string == null ) {
            throw new NullPointerException();
        }
        if( guess ) {
            string = string.trim();
        }
        if( string.length() == 0 ) {
            throw new StringTooShortParseException( string, null, 1 );
        }

        String contextString;
        if( context != null ) {
            contextString = context.toExternalForm();
            
            int startFrom = contextString.indexOf( "//" );
            if( startFrom >= 0 ) {
                startFrom += 2; // position after the double slash
            } else {
                startFrom = 0; // beginning
            }
            
            int lastSlash = contextString.substring( startFrom ).lastIndexOf( '/' );
            if( lastSlash > 0 ) {
                contextString = contextString.substring( 0, startFrom + lastSlash+1 ); // we want the slash
            } else {
                contextString = null; // cannot come up with something reasonable
            }
        } else {
            contextString = null;
        }

        NetMeshBaseIdentifier ret;

        for( Scheme current : theSchemes ) {
            ret = current.strictlyMatchAndCreate( contextString, string, this );
            if( ret != null ) {
                return ret;
            }
        }
        if( guess ) {
            for( Scheme current : theSchemes ) {
                ret = current.guessAndCreate( contextString, string, this );
                if( ret != null ) {
                    return ret;
                }
            }
        }
        throw new UnknownSchemeParseException( string, this );
    }

    /**
     * Factory method.
     * 
     * @param file the local File whose NetMeshBaseIdentifier we obtain
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public NetMeshBaseIdentifier obtain(
            File file )
        throws
            ParseException
    {
        return obtain( file.toURI() );
    }

    /**
     * Factory method.
     * 
     * @param url the URL whose NetMeshBaseIdentifier we obtain
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public NetMeshBaseIdentifier obtain(
            URL url )
        throws
            ParseException
    {
        return obtain( null, url.toExternalForm(), false );
    }

    /**
     * Factory method.
     * 
     * @param uri the URI whose NetMeshBaseIdentifier we obtain
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if the syntax could not be parsed
     */
    public NetMeshBaseIdentifier obtain(
            URI uri )
        throws
            ParseException
    {
        return obtain( null, uri.toASCIIString(), false );
    }

    /**
     * Recreate a NetMeshBaseIdentifier from an external form. Be strict about syntax.
     *
     * @param raw the external form
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public NetMeshBaseIdentifier fromExternalForm(
            String raw )
        throws
            ParseException
    {
        return obtain( null, raw, false );
    }

    /**
     * Recreate a NetMeshBaseIdentifier from an external form. Be strict about syntax.
     *
     * @param context the NetMeshBaseIdentifier that forms the context
     * @param raw the external form
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public NetMeshBaseIdentifier fromExternalForm(
            NetMeshBaseIdentifier context,
            String                raw )
        throws
            ParseException
    {
        return obtain( context, raw, false );
    }

    /**
     * Convert this StringRepresentation back to a NetMeshBaseIdentifier.
     *
     * @param representation the StringRepresentation in which this String is represented
     * @param s the String to parse
     * @return the created NetMeshBaseIdentifier
     * @throws ParseException thrown if a parsing error occurred
     */
    public NetMeshBaseIdentifier fromStringRepresentation(
            StringRepresentation representation,
            String               s )
        throws
            ParseException
    {
        try {
            Object [] found = representation.parseEntry( NetMeshBaseIdentifier.class, StringRepresentation.DEFAULT_ENTRY, s, this );

            NetMeshBaseIdentifier ret;
            switch( found.length ) {
                case 1:
                    ret = (NetMeshBaseIdentifier) found[0];
                    break;

                default:
                    throw new InvalidObjectNumberFoundParseException( s, 1, found );
            }

            return ret;

        // pass-through ParseException

        } catch( ClassCastException ex ) {
            throw new InvalidObjectTypeFoundParseException( s, NetMeshBaseIdentifier.class, null, ex );
        }
        
    }

    /**
     * The schemes supported by this instance of DefaultNetMeshBaseIdentifierFactory.
     */
    protected Scheme [] theSchemes;
    
    /**
     * The default schemes.
     */
    protected static final Scheme [] DEFAULT_SCHEMES = {
            new HttpScheme(),
            new HttpsScheme(),
            new FileScheme(),
            new AcctScheme(),
            new XriScheme(),
    };
}
