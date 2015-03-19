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

package org.infogrid.util;

import java.text.ParseException;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.SimpleStringRepresentationParameters;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationDirectory;
import org.infogrid.util.text.StringRepresentationDirectorySingleton;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * Subclasses the JDK's ParseException to support the InfoGrid localization framework.
 */
public abstract class LocalizedParseException
    extends
        ParseException
    implements
        LocalizedException
{
    private static final long serialVersionUID = 1L; // helps with serialization
    private static final Log  log              = Log.getLogInstance( LocalizedParseException.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param string the String that could not be parsed
     * @param message the Exception's message, if any
     * @param errorOffset the offset, into the string, where the error occurred
     * @param cause the underlying cause, if any
     */
    protected LocalizedParseException(
            String    string,
            String    message,
            int       errorOffset,
            Throwable cause )
    {
        super( message, errorOffset );
        initCause( cause );

        theString = string;
    }

    /**
     * Obtain the String that could not be parsed.
     *
     * @return the String that could not be parsed
     */
    public String getString()
    {
        return theString;
    }

    /**
     * Return the explicitly set message, or the localized message if non was explicitly set.
     *
     * @return the message
     */
    @Override
    public String getMessage()
    {
        String ret = super.getMessage();
        if( ret == null ) {
            ret = getLocalizedMessage();
        }
        return ret;
    }

    /**
     * Determine the correct internationalized string that can be shown to the
     * user. Use a default formatter.
     *
     * @return the internationalized string
     */
    @Override
    public String getLocalizedMessage()
    {
        try {
            return toStringRepresentation(
                    StringRepresentationDirectorySingleton.getSingleton().get( StringRepresentationDirectory.TEXT_PLAIN_NAME ),
                    StringRepresentationParameters.EMPTY );

        } catch( StringifierException ex ) {
            log.error( ex );
            // now we are in trouble, we can't do super.getLocalizedMessage() because we'd be right back here
            return getClass().getName() + ": " + getCause().getLocalizedMessage();
        }
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { getCause().getMessage(), getCause().getLocalizedMessage() };
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return AbstractLocalizedException.constructStringRepresentation(
                this,
                rep,
                pars,
                findResourceHelperForLocalizedMessage(),
                getLocalizationParameters(),
                findStringRepresentationParameter() );
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return AbstractLocalizedException.constructStringRepresentationLinkStart(
                this,
                rep,
                pars,
                findResourceHelperForLocalizedMessage(),
                getLocalizationParameters(),
                findStringRepresentationLinkStartParameter() );
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return AbstractLocalizedException.constructStringRepresentationLinkEnd(
                this,
                rep,
                pars,
                findResourceHelperForLocalizedMessage(),
                getLocalizationParameters(),
                findStringRepresentationLinkEndParameter() );
    }

    /**
     * Allow subclasses to override which ResourceHelper to use.
     *
     * @return the ResourceHelper to use
     */
    protected ResourceHelper findResourceHelperForLocalizedMessage()
    {
        return ResourceHelper.getInstance( getClass() );
    }

    /**
     * Allow subclasses to override which ResourceHelper to use.
     *
     * @return the ResourceHelper to use
     */
    protected ResourceHelper findResourceHelperForLocalizedMessageViaEnclosingClass()
    {
        String className = getClass().getName();
        int    dollar = className.indexOf( '$' );
        if( dollar >= 0 ) {
            className = className.substring( 0, dollar );
        }
        return ResourceHelper.getInstance( className, getClass().getClassLoader() );
    }

    /**
     * Allow subclasses to override which key to use in the Resource file for the string representation.
     *
     * @return the key
     */
    protected String findStringRepresentationParameter()
    {
        return STRING_REPRESENTATION_KEY;
    }

    /**
     * Allow subclasses to override which key to use in the Resource file for the link start string representation.
     *
     * @return the key
     */
    protected String findStringRepresentationLinkStartParameter()
    {
        return STRING_REPRESENTATION_LINK_START_KEY;
    }

    /**
     * Allow subclasses to override which key to use in the Resource file for the link end string representation.
     *
     * @return the key
     */
    protected String findStringRepresentationLinkEndParameter()
    {
        return STRING_REPRESENTATION_LINK_END_KEY;
    }

    /**
     * This method can be invoked by subclasses to obtain a suitable message key
     * for the same resource file for all inner classes.
     *
     * @param key the key
     * @return the modified key
     */
    protected String findParameterViaEnclosingClass(
            String key )
    {
        String className = getClass().getName();
        String ret;
        int    dollar = className.indexOf( '$' );
        if( dollar >= 0 ) {
            ret = className.substring( dollar+1 ) + "-" + key;
        } else {
            ret = key;
        }
        return ret;
    }

    /**
     * The String that could not be parsed.
     */
    protected String theString;
}
