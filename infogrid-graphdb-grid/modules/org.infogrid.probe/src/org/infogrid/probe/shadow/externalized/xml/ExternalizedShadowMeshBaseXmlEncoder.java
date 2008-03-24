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

package org.infogrid.probe.shadow.externalized.xml;

import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObjectFactory;

import org.infogrid.meshbase.BulkLoadException;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;

import org.infogrid.meshbase.net.externalized.xml.ExternalizedProxyXmlEncoder;
import org.infogrid.meshbase.net.externalized.ExternalizedProxy;

import org.infogrid.model.primitives.externalized.DecodingException;
import org.infogrid.model.primitives.externalized.EncodingException;

import org.infogrid.modelbase.MeshTypeIdentifierFactory;

import org.infogrid.probe.shadow.externalized.ExternalizedShadowMeshBase;
import org.infogrid.probe.shadow.externalized.ExternalizedShadowProxy;
import org.infogrid.probe.shadow.externalized.ParserFriendlyExternalizedShadowMeshBase;
import org.infogrid.probe.shadow.externalized.ParserFriendlyExternalizedShadowProxy;

import org.infogrid.util.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.URISyntaxException;

/**
 * Parses the content of a ShadowMeshBase.
 */
public class ExternalizedShadowMeshBaseXmlEncoder
        extends
            ExternalizedProxyXmlEncoder
        implements
            ExternalizedShadowMeshBaseXmlTags
{
    private static final Log log = Log.getLogInstance( ExternalizedShadowMeshBaseXmlEncoder.class ); // our own, private logger

    /**
     * Constructor.
     */
    public ExternalizedShadowMeshBaseXmlEncoder()
    {
        // noop
    }

    /**
     * Append an ExternalizedShadowMeshBase to an OutputStream.
     *
     * @param mb the ExternalizedShadowMeshBase
     * @param appendProxies if false, do not append the Proxies
     * @param out the OutputStream to write to
     */
    public void encodeShadowMeshBase(
            ExternalizedShadowMeshBase mb,
            boolean                    appendProxies,
            OutputStream               out )
        throws
            IOException,
            EncodingException
    {
        StringBuilder buf = new StringBuilder();

        appendShadowMeshBase( mb, appendProxies, buf );

        OutputStreamWriter w = new OutputStreamWriter( out, ENCODING );
        w.write( buf.toString() );
        w.flush();
    }

    /**
     * Append an ExternalizedShadowMeshBase to a StringBuilder.
     *
     * @param mb the ExternalizedShadowMeshBase
     * @param appendProxies if false, do not append the Proxies
     * @param buf the StringBuilder to write to
     */
    public void appendShadowMeshBase(
            ExternalizedShadowMeshBase mb,
            boolean                    appendProxies,
            StringBuilder              buf )
        throws
            EncodingException
    {
        buf.append( "<" ).append( SHADOW_TAG );
        buf.append( " " ).append( SHADOW_IDENTIFIER_TAG ).append( "=\"" );
        appendNetworkIdentifier( mb.getNetworkIdentifier(), buf );        
        buf.append( "\">\n" );

        if( appendProxies ) {
            ExternalizedProxy [] proxies = mb.getExternalizedProxies();
            for( int i=0 ; i<proxies.length ; ++i ) {
                appendExternalizedProxy( proxies[i], buf );
            }
        }
        
        ExternalizedNetMeshObject [] objects = mb.getExternalizedNetMeshObjects();
        for( int i=0 ; i<objects.length ; ++i ) {
            appendExternalizedMeshObject( objects[i], buf );
        }
        
        buf.append( "</" ).append( SHADOW_TAG ).append( ">\n" );
    }

    /**
     * Enables subclasses to add information to an Proxy.
     * 
     * @param value the input Proxy
     * @param buf the StringBuilder to append to
     */
    @Override
    protected void appendExternalizedProxyHook(
            ExternalizedProxy value,
            StringBuilder     buf )
        throws
            EncodingException
    {
        if( value instanceof ExternalizedShadowProxy ) {
            ExternalizedShadowProxy realValue = (ExternalizedShadowProxy) value;
            
            if( realValue.getIsPlaceholder() ) {
                buf.append( "<" ).append( PROXY_PLACEHOLDER_TAG ).append( "/>\n" );
            }
        }
    }
   
    /**
     * Bulk-load data.
     *
     * @param inStream the Stream from which to read the data
     * @param bestEffort if true, the bulk tries to work around errors to the maximum extent possible
     * @return the iterator over the ExternalizedMeshObjects
     */
    public ExternalizedShadowMeshBase decodeShadowMeshBase(
            InputStream                                    s,
            ParserFriendlyExternalizedNetMeshObjectFactory externalizedMeshObjectFactory,
            NetMeshObjectIdentifierFactory                 meshObjectIdentifierFactory,
            MeshTypeIdentifierFactory                      meshTypeIdentifierFactory )
        throws
            DecodingException,
            IOException,
            BulkLoadException
    {
        theExternalizedMeshObjectFactory = externalizedMeshObjectFactory; // note the synchronized statement
        theMeshObjectIdentifierFactory   = meshObjectIdentifierFactory;
        theMeshTypeIdentifierFactory     = meshTypeIdentifierFactory;

        try {
            theParser.parse( s, this );

            return this.theParsedShadowMeshBase;
            
        } catch( SAXException ex ) {
            throw new BulkLoadException( ex );

        } finally {
            clearState();
        }
    }

    /**
     * Addition to parsing.
     *
     * @param namespaceURI URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     */
    @Override
    protected void startElement5(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {        
        if( SHADOW_TAG.equals( qName )) {
            String id = attrs.getValue( SHADOW_IDENTIFIER_TAG );

            theParsedShadowMeshBase = ParserFriendlyExternalizedShadowMeshBase.create();
            
            if( id != null && id.length() > 0 ) {
                try {
                    theParsedShadowMeshBase.setNetworkIdentifier( NetMeshBaseIdentifier.fromExternalForm( id ));

                } catch( URISyntaxException ex ) {
                    error( ex );
                }
            }

        } else if( PROXY_PLACEHOLDER_TAG.equals( qName )) {
            ((ParserFriendlyExternalizedShadowProxy) theProxyBeingParsed).setIsPlaceholder( true );
            
        } else {
            startElement6( namespaceURI, localName, qName, attrs );
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
    protected void startElement6(
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
     * Addition to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    @Override
    protected void endElement1(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        if( MESHOBJECT_TAG.equals( qName )) {
            theParsedShadowMeshBase.addExternalizedNetMeshObject( (ExternalizedNetMeshObject) theMeshObjectBeingParsed );    

        } else {
            super.endElement1( namespaceURI, localName, qName );
        }
    }

    /**
     * Addition to parsing.
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
            if( theParsedShadowMeshBase != null ) {
                theParsedShadowMeshBase.addExternalizedProxies( theProxyBeingParsed );
                // FIXME: this if should not be necessary: I believe we invoke
                // ExternalizedShadowMeshBaseXmlEncoder for the encoding/decoding of
                // Shadow Proxies by themselves, and it appears we should not use this
                // class for it
            }
            
        } else {
            super.endElement4( namespaceURI, localName, qName );
        }        
    }
    /**
     * Addition to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    @Override
    protected void endElement5(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        if( SHADOW_TAG.equals( qName )) {
            // noop

        } else if( PROXY_PLACEHOLDER_TAG.equals( qName )) {
            // noop
            
        } else {
            endElement6( namespaceURI, localName, qName );
        }
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    protected void endElement6(
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
    @Override
    protected ParserFriendlyExternalizedShadowProxy createParserFriendlyExternalizedProxy()
    {
        ParserFriendlyExternalizedShadowProxy ret = new ParserFriendlyExternalizedShadowProxy();
        return ret;
    }

    /**
     * Reset the parser.
     */
    @Override
    public void clearState()
    {
        theParsedShadowMeshBase = ParserFriendlyExternalizedShadowMeshBase.create();
        
        super.clearState();
    }

    /**
     * The ExternalizedShadowMeshBase being parsed.
     */
    protected ParserFriendlyExternalizedShadowMeshBase theParsedShadowMeshBase = null;
}
