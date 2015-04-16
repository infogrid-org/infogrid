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
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class VCardSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.VCard" );

    /**
      * An electronic business card
      */
    public static final EntityType VCARD = org.infogrid.model.VCard.VCard._TYPE;

    /**
      * The full name of the Person represented by the VCard.
      */
    public static final PropertyType VCARD_FULLNAME = org.infogrid.model.VCard.VCard.FULLNAME;
    public static final org.infogrid.model.primitives.StringDataType VCARD_FULLNAME_type = (org.infogrid.model.primitives.StringDataType) VCARD_FULLNAME.getDataType();

    public static final PropertyType VCARD_FAMILYNAME = org.infogrid.model.VCard.VCard.FAMILYNAME;
    public static final org.infogrid.model.primitives.StringDataType VCARD_FAMILYNAME_type = (org.infogrid.model.primitives.StringDataType) VCARD_FAMILYNAME.getDataType();

    public static final PropertyType VCARD_GIVENNAME = org.infogrid.model.VCard.VCard.GIVENNAME;
    public static final org.infogrid.model.primitives.StringDataType VCARD_GIVENNAME_type = (org.infogrid.model.primitives.StringDataType) VCARD_GIVENNAME.getDataType();

    public static final PropertyType VCARD_ADDITIONALNAMES = org.infogrid.model.VCard.VCard.ADDITIONALNAMES;
    public static final org.infogrid.model.primitives.StringDataType VCARD_ADDITIONALNAMES_type = (org.infogrid.model.primitives.StringDataType) VCARD_ADDITIONALNAMES.getDataType();

    public static final PropertyType VCARD_HONORIFICPREFIXES = org.infogrid.model.VCard.VCard.HONORIFICPREFIXES;
    public static final org.infogrid.model.primitives.StringDataType VCARD_HONORIFICPREFIXES_type = (org.infogrid.model.primitives.StringDataType) VCARD_HONORIFICPREFIXES.getDataType();

    public static final PropertyType VCARD_HONORIFICSUFFIXES = org.infogrid.model.VCard.VCard.HONORIFICSUFFIXES;
    public static final org.infogrid.model.primitives.StringDataType VCARD_HONORIFICSUFFIXES_type = (org.infogrid.model.primitives.StringDataType) VCARD_HONORIFICSUFFIXES.getDataType();

    public static final PropertyType VCARD_NICKNAME = org.infogrid.model.VCard.VCard.NICKNAME;
    public static final org.infogrid.model.primitives.StringDataType VCARD_NICKNAME_type = (org.infogrid.model.primitives.StringDataType) VCARD_NICKNAME.getDataType();

    public static final PropertyType VCARD_NOTE = org.infogrid.model.VCard.VCard.NOTE;
    public static final org.infogrid.model.primitives.StringDataType VCARD_NOTE_type = (org.infogrid.model.primitives.StringDataType) VCARD_NOTE.getDataType();

    /**
      * An address on a VCard
      */
    public static final EntityType ADDRESS = org.infogrid.model.VCard.Address._TYPE;

    public static final PropertyType ADDRESS_ISPREFERRED = org.infogrid.model.VCard.Address.ISPREFERRED;
    public static final org.infogrid.model.primitives.BooleanDataType ADDRESS_ISPREFERRED_type = (org.infogrid.model.primitives.BooleanDataType) ADDRESS_ISPREFERRED.getDataType();

    public static final PropertyType ADDRESS_ISHOME = org.infogrid.model.VCard.Address.ISHOME;
    public static final org.infogrid.model.primitives.BooleanDataType ADDRESS_ISHOME_type = (org.infogrid.model.primitives.BooleanDataType) ADDRESS_ISHOME.getDataType();

    public static final PropertyType ADDRESS_ISWORK = org.infogrid.model.VCard.Address.ISWORK;
    public static final org.infogrid.model.primitives.BooleanDataType ADDRESS_ISWORK_type = (org.infogrid.model.primitives.BooleanDataType) ADDRESS_ISWORK.getDataType();

    /**
      * A physical address on a VCard
      */
    public static final EntityType PHYSICALADDRESS = org.infogrid.model.VCard.PhysicalAddress._TYPE;

    public static final PropertyType PHYSICALADDRESS_POSTOFFICEBOX = org.infogrid.model.VCard.PhysicalAddress.POSTOFFICEBOX;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_POSTOFFICEBOX_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_POSTOFFICEBOX.getDataType();

    public static final PropertyType PHYSICALADDRESS_EXTENDEDADDRESS = org.infogrid.model.VCard.PhysicalAddress.EXTENDEDADDRESS;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_EXTENDEDADDRESS_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_EXTENDEDADDRESS.getDataType();

    public static final PropertyType PHYSICALADDRESS_STREETADDRESS = org.infogrid.model.VCard.PhysicalAddress.STREETADDRESS;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_STREETADDRESS_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_STREETADDRESS.getDataType();

    /**
      * The locality, e.g. city.
      */
    public static final PropertyType PHYSICALADDRESS_LOCALITY = org.infogrid.model.VCard.PhysicalAddress.LOCALITY;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_LOCALITY_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_LOCALITY.getDataType();

    /**
      * The region, such as state or district.
      */
    public static final PropertyType PHYSICALADDRESS_REGION = org.infogrid.model.VCard.PhysicalAddress.REGION;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_REGION_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_REGION.getDataType();

    public static final PropertyType PHYSICALADDRESS_POSTALCODE = org.infogrid.model.VCard.PhysicalAddress.POSTALCODE;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_POSTALCODE_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_POSTALCODE.getDataType();

    public static final PropertyType PHYSICALADDRESS_COUNTRY = org.infogrid.model.VCard.PhysicalAddress.COUNTRY;
    public static final org.infogrid.model.primitives.StringDataType PHYSICALADDRESS_COUNTRY_type = (org.infogrid.model.primitives.StringDataType) PHYSICALADDRESS_COUNTRY.getDataType();

    /**
      * Is this a postal address?
      */
    public static final PropertyType PHYSICALADDRESS_ISPOSTAL = org.infogrid.model.VCard.PhysicalAddress.ISPOSTAL;
    public static final org.infogrid.model.primitives.BooleanDataType PHYSICALADDRESS_ISPOSTAL_type = (org.infogrid.model.primitives.BooleanDataType) PHYSICALADDRESS_ISPOSTAL.getDataType();

    /**
      * Is this a parcel address?
      */
    public static final PropertyType PHYSICALADDRESS_ISPARCEL = org.infogrid.model.VCard.PhysicalAddress.ISPARCEL;
    public static final org.infogrid.model.primitives.BooleanDataType PHYSICALADDRESS_ISPARCEL_type = (org.infogrid.model.primitives.BooleanDataType) PHYSICALADDRESS_ISPARCEL.getDataType();

    /**
      * Scope of the address
      */
    public static final PropertyType PHYSICALADDRESS_SCOPE = org.infogrid.model.VCard.PhysicalAddress.SCOPE;
    public static final org.infogrid.model.primitives.EnumeratedDataType PHYSICALADDRESS_SCOPE_type = (org.infogrid.model.primitives.EnumeratedDataType) PHYSICALADDRESS_SCOPE.getDataType();
    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue PHYSICALADDRESS_SCOPE_type_DOM = PHYSICALADDRESS_SCOPE_type.select( "Dom" );
    public static final EnumeratedValue PHYSICALADDRESS_SCOPE_type_INTL = PHYSICALADDRESS_SCOPE_type.select( "Intl" );

    /**
      * A communications address on a VCard, such as a phone number or e-mail address.
      */
    public static final EntityType COMMUNICATIONADDRESS = org.infogrid.model.VCard.CommunicationAddress._TYPE;

    /**
      * The address handle, such as foo@bar.com, or number, such as (408) 123-1234.
      */
    public static final PropertyType COMMUNICATIONADDRESS_HANDLE = org.infogrid.model.VCard.CommunicationAddress.HANDLE;
    public static final org.infogrid.model.primitives.StringDataType COMMUNICATIONADDRESS_HANDLE_type = (org.infogrid.model.primitives.StringDataType) COMMUNICATIONADDRESS_HANDLE.getDataType();

    /**
      * This reflects the type of address, such as whether this is a fax number\n         or an instant messaging handle.
      */
    public static final PropertyType COMMUNICATIONADDRESS_TYPE = org.infogrid.model.VCard.CommunicationAddress.TYPE;
    public static final org.infogrid.model.primitives.EnumeratedDataType COMMUNICATIONADDRESS_TYPE_type = (org.infogrid.model.primitives.EnumeratedDataType) COMMUNICATIONADDRESS_TYPE.getDataType();
    /* Values of the EnumeratedDataType. */
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_VOICE = COMMUNICATIONADDRESS_TYPE_type.select( "Voice" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_MOBILE = COMMUNICATIONADDRESS_TYPE_type.select( "Mobile" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_FAX = COMMUNICATIONADDRESS_TYPE_type.select( "Fax" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_VIDEO = COMMUNICATIONADDRESS_TYPE_type.select( "Video" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_PAGER = COMMUNICATIONADDRESS_TYPE_type.select( "Pager" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_BBS = COMMUNICATIONADDRESS_TYPE_type.select( "Bbs" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_MODEM = COMMUNICATIONADDRESS_TYPE_type.select( "Modem" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_CAR = COMMUNICATIONADDRESS_TYPE_type.select( "Car" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_ISDN = COMMUNICATIONADDRESS_TYPE_type.select( "ISDN" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_PCS = COMMUNICATIONADDRESS_TYPE_type.select( "PCS" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_EMAIL = COMMUNICATIONADDRESS_TYPE_type.select( "Email" );
    public static final EnumeratedValue COMMUNICATIONADDRESS_TYPE_type_IM = COMMUNICATIONADDRESS_TYPE_type.select( "IM" );

    /**
      * Does this CommunicationAddress support leaving messages even if the user is not available.
      */
    public static final PropertyType COMMUNICATIONADDRESS_HASMESSAGEBOX = org.infogrid.model.VCard.CommunicationAddress.HASMESSAGEBOX;
    public static final org.infogrid.model.primitives.BooleanDataType COMMUNICATIONADDRESS_HASMESSAGEBOX_type = (org.infogrid.model.primitives.BooleanDataType) COMMUNICATIONADDRESS_HASMESSAGEBOX.getDataType();

    public static final PropertyType COMMUNICATIONADDRESS_ISVOICE = org.infogrid.model.VCard.CommunicationAddress.ISVOICE;
    public static final org.infogrid.model.primitives.BooleanDataType COMMUNICATIONADDRESS_ISVOICE_type = (org.infogrid.model.primitives.BooleanDataType) COMMUNICATIONADDRESS_ISVOICE.getDataType();

    public static final RelationshipType VCARD_SHOWS_ADDRESS = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.VCard/VCard_Shows_Address" );

}
