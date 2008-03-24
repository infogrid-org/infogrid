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

package org.infogrid.meshbase.net.externalized.xml;

import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObjectFactory;

import org.infogrid.meshbase.net.externalized.ExternalizedProxy;
import org.infogrid.meshbase.net.externalized.ExternalizedProxyEncoder;
import org.infogrid.meshbase.net.externalized.ParserFriendlyExternalizedProxy;

import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.meshbase.net.xpriso.xml.XprisoMessageXmlEncoder;

import org.infogrid.model.primitives.externalized.DecodingException;
import org.infogrid.model.primitives.externalized.EncodingException;

import org.infogrid.modelbase.MeshTypeIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;

import org.infogrid.util.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class ExternalizedProxyXmlEncoder
        extends
            XprisoMessageXmlEncoder
        implements
            ExternalizedProxyEncoder,
            ExternalizedProxyXmlTags
{
    private static final Log log = Log.getLogInstance( ExternalizedProxyXmlEncoder.class ); // our own, private logger

    /**
     * Serialize an ExternalizedProxy to an OutputStream.
     * 
     * @param obj the input ExternalizedProxy
     * @param out the OutputStream to which to append the ExternalizedProxy
     * @throws EncodingException thrown if a problem occurred during encoding
     */
    public void encodeExternalizedProxy(
            ExternalizedProxy p,
            OutputStream      out )
        throws
            EncodingException,
            IOException
    {
        StringBuilder buf = new StringBuilder();

        appendExternalizedProxy( p, buf );

        OutputStreamWriter w = new OutputStreamWriter( out, ENCODING );
        w.write( buf.toString() );
        w.flush();
    }

    /**
     * Encode an ExternalizedProxy.
     * 
     * @param value the input Proxy
     * @param buf the StringBuilder to append to
     */
    public void appendExternalizedProxy(
            ExternalizedProxy value,
            StringBuilder     buf )
        throws
            EncodingException
    {
        buf.append( "<" ).append( PROXY_INLINED_TAG );
        buf.append( " " ).append( TIME_CREATED_TAG ).append( "=\"" );
        appendLong( value.getTimeCreated(), buf );
        buf.append( "\"" );
        buf.append( " " ).append( TIME_UPDATED_TAG ).append( "=\"" );
        appendLong( value.getTimeUpdated(), buf );
        buf.append( "\"" );
        buf.append( " " ).append( TIME_READ_TAG    ).append( "=\"" );
        appendLong( value.getTimeRead(), buf );
        buf.append( "\"" );
        buf.append( " " ).append( TIME_EXPIRES_TAG ).append( "=\"" );
        appendLong( value.getTimeExpires(), buf );
        buf.append( "\"" );
        buf.append( " " ).append( HERE_TAG  ).append( "=\"" );
        appendNetworkIdentifier( value.getNetworkIdentifier(), buf );
        buf.append( "\"" );
        buf.append( " " ).append( THERE_TAG ).append( "=\"" );
        appendNetworkIdentifier( value.getNetworkIdentifierOfPartner(), buf );
        buf.append( "\"" );
        buf.append( ">\n" );

        appendExternalizedProxyHook( value, buf );

        buf.append( "  <" ).append( LAST_SENT_TOKEN_TAG     ).append( ">" );
        appendLong( value.getLastSentToken(), buf );
        buf.append( "</" ).append( LAST_SENT_TOKEN_TAG     ).append( ">\n" );
        buf.append( "  <" ).append( LAST_RECEIVED_TOKEN_TAG ).append( ">" );
        appendLong( value.getLastReceivedToken(), buf );
        buf.append( "</" ).append( LAST_RECEIVED_TOKEN_TAG ).append( ">\n" );

        buf.append( "  <" ).append( MESSAGES_TO_SEND_TAG ).append( ">\n" );
        Iterator<XprisoMessage> iter = value.messagesToBeSent().iterator();
        while( iter.hasNext() ) {
            appendXprisoMessage( iter.next(), buf );
        }
        buf.append( "  </" ).append( MESSAGES_TO_SEND_TAG ).append( ">\n" );

        buf.append( "  <" ).append( MESSAGES_LAST_SENT_TAG ).append( ">\n" );
        iter = value.messagesLastSent().iterator();
        while( iter.hasNext() ) {
            appendXprisoMessage( iter.next(), buf );
        }
        buf.append( "  </" ).append( MESSAGES_LAST_SENT_TAG ).append( ">\n" );

        buf.append( "</" ).append( PROXY_INLINED_TAG ).append( ">" );
    }        

    /**
     * Enables subclasses to add information to an Proxy.
     * 
     * @param value the input Proxy
     * @param buf the StringBuilder to append to
     */
    protected void appendExternalizedProxyHook(
            ExternalizedProxy value,
            StringBuilder     buf )
        throws
            EncodingException
    {
        // nothing on this level
    }

    /**
     * Deserialize an ExternalizedProxy from a byte stream.
     *
     * @param s the InputStream from which to read
     * @throws DecodingException thrown if a problem occurred during decoding
     */
    public ExternalizedProxy decodeExternalizedProxy(
            InputStream                                    s,
            ParserFriendlyExternalizedNetMeshObjectFactory externalizedMeshObjectFactory,
            NetMeshObjectIdentifierFactory                 meshObjectIdentifierFactory,
            MeshTypeIdentifierFactory                      meshTypeIdentifierFactory )
        throws
            DecodingException,
            IOException
    {
        theExternalizedMeshObjectFactory = externalizedMeshObjectFactory; // note the synchronized statement
        theMeshObjectIdentifierFactory   = meshObjectIdentifierFactory;
        theMeshTypeIdentifierFactory     = meshTypeIdentifierFactory;

        try {
            theParser.parse( s, this );

            return theProxyBeingParsed;

        } catch( SAXException ex ) {
            throw new DecodingException( ex );

        } finally {
            clearState();
        }
    }

    /**
     * Obtain an encodingId that reflects this ExternalizedProxyXmlEncoder.
     * 
     * @return the encodingId.
     */
    @Override
    public String getEncodingId()
    {
        return getClass().getName();
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     */
    @Override
    protected void startElement4(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( PROXY_INLINED_TAG.equals( qName )) {
            theProxyBeingParsed = createParserFriendlyExternalizedProxy();

            String here  = attrs.getValue( HERE_TAG );
            String there = attrs.getValue( THERE_TAG );

            if( here != null ) {
                try {
                    theProxyBeingParsed.setNetworkIdentifier( NetMeshBaseIdentifier.fromExternalForm( here ));
                } catch( URISyntaxException ex ) {
                    log.warn( ex ); // we can do without this one
                }
            }
                
            if( there != null ) {
                try {
                    theProxyBeingParsed.setNetworkIdentifierOfPartner( NetMeshBaseIdentifier.fromExternalForm( there ));
                } catch( URISyntaxException ex ) {
                    error( ex ); // we cannot do without this one
                }
            } else {
                throw new SAXException( "No " + THERE_TAG + " attribute on node " + PROXY_INLINED_TAG );
            }

            theProxyBeingParsed.setTimeCreated( parseLong( attrs, TIME_CREATED_TAG, -1 ));
            theProxyBeingParsed.setTimeUpdated( parseLong( attrs, TIME_UPDATED_TAG, -1 ));
            theProxyBeingParsed.setTimeRead(    parseLong( attrs, TIME_READ_TAG,    -1 ));
            theProxyBeingParsed.setTimeExpires( parseLong( attrs, TIME_EXPIRES_TAG, -1 ));
            
        } else if( LAST_SENT_TOKEN_TAG.equals( qName )) {
            // no op
        } else if( LAST_RECEIVED_TOKEN_TAG.equals( qName )) {
            // no op
        } else if( MESSAGES_TO_SEND_TAG.equals( qName )) {
            theMessagesBeingParsed = new ArrayList<XprisoMessage>();

        } else if( MESSAGES_LAST_SENT_TAG.equals( qName )) {
            theMessagesBeingParsed = new ArrayList<XprisoMessage>();

        } else {
            startElement5( namespaceURI, localName, qName, attrs );
        }
    }
    
    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     */
    protected void startElement5(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        log.error( "unknown qname " + qName );
    }
    
    /**
     * Override end-of-message handling.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    @Override
    protected void endElement3(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        if( MESSAGE_TAG.equals( qName )) {
            theMessagesBeingParsed.add( theMessage );
        } else {
            super.endElement3( namespaceURI, localName, qName );
        }
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    @Override
    protected void endElement4(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        if( PROXY_INLINED_TAG.equals( qName )) {
            // do nothing
            
        } else if( LAST_SENT_TOKEN_TAG.equals( qName )) {
            theProxyBeingParsed.setLastSentToken( parseLong( theCharacters.toString(), -1 ));

        } else if( LAST_RECEIVED_TOKEN_TAG.equals( qName )) {
            theProxyBeingParsed.setLastReceivedToken( parseLong( theCharacters.toString(), -1 ));
    
        } else if( MESSAGES_TO_SEND_TAG.equals( qName )) {
            theProxyBeingParsed.setMessagesToSend( theMessagesBeingParsed );
            theMessagesBeingParsed = null;

        } else if( MESSAGES_LAST_SENT_TAG.equals( qName )) {
            theProxyBeingParsed.setMessagesLastSent( theMessagesBeingParsed );
            theMessagesBeingParsed = null;
            
        } else {
            endElement5( namespaceURI, localName, qName );
        }
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    protected void endElement5(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        log.error( "unknown qname " + qName );
    }

    /**
     * Factory method for ExternalizedProxy. This may be overridden by subclasses.
     * 
     * @return the ParserFriendlyExternalizedProxy
     */
    protected ParserFriendlyExternalizedProxy createParserFriendlyExternalizedProxy()
    {
        ParserFriendlyExternalizedProxy ret = new ParserFriendlyExternalizedProxy();
        return ret;
    }

    /**
     * Reset the parser.
     */
    @Override
    public void clearState()
    {
        theProxyBeingParsed    = null;
        theMessagesBeingParsed = null;

        super.clearState();
    }

    /**
     * The ExternalizedProxy currently being parsed.
     */
    protected ParserFriendlyExternalizedProxy theProxyBeingParsed;
    
    /**
     * The currently parsed list of XprisoMessages.
     */
    protected ArrayList<XprisoMessage> theMessagesBeingParsed;
}
