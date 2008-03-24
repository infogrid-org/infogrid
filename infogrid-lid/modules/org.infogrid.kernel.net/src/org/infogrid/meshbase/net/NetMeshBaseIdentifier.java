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

import org.infogrid.meshbase.MeshBaseIdentifier;

import org.infogrid.util.Identifier;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.StringHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a network identifier, such as a URI, XRI or URL. It has two components
 * (which may be the same): the identifier in its canonical form, and a URL-equivalent.
 * For our purposes, we always need to have an URL-equivalent, otherwise the
 * identifier cannot be used for anything useful in InfoGrid. This class performs
 * the conversion.
 */
public class NetMeshBaseIdentifier
        extends
            MeshBaseIdentifier
        implements
            Identifier
{
    /**
     * Factory method to create a NetMeshBaseIdentifier that is resolvable into a stream,
     * e.g. http URL.
     * 
     * @param canonicalForm the canonical form of this NNetMeshBaseIdentifier@return the created NeNetMeshBaseIdentifier
     */
    public static NetMeshBaseIdentifier create(
            String canonicalForm )
        throws
            URISyntaxException
    {
        return create( null, canonicalForm, true, false );
    }
    
    /**
     * Factory method to create a NetMeshBaseIdentifier that cannot be resolved into a stream,
     * e.g. jdbc
     * 
     * @param canonicalForm the canonical form of this NNetMeshBaseIdentifier
     * @return the created NetMeshBaseIdentifier
     */
    public static NetMeshBaseIdentifier createUnresolvable(
            String canonicalForm )
        throws
            URISyntaxException
    {
        return create( null, canonicalForm, false, false );
    }
    
    /**
     * Factory method to create a NetMeshBaseIdentifier that is resolvable into a stream,
     * e.g. http URL. This method attempts to guess the protocol if none has been provided.
     * 
     * @param string the (potentially incomplete) String form of this NNetMeshBaseIdentifier@return the created NeNetMeshBaseIdentifier
     */
    public static NetMeshBaseIdentifier guessAndCreate(
            String string )
        throws
            URISyntaxException
    {
        return create( null, string, true, true );
    }
    
    /**
     * Factory method to create a NetMeshBaseIdentifier specified in relative form in the
     * context of another NetMeshBaseIdentifier.
     * 
     * @param context the NNetMeshBaseIdentifierthat forms the context
     * @param string the (potentially incomplete) String form of this NeNetMeshBaseIdentifierreturn the created NetNetMeshBaseIdentifier
     */
    public static NetMeshBaseIdentifier guessAndCreate(
            NetMeshBaseIdentifier context,
            String            string )
        throws
            URISyntaxException
    {
        return create( context, string, true, true );
    }

    /**
     * Factory method.
     * 
     * 
     * @param s the canonical form of this NetMeshBaseIdentifier
     * @param checkForSupportedProtocol if true, check for supported protocols
     * @param guess if true, attempt to guess the protocol if none was given
     * @return the created NetNetMeshBaseIdentifier
     */
    protected static NetMeshBaseIdentifier create(
            NetMeshBaseIdentifier context,
            String                s,
            boolean               checkForSupportedProtocol,
            boolean               guess )
        throws
            URISyntaxException
    {
        if( s == null ) {
            throw new NullPointerException();
        }
        s = s.trim();
        if( s != null ) {
            Matcher m = thePort80Pattern.matcher( s );
            if( m.matches() ) {
                String zapped = m.group( 1 ) + m.group( 2 );

                s = zapped;
            }
        }

        if( s.length() == 0 ) {
            throw new URISyntaxException( s, "identifier cannot be empty String" );
        }

        if( isXriGlobalContextSymbol( s.charAt( 0 ))) {
            return new NetMeshBaseIdentifier( s, new URI( theXriResolverPrefix + s ), true );
        }
        if( s.startsWith( theXriResolverPrefix )) {
            s = s.substring( theXriResolverPrefix.length() );
            return new NetMeshBaseIdentifier( s, new URI( theXriResolverPrefix + s ), true );
        }

        String lower = s.toLowerCase();
        
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
                    s = prefix + "/" + s;
                } else {
                    s     = "http://" + s;
                }
                lower = s.toLowerCase();
            }
        }
        
        boolean isResolvable = false;
        for( String urlProtocol : theRestfulProtocols ) {
            if( lower.startsWith( urlProtocol + ":" )) {
                isResolvable = true;
                break;
            }
        }
        if( isResolvable ) {
            s = stripDirectoryPaths( s );
            lower = s.toLowerCase();
        }
        
        if( checkForSupportedProtocol ) {
            for( String urlProtocol : theSupportedUrlProtocols ) {
                if( lower.startsWith( urlProtocol + ":" )) {
                    return new NetMeshBaseIdentifier( s, new URI( s ), isResolvable );
                }
            }
            throw new IllegalArgumentException(
                    "canonical identifier uses unknown protocol (need one of "
                    + StringHelper.join( theSupportedUrlProtocols )
                    + "), is "
                    + s );
        } else {
            return new NetMeshBaseIdentifier( s, new URI( s ), isResolvable );
        }
    }

    /**
     * Factory method.
     * 
     * @param file the local File whose NNetMeshBaseIdentifierwe create
     */
    public static NetMeshBaseIdentifier create(
            File file )
    {
        return new NetMeshBaseIdentifier( file.toURI(), true );
    }

    /**
     * Factory method.
     * 
     * @param url the URL whose NNetMeshBaseIdentifierwe create
     */
    public static NetMeshBaseIdentifier create(
            URL url )
        throws
            URISyntaxException
    {
        return new NetMeshBaseIdentifier( url.toURI(), true );
    }

    /**
     * Factory method.
     * 
     * @param uri the URI whose NNetMeshBaseIdentifierwe create
     */
    public static NetMeshBaseIdentifier create(
            URI uri )
    {
        String lower = uri.toString().toLowerCase();
        
        boolean isResolvable = false;
        for( String urlProtocol : theRestfulProtocols ) {
            if( lower.startsWith( urlProtocol + ":" )) {
                isResolvable = true;
                break;
            }
        }
        return new NetMeshBaseIdentifier( uri, isResolvable );
    }

    /**
     * For consistency with the Java APIs, we provide this method.
     * 
     * @param canonicalForm the canonical form of this NNetMeshBaseIdentifier@return the created NeNetMeshBaseIdentifier
     */
    public static NetMeshBaseIdentifier fromExternalForm(
            String canonicalForm )
        throws
            URISyntaxException
    {
        return create( null, canonicalForm, false, false );
    }

    /**
     * Constructor.
     * 
     * @param canonicalForm the canonical representation of this identifier
     * @param uri URI representation of this identifier
     * @param isResolvable if true, this identifier is REST-fully resolvable
     */
    protected NetMeshBaseIdentifier(
            String  canonicalForm,
            URI     uri,
            boolean isResolvable )
    {
        super( canonicalForm );

        theUri           = uri;
        theIsResolvable  = isResolvable;
    }

    /**
     * Constructor.
     */
    protected NetMeshBaseIdentifier(
            URI     uri,
            boolean isResolvable )
    {
        this( uri.toString(), uri, isResolvable );
    }

    /**
     * Determine whether this NetMeshBaseIdentifier is REST-fully resolvable.
     * 
     * @return true if it is
     */
    public boolean isRestfullyResolvable()
    {
        return theIsResolvable;
    }

    /**
     * Obtain the URI form of this identifier as URI.
     */
    public URI toUri()
    {
        return theUri;
    }

    /**
     * Obtain the URL form of this identifier as URL.
     */
    public URL toUrl()
        throws
           MalformedURLException
    {
        return theUri.toURL();
    }

    /**
     * Obtain the URI form of this identifier.
     *
     * @return the URI form
     */
    public String getUriString()
    {
        if( theUriString == null ) {
            theUriString = theUri.toString();
        }
        return theUriString;
    }

    /**
     * Obtain the protocol of the URL form.
     *
     * @return the protocol.
     */
    public String getUrlProtocol()
        throws
           MalformedURLException
    {
        if( theUrlProtocol == null ) {
            theUrlProtocol = toUrl().getProtocol();
        }
        return theUrlProtocol;
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
     * Determine equality.
     *
     * @param other the Object to compare against
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof NetMeshBaseIdentifier )) {
            return false;
        }
        NetMeshBaseIdentifier realOther = (NetMeshBaseIdentifier) other;
        
        String here  = getCanonicalForm();
        String there = realOther.getCanonicalForm();
        
        boolean ret = here.equals( there );
        return ret;
    }

    /**
     * Calculate hash value. Make NetBeans happy.
     *
     * @return the hash value
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * The URI.
     */
    protected URI theUri;

    /**
     * The URI in String form. This is stored here for efficiency reasons.
     */
    protected String theUriString;

    /**
     * The protocol part of the URL form. This is stored here for efficiency reasons.
     */
    protected String theUrlProtocol;

    /**
     * Whether this NetMeshBaseIdentifier is REST-fully resolvable.
     */
    protected boolean theIsResolvable;

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( NetMeshBaseIdentifier.class );
    
    /**
     * The XRI resolver location.
     */
    private static final String theXriResolverPrefix = theResourceHelper.getResourceStringOrDefault(
            "XriResolverPrefix",
            "http://xri.net/" );

    /**
     * The XRI URI prefix.
     */
    private static final String theXriUriPrefix = "xri://";

    /**
     * The supported URL protocols.
     */
    private static final String [] theSupportedUrlProtocols = { "http", "https", "file", "ppid" };
    
    /**
     * The Restful protocols.
     */
    private static final String [] theRestfulProtocols = { "http", "https", "file" };

    /**
     * The pattern that allows us to remove a unnecessary port 80 from a URL spec.
     */
    public static final Pattern thePort80Pattern = Pattern.compile(
            "^(http[s]?://[^/:]+):80(/.*)$" );
}
