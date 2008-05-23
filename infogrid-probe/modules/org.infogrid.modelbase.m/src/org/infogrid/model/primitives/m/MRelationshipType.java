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

package org.infogrid.model.primitives.m;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MultiplicityValue;
import org.infogrid.model.primitives.RelationshipType;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.StringHelper;

/**
  * A MeshType that has a source and a destination, that can participate in
  * inheritance relationships, and may be attributed.
  * In-memory implementation.
  */
public final class MRelationshipType
        extends
            MAttributableMeshType
        implements
            RelationshipType
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor to create a binary RelationshipType.
     * 
     * @param identifier the Identifier of the to-be-created object
     * @param sourceEntity the source EntityType of the RelationshipType
     * @param destinationEntity the destination EntityType of the RelationshipType
     * @param sourceMultiplicity the multiplicity of the source
     * @param destinationMultiplicity the multiplicity of the destination
     * @param sourceRoleConstraintClassNames the RoleConstraints to enforce at the source end of all instances of this RelationshipType
     * @param destinationRoleConstraintClassNames the RoleConstraints to enforce at the destination end of all instances of this RelationshipType
     */
    public MRelationshipType(
            MeshTypeIdentifier identifier,
            MEntityType        sourceEntity,
            MEntityType        destinationEntity,
            MultiplicityValue  sourceMultiplicity,
            MultiplicityValue  destinationMultiplicity,
            String []          sourceRoleConstraintClassNames,
            String []          destinationRoleConstraintClassNames )
    {
        super( identifier );

        sourceRole      = new MRoleType.Source(      this, sourceEntity,      sourceMultiplicity,      sourceRoleConstraintClassNames );
        destinationRole = new MRoleType.Destination( this, destinationEntity, destinationMultiplicity, destinationRoleConstraintClassNames );
    }

    /**
     * Constructor to create a unary RelationshipType.
     * 
     * @param identifier the Identifier of the to-be-created object
     * @param sourceAndDestEntity the associated EntityType of the RelationshipType
     * @param sourceAndDestMultiplicity the multiplicity of the single RoleType
     * @param sourceAndDestRoleConstraintClassNames the RoleConstraints to enforce at all ends of all instances of this RelationshipType
     */
    public MRelationshipType(
            MeshTypeIdentifier identifier,
            MEntityType        sourceAndDestEntity,
            MultiplicityValue  sourceAndDestMultiplicity,
            String []          sourceAndDestRoleConstraintClassNames )
    {
        super( identifier );

        sourceRole      = new MRoleType.TopSingleton( this, sourceAndDestEntity, sourceAndDestMultiplicity, sourceAndDestRoleConstraintClassNames );
        destinationRole = sourceRole;
    }

    /**
      * Obtain the source RoleType of this RelationshipType.
      *
      * @return the source RoleType of this RelationshipType
      */
    public final RoleType getSource()
    {
        return sourceRole;
    }

    /**
      * Obtain the destination RoleType of this RelationshipType.
      *
      * @return the destination RoleType of this RelationshipType
      */
    public final RoleType getDestination()
    {
        return destinationRole;
    }

    /**
     * Convert to String form, for debugging.
     *
     * @return String form of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theIdentifier"
                },
                new Object[] {
                    getIdentifier()
                });
    }

    /**
      * The value of the source RoleType.
      */
    private MRoleType sourceRole;

    /**
      * The value of the destination RoleType.
      */
    private MRoleType destinationRole;
}
