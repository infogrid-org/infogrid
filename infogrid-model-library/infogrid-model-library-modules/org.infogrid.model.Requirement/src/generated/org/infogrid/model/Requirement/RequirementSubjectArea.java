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
// Copyright 1998-2013 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

//
// This file has been generated AUTOMATICALLY. DO NOT MODIFY.
// on Thu, 2015-04-16 22:41:02 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Requirement;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class RequirementSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Requirement" );

    /**
      * A requirement.
      */
    public static final EntityType REQUIREMENT = org.infogrid.model.Requirement.Requirement._TYPE;

    /**
      * A requirement that is composed of other requirements.
      */
    public static final EntityType COMPOSITEREQUIREMENT = org.infogrid.model.Requirement.CompositeRequirement._TYPE;

    /**
      * Captures how the evaluated Requirements need to be evaluated\ndo determine whether the CompositeRequirement is met.
      */
    public static final PropertyType COMPOSITEREQUIREMENT_OPERATOR = org.infogrid.model.Requirement.CompositeRequirement.OPERATOR;
    public static final org.infogrid.model.primitives.EnumeratedDataType COMPOSITEREQUIREMENT_OPERATOR_type = (org.infogrid.model.primitives.EnumeratedDataType) COMPOSITEREQUIREMENT_OPERATOR.getDataType();
    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue COMPOSITEREQUIREMENT_OPERATOR_type_AND = COMPOSITEREQUIREMENT_OPERATOR_type.select( "AND" );
    public static final EnumeratedValue COMPOSITEREQUIREMENT_OPERATOR_type_OR = COMPOSITEREQUIREMENT_OPERATOR_type.select( "OR" );
    public static final EnumeratedValue COMPOSITEREQUIREMENT_OPERATOR_type_XOR = COMPOSITEREQUIREMENT_OPERATOR_type.select( "XOR" );

    /**
      * Links the CompositeRequirement to the Requirements that it evaluates.
      */
    public static final RelationshipType COMPOSITEREQUIREMENT_EVALUATES_REQUIREMENT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Requirement/CompositeRequirement_Evaluates_Requirement" );

}
