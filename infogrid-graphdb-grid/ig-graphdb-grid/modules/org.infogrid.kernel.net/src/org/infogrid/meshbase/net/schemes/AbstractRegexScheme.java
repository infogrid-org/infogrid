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

import java.util.regex.Pattern;

/**
 * Abstract implementation of Scheme that strictly matches against a regular expression.
 */
public abstract class AbstractRegexScheme
        extends
            AbstractScheme
        implements
            Scheme
{
    /**
     * Constructor for subclasses only.
     *
     * @param name the name of this Scheme
     * @param regex the regular expression to strictly check against
     */
    protected AbstractRegexScheme(
            String  name,
            Pattern regex )
    {
        super( name );

        thePattern = regex;
    }

    /**
     * Detect whether a candidate identifier String strictly matches this scheme.
     *
     * @param context the identifier root that forms the context
     * @param candidate the candidate identifier
     * @return non-null if the candidate identifier strictly matches this scheme, in the absolute form
     */
    public String matchesStrictly(
            String context,
            String candidate )
    {
        if( thePattern.matcher( candidate ).matches() ) {
            return candidate;
        }

        String full = context + candidate;
        if( context != null && thePattern.matcher( full ).matches() ) {
            return full;
        }

        return null;
    }

    /**
     * The pattern that is being checked strictly.
     */
    protected Pattern thePattern;
}
