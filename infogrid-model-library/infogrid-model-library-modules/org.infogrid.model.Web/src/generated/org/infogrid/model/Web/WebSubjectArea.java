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
// on Thu, 2015-04-16 22:41:07 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Web;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class WebSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Web" );

    /**
      * The resource at a URI. The URI is found in the identifier property.
      */
    public static final EntityType WEBRESOURCE = org.infogrid.model.Web.WebResource._TYPE;

    /**
      * The HTTP status code
      */
    public static final PropertyType WEBRESOURCE_HTTPSTATUSCODE = org.infogrid.model.Web.WebResource.HTTPSTATUSCODE;
    public static final org.infogrid.model.primitives.IntegerDataType WEBRESOURCE_HTTPSTATUSCODE_type = (org.infogrid.model.primitives.IntegerDataType) WEBRESOURCE_HTTPSTATUSCODE.getDataType();

    /**
      * In case of a redirect status code, contains the value of the location header.
      */
    public static final PropertyType WEBRESOURCE_HTTPHEADERLOCATION = org.infogrid.model.Web.WebResource.HTTPHEADERLOCATION;
    public static final org.infogrid.model.primitives.StringDataType WEBRESOURCE_HTTPHEADERLOCATION_type = (org.infogrid.model.primitives.StringDataType) WEBRESOURCE_HTTPHEADERLOCATION.getDataType();

    /**
      * Captures the link relationship between Web Resources, as expressed\nin HTTP Link headers, HTML link tags and the like. Specific types of link relationships are\nexpressed using subtypes of this RelationshipType.
      */
    public static final RelationshipType WEBRESOURCE_HASLINKTO_WEBRESOURCE = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Web/WebResource_HasLinkTo_WebResource" );

}
