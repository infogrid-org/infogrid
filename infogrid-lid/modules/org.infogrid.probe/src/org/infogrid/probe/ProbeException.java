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

package org.infogrid.probe;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.util.AbstractLocalizedException;
import org.infogrid.util.ResourceHelper;

import java.io.IOException;

/**
  * This Exception indicates that something went wrong when we were trying
  * to run a Probe. Through delegation, we can find out what exactly went wrong.
  */
public abstract class ProbeException
        extends
            AbstractLocalizedException
{
    /**
     * Constructor, for subclasses only.
     * 
     * @param id the NetMeshBaseIdentifier that we were trying to access
     * @param msg a message
     * @param org the original Throwable that caused this
     */
    protected ProbeException(
            NetMeshBaseIdentifier id,
            String                msg,
            Throwable             org )
    {
        super( msg, org );

        theNetworkIdentifier = id;
    }

    /**
     * Obtain the NetMeshBaseIdentifier that we were trying to access when the Exception occurred.
     * 
     * @return the NNetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getNetworkIdentifier()
    {
        return theNetworkIdentifier;
    }

    /**
     * Same ResourceHelper for all inner classes.
     *
     * @return the ResourceHelper to use
     */
    @Override
    protected ResourceHelper findResourceHelperForLocalizedMessage()
    {
        return ResourceHelper.getInstance( ProbeException.class );
    }
    
    /**
     * Allow subclasses to override which key to use in the Resource file for the message.
     *
     * @return the key
     */
    @Override
    protected String findMessageParameter()
    {
        String ret = getClass().getName();
        ret = ret.substring( ret.lastIndexOf( '$' )+1 );
        ret = MESSAGE_PARAMETER + '-' + ret;
        return ret;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theNetworkIdentifier.getCanonicalForm(), getMessage() };
    }

    /**
     * Obtain a printable representation of this object, for debugging.
     *
     * @return a printable representation of this object
     */
    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer( 128 );
        Object [] pars = getLocalizationParameters();
        for( int i = 0 ; i<pars.length ; ++i ) {
            if( i>0 ) {
                buf.append( ", " );
            }
            buf.append( pars[i] );
        }

        String almostRet = super.toString() + ", pars: " + buf.toString();

        if( getCause() != null ) {
            return almostRet + " - caused by: " + getCause().toString();
        } else {
            return almostRet;
        }
    }

    /**
     * The NetMeshBaseIdentifier that we were trying to access when the Exception occurred.
     */
    protected NetMeshBaseIdentifier theNetworkIdentifier;

    /**
     * This is the abstract superclass for all ProbeExceptions that indicate
     * we were not able to find a matching Probe.
     */
    public static class DontHaveProbe
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable that caused this
         */
        public DontHaveProbe(
                NetMeshBaseIdentifier id )
        {
            super( id, null, null );
        }
        
        /**
         * Constructor.
         *
         * @param u the URL that we were trying to access. The URL is passed in string form as
         *          java.net.URL does not support non-standard protocols very well.
         * @param org the original Throwable that caused this
         */
        public DontHaveProbe(
                NetMeshBaseIdentifier id,
                Throwable         org )
        {
            super( id, null, org );
        }
    }

    /**
     * This is a special type of ProbeException: we were not able to find
     * a probe for a specific MIME type that wasn't XML.
     */
    public static class DontHaveNonXmlStreamProbe
            extends
                DontHaveProbe
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param mimeType the MIME type that we don't support
         * @param org the original Throwable that caused this
         */
        public DontHaveNonXmlStreamProbe(
                NetMeshBaseIdentifier id,
                String            mimeType,
                Throwable         org )
        {
            super( id, org );
            theMimeType = mimeType;
        }

        /**
         * Obtain resource parameters for the internationalization.
         *
         * @return the resource parameters
         */
        @Override
        public Object [] getLocalizationParameters()
        {
            return new Object[] { theNetworkIdentifier.getCanonicalForm(), getMimeType() };
        }

        /**
         * Obtain the MIME type that we didn't support.
         *
         * @return the MIME type that we didn't support
         */
        public String getMimeType()
        {
            return theMimeType;
        }

        /**
         * The MIME type that we didn't support.
         */
        protected String theMimeType;
    }

    /**
     * This is a special type of ProbeException: we were not able to find
     * a probe for a specific XML format.
     */
    public static class DontHaveXmlStreamProbe
            extends
                DontHaveProbe
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param dtd the document type that we don't support. If this is not given, tag is given.
         * @param tag the top-level tag that we don't support. If this is not given, dtd is given.
         * @param org the original Throwable that caused this
         */
        public DontHaveXmlStreamProbe(
                NetMeshBaseIdentifier id,
                String            dtd,
                String            tag,
                Throwable         org )
        {
            super( id, org );

            theDtd = dtd;
            theTag = tag;
        }

        /**
         * Obtain resource parameters for the internationalization.
         *
         * @return the resource parameters
         */
        @Override
        public Object [] getLocalizationParameters()
        {
            return new Object[] { theNetworkIdentifier.getCanonicalForm(), getDtd(), getTag() };
        }

        /**
         * Obtain the DTD that we didn't support.
         *
         * @return the DTD that we didn't support
         */
        public String getDtd()
        {
            return theDtd;
        }

        /**
         * Obtain the top-level tag that we didn't support.
         *
         * @return the top-level tag that we didn't support
         */
        public String getTag()
        {
            return theTag;
        }

        /**
         * The DTD that we didn't support.
         */
        protected String theDtd;

        /**
         * The top-level tag that we didn't support.
         */
        protected String theTag;
    }

    /**
     * This is a special type of ProbeException: we were not able to find
     * an API Probe.
     */
    public static class DontHaveApiProbe
            extends
                DontHaveProbe
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable that caused this
         */
        public DontHaveApiProbe(
                NetMeshBaseIdentifier id,
                Throwable         org )
        {
            super( id, org );
        }

        /**
         * Obtain resource parameters for the internationalization.
         *
         * @return the resource parameters
         */
        @Override
        public Object [] getLocalizationParameters()
        {
            return new Object[] { theNetworkIdentifier.getCanonicalForm() };
        }
    }
    
    /**
     * This is a special type of ProbeException: the data source was empty
     * when we accessed it.
     */
    public static class EmptyDataSource
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         */
        public EmptyDataSource(
                NetMeshBaseIdentifier id )
        {
            super( id, null, null );
        }
    }

    /**
     * This is a special type of ProbeException: we could not determine
     * the type of the data source.
     */
    public static class CannotDetermineContentType
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable causing this one
         */
        public CannotDetermineContentType(
                NetMeshBaseIdentifier id,
                Throwable         org )
        {
            super( id, null, org );
        }

        /**
         * Constructor.
         *
         * @param u the URL of the data source whose type we could not determine
         */
        public CannotDetermineContentType(
                NetMeshBaseIdentifier id )
        {
            super( id, null, null );
        }
    }

    /**
     * This is a special type of ProbeException: we were trying to access
     * a file-URL that is not on our host.
     */
    public static class FileNotLocal
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         */
        public FileNotLocal(
                NetMeshBaseIdentifier id )
        {
            super( id, null, null );
        }
    }

    /**
     * This is a special type of ProbeException: it indicates a programming error in a Probe.
     */
    public static class ErrorInProbe
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable that caused this
         * @param buggyProbe the Probe class that was buggy
         */
        public ErrorInProbe(
                NetMeshBaseIdentifier id,
                Throwable         org,
                Class             buggyProbe )
        {
            super( id, buggyProbe != null ? buggyProbe.getName() : null, org );

            theBuggyProbe = buggyProbe;
        }

        /**
         * Constructor.
         *
         * @param u the URL that we were trying to access. The URL is passed in string form as java.net.URL does not support non-standard protocols very well.
         * @param buggyProbe the Probe class that was buggy
         */
        public ErrorInProbe(
                NetMeshBaseIdentifier id,
                Class             buggyProbe )
        {
            super( id, buggyProbe != null ? buggyProbe.getName() : null, null );

            theBuggyProbe = buggyProbe;
        }

        /**
         * Obtain resource parameters for the internationalization.
         *
         * @return the resource parameters
         */
        @Override
        public Object [] getLocalizationParameters()
        {
            if( theBuggyProbe != null ) {
                return new Object[] { theNetworkIdentifier.getCanonicalForm(), theBuggyProbe.getName() };
            } else {
                return new Object[] { theNetworkIdentifier.getCanonicalForm() };
            }
        }

        /**
         * The Probe class that is buggy.
         */
        protected Class theBuggyProbe;
    }

    /**
     * This is a special type of ProbeException: it indicates a syntax error in the data source.
     */
    public static class SyntaxError
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param message error message
         * @param org the original Throwable that caused this
         */
        public SyntaxError(
                NetMeshBaseIdentifier id,
                String                message,
                Throwable             org )
        {
            super( id, message, org );
        }

        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param message error message
         */
        public SyntaxError(
                NetMeshBaseIdentifier id,
                String                message )
        {
            super( id, message, null );
        }

        /**
         * Constructor.
         *
         * @param u the URL that we were trying to access. The URL is passed in string form as java.net.URL does not support non-standard protocols very well.
         * @param org the original Throwable that caused this
         */
        public SyntaxError(
                NetMeshBaseIdentifier id,
                Throwable             org )
        {
            super( id, null, org );
        }
    }

    /**
     * This is a special type of ProbeException: it indicates an incomplete data source.
     */
    public static class IncompleteData
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable that caused this
         */
        public IncompleteData(
                NetMeshBaseIdentifier id,
                Throwable         org )
        {
            super( id, null, org );
        }
    }

    /**
     * This is a special type of ProbeException: it indicates an IOException.
     */
    public static class IO
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original IOException that caused this
         */
        public IO(
                NetMeshBaseIdentifier id,
                IOException       org )
        {
            super( id, null, org );
        }
    }

    /**
     * This is a special type of ProbeException that only applies to HTTP connections:
     * it indicates that a code was returned by HTTP other than okay.
     */
    public static class HttpErrorResponse
            extends
                ProbeException
    {
        /**
         * Constructor.
         *
         * @param u the URL that we were trying to access. The URL is passed in string form as java.net.URL does not support non-standard protocols very well.
         * @param statusCode the HTTP code that we got back
         */
        public HttpErrorResponse(
                NetMeshBaseIdentifier id,
                String            statusCode )
        {
            super( id, null, null );

            theStatusCode = statusCode;
        }

        /**
         * Obtain the HTTP status that we got back.
         *
         * @return the HTTP status that we got back
         */
        public String getStatusCode()
        {
            return theStatusCode;
        }

        /**
         * Obtain resource parameters for the internationalization.
         *
         * @return the resource parameters
         */
        @Override
        public Object [] getLocalizationParameters()
        {
            return new Object[] { theNetworkIdentifier.getCanonicalForm(), getStatusCode() };
        }

        /**
         * The HTTP status code that we got back.
         */
        protected String theStatusCode;
    }

    /**
     * This is a special type of ProbeException: it indicates that the Probe does not
     * want to be called again. This may indicate a non-recoverable error, for example,
     * such as a permanent unavailability of the underlying data source, or that the
     * provided user name and password were incorrect and further attempts at logging
     * on to the data source would be futile (and would probably produce a nasty
     * response from the maintainers of the underlying data source).
     */
    public static class DoNotRunAgain
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param explanation the user-visible, localized explanation of what went wrong
         * @param org the original Throwable that cause this Exception
         */
        public DoNotRunAgain(
                NetMeshBaseIdentifier id,
                String            explanation,
                Throwable         org )
        {
            super( id, null, org );

            theExplanation = explanation;
        }

        /**
         * Constructor.
         *
         * @param u the URL that we were trying to access. The URL is passed in string form as java.net.URL does not support non-standard protocols very well.
         * @param explanation the user-visible, localized explanation of what went wrong
         */
        public DoNotRunAgain(
                NetMeshBaseIdentifier id,
                String            explanation )
        {
            super( id, null, null );

            theExplanation = explanation;
        }

        /**
         * Obtain the explanation for why we don't want to run this Probe again.
         *
         * @return the localized explanation for not wanting to run again
         */
        public String getExplanation()
        {
            return theExplanation;
        }

        /**
         * The localized explanation for not wanting to run again.
         */
        protected String theExplanation;
    }

    /**
     * This is a special type of ProbeException: it indicates an unknown other error.
     */
    public static class Other
            extends
                ProbeException
    {
        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         * @param org the original Throwable that cause this Exception
         */
        public Other(
                NetMeshBaseIdentifier id,
                Throwable         org )
        {
            super( id, null, org );
        }

        /**
         * Constructor.
         * 
         * @param id the NNetMeshBaseIdentifierthat we were trying to access
         */
        public Other(
                NetMeshBaseIdentifier id )
        {
            super( id, null, null );
        }
    }
}
