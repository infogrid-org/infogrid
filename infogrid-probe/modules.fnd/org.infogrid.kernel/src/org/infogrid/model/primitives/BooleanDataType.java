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

package org.infogrid.model.primitives;

import java.io.ObjectStreamException;
import org.infogrid.util.text.HasStringRepresentation;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationContext;
/**
  * This is a boolean DataType.
  */
public final class BooleanDataType
        extends DataType
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
      * This is the default instance of this class.
      */
    public static final BooleanDataType theDefault = new BooleanDataType();

    /**
     * Factory method. Always returns the same instance.
     *
     * @return the default instance of this class
     */
    public static BooleanDataType create()
    {
        return theDefault;
    }

    /**
      * Private constructor, there is no reason to instatiate this more than once.
      */
    private BooleanDataType()
    {
        super( null );
    }

    /**
      * Test for equality.
      *
      * @param other the object to test against
      * @return true if objects are equal
      */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof BooleanDataType ) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether this PropertyValue conforms to this DataType.
     *
     * @param value the candidate PropertyValue
     * @return true if the candidate PropertyValue conforms to this type
     */
    public boolean conforms(
            PropertyValue value )
    {
        if( value instanceof BooleanValue ) {
            return true;
        }
        return false;
    }

    /**
      * Obtain the Java class that can hold values of this data type.
      *
      * @return the Java class that can hold values of this data type
      */
    public Class getCorrespondingJavaClass()
    {
        return BooleanValue.class;
    }

    /**
      * Instantiate this data type into a PropertyValue with a
      * reasonable default value.
      *
      * @return a PropertyValue with a reasonable default value that is an instance of this DataType
      */
    public PropertyValue instantiate()
    {
        return BooleanValue.FALSE;
    }

    /**
     * Obtain the default value of this DataType.
     *
     * @return the default value of this DataType
     */
    public PropertyValue getDefaultValue()
    {
        return BooleanValue.FALSE;
    }

    /**
     * Correctly deserialize a static instance.
     *
     * @return the static instance if appropriate
     * @throws ObjectStreamException thrown if reading from the stream failed
     */
    public Object readResolve()
        throws
            ObjectStreamException
    {
        if( this.equals( theDefault )) {
            return theDefault;
        } else {
            return this;
        }
    }

    /**
     * Obtain a value expression in the Java language that invokes the constructor
     * of factory method of the underlying concrete class, thereby creating or
     * reusing an instance of the underlying concrete class that is identical
     * to the instance on which this method was invoked.
     *
     * This is used primarily for code-generation purposes.
     *
     * @param classLoaderVar name of a variable containing the class loader to be used to initialize this value
     * @return the Java language expression
     */
    public String getJavaConstructorString(
            String classLoaderVar )
    {
        final String className = getClass().getName();

        return className + DEFAULT_STRING;
    }

    /**
     * Obtain a PropertyValue that corresponds to this DataType, based on the String representation
     * of the PropertyValue.
     *
     * @param s the String representation
     * @return the PropertyValue
     */
    public BooleanValue fromString(
            String s )
    {
        if( "true".equalsIgnoreCase( s )) {
            return BooleanValue.TRUE;
        } else if( "yes".equalsIgnoreCase( s ) ) {
            return BooleanValue.TRUE;
        } else {
            return BooleanValue.FALSE;
        }
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @param maxLength maximum length of emitted String. -1 means unlimited.
     * @param colloquial if applicable, output in colloquial form
     * @return String representation
     */
    public String toStringRepresentation(
            StringRepresentation        rep,
            StringRepresentationContext context,
            int                         maxLength,
            boolean                     colloquial ) // ignore colloquial
    {
        return rep.formatEntry(
                BooleanValue.class,
                DEFAULT_ENTRY,
                maxLength,
                colloquial,
                theSupertype );
    }

    /**
     * Obtain a PropertyValue that corresponds to this PropertyType, based on the String representation
     * of the PropertyValue.
     * 
     * @param representation the StringRepresentation in which the String s is given
     * @param s the String
     * @return the PropertyValue
     * @throws PropertyValueParsingException thrown if the String representation could not be parsed successfully
     */
    public PropertyValue fromStringRepresentation(
            StringRepresentation representation,
            String               s )
        throws
            PropertyValueParsingException
    {
        String compareTo = representation.formatEntry( BooleanValue.class, "True", HasStringRepresentation.UNLIMITED_LENGTH, true );
        if( compareTo.equalsIgnoreCase( s )) {
            return BooleanValue.TRUE;
        }
        compareTo = representation.formatEntry( BooleanValue.class, "False", HasStringRepresentation.UNLIMITED_LENGTH, true );
        if( compareTo.equalsIgnoreCase( s )) {
            return BooleanValue.FALSE;
        }
        compareTo = representation.formatEntry( BooleanValue.class, "True", HasStringRepresentation.UNLIMITED_LENGTH, false );
        if( compareTo.equalsIgnoreCase( s )) {
            return BooleanValue.TRUE;
        }
        compareTo = representation.formatEntry( BooleanValue.class, "False", HasStringRepresentation.UNLIMITED_LENGTH, false );
        if( compareTo.equalsIgnoreCase( s )) {
            return BooleanValue.FALSE;
        }
        throw new PropertyValueParsingException( this, representation, s );
    }
}
