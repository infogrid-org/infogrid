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

package org.infogrid.util;

import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * Trivially simple implementation of Identifier using a String.
 */
public class SimpleStringIdentifier
        extends
             AbstractIdentifier
        implements
            Identifier
{
    /**
     * Factory method.
     *
     * @param s the underlying String
     * @return the created SimpleStringIdentifier
     */
    public static SimpleStringIdentifier create(
            String s )
    {
        return new SimpleStringIdentifier( s, s );
    }

    /**
     * Constructor, use factory method instead.
     *
     * @param s the underlying string
     * @param asEnteredByUser String form as entered by the user, if any. This helps with error messages.
     */
    protected SimpleStringIdentifier(
            String s,
            String asEnteredByUser )
    {
        super( asEnteredByUser );

        theString = s;
    }

    /**
     * Obtain an external form for this Identifier, similar to
     * <code>java.net.URL.toExternalForm()</code>.
     *
     * @return external form of this Identifier
     */
    public String toExternalForm()
    {
        return theString;
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String ret = rep.formatEntry(
                    getClass(), // dispatch to the right subtype
                    StringRepresentation.DEFAULT_ENTRY,
                    pars,
                    this );
        return ret;
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return ""; // FIXME?
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return ""; // FIXME?
    }

    /**
     * Compare.
     *
     * @param other the Object to compare to
     * @return true if the Objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof Identifier )) {
            return false;
        }
        if( other == null ) {
            return false;
        }
        Identifier realOther = (Identifier) other;
        if( toExternalForm().equals( realOther.toExternalForm() )) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return toExternalForm().hashCode();
    }

    /**
     * Convert to String, for debugging.
     *
     * @return String format
     */
    @Override
    public String toString()
    {
        return toExternalForm();
    }

    /**
     * The underlying String.
     */
    protected String theString;
}
