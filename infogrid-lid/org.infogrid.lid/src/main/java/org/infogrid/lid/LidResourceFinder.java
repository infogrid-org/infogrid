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

package org.infogrid.lid;

import java.text.ParseException;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.HasIdentifierFinder;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.http.SaneRequest;

/**
 * Knows how to find requested resources.
 */
public interface LidResourceFinder
        extends
            HasIdentifierFinder
{
    /**
     * Find the resource requested by this incoming request.
     *
     * @param request the incoming request
     * @return the found resource
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     * @throws ParseException thrown if the identifier in the request could not be parsed
     */
    public HasIdentifier findFromRequest(
            SaneRequest request )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException,
            ParseException;

    /**
     * Obtain the IdentifierFactory used by this LidResourceFinder.
     *
     * @return the IdentifierFactory
     */
    public IdentifierFactory getIdentifierFactory();
}
