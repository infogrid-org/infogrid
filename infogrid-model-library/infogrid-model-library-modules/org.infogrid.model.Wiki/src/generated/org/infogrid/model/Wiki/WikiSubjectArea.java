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
// on Thu, 2015-04-16 22:41:09 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Wiki;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class WikiSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Wiki" );

    /**
      * A wiki object
      */
    public static final EntityType WIKIOBJECT = org.infogrid.model.Wiki.WikiObject._TYPE;

    /**
      * Current official content of the Wiki Object.
      */
    public static final PropertyType WIKIOBJECT_CONTENT = org.infogrid.model.Wiki.WikiObject.CONTENT;
    public static final org.infogrid.model.primitives.BlobDataType WIKIOBJECT_CONTENT_type = (org.infogrid.model.primitives.BlobDataType) WIKIOBJECT_CONTENT.getDataType();

}
