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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
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
    public final String getExternalForm()
    {
        return toExternalForm();
    }

    /**
     * The String entered by the user, if any
     */
    protected String theAsEntered;
}
