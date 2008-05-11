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

package org.infogrid.probe.shadow.externalized;

import org.infogrid.meshbase.net.externalized.ParserFriendlyExternalizedProxy;

/**
 * Parser-friendly implementaiton of ExternalizedShadowProxy.
 */
public class ParserFriendlyExternalizedShadowProxy
        extends
            ParserFriendlyExternalizedProxy
        implements
            ExternalizedShadowProxy
{
    /**
     * Determine whether this a placeholder Proxy, or a real Proxy.
     *
     * @return ytrue of this is a placeholder Proxy
     */
    public boolean getIsPlaceholder()
    {
        return theIsPlaceholder;
    }

    /**
     * Set whether this is a placeholder Proxy, or a real Proxy.
     * 
     * @param newValue the new value
     */
    public void setIsPlaceholder(
            boolean newValue )
    {
        theIsPlaceholder = newValue;
    }
    
    /**
     * If true, this represents a placeholder Proxy.
     */
    protected boolean theIsPlaceholder;
}