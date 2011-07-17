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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.util.logging.Log;

/**
 * Represents the "acct" protocol for the DefaultNetMeshBaseIdentifierFactory.
 */
public class AcctScheme
        extends
            AbstractRegexScheme
        implements
            Scheme
{
    private static final Log log = Log.getLogInstance( AcctScheme.class ); // our own, private logger

    /**
     * Constructor.
     */
    public AcctScheme()
    {
        super( "acct", ACCT_PATTERN );
    }

    /**
     * Attempt to convert this candidate identifier String into an identifier with this
     * scheme, taking creative license if needed. If successful, return the identifier,
     * null otherwise.
     *
     * @param context the identifier root that forms the context
     * @param candidate the candidate identifier
     * @param fact the NetMeshBaseIdentifierFactory on whose behalf we create this NetMeshBaseIdentifier
     * @return the successfully created identifier, or null otherwise
     */
    public NetMeshBaseIdentifier guessAndCreate(
            String                       context,
            String                       candidate,
            NetMeshBaseIdentifierFactory fact )
    {
        try {
            if( thePattern.matcher( candidate ).matches() ) {
                return new NetMeshBaseIdentifier( fact, candidate, new URI( candidate ), candidate, false );

            }
            String tryThis = "acct:" + candidate.toLowerCase(); // might as well turn it lower-case here
            
            if( thePattern.matcher( tryThis ).matches() ) {
                return new NetMeshBaseIdentifier( fact, tryThis, new URI( tryThis ), candidate, false );
            }

        } catch( URISyntaxException ex ) {
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
        }
        return null;
    }

    /**
     * Determine whether this Scheme is restful.
     *
     * @return true if the Scheme is restful
     */
    public boolean isRestful()
    {
        return false;
    }

    /**
     * The Pattern that we use for this scheme.
     */
    public static final Pattern ACCT_PATTERN = Pattern.compile( "(acct:[A-Z0-9._%+-]+@[A-Z0-9.-]*[A-Z])", Pattern.CASE_INSENSITIVE );
}
