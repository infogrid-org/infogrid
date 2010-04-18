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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.account.store;

/**
 * Collects the XML tags for the SimpleLidAccount mapping.
 */
public interface SimpleLidAccountTags
{
    /** Top-level tag representing a LidAccount. */
    public static final String ACCOUNT_TAG = "account";

    /** Tag for an identifier. */
    public static final String IDENTIFIER_TAG = "id";

    /** Tag for an identity attribute. */
    public static final String ATTRIBUTE_TAG = "attribute";
    
    /** Tag for a credential. */
    public static final String CREDENTIAL_TAG = "credential";

    /** Tag for a group identifier. */
    public static final String GROUP_TAG = "group";
}
