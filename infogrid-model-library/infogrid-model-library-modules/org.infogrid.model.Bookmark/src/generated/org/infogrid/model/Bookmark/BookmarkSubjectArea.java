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
// on Thu, 2015-04-16 22:40:58 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Bookmark;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class BookmarkSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Bookmark" );

    /**
      * A collection of book marks.
      */
    public static final EntityType BOOKMARKCOLLECTION = org.infogrid.model.Bookmark.BookmarkCollection._TYPE;

    /**
      * The name of the Bookmark Collection
      */
    public static final PropertyType BOOKMARKCOLLECTION_NAME = org.infogrid.model.Bookmark.BookmarkCollection.NAME;
    public static final org.infogrid.model.primitives.StringDataType BOOKMARKCOLLECTION_NAME_type = (org.infogrid.model.primitives.StringDataType) BOOKMARKCOLLECTION_NAME.getDataType();

    /**
      * A bookmark
      */
    public static final EntityType BOOKMARK = org.infogrid.model.Bookmark.Bookmark._TYPE;

    /**
      * The name of the bookmark
      */
    public static final PropertyType BOOKMARK_NAME = org.infogrid.model.Bookmark.Bookmark.NAME;
    public static final org.infogrid.model.primitives.StringDataType BOOKMARK_NAME_type = (org.infogrid.model.primitives.StringDataType) BOOKMARK_NAME.getDataType();

    /**
      * Defines the position of the Bookmark with respect to its siblings
      */
    public static final PropertyType BOOKMARK_SEQUENCENUMBER = org.infogrid.model.Bookmark.Bookmark.SEQUENCENUMBER;
    public static final org.infogrid.model.primitives.FloatDataType BOOKMARK_SEQUENCENUMBER_type = (org.infogrid.model.primitives.FloatDataType) BOOKMARK_SEQUENCENUMBER.getDataType();

    /**
      * Relates a MeshObject to the BookmarkCollections in which it is collected.
      */
    public static final RelationshipType BOOKMARKCOLLECTION_COLLECTS_MESHOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Bookmark/BookmarkCollection_Collects_MeshObject" );

    /**
      * Relates a Bookmark to the BookmarkCollection in which it is collected.
      */
    public static final RelationshipType BOOKMARKCOLLECTION_COLLECTS_BOOKMARK = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Bookmark/BookmarkCollection_Collects_Bookmark" );

    /**
      * Relates the Bookmark to the MeshObject that is being bookmarked.
      */
    public static final RelationshipType BOOKMARK_BOOKMARKS_MESHOBJECT = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Bookmark/Bookmark_Bookmarks_MeshObject" );

    /**
      * Enables a MeshObject (such as a person) to identify those BookmarkCollections they use.
      */
    public static final RelationshipType MESHOBJECT_USES_BOOKMARKCOLLECTION = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Bookmark/MeshObject_Uses_BookmarkCollection" );

}
