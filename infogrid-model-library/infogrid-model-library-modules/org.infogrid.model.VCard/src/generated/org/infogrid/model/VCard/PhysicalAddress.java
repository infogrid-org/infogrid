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
// on Thu, 2015-04-16 22:41:05 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.VCard;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Java interface for EntityType org.infogrid.model.VCard/PhysicalAddress.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>PhysicalAddress</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Physical Address</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>A physical address on a VCard</td></tr></table></td></tr>
  * </table>
  */

public interface PhysicalAddress
        extends
            org.infogrid.model.VCard.Address
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.VCard" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.VCard/PhysicalAddress" );

    /**
      * <p>Set value for property PostOfficeBox.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_PostOfficeBox</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>PostOfficeBox</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Post Office Box</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setPostOfficeBox(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property PostOfficeBox.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_PostOfficeBox</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>PostOfficeBox</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Post Office Box</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getPostOfficeBox()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the PostOfficeBox property.
      */
    public static final String POSTOFFICEBOX_name = "PostOfficeBox";

    /**
      * The PostOfficeBox PropertyType.
      */
    public static final PropertyType POSTOFFICEBOX = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_PostOfficeBox" );
    public static final org.infogrid.model.primitives.StringDataType POSTOFFICEBOX_type = (org.infogrid.model.primitives.StringDataType) POSTOFFICEBOX.getDataType();

    /**
      * <p>Set value for property ExtendedAddress.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_ExtendedAddress</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>ExtendedAddress</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Extended Address</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setExtendedAddress(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property ExtendedAddress.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_ExtendedAddress</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>ExtendedAddress</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Extended Address</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getExtendedAddress()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the ExtendedAddress property.
      */
    public static final String EXTENDEDADDRESS_name = "ExtendedAddress";

    /**
      * The ExtendedAddress PropertyType.
      */
    public static final PropertyType EXTENDEDADDRESS = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_ExtendedAddress" );
    public static final org.infogrid.model.primitives.StringDataType EXTENDEDADDRESS_type = (org.infogrid.model.primitives.StringDataType) EXTENDEDADDRESS.getDataType();

    /**
      * <p>Set value for property StreetAddress.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_StreetAddress</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>StreetAddress</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Street Address</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setStreetAddress(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property StreetAddress.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_StreetAddress</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>StreetAddress</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Street Address</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getStreetAddress()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the StreetAddress property.
      */
    public static final String STREETADDRESS_name = "StreetAddress";

    /**
      * The StreetAddress PropertyType.
      */
    public static final PropertyType STREETADDRESS = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_StreetAddress" );
    public static final org.infogrid.model.primitives.StringDataType STREETADDRESS_type = (org.infogrid.model.primitives.StringDataType) STREETADDRESS.getDataType();

    /**
      * <p>Set value for property Locality.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Locality</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Locality</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Locality</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The locality, e.g. city.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setLocality(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property Locality.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Locality</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Locality</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Locality</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The locality, e.g. city.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getLocality()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Locality property.
      */
    public static final String LOCALITY_name = "Locality";

    /**
      * The Locality PropertyType.
      */
    public static final PropertyType LOCALITY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_Locality" );
    public static final org.infogrid.model.primitives.StringDataType LOCALITY_type = (org.infogrid.model.primitives.StringDataType) LOCALITY.getDataType();

    /**
      * <p>Set value for property Region.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Region</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Region</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Region</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The region, such as state or district.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setRegion(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property Region.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Region</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Region</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Region</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The region, such as state or district.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getRegion()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Region property.
      */
    public static final String REGION_name = "Region";

    /**
      * The Region PropertyType.
      */
    public static final PropertyType REGION = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_Region" );
    public static final org.infogrid.model.primitives.StringDataType REGION_type = (org.infogrid.model.primitives.StringDataType) REGION.getDataType();

    /**
      * <p>Set value for property PostalCode.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_PostalCode</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>PostalCode</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Postal Code</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setPostalCode(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property PostalCode.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_PostalCode</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>PostalCode</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Postal Code</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getPostalCode()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the PostalCode property.
      */
    public static final String POSTALCODE_name = "PostalCode";

    /**
      * The PostalCode PropertyType.
      */
    public static final PropertyType POSTALCODE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_PostalCode" );
    public static final org.infogrid.model.primitives.StringDataType POSTALCODE_type = (org.infogrid.model.primitives.StringDataType) POSTALCODE.getDataType();

    /**
      * <p>Set value for property Country.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Country</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Country</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Country</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setCountry(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property Country.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Country</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Country</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Country</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getCountry()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Country property.
      */
    public static final String COUNTRY_name = "Country";

    /**
      * The Country PropertyType.
      */
    public static final PropertyType COUNTRY = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_Country" );
    public static final org.infogrid.model.primitives.StringDataType COUNTRY_type = (org.infogrid.model.primitives.StringDataType) COUNTRY.getDataType();

    /**
      * <p>Set value for property IsPostal.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_IsPostal</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsPostal</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a postal address?</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Is this a postal address?</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setIsPostal(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property IsPostal.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_IsPostal</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsPostal</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is this a postal address?</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Is this a postal address?</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsPostal()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsPostal property.
      */
    public static final String ISPOSTAL_name = "IsPostal";

    /**
      * The IsPostal PropertyType.
      */
    public static final PropertyType ISPOSTAL = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_IsPostal" );
    public static final org.infogrid.model.primitives.BooleanDataType ISPOSTAL_type = (org.infogrid.model.primitives.BooleanDataType) ISPOSTAL.getDataType();

    /**
      * <p>Set value for property IsParcel.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_IsParcel</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsParcel</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is Postal Address</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Is this a parcel address?</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setIsParcel(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property IsParcel.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_IsParcel</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsParcel</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is Postal Address</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Is this a parcel address?</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsParcel()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsParcel property.
      */
    public static final String ISPARCEL_name = "IsParcel";

    /**
      * The IsParcel PropertyType.
      */
    public static final PropertyType ISPARCEL = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_IsParcel" );
    public static final org.infogrid.model.primitives.BooleanDataType ISPARCEL_type = (org.infogrid.model.primitives.BooleanDataType) ISPARCEL.getDataType();

    /**
      * <p>Set value for property Scope.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Scope</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Scope</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>Is a domestic address</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Scope</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Scope of the address</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setScope(
            org.infogrid.model.primitives.EnumeratedValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property Scope.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/PhysicalAddress_Scope</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Scope</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>Is a domestic address</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Scope</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Scope of the address</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.EnumeratedValue getScope()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Scope property.
      */
    public static final String SCOPE_name = "Scope";

    /**
      * The Scope PropertyType.
      */
    public static final PropertyType SCOPE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/PhysicalAddress_Scope" );
    public static final org.infogrid.model.primitives.EnumeratedDataType SCOPE_type = (org.infogrid.model.primitives.EnumeratedDataType) SCOPE.getDataType();

    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue SCOPE_type_DOM = SCOPE_type.select( "Dom" );
    public static final EnumeratedValue SCOPE_type_INTL = SCOPE_type.select( "Intl" );
}
