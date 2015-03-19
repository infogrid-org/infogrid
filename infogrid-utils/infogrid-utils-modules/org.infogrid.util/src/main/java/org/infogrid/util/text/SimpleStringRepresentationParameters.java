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

package org.infogrid.util.text;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of StringRepresentationParameters.
 */
public class SimpleStringRepresentationParameters
    implements
        StringRepresentationParameters
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Factory method.
     *
     * @return the created SimpleStringRepresentationParameters
     */
    public static SimpleStringRepresentationParameters create()
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( null );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param map the initial content for this object
     * @return the created SimpleStringRepresentationParameters
     */
    public static SimpleStringRepresentationParameters create(
            Map<String,?> map )
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( null );
        ret.putAll( map );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param delegate the delegate, if any
     * @return the created SimpleStringRepresentationParameters
     */
    public static SimpleStringRepresentationParameters create(
            StringRepresentationParameters delegate )
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( delegate );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param delegate the delegate, if any
     * @param map the initial content for this object
     * @return the created SimpleStringRepresentationParameters
     */
    public static SimpleStringRepresentationParameters create(
            StringRepresentationParameters delegate,
            Map<String,?>                  map )
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( delegate );
        ret.putAll( map );
        return ret;
    }

    /**
     * Constructor.
     *
     * @param delegate the delegate, if any
     */
    protected SimpleStringRepresentationParameters(
            StringRepresentationParameters delegate )
    {
        theDelegate = delegate;
    }

    /**
     * Obtain the delegate, if any.
     *
     * @return the delegate, if any
     */
    public StringRepresentationParameters getDelegate()
    {
        return theDelegate;
    }

    /**
     * Obtain the number of parameters.
     *
     * @return the number of parameters
     */
    public int size()
    {
        int ret = theStorage.size();
        if( theDelegate != null ) {
            ret += theDelegate.size();
        }
        return ret;
    }

    /**
     * Returns <tt>true</tt> if this is empty.
     *
     * @return <tt>true</tt> if this is empty.
     */
    public boolean isEmpty()
    {
        if( !theStorage.isEmpty() ) {
            return false;
        }
        if( theDelegate != null ) {
            return theDelegate.isEmpty();
        }
        return true;
    }

    /**
     * Obtain a named value, or null.
     *
     * @param key the name of the value
     * @return the value, if any
     */
    public Object get(
            String key )
    {
        Object ret = theStorage.get( key );
        if( ret == null && theDelegate != null ) {
            ret = theDelegate.get( key );
        }
        return ret;
    }

    /**
     * Set a named value.
     *
     * @param key the name of the value
     * @param value the value
     */
    public void put(
            String key,
            Object value )
    {
        theStorage.put( key, value );
    }

    /**
     * Put all the members of the map into this object.
     *
     * @param map the map
     */
    public void putAll(
            Map<String,?> map )
    {
        for( String key : map.keySet() ) {
            Object value = map.get( key );
            theStorage.put( key, value );
        }
    }

    /**
     * Create a copy of this instance, but with an additional the named value.
     *
     * @param key the name of the value
     * @param value the value
     * @return copy, with the named value
     */
    public StringRepresentationParameters with(
            String key,
            Object value )
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( this );
        ret.put( key, value );
        return ret;
    }

    /**
     * Create a copy of this instance, but with the named values.
     *
     * @param map the named values
     * @return copy, with the named values
     */
    public StringRepresentationParameters with(
            Map<String,?> map )
    {
        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( this );
        ret.putAll( map );
        return ret;
    }

    /**
     * Create a copy of this instance, but without the named value.
     *
     * @param key the name of the value
     * @return copy, without the named value
     */
    public SimpleStringRepresentationParameters without(
            String key )
    {
        StringRepresentationParameters newDelegate = theDelegate != null ? theDelegate.without( key ) : null;

        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( newDelegate );
        for( String current : theStorage.keySet() ) {
            if( !current.equals( key )) {
                ret.put( current, theStorage.get( current ));
            }
        }

        return ret;
    }

    /**
     * Create a copy of this instance, but without the named values.
     *
     * @param keys the names of the values
     * @return copy, without the named values
     */
    public StringRepresentationParameters without(
            String [] keys )
    {
        StringRepresentationParameters newDelegate = theDelegate != null ? theDelegate.without( keys ) : null;

        SimpleStringRepresentationParameters ret = new SimpleStringRepresentationParameters( newDelegate );
        for( String current : theStorage.keySet() ) {

            boolean found = false;
            for( int i=0 ; i<keys.length ; ++i ) {
                if( current.equals( keys[i] )) {
                    found = true;
                    break;
                }
            }
            if( !found ) {
                ret.put( current, theStorage.get( current ));
            }
        }

        return ret;
    }

    /**
     * The StringRepresentationParameters to ask if this object does not know of an entry.
     */
    protected StringRepresentationParameters theDelegate;

    /**
     * Storage.
     */
    protected HashMap<String,Object> theStorage = new HashMap<String,Object>();
}
