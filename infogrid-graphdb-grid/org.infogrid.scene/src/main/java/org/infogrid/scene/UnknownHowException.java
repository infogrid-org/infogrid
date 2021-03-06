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

package org.infogrid.scene;

/**
 * Thrown if an operation cannot be performed on a Scene because
 * the Scene implementation does not know how to.
 */
public class UnknownHowException
    extends
        RuntimeException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Pass-through constructor.
     */
    public UnknownHowException()
    {
        super();
    }

    /**
     * Pass-through constructor.
     *
     * @param s a message
     */
    public UnknownHowException(
            String s )
    {
        super( s );
    }
}
