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
  * <p>Java interface for EntityType org.infogrid.model.Web/WebResource.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Web/WebResource</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>WebResource</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Web Resource</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The resource at a URI. The URI is found in the identifier property.</td></tr></table></td></tr>
  * </table>
  */

public interface WebResource
        extends
            org.infogrid.mesh.TypedMeshObjectFacade
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Web" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Web/WebResource" );

    /**
      * This MeshType.
      */
    public static final RoleType _WebResource_HasLinkTo_WebResource_SOURCE = ModelBaseSingleton.findRoleType( "org.infogrid.model.Web/WebResource_HasLinkTo_WebResource-S" );

    /**
      * This MeshType.
      */
    public static final RoleType _WebResource_HasLinkTo_WebResource_DESTINATION = ModelBaseSingleton.findRoleType( "org.infogrid.model.Web/WebResource_HasLinkTo_WebResource-D" );

    /**
      * <p>Set value for property HttpStatusCode.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Web/WebResource_HttpStatusCode</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HttpStatusCode</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>200</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>HTTP Status Code</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The HTTP status code</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setHttpStatusCode(
            org.infogrid.model.primitives.IntegerValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property HttpStatusCode.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Web/WebResource_HttpStatusCode</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HttpStatusCode</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Integer (default: 0, min: -9223372036854775808, max: 9223372036854775807)</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>200</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>HTTP Status Code</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The HTTP status code</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.IntegerValue getHttpStatusCode()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the HttpStatusCode property.
      */
    public static final String HTTPSTATUSCODE_name = "HttpStatusCode";

    /**
      * The HttpStatusCode PropertyType.
      */
    public static final PropertyType HTTPSTATUSCODE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Web/WebResource_HttpStatusCode" );
    public static final org.infogrid.model.primitives.IntegerDataType HTTPSTATUSCODE_type = (org.infogrid.model.primitives.IntegerDataType) HTTPSTATUSCODE.getDataType();

    /**
      * <p>Set value for property HttpHeaderLocation.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Web/WebResource_HttpHeaderLocation</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HttpHeaderLocation</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>HTTP Location Header</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>In case of a redirect status code, contains the value of the location header.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setHttpHeaderLocation(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property HttpHeaderLocation.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Web/WebResource_HttpHeaderLocation</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HttpHeaderLocation</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>HTTP Location Header</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>In case of a redirect status code, contains the value of the location header.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getHttpHeaderLocation()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the HttpHeaderLocation property.
      */
    public static final String HTTPHEADERLOCATION_name = "HttpHeaderLocation";

    /**
      * The HttpHeaderLocation PropertyType.
      */
    public static final PropertyType HTTPHEADERLOCATION = ModelBaseSingleton.findPropertyType( "org.infogrid.model.Web/WebResource_HttpHeaderLocation" );
    public static final org.infogrid.model.primitives.StringDataType HTTPHEADERLOCATION_type = (org.infogrid.model.primitives.StringDataType) HTTPHEADERLOCATION.getDataType();

}
