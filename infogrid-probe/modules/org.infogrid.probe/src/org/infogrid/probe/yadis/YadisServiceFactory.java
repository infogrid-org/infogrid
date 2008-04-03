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

package org.infogrid.probe.yadis;

import org.infogrid.mesh.BlessedAlreadyException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.IntegerValue;
import org.infogrid.model.primitives.MeshType;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.MeshTypeNotFoundException;

import org.infogrid.lid.openid.Authentication1_0Service;
import org.infogrid.lid.yadis.Service;
import org.infogrid.lid.yadis.Site;
import org.infogrid.lid.yadis.YadisSubjectArea;

import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class knows how to instantiate YadisService, and subclasses of YadisService, given
 * the information provided in a service node in a Yadis Capability Document DOM.
 */
public class YadisServiceFactory
    implements
        XmlConstants
//        KnownYadisServiceTypes
{
    private static final Log log = Log.getLogInstance( YadisServiceFactory.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param settings the Settings object
     */
    public YadisServiceFactory(
            DocumentBuilder builder )
    {
        theDocumentBuilder = builder;
    }

    /**
     * Instantiate the Services from a Yadis capability document.
     *
     * @param url the identity URL
     * @param yadisCapabilityFile the content of the Yadis document
     * @param fact the factory from which to instantiate the Service objects
     */
    public void addYadisServicesFromXml(
            NetMeshBaseIdentifier sourceIdentifier,
            String            yadisCapabilityFile,
            MeshBase          base )
    {
        try {
            InputSource     source  = new InputSource( new StringReader( yadisCapabilityFile ));
            Document        dom     = theDocumentBuilder.parse( source );

            createAllServices( sourceIdentifier, dom, base.getHomeObject(), base );

        } catch( Exception ex ) {
            log.warn( ex );
        }
    }
    
    /**
     * Create all the services that are defined in this YADIS document.
     *
     * @param url the URL that is described by this YADIS document
     * @param dom the YADIS document's DOM
     * @return all found Services, in the sequence in which they should be tried per the priority attributes
     */
    public void createAllServices(
            NetMeshBaseIdentifier sourceIdentifier,
            Document          dom,
            MeshObject        subject,
            MeshBase          base )
        throws
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException,
            URISyntaxException
    {
        try {
            subject.bless( YadisSubjectArea.SITE );

        } catch( BlessedAlreadyException ex ) {
            log.warn( ex );
        }

        NodeList rootList = dom.getChildNodes();
        for( int h=0 ; h<rootList.getLength() ; ++h ) {
            Node rootNode = rootList.item( h );
            if( !XRDS_XML_NAMESPACE.equals( rootNode.getNamespaceURI()) || !"XRDS".equals( rootNode.getLocalName() )) {
                continue;
            }
            NodeList xrdList     = rootNode.getChildNodes();
            Node     lastXrdNode = null;
            for( int i=0 ; i<xrdList.getLength() ; ++i ) {
                Node xrdNode = xrdList.item( i );
                if( !XRD_XML_NAMESPACE.equals( xrdNode.getNamespaceURI()) || !"XRD".equals( xrdNode.getLocalName() )) {
                    continue;
                }
                lastXrdNode = xrdNode;
            }
            if( lastXrdNode != null ) {
                NodeList serviceList = lastXrdNode.getChildNodes();
                int serviceCount = 0;
                for( int j=0 ; j<serviceList.getLength() ; ++j ) {
                    Node serviceNode = serviceList.item( j );
                    if( !XRD_XML_NAMESPACE.equals( serviceNode.getNamespaceURI()) || !"Service".equals( serviceNode.getLocalName() )) {
                        continue;
                    }
                    
                    String prefix = "YadisService-" + String.valueOf( serviceCount++ );
                    MeshObject serviceMeshObject = base.getMeshBaseLifecycleManager().createMeshObject(
                            base.getMeshObjectIdentifierFactory().fromExternalForm( prefix ),
                            // MeshObjectIdentifier.create( sourceIdentifier.toExternalForm(), prefix ),
                            Service._TYPE );

                    createService( sourceIdentifier, (Element) serviceNode, serviceMeshObject, prefix, base );
                    
                    try {
                        serviceMeshObject.relate( subject );
                    } catch( RelatedAlreadyException ex ) {
                        // ignore
                    }
                    try {
                        serviceMeshObject.blessRelationship( Service._Site_MakesUseOf_Service_DESTINATION, subject );
                    } catch( RelatedAlreadyException ex ) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Factory method to instantiate the Services found at this serviceNode.
     *
     * @param url the URL that is described by this YADIS document
     * @param serviceNode the Element in the DOM that we are currently working on
     * @return list of Services found at this Element
     */
    public void createService(
            NetMeshBaseIdentifier sourceIdentifier,
            Element               serviceNode,
            MeshObject            serviceMeshObject,
            String                prefix,
            MeshBase              base )
        throws
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException,
            URISyntaxException
    {
        serviceMeshObject.setPropertyValue( Service.PRIORITY, decodePriorityValue( serviceNode ));

        NodeList infoList = serviceNode.getChildNodes();
        int endpointIndex = 0;
        for( int k=0 ; k<infoList.getLength() ; ++k ) {
            Node infoNode = infoList.item( k );
            if( XRD_XML_NAMESPACE.equals( infoNode.getNamespaceURI() ) && "Type".equals( infoNode.getLocalName() )) {
                NodeList     childList = infoNode.getChildNodes();
                StringBuffer found     = new StringBuffer();
                for( int l=0 ; l<childList.getLength() ; ++l ) {
                    Node child = childList.item( l );
                    if( child instanceof Text ) {
                        found.append( ((Text)child).getData() );
                    }
                }
                String realFound = found.toString().trim();
                if( realFound.indexOf( "://" ) > 0 ) {
                    
                    EntityType type = findEntityType( base.getModelBase(), realFound );
                    if( type != null ) {
                        serviceMeshObject.bless( type );
                    }

                }
            } else if( XRD_XML_NAMESPACE.equals( infoNode.getNamespaceURI() ) && "URI".equals( infoNode.getLocalName() )) {
                NodeList     childList = infoNode.getChildNodes();
                StringBuffer found     = new StringBuffer();
                for( int l=0 ; l<childList.getLength() ; ++l ) {
                    Node child = childList.item( l );
                    if( child instanceof Text ) {
                        found.append( ((Text)child).getData() );
                    }
                }

                String realFound = found.toString().trim();
                if( realFound.indexOf( "://" ) > 0 ) {
                    MeshObjectIdentifier endpointIdentifier = base.getMeshObjectIdentifierFactory().fromExternalForm( realFound );

                    MeshObject endpoint = findOrCreateAndBless( endpointIdentifier, YadisSubjectArea.SITE, base );
                    
                    // FIXME? endpoint.setPropertyValue( ServiceEndPoint.Priority_PROPERTYTYPE, decodePriorityValue( infoNode ));
                    // endpoint.setPropertyValue( ServiceEndPoint.URI_PROPERTYTYPE, StringValue.create( realFound ));
                    try {
                        serviceMeshObject.relate( endpoint );
                    } catch( RelatedAlreadyException ex ) {
                        // ignore
                    }
                    try {
                        serviceMeshObject.blessRelationship( Service._Service_IsProvidedAtEndpoint_Site_SOURCE, endpoint );
                    } catch( BlessedAlreadyException ex ) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Find a priority attribute on a node, and obtain the value as an IntegerValue.
     *
     * @param nodeWithPriority the XML node that holds the priority attribute
     * @return the corresponding IntegerValue
     */
    protected PropertyValue decodePriorityValue(
            Node nodeWithPriority )
    {
        Node priorityNode = nodeWithPriority.getAttributes().getNamedItemNS( XRD_XML_NAMESPACE, "priority" );

        if( priorityNode == null ) {
            priorityNode = nodeWithPriority.getAttributes().getNamedItem( "priority" );
            // seems like a good compromise? FIXME?
        }
        int priorityValue = Integer.MAX_VALUE;
        if( priorityNode != null ) {
            String tmp = ((Attr)priorityNode).getValue();
            try {
                priorityValue = Integer.parseInt( tmp );
            } catch( Exception ex ) {
                log.warn( ex );
            }
        }
        return IntegerValue.create( priorityValue );
    }

    /**
     * Find an EntityType that goes with the provided type tag in the XRDS file.
     */
    protected EntityType findEntityType(
            ModelBase modelBase,
            String    externalForm )
    {
        MeshTypeIdentifier identifier;
        MeshType ret;
        try {
            // first look in the mapping table
            identifier = theTypeMappingTable.get( externalForm );
            if( identifier != null ) {
                ret = modelBase.findMeshTypeByIdentifier( identifier );
                return (EntityType) ret;
            }
            
            // then try directly
            identifier = modelBase.getMeshTypeIdentifierFactory().fromExternalForm( externalForm );
            ret = modelBase.findMeshTypeByIdentifier( identifier );
            if( ret != null ) {
                return (EntityType) ret;
            }
        } catch( MeshTypeNotFoundException ex ) {
            // ignore
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
        }

        // finally try to strip of the beta version
        Matcher m = betaPattern.matcher( externalForm );
        if( !m.find() ) {
            return null;
        }
        externalForm = m.group( 1 );
        identifier = modelBase.getMeshTypeIdentifierFactory().fromExternalForm( externalForm );

        // try directly
        try {
            ret = modelBase.findMeshTypeByIdentifier( identifier );
            return (EntityType) ret;

        } catch( MeshTypeNotFoundException ex ) {
            // ignore
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
        }
        
        identifier = theTypeMappingTable.get( externalForm );
        if( identifier == null ) {
            return null;
        }

        try {
            ret = modelBase.findMeshTypeByIdentifier( identifier );
            return (EntityType) ret;

        } catch( MeshTypeNotFoundException ex ) {
            // ignore, we just don't know
            if( log.isInfoEnabled() ) {
                log.info( ex );
            }
        }
        return null;
    }

    protected MeshObject findOrCreateAndBless(
            MeshObjectIdentifier identifier,
            EntityType      type,
            MeshBase        base )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException,
            NotPermittedException
    {
        // FIXME: ForwardReference
        MeshObject ret = base.findMeshObjectByIdentifier( identifier );
        if( ret == null ) {
            ret = base.getMeshBaseLifecycleManager().createMeshObject( identifier );
        }
        try {
            ret.bless( type );
        } catch( BlessedAlreadyException ex ) {
            // ignore
        }
        return ret;
    }

    public void addYadisServicesFromHtml(
            NetMeshBaseIdentifier sourceIdentifier,
            String            content,
            MeshBase          base )
    {
        try {
            MeshObject subject = base.getHomeObject();
        
            addYadisServicesFromHtml( sourceIdentifier, content, subject, base );

        } catch( Exception ex ) {
            log.error( ex );
        }
    }
        
    public void addYadisServicesFromHtml(
            NetMeshBaseIdentifier sourceIdentifier,
            String                content,
            MeshObject            subject,
            MeshBase              base )
        throws
            TransactionException,
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException,
            URISyntaxException
    {
        Matcher startHeadMatcher       = startHeadPattern.matcher( content );
        Matcher endHeadMatcher         = endHeadPattern.matcher( content );
        Matcher yadisHttpEquivMatcher1 = yadisHttpEquivPattern1.matcher( content );
        Matcher yadisHttpEquivMatcher2 = yadisHttpEquivPattern2.matcher( content );
        Matcher yadisHttpEquivMatcher3 = yadisHttpEquivPattern3.matcher( content );
        Matcher yadisHttpEquivMatcher4 = yadisHttpEquivPattern4.matcher( content );

        if( startHeadMatcher.find() && endHeadMatcher.find()) {

            int startHeadStart    = startHeadMatcher.start( 1 );
            int endHeadStart      = endHeadMatcher.start( 1 );

            Matcher yadisHttpEquivMatcher = null;
            if( yadisHttpEquivMatcher == null && yadisHttpEquivMatcher1.find() ) {
                yadisHttpEquivMatcher = yadisHttpEquivMatcher1;
            }
            if( yadisHttpEquivMatcher == null && yadisHttpEquivMatcher2.find() ) {
                yadisHttpEquivMatcher = yadisHttpEquivMatcher2;
            }
            if( yadisHttpEquivMatcher == null && yadisHttpEquivMatcher3.find() ) {
                yadisHttpEquivMatcher = yadisHttpEquivMatcher3;
            }
            if( yadisHttpEquivMatcher == null && yadisHttpEquivMatcher4.find() ) {
                yadisHttpEquivMatcher = yadisHttpEquivMatcher4;
            }

            if( yadisHttpEquivMatcher != null ) {
                // don't do another 'find' -- we already have one
                try {
                    int yadisLocationStart = yadisHttpEquivMatcher.start( 1 );

                    String                yadisLocation          = yadisHttpEquivMatcher.group( 1 );
                    NetMeshBaseIdentifier yadisNetworkIdentifier = NetMeshBaseIdentifier.guessAndCreate( sourceIdentifier, yadisLocation );

                    yadisLocation = yadisNetworkIdentifier.getUriString();

                    if( startHeadStart < yadisLocationStart && yadisLocationStart < endHeadStart ) {
                        HTTP.Response yadisResponse = HTTP.http_get( yadisLocation );
                        if( yadisResponse.isSuccess() ) {
                            addYadisServicesFromXml( yadisNetworkIdentifier, yadisResponse.getContentAsString(), base );
                            return;
                        }
                    }
                } catch( URISyntaxException ex ) {
                    log.warn( ex );
                } catch( IOException ex ) {
                    log.warn( ex );
                }
            }
            
            // look for OpenID tags
            Matcher openIdServerMatcher1   = openIdServerPattern1.matcher( content );
            Matcher openIdServerMatcher2   = openIdServerPattern2.matcher( content );
            Matcher openIdDelegateMatcher1 = openIdDelegatePattern1.matcher( content );
            Matcher openIdDelegateMatcher2 = openIdDelegatePattern2.matcher( content );

            Matcher openIdServerMatcher = null;
            if( openIdServerMatcher == null && openIdServerMatcher1.find() ) {
                openIdServerMatcher = openIdServerMatcher1;
            }
            if( openIdServerMatcher == null && openIdServerMatcher2.find() ) {
                openIdServerMatcher = openIdServerMatcher2;
            }
            // make sure it is in the right place
            if( openIdServerMatcher != null ) {
                int openIdServerStart = openIdServerMatcher.start( 1 );

                String identityServer = openIdServerMatcher.group( 1 );
                String delegateUrl    = null;

                try {
                    NetMeshBaseIdentifier identityServerIdentifier = NetMeshBaseIdentifier.guessAndCreate( identityServer );

                    identityServer = identityServerIdentifier.getUriString();

                    if( startHeadStart < openIdServerStart && openIdServerStart < endHeadStart ) {

                        // look for optional delegate tag
                        Matcher openIdDelegateMatcher = null;
                        if( openIdServerMatcher == null && openIdDelegateMatcher1.find() ) {
                            openIdDelegateMatcher = openIdDelegateMatcher1;
                        }
                        if( openIdServerMatcher == null && openIdDelegateMatcher2.find() ) {
                            openIdDelegateMatcher = openIdDelegateMatcher2;
                        }
                        if( openIdDelegateMatcher != null && openIdDelegateMatcher.find() ) {
                            int openIdDelegateStart = openIdDelegateMatcher.start();
                            if( startHeadStart < openIdDelegateStart && openIdDelegateStart < endHeadStart ) {
                                delegateUrl = openIdDelegateMatcher.group( 1 );

                                NetMeshBaseIdentifier delegateIdentifier = NetMeshBaseIdentifier.guessAndCreate( delegateUrl );

                                delegateUrl = delegateIdentifier.getUriString();
                            }
                        }
                    }
                } catch( URISyntaxException ex ) {
                    log.warn( ex );
                }

                try {
                    subject.bless( YadisSubjectArea.SITE );

                } catch( BlessedAlreadyException ex ) {
                    log.warn( ex );
                }

                String prefix = "YadisService-0";
                MeshObject serviceMeshObject = base.getMeshBaseLifecycleManager().createMeshObject(
                        base.getMeshObjectIdentifierFactory().fromExternalForm( prefix ),
                        // MeshObjectIdentifier.create( sourceIdentifier.toExternalForm(), prefix ),
                        Service._TYPE );

                serviceMeshObject.bless( Authentication1_0Service._TYPE ); // FIXME? OpenIDAuthentication.TYPE );
                if( delegateUrl != null ) {
                    serviceMeshObject.setPropertyValue( org.infogrid.lid.openid.AuthenticationService.DELEGATE, StringValue.create( delegateUrl ));
                }

                MeshObject endpoint = base.getMeshBaseLifecycleManager().createMeshObject(
                        base.getMeshObjectIdentifierFactory().fromExternalForm( prefix + "-endpoint-0" ),
                        // MeshObjectIdentifier.create( sourceIdentifier.getCanonicalForm(), prefix + "-endpoint-0" ),
                        Site._TYPE );

                // endpoint.setPropertyValue( ServiceEndPoint.URI_PROPERTYTYPE, StringValue.create( identityServer ));

                serviceMeshObject.relateAndBless( Service._Service_IsProvidedAtEndpoint_Site_SOURCE, endpoint );
                serviceMeshObject.relateAndBless( Service._Site_Offers_Service_DESTINATION, subject );
            }
        }
    }

    /**
     * The XML DocumentBuilder to use.
     */
    protected DocumentBuilder theDocumentBuilder;

    /**
     * The pattern that helps us find the beginning the HTML head section.
     */
    private static final Pattern startHeadPattern = Pattern.compile(
            ".*(<head).*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );

    /**
     * The pattern that helps us find the end of the HTML head section.
     */
    private static final Pattern endHeadPattern = Pattern.compile(
            ".*(</head).*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );

    /**
     * The pattern that helps us find the openid.server tag.
     */
    private static final Pattern openIdServerPattern1 = Pattern.compile(
            "<link[^>]+rel=[\"']?openid.server[\"'\\s][^>]*href=[\"']?([^\\s\"']*)[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    private static final Pattern openIdServerPattern2 = Pattern.compile(
            "<link[^>]+href=[\"']?([^\\s\"']*)[\"'\\s][^>]*rel=[\"']?openid.server[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );

    /**
     * The pattern that helps us find the openid.delegate tag.
     */
    private static final Pattern openIdDelegatePattern1 = Pattern.compile(
            "<link[^>]+rel=[\"']?openid.delegate[\"'\\s][^>]*href=[\"']?([^\\s\"']*)[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    private static final Pattern openIdDelegatePattern2 = Pattern.compile(
            "<link[^>]+href=[\"']?([^\\s\"']*)[\"'\\s][^>]*rel=[\"']?openid.delegate[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );

    /**
     * The pattern that helps us find the yadis location tag.
     */
    private static final Pattern yadisHttpEquivPattern1 = Pattern.compile(
            "<meta[^>]+http-equiv=[\"']?X-XRDS-Location[\"'\\s][^>]*content=[\"']?([^\\s\"']*)[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    private static final Pattern yadisHttpEquivPattern2 = Pattern.compile(
            "<meta[^>]+content=[\"']?([^\\s\"']*)[\"'\\s][^>]*http-equiv=[\"']?X-XRDS-Location[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    private static final Pattern yadisHttpEquivPattern3 = Pattern.compile(
            "<meta[^>]+http-equiv=[\"']?X-YADIS-Location[\"'\\s][^>]*content=[\"']?([^\\s\"']*)[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    private static final Pattern yadisHttpEquivPattern4 = Pattern.compile(
            "<meta[^>]+content=[\"']?([^\\s\"']*)[\"'\\s][^>]*http-equiv=[\"']?X-YADIS-Location[\"'\\s>]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
    
    /**
     * Table that maps Yadis type tags to Identifiers in our model.
     */
    protected static final HashMap<String, MeshTypeIdentifier> theTypeMappingTable = new HashMap<String,MeshTypeIdentifier>();
    static {
        // none of the following lines should be instantiated as long as we haven't defined a subtype of the Yadis generic Service
        // theTypeMappingTable.put( "http://lid.netmesh.org/minimum-lid/2.0",        MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/sso/2.0",                MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/relying-party/2.0",      MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/traversal/2.0",          MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/format-negotiation/2.0", MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/post/sender/2.0",        MeshObjectIdentifier.create( "org.Yadis", "Service" ));
        // theTypeMappingTable.put( "http://lid.netmesh.org/post/receiver/2.0",      MeshObjectIdentifier.create( "org.Yadis", "Service" ));
    }
    
    /**
     * The "beta" pattern.
     */
    private static final Pattern betaPattern = Pattern.compile(
            "^(.*)b([0-9]+)$",
            Pattern.CASE_INSENSITIVE );
}

