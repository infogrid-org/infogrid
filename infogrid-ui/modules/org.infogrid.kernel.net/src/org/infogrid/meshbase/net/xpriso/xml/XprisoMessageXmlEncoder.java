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

package org.infogrid.meshbase.net.xpriso.xml;

import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObjectFactory;
import org.infogrid.mesh.net.externalized.xml.ExternalizedNetMeshObjectXmlEncoder;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.net.xpriso.ParserFriendlyXprisoMessage;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.externalized.DecodingException;
import org.infogrid.model.primitives.externalized.EncodingException;

import org.infogrid.modelbase.MeshTypeIdentifierFactory;

import org.infogrid.util.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;

/**
 * Encodes and decodes XprisoMessages in default XML format.
 */
public class XprisoMessageXmlEncoder
        extends
            ExternalizedNetMeshObjectXmlEncoder
        implements
            XprisoMessageXmlTags
{
    private static final Log log = Log.getLogInstance( XprisoMessageXmlEncoder.class ); // our own, private logger

    /**
     * Constructor.
     */
    public XprisoMessageXmlEncoder()
    {
    }

    /**
     * Serialize an XprisoMessage to an OutputStream.
     * 
     * 
     * @param msg the input XprisoMessage
     * @param out the OutputStream to which to append the XprisoMessage
     * @throws EncodingException thrown if a problem occurred during encoding
     */
    public void encodeXprisoMessage(
            XprisoMessage msg,
            OutputStream  out )
        throws
            EncodingException,
            IOException
    {
        StringBuilder buf = new StringBuilder();

        appendXprisoMessage( msg, buf );

        OutputStreamWriter w = new OutputStreamWriter( out, ENCODING );
        w.write( buf.toString() );
        w.flush();
    }

    /**
     * Encode an XprisoMessage.
     * 
     * @param msg the input XprisoMessage
     * @param buf the StringBuilder to append to
     */
    public void appendXprisoMessage(
            XprisoMessage msg,
            StringBuilder buf )
        throws
            EncodingException
    {
        buf.append( "<" ).append( MESSAGE_TAG );
        buf.append( " " ).append( REQUEST_ID_TAG  ).append( "=\"" ).append( msg.getRequestId() ).append( "\"" );
        buf.append( " " ).append( RESPONSE_ID_TAG ).append( "=\"" ).append( msg.getResponseId() ).append( "\"" );
        if( msg.getSenderIdentifier() != null ) {
            buf.append( " " ).append( SENDER_ID_TAG ).append( "=\"" );
            appendNetworkIdentifier( msg.getSenderIdentifier(), buf );
            buf.append( "\"" );
        }
        if( msg.getReceiverIdentifier() != null ) {
            buf.append( " " ).append( RECEIVER_ID_TAG ).append( "=\"" );
            appendNetworkIdentifier( msg.getReceiverIdentifier(), buf );
            buf.append( "\"" );
        }
        buf.append( ">\n" );

        NetMeshObjectAccessSpecification [] requestedFirstTimeObjects = msg.getRequestedFirstTimeObjects();
        if( requestedFirstTimeObjects != null ) {
            for( NetMeshObjectAccessSpecification current : requestedFirstTimeObjects ) {
                buf.append( "    <" ).append( REQUESTED_FIRST_TIME_OBJECT_TAG );
                buf.append( " " ).append( NETWORK_PATH_TAG ).append( "=\"" );
                appendNetworkPath( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        MeshObjectIdentifier [] requestedCanceledObjects = msg.getRequestedCanceledObjects();
        if( requestedCanceledObjects != null ) {
            for( MeshObjectIdentifier current : requestedCanceledObjects ) {
                buf.append( "    <" ).append( REQUESTED_CANCELED_OBJECT_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        NetMeshObjectDeletedEvent [] deleteChanges = msg.getDeleteChanges();
        if( deleteChanges != null ) {
            for( NetMeshObjectDeletedEvent current : deleteChanges ) {
                buf.append( "    <" ).append( MESH_OBJECT_DELETED_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\"/>\n" );
            }
        }
        NetMeshObjectCreatedEvent [] creations = msg.getCreations();
        if( creations != null ) {
            for( NetMeshObjectCreatedEvent current : creations ) {
                appendExternalizedMeshObject( current.getExternalizedMeshObject(), MESH_OBJECT_CREATED_TAG, buf );
            }
        }
        ExternalizedNetMeshObject [] conveyedMeshObjects = msg.getConveyedMeshObjects();
        if( conveyedMeshObjects != null ) {
            for( ExternalizedNetMeshObject current : conveyedMeshObjects ) {
                appendExternalizedMeshObject( current, CONVEYED_MESH_OBJECT_TAG, buf );
            }
        }
        NetMeshObjectNeighborAddedEvent [] neighborAdditions = msg.getNeighborAdditions();
        if( neighborAdditions != null ) {
            for( NetMeshObjectNeighborAddedEvent current : neighborAdditions ) {
                buf.append( "    <" ).append( NEIGHBOR_ADDITION_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( NEIGHBOR_TAG ).append( "=\"" );
                appendIdentifier( current.getNeighborMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">\n" );
                if( current.getAffectedRoleTypeIdentifiers() != null ) {
                    for( MeshTypeIdentifier type : current.getAffectedRoleTypeIdentifiers() ) {
                        buf.append( "      <" ).append( TYPE_TAG ).append( ">" );
                        appendIdentifier( type, buf );
                        buf.append( "</" ).append( TYPE_TAG ).append( ">\n" );
                    }
                }
                buf.append( "    </" ).append( NEIGHBOR_ADDITION_TAG ).append( ">\n" );
            }
        }
        NetMeshObjectNeighborRemovedEvent [] neighborRemovals = msg.getNeighborRemovals();
        if( neighborRemovals != null ) {
            for( NetMeshObjectNeighborRemovedEvent current : neighborRemovals ) {
                buf.append( "    <" ).append( NEIGHBOR_REMOVAL_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( NEIGHBOR_TAG ).append( "=\"" );
                appendIdentifier( current.getNeighborMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\"/>\n" );
            }
        }
        NetMeshObjectPropertyChangeEvent [] propertyChanges = msg.getPropertyChanges();
        if( propertyChanges != null ) {
            for( NetMeshObjectPropertyChangeEvent current : propertyChanges ) {
                buf.append( "    <" ).append( PROPERTY_CHANGE_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TYPE_TAG ).append( "=\"" );
                appendIdentifier( current.getPropertyTypeIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">" );
                appendPropertyValue( current.getDeltaValue(), buf );
                buf.append( "</" ).append( PROPERTY_CHANGE_TAG ).append( ">\n" );
            }
        }
        NetMeshObjectRoleAddedEvent [] roleAdditions = msg.getRoleAdditions();
        if( roleAdditions != null ) {
            for( NetMeshObjectRoleAddedEvent current : roleAdditions ) {
                buf.append( "    <" ).append( ROLE_ADDITION_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( NEIGHBOR_TAG ).append( "=\"" );
                appendIdentifier( current.getNeighborMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">\n" );
                for( MeshTypeIdentifier type : current.getAffectedRoleTypeIdentifiers() ) {
                    buf.append( "      <" ).append( TYPE_TAG ).append( ">" );
                    appendIdentifier( type, buf );
                    buf.append( "</" ).append( TYPE_TAG ).append( ">\n" );
                }
                buf.append( "    </" ).append( ROLE_ADDITION_TAG ).append( ">\n" );
            }
        }
        NetMeshObjectRoleRemovedEvent [] roleRemovals = msg.getRoleRemovals();
        if( roleAdditions != null ) {
            for( NetMeshObjectRoleRemovedEvent current : roleRemovals ) {
                buf.append( "    <" ).append( ROLE_REMOVAL_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( NEIGHBOR_TAG ).append( "=\"" );
                appendIdentifier( current.getNeighborMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">\n" );
                for( MeshTypeIdentifier type : current.getAffectedRoleTypeIdentifiers() ) {
                    buf.append( "      <" ).append( TYPE_TAG ).append( ">" );
                    appendIdentifier( type, buf );
                    buf.append( "</" ).append( TYPE_TAG ).append( ">\n" );
                }                
                buf.append( "    </" ).append( ROLE_REMOVAL_TAG ).append( ">\n" );
            }
        }
        NetMeshObjectTypeAddedEvent [] typeAdditions = msg.getTypeAdditions();
        if( typeAdditions != null ) {
            for( NetMeshObjectTypeAddedEvent current : typeAdditions ) {
                buf.append( "    <" ).append( TYPE_ADDITION_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">\n" );
                for( MeshTypeIdentifier type : current.getEntityTypeIdentifiers() ) {
                    buf.append( "      <" ).append( TYPE_TAG ).append( ">" );
                    appendIdentifier( type, buf );
                    buf.append( "</" ).append( TYPE_TAG ).append( ">\n" );
                }                
                buf.append( "    </" ).append( TYPE_ADDITION_TAG ).append( ">\n" );
            }
        }
        NetMeshObjectTypeRemovedEvent [] typeRemovals = msg.getTypeRemovals();
        if( typeRemovals != null ) {
            for( NetMeshObjectTypeRemovedEvent current : typeRemovals ) {
                buf.append( "    <" ).append( TYPE_REMOVAL_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current.getAffectedMeshObjectIdentifier(), buf );
                buf.append( "\" " ).append( TIME_UPDATED_TAG ).append( "=\"" );
                appendLong( current.getTimeEventOccurred(), buf );
                buf.append( "\">\n" );
                for( MeshTypeIdentifier type : current.getEntityTypeIdentifiers() ) {
                    buf.append( "      <" ).append( TYPE_TAG ).append( ">" );
                    appendIdentifier( type, buf );
                    buf.append( "</" ).append( TYPE_TAG ).append( ">\n" );
                }                
                buf.append( "    </" ).append( TYPE_REMOVAL_TAG ).append( ">\n" );
            }
        }
        MeshObjectIdentifier [] requestedLockObjects = msg.getRequestedLockObjects();
        if( requestedLockObjects != null ) {
            for( MeshObjectIdentifier current : requestedLockObjects ) {
                buf.append( "    <" ).append( REQUESTED_LOCK_OBJECT_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        MeshObjectIdentifier [] pushLockObjects = msg.getPushLockObjects();
        if( pushLockObjects != null ) {
            for( MeshObjectIdentifier current : pushLockObjects ) {
                buf.append( "    <" ).append( PUSH_LOCK_OBJECT_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        MeshObjectIdentifier [] reclaimedLockObjects = msg.getReclaimedLockObjects();
        if( pushLockObjects != null ) {
            for( MeshObjectIdentifier current : reclaimedLockObjects ) {
                buf.append( "    <" ).append( RECLAIMED_LOCK_OBJECT_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        MeshObjectIdentifier [] requestedResynchronizeDependentReplicas = msg.getRequestedResynchronizeDependentReplicas();
        if( requestedResynchronizeDependentReplicas != null ) {
            for( MeshObjectIdentifier current : requestedResynchronizeDependentReplicas ) {
                buf.append( "    <" ).append( REQUESTED_RESYNCHRONIZE_DEPENDENT_REPLICA_TAG );
                buf.append( " " ).append( IDENTIFIER_TAG ).append( "=\"" );
                appendIdentifier( current, buf );
                buf.append( "\"/>\n" );
            }
        }
        ExternalizedNetMeshObject [] resynchronizedDependentReplicas = msg.getResynchronizeDependentReplicas();
        if( resynchronizedDependentReplicas != null ) {
            for( ExternalizedNetMeshObject current : resynchronizedDependentReplicas ) {
                appendExternalizedMeshObject( current, RESYNCHRONIZED_DEPENDENT_REPLICA_TAG, buf );
            }
        }
        if( msg.getCeaseCommunications() ) {
            buf.append( "    <" ).append( CEASE_COMMUNICATIONS_TAG ).append( "/>\n" );
        }
        buf.append( "   </" ).append( MESSAGE_TAG ).append( ">\n" );        
    }

    /**
     * Deserialize an XprisoMessage from a byte stream.
     *
     * @param s the InputStream from which to read
     * @param life the MeshBaseLifecycleManager appropriate to create an appropriate ExternalizedMeshObject
     * @return return the found XprisoMessage
     * @throws DecodingException thrown if a problem occurred during decoding
     */
    public synchronized XprisoMessage decodeXprisoMessage(
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
            return theMessage;

        } catch( SAXException ex ) {
            throw new DecodingException( ex );

        } finally {
            clearState();
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
    @Override
    protected void startElement3(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( MESSAGE_TAG.equals( qName )) {
            long requestId  = parseLong( attrs, REQUEST_ID_TAG,  -1L );
            long responseId = parseLong( attrs, RESPONSE_ID_TAG, -1L );
            NetMeshBaseIdentifier senderId   = null;
            NetMeshBaseIdentifier receiverId = null;
            
            try {
                String sender = attrs.getValue( SENDER_ID_TAG );
                if( sender != null && sender.length() > 0 ) {
                    senderId = NetMeshBaseIdentifier.fromExternalForm( sender );
                }
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            try {
                String receiver = attrs.getValue( RECEIVER_ID_TAG );
                if( receiver != null && receiver.length() > 0 ) {
                    receiverId = NetMeshBaseIdentifier.fromExternalForm( receiver );
                }
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }

            theMessage = ParserFriendlyXprisoMessage.create(
                    requestId,
                    responseId,
                    senderId,
                    receiverId );
            
        } else if( REQUESTED_FIRST_TIME_OBJECT_TAG.equals( qName )) {
            String pathString = attrs.getValue( NETWORK_PATH_TAG );
            try {
                NetMeshObjectAccessSpecification path = 
                        ((NetMeshObjectIdentifierFactory)theMeshObjectIdentifierFactory).createNetMeshObjectAccessSpecificationFromExternalForm( pathString );
                theMessage.addRequestedFirstTimeObject( path );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( REQUESTED_CANCELED_OBJECT_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                theMessage.addRequestedCanceledObject( ref );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }

        } else if( MESH_OBJECT_DELETED_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref  = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                long                 time = parseLong( attrs, TIME_UPDATED_TAG, -1L );
                theMessage.addDeleteChange( new NetMeshObjectDeletedEvent( null, theMessage.getSenderIdentifier(), null, ref, theMessage.getSenderIdentifier(), time ));
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if(    MESH_OBJECT_CREATED_TAG.equals( qName )
                   || CONVEYED_MESH_OBJECT_TAG.equals( qName )
                   || RESYNCHRONIZED_DEPENDENT_REPLICA_TAG.equals( qName ))
        {
            theMeshObjectBeingParsed = theExternalizedMeshObjectFactory.createParserFriendlyExternalizedMeshObject();
            ParserFriendlyExternalizedNetMeshObject realObjectBeingParsed = (ParserFriendlyExternalizedNetMeshObject) theMeshObjectBeingParsed;

            String identifier      = attrs.getValue( IDENTIFIER_TAG );
            String timeCreated     = attrs.getValue( TIME_CREATED_TAG );
            String timeUpdated     = attrs.getValue( TIME_UPDATED_TAG );
            String timeRead        = attrs.getValue( TIME_READ_TAG );
            String timeExpires     = attrs.getValue( TIME_EXPIRES_TAG );
            String giveUpLock      = attrs.getValue( GIVE_UP_LOCK_TAG );

            if( identifier != null ) {
                try {
                    theMeshObjectBeingParsed.setIdentifier( theMeshObjectIdentifierFactory.fromExternalForm( descape( identifier )));
                } catch( URISyntaxException ex ) {
                    log.warn( ex );
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
            if( YES_TAG.equals( giveUpLock )) {
                realObjectBeingParsed.setGiveUpLock( true );
            }
            
        } else if(    NEIGHBOR_ADDITION_TAG.equals( qName )
                   || NEIGHBOR_REMOVAL_TAG.equals( qName )
                   || ROLE_ADDITION_TAG.equals( qName )
                   || ROLE_REMOVAL_TAG.equals( qName ))
        {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                MeshObjectIdentifier neighbor = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( NEIGHBOR_TAG ));
                long           updated  = parseLong( attrs, TIME_UPDATED_TAG, -1L );
                theHasTypesBeingParsed  = new ParserFriendlyExternalizedMeshObject.HasRoleTypes( ref, neighbor, updated );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( PROPERTY_CHANGE_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                MeshTypeIdentifier   type     = theMeshTypeIdentifierFactory.fromExternalForm( attrs.getValue( TYPE_TAG ));
                long           updated  = parseLong( attrs, TIME_UPDATED_TAG, -1L );
                theHasPropertiesBeingParsed = new ParserFriendlyExternalizedMeshObject.HasProperties( ref, type, updated );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }

        } else if( TYPE_ADDITION_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                long           updated = parseLong( attrs, TIME_UPDATED_TAG, -1L );
                theHasTypesBeingParsed = new ParserFriendlyExternalizedMeshObject.HasTypes( ref, updated );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( TYPE_REMOVAL_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                long           updated = parseLong( attrs, TIME_UPDATED_TAG, -1L );
                theHasTypesBeingParsed = new ParserFriendlyExternalizedMeshObject.HasTypes( ref, updated );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( REQUESTED_LOCK_OBJECT_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                theMessage.addRequestedLockObjects( ref );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( PUSH_LOCK_OBJECT_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                theMessage.addPushLockObject( ref );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( RECLAIMED_LOCK_OBJECT_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                theMessage.addReclaimedLockObject( ref );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( REQUESTED_RESYNCHRONIZE_DEPENDENT_REPLICA_TAG.equals( qName )) {
            try {
                MeshObjectIdentifier ref      = theMeshObjectIdentifierFactory.fromExternalForm( attrs.getValue( IDENTIFIER_TAG ));
                theMessage.addRequestedResynchronizeDependentReplica( ref );
            } catch( URISyntaxException ex ) {
                log.warn( ex );
            }
            
        } else if( CEASE_COMMUNICATIONS_TAG.equals( qName )) {
            theMessage.setCeaseCommunications( true );

        } else {
            startElement4( namespaceURI, localName, qName, attrs );
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
    protected void startElement4(
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
     * Allows subclasses to add to parsing.
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
            // no op

        } else if( REQUESTED_FIRST_TIME_OBJECT_TAG.equals( qName )) {
            // no op

        } else if( REQUESTED_CANCELED_OBJECT_TAG.equals( qName )) {
            // no op

        } else if( MESH_OBJECT_DELETED_TAG.equals( qName )) {
            
        } else if( MESH_OBJECT_CREATED_TAG.equals( qName )) {
            theMessage.addCreation( new NetMeshObjectCreatedEvent(
                    null,
                    theMessage.getSenderIdentifier(),
                    (ExternalizedNetMeshObject) theMeshObjectBeingParsed,
                    theMessage.getSenderIdentifier() ));
            
        } else if( CONVEYED_MESH_OBJECT_TAG.equals( qName )) {
            theMessage.addConveyedMeshObject( (ExternalizedNetMeshObject) theMeshObjectBeingParsed );
            theMeshObjectBeingParsed = null;
            
        } else if( RESYNCHRONIZED_DEPENDENT_REPLICA_TAG.equals( qName )) {
            theMessage.addResynchronizeDependentReplica( (ExternalizedNetMeshObject) theMeshObjectBeingParsed );
            theMeshObjectBeingParsed = null;
            
        } else if( NEIGHBOR_ADDITION_TAG.equals( qName )) {
            theMessage.addNeighborAddition( new NetMeshObjectNeighborAddedEvent(
                    (NetMeshObjectIdentifier) theHasTypesBeingParsed.getIdentifier(),
                    theHasTypesBeingParsed.getTypes(),
                    (NetMeshObjectIdentifier) ((ParserFriendlyExternalizedNetMeshObject.HasRoleTypes)theHasTypesBeingParsed).getNeighborIdentifier(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));

            theHasTypesBeingParsed = null;
            
        } else if( NEIGHBOR_REMOVAL_TAG.equals( qName )) {
            theMessage.addNeighborRemoval( new NetMeshObjectNeighborRemovedEvent(
                    (NetMeshObjectIdentifier) theHasTypesBeingParsed.getIdentifier(),
                    (NetMeshObjectIdentifier) ((ParserFriendlyExternalizedNetMeshObject.HasRoleTypes)theHasTypesBeingParsed).getNeighborIdentifier(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));
            
            theHasTypesBeingParsed = null;
            
        } else if( ROLE_ADDITION_TAG.equals( qName )) {
            theMessage.addRoleAdditions( new NetMeshObjectRoleAddedEvent(
                    (NetMeshObjectIdentifier) theHasTypesBeingParsed.getIdentifier(),
                    theHasTypesBeingParsed.getTypes(),
                    (NetMeshObjectIdentifier) ((ParserFriendlyExternalizedNetMeshObject.HasRoleTypes)theHasTypesBeingParsed).getNeighborIdentifier(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));

            theHasTypesBeingParsed = null;
            
        } else if( ROLE_REMOVAL_TAG.equals( qName )) {
            theMessage.addRoleRemoval( new NetMeshObjectRoleRemovedEvent(
                    (NetMeshObjectIdentifier) theHasTypesBeingParsed.getIdentifier(),
                    theHasTypesBeingParsed.getTypes(),
                    (NetMeshObjectIdentifier) ((ParserFriendlyExternalizedNetMeshObject.HasRoleTypes)theHasTypesBeingParsed).getNeighborIdentifier(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));

            theHasTypesBeingParsed = null;
            
        } else if( PROPERTY_CHANGE_TAG.equals( qName )) {
            theMessage.addPropertyChange( new NetMeshObjectPropertyChangeEvent(
                    (NetMeshObjectIdentifier) theHasPropertiesBeingParsed.getIdentifier(),
                    theHasPropertiesBeingParsed.getPropertyTypeName(),
                    thePropertyValue,
                    theMessage.getSenderIdentifier(),
                    theHasPropertiesBeingParsed.getTimeUpdated() ));

            theHasPropertiesBeingParsed = null;
            
        } else if( TYPE_ADDITION_TAG.equals( qName )) {
            theMessage.addTypeAddition( new NetMeshObjectTypeAddedEvent(
                    (NetMeshObjectIdentifier) theHasTypesBeingParsed.getIdentifier(),
                    theHasTypesBeingParsed.getTypes(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));
            
        } else if( TYPE_REMOVAL_TAG.equals( qName )) {
            theMessage.addTypeRemoval( new NetMeshObjectTypeRemovedEvent(
                    theHasTypesBeingParsed.getIdentifier(),
                    theHasTypesBeingParsed.getTypes(),
                    theMessage.getSenderIdentifier(),
                    theHasTypesBeingParsed.getTimeUpdated() ));
            
        } else if( REQUESTED_LOCK_OBJECT_TAG.equals( qName )) {
            // no op

        } else if( PUSH_LOCK_OBJECT_TAG.equals( qName )) {
            // no op

        } else if( RECLAIMED_LOCK_OBJECT_TAG.equals( qName )) {
            // no op

        } else if( REQUESTED_RESYNCHRONIZE_DEPENDENT_REPLICA_TAG.equals( qName )) {
            // no op

        } else if( CEASE_COMMUNICATIONS_TAG.equals( qName )) {
            // no op

        } else {
            endElement4( namespaceURI, localName, qName );
        }
    }
    
    /**
     * Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     */
    protected void endElement4(
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
        super.clearState();
    }

    /**
     * The XprisoMessage currently being parsed.
     */
    protected ParserFriendlyXprisoMessage theMessage;
    
    /**
     * The PropertyChangeEvent currently being parsed.
     */
    protected ParserFriendlyExternalizedMeshObject.HasProperties theHasPropertiesBeingParsed;
}
