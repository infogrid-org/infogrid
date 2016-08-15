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

import org.infogrid.util.text.HasStringRepresentation;

/**
 * An abstract interface to capture the semantics of identifiers.
 */
public interface Identifier
        extends
            HasStringRepresentation,
            AsEntered
{
    /**
     * Obtain an external form for this Identifier, similar to
     * <code>java.net.URL.toExternalForm()</code>.
     *
     * @return external form of this Identifier
     */
    public abstract String toExternalForm();

    /**
     * Obtain an external form for this Identifier, similar to
     * <code>java.net.URL.toExternalForm()</code>. This is provided
     * to make invocation from JSPs easier.
     *
     * @return external form of this Identifier
     */
    public abstract String getExternalForm();

    /**
     * Obtain a colloquial external form for this Identifier.
     *
     * @return colloquial external form of this Identifier
     */
    public abstract String toColloquialExternalForm();

    /**
     * Obtain a colloquial external form for this Identifier.
     * This is provided to make invocation from JSPs easier.
     *
     * @return colloquial external form of this Identifier
     */
    public abstract String getColloquialExternalForm();

    /**
     * Determine whether the provided identifier is higher up in
     * the name space as this identifier. This is equivalent to
     * toExternalForm().startsWith( other.toExternalForm().
     *
     * @param other the identifier to test against
     * @return true if this provided identifier is higher up in the name space
     */
    public abstract boolean startsWith(
            Identifier other );
}
