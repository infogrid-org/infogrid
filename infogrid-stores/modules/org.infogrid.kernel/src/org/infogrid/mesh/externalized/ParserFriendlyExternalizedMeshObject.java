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

package org.infogrid.mesh.externalized;

import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyValue;

import java.util.ArrayList;

/**
 * This is a temporary buffer for a to-be-deserialized MeshObject.
 */
public class ParserFriendlyExternalizedMeshObject
        extends
            AbstractExternalizedMeshObject
        implements
            ExternalizedMeshObject
{
    /**
     * Set the Identifier.
     * 
     * @param newValue the new value
     */
    public void setIdentifier(
            MeshObjectIdentifier newValue )
    {
        theIdentifier = newValue;
    }

    /**
     * Set the TimeCreated.
     *
     * @param newValue the new value
     */
    public void setTimeCreated(
            long newValue )
    {
        theTimeCreated = newValue;
    }
    
    /**
     * Set the TimeUpdated.
     *
     * @param newValue the new value
     */
    public void setTimeUpdated(
            long newValue )
    {
        theTimeUpdated = newValue;
    }
    
    /**
     * Set the TimeRead.
     *
     * @param newValue the new value
     */
    public void setTimeRead(
            long newValue )
    {
        theTimeRead = newValue;
    }
    
    /**
     * Set the TimeExpires.
     *
     * @param newValue the new value
     */
    public void setTimeExpires(
            long newValue )
    {
        theTimeExpires = newValue;
    }
    
    /**
     * Add a MeshType, using its HasTypes.
     * 
     * 
     * @param identifier the MeshType's HasTypes
     */
    public void addMeshType(
            MeshTypeIdentifier identifier )
    {
        theMeshTypes.add( identifier );
    }

    /**
     * Get the MeshTypes' Identifiers.
     *
     * @return the MeshTypes' Identifiers.
     */
    public MeshTypeIdentifier [] getExternalTypeIdentifiers()
    {
        MeshTypeIdentifier [] ret = theMeshTypes.toArray( new MeshTypeIdentifier[ theMeshTypes.size() ]);
        return ret;
    }

    /**
     * Obtain the Identifiers of the neighbors of this MeshObject.
     * 
     * @return the Identifiers of the neighbors
     * @see #getTypes
     */
    public MeshObjectIdentifier[] getNeighbors()
    {
        MeshObjectIdentifier [] ret = new MeshObjectIdentifier[ theRelationships.size() ];
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = theRelationships.get( i ).getNeighborIdentifier();
        }
        return ret;
    }

    /**
     * Obtain the RoleTypes played by this MeshObject with respect to
     * a given neighbor.
     *
     * @return the RoleTypes
     */
    public MeshTypeIdentifier [] getRoleTypesFor(
            MeshObjectIdentifier neighbor )
    {
        for( int i=0 ; i<theRelationships.size() ; ++i ) {
            HasRoleTypes current = theRelationships.get( i );

            if( current.getNeighborIdentifier().equals( neighbor )) {
                return current.getTypes();
            }
        }
        throw new IllegalArgumentException( "Not found" );
    }

    /**
     * Add an equivalent, using its MeshObjectIdentifier.
     * 
     * @param identifier the HasTypes of an equivalent
     */
    public void addEquivalent(
            MeshObjectIdentifier identifier )
    {
        theEquivalents.add( identifier );
    }

    /**
     * Get the MeshObject's equivalents, if any.
     *
     * @return the equivalents' Identifiers.
     */
    public MeshObjectIdentifier[] getEquivalents()
    {
        MeshObjectIdentifier [] ret = theEquivalents.toArray( new MeshObjectIdentifier[ theEquivalents.size() ]);
        return ret;
    }

    /**
     * Add a PropertyType, using its HasTypes.
     * 
     * 
     * @param identifier the PropertyType's HasTypes
     */
    public void addPropertyType(
            MeshTypeIdentifier identifier )
    {
        thePropertyTypes.add( identifier );
    }

    /**
     * Get the PropertyTypes' Identifiers.
     *
     * @return the PropertyTypes' Identifiers.
     */
    public MeshTypeIdentifier [] getPropertyTypes()
    {
        MeshTypeIdentifier [] ret = thePropertyTypes.toArray( new MeshTypeIdentifier[ thePropertyTypes.size() ]);
        return ret;
    }

    /**
     * Add a PropertyValue.
     *
     * @param newValue the PropertyValue.
     */
    public void addPropertyValue(
            PropertyValue newValue )
    {
        thePropertyValues.add( newValue );
    }

    /**
     * Get the PropertyValues.
     *
     * @return the PropertyValues
     */
    public PropertyValue [] getPropertyValues()
    {
        PropertyValue [] ret = thePropertyValues.toArray( new PropertyValue[ thePropertyValues.size() ]);
        return ret;
    }

    /**
     * Add a Relationship.
     *
     * @param rel the other Relationship
     */
    public void addRelationship(
            HasRoleTypes rel )
    {
        theRelationships.add( rel );
    }

    /**
     * Get the Relationships.
     *
     * @return the Relationships
     */
    public HasRoleTypes [] getRelationships()
    {
        HasRoleTypes [] ret = theRelationships.toArray( new HasRoleTypes[ theRelationships.size() ]);
        return ret;
    }

    /**
     * Set the PropertyValue that is currently being parsed.
     *
     * @param newPropertyValue the PropertyValue
     */
    public void setCurrentPropertyValue(
            PropertyValue newPropertyValue )
    {
        theCurrentPropertyValue = newPropertyValue;
    }

    /**
     * Obtain the PropertyValue that is currently being parsed.
     *
     * @return the PropertyValue
     */
    public PropertyValue getCurrentPropertyValue()
    {
        return theCurrentPropertyValue;
    }

    /**
     * The Identifiers of the MeshTypes.
     */
    protected ArrayList<MeshTypeIdentifier> theMeshTypes = new ArrayList<MeshTypeIdentifier>();
    
    /**
     * The Identifiers of the equivalents.
     */
    protected ArrayList<MeshObjectIdentifier> theEquivalents = new ArrayList<MeshObjectIdentifier>();
    
    /**
     * The Identifiers of the PropertyTypes.
     */
    protected ArrayList<MeshTypeIdentifier> thePropertyTypes = new ArrayList<MeshTypeIdentifier>();
    
    /**
     * The PropertyValues that go with the PropertyTypes.
     */
    protected ArrayList<PropertyValue> thePropertyValues = new ArrayList<PropertyValue>();

    /**
     * The Relationships in which this ExternalizedMeshObject participates.
     */
    protected ArrayList<HasRoleTypes> theRelationships = new ArrayList<HasRoleTypes>();
    
    /**
     * The PropertyValue that is currently being parsed.
     */
    protected PropertyValue theCurrentPropertyValue;

    /**
     * Represents something thas has types.
     */
    public static class HasTypes
    {
        /**
         * Constructor.
         */
        public HasTypes(
                MeshObjectIdentifier identifier,
                long                 timeUpdated )
        {
            theIdentifier  = identifier;
            theTimeUpdated = timeUpdated;
        }

        /**
         * Obtain the HasTypes of the MeshObject at this end.
         * 
         * @return the HasTypes of the MeshObject at this end
         */
        public MeshObjectIdentifier getIdentifier()
        {
            return theIdentifier;
        }
        
        /**
         * Get the Identifiers of the RoleTypes played by this relationship.
         *
         * @return the Identifiers of the RoleTypes
         */
        public MeshTypeIdentifier [] getTypes()
        {
            MeshTypeIdentifier [] ret = theTypes.toArray( new MeshTypeIdentifier[ theTypes.size() ]);
            return ret;
        }

        /**
         * Add the HasTypes of a RoleType.
         * 
         * @param identifier the HasTypes
         */
        public void addType(
                MeshTypeIdentifier identifier )
        {
            theTypes.add( identifier );
        }

        /**
         * Obtain the time at which the update was performed.
         *
         * @return the time at which the udpated was performed, in System.currentTimeMillis() format.
         */
        public long getTimeUpdated()
        {
            return theTimeUpdated;
        }

        /**
         * The HasTypes of the MeshObject on this side.
         */
        protected MeshObjectIdentifier theIdentifier;

        /**
         * The Identifiers of the RoleTypes.
         */
        protected ArrayList<MeshTypeIdentifier> theTypes = new ArrayList<MeshTypeIdentifier>();

        /**
         * The time at which this event occurred.
         */
        protected long theTimeUpdated;
    }

    /**
     * Represents something that has RoleTypes.
     */
    public static class HasRoleTypes
            extends
                HasTypes
    {
        /**
         * Constructor.
         * 
         * @param identifier the HasTypes of the other side of the relationship
         */
        public HasRoleTypes(
                MeshObjectIdentifier identifier,
                MeshObjectIdentifier neighborIdentifier,
                long                 timeUpdated )
        {
            super( identifier, timeUpdated );

            theNeighborIdentifier = neighborIdentifier;
        }

        /**
         * Obtain the HasTypes of the MeshObject at the other end.
         * 
         * @return the HasTypes of the MeshObject at the other end
         */
        public MeshObjectIdentifier getNeighborIdentifier()
        {
            return theNeighborIdentifier;
        }
        
        /**
         * The HasTypes of the MeshObject on the other side.
         */
        protected MeshObjectIdentifier theNeighborIdentifier;
    }
    
    /**
     * Represents something that has Properties.
     */
    public static class HasProperties
    {
        /**
         * Constructor.
         * 
         * 
         * @param identifier the HasTypes of the other side of the relationship
         */
        public HasProperties(
                MeshObjectIdentifier identifier,
                MeshTypeIdentifier   propertyTypeName,
                long                 timeUpdated )
        {
            theIdentifier     = identifier;
            thePropertyTypeName = propertyTypeName;
            theTimeUpdated      = timeUpdated;
        }
        
        /**
         * Obtain the HasTypes of the MeshObject at this end.
         * 
         * @return the HasTypes of the MeshObject at this end
         */
        public MeshObjectIdentifier getIdentifier()
        {
            return theIdentifier;
        }
        
        /**
         * Obtain the HasTypes of the PropertyType.
         * 
         * @return the HasTypes of the PropertyType.
         */
        public MeshTypeIdentifier getPropertyTypeName()
        {
            return thePropertyTypeName;
        }
        
        /**
         * Obtain the time at which the update was performed.
         *
         * @return the time at which the udpated was performed, in System.currentTimeMillis() format.
         */
        public long getTimeUpdated()
        {
            return theTimeUpdated;
        }

        /**
         * The Identifier of the affected MeshObject.
         */
        protected MeshObjectIdentifier theIdentifier;

        /**
         * The Identifier PropertyType.
         */
        protected MeshTypeIdentifier thePropertyTypeName;

        /**
         * The time at which this event occurred.
         */
        protected long theTimeUpdated;
    }
}
