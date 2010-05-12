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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.InvalidObjectNumberFoundParseException;
import org.infogrid.util.InvalidObjectTypeFoundParseException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.StringTooShortParseException;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParseException;

/**
 * Default implementation of NetMeshBaseIdentifierFactory.
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
                DEFAULT_RESTFUL_PROTOCOL_NAMES,
                DEFAULT_NON_RESTFUL_PROTOCOL_NAMES );
        return ret;
    }
    
    /**
     * Factory method for a specified set of protocols.
     * 
     * @param restfulProtocols the supported REST-ful protocols
     * @param nonRestfulProtocols the supported non-REST-ful protocols
     * @return the created DefaultNetMeshBaseIdentifierFactory
     */
    public static DefaultNetMeshBaseIdentifierFactory create(
            String [] restfulProtocols,
            String [] nonRestfulProtocols )
    {
        DefaultNetMeshBaseIdentifierFactory ret = new DefaultNetMeshBaseIdentifierFactory(
                restfulProtocols,
                nonRestfulProtocols );
        return ret;
    }
    
    /**
     * Constructor.
     * 
     * @param restfulProtocols the supported REST-ful protocols
     * @param nonRestfulProtocols the supported non-REST-ful protocols
     */
    protected DefaultNetMeshBaseIdentifierFactory(
            String [] restfulProtocols,
            String [] nonRestfulProtocols )
    {
        theRestfulProtocols    = restfulProtocols;
        theNonRestfulProtocols = nonRestfulProtocols;
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
        string = string.trim();

        String canonical = string;

        if( canonical != null ) {
            Matcher m = thePort80Pattern.matcher( canonical );
            if( m.matches() ) {
                String zapped = m.group( 1 ) + m.group( 2 );

                canonical = zapped;
            } else {
                m = thePort443Pattern.matcher( canonical );
                if( m.matches() ) {
                    String zapped = m.group( 1 ) + m.group( 2 );

                    canonical = zapped;
                }
            }
        }

        if( canonical.length() == 0 ) {
            throw new StringTooShortParseException( canonical, null, 1 );
        }

        if( isXriGlobalContextSymbol( canonical.charAt( 0 ))) {
            try {
                return new NetMeshBaseIdentifier( this, canonical, new URI( theXriResolverPrefix + canonical ), string, true );
                
            } catch( URISyntaxException ex ) {
                throw new StringRepresentationParseException( canonical, null, ex );
            }
        }
        if( canonical.startsWith( theXriResolverPrefix )) {
            canonical = canonical.substring( theXriResolverPrefix.length() );
            try {
                return new NetMeshBaseIdentifier( this, canonical, new URI( theXriResolverPrefix + canonical ), string, true );

            } catch( URISyntaxException ex ) {
                throw new StringRepresentationParseException( canonical, null, ex );
            }
        }

        String lower = canonical.toLowerCase();
        
        if( guess ) {
            if( lower.indexOf( "://" ) < 0 ) {
                String prefix = null;
                if( context != null ) {
                    String contextString = context.toExternalForm();
                    int lastSlash = contextString.lastIndexOf( '/' );
                    if( lastSlash > 0 ) {
                        prefix = contextString.substring( 0, lastSlash );
                    }
                }
                if( prefix != null ) {
                    canonical = prefix + "/" + canonical;
                } else if( canonical.contains( "/" )) {
                    canonical = "http://" + canonical;
                } else {
                    canonical = "http://" + canonical + "/"; // example "cnn.com" without trailing slash
                }

                lower = canonical.toLowerCase();
            }
        }
        
        for( String p : theRestfulProtocols ) {
            if( lower.startsWith( p + ":" )) {
                try {
                    return new NetMeshBaseIdentifier( this, canonical, new URI( canonical ), string, true );

                } catch( URISyntaxException ex ) {
                    throw new StringRepresentationParseException( canonical, null, ex );
                }
            }
        }
        for( String p : theNonRestfulProtocols ) {
            if( lower.startsWith( p + ":" )) {
                try {
                    return new NetMeshBaseIdentifier( this, canonical, new URI( canonical ), string, false );

                } catch( URISyntaxException ex ) {
                    throw new StringRepresentationParseException( canonical, null, ex );
                }
            }
        }

        throw new UnknownProtocolParseException(
                canonical,
                -1,
                lower,
                ArrayHelper.append( theRestfulProtocols, theNonRestfulProtocols, String.class ) );
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
     * Determine whether a character is an XRI global context symbol.
     *
     * @param c the character
     * @return true if this character is an XRI global context symbol
     */
    public static boolean isXriGlobalContextSymbol(
            char c )
    {
        switch( c ) {
            case '=':
            case '@':
            case '+':
            case '$':
            case '!':
                return true;

            default:
                return false;
        }
    }

    /**
     * Helper method to remove .. and . from paths.
     *
     * @param in the input String
     * @return the output String
     */
    protected static String stripDirectoryPaths(
            String in )
    {
        return in; // FIXME
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( DefaultNetMeshBaseIdentifierFactory.class );
    
    /**
     * The XRI resolver location.
     */
    private static final String theXriResolverPrefix = theResourceHelper.getResourceStringOrDefault(
            "XriResolverPrefix",
            "http://xri.net/" );

    /**
     * The REST-ful protocols for this instance.
     */
    protected String [] theRestfulProtocols;

    /*
     * The non-REST-ful protocols for this instance.
     */
    protected String [] theNonRestfulProtocols;

    /**
     * The pattern that allows us to remove a unnecessary port 80 from a URL spec.
     */
    public static final Pattern thePort80Pattern = Pattern.compile(
            "^(http://[^/:]+):80(/.*)$" );

    /**
     * The pattern that allows us to remove a unnecessary port 443 from a URL spec.
     */
    public static final Pattern thePort443Pattern = Pattern.compile(
            "^(https://[^/:]+):443(/.*)$" );

    /**
     * The default REST-ful protocols for this class.
     */
    protected static final String [] DEFAULT_RESTFUL_PROTOCOL_NAMES = theResourceHelper.getResourceStringArrayOrDefault(
            "RestfulProtocolNames",
            new String [] { "http", "https", "file" } );

    /**
     * The default non-REST-ful protocol for this class.
     */
    protected static final String [] DEFAULT_NON_RESTFUL_PROTOCOL_NAMES = theResourceHelper.getResourceStringArrayOrDefault(
            "NonRestfulProtocolNames",
            new String[] {} );
}
