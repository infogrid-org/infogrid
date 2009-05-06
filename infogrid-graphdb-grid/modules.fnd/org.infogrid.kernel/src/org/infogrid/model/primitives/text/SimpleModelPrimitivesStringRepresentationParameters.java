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

package org.infogrid.model.primitives.text;

import org.infogrid.util.text.SimpleStringRepresentationParameters;
import org.infogrid.util.text.StringRepresentationParameters;

/**
 * Simple implementation of StringRepresentationParameters.
 */
public class SimpleModelPrimitivesStringRepresentationParameters
    extends
        SimpleStringRepresentationParameters
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Factory method.
     *
     * @return the created SimpleModelPrimitivesStringRepresentationParameters
     */
    public static SimpleModelPrimitivesStringRepresentationParameters create()
    {
        return new SimpleModelPrimitivesStringRepresentationParameters( null );
    }

    /**
     * Factory method.
     *
     * @param delegate the delegate, if any
     * @return the created SimpleModelPrimitivesStringRepresentationParameters
     */
    public static SimpleModelPrimitivesStringRepresentationParameters create(
            StringRepresentationParameters delegate )
    {
        return new SimpleModelPrimitivesStringRepresentationParameters( delegate );
    }

    /**
     * Constructor.
     *
     * @param delegate the delegate, if any
     */
    protected SimpleModelPrimitivesStringRepresentationParameters(
            StringRepresentationParameters delegate )
    {
        super( delegate );
    }
}
