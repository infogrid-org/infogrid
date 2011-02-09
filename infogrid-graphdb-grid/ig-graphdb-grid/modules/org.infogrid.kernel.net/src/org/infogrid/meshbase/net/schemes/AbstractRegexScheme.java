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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;

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
     * Parse a candidate identifier String and create a NetMeshBaseIdentifier. Use strict checking.
     *
     * @param context the identifier root that forms the context
     * @param candidate the candidate identifier
     * @param fact the NetMeshBaseIdentifierFactory on whose behalf we create this NetMeshBaseIdentifier
     * @return the successfully created identifier, or null otherwise
     */
    public NetMeshBaseIdentifier strictlyMatchAndCreate(
            String                       context,
            String                       candidate,
            NetMeshBaseIdentifierFactory fact )
    {
        try {
            Matcher m = thePattern.matcher( candidate );
            if( m.matches() ) {
                String fromRegex = useRegexForCanonicalForm( candidate, m );
                String canonical = canonisize( fromRegex );
                return new NetMeshBaseIdentifier( fact, canonical, new URI( canonical ), candidate, isRestful() );
            }
            if( context != null ) {
                String full = context + candidate;
                m = thePattern.matcher( full );
                if( m.matches() ) {
                    String fromRegex = useRegexForCanonicalForm( full, m );
                    String canonical = canonisize( fromRegex );
                    return new NetMeshBaseIdentifier( fact, canonical, new URI( canonical ), candidate, isRestful() );
                }
            }
        } catch( URISyntaxException ex ) {
            // ignore
        }
        return null;
    }

    /**
     * Convert the matched pattern into the canonical form.
     *
     * @param matched the String that matched the regex
     * @param res the MatchResult that matched
     * @return the canonical form
     */
    protected String useRegexForCanonicalForm(
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
     * Convert a valid candidate to its canonical form. For example, remove redundant "foo/../"
     *
     * @param candidate the valid candidate
     * @return the canonical form
     */
    protected String canonisize(
            String candidate )
    {
        return candidate;
    }

    /**
     * The pattern that is being checked strictly.
     */
    protected Pattern thePattern;
}
