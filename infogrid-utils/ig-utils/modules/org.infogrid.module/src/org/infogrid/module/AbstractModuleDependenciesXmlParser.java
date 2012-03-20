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

package org.infogrid.module;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Factors out common functionality for the parsing of ModuleDependencies.
 */
public abstract class AbstractModuleDependenciesXmlParser
        extends
            DefaultHandler
        implements
            ModuleDependenciesXmlTags
{
    /**
     * Constructor.
     */
    protected AbstractModuleDependenciesXmlParser()
    {
        theFactory = SAXParserFactory.newInstance();
        theFactory.setValidating( false );
    }

    /**
     * Initialize local variables.
     */
    protected void initialize()
    {
        lastString                        = null;
        buildTimeDependencies             = new ArrayList<ModuleRequirement>();
        runTimeDependencies               = new ArrayList<ModuleRequirement>();
        moduleParameterValues             = null;
        moduleParameterDefaults           = null;
        requirementParameterValues        = null;
        requirementParameterDefaults      = null;
        currentModuleRequirementBuildTime = false;
        currentModuleRequirementRunTime   = false;
        currentModuleRequirementName      = null;
        currentModuleRequirementVersion   = null;
        inDependenciesSection             = false;

        theLocator = new MyLocator();
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

        if( DEPENDENCIES_TAG.equals( qName )) {

            inDependenciesSection = true;

        } else if( REQUIRES_TAG.equals( qName )) {
            requirementParameterValues   = new HashMap<String,String>();
            requirementParameterDefaults = new HashMap<String,String>();

            currentModuleRequirementName = attrs.getValue( NAME_PAR );
            if( currentModuleRequirementName == null ) {
                throw new SAXParseException( "ModuleRequirement cannot have empty name", theLocator );
            }
            currentModuleRequirementVersion = attrs.getValue( VERSION_PAR ); // may or may not be given

            String mode = attrs.getValue( MODE_PAR );
            if( mode == null || "".equals( mode ) || MODE_PAR_BOTH.equalsIgnoreCase( mode )) {
                currentModuleRequirementBuildTime = true;
                currentModuleRequirementRunTime   = true;

            } else if( MODE_PAR_BUILDTIME.equalsIgnoreCase( mode )) {
                currentModuleRequirementBuildTime = true;
                currentModuleRequirementRunTime   = false;

            } else if( MODE_PAR_RUNTIME.equalsIgnoreCase( mode )) {
                currentModuleRequirementBuildTime = false;
                currentModuleRequirementRunTime   = true;

            } else {
                 throw new SAXParseException( "Cannot understand mode of dependency: " + mode, theLocator );
            }

        } else if( PARAMETER_TAG.equals( qName )) {
            String parName    = attrs.getValue( NAME_PAR );
            String parDefault = attrs.getValue( DEFAULT_PAR );
            String parValue   = attrs.getValue( VALUE_PAR );

            if( parName == null ) {
                throw new SAXParseException( "No name was given in the parameter tag", theLocator );

            } else if( !inDependenciesSection ) {
                if( parDefault == null ) {
                    if( parValue != null ) {
                        if( moduleParameterValues == null ) {
                            moduleParameterValues = new HashMap<String,String>();
                        }
                        moduleParameterValues.put( parName, parValue );

                    } else {
                        throw new SAXParseException( "No value or default was given for parameter " + parName, theLocator );

                    }
                } else if( parValue == null ) {
                    if( moduleParameterDefaults == null ) {
                        moduleParameterDefaults = new HashMap<String,String>();
                    }
                    moduleParameterDefaults.put( parName, parDefault );

                } else {
                    throw new SAXParseException( "Specify either value or default, not both, for parameter " + parName, theLocator );
                }

            } else {
                // this is for a Module Requirement
                if( parDefault == null ) {
                    if( parValue != null ) {
                        if( requirementParameterValues == null ) {
                            requirementParameterValues = new HashMap<String,String>();
                        }
                        requirementParameterValues.put( parName, parValue );

                    } else {
                        throw new SAXParseException( "No value or default was given for parameter " + parName, theLocator );
                    }

                } else if( parValue == null ) {
                    if( requirementParameterDefaults == null ) {
                        requirementParameterDefaults = new HashMap<String,String>();
                    }
                    requirementParameterDefaults.put( parName, parDefault );

                } else {
                    throw new SAXParseException( "Specify either value or default, not both, for parameter " + parName, theLocator );
                }
            }
        } else {
            startElement1( namespaceURI, sName, qName, attrs );
        }
    }

    /**
     * Allows subclasses to add to parsing.
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
     */
    protected void startElement1(
            String     namespaceURI,
            String     sName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        throw new SAXParseException( "Don't know anything about opening tag " + qName, theLocator );
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
        if( DEPENDENCIES_TAG.equals( qName )) {
            inDependenciesSection = false;

        } else if( REQUIRES_TAG.equals( qName )) {
            if( currentModuleRequirementName != null ) {
                ModuleRequirement req = ModuleRequirement.create1(
                        currentModuleRequirementName,
                        currentModuleRequirementVersion,
                        requirementParameterValues,
                        requirementParameterDefaults );

                if( currentModuleRequirementBuildTime ) {
                    buildTimeDependencies.add( req );
                }
                if( currentModuleRequirementRunTime ) {
                    // this is NOT an "else", it can be both
                    runTimeDependencies.add( req );
                }
            }

        } else if( PARAMETER_TAG.equals( qName )) {
            // FIXME
        } else {
            endElement1( namespaceURI, sName, qName );
        }
        lastString = null;
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
    public void endElement1(
            String namespaceURI,
            String sName,
            String qName )
        throws
            SAXException
    {
        throw new SAXParseException( "Don't know anything about closing tag " + qName, theLocator );
    }

    /**
     * Copy elements into a new Array of a certain type.
     *
     * @param theCollection the Collection from which to take the data
     * @param arrayComponentType the type of array that shall be allocated
     * @return the new Array of type arrayComponentType filled with the data from theCollection
     * @param <T> the type
     */
    @SuppressWarnings("unchecked")
    protected static <T> T [] copyIntoNewArray(
            Collection theCollection,
            Class<T>   arrayComponentType )
    {
        Object [] ret = (Object []) Array.newInstance(
                arrayComponentType,
                theCollection.size() );

        Iterator theIter = theCollection.iterator();
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = theIter.next();
        }
        return (T []) ret;
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
     * The ParserFactory that we use.
     */
    protected SAXParserFactory theFactory;

    /**
     * Data source that is being parsed.
     */
    protected File theFile;

    /**
     * The last string we found during parsing.
     */
    protected String lastString;

    /**
     * Where are we in parsing the stream.
     */
    protected MyLocator theLocator;

    /**
     * The build-time dependencies, as ArrayList<ModuleRequirement>.
     */
    protected ArrayList<ModuleRequirement> buildTimeDependencies = new ArrayList<ModuleRequirement>();

    /**
     * The run-time dependencies, as ArrayList<ModuleRequirement>.
     */
    protected ArrayList<ModuleRequirement> runTimeDependencies = new ArrayList<ModuleRequirement>();

    /**
     * The parameter-value pairs for this ModuleRequirement that cannot be overridden.
     */
    protected Map<String,String> requirementParameterValues;

    /**
     * The parameter-value pairs for this ModuleRequirement that may be overridden.
     */
    protected Map<String,String> requirementParameterDefaults;

    /**
     * The parameter-value pairs for this Module that cannot be overridden.
     */
    protected Map<String,String> moduleParameterValues;

    /**
     * The parameter-value pairs for this Module that may be overridden.
     */
    protected Map<String,String> moduleParameterDefaults;

    /**
     * True when we are inside the dependencies section.
     */
    protected boolean inDependenciesSection = false;

    /**
     * The name of the current ModuleRequirement.
     */
    protected String currentModuleRequirementName;

    /**
     * The version of the current ModuleRequirement.
     */
    protected String currentModuleRequirementVersion;

    /**
     * Is the current ModuleRequirement a build-time requirement.
     */
    protected boolean currentModuleRequirementBuildTime;

    /**
     * Is the current ModuleRequirement a run-time requirement.
     */
    protected boolean currentModuleRequirementRunTime;

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
