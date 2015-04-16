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
// on Thu, 2015-04-16 22:40:57 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Blob;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class BlobSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Blob" );

    /**
      * A document such as a file.
      */
    public static final EntityType BLOBOBJECT = org.infogrid.model.Blob.BlobObject._TYPE;

    /**
      * The content of the Document
      */
    public static final PropertyType BLOBOBJECT_CONTENT = org.infogrid.model.Blob.BlobObject.CONTENT;
    public static final org.infogrid.model.primitives.BlobDataType BLOBOBJECT_CONTENT_type = (org.infogrid.model.primitives.BlobDataType) BLOBOBJECT_CONTENT.getDataType();

    /**
      * The URL from which this BlobObject was loaded.
      */
    public static final PropertyType BLOBOBJECT_CODEBASE = org.infogrid.model.Blob.BlobObject.CODEBASE;
    public static final org.infogrid.model.primitives.StringDataType BLOBOBJECT_CODEBASE_type = (org.infogrid.model.primitives.StringDataType) BLOBOBJECT_CODEBASE.getDataType();

    /**
      * A named reference to a file, but not the file itself.\nMultiple instances of this object can reference the actual file content\n(what certain operating systems call \"hard link\").
      */
    public static final EntityType FILE = org.infogrid.model.Blob.File._TYPE;

    /**
      * A directory in a file system.
      */
    public static final EntityType DIRECTORY = org.infogrid.model.Blob.Directory._TYPE;

    /**
      * An image in a format such as PNG.
      */
    public static final EntityType IMAGE = org.infogrid.model.Blob.Image._TYPE;

    /**
      * The content of the Image
      */
    public static final PropertyType IMAGE_CONTENT = org.infogrid.model.Blob.Image.CONTENT;
    public static final org.infogrid.model.primitives.BlobDataType IMAGE_CONTENT_type = (org.infogrid.model.primitives.BlobDataType) IMAGE_CONTENT.getDataType();

}
