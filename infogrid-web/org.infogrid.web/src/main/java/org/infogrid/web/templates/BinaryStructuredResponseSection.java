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

package org.infogrid.web.templates;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An actual section in a StructuredResponse that contains text.
 */
public class BinaryStructuredResponseSection
        extends
            StructuredResponseSection
{
    /**
     * Factory method.
     *
     * @return the created BinaryStructuredResponseSection
     */
    public static BinaryStructuredResponseSection create()
    {
        BinaryStructuredResponseSection ret = new BinaryStructuredResponseSection();
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     */
    protected BinaryStructuredResponseSection()
    {
    }

    /**
     * Determine whether this section is empty.
     * 
     * @return true if this section is empty
     */
    @Override
    public boolean isEmpty()
    {
        if( theHttpResponseCode > 0 && theHttpResponseCode != 200 ) {
            return false;
        }
        if( theContent != null ) {
            return false;
        }
        if( !theOutgoingCookies.isEmpty() ) {
            return false;
        }
        if( !theCurrentProblems.isEmpty() ) {
            return false;
        }
        return true;
    }
    
    /**
     * Stream this StructuredResponseSection to an OutputStream.
     * 
     * @param s the OutputStream to write to
     * @return true if something was output, false otherwise
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public boolean doOutput(
            OutputStream s )
        throws
            IOException
    {
        if( theContent != null && theContent.length > 0 ) {
            s.write( theContent );
            return true;

        } else {
            return false;
        }
    }

    /**
     * Obtain the current content of this section.
     * 
     * @return the current content of this section, or null
     */
    public byte [] getContent()
    {
        return theContent;
    }

    /**
     * Set the content of this section.
     * 
     * @param newValue the new content of this section
     */
    public void setContent(
            byte [] newValue )
    {
        theContent = newValue;
    }
    
    /**
     * Append to the content of this section.
     * 
     * @param toAppend the content to append to this section
     */
    public void appendContent(
            byte [] toAppend )
    {
        appendContent( toAppend, toAppend.length );
    }

    /**
     * Append to the content of this section.
     * 
     * @param toAppend the content to append to this section
     * @param len   the number of bytes
     */
    public void appendContent(
            byte [] toAppend,
            int     len )
    {
        if( theContent == null ) {
            theContent = new byte[ len ];
            System.arraycopy( toAppend, 0, theContent, 0, len );
        } else {
            byte [] old = theContent;
            theContent = new byte[ old.length + len ];
            System.arraycopy( old,      0, theContent, 0,          old.length );
            System.arraycopy( toAppend, 0, theContent, old.length, len );
        }
    }

    /**
     * Content of this section, if any.
     */
    protected byte [] theContent;
}
