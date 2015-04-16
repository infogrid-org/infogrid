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
// on Thu, 2015-04-16 22:40:56 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Common;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class CommonSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Common" );

    /**
      * This entitytype is an abstract supertype for components inside a structured, potentially\nhierarchical definition (represented by DefinitionObject). In programming, for example, a member variable\nof a class could be represented as a (subtype of) ComponentObject.
      */
    public static final EntityType COMPONENTOBJECT = org.infogrid.model.Common.ComponentObject._TYPE;

    public static final PropertyType COMPONENTOBJECT_SEQUENCENUMBER = org.infogrid.model.Common.ComponentObject.SEQUENCENUMBER;
    public static final org.infogrid.model.primitives.FloatDataType COMPONENTOBJECT_SEQUENCENUMBER_type = (org.infogrid.model.primitives.FloatDataType) COMPONENTOBJECT_SEQUENCENUMBER.getDataType();

    /**
      * DefinitionObject serves as an abstract supertype for all definitions which may\nbe reused. A DefinitionObject may contain ComponentObjects, indicating that the definition is\nstructured. The ComponentObjects contained in a DefinitionObject form the components of\nthe structured definition. DefinitionObjects are referenced by ComponentObjects, indicating\nthe definition of the ComponentObject. In programming, for example, a class could be represented\nas a (subtype of) DefinitionObject. Its member variables would be (subtypes of) ComponentObject,\nwhich would be contained in the DefinitionObject. In turn, the contained ComponentObjects reference\nother DefinitionObjects (representing other classes, for example), to indicate their own structure.
      */
    public static final EntityType DEFINITIONOBJECT = org.infogrid.model.Common.DefinitionObject._TYPE;

    /**
      * This relationshiptype relates a DefinitionObject to those ComponentObjects wich form its structure\nor composition.
      */
    public static final RelationshipType DEFINITIONOBJECT_CONTAINS_COMPONENTOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Common/DefinitionObject_Contains_ComponentObject" );

    /**
      * This relates a ComponentObject to its sharable definition.
      */
    public static final RelationshipType COMPONENTOBJECT_REFERENCES_DEFINITIONOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Common/ComponentObject_References_DefinitionObject" );

}
