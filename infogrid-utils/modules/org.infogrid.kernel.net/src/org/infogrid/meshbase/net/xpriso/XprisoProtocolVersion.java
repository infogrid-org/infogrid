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

package org.infogrid.meshbase.net.xpriso;

import java.io.Serializable;

/**
 * This represents a version of the XPRISO protocol.
 *
 * Currently only one version is supported.
 */
public class XprisoProtocolVersion
        implements
            Serializable
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * The currently only defined protocol version.
     */
    public static final XprisoProtocolVersion VERSION_ONE = new XprisoProtocolVersion( "V1" );

    /**
     * Private constructor to keep a closed list.
     * 
     * @param versionId the version id
     */
    private XprisoProtocolVersion(
            String versionId )
    {
        theId = versionId;
    }

    /**
     * Comparison.
     *
     * @param other the Object to compare to
     * @return true if these are the same Objects
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof XprisoProtocolVersion ) {
            XprisoProtocolVersion realOther = (XprisoProtocolVersion) other;

            return theId.equals( realOther.theId );
        }
        return false;
    }

    /**
     * Hash code is the same as the version's hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return theId.hashCode();
    }

    /**
     * The actual version in string form.
     */
    protected String theId;
}
