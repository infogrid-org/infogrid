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
 * Interface supported by those objects that are directly related to user input.
 * For example, Identifier supports AsEntered. The method here allows us to
 * determine what the user entered that led to the object. For example,
 * the Identifier http://infogrid.org/ may have an as-entered of "infogrid.org".
 * This interface is particularly helpful for error reporting to the user as
 * the user will only remember what they entered, not what InfoGrid turned it
 * into.
 */
public interface AsEntered
{
    /**
     * Determine the String form of this object as entered by the user, if any.
     * This may be null if the user did not enter anything, or it is not known.
     *
     * @return String form, as entered by the user, if any
     */
    public abstract String getAsEntered();
}
