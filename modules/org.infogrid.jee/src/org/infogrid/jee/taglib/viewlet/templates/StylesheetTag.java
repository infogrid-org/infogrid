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

package org.infogrid.jee.taglib.viewlet.templates;

/**
 * <p>Insert a href'd or inline'd style sheet into the HTML header.</p>
 */
public class StylesheetTag
    extends
        AbstractHrefOrInlineTag
{
    /**
     * Constructor.
     */
    public StylesheetTag()
    {
        // noop
    }

    /**
     * Initialize all default values. To be invoked by subclasses.
     */
    @Override
    protected void initializeToDefaults()
    {
        super.initializeToDefaults();
    }
    
    /**
     * Enable subclass to format the Href properly.
     * 
     * @param href the Href
     * @return formatted String
     */
    protected String formatHref(
            String href )
    {
        StringBuilder ret = new StringBuilder();
        ret.append( "<link rel=\"stylesheet\" href=\"" );
        ret.append( href );
        ret.append( "\" />" );
        
        return ret.toString();
    }

    /**
     * Enable subclass to format the inlined text properly.
     * 
     * @param text the inlined text
     * @return formatted String
     */
    protected String formatInline(
            String text )
    {
        return text;
    }
    
}