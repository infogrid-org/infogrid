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
 * Represents a generic RegexScheme that is strict about guessing for the DefaultNetMeshBaseIdentifierFactory.
 */
public class StrictRegexScheme
        extends
            AbstractRegexScheme
        implements
            Scheme
{
    /**
     * Constructor for a non-RESTful Scheme.
     *
     * @param protocolName the name of the protocol, e.g. "foobar"
     * @param regex the pattern to check strictly
     */
    public StrictRegexScheme(
            String  protocolName,
            Pattern regex )
    {
        super( protocolName, regex );

        theIsRestful = false;
    }

    /**
     * Constructor.
     *
     * @param protocolName the name of the protocol, e.g. "foobar"
     * @param regex the pattern to check strictly
     * @param isRestful if true, the Scheme is REST-ful
     */
    public StrictRegexScheme(
            String  protocolName,
            Pattern regex,
            boolean isRestful )
    {
        super( protocolName, regex );

        theIsRestful = isRestful;
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
        return ret;
    }

    /**
     * Determine whether this Scheme is restful.
     *
     * @return true if the Scheme is restful
     */
    public final boolean isRestful()
    {
        return theIsRestful;
    }

    /**
     * Is the Scheme REST-ful.
     */
    protected boolean theIsRestful;
}
