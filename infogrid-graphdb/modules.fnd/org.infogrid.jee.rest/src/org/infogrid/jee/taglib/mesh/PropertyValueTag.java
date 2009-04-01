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

package org.infogrid.jee.taglib.mesh;

import javax.servlet.jsp.JspException;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.taglib.rest.AbstractRestInfoGridTag;
import org.infogrid.model.primitives.PropertyValue;

/**
 * Tag that renders an instance of <code>PropertyValue</code>, held in the context.
 * Unlike {@link PropertyTag PropertyTag}, this tag does not attempt to obtain a
 * <code>PropertyValue</code> by accessing a <code>MeshObject</code>.
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class PropertyValueTag
        extends
            AbstractRestInfoGridTag
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public PropertyValueTag()
    {
        // noop
    }

    /**
     * Initialize all default values. To be invoked by subclasses.
     */
    @Override
    protected void initializeToDefaults()
    {
        thePropertyValueName    = null;
        theNullString           = "";
        theStringRepresentation = null;
        theMaxLength            = -1;
        theColloquial           = false;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the propertyValueName property.
     *
     * @return value of the propertyValueName property
     * @see #setPropertyValueName
     */
    public final String getPropertyValueName()
    {
        return thePropertyValueName;
    }

    /**
     * Set value of the propertyValueName property.
     *
     * @param newValue new value of the propertyValueName property
     * @see #getPropertyValueName
     */
    public final void setPropertyValueName(
            String newValue )
    {
        thePropertyValueName = newValue;
    }
    
    /**
     * Obtain value of the nullString property.
     *
     * @return value of the nullString property
     * @see #setNullString
     */
    public String getNullString()
    {
        return theNullString;
    }

    /**
     * Set value of the nullString property.
     *
     * @param newValue new value of the nullString property
     * @see #getNullString
     */
    public void setNullString(
            String newValue )
    {
        theNullString = newValue;
    }

    /**
     * Obtain value of the stringRepresentation property.
     *
     * @return value of the stringRepresentation property
     * @see #setStringRepresentation
     */
    public String getStringRepresentation()
    {
        return theStringRepresentation;
    }

    /**
     * Set value of the stringRepresentation property.
     *
     * @param newValue new value of the stringRepresentation property
     * @see #getStringRepresentation
     */
    public void setStringRepresentation(
            String newValue )
    {
        theStringRepresentation = newValue;
    }

    /**
     * Obtain value of the maxLength property.
     *
     * @return value of the maxLength property
     * @see #setMaxLength
     */
    public int getMaxLength()
    {
        return theMaxLength;
    }

    /**
     * Set value of the maxLength property.
     *
     * @param newValue new value of the maxLength property
     * @see #getNullString
     */
    public void setMaxLength(
            int newValue )
    {
        theMaxLength = newValue;
    }

    /**
     * Obtain value of the colloquial property.
     *
     * @return value of the colloquial property
     * @see #setColloquial
     */
    public boolean getColloquial()
    {
        return theColloquial;
    }

    /**
     * Set value of the colloquial property.
     *
     * @param newValue new value of the colloquial property
     */
    public void setColloquial(
            boolean newValue )
    {
        theColloquial = newValue;
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        PropertyValue value = (PropertyValue) lookupOrThrow( thePropertyValueName );
        
        String text = formatValue( pageContext, value, theNullString, theStringRepresentation, theMaxLength, theColloquial );

        print( text );
        
        return SKIP_BODY;
    }

    /**
     * String containing the name of the bean that is the PropertyValue.
     */
    protected String thePropertyValueName;

    /**
     * The String that is shown if a value is null.
     */
    protected String theNullString;

    /**
     * Name of the String representation.
     */
    protected String theStringRepresentation;
    
    /**
     * The maximum length of an emitted String.
     */
    protected int theMaxLength;

    /**
     * Should the value be outputted in colloquial form.
     */
    protected boolean theColloquial;
}
