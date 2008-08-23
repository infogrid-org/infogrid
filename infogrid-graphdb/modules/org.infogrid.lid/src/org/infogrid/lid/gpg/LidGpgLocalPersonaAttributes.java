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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.gpg;

/**
 * Collects the definitions of attribute keys, by LID GPG support, for LidLocalPersona
 * attrbutes.
 */
public interface LidGpgLocalPersonaAttributes
{
    /**
     * The LID GPG public key attribute.
     */
    public static final String LID_GPG_PERSONA_PUBLIC_KEY_ATTRIBUTE = "gpg --export --armor";

    /**
     * The LID GPG private key attribute.
     */
    public static final String LID_GPG_PERSONA_PRIVATE_KEY_ATTRIBUTE = "gpg --export-secret-keys --armor";
}

