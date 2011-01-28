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

package org.infogrid.probe.xrd;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import org.infogrid.lid.model.xrd.XrdSubjectArea;
import org.infogrid.mesh.BlessedAlreadyException;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Web.WebSubjectArea;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.primitives.TimeStampValue;
import org.infogrid.module.ModuleException;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.xml.XmlDOMProbe;
import org.infogrid.util.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author jernst
 */
public class XrdProbe
        implements
            XmlDOMProbe,
            XrdXmlConstants
{
    private static final Log log = Log.getLogInstance( XrdProbe.class ); // our own, private logger

    /**
     * {@inheritDoc}
     */
    public void parseDocument(
            NetMeshBaseIdentifier  dataSourceIdentifier,
            CoherenceSpecification coherenceSpecification,
            byte []                documentBytes,
            String                 documentMime,
            Document               theDocument,
            StagingMeshBase        freshMeshBase )
        throws
            EntityBlessedAlreadyException,
            EntityNotBlessedException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            IOException,
            IsAbstractException,
            MeshObjectIdentifierNotUniqueException,
            ModuleException,
            NotPermittedException,
            NotRelatedException,
            ProbeException,
            RelatedAlreadyException,
            RoleTypeBlessedAlreadyException,
            TransactionException,
            URISyntaxException,
            ParseException
    {
        NetMeshObject home = freshMeshBase.getHomeObject();

        try {
            home.bless( XrdSubjectArea.XRD );

        } catch( BlessedAlreadyException ex ) {
            log.warn( ex );
        } catch( IsAbstractException ex ) {
            log.error( ex );
            return;
        }

        int nodeCounter = 0;

        NodeList rootList = theDocument.getChildNodes();
        for( int h=0 ; h<rootList.getLength() ; ++h ) {
            Node rootNode = rootList.item( h );
            if( !XRD_XML_NAMESPACE.equals( rootNode.getNamespaceURI()) || !"XRD".equals( rootNode.getLocalName() )) {
                continue;
            }
            NodeList mainNodeList = rootNode.getChildNodes();
            for( int i=0 ; i<mainNodeList.getLength() ; ++i ) {
                Node mainNode = mainNodeList.item( i );
                if( !XRD_XML_NAMESPACE.equals( mainNode.getNamespaceURI() )) {
                    continue;
                }

                String elementName = mainNode.getLocalName();

                if( EXPIRES.equals( elementName )) {
                    handleExpiresNode( mainNode, ++nodeCounter, home );

                } else if( SUBJECT.equals( elementName )) {
                    handleSubjectNode( mainNode, ++nodeCounter, home );

                } else if( ALIAS.equals( elementName )) {
                    handleAliasNode( mainNode, ++nodeCounter, home );

                } else if( PROPERTY.equals( elementName )) {
                    handlePropertyNode( mainNode, ++nodeCounter, home );

                } else if( LINK.equals( elementName )) {
                    handleLinkNode( mainNode, ++nodeCounter, home );

                } else {
                   log.error( "Unexpected element", elementName, mainNode );
                }
            }
        }
    }

    /**
     * Handle a discovered Exoires Node.
     *
     * @param current the current Node
     * @param index unique index that can be used to generate identifiers
     * @param home the Probe's home object
     */
    protected void handleExpiresNode(
            Node          current,
            int           index,
            NetMeshObject home )
        throws
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            NotPermittedException,
            TransactionException,
            ParseException
    {
        String text = getTextContentOf( current );
        home.setPropertyValue( XrdSubjectArea.XRD_EXPIRES, TimeStampValue.createFromW3c( text ));
    }

    /**
     * Handle a discovered Subject Node.
     *
     * @param current the current Node
     * @param index unique index that can be used to generate identifiers
     * @param home the Probe's home object
     */
    protected void handleSubjectNode(
            Node          current,
            int           index,
            NetMeshObject home )
        throws
            EntityNotBlessedException,
            RelatedAlreadyException,
            IsAbstractException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            NotPermittedException,
            TransactionException,
            ParseException
    {
        NetMeshBase base = home.getMeshBase();
        String      text = getTextContentOf( current );

        NetMeshObjectIdentifier subjectIdentifier = base.getMeshObjectIdentifierFactory().guessFromExternalForm( text );

        NetMeshObject subject = findOrCreateForwardReferenceAndBless(
                subjectIdentifier,
                WebSubjectArea.WEBRESOURCE,
                (StagingMeshBase) base );

        home.relateAndBless( XrdSubjectArea.XRD_ABOUTPRIMARY_WEBRESOURCE.getSource(), subject );
    }

    /**
     * Handle a discovered Alias Node.
     *
     * @param current the current Node
     * @param index unique index that can be used to generate identifiers
     * @param home the Probe's home object
     */
    protected void handleAliasNode(
            Node          current,
            int           index,
            NetMeshObject home )
        throws
            EntityNotBlessedException,
            RelatedAlreadyException,
            IsAbstractException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            NotPermittedException,
            TransactionException,
            ParseException
    {
        NetMeshBase base = home.getMeshBase();
        String      text = getTextContentOf( current );

        NetMeshObjectIdentifier aliasIdentifier = base.getMeshObjectIdentifierFactory().guessFromExternalForm( text );

        NetMeshObject alias = findOrCreateForwardReferenceAndBless(
                aliasIdentifier,
                WebSubjectArea.WEBRESOURCE,
                (StagingMeshBase) base );

        home.relateAndBless( XrdSubjectArea.XRD_ABOUTALIAS_WEBRESOURCE.getSource(), alias );
    }

    /**
     * Handle a discovered Property Node.
     *
     * @param current the current Node
     * @param index unique index that can be used to generate identifiers
     * @param home the Probe's home object
     */
    protected void handlePropertyNode(
            Node          current,
            int           index,
            NetMeshObject home )
    {
        // currently we don't do anything about them. The plan is to instantiate subclasses of Xrd,
        // or something like that, and set properties on them.
    }

    /**
     * Handle a discovered Link Node.
     *
     * @param current the current Node
     * @param index unique index that can be used to generate identifiers
     * @param home the Probe's home object
     */
    protected void handleLinkNode(
            Node          current,
            int           index,
            NetMeshObject home )
        throws
            MeshObjectIdentifierNotUniqueException,
            EntityNotBlessedException,
            RelatedAlreadyException,
            IsAbstractException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            NotPermittedException,
            TransactionException,
            ParseException,
            ProbeException
    {
        NetMeshBase                    base   = home.getMeshBase();
        NetMeshBaseLifecycleManager    life   = base.getMeshBaseLifecycleManager();
        NetMeshObjectIdentifierFactory idFact = base.getMeshObjectIdentifierFactory();

        NamedNodeMap attributes = current.getAttributes();

        Node relNode      = attributes.getNamedItemNS( XRD_XML_NAMESPACE, REL );
        Node typeNode     = attributes.getNamedItemNS( XRD_XML_NAMESPACE, TYPE );
        Node hrefNode     = attributes.getNamedItemNS( XRD_XML_NAMESPACE, HREF );
        Node templateNode = attributes.getNamedItemNS( XRD_XML_NAMESPACE, TEMPLATE );

        if( relNode == null ) {
            relNode = attributes.getNamedItem( REL );
        }
        if( typeNode == null ) {
            typeNode = attributes.getNamedItem( TYPE );
        }
        if( hrefNode == null ) {
            hrefNode = attributes.getNamedItem( HREF );
        }
        if( templateNode == null ) {
            templateNode = attributes.getNamedItem( TEMPLATE );
        }

        NetMeshObject link = life.createMeshObject(
                idFact.guessFromExternalForm( "link-" + index ),
                templateNode != null ? XrdSubjectArea.LINKTEMPLATE : XrdSubjectArea.LINK );

        if( relNode != null ) {
            link.setPropertyValue( XrdSubjectArea.ABSTRACTLINK_REL, StringValue.create( getTextContentOf( relNode )));
        }
        if( typeNode != null ) {
            link.setPropertyValue( XrdSubjectArea.ABSTRACTLINK_TYPE, StringValue.create( getTextContentOf( typeNode )));
        }

        if( templateNode != null ) {
            link.setPropertyValue( XrdSubjectArea.LINKTEMPLATE_TEMPLATE, StringValue.create( getTextContentOf( templateNode )));

        } else if( hrefNode != null ) {
            NetMeshObjectIdentifier destIdentifier = idFact.guessFromExternalForm( getTextContentOf( hrefNode ));

            NetMeshObject dest = findOrCreateForwardReferenceAndBless(
                    destIdentifier,
                    WebSubjectArea.WEBRESOURCE,
                    (StagingMeshBase) base );

            link.relateAndBless( XrdSubjectArea.LINK_TO_WEBRESOURCE.getSource(), dest );

        } else {
            throw new ProbeException.SyntaxError( base.getIdentifier(), "Node of type " + LINK + "must have either href or template attribute" );
        }

        home.relateAndBless( XrdSubjectArea.XRD_CONTAINS_ABSTRACTLINK.getSource(), link );
    }

    /**
     * Helper method to get the trimmed text content of a node.
     *
     * @param current the Node
     * @return the text content
     */
    protected String getTextContentOf(
            Node current )
    {
        NodeList childList = current.getChildNodes();

        StringBuilder found     = new StringBuilder();
        for( int l=0 ; l<childList.getLength() ; ++l ) {
            Node child = childList.item( l );
            if( child instanceof Text ) {
                found.append( ((Text)child).getData() );
            }
        }
        return found.toString().trim();
    }

    /**
     * Find a ForwardReference MeshObject with a certain identifier; if not found, instantiate
     * a new one and bless it with an EntityType.
     *
     * @param identifier the identifier of the MeshObject to be found or created
     * @param type the EntityType with which to bless a newly created MeshObject
     * @param base the MeshBase to use
     * @return the found or newly created MeshObject
     * @throws TransactionException thrown if invoked outside of proper Transaction boundaries. This should not happen.
     * @throws NotPermittedException an operation was not permitted. This should not happen.
     */
    protected NetMeshObject findOrCreateForwardReferenceAndBless(
            NetMeshObjectIdentifier identifier,
            EntityType              type,
            StagingMeshBase         base )
        throws
            TransactionException,
            NotPermittedException
    {
        NetMeshObject ret = base.findMeshObjectByIdentifier( identifier );
        try {
            if( ret == null ) {
                ret = base.getMeshBaseLifecycleManager().createForwardReference( identifier.getNetMeshBaseIdentifier(), identifier );
            }

            ret.bless( type );
        } catch( BlessedAlreadyException ex ) {
            // ignore
        } catch( MeshObjectIdentifierNotUniqueException ex ) {
            log.error( ex );
        } catch( IsAbstractException ex ) {
            log.error( ex );
        }
        return ret;
    }
}
