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

package org.infogrid.util;

/**
 * Makes implementing subtypes of Identifier just a tiny bit easier.
 */
public abstract class AbstractIdentifier
    implements
        Identifier
{
    /**
     * Constructor, for subclasses only.
     *
     * @param asEntered the String that the user entered, if any
     */
    protected AbstractIdentifier(
            String asEntered )
    {
        theAsEntered = asEntered; // may be null, user may not have entered anything
    }

    /**
     * Determine the String form of this object as entered by the user, if any.
     * This may be null if the user did not enter anything, or it is not known.
     *
     * @return String form, as entered by the user, if any
     */
    @Override
    public String getAsEntered()
    {
        return theAsEntered;
    }

    /**
     * Obtain an external form for this Identifier, similar to
     * <code>java.net.URL.toExternalForm()</code>. This is provided
     * to make invocation from JSPs easier.
     *
     * @return external form of this Identifier
     */
    @Override
    public final String getExternalForm()
    {
        return toExternalForm();
    }

    /**
     * Obtain a colloquial external form for this Identifier.
     * This may be overridden by subclasses.
     *
     * @return colloquial external form of this Identifier
     */
    @Override
    public String toColloquialExternalForm()
    {
        String ret = toExternalForm();

        for( String current : COLLOQUIAL_REMOVED_PREFIXES ) {
            if( ret.startsWith( current )) {
                ret = ret.substring( current.length() );
                break; // there won't be more than one
            }
        }
        // remove last slash if there isn't any other
        if( ret.indexOf( '/' ) == ret.length()-1 ) {
            ret = ret.substring( 0, ret.length()-1 );
        }
        return ret;
    }

    /**
     * Obtain a colloquial external form for this Identifier.
     * This is provided to make invocation from JSPs easier.
     *
     * @return colloquial external form of this Identifier
     */
    @Override
    public final String getColloquialExternalForm()
    {
        return toColloquialExternalForm();
    }

    /**
     * Determine whether the provided identifier is higher up in
     * the name space as this identifier. This is equivalent to
     * toExternalForm().startsWith( other.toExternalForm().
     *
     * @param other the identifier to test against
     * @return true if this provided identifier is higher up in the name space
     */
    @Override
    public boolean startsWith(
            Identifier other )
    {
        // subclasses might be able to be more efficient
        return toExternalForm().startsWith( other.getExternalForm() );
    }

    /**
     * The String entered by the user, if any
     */
    protected String theAsEntered;

    /**
     * The prefixes to strip for colloquial external forms.
     */
    private static final String [] COLLOQUIAL_REMOVED_PREFIXES
            = ResourceHelper.getInstance( AbstractIdentifier.class ).getResourceStringArray( "ColloquialRemovedPrefixes" );
}
