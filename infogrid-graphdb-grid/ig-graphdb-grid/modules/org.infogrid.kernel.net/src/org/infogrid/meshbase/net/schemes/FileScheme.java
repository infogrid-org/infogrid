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
 * Represents the "file" protocol for the DefaultNetMeshBaseIdentifierFactory.
 */
public class FileScheme
        extends
            AbstractRegexScheme
        implements
            Scheme
{
    private static final Log log = Log.getLogInstance( FileScheme.class ); // our own, private logger

    /**
     * Constructor.
     */
    public FileScheme()
    {
        super( "file", Pattern.compile( "file:/.*" ));
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
            String actual = matchesStrictly( context, candidate );
            if( actual != null ) {
                actual = HttpScheme.stripDirectoryPaths( actual );

                return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
            }
            String full = "file:" + candidate;
            actual = matchesStrictly( context, full );
            if( actual != null ) {
                actual = HttpScheme.stripDirectoryPaths( actual );

                return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
            }

        } catch( URISyntaxException ex ) {
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
        }
        return null;
    }
}
