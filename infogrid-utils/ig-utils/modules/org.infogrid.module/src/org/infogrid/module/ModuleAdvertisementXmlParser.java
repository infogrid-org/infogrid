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
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This helper program takes XML advertisements and instantiates them.
 *
 * FIXME: support XML namespaces.
 */
public class ModuleAdvertisementXmlParser
        extends
            AbstractModuleDependenciesXmlParser
        implements
            ModuleXmlTags
{
    /**
     * Constructor.
     */
    public ModuleAdvertisementXmlParser()
    {
        super();
    }

    /**
     * Parse the XML file.
     *
     * @param theStream the stream from which we read the ModuleAdvertisement
     * @param file the file that is being parsed, for relative path construction and error reporting
     * @param defaultBuildDate if given, use this Date as the buildDate for the ModuleAdvertisement if none is given
     * @return the read ModuleAdvertisement (may be subclass)
     * @throws IOException an input/output error occurred
     * @throws ModuleConfigurationException a configuration error occurred during parsing or setup
     */
    public synchronized ModuleAdvertisement readAdvertisement(
            InputStream theStream,
            File        file,
            Date        defaultBuildDate )
        throws
            IOException,
            ModuleConfigurationException
    {
        // initialize first
        initialize();

        buildDate = defaultBuildDate;

        // now do it
        try {
            SAXParser theParser = theFactory.newSAXParser();
            theFile             = file;

            theParser.parse( theStream, this );

        } catch( ParserConfigurationException ex ) {
            throw new ModuleConfigurationException( null, "Could not parse file " + theFile.getCanonicalPath(), ex );
        } catch( SAXException ex ) {
            throw new ModuleConfigurationException( null, "Could not parse file " + theFile.getCanonicalPath(), ex );
        }

        ModuleAdvertisement ret;

        if( STANDARDMODULE_TAG.equals( keyword )) {
            ret = StandardModuleAdvertisement.create1(
                    name,
                    version,
                    usernames,
                    userdescriptions,
                    buildDate,
                    null, // FIXME, license
                    file,
                    copyIntoNewArray( buildTimeDependencies, ModuleRequirement.class ),
                    copyIntoNewArray( runTimeDependencies,   ModuleRequirement.class ),
                    copyIntoNewArray( jars,                  File.class ),
                    moduleParameterValues,
                    moduleParameterDefaults,
                    copyIntoNewArray( capabilities,          ModuleCapability.class ),
                    activationClassName,
                    activationMethodName,
                    deactivationMethodName,
                    configurationClassName,
                    configurationMethodName,
                    runClassName,
                    runMethodName );

        } else if( MODELMODULE_TAG.equals( keyword )) {
            ret = ModelModuleAdvertisement.create1(
                    name,
                    version,
                    usernames,
                    userdescriptions,
                    buildDate,
                    null, // FIXME, license
                    file,
                    copyIntoNewArray( buildTimeDependencies, ModuleRequirement.class ),
                    copyIntoNewArray( runTimeDependencies,   ModuleRequirement.class ),
                    copyIntoNewArray( jars,                  File.class ),
                    moduleParameterValues,
                    moduleParameterDefaults );

        } else {
            throw new IllegalArgumentException( "Unexpected type of module: " + keyword );
        }
        return ret;
    }

    /**
     * Initialize local variables.
     */
    @Override
    protected void initialize()
    {
        keyword                           = null;
        name                              = null;
        version                           = null;
        usernames                         = new HashMap<String,String>();
        userdescriptions                  = new HashMap<String,String>();
        buildDate                         = null;
        jars                              = new ArrayList<File>();
        activationClassName               = null;
        activationMethodName              = "activate";
        deactivationMethodName            = "deactivate";
        configurationClassName            = null;
        configurationMethodName           = "configure";
        runClassName                      = null;
        runMethodName                     = "main";
        argumentCombinations              = null;
        arguments                         = null;
        capabilities                      = new ArrayList<ModuleCapability>();
        interfaceNames                    = null;
        implementationName                = null;

        super.initialize();
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
    public void startElement1(
            String     namespaceURI,
            String     sName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( STANDARDMODULE_TAG.equals( qName ) ) {
            keyword = qName;

        } else if( MODELMODULE_TAG.equals( qName ) ) {
            keyword = qName;

        } else if( NAME_TAG.equals( qName )) {
            // no op

        } else if( VERSION_TAG.equals( qName )) {
            // no op

        } else if( USERNAME_TAG.equals( qName )) {
            locale = attrs.getValue( LOCALE_PAR );

        } else if( USERDESCRIPTION_TAG.equals( qName )) {
            locale = attrs.getValue( LOCALE_PAR );

        } else if( BUILD_TIME_TAG.equals( qName )) {
            // no op

        } else if( PROVIDES_TAG.equals( qName )) {
            // no op

        } else if( JAR_TAG.equals( qName )) {
            // no op

        } else if( ACTIVATIONCLASS_TAG.equals( qName )) {
            // no op

        } else if( CONFIGURATIONCLASS_TAG.equals( qName )) {
            // no op

        } else if( RUNCLASS_TAG.equals( qName )) {
            // no op

        } else if( CAPABILITY_TAG.equals( qName )) {
            implementationName   = null;
            argumentCombinations = new ArrayList<ModuleCapability.ArgumentCombination>();
            interfaceNames       = new ArrayList<String>();

        } else if( INTERFACE_TAG.equals( qName )) {
            // no op

        } else if( IMPLEMENTATION_TAG.equals( qName )) {
            // no op

        } else if( ARGUMENTCOMBINATION_TAG.equals( qName )) {
            arguments = new ArrayList<String>();

        } else if( ARG_TAG.equals( qName )) {
            // no op

        } else {
            throw new SAXParseException( "Don't know anything about opening tag " + qName, theLocator );
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
    public void endElement1(
            String namespaceURI,
            String sName,
            String qName )
        throws
            SAXException
    {
        if( STANDARDMODULE_TAG.equals( qName ) ) {
            keyword = qName;

        } else if( MODELMODULE_TAG.equals( qName ) ) {
            keyword = qName;

        } else if( NAME_TAG.equals( qName )) {
            name = lastString;

        } else if( VERSION_TAG.equals( qName )) {
            version = lastString;

        } else if( USERNAME_TAG.equals( qName )) {
            usernames.put( locale, lastString );

        } else if( USERDESCRIPTION_TAG.equals( qName )) {
            userdescriptions.put( locale, lastString );

        } else if( BUILD_TIME_TAG.equals( qName )) {
            try {
                DateFormat myDateFormat = (DateFormat) theDefaultDateFormat.clone(); // not thread-safe

                buildDate = myDateFormat.parse( lastString );
            } catch( ParseException ex ) {
                throw new SAXParseException( "Date value cannot be parsed: " + ex, theLocator );
            }

        } else if( PROVIDES_TAG.equals( qName )) {
            // no op

        } else if( JAR_TAG.equals( qName )) {
            jars.add( jarFile( lastString ));

        } else if( ACTIVATIONCLASS_TAG.equals( qName )) {
            activationClassName = lastString;

        } else if( CONFIGURATIONCLASS_TAG.equals( qName )) {
            configurationClassName = lastString;

        } else if( RUNCLASS_TAG.equals( qName )) {
            runClassName = lastString;


        } else if( CAPABILITY_TAG.equals( qName )) {
            ModuleCapability currentCapability = ModuleCapability.create1(
                    copyIntoNewArray( interfaceNames, String.class ),
                    implementationName,
                    copyIntoNewArray( argumentCombinations, ModuleCapability.ArgumentCombination.class ));

            capabilities.add( currentCapability );

            interfaceNames       = null;
            implementationName   = null;
            argumentCombinations = null;

        } else if( INTERFACE_TAG.equals( qName )) {
            interfaceNames.add( lastString );
        } else if( IMPLEMENTATION_TAG.equals( qName )) {
            implementationName = lastString;
        } else if( ARGUMENTCOMBINATION_TAG.equals( qName )) {

            argumentCombinations.add(
                    ModuleCapability.createArgumentCombination1(
                            copyIntoNewArray( arguments, String.class )) );

            arguments = null;

        } else if( ARG_TAG.equals( qName )) {
            arguments.add( lastString );
        } else if( PARAMETER_TAG.equals( qName )) {
            // FIXME
        } else {
            throw new SAXParseException( "Don't know anything about closing tag " + qName, theLocator );
        }
        lastString = null;
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
     * Helper method to construct the path of a JAR file.
     *
     * @param string name of the File as given in the ModuleAdvertisement.
     * @return the File
     * @throws SAXException thrown if the filesystem could not be queried
     */
    protected File jarFile(
            String string )
        throws
            SAXException
    {
        try {
            File ret;

            // FIXME this looks like a mess
            if( string.startsWith( "/" )) {
                ret = new File( string );
            } else {
                File candidate = new File( theFile.getCanonicalFile().getParentFile(), string );

                if( candidate.canRead() ) {
                    ret = candidate;
                } else {
                    File distDir = new File( theFile.getCanonicalFile().getParentFile().getParentFile(), "dist" );
                    ret = new File( distDir, string );
                }
            }
            return ret;

        } catch( IOException ex ) {
            throw new SAXException( ex );
        }
    }

    /**
     * The outermost keyword indicating what type of Module it is.
     */
    protected String keyword;

    /**
     * The name element.
     */
    protected String name;

    /**
     * The version element.
     */
    protected String version;

    /**
     * The locale attribute.
     */
    protected String locale;

    /**
     * The user names, keyed by locale.
     */
    protected Map<String,String> usernames;

    /**
     * The user descriptions, keyed by locale.
     */
    protected Map<String,String> userdescriptions;
    
    /**
     * The time when the Module was built.
     */
    protected Date buildDate;

    /**
     * The JAR files that we provide.
     */
    protected ArrayList<File> jars = new ArrayList<File>();

    /**
     * Name of the activation class.
     */
    protected String activationClassName;

    /**
     * Name of the activation method.
     */
    protected String activationMethodName = "activate";

    /**
     * Name of the deactivation method.
     */
    protected String deactivationMethodName = "deactivate";

    /**
     * Name of the configuration class.
     */
    protected String configurationClassName;

    /**
     * Name of the configuration method.
     */
    protected String configurationMethodName = "configure";

    /**
     * Name of the run class.
     */
    protected String runClassName;

    /**
     * Name of the run method.
     */
    protected String runMethodName = "main";

    /**
     * The set of ArgumentCombinations that we are currently parsing. Only important during parsing.
     */
    protected ArrayList<ModuleCapability.ArgumentCombination> argumentCombinations;

    /**
     * The list of arguments that we are currently parsing. Only important during parsing.
     */
    protected ArrayList<String> arguments;

    /**
     * The list of ModuleCapabilities of this Module. Only important during parsing.
     */
    protected ArrayList<ModuleCapability> capabilities = new ArrayList<ModuleCapability>();

    /**
     * The current set of interface names of this capability. Only important during parsing.
     */
    protected ArrayList<String> interfaceNames;

    /**
     * The implementation name of the currently parsed capability. Only important during parsing.
     */
    protected String implementationName;
}
