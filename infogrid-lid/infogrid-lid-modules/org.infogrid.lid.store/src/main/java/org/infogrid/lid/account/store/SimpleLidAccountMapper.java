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

package org.infogrid.lid.account.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.account.SimpleLidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreValue;
import org.infogrid.store.StoreValueDecodingException;
import org.infogrid.store.StoreValueEncodingException;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.Identifier;
import org.infogrid.util.SimpleStringIdentifier;
import org.infogrid.util.XmlUtils;
import org.infogrid.util.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Knows how to map SimpleLidAccount to and from the Store.
 */
public class SimpleLidAccountMapper
        extends
            DefaultHandler
        implements
            StoreEntryMapper<Identifier,SimpleLidAccount>,
            SimpleLidAccountTags
{
    private static final Log log = Log.getLogInstance( SimpleLidAccountMapper.class  ); // our own, private logger

    /**
     * Constructor.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param availableCredentialTypes the credential types known to the application
     */
    public SimpleLidAccountMapper(
            Identifier           siteIdentifier,
            LidCredentialType [] availableCredentialTypes )
    {
        theSiteIdentifier           = siteIdentifier;
        theAvailableCredentialTypes = availableCredentialTypes;
        
        try {
            theParser = theSaxParserFactory.newSAXParser();

        } catch( Throwable t ) {
            log.error( t );
        }
    }

    /**
     * Map a key to a String value that can be used for the Store.
     *
     * @param key the key object
     * @return the corresponding String value that can be used for the Store
     */
    public String keyToString(
            Identifier key )
    {
        return key.toExternalForm();
    }

    /**
     * Map a String value that can be used for the Store to a key object.
     *
     * @param stringKey the key in String form
     * @return the corresponding key object
     */
    public Identifier stringToKey(
            String stringKey )
    {
        Identifier ret = SimpleStringIdentifier.create( stringKey );
        return ret;
    }

    /**
     * Map a StoreValue to a value.
     *
     * @param key the key to the StoreValue
     * @param value the StoreValue
     * @return the value
     * @throws StoreValueDecodingException thrown if the StoreValue could not been decoded
     */
    public SimpleLidAccount decodeValue(
            Identifier key,
            StoreValue value )
        throws
            StoreValueDecodingException
    {
        try {
            InputStream contentAsStream = value.getDataAsStream();

            theCurrentIdentifier     = null;
            theRemoteIdentifiers     = new ArrayList<Identifier>();
            theCurrentAccountData    = null;
            theCurrentAttribute      = null;
            theCurrentCredentialType = null;

            theParser.parse( contentAsStream, this );
            
            SimpleLidAccount ret = SimpleLidAccount.create(
                    theCurrentIdentifier,
                    theSiteIdentifier,
                    theCurrentAccountData.getStatus(),
                    ArrayHelper.copyIntoNewArray( theRemoteIdentifiers, Identifier.class ),
                    theCurrentAccountData.getAttributes(),
                    theCurrentAccountData.getGroupIdentifiers(),
                    theCurrentAccountData.getTimeCreated() );
            
            return ret;

        } catch( SAXException ex ) {
            throw new StoreValueDecodingException( ex );

        } catch( IOException ex ) {
            throw new StoreValueDecodingException( ex );

        } finally {
            clearState();
        }
    }
    
    /**
     * Override locator method so we know what we are parsing.
     *
     * @param locator the new Locator object
     */
    @Override
    public void setDocumentLocator(
            Locator locator )
    {
        theLocator = locator;
    }

    /**
     * SAX found some characters.
     *
     * @param ch the character array
     * @param start the start index
     * @param length the number of characters
     */
    @Override
    public final void characters(
            char [] ch,
            int     start,
            int     length )
    {
        try {
            if( length > 0 ) {
                if( theCharacters == null ) {
                    theCharacters = new StringBuilder();
                }
                theCharacters.append( ch, start, length );
            }
        } catch( RuntimeException ex ) {
            log.error( ex ); // internal error, no need to throw SAXParseException
        }
    }

    /**
     * SAX found a new element.
     *
     * @param namespaceURI URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     * @throws SAXException thrown if a parsing error occurrs
     */
    @Override
    public void startElement(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( log.isInfoEnabled() ) {
            log.info( "SAX startElement: " + namespaceURI + ", " + localName + ", " + qName );
        }
        theCharacters = null; // if we haven't processed them so far, we never will

        if( ACCOUNT_TAG.equals( qName )) {
            if( theCurrentIdentifier != null ) {
                throw new SAXParseException( "Repeated tag " + ACCOUNT_TAG, theLocator );
            }
            theCurrentIdentifier = SimpleStringIdentifier.create( attrs.getValue( IDENTIFIER_TAG ));
            theCurrentAccountData = new AccountData(
                    LidAccount.LidAccountStatus.valueOf( attrs.getValue( STATUS_TAG )),
                    Long.parseLong( attrs.getValue( CREATED_TAG ) ));

        } else if( ATTRIBUTE_TAG.equals( qName )) {
            if( theCurrentAttribute != null ) {
                throw new SAXParseException( "Have current " + ATTRIBUTE_TAG + " already", theLocator );
            }
            theCurrentAttribute = attrs.getValue( IDENTIFIER_TAG );
            
        } else if( GROUP_TAG.equals( qName )) {
            // no op

        } else {
            startElement1( namespaceURI, localName, qName, attrs );
        }
    }

    /**
     * Invoked when no previous start-element parsing rule has matched. Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @param attrs the Attributes at this element
     * @throws SAXException thrown if a parsing error occurrs
     */
    protected void startElement1(
            String     namespaceURI,
            String     localName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        log.error( "unknown qname " + qName );
    }
            
    /**
     * SAX says an element ends.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @throws SAXException thrown if a parsing error occurrs
     */
    @Override
    public void endElement(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        if( log.isInfoEnabled() ) {
            log.info( "SAX endElement: " + namespaceURI + ", " + localName + ", " + qName );
        }

        if( ACCOUNT_TAG.equals( qName )) {
            // no op

        } else if( ATTRIBUTE_TAG.equals( qName )) {
            theCurrentAccountData.addAttribute(
                    theCurrentAttribute,
                    theCharacters != null ? XmlUtils.cdataDescape( theCharacters.toString()) : "" );
            theCurrentAttribute = null;

        } else if( GROUP_TAG.equals( qName )) {
            theCurrentAccountData.addGroupIdentifier( SimpleStringIdentifier.create( theCharacters.toString() ));

        } else {
            endElement1( namespaceURI, localName, qName );
        }
    }

    /**
     * Invoked when no previous end-element parsing rule has matched. Allows subclasses to add to parsing.
     *
     * @param namespaceURI the URI of the namespace
     * @param localName the local name
     * @param qName the qName
     * @throws SAXException thrown if a parsing error occurrs
     */
    protected void endElement1(
            String namespaceURI,
            String localName,
            String qName )
        throws
            SAXException
    {
        log.error( "unknown qname " + qName );
    }
    
    /**
     * Reset the parser.
     */
    public void clearState()
    {
        theCharacters            = null;
        theCurrentIdentifier     = null;
        theCurrentAccountData    = null;
        theCurrentAttribute      = null;
        theCurrentCredentialType = null;

        theStack.clear();
    }

    /**
     * Throw exception in case of an Exception indicating an error.
     *
     * @param ex the Exception
     * @throws org.xml.sax.SAXParseException thrown if a parsing error occurs
     */
    public final void error(
            Throwable ex )
        throws
            SAXParseException
    {
        if( ex instanceof SAXParseException ) {
            throw ((SAXParseException)ex);
        } else {
            throw new FixedSAXParseException( theErrorPrefix, theLocator, ex );
        }
    }
    
    /**
     * Helper method to find a LidCredentialType based on its name.
     * 
     * @param className name of the Class
     * @return the LidCredentialType
     * @throws org.xml.sax.SAXParseException thrown if a problem occurred
     */
    protected LidCredentialType findCredentialType(
            String className )
        throws
            SAXParseException
    {
        for( LidCredentialType current : theAvailableCredentialTypes ) {
            if( current.getFullName().equals( className )) {
                return current;
            }
        }
        log.error( this, "Could not find LidCredentialType", className );

        throw new FixedSAXParseException( theErrorPrefix, theLocator, null );
    }

    /**
     * Obtain the preferred encoding id of this StoreEntryMapper.
     * 
     * @return the preferred encoding id
     */
    public String getPreferredEncodingId()
    {
        return ENCODING;
    }

    /**
     * Obtain the time a value was created.
     *
     * @param value the time a value was created.
     * @return the time created, in System.currentTimeMillis() format
     */
    public long getTimeCreated(
            SimpleLidAccount value )
    {
        return -1L;
    }

    /**
     * Obtain the time a value was last updated.
     *
     * @param value the time a value was last updated.
     * @return the time updated, in System.currentTimeMillis() format
     */
    public long getTimeUpdated(
            SimpleLidAccount value )
    {
        return -1L;
    }

    /**
     * Obtain the time a value was last read.
     *
     * @param value the time a value was last read.
     * @return the time read, in System.currentTimeMillis() format
     */
    public long getTimeRead(
            SimpleLidAccount value )
    {
        return -1L;
    }

    /**
     * Obtain the time a value will expire.
     *
     * @param value the time a value will expire.
     * @return the time will expire, in System.currentTimeMillis() format
     */
    public long getTimeExpires(
            SimpleLidAccount value )
    {
        return -1L;
    }

    /**
     * Obtain the SimpleLidAccount as a byte array.
     *
     * @param account the value
     * @return the byte array
     * @throws StoreValueEncodingException thrown if the value could not been encoded
     */
    public byte [] asBytes(
            SimpleLidAccount account )
        throws
            StoreValueEncodingException
    {
        StringBuilder buf = new StringBuilder();
        buf.append( "<" ).append( ACCOUNT_TAG );
        buf.append(  " " ).append( IDENTIFIER_TAG ).append( "=\"" ).append( XmlUtils.escape( account.getIdentifier().toExternalForm() )).append( "\"");
        buf.append(  " " ).append( CREATED_TAG ).append( "=\"" ).append( account.getTimeCreated() ).append( "\"");
        buf.append(  " " ).append( STATUS_TAG ).append( "=\"" ).append( XmlUtils.escape( account.getAccountStatus().name() )).append( "\">\n");

        for( String name : account.getAttributeKeys() ) {
            String value = account.getAttribute( name );
            
            buf.append( "<" ).append( ATTRIBUTE_TAG );
            buf.append(  " " ).append( IDENTIFIER_TAG ).append( "=\"" ).append( XmlUtils.escape( name )).append( "\">\n");
            buf.append( XmlUtils.escape( value ));
            buf.append( "</" ).append( ATTRIBUTE_TAG ).append( ">" );
        }
        for( Identifier id : account.getGroupIdentifiers() ) {
            buf.append( "<" ).append( GROUP_TAG ).append( ">" );
            buf.append( XmlUtils.escape( id.toExternalForm() ));
            buf.append( "</" ).append( GROUP_TAG ).append( ">" );
        }

        buf.append( "</" ).append( ACCOUNT_TAG ).append( ">" );
        try {
            return buf.toString().getBytes( CHARSET );

        } catch( UnsupportedEncodingException ex ) {
            throw new StoreValueEncodingException( ex );
        }
    }
    
    /**
     * The encoding to use.
     */
    public static final String ENCODING = SimpleLidAccountMapper.class.getName();

    /**
     * The character set to use.
     */
    public static final String CHARSET = "UTF-8";

    /**
     * The SAX parser factory to use.
     */
    protected static final SAXParserFactory theSaxParserFactory;
    static {
        theSaxParserFactory = SAXParserFactory.newInstance();
        theSaxParserFactory.setValidating( true );
    }

    /**
     * The identifier of the site at which the accounts are managed.
     */
    protected Identifier theSiteIdentifier;

    /**
     * The credential types known by the application.
     */
    protected LidCredentialType [] theAvailableCredentialTypes;

    /**
     * Our SAX parser.
     */
    protected SAXParser theParser;
    
    /**
     * The identifier of the SimpleLidAccount currently being parsed.
     */
    protected Identifier theCurrentIdentifier;
    
    /**
     * The remote Identifiers of the SimpleLidAccount currently being parsed.
     */
    protected ArrayList<Identifier> theRemoteIdentifiers;

    /**
     * The AccountData of the SimpleLidAccount currently being parsed.
     */
    protected AccountData theCurrentAccountData;
    
    /**
     * The name of the currently being parsed attribute in theObjectBeingParsed.
     */
    protected String theCurrentAttribute;
    
    /**
     * The name of the currently being parsed credential in theObjectBeingParsed.
     */
    protected LidCredentialType theCurrentCredentialType;

    /**
     * The error message prefix in case we need it.
     */
    protected String theErrorPrefix;

    /**
     * The Locator object that tells us where we are in the parsed file.
     */
    protected Locator theLocator;

    /**
     * The parse stack.
     */
    protected Stack<Object> theStack = new Stack<Object>();

    /**
     * The character String that is currently being parsed, if any.
     */
    protected StringBuilder theCharacters = null;

    /**
     * Java FixedSAXParseException's constructor is broken, so we created this workaround class.
     */
    static class FixedSAXParseException
            extends
                SAXParseException
    {
        private static final long serialVersionUID = 1L; // helps with serialization

        /**
         * Constructor.
         * 
         * @param message the error message
         * @param locator indicates the location of the error in the stream
         * @param cause the underlying cause, if any
         */
        public FixedSAXParseException(
                String    message,
                Locator   locator,
                Throwable cause )
        {
            super( message, locator );
            
            initCause( cause );
        }
    }
}
