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

package org.infogrid.model.primitives;

import org.infogrid.util.ResourceHelper;
import org.infogrid.util.text.StringRepresentation;

/**
  * This is a floating point value for PropertyValues. Internally, it uses double.
  * It can also carry a Unit.
  *
  * FIXME: units not tested.
  */
public final class FloatValue
        extends
            PropertyValue
{
    private final static long serialVersionUID = 1L; // helps with serialization

    /**
     * Convenience constant like Float.NaN.
     */
    public static final FloatValue NaN = create( Float.NaN );

    /**
     * Convenience constant for zero.
     */
    public static final FloatValue ZERO = create( 0. );

    /**
     * Factory method.
     *
     * @param value the value
     * @return the created FloatValue
     */
    public static FloatValue create(
            double value )
    {
        return new FloatValue( value, null );
    }

    /**
     * Factory method.
     *
     * @param value the value
     * @param u the Unit for the value
     * @return the created FloatValue
     */
    public static FloatValue create(
            double value,
            Unit   u )
    {
        return new FloatValue( value, u );
    }

    /**
      * Private constructor, use factory methods.
      *
      * @param value the value
      * @param u the Unit for the value
      */
    private FloatValue(
            double value,
            Unit   u )
    {
        this.theValue = value;
        this.theUnit  = u;
    }

    /**
      * Convert back to double.
      *
      * @return the value as double
      */
    public Double value()
    {
        return theValue;
    }

    /**
      * Determine Unit, if any.
      *
      * @return the Unit, if any
      */
    public Unit getUnit()
    {
        return theUnit;
    }

    /**
      * Determine equality of two objects.
      *
      * @param otherValue the object to test against
      * @return true if the objects are equal
      */
    @Override
    public boolean equals(
            Object otherValue )
    {
        if( ! ( otherValue instanceof FloatValue )) {
            return false;
        }

        FloatValue realOtherValue = (FloatValue) otherValue;

        if( theUnit == null ) {
            if( realOtherValue.theUnit != null ) {
                return false;
            }

            return theValue == realOtherValue.theValue;
        } else {
            if( realOtherValue.theUnit == null ) {
                return false;
            }

            if( ! theUnit.getFamily().equals( realOtherValue.theUnit.getFamily() )) {
                return false;
            }

            return theValue * theUnit.getPrefix() == realOtherValue.theValue * realOtherValue.theUnit.getPrefix();
        }
    }

    /**
      * Determine relationship between two values.
      *
      * @param otherValue the value to test against
      * @return returns true if this object is smaller than, or the same as otherValue
      */
    public boolean isSmallerOrEquals(
            FloatValue otherValue )
    {
        if( theUnit == null ) {
            if( otherValue.theUnit != null ) {
                return false;
            }

            return theValue <= otherValue.theValue;
        } else {
            if( otherValue.theUnit == null ) {
                return false;
            }

            if( ! theUnit.getFamily().equals( otherValue.theUnit.getFamily() )) {
                return false;
            }

            return theValue * theUnit.getPrefix() <= otherValue.theValue * otherValue.theUnit.getPrefix();
        }
    }

    /**
      * Determine relationship between two values.
      *
      * @param otherValue the value to test against
      * @return returns true if this object is smaller than otherValue
      */
    public boolean isSmaller(
            FloatValue otherValue )
    {
        if( theUnit == null ) {
            if( otherValue.theUnit != null ) {
                return false;
            }
            return theValue < otherValue.theValue;
        } else {
            if( otherValue.theUnit == null ) {
                return false;
            }

            if( ! theUnit.getFamily().equals( otherValue.theUnit.getFamily() )) {
                return false;
            }
            return theValue * theUnit.getPrefix() < otherValue.theValue * otherValue.theUnit.getPrefix();
        }
    }

    /**
      * Determine relationship between two values.
      *
      * @param otherValue the value to test against
      * @return returns true if this object is larger, or the same, as otherValue
      */
    public boolean isLargerOrEquals(
            FloatValue otherValue )
    {
        return otherValue.isSmallerOrEquals( this );
    }

    /**
      * Determine relationship between two values.
      *
      * @param otherValue the value to test against
      * @return returns true if this object is larger than otherValue
      */
    public boolean isLarger(
            FloatValue otherValue )
    {
        return otherValue.isSmaller( this );
    }

    /**
      * Obtain as string representation, for debugging.
      *
      * @return string representation of this object
      */
    public String toString()
    {
        if( theUnit != null ) {
            return String.valueOf( theValue ) + " " + theUnit.toString();
        } else {
            return String.valueOf( theValue );
        }
    }

    /**
      * This attempts to parse a string and turn it into a float value similarly
      * to Float.parseFloat().
      *
      * FIXME: need to deal with unit
      *
      * @param theString the string that shall be parsed
      * @return the created FloatValue
      * @throws NumberFormatException thrown if theString does not follow the correct syntax
      */
    public static FloatValue parseFloatValue(
            String theString )
        throws
            NumberFormatException
    {
        return FloatValue.create( Double.parseDouble( theString ));
    }

    /**
     * Obtain a string which is the Java-language constructor expression reflecting this value.
     *
     * @param classLoaderVar name of a variable containing the class loader to be used to initialize this value
     * @param typeVar  name of the variable containing the DatatType that goes with the to-be-created instance.
     * @return the Java-language constructor expression
     */
    public String getJavaConstructorString(
            String classLoaderVar,
            String typeVar )
    {
        StringBuffer buf = new StringBuffer( 128 );
        buf.append( getClass().getName() );
        buf.append( DataType.CREATE_STRING );
        buf.append( theValue );
        
        if( theUnit != null ) {
            buf.append( ", " );
            buf.append( theUnit.getJavaConstructorString() );
        }
        buf.append( DataType.CLOSE_PAREN_STRING );
        return buf.toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the PropertyValue to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    public int compareTo(
            PropertyValue o )
    {
        FloatValue realOther = (FloatValue) o;

        if( theValue < realOther.theValue ) {
            return -1;
        } else if( theValue == realOther.theValue ) {
            return 0;
        } else {
            return +1;
        }
    }

    /**
     * Convert this PropertyValue to its String representation, using the representation scheme.
     *
     * @param representation the representation scheme
     * @return the String representation
     */
    public String toStringRepresentation(
            StringRepresentation representation )
    {
        return representation.formatEntry( RESOURCEHELPER, DEFAULT_ENTRY, theValue );
    }

    /**
      * The actual value.
      */
    protected double theValue;

    /**
      * The Unit, if any.
      */
    protected Unit theUnit;

    /**
     * Our ResourceHelper.
     */
    static final ResourceHelper RESOURCEHELPER = ResourceHelper.getInstance( FloatValue.class );    
}