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

package org.infogrid.modelbase.m;

import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationContext;

/**
 * MeshTypeIdentifier implementation for MModelBase.
 */
public class MMeshTypeIdentifier
        implements
             MeshTypeIdentifier
{
    /**
     * Factory method.
     *
     * @param s the String
     * @return the created MMeshTypeIdentifier
     */
    public static MMeshTypeIdentifier create(
            String s )
    {
        return new MMeshTypeIdentifier( s );
    }

    /**
     * Constructor.
     *
     * @param s the String
     */
    protected MMeshTypeIdentifier(
            String s )
    {
        theString = s;
    }
    
    /**
     * Convert to external form.
     *
     * @return the external form
     */
    public String toExternalForm()
    {
        return theString;
    }

    /**
     * Create a derived MeshTypeIdentifier.
     *
     * @param suffix the suffix to append
     * @return the created MeshTypeIdentifier
     */
    public MeshTypeIdentifier createDerivedMeshTypeIdentifier(
            String suffix )
    {
        return new MMeshTypeIdentifier( theString + suffix );
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @param maxLength maximum length of emitted String. -1 means unlimited.
     * @return String representation
     */
    public String toStringRepresentation(
            StringRepresentation        rep,
            StringRepresentationContext context,
            int                         maxLength )
    {
        return rep.formatEntry( getClass(), DEFAULT_ENTRY, maxLength, toExternalForm() );
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param additionalArguments additional arguments for URLs, if any
     * @param target the HTML target, if any
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @return String representation
     */
    public String toStringRepresentationLinkStart(
            String                      additionalArguments,
            String                      target,
            StringRepresentation        rep,
            StringRepresentationContext context )
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @return String representation
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation        rep,
            StringRepresentationContext context )
    {
        return "";
    }

    /**
     * Convert to String. For debugging.
     *
     * @return String representation
     */
    @Override
    public String toString()
    {
        return toExternalForm();
    }

    /**
     * Default hashCode implementation.
     *
     * @return hashCode
     */
    @Override
    public int hashCode()
    {
        return toExternalForm().hashCode();
    }

    /**
     * Default equals implementation.
     *
     * @param other the Object to compare with
     * @return true if the Objects equal each other
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof MeshTypeIdentifier )) {
            return false;
        }
        
        MeshTypeIdentifier realOther = (MeshTypeIdentifier) other;
        
        boolean ret = toExternalForm().equals( realOther.toExternalForm() );
        return ret;
    }
    
    /**
     * The underlying String.
     */
    protected String theString;

    /**
     * The default entry in the resouce files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_ENTRY = "String";
}