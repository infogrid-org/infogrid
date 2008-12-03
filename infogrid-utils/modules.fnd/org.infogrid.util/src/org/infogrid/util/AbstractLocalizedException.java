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

package org.infogrid.util;

import java.text.MessageFormat;
import org.infogrid.util.text.HasStringRepresentation;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationContext;

/**
 * This is a supertype for Exceptions that knows how to internationalize themselves.
 * Given that Exceptions carry all their data, it is a lot easier to to
 * ask the Exception how to internationalize itself, than to write outside
 * code to do so.
 */
public abstract class AbstractLocalizedException
        extends
            Exception
        implements
            LocalizedException,
            HasStringRepresentation
{
    /**
     * Constructor.
     */
    public AbstractLocalizedException()
    {
    }

    /**
     * Constructor with a message.
     *
     * @param msg the message
     */
    public AbstractLocalizedException(
            String msg )
    {
        super( msg );
    }

    /**
     * Constructor with no message but a cause.
     *
     * @param cause the Throwable that caused this Exception
     */
    public AbstractLocalizedException(
            Throwable cause )
    {
        super( cause );
    }

    /**
     * Constructor with a message and a cause.
     *
     * @param msg the message
     * @param cause the Exception that caused this Exception
     */
    public AbstractLocalizedException(
            String    msg,
            Throwable cause )
    {
        super( msg, cause );
    }

    /**
     * Obtain localized message, per JDK 1.5.
     *
     * @return localized message
     */
    @Override
    public String getLocalizedMessage()
    {
        return getLocalizedMessage( null );
    }

    /**
     * Determine the correct internationalized string that can be shown to the
     * user when the LocalizedException is thrown.
     *
     * @param formatter the formatter to use for data objects to be displayed as part of the message
     * @return the internationalized string
     */
    public String getLocalizedMessage(
            LocalizedObjectFormatter formatter )
    {
        return constructLocalizedMessage(
                this,
                findResourceHelperForLocalizedMessage(),
                getLocalizationParameters(),
                findMessageParameter(),
                formatter );
    }
    
    /**
     * Factored out formatting, so several exception classes can reference the same code.
     * 
     * @param ex the exception to be localized
     * @param theHelper the ResourceHelper to use
     * @param params the localization parameters to use
     * @param messageParameter the name of the message parameter to use with the ResourceHelper
     * @param formatter the formatter to use for data objects to be displayed as part of the message
     * @return the internationalized string
     */
    static String constructLocalizedMessage(
            Exception                ex,
            ResourceHelper           theHelper,
            Object []                params,
            String                   messageParameter,
            LocalizedObjectFormatter formatter )
    {
        Throwable cause = ex.getCause();
        if( cause != null && cause instanceof LocalizedException ) {
             return ((LocalizedException)cause).getLocalizedMessage( formatter );
        }

        String message = theHelper.getResourceStringOrDefault( messageParameter, null );
        
        Class c = ex.getClass();

        while( message == null && ! Object.class.equals( c )) {
            c = c.getSuperclass();

            theHelper = ResourceHelper.getInstance( c );
            if( theHelper == null ) {
                // built-in JDK classes
                break;
            }
            message = theHelper.getResourceStringOrDefault( MESSAGE_PARAMETER, null );
        }
        if( message == null ) {
            message = ex.getClass().getName();
        }

        if( params != null ) {
            
            Object [] formattedParams;
            if( formatter != null ) {
                formattedParams = new Object[ params.length ];
                for( int i=0 ; i<formattedParams.length ; ++i ) {
                    formattedParams[i] = formatter.asLocalizedString( params[i] );
                }
            } else {
                formattedParams = params;
            }
            
            try {
                message = MessageFormat.format( message, formattedParams );

            } catch( IllegalArgumentException ex2 ) {
                message = message + "(error while formatting translated message)";
            }
        }
        return message;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */    
    public abstract Object [] getLocalizationParameters();

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     * This is only a default implementation; subclasses will want to override.
     * 
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @return String representation
     */
    public String toStringRepresentation(
            StringRepresentation        rep,
            StringRepresentationContext context )
    {
        Throwable cause     = getCause();
        Throwable rootCause = cause;

        if( rootCause != null ) {
            while( true ) {
                Throwable t = rootCause.getCause();
                if( t == null ) {
                    break;
                }
                rootCause = t;
            }
        }
        
        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                STRING_REPRESENTATION_KEY,
                getMessage(),
                getLocalizedMessage(),
                getStackTrace(),
                cause,
                cause != null ? cause.getMessage() : null,
                cause != null ? cause.getLocalizedMessage() : null,
                cause != null ? cause.getStackTrace() : null,
                rootCause,
                rootCause != null ? rootCause.getMessage() : null,
                rootCause != null ? rootCause.getLocalizedMessage() : null,
                rootCause != null ? rootCause.getStackTrace() : null );

        return ret;
    }

    /**
     * Obtain the start part of a String representation of this MeshBase that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @return String representation
     */
    public final String toStringRepresentationLinkStart(
            StringRepresentation        rep,
            StringRepresentationContext context )
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this MeshBase that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param context the StringRepresentationContext of this object
     * @return String representation
     */
    public final String toStringRepresentationLinkEnd(
            StringRepresentation        rep,
            StringRepresentationContext context )
    {
        return "";
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
        String key;
        int    dollar = className.indexOf( '$' );
        if( dollar >= 0 ) {
            className = className.substring( 0, dollar );
        }
        return ResourceHelper.getInstance( className, getClass().getClassLoader() );
    }
    
    /**
     * Allow subclasses to override which key to use in the Resource file for the message.
     *
     * @return the key
     */
    protected String findMessageParameter()
    {
        return MESSAGE_PARAMETER;
    }
    
    /**
     * This method can be invoked by subclasses to obtain a suitable message key
     * for the same resource file for all inner classes.
     *
     * @return the key
     */
    protected String findMessageParameterViaEnclosingClass()
    {
        String className = getClass().getName();
        String key;
        int    dollar = className.indexOf( '$' );
        if( dollar >= 0 ) {
            key = className.substring( dollar+1 ) + "-" + MESSAGE_PARAMETER;
        } else {
            key = MESSAGE_PARAMETER;
        }
        return key;
    }
}