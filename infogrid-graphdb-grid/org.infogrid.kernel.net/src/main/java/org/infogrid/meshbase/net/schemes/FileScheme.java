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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.schemes;

import java.util.regex.Pattern;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;

/**
 * Represents the "file" protocol for the DefaultNetMeshBaseIdentifierFactory.
 */
public class FileScheme
        extends
            AbstractRegexScheme
        implements
            Scheme
{
    /**
     * Constructor.
     */
    public FileScheme()
    {
        super( "file", FILE_PATTERN );
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
        NetMeshBaseIdentifier ret = strictlyMatchAndCreate( context, candidate, fact );
        if( ret != null ) {
            return ret;
        }

        String full = "file:" + candidate;
        ret = strictlyMatchAndCreate( context, full, fact );
        if( ret != null ) {
            return ret;
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
        return true;
    }

    /**
     * Convert a valid candidate to its canonical form. For example, remove redundant "foo/../"
     *
     * @param candidate the valid candidate
     * @return the canonical form
     */
    @Override
    protected String canonisize(
            String candidate )
    {
        return HttpScheme.stripDirectoryPaths( candidate );
    }

    /**
     * The Pattern that we use for this scheme.
     */
    public static final Pattern FILE_PATTERN = Pattern.compile( "((?i:file)):/.*" );
}
