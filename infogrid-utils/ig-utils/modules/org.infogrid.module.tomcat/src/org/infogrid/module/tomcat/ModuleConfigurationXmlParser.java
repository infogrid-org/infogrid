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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.infogrid.module.AbstractModuleDependenciesXmlParser;
import org.infogrid.module.ModuleConfigurationException;
import org.infogrid.module.ModuleRequirement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author jernst
 */
public class ModuleConfigurationXmlParser
        extends
            AbstractModuleDependenciesXmlParser
        implements
            ModuleRequirementsConfigurationXmlTags
{
    /**
     * Constructor.
     */
    public ModuleConfigurationXmlParser()
    {
        super();
    }

    public ModuleRequirementsConfiguration readConfiguration(
            InputStream stream,
            File        file )
        throws
            ModuleConfigurationException,
            IOException
    {
        // initialize first
        initialize();

        // now do it
        try {
            SAXParser theParser = theFactory.newSAXParser();
            theFile             = file;

            theParser.parse( stream, this );

        } catch( ParserConfigurationException ex ) {
            throw new ModuleConfigurationException( null, "Could not parse file " + theFile.getCanonicalPath(), ex );
        } catch( SAXException ex ) {
            throw new ModuleConfigurationException( null, "Could not parse file " + theFile.getCanonicalPath(), ex );
        }

        ModuleRequirementsConfiguration ret = ModuleRequirementsConfiguration.create(
                repositories,
                copyIntoNewArray( runTimeDependencies, ModuleRequirement.class ));
        return ret;
    }
    
    /**
     * Initialize local variables.
     */
    @Override
    protected void initialize()
    {
        repositories = new HashMap<String,File>();

        super.initialize();
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
    @Override
    protected void startElement1(
            String     namespaceURI,
            String     sName,
            String     qName,
            Attributes attrs )
        throws
            SAXException
    {
        if( MODULESETTINGS_TAG.equals( qName )) {
            // no op

        } else if( REPOSITORIES_TAG.equals( qName )) {
            // no op
            
        } else if( REPOSITORY_TAG.equals( qName )) {
            String settingsPath = attrs.getValue( SETTINGSPATH_ATT );
            String shareName    = attrs.getValue( SHARENAME_ATT );

            File settingsFile = new File( settingsPath );
            if( repositories.put( shareName, settingsFile ) != null ) {
                throw new SAXParseException( "Repository with sharename " + shareName + " must not be declared more than once", theLocator );
            }
            
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
        if( MODULESETTINGS_TAG.equals( qName )) {
            // no op

        } else if( REPOSITORIES_TAG.equals( qName )) {
            // no op

        } else if( REPOSITORY_TAG.equals( qName )) {
            // no op

        } else {
            throw new SAXParseException( "Don't know anything about closing tag " + qName, theLocator );
        }
    }

    protected HashMap<String,File> repositories;
}
