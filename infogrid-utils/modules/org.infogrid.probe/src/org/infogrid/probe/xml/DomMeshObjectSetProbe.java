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

package org.infogrid.probe.xml;

import org.infogrid.mesh.BlessedAlreadyException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifier;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.primitives.BlobValue;
import org.infogrid.model.primitives.BooleanValue;
import org.infogrid.model.primitives.ColorValue;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.EnumeratedDataType;
import org.infogrid.model.primitives.ExtentValue;
import org.infogrid.model.primitives.FloatValue;
import org.infogrid.model.primitives.IntegerValue;
import org.infogrid.model.primitives.MeshType;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MultiplicityValue;
import org.infogrid.model.primitives.PointValue;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.primitives.TimePeriodValue;
import org.infogrid.model.primitives.TimeStampValue;

import org.infogrid.modelbase.MeshTypeNotFoundException;
import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;
import org.infogrid.modelbase.ModelBase;

import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.StagingMeshBaseLifecycleManager;

import org.infogrid.util.Base64;
import org.infogrid.util.logging.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.NotRelatedException;

/**
 * The NetMesh Xml Probe.
 */
public class DomMeshObjectSetProbe
        implements
            MeshObjectSetProbeTags
{
    private static final Log log = Log.getLogInstance( DomMeshObjectSetProbe.class ); // our own, private logger

    /**
     * Constructor.
     */
    public DomMeshObjectSetProbe()
    {
        // no op
    }
    
    /**
     * Parse a DOM Document and instantiate corresponding MeshObjects.
     * 
     * 
     * @param theDocument the pre-parsed Document Object Model of the parsed data source
     * @param coherenceSpecification the type of data coherence that is requested by the application. Probe
     *         implementors may ignore this parameter, letting the Probe framework choose its own policy.
     *         If the Probe chooses to define its own policy (considering or ignoring this parameter), the
     *         Probe must bless the Probe's HomeObject with a subtype of ProbeUpdateSpecification (defined
     *         in the <code>org.infogrid.model.ProbeModel</code>) that reflects the policy.
     * @param pmbf the interface through which the Probe instantiates MeshObjects
     * @throws DoNotHaveLockException a Probe can declare to throw this Exception,
     *         which makes programming easier, but if it actually threw it, that would be a programming error
     * @throws MeshObjectIdentifierNotUniqueException Probe throws this Exception, it indicates that the
     *         Probe developer incorrectly assigned duplicate Identifiers to created MeshObject
     * @throws RelationshipExistsAlreadyException if a Probe throws this Exception, it indicates that the
     *         Probe developer incorrectly attempted to create another RelationshipType instance between
     *         the same two MeshObjects.
     * @throws TransactionException a Probe can declare to throw this Exception,
     *         which makes programming easier, but if it actually threw it, that would be a programming error
     * @throws ProbeException a Probe error occurred per the possible subclasses defined in ProbeException
     */
    public void parseDocument(
            NetMeshBaseIdentifier  theNetworkIdentifier,
            CoherenceSpecification coherence,
            Document               theDocument,
            StagingMeshBase        mb )
        throws
            NotPermittedException,
            MeshObjectIdentifierNotUniqueException,
            ProbeException,
            TransactionException,
            IOException
    {
        ModelBase                       theModelBase = mb.getModelBase();
        StagingMeshBaseLifecycleManager life         = mb.getMeshBaseLifecycleManager();
        
        ArrayList<ExternalizedMeshObject> theBufferedObjects = new ArrayList<ExternalizedMeshObject>();
        
        Node     topNode = null;
        NodeList meshObjectList = theDocument.getChildNodes();
        for( int i=0 ; i<meshObjectList.getLength() ; ++i ) {
            Node meshObjectNode = meshObjectList.item( i );
            if( meshObjectNode.getNodeType() == Node.ELEMENT_NODE && MESHOBJECT_SET_TAG.equals( meshObjectNode.getNodeName() )) {
                topNode = meshObjectNode;
                break;
            }
        }
        if( topNode == null ) {
            throw new ProbeException.EmptyDataSource( theNetworkIdentifier );
        }
        
        meshObjectList = topNode.getChildNodes();
        for( int i=0 ; i<meshObjectList.getLength() ; ++i ) {
            Node meshObjectNode = meshObjectList.item( i );
            
            if( meshObjectNode.getNodeType() != Node.ELEMENT_NODE ) {
                continue;
            }
            NamedNodeMap attrs = meshObjectNode.getAttributes();
            
            String identifier       = getTextContent( attrs, IDENTIFIER_TAG );
            String timeCreated      = getTextContent( attrs, TIME_CREATED_TAG );
            String timeUpdated      = getTextContent( attrs, TIME_UPDATED_TAG );
            String timeRead         = getTextContent( attrs, TIME_READ_TAG );
            String timeAutoDeletes  = getTextContent( attrs, TIME_AUTO_DELETES_TAG );
            String giveUpLock       = getTextContent( attrs, GIVE_UP_LOCK_TAG );
            String proxyTowardsHome = getTextContent( attrs, PROXY_TOWARDS_HOME_TAG );

            ExternalizedMeshObject theObjectBeingParsed = new ExternalizedMeshObject();
            
            if( identifier != null ) { // need to support "" for Home Object
                theObjectBeingParsed.setIdentifier( identifier );
            }
            if( timeCreated != null && timeCreated.length() > 0 ) {
                theObjectBeingParsed.setTimeCreated( MeshObjectSetProbeUtils.parseTime( timeCreated ));
            }
            if( timeUpdated != null && timeUpdated.length() > 0 ) {
                theObjectBeingParsed.setTimeUpdated( MeshObjectSetProbeUtils.parseTime( timeUpdated ));
            }
            if( timeRead != null && timeRead.length() > 0 ) {
                theObjectBeingParsed.setTimeRead( MeshObjectSetProbeUtils.parseTime( timeRead ));
            }
            if( timeAutoDeletes != null && timeAutoDeletes.length() > 0 ) {
                theObjectBeingParsed.setTimeAutoDeletes( MeshObjectSetProbeUtils.parseTime( timeAutoDeletes ));
            }
            if( YES_TAG.equals( giveUpLock )) {
                theObjectBeingParsed.setGiveUpLock( true );
            }
            if( proxyTowardsHome != null && proxyTowardsHome.length() > 0 ) {
                theObjectBeingParsed.setProxyTowardsHome( constructNetworkIdentifier( theNetworkIdentifier, proxyTowardsHome ));
            }
            
            NodeList childNodeList = meshObjectNode.getChildNodes();
            for( int j=0 ; j<childNodeList.getLength() ; ++j ) {
                Node   childNode     = childNodeList.item( j );
                String childNodeName = childNode.getNodeName();
                
                if( childNode.getNodeType() != Node.ELEMENT_NODE ) {
                    continue;
                }
                if( MESH_TYPE_TAG.equals( childNodeName )) {
                    String typeIdentifier = childNode.getTextContent();
                    theObjectBeingParsed.addMeshType( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( typeIdentifier ));

                } else if( PROPERTY_TYPE_TAG.equals( childNodeName )) {
                    String propertyIdentifier = getTextContent( childNode.getAttributes(), TYPE_TAG );
                    theObjectBeingParsed.addPropertyType( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( propertyIdentifier ));
                    
                    NodeList grandNodeList = childNode.getChildNodes();
                    for( int k=0 ; k<grandNodeList.getLength() ; ++k ) {
                        Node   grandNode     = grandNodeList.item( k );
                        String grandNodeName = grandNode.getNodeName();
                        String content       = grandNode.getTextContent();

                        if( grandNode.getNodeType() != Node.ELEMENT_NODE ) {
                            continue;
                        }

                        NamedNodeMap  grandAttrs = grandNode.getAttributes();
                        PropertyValue propValue  = null;
                        
                        if( BLOB_VALUE_TAG.equals( grandNodeName )) {
                            String mime      = getTextContent( grandAttrs, BLOB_VALUE_MIME_TAG );
                            String loadFrom  = getTextContent( grandAttrs, BLOB_VALUE_LOAD_TAG );

                            if( mime != null && mime.length() > 0 ) {
                                if( loadFrom != null && loadFrom.length() > 0 ) {
                                    propValue = BlobValue.createByLoadingFrom( loadFrom, mime );
                                } else if( mime.startsWith( "text/" )) {
                                    propValue = BlobValue.create( Base64.base64decode( content.trim() ), mime );
                                } else {
                                    propValue = BlobValue.create( content.trim(), mime );
                                }
                            } else {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier, "empty '" + BLOB_VALUE_MIME_TAG + "' on '" + BLOB_VALUE_TAG + "'", null );
                            }
                           
                        } else if( BOOLEAN_VALUE_TAG.equals( grandNodeName )) {
                            propValue = BooleanValue.create( BOOLEAN_VALUE_TRUE_TAG.equals( content.trim() ));
                            
                        } else if( COLOR_VALUE_TAG.equals( grandNodeName )) {
                            String red   = getTextContent( grandAttrs, COLOR_VALUE_RED_TAG );
                            String green = getTextContent( grandAttrs, COLOR_VALUE_GREEN_TAG );
                            String blue  = getTextContent( grandAttrs, COLOR_VALUE_BLUE_TAG );
                            String alpha = getTextContent( grandAttrs, COLOR_VALUE_ALPHA_TAG );

                            propValue = ColorValue.create( new Color(
                                    Float.parseFloat( red ),
                                    Float.parseFloat( green ),
                                    Float.parseFloat( blue ),
                                    Float.parseFloat( alpha )));
                            
                        } else if( ENUMERATED_VALUE_TAG.equals( grandNodeName )) {
                            try {
                                MeshType mt = theModelBase.findMeshTypeByIdentifier( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( propertyIdentifier ));
                                if( ( mt instanceof PropertyType ) && ((PropertyType)mt).getDataType() instanceof EnumeratedDataType ) {
                                    EnumeratedDataType realPt = (EnumeratedDataType) ((PropertyType)mt).getDataType();
                                    theObjectBeingParsed.addPropertyValue( realPt.select( content.trim() )); // FIXME?
                                } else {
                                    throw new ProbeException.SyntaxError( theNetworkIdentifier, "MeshType with " + propertyIdentifier + " is not a PropertyType", null );
                                }
                            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier, "Cannot find PropertyType with " + propertyIdentifier, ex );
                            }
                            
                        } else if( EXTENT_VALUE_TAG.equals( grandNodeName )) {
                            String width  = getTextContent( grandAttrs, EXTENT_VALUE_WIDTH_TAG );
                            String height = getTextContent( grandAttrs, EXTENT_VALUE_HEIGHT_TAG );

                            if( width == null || width.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + EXTENT_VALUE_WIDTH_TAG + "' on '" + EXTENT_VALUE_TAG + "'", null );
                            }            
                            if( height == null && height.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + EXTENT_VALUE_HEIGHT_TAG + "' on '" + EXTENT_VALUE_TAG + "'", null );
                            }
                            propValue = ExtentValue.create( Double.parseDouble( width ), Double.parseDouble( height ));

                        } else if( INTEGER_VALUE_TAG.equals( grandNodeName )) {
                            propValue = IntegerValue.parseIntegerValue( content );
                            
                        } else if( FLOAT_VALUE_TAG.equals( grandNodeName )) {
                            propValue = FloatValue.parseFloatValue( content );
                            
                        } else if( MULTIPLICITY_VALUE_TAG.equals( grandNodeName )) {
                            String min = getTextContent( grandAttrs, MULTIPLICITY_VALUE_MIN_TAG );
                            String max = getTextContent( grandAttrs, MULTIPLICITY_VALUE_MAX_TAG );

                            propValue = MultiplicityValue.create(
                                    ( min != null && min.length() > 0 ) ? Integer.parseInt( min ) : MultiplicityValue.N,
                                    ( max != null && max.length() > 0 ) ? Integer.parseInt( max ) : MultiplicityValue.N );
                            
                        } else if( POINT_VALUE_TAG.equals( grandNodeName )) {
                            String x = getTextContent( grandAttrs, POINT_VALUE_X_TAG );
                            String y = getTextContent( grandAttrs, POINT_VALUE_Y_TAG );

                            if( x == null || x.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + POINT_VALUE_X_TAG + "' on '" + POINT_VALUE_TAG + "'", null );
                            }
                            if( y != null || y.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + POINT_VALUE_Y_TAG + "' on '" + POINT_VALUE_TAG + "'", null );
                            }
                            propValue = PointValue.create( Double.parseDouble( x ), Double.parseDouble( y ));

                        } else if( STRING_VALUE_TAG.equals( grandNodeName )) {
                            propValue = StringValue.create( content );

                        } else if( TIME_PERIOD_TAG.equals( grandNodeName )) {
                            String yr  = getTextContent( grandAttrs, TIME_PERIOD_YEAR_TAG );
                            String mon = getTextContent( grandAttrs, TIME_PERIOD_MONTH_TAG );
                            String day = getTextContent( grandAttrs, TIME_PERIOD_DAY_TAG );
                            String hr  = getTextContent( grandAttrs, TIME_PERIOD_HOUR_TAG );
                            String min = getTextContent( grandAttrs, TIME_PERIOD_MINUTE_TAG );
                            String sec = getTextContent( grandAttrs, TIME_PERIOD_SECOND_TAG );

                            if( yr == null || yr.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_YEAR_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }
                            if( mon == null || mon.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_MONTH_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }
                            if( day == null || day.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_DAY_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }
                            if( hr == null || hr.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_HOUR_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }
                            if( min == null || min.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_MINUTE_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }
                            if( sec == null || sec.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_PERIOD_SECOND_TAG + "' on '" + TIME_PERIOD_TAG + "'", null );
                            }

                            propValue = TimePeriodValue.create(
                                    Short.parseShort( yr ),
                                    Short.parseShort( mon ),
                                    Short.parseShort( day ),
                                    Short.parseShort( hr ),
                                    Short.parseShort( min ),
                                    Float.parseFloat( sec ));
                            
                        } else if( TIME_STAMP_TAG.equals( grandNodeName )) {
                            String yr  = getTextContent( grandAttrs, TIME_STAMP_YEAR_TAG );
                            String mon = getTextContent( grandAttrs, TIME_STAMP_MONTH_TAG );
                            String day = getTextContent( grandAttrs, TIME_STAMP_DAY_TAG );
                            String hr  = getTextContent( grandAttrs, TIME_STAMP_HOUR_TAG );
                            String min = getTextContent( grandAttrs, TIME_STAMP_MINUTE_TAG );
                            String sec = getTextContent( grandAttrs, TIME_STAMP_SECOND_TAG );

                            if( yr == null || yr.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_YEAR_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            if( mon == null || mon.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_MONTH_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            if( day == null || day.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_DAY_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            if( hr == null || hr.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_HOUR_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            if( min == null || min.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_MINUTE_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            if( sec == null || sec.length() == 0 ) {
                                throw new ProbeException.SyntaxError( theNetworkIdentifier,  "empty '" + TIME_STAMP_SECOND_TAG + "' on '" + TIME_STAMP_TAG + "'", null );
                            }
                            propValue = TimeStampValue.create(
                                    Short.parseShort( yr ),
                                    Short.parseShort( mon ),
                                    Short.parseShort( day ),
                                    Short.parseShort( hr ),
                                    Short.parseShort( min ),
                                    Float.parseFloat( sec ));

            
                        } else {
                            throw new ProbeException.SyntaxError( theNetworkIdentifier,  "Unknown XML tag: " + grandNodeName, null );
                        }
                    
                        theObjectBeingParsed.addPropertyValue( propValue );
                    }
                } else if( RELATIONSHIP_TAG.equals( childNodeName )) {
                    String otherSideIdentifier = getTextContent( childNode.getAttributes(), IDENTIFIER_TAG );

                    theObjectBeingParsed.addRelationship( otherSideIdentifier );
                    
                    NodeList grandNodeList = childNode.getChildNodes();

                    for( int k=0 ; k<grandNodeList.getLength() ; ++k ) {
                        Node   grandNode     = grandNodeList.item( k );

                        if( grandNode.getNodeType() != Node.ELEMENT_NODE ) {
                            continue;
                        }

                        NamedNodeMap grandAttrs     = grandNode.getAttributes();
                        String       typeIdentifier = getTextContent( grandAttrs, TYPE_TAG );

                        theObjectBeingParsed.getCurrentRelationship().addRoleType( theModelBase.getMeshTypeIdentifierFactory().fromExternalForm( typeIdentifier ));
                    }

                } else {
                    throw new ProbeException.SyntaxError( theNetworkIdentifier, "Unknown XML tag: " + childNodeName, null );
                }
            }
            theBufferedObjects.add( theObjectBeingParsed );
        }
        
        try {
            // then instantiate MeshObjects

            for( ExternalizedMeshObject currentObject : theBufferedObjects ) {

                try {
                    MeshObject            realCurrentObject;
                    NetMeshBaseIdentifier proxy             = currentObject.getProxyTowardsHome();
                    String                currentIdentifier = currentObject.getIdentifier();

    //                if(    currentObject.getIdentifier().getLocalId() == null
    //                    && currentObject.getIdentifier().getPrefix()  == null
    //                    && proxy == null )

                    if( currentIdentifier.indexOf( '#' ) < 0 && proxy == null ) {
                        realCurrentObject = mb.getHomeObject();

                        realCurrentObject.bless( lookupEntityTypes( currentObject.getMeshTypes(), theModelBase ));

                    } else if( proxy == null ) {
                        realCurrentObject = life.createMeshObject(
                                constructIdentifier( theNetworkIdentifier, null, currentObject.getIdentifier()),
                                lookupEntityTypes( currentObject.getMeshTypes(), theModelBase ),
                                currentObject.getTimeCreated(),
                                currentObject.getTimeUpdated(),
                                currentObject.getTimeRead(),
                                currentObject.getTimeAutoDeletes());

                    } else {
                        // ForwardReference

                        NetMeshObjectIdentifier fwdRefName = constructIdentifier( theNetworkIdentifier, proxy, currentObject.getIdentifier() );

                        realCurrentObject = life.createForwardReference(
                                proxy,
                                fwdRefName,
                                lookupEntityTypes( currentObject.getMeshTypes(), theModelBase ));
                    }

                    for( int i=currentObject.thePropertyTypes.size()-1 ; i>=0 ; --i ) {
                        PropertyType  type  = lookupPropertyType( currentObject.thePropertyTypes.get( i ), theModelBase );
                        PropertyValue value = currentObject.thePropertyValues.get( i );
                        realCurrentObject.setPropertyValue( type, value );
                    }
                } catch( IsAbstractException ex ) {
                    log.error( ex );
                } catch( EntityBlessedAlreadyException ex ) {
                    log.error( ex );
                } catch( IllegalPropertyTypeException ex ) {
                    log.error( ex );
                } catch( IllegalPropertyValueException ex ) {
                    log.error( ex );
                }
            }

            // finally relate MeshObjects

            for( ExternalizedMeshObject currentObject : theBufferedObjects ) {
                NetMeshObjectIdentifier currentObjectName = constructIdentifier( theNetworkIdentifier, null, currentObject.getIdentifier() );
                MeshObject              realCurrentObject = mb.findMeshObjectByIdentifier( currentObjectName );

                for( ExternalizedMeshObject.ExternalizedRelationship currentRelationship : currentObject.theRelationships ) {
                    NetMeshObjectIdentifier otherSideName = constructIdentifier( theNetworkIdentifier, null, currentRelationship.getIdentifier() );
                    MeshObject              otherSide     = mb.findMeshObjectByIdentifier( otherSideName );

                    if( otherSide == null ) {
                        throw new ProbeException.SyntaxError( theNetworkIdentifier, "Referenced MeshObject could not be found: " + otherSide, null );
                    }
                    try {
                        realCurrentObject.relate( otherSide );
                    } catch( RelatedAlreadyException ex ) {
                        // this must be the other side of what we related already
                    }
                    try {
                        realCurrentObject.blessRelationship( lookupRoleTypes( currentRelationship.theRoleTypes, theModelBase ), otherSide );
                    } catch( BlessedAlreadyException ex ) {
                        // this must be the other side of what we related already
                    } catch( EntityNotBlessedException ex ) {
                        log.error( ex );
                    } catch( NotRelatedException ex ) {
                        log.error( ex );
                    } catch( IsAbstractException ex ) {
                        log.error( ex );
                    }
                }
            }

        
        } catch( NotPermittedException ex ) {
            throw new ProbeException.Other( theNetworkIdentifier, ex );

        } catch( MeshObjectIdentifierNotUniqueException ex ) {
            throw new ProbeException.SyntaxError( theNetworkIdentifier, ex );

        } catch( MeshTypeNotFoundException ex ) {
            throw new ProbeException.SyntaxError( theNetworkIdentifier, ex );

        } catch( URISyntaxException ex ) {
            throw new ProbeException.SyntaxError( theNetworkIdentifier, ex );
        }
    }

    /**
     * Helper method to look up EntityTypes.
     *
     * @throws ClassCastException thrown if an Identifier did not refer to an EntityType but something else
     * @throws ModuleException thrown if the Identifier referred to a Module that is unavailable
     */
    protected static EntityType [] lookupEntityTypes(
            MeshTypeIdentifier[] identifiers,
            ModelBase            modelBase )
        throws
            MeshTypeNotFoundException
    {
        EntityType [] ret = new EntityType[ identifiers.length ];
        for( int i=0 ; i<identifiers.length ; ++i ) {
            ret[i] = modelBase.findEntityTypeByIdentifier( identifiers[i] );
        }
        return ret;
    }
    
    /**
     * Helper method to look up a PropertyType.
     * 
     * @throws ClassCastException thrown if an identifier did not refer to a PropertyType but something else
     * @throws ModuleException thrown if the identifier referred to a Module that is unavailable
     */
    protected static PropertyType lookupPropertyType(
            MeshTypeIdentifier identifier,
            ModelBase          modelBase )
        throws
            MeshTypeNotFoundException
    {
        PropertyType ret = modelBase.findPropertyTypeByIdentifier( identifier );
        return ret;
    }
    
    /**
     * Helper method to look up a RoleType.
     *
     * @throws ClassCastException thrown if an Identifier did not refer to a RoleType but something else
     * @throws ModuleException thrown if the Identifier referred to a Module that is unavailable
     */
    protected static RoleType [] lookupRoleTypes(
            ArrayList<MeshTypeIdentifier> identifier,
            ModelBase                     modelBase )
        throws
            MeshTypeNotFoundException
    {
        RoleType [] ret = new RoleType[ identifier.size() ];
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = modelBase.findRoleTypeByIdentifier( identifier.get( i ) );
        }
        return ret;
    }
    
    /**
     * Helper method to obtain the text attribute of a node.
     */
    protected static String getTextContent(
            NamedNodeMap attrs,
            String       name )
    {
        Node n = attrs.getNamedItem( name );
        if( n != null ) {
            return n.getTextContent();
        } else {
            return null;
        }
    }
    
    /**
     * Helper method to construct a fully-qualified Identifier, given a String
     * in the XML file that represents the Identifier, and a NetMeshBaseIdentifier for proxyIdentifier.
     */
    protected NetMeshObjectIdentifier constructIdentifier(
            NetMeshBaseIdentifier   dataSourceIdentifier,
            NetMeshBaseIdentifier   proxyIdentifier,
            String                  externalForm )
        throws
            ProbeException.SyntaxError,
            URISyntaxException
    {
//        if( ret.getPrefix() == null ) {
//            if( proxyIdentifier != null ) {
//                ret = MeshObjectIdentifier.create( proxyIdentifier.toExternalForm(), ret.getLocalId() );
//            }
//        } else {
        
            NetMeshObjectIdentifier ret;
            
            Matcher m = VARIABLE_PATTERN.matcher( externalForm );
            if( m.find() ) {
                String variable = m.group( 1 );
                String replacement = theVariableReplacements.get( variable );
                if( replacement == null ) {
                    throw new ProbeException.SyntaxError( dataSourceIdentifier, "Cannot resolve variable " + variable, null );
                }
                String newExternalForm = externalForm.replaceAll( VARIABLE_PATTERN.pattern(), replacement );
                ret = DefaultAnetMeshObjectIdentifier.fromExternalForm( dataSourceIdentifier, newExternalForm );
            } else {
                ret = DefaultAnetMeshObjectIdentifier.fromExternalForm( dataSourceIdentifier, externalForm );
            }
//        }
        return ret;
    }
    
    /**
     * Helper method to construct a NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier constructNetworkIdentifier(
            NetMeshBaseIdentifier theNetworkIdentifier,
            String            raw )
        throws
            ProbeException.SyntaxError
    {
        try {
            NetMeshBaseIdentifier ret = NetMeshBaseIdentifier.guessAndCreate( theNetworkIdentifier, raw );

            theVariableReplacements.put( raw, ret.toExternalForm() );
            
            return ret;

        } catch( URISyntaxException ex ) {
            throw new ProbeException.SyntaxError( theNetworkIdentifier, ex );
        }
    }

    /**
     * Pattern indicating a variable.
     */
    public static final Pattern VARIABLE_PATTERN = Pattern.compile( "\\$\\{(.*)\\}" );

    /**
     * The table of variables and their replacements.
     */
    protected HashMap<String,String> theVariableReplacements = new HashMap<String,String>();
}
