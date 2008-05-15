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

package org.infogrid.mesh.externalized.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.externalized.ExternalizedMeshObjectEncoder;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObject;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObjectFactory;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.externalized.DecodingException;
import org.infogrid.model.primitives.externalized.EncodingException;
import org.infogrid.model.primitives.externalized.xml.PropertyValueXmlEncoder;
import org.infogrid.modelbase.MeshTypeIdentifierFactory;
import org.infogrid.util.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Utility methods to encode/decode an ExternalizedMeshObject to/from XML. Implements the
 * SAX interface.
 */
public class ExternalizedMeshObjectXmlEncoder
        extends
            PropertyValueXmlEncoder
        implements
            ExternalizedMeshObjectEncoder,
            ExternalizedMeshObjectXmlTags
{
    private static final Log log = Log.getLogInstance( ExternalizedMeshObjectXmlEncoder.class ); // our own, private logger

    /**
     * Constructor.
     */
    public ExternalizedMeshObjectXmlEncoder()
    {
    }

    /**
     * Serialize an ExternalizedMeshObject to an OutputStream.
     * 
     * @param obj the input ExternalizedMeshObject
     * @param out the OutputStream to which to append the ExternalizedMeshObject
     * @throws EncodingException thrown if a problem occurred during encoding
     * @throws IOException thrown if an I/O error occurred
     */
    public void encodeExternalizedMeshObject(
            ExternalizedMeshObject obj,
            OutputStream           out )
        throws
            EncodingException,
            IOException
    {
        StringBuilder buf = new StringBuilder();

        appendExternalizedMeshObject( obj, buf );

        OutputStreamWriter w = new OutputStreamWriter( out, ENCODING );
        w.write( buf.toString() );
        w.flush();
    }

    /**
     * Encode a MeshObject value.
     *
     * @param theObject the MeshObject to encode
     * @param buf the StringBuilder to append to
     * @throws EncodingException thrown if a problem occurred during encoding
     */
    public void appendExternalizedMeshObject(
            ExternalizedMeshObject theObject,
            StringBuilder          buf )
        throws
            EncodingException
    {
        appendExternalizedMeshObject( theObject, MESHOBJECT_TAG, buf );
    }

    /**
     * Encode a MeshObject value.
     *
     * @param theObject the MeshObject to encode
     * @param meshObjectTagName the XML tag name for this ExternalizedMeshObject
     * @param buf the StringBuilder to append to
     * @throws EncodingException thrown if a problem occurred during encoding
     */
    public void appendExternalizedMeshObject(
            ExternalizedMeshObject theObject,
            String                 meshObjectTagName,
            StringBuilder          buf )
        throws
            EncodingException
    {
        encodeOpeningTag( theObject, meshObjectTagName, buf );

        // types
        MeshTypeIdentifier [] allTypes = theObject.getExternalTypeIdentifiers();
        if( allTypes != null ) {
            for( int i=0 ; i<allTypes.length ; ++i ) {
                buf.append( " " );
                buf.append( "<" );
                buf.append( TYPE_TAG );
                buf.append( ">" );
                appendIdentifier( allTypes[i], buf );
                buf.append( "</" );
                buf.append( TYPE_TAG );
                buf.append( ">\n" );
            }
        }

        // properties
        MeshTypeIdentifier [] allPropertyTypes  = theObject.getPropertyTypes();
        PropertyValue []      allPropertyValues = theObject.getPropertyValues();
        if( allPropertyTypes != null ) {
            for( int i=0 ; i<allPropertyTypes.length ; ++i ) {
                PropertyValue value        = allPropertyValues[i];

                buf.append( " " );
                buf.append( "<" );
                buf.append( PROPERTY_TYPE_TAG );
                buf.append( " " );
                buf.append( TYPE_TAG );
                buf.append( "=\"" );
                appendIdentifier( allPropertyTypes[i], buf );
                buf.append( "\">" );
                appendPropertyValue( value, buf );
                buf.append( "</" );
                buf.append( PROPERTY_TYPE_TAG );
                buf.append( ">\n" );
            }
        }
        
        // neighbors
        MeshObjectIdentifier [] neighbors = theObject.getNeighbors();
        if( neighbors != null ) {
            for( int i=0 ; i<neighbors.length ; ++i ) {
                MeshObjectIdentifier  currentNeighbor  = neighbors[i];
                MeshTypeIdentifier [] currentRoleTypes = theObject.getRoleTypesFor( currentNeighbor );
                buf.append( " <" );
                buf.append( RELATIONSHIP_TAG );
                buf.append( " " );
                buf.append( IDENTIFIER_TAG );
                buf.append( "=\"" );
                appendIdentifier( currentNeighbor, buf );
                buf.append( "\"" );
                if( currentRoleTypes == null || currentRoleTypes.length == 0 ) {
                    buf.append( "/>\n" );
                } else {
                    buf.append( ">\n" );
                    for( int j=0 ; j<currentRoleTypes.length ; ++j ) {
                        buf.append( "  <" );
                        buf.append( TYPE_TAG );
                        buf.append( ">" );
                        appendIdentifier( currentRoleTypes[j], buf );
                        buf.append( "</" );
                        buf.append( TYPE_TAG );
                        buf.append( ">\n" );
                    }
                    buf.append( " </" );
                    buf.append( RELATIONSHIP_TAG );
                    buf.append( ">\n" );
                }
            }
        }
        
        // equivalents. If we have this, we have to write even null values, because otherwise we can't distinguish right from left
        MeshObjectIdentifier [] equivalentsNames = theObject.getEquivalents();
        if( equivalentsNames != null ) {
            for( int i=0 ; i<equivalentsNames.length ; ++i ) {
                buf.append( " <" );
                buf.append( EQUIVALENT_TAG );
                buf.append( ">" );
                appendIdentifier( equivalentsNames[i], buf );
                buf.append( "</" );
                buf.append( EQUIVALENT_TAG );
                buf.append( ">\n" );
            }
        }
        
        subclassEncodingHook( theObject, buf );

        buf.append( "</" ).append( meshObjectTagName ).append( ">\n" );
    }

    /**
     * Factored out to make it easy for subclasses to add to the attributes list.
     *
     * @param theObject the AMeshObject to encode
     * @param meshObjectTagName the XML tag name for this ExternalizedMeshObject
     * @param buf the StringBuffer to write to
     */
    protected void encodeOpeningTag(
            ExternalizedMeshObject theObject,
            String                 meshObjectTagName,
            StringBuilder          buf )
    {
        buf.append( "<" );
        buf.append( meshObjectTagName );
        buf.append( " " );
        buf.append( IDENTIFIER_TAG );
        buf.append( "=\"" );
        appendIdentifier( theObject.getIdentifier(), buf );
        buf.append( "\" " );
        buf.append( TIME_CREATED_TAG );
        buf.append( "=\"" );
        appendLong( theObject.getTimeCreated(), buf );
        buf.append( "\" " );
        buf.append( TIME_UPDATED_TAG );
        buf.append( "=\"" );
        appendLong( theObject.getTimeUpdated(), buf );
        buf.append( "\" " );
        buf.append( TIME_READ_TAG );
        buf.append( "=\"" );
        appendLong( theObject.getTimeRead(), buf );
        buf.append( "\" " );
        buf.append( TIME_EXPIRES_TAG );
        buf.append( "=\"" );
        appendLong( theObject.getTimeExpires(), buf );
        buf.append( "\">\n" );
    }
    
    /**
     * Hook to enable subclasses to add to the encoding defined on this level.
     *
     * @param theObject the MeshObject to encode
     * @param buf the StringBuilder to add to
     */
    protected void subclassEncodingHook(
            ExternalizedMeshObject theObject,
            StringBuilder          buf )
    {
        // noop on this level
    }
    
    /**
     * Deserialize an ExternalizedMeshObject from a byte stream.
     *
     * @param s the InputStream from which to read
     * @return return the deserialized ExternalizedMeshObject
     * @throws DecodingException thrown if a problem occurred during decoding
     * @throws IOException thrown if an I/O error occurred
     */
    public synchronized ExternalizedMeshObject decodeExternalizedMeshObject(
            InputStream                                 s,
            ParserFriendlyExternalizedMeshObjectFactory externalizedMeshObjectFactory,
            MeshObjectIdentifierFactory                 meshObjectIdentifierFactory,
            MeshTypeIdentifierFactory                   meshTypeIdentifierFactory )
        throws
            DecodingException,
            IOException
    {
        theExternalizedMeshObjectFactory = externalizedMeshObjectFactory; // note the synchronized statement
        theMeshObjectIdentifierFactory   = meshObjectIdentifierFactory;
        theMeshTypeIdentifierFactory     = meshTypeIdentifierFactory;
        
        try {
            theParser.parse( s, this );
            return theMeshObjectBeingParsed;

        } catch( SAXException ex ) {
            throw new DecodingException( ex );

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
     * @throws SAXException thrown if a parsing error occurrs
     */
    @Override
    protected void startElement1(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( MESHOBJECT_TAG.equals( qName )) {
            theMeshObjectBeingParsed = theExternalizedMeshObjectFactory.createParserFriendlyExternalizedMeshObject();

            String identifier      = attrs.getValue( IDENTIFIER_TAG );
            String timeCreated     = attrs.getValue( TIME_CREATED_TAG );
            String timeUpdated     = attrs.getValue( TIME_UPDATED_TAG );
            String timeRead        = attrs.getValue( TIME_READ_TAG );
            String timeExpires     = attrs.getValue( TIME_EXPIRES_TAG );

            if( identifier != null ) {
                try {
                    theMeshObjectBeingParsed.setIdentifier(
                            theMeshObjectIdentifierFactory.fromExternalForm( descape( identifier )));
                } catch( URISyntaxException ex ) {
                    error( ex );
                }
            }
            if( timeCreated != null && timeCreated.length() > 0 ) {
                theMeshObjectBeingParsed.setTimeCreated( Long.parseLong( timeCreated ));
            }
            if( timeUpdated != null && timeUpdated.length() > 0 ) {
                theMeshObjectBeingParsed.setTimeUpdated( Long.parseLong( timeUpdated ));
            }
            if( timeRead != null && timeRead.length() > 0 ) {
                theMeshObjectBeingParsed.setTimeRead( Long.parseLong( timeRead ));
            }
            if( timeExpires != null && timeExpires.length() > 0 ) {
                theMeshObjectBeingParsed.setTimeExpires( Long.parseLong( timeExpires ));
            } 
        } else if( TYPE_TAG.equals( qName )) {
            // no op
        } else if( PROPERTY_TYPE_TAG.equals( qName )) {
            String type = attrs.getValue( TYPE_TAG );

            if( type != null && type.length() > 0 ) {
                theMeshObjectBeingParsed.addPropertyType( theMeshTypeIdentifierFactory.fromExternalForm( descape( type )));
            } else {
                log.error( "empty '" + TYPE_TAG + "' on '" + PROPERTY_TYPE_TAG + "'" );
            }
        } else if( RELATIONSHIP_TAG.equals( qName )) {
            String identifier  = attrs.getValue( IDENTIFIER_TAG );
            long   updated     = parseLong( attrs, TIME_UPDATED_TAG, -1L );

            if( identifier != null ) {
                try {
                    theHasTypesBeingParsed = new ParserFriendlyExternalizedMeshObject.HasRoleTypes(
                            theMeshObjectBeingParsed.getIdentifier(),
                            theMeshObjectIdentifierFactory.fromExternalForm( descape( identifier )),
                            updated );
                } catch( URISyntaxException ex ) {
                    error( ex );
                }
            } else {
                log.error( "empty '" + IDENTIFIER_TAG + "' on '" + RELATIONSHIP_TAG + "'" );
            }

        } else if( EQUIVALENT_TAG.equals( qName )) {
            // no op
        } else {
            startElement2( namespaceURI, localName, qName, attrs );
        }
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     * @throws SAXException thrown if a parsing error occurrs
     */
    protected void startElement2(
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
     * @throws SAXException thrown if a parsing error occurrs
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
            // no op        
        } else if( TYPE_TAG.equals( qName )) {
            // first the "inner" element if present
            if( theHasTypesBeingParsed != null ) {
                theHasTypesBeingParsed.addType(
                        theMeshTypeIdentifierFactory.fromExternalForm( theCharacters.toString() ) );
            } else if( theMeshObjectBeingParsed != null ) {
                theMeshObjectBeingParsed.addMeshType(
                        theMeshTypeIdentifierFactory.fromExternalForm( theCharacters.toString() ) );
            } else {
                log.error( "neither found" );
            }
        
        } else if( PROPERTY_TYPE_TAG.equals( qName )) {
            theMeshObjectBeingParsed.addPropertyValue( thePropertyValue );

        } else if( RELATIONSHIP_TAG.equals( qName )) {
            theMeshObjectBeingParsed.addRelationship( (ParserFriendlyExternalizedMeshObject.HasRoleTypes) theHasTypesBeingParsed );
            theHasTypesBeingParsed = null;

        } else if( EQUIVALENT_TAG.equals( qName )) {
            if( theCharacters != null ) {
                try {
                    theMeshObjectBeingParsed.addEquivalent(
                            theMeshObjectIdentifierFactory.fromExternalForm( theCharacters.toString() ));
                } catch( URISyntaxException ex ) {
                    error( ex );
                }
            }
            
        } else {
            endElement2( namespaceURI, localName, qName );
        }
    }

    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @throws SAXException thrown if a parsing error occurrs
     */
    protected void endElement2(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        log.error( "unknown qname " + qName );
    }

    /**
     * Reset the parser.
     */
    @Override
    public void clearState()
    {
        theMeshObjectBeingParsed = null;
        
        super.clearState();
    }

    /**
     * The factory for ParserFriendlyExternalizedMeshObjects to use.
     */
    protected ParserFriendlyExternalizedMeshObjectFactory theExternalizedMeshObjectFactory;

    /**
     * The factory for MeshObjectIdentifiers.
     */
    protected MeshObjectIdentifierFactory theMeshObjectIdentifierFactory;
    
    /**
     * The factory for MeshTypeIdentifiers.
     */
    protected MeshTypeIdentifierFactory theMeshTypeIdentifierFactory;

    /**
     * The ExternalizedMeshObject that is currently being parsed, if any.
     */
    protected ParserFriendlyExternalizedMeshObject theMeshObjectBeingParsed = null;
    
    /**
     * The Relationship that is currently being parsed, if any.
     */
    protected ParserFriendlyExternalizedMeshObject.HasTypes theHasTypesBeingParsed = null;
}
