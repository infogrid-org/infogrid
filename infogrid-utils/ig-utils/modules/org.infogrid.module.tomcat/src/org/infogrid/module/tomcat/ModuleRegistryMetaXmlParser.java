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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module.tomcat;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This helper takes ModuleRegistryMeta XML files and parses them.
 */
public class ModuleRegistryMetaXmlParser
        extends
            DefaultHandler
        implements
            ModuleRegistryMetaXmlTags
{
    /**
     * Constructor.
     */
    public ModuleRegistryMetaXmlParser()
    {
        theFactory = SAXParserFactory.newInstance();
        theFactory.setValidating( false );
    }

    /**
     * Parse the XML file.
     *
     * @param theStream the stream from which we read the ModuleAdvertisement
     * @param file the file that is being parsed, for relative path construction and error reporting
     * @param defaultBuildDate if given, use this Date as the buildDate for the ModuleAdvertisement if none is given
     * @return the read ModuleAdvertisement (may be subclass)
     * @throws IOException an input/output error occurred
     */
    public synchronized ModuleRegistryMeta readRegistrySettings(
            InputStream theStream,
            File        source )
        throws
            ModuleRegistryMetaParseException,
            IOException
    {
        // initialize first
        initialize();

        // now do it
        try {
            SAXParser theParser = theFactory.newSAXParser();
            theSource           = source;

            theParser.parse( theStream, this );

        } catch( ParserConfigurationException ex ) {
            throw new ModuleRegistryMetaParseException( "Could not parse file " + theSource.getCanonicalPath(), ex );
        } catch( SAXException ex ) {
            throw new ModuleRegistryMetaParseException( "Could not parse file " + theSource.getCanonicalPath(), ex );
        }

        ModuleRegistryMeta ret = ModuleRegistryMeta.create(
                theSource,
                paths );

        return ret;
    }

    /**
     * Initialize local variables.
     */
    protected void initialize()
    {
        lastString = null;
        theSource  = null;
        paths      = null;

        theLocator = new MyLocator();
    }

    /**
     * Receive a Locator object for document events.
     *
     * @param locator A locator for all SAX document events.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     * @see org.xml.sax.Locator
     */
    @Override
    public void setDocumentLocator(
            Locator locator )
    {
        theLocator.update(
                locator.getPublicId(),
                locator.getSystemId(),
                locator.getLineNumber(),
                locator.getColumnNumber());
    }

    /**
     * Callback indicating that a new XML element starts.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param sName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attrs The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @throws SAXException if an error occurred
     * @see #endElement
     */
    @Override
    public void startElement(
            String     namespaceURI,
            String     sName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        lastString = null;

        if( MODULEREGISTRY_TAG.equals( qName ) ) {
            paths = new ArrayList<String>();

        } else if( MODULEADVERTISEMENTPATH_TAG.equals( qName ) ) {
            String path = attrs.getValue( PATH_TAG );
            if( path == null || path.isEmpty() ) {
                throw new SAXParseException( "Element " + MODULEADVERTISEMENTPATH_TAG + " must have non-empty attribute " + PATH_TAG, theLocator );
            }
            paths.add( path );

        } else {
            throw new SAXParseException( "Don't know anything about opening tag " + qName, theLocator );
        }
    }

    /**
     * Callback indicating that we found some characters.
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     */
    @Override
    public void characters(
            char [] ch,
            int     start,
            int     length )
    {
        if( lastString == null ) {
            lastString = new String( ch, start, length );
        } else {
            lastString += new String( ch, start, length );
        }
    }

    /**
     * Callback indicating that an XML element starts.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param sName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws SAXException a parse error occurred
     */
    @Override
    public void endElement(
            String namespaceURI,
            String sName,
            String qName )
        throws
            SAXException
    {
        if( MODULEREGISTRY_TAG.equals( qName ) ) {
            // no op

        } else if( MODULEADVERTISEMENTPATH_TAG.equals( qName ) ) {
            // no op

        } else {
            throw new SAXParseException( "Don't know anything about closing tag " + qName, theLocator );
        }
        lastString = null;
    }

    /**
     * The ParserFactory that we use.
     */
    protected SAXParserFactory theFactory;

    /**
     * Data source that is being parsed.
     */
    protected File theSource;

    /**
     * The last string we found during parsing.
     */
    protected String lastString;

    /**
     * The ModuleAdvertisementPaths found so far.
     */
    protected ArrayList<String> paths;
    
    /**
     * Where are we in parsing the stream.
     */
    protected MyLocator theLocator;

    /**
     * Simple Locator implementation.
     */
    protected static class MyLocator
        implements
            Locator
    {
        /**
         * Return the public identifier for the current document event.
         *
         * @return A string containing the public identifier, or
         *         null if none is available.
         * @see #getSystemId
         */
        public String getPublicId()
        {
            return thePublicId;
        }

        /**
         * Return the system identifier for the current document event.
         *
         * @return A string containing the system identifier, or null
         *         if none is available.
         * @see #getPublicId
         */
        public String getSystemId()
        {
            return theSystemId;
        }

        /**
         * Return the line number where the current document event ends.
         *
         * @return The line number, or -1 if none is available.
         * @see #getColumnNumber
         */
        public int getLineNumber()
        {
            return theLineNumber;
        }

        /**
         * Return the column number where the current document event ends.
         *
         * @return The column number, or -1 if none is available.
         * @see #getLineNumber
         */
        public int getColumnNumber()
        {
            return theColumnNumber;
        }

        /**
         * This allows us to update what is held by this object.
         *
         * @param newPublicId the new public ID
         * @param newSystemId the new system ID
         * @param newLineNumber the new line number
         * @param newColumnNumber the new column number
         */
        void update(
                String newPublicId,
                String newSystemId,
                int newLineNumber,
                int newColumnNumber )
        {
            thePublicId     = newPublicId;
            theSystemId     = newSystemId;
            theLineNumber   = newLineNumber;
            theColumnNumber = newColumnNumber;
        }

        /**
         * The XML public ID.
         */
        protected String thePublicId;

        /**
         * The XML system ID.
         */
        protected String theSystemId;

        /**
         * The current line number.
         */
        protected int theLineNumber;

        /**
         * The current column number.
         */
        protected int theColumnNumber;
    }
}
