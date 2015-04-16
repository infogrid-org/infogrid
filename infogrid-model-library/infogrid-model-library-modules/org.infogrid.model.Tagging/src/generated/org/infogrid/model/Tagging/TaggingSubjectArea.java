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
// on Thu, 2015-04-16 22:41:03 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Tagging;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class TaggingSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Tagging" );

    /**
      * A library of tags.
      */
    public static final EntityType TAGLIBRARY = org.infogrid.model.Tagging.TagLibrary._TYPE;

    /**
      * The name of the Tag Library
      */
    public static final PropertyType TAGLIBRARY_NAME = org.infogrid.model.Tagging.TagLibrary.NAME;
    public static final org.infogrid.model.primitives.StringDataType TAGLIBRARY_NAME_type = (org.infogrid.model.primitives.StringDataType) TAGLIBRARY_NAME.getDataType();

    /**
      * A tag.
      */
    public static final EntityType TAG = org.infogrid.model.Tagging.Tag._TYPE;

    /**
      * The public representation of the Tag
      */
    public static final PropertyType TAG_LABEL = org.infogrid.model.Tagging.Tag.LABEL;
    public static final org.infogrid.model.primitives.StringDataType TAG_LABEL_type = (org.infogrid.model.primitives.StringDataType) TAG_LABEL.getDataType();

    /**
      * Relates a Tag to the TagLibrary in which it is defined.
      */
    public static final RelationshipType TAGLIBRARY_COLLECTS_TAG = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Tagging/TagLibrary_Collects_Tag" );

    /**
      * Relates a Tag to the zero or more MeshObjects that it tags.
      */
    public static final RelationshipType TAG_TAGS_MESHOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Tagging/Tag_Tags_MeshObject" );

    /**
      * Enables a MeshObject (such as a person) to identify those TagLibraries they use.
      */
    public static final RelationshipType MESHOBJECT_USES_TAGLIBRARY = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Tagging/MeshObject_Uses_TagLibrary" );

}
