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

package org.infogrid.lid.openid.store;

import org.infogrid.lid.openid.CryptUtils;
import org.infogrid.lid.openid.RelyingPartySideAssociation;

import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreValue;
import org.infogrid.store.StoreValueDecodingException;
import org.infogrid.store.StoreValueEncodingException;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 *
 */
public class RelyingPartySideAssociationMapper
        implements
            StoreEntryMapper<String,RelyingPartySideAssociation>
{
    /**
     * Factory method.
     */
    public static RelyingPartySideAssociationMapper create()
    {
        return new RelyingPartySideAssociationMapper();
    }

    /**
     * Constructor.
     */
    protected RelyingPartySideAssociationMapper()
{
    }
    
    /**
     * Map a key to a String value that can be used for the Store.
     *
     * @param key the key object
     * @return the corresponding String value
     */
    public String keyToString(
            String key )
    {
        return key;
    }

    /**
     * Map a String value that can be used for the Store to a key
     *
     * @param key the key in String form
     * @return the corresponding key
     */
    public String stringToKey(
            String key )
    {
        return key;
    }

    /**
     * Map a StoreValue to a value.
     *
     * @param key the key to the StoreValue
     * @param value the StoreValue
     * @return the value
     */
    public RelyingPartySideAssociation decodeValue(
            String     key,
            StoreValue value )
        throws
            StoreValueDecodingException
    {
        try {
            String          s     = new String( value.getData(), ENCODING );
            StringTokenizer token = new StringTokenizer( s, "\n" );

            String associationHandle = null;
            String serverUrl         = null;
            String sharedSecretInHex = null;

            while( token.hasMoreTokens() ) {
                String line = token.nextToken();

                if( line.startsWith( ASSOCIATION_HANDLE_TAG )) {
                    associationHandle = line.substring( ASSOCIATION_HANDLE_TAG.length() );

                } else if( line.startsWith( SERVER_URL_TAG )) {
                    serverUrl = line.substring( SERVER_URL_TAG.length() );

                } else if( line.startsWith( SHARED_SECRET_TAG )) {
                    sharedSecretInHex = line.substring( SHARED_SECRET_TAG.length() );

                } else {
                    throw new StoreValueDecodingException( "Unknown token in line: " + line );
                }
            }
            
            if( sharedSecretInHex == null ) {
                throw new StoreValueDecodingException( "no shared secret" );
            }
            byte [] sharedSecret = CryptUtils.dehex( sharedSecretInHex );

            RelyingPartySideAssociation ret = RelyingPartySideAssociation.create(
                    serverUrl,
                    associationHandle,
                    sharedSecret,
                    value.getTimeCreated(),
                    value.getTimeExpires() );
            ret.checkCompleteness();

            return ret;
            
        } catch( UnsupportedEncodingException ex ) {
            throw new StoreValueDecodingException( ex );

        } catch( IllegalStateException ex ) {
            throw new StoreValueDecodingException( ex );
        }
    }
    
    /**
     * Obtain the preferred encoding id of this StoreMapper.
     *
     * @return the preferred encoding id
     */
    public String getPreferredEncodingId()
    {
        return getClass().getName();
    }

    /**
     * Obtain the time a RelyingPartySideAssociation was created.
     *
     * @param value the time a RelyingPartySideAssociation was created.
     * @return the time created, in System.currentTimeMillis() format
     */
    public long getTimeCreated(
            RelyingPartySideAssociation value )
    {
        return value.getTimeCreated();
    }

    /**
     * Obtain the time a RelyingPartySideAssociation was last updated.
     *
     * @param value the time a RelyingPartySideAssociation was last updated.
     * @return the time updated, in System.currentTimeMillis() format
     */
    public long getTimeUpdated(
            RelyingPartySideAssociation value )
    {
        return getTimeCreated( value );
    }

    /**
     * Obtain the time a RelyingPartySideAssociation was last read.
     *
     * @param value the time a RelyingPartySideAssociation was last read.
     * @return the time read, in System.currentTimeMillis() format
     */
    public long getTimeRead(
            RelyingPartySideAssociation value )
    {
        return value.getTimeRead();
    }

    /**
     * Obtain the time a RelyingPartySideAssociation will expire.
     *
     * @param value the time a RelyingPartySideAssociation will expire.
     * @return the time will expire, in System.currentTimeMillis() format
     */
    public long getTimeExpires(
            RelyingPartySideAssociation value )
    {
        return value.getTimeExpires();
    }

    /**
     * Obtain the value as a byte array.
     *
     * @return the byte array
     */
    public byte [] asBytes(
            RelyingPartySideAssociation value )
        throws
            StoreValueEncodingException
    {
        try {
            String sharedSecretInHex = CryptUtils.hex( value.getSharedSecret() );

            StringBuilder buf = new StringBuilder();
            buf.append( ASSOCIATION_HANDLE_TAG ).append( value.getAssociationHandle() ).append( "\n" );
            buf.append(         SERVER_URL_TAG ).append( value.getServerUrl()         ).append( "\n" );
            buf.append(      SHARED_SECRET_TAG ).append( sharedSecretInHex            ).append( "\n" );

            byte [] ret = buf.toString().getBytes( ENCODING );
            return ret;

        } catch( UnsupportedEncodingException ex ) {
            throw new StoreValueEncodingException( ex );
        }
    }
    
    /**
     * Tags for our serialization syntax.
     */
    protected static final String ASSOCIATION_HANDLE_TAG = "associationHandle:";
    protected static final String         SERVER_URL_TAG = "serverUrl:";
    protected static final String      SHARED_SECRET_TAG = "sharedSecret:";
    
    /**
     * The encoding to use.
     */
    protected static final String ENCODING = "UTF-8";
}