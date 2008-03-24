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

import javax.servlet.jsp.JspException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Formats an Object with its <code>toExternalForm</code> method.
 *    See description in the
 *   {@link org.infogrid.jee.taglib.util package documentation}.</p>
 */
public class ToExternalFormTag
        extends
             AbstractInfoGridTag
{
    /**
     * Constructor.
     */
    public ToExternalFormTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theObjectName  = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the objectName property.
     *
     * @return value of the objectName property
     * @see #setObjectName
     */
    public final String getObjectName()
    {
        return theObjectName;
    }

    /**
     * Set value of the objectName property.
     *
     * @param newValue new value of the objectName property
     * @see #getObjectName
     */
    public final void setObjectName(
            String newValue )
    {
        theObjectName = newValue;
    }

    /**
     * Do the start tag operation.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        Object obj = lookup( theObjectName );

        if( obj != null ) {
            try {
                Method m = obj.getClass().getMethod( "toExternalForm", (Class []) null );

                Object result = m.invoke( obj );
                if( result != null ) {
                    print( result.toString() );
                }

            } catch( NoSuchMethodException ex ) {
                throw new JspException( ex );

            } catch( IllegalAccessException ex ) {
                throw new JspException( ex );

            } catch( InvocationTargetException ex ) {
                throw new JspException( ex );
            }
        }
        
        return EVAL_BODY_INCLUDE;
    }

    /**
     * The name of the bean that contains the Object to be rendered.
     */
    protected String theObjectName;
}
