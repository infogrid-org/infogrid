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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.xpriso.logging;

import org.infogrid.util.logging.ToStringDumper;
import org.infogrid.util.logging.ToStringDumperFactory;

/**
 * Factory for XprisoMessageDumpers.
 */
public class XprisoMessageDumperFactory
        extends
            ToStringDumperFactory
{
    /**
     * Factory method.
     *
     * @return the created XprisoMessageDumperFactory
     */
    public static XprisoMessageDumperFactory create()
    {
        return new XprisoMessageDumperFactory( ToStringDumper.DEFAULT_MAXLEVEL, ToStringDumper.DEFAULT_MAXARRAYLENGTH );
    }

    /**
     * Factory method.
     *
     * @param maxLevel the number of object levels to dump
     * @param maxArrayLength the maximum number of array entries to dump
     * @return the created XprisoMessageDumperFactory
     */
    public static XprisoMessageDumperFactory create(
            int maxLevel,
            int maxArrayLength )
    {
        return new XprisoMessageDumperFactory( maxLevel, maxArrayLength );
    }

    /**
     * Constructor.
     *
     * @param maxLevel the number of object levels to dump
     * @param maxArrayLength the maximum number of array entries to dump
     */
    protected XprisoMessageDumperFactory(
            int maxLevel,
            int maxArrayLength )
    {
        super( maxLevel, maxArrayLength );
    }

    /**
     * Factory method.
     *
     * @param key the key information required for object creation, if any
     * @return the created object
     */
    @Override
    public XprisoMessageDumper obtainFor(
            Object key )
    {
        return XprisoMessageDumper.create( theMaxLevel, theMaxArrayLength );
    }
}
