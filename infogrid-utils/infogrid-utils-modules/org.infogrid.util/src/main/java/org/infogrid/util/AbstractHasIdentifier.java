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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

/**
 * Collects features common to implementations of HasIdentifier.
 */
public abstract class AbstractHasIdentifier
        implements
            HasIdentifier
{
    /**
     * Constructor for subclasses only.
     * 
     * @param identifier the unique identifier
     */
    protected AbstractHasIdentifier(
            Identifier identifier )
    {
        theIdentifier = identifier;
    }

    /**
     * Obtain the unique identifier.
     * 
     * @return the unique identifier
     */
    public Identifier getIdentifier()
    {
        return theIdentifier;
    }

    /**
     * Determine whether this object is being identified with the provided Identifier.
     * This is a useful method for those objects of type HasIdentifier that may listen
     * to multiple names.
     *
     * @param toTest the Identifier to test against
     * @return true if this HasIdentifier is being identified by the provided Identifier
     */
    public boolean isIdentifiedBy(
            Identifier toTest )
    {
        return theIdentifier.equals( toTest );
    }

    /**
     * The unique identifier.
     */
    protected Identifier theIdentifier;
}
