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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.schemes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.model.primitives.StringDataType;

/**
 * Represents HTTPS for the DefaultNetMeshBaseIdentifierFactory.
 */
public class HttpsScheme
        extends
            HttpScheme
{
    /**
     * Constructor.
     */
    public HttpsScheme()
    {
        super( "https", StringDataType.theHttpsUrlType.getRegex() );
    }

    /**
     * Helper method to remove an unnecessary port specification.
     *
     * @param candidate the candidate String
     * @return the candidate, with any unnecessary port specification removed
     */
    @Override
    protected String removeUnnecessaryPort(
            String candidate )
    {
        String ret;

        Matcher m = thePort443Pattern.matcher( candidate );
        if( m.matches() ) {
            ret = m.group( 1 ) + m.group( 2 );
        } else {
            ret = candidate;
        }
        return ret;
    }

    /**
     * The pattern that allows us to remove a unnecessary port 443 from a URL spec.
     */
    public static final Pattern thePort443Pattern = Pattern.compile(
            "^(https://[^/:]+):443(/.*)$" );

}
