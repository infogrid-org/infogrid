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

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract implementation of Scheme that strictly matches against a regular expression.
 * All capturing groups of the provided regex will be turned into their lower-case equivalent
 * in the matchesStrictly method.
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
     * @return non-null if the candidate identifier strictly matches this scheme, in its canonical form
     */
    public String matchesStrictly(
            String context,
            String candidate )
    {
        Matcher m = thePattern.matcher( candidate );
        if( m.matches() ) {
            return toCanonicalForm( candidate, m );
        }
        if( context != null ) {
            String full = context + candidate;
            m = thePattern.matcher( full );
            if( m.matches() ) {
                return toCanonicalForm( full, m );
            }
        }
        return null;
    }

    /**
     * Convert the matched pattern into the canonical form.
     *
     * @param matcher the String that matched the regex
     * @param m the Matcher that matched
     * @return the canonical form
     */
    protected String toCanonicalForm(
            String      matched,
            MatchResult res )
    {
        StringBuilder ret = new StringBuilder();

        int nMatches = res.groupCount();
        int current    = 0;
        for( int i=1 ; i<=nMatches ; ++i ) {
            ret.append( matched.substring( current, res.start( i )));
            ret.append( matched.substring( res.start( i ), res.end( i )).toLowerCase() );
            current = res.end( i );
        }
        ret.append( matched.substring( current ));
        return ret.toString();
    }

    /**
     * The pattern that is being checked strictly.
     */
    protected Pattern thePattern;
}
