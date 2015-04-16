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
  * <p>Java interface for EntityType org.infogrid.model.VCard/CommunicationAddress.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>CommunicationAddress</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>false</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Physical Address</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>A communications address on a VCard, such as a phone number or e-mail address.</td></tr></table></td></tr>
  * </table>
  */

public interface CommunicationAddress
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
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.VCard/CommunicationAddress" );

    /**
      * <p>Set value for property Handle.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_Handle</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Handle</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Address Handle</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The address handle, such as foo@bar.com, or number, such as (408) 123-1234.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
      */
    public abstract void setHandle(
            org.infogrid.model.primitives.StringValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException;

    /**
      * <p>Obtain value for property Handle.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_Handle</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Handle</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>String</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>null</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Address Handle</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>The address handle, such as foo@bar.com, or number, such as (408) 123-1234.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.StringValue getHandle()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Handle property.
      */
    public static final String HANDLE_name = "Handle";

    /**
      * The Handle PropertyType.
      */
    public static final PropertyType HANDLE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/CommunicationAddress_Handle" );
    public static final org.infogrid.model.primitives.StringDataType HANDLE_type = (org.infogrid.model.primitives.StringDataType) HANDLE.getDataType();

    /**
      * <p>Set value for property Type.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_Type</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Type</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>Is a voice phone number</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Address Type</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>This reflects the type of address, such as whether this is a fax number\n         or an instant messaging handle.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setType(
            org.infogrid.model.primitives.EnumeratedValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property Type.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_Type</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>Type</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Enumeration</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt>Is a voice phone number</tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>true</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Address Type</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>This reflects the type of address, such as whether this is a fax number\n         or an instant messaging handle.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.EnumeratedValue getType()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the Type property.
      */
    public static final String TYPE_name = "Type";

    /**
      * The Type PropertyType.
      */
    public static final PropertyType TYPE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/CommunicationAddress_Type" );
    public static final org.infogrid.model.primitives.EnumeratedDataType TYPE_type = (org.infogrid.model.primitives.EnumeratedDataType) TYPE.getDataType();

    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue TYPE_type_VOICE = TYPE_type.select( "Voice" );
    public static final EnumeratedValue TYPE_type_MOBILE = TYPE_type.select( "Mobile" );
    public static final EnumeratedValue TYPE_type_FAX = TYPE_type.select( "Fax" );
    public static final EnumeratedValue TYPE_type_VIDEO = TYPE_type.select( "Video" );
    public static final EnumeratedValue TYPE_type_PAGER = TYPE_type.select( "Pager" );
    public static final EnumeratedValue TYPE_type_BBS = TYPE_type.select( "Bbs" );
    public static final EnumeratedValue TYPE_type_MODEM = TYPE_type.select( "Modem" );
    public static final EnumeratedValue TYPE_type_CAR = TYPE_type.select( "Car" );
    public static final EnumeratedValue TYPE_type_ISDN = TYPE_type.select( "ISDN" );
    public static final EnumeratedValue TYPE_type_PCS = TYPE_type.select( "PCS" );
    public static final EnumeratedValue TYPE_type_EMAIL = TYPE_type.select( "Email" );
    public static final EnumeratedValue TYPE_type_IM = TYPE_type.select( "IM" );
    /**
      * <p>Set value for property HasMessageBox.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_HasMessageBox</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HasMessageBox</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Has Message Box</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Does this CommunicationAddress support leaving messages even if the user is not available.</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setHasMessageBox(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property HasMessageBox.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_HasMessageBox</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>HasMessageBox</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Has Message Box</td></tr></table></td></tr>
      *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>Does this CommunicationAddress support leaving messages even if the user is not available.</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getHasMessageBox()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the HasMessageBox property.
      */
    public static final String HASMESSAGEBOX_name = "HasMessageBox";

    /**
      * The HasMessageBox PropertyType.
      */
    public static final PropertyType HASMESSAGEBOX = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/CommunicationAddress_HasMessageBox" );
    public static final org.infogrid.model.primitives.BooleanDataType HASMESSAGEBOX_type = (org.infogrid.model.primitives.BooleanDataType) HASMESSAGEBOX.getDataType();

    /**
      * <p>Set value for property IsVoice.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_IsVoice</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsVoice</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is voice</td></tr></table></td></tr>
      * </table>
      *
       * @param newValue the new value for this property
       * @throws NotPermittedException thrown if this caller was not allowed to perform this operation
       * @throws TransactionException thrown if this operation is invoked from outside of a Transaction
       * @throws IllegalPropertyValueException thrown if it is attempted to assign the <code>null</code> value to this non-optional property
      */
    public abstract void setIsVoice(
            org.infogrid.model.primitives.BooleanValue newValue )
        throws
            org.infogrid.mesh.NotPermittedException,
            org.infogrid.meshbase.transaction.TransactionException,
            org.infogrid.mesh.IllegalPropertyValueException;

    /**
      * <p>Obtain value for property IsVoice.</p>
      *
      * <table>
      *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.VCard/CommunicationAddress_IsVoice</tt></td></tr>
      *  <tr><td>Name:</td><td><tt>IsVoice</tt></td></tr>
      *  <tr><td>DataType:</td><td><tt>Boolean</tt></td></tr>
      *  <tr><td>DefaultValue:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsOptional:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>IsReadOnly:</td><td><tt><code>false</code></tt></td></tr>
      *  <tr><td>SequenceNumber:</td><td><tt>0.0</tt></td></tr>
      *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Is voice</td></tr></table></td></tr>
      * </table>
      *
      * @return the current value of the property
      * @throws NotPermittedException thrown if this caller was not permitted to perform this operation
      */
    public abstract org.infogrid.model.primitives.BooleanValue getIsVoice()
        throws
            org.infogrid.mesh.NotPermittedException;

    /**
      * Name of the IsVoice property.
      */
    public static final String ISVOICE_name = "IsVoice";

    /**
      * The IsVoice PropertyType.
      */
    public static final PropertyType ISVOICE = ModelBaseSingleton.findPropertyType( "org.infogrid.model.VCard/CommunicationAddress_IsVoice" );
    public static final org.infogrid.model.primitives.BooleanDataType ISVOICE_type = (org.infogrid.model.primitives.BooleanDataType) ISVOICE.getDataType();

}
