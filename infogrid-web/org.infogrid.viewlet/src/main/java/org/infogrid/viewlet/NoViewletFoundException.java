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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.viewlet;

import org.infogrid.util.FactoryException;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.text.HasStringRepresentation;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * No Viewlet could be found that could view the provided MeshObjectsToView.
 */
public class NoViewletFoundException
        extends
            FactoryException
        implements
            HasStringRepresentation,
            CanBeDumped
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param sender the Factory that threw this exception
     * @param o which MeshObjectsToView could not be viewed
     */
    public NoViewletFoundException(
            ViewletFactory    sender,
            MeshObjectsToView o )
    {
        super( sender );

        theObjectsToView = o;
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "objectsToView"
                },
                new Object[] {
                    theObjectsToView
        } );
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    @Override
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        if( theObjectsToView.getViewletTypeName() == null ) {
            return rep.formatEntry(
                    getClass(),
                    DEFAULT_NO_VIEWLET_TYPE_ENTRY,
                    pars,
                    theObjectsToView.getSubject());

        } else {
            return rep.formatEntry(
                    getClass(),
                    DEFAULT_VIEWLET_TYPE_ENTRY,
                    pars,
                    theObjectsToView.getSubject());
        }
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
    {
        return "";
    }

    /**
     * The MeshObjectsToView that could not be viewed.
     */
    protected MeshObjectsToView theObjectsToView;

    /**
     * The default entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_ENTRY = "String";

    /**
     * The default entry in the resource files for the case where no ViewletType has
     * been specified, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_NO_VIEWLET_TYPE_ENTRY = DEFAULT_ENTRY + "NoViewletType";

    /**
     * The default entry in the resource files for the case where a ViewletType has
     * been specified, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_VIEWLET_TYPE_ENTRY = DEFAULT_ENTRY + "ViewletType";
}
