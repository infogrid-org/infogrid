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
// on Thu, 2015-04-16 22:40:52 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Annotation;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class AnnotationSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Annotation" );

    /**
      * An annotation to another MeshObject.
      */
    public static final EntityType ANNOTATION = org.infogrid.model.Annotation.Annotation._TYPE;

    /**
      * Relates the Annotation to the MeshObject it annotates.
      */
    public static final RelationshipType ANNOTATION_ANNOTATES_MESHOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Annotation/Annotation_Annotates_MeshObject" );

}
