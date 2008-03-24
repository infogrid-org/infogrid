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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

/**
 * This Exception is thrown if we are trying to apply an operation to an
 * object that is dead already.
 */
public class IsDeadException
    extends
        RuntimeException
{
    /**
     * Constructor.
     *
     * @param obj the dead object
     */
    public IsDeadException(
            Object obj )
    {
        theDeadObject = obj;
    }

    /**
     * Constructor.
     *
     * @param obj the dead object
     * @param msg a text message
     */
    public IsDeadException(
            Object  obj,
            String  msg )
    {
        super( msg );

        theDeadObject = obj;
    }

    /**
     * Obtain the Object that is dead already. Typically, this returns a LiveDeadObject,
     * but this Exception may also be used for Objects that do not support that interface.
     *
     * @return the object that is dead already. This returns null after deserialization.
     */
    public Object getDeadObject()
    {
        return theDeadObject;
    }

    /**
     * Convert to string format, for debugging.
     *
     * @return string format of this instance
     */
    public String toString()
    {
        return super.toString() + ": " + theDeadObject;
    }

    /**
     * The Object that is dead.
     */
    protected transient Object theDeadObject;
}
