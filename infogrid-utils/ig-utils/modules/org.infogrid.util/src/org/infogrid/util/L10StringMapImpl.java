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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

import java.util.Map;

/**
 * <p>This is a memory-conservative implementation of a map between Locale and a type T.</p>
 *
 * <p>This will not scale for large numbers of supported locales, but it is good for just one or
 * a handful of locales (the typical case here).</p>
 */
public class L10StringMapImpl
        extends
            L10MapImpl<String>
{
    /**
      * Construct an empty L10Map with only a default value and no Locale-specific values.
      *
      * @param defaultValue the default T returned by get for all Locales
      * @return the created L10MapImpl
      */
    public static L10StringMapImpl create(
            String defaultValue )
    {
        return new L10StringMapImpl( defaultValue, null, null );
    }

    /**
     * Construct an L10Map from a HashMap whose keys are String representations of the Locale,
     * and whose values are the corresponding Ts. The default value of this
     * L10Map will be the T with the key null. If the T with
     * the key null is null, the default value of this L10Map will be secondDefaultValue.
     *
     * @param theMap the HashMap with the Locale keys and the corresponding T
     * @param secondDefaultValue the default value of this L10Map if none was found in theMap
     * @return the created L10MapImpl
     */
     public static L10StringMapImpl create(
            Map<String,String> theMap,
            String             secondDefaultValue )
    {
        return create( theMap, secondDefaultValue, new Fact<String,L10StringMapImpl>() {
                public L10StringMapImpl create(
                        String      theDefaultValue,
                        String [][] theKeys,
                        Object []   theValues )
                {
                    return new L10StringMapImpl( theDefaultValue, theKeys, theValues );
                }
        });
    }

    /**
     * Private constructor, for subclasses only.
     *
     * @param theDefaultValue the default value of this L10Map
     * @param theKeys the Locales in key format that have values in this L10Map
     * @param theValues the corresponding PropertyValues, in same sequence as theKeys
     */
    protected L10StringMapImpl(
            String      theDefaultValue,
            String [][] theKeys,
            Object []   theValues )
    {
        super( theDefaultValue, theKeys, theValues );
    }


    /**
     * Obtain a string which is the Java-language constructor expression reflecting this value.
     * This is mainly for code-generation purposes.
     *
     * FIXME? This makes the assumption that all PropertyValues here can use the same
     * ClassLoader. I think this assumption is correct, but I better make a note here ...
     *
     * @param classLoaderVar name of a variable containing the class loader to be used to initialize this value
     * @param typeVar  name of the variable containing the DataType that goes with the to-be-created instance.
     * @return the Java-language constructor expression
     */
    public String getJavaConstructorString(
            String classLoaderVar,
            String typeVar )
    {
        StringBuilder ret = new StringBuilder( getClass().getName() );
        ret.append( ".create( " );

        ret.append( '"' ).append( StringHelper.stringToJavaString( defaultValue )).append( '"' );

        if( keys != null && keys.length > 0 ) {
            ret.append( ", new String[][] { " );
            for( int i=0 ; i<keys.length ; ++i ) {
                ret.append( "{ " );
                for( int j=0 ; j<keys[i].length ; ++j ) {
                    ret.append( "\"" );
                    ret.append( keys[i][j] );
                    ret.append( "\"" );
                    if( j<keys[i].length-1 ) {
                        ret.append( ", " );
                    }
                }
                ret.append( "}" );
                if( i<keys.length-1 ) {
                    ret.append( ", " );
                }
            }

            ret.append( "}, new String[] { " );
            for( int i=0 ; i<keys.length ; ++i ) {
                ret.append( '"' ).append( StringHelper.stringToJavaString( (String) values[i] )).append( "'" );
                if( i<keys.length-1 ) {
                    ret.append( ", " );
                }
            }
            ret.append( "} " );
        }

        ret.append( ")" );

        return ret.toString();
    }
}
