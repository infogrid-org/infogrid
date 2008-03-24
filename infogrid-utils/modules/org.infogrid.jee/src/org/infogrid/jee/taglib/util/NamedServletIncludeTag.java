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

package org.infogrid.jee.taglib.util;

import org.infogrid.jee.taglib.AbstractInfoGridTag;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.taglib.InfoGridJspUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Allows the inclusion of named servlets. This should be a standard feature
 *    of the <code>&lt;jsp:include&lt;</code> tag, but it isn't.
 *    See description in the
 *   {@link org.infogrid.jee.taglib.util package documentation}.</p>
 */
public class NamedServletIncludeTag
    extends
        AbstractInfoGridTag
    
{
    /**
     * Constructor.
     */
    public NamedServletIncludeTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theServletName = null;
        theFlush       = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the servletName property.
     *
     * @return value of the servletName property
     * @see #setServletName
     */
    public String getServletName()
    {
        return theServletName;
    }

    /**
     * Set value of the servletName property.
     *
     * @param newValue new value of the servletName property
     * @see #getServletName
     */
    public void setServletName(
            String newValue )
    {
        theServletName = newValue;
    }

    /**
     * Obtain value of the flush property.
     *
     * @return value of the flush property
     * @see #setFlush
     */
    public String getFlush()
    {
        return theFlush;
    }

    /**
     * Set value of the flush property.
     *
     * @param newValue new value of the flush property
     * @see #getFlush
     */
    public void setFlush(
            String newValue )
    {
        theFlush = newValue;
    }

    /**
     * Do the start tag operation.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IOException thrown if an I/O error occurred
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        // This is created after org/apache/jasper/runtime/JspRuntimeLibrary.include
        
        JspWriter out = pageContext.getOut();
        if( InfoGridJspUtils.isTrue( theFlush ) && !(out instanceof BodyContent)) {
            out.flush();
        }

        try {
            RequestDispatcher rd = pageContext.getServletContext().getNamedDispatcher( theServletName );
            rd.include( pageContext.getRequest(), new ServletResponseWrapperInclude( (HttpServletResponse) pageContext.getResponse(), out ));
            return EVAL_BODY_INCLUDE;
            
        } catch( ServletException ex ) {
            throw new JspException( ex ); // why in the world are these two differnt types of exceptions?
        }
    }

    /**
     * Name of the Servlet as configured in <code>web.xml</code>.
     */
    protected String theServletName;
    
    /**
     * Should we flush prior to including.
     */
    protected String theFlush;
    
    /**
     * Separate response, per org/apache/jasper/runtime/JspRuntimeLibrary.include. FIXME: not entirely clear why.
     */
    @SuppressWarnings(value={"deprecation"})
    static class ServletResponseWrapperInclude
        extends
            HttpServletResponseWrapper
    {
        /**
         * Constructor.
         *
         * @param response the real response
         * @param jspWriter the JspWriter
         */
        public ServletResponseWrapperInclude(
                HttpServletResponse response, 
                JspWriter           jspWriter )
        {
            super( response );
        	this.printWriter = new PrintWriter(jspWriter);
            this.jspWriter = jspWriter;
        }

        /**
         * Obtain a PrintWriter.
         *
         * @return the PrintWriter
         */
        @Override
        public PrintWriter getWriter()
            throws
                IOException
        {
            return printWriter;
        }

        /**
         * Cannot obtain an OutputStream.
         *
         * @return nothing
         * @throws IllegalStateException
         */
        @Override
        public ServletOutputStream getOutputStream()
            throws
                IOException
        {
            throw new IllegalStateException();
        }

        /**
         * Reset the buffer.
         */
        @Override
        public void resetBuffer()
        {
            try {
                jspWriter.clearBuffer();
            } catch( IOException ex ) {
                // ignore
            }
    	}

        private PrintWriter printWriter;
        private JspWriter   jspWriter;
    }
}
