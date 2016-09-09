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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.web.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.Cookie;
import org.infogrid.util.Base64;
import org.infogrid.util.CreateWhenNeeded;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.UniqueStringGenerator;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.web.templates.StructuredResponse;

/**
 * Categorizes incoming requests as safe or unsafe, depending on whether submitted
 * forms contain a valid form token or not. The form token is valid if it is
 * consistent with the value of a corresponding cookie. The two values are
 * entangled through a hash.
 *
 * When forms are created, they can ask for a form field, which is the hash of
 * the cookie and the secret. Upon post, we check for the existence of the POST field, and
 * re-calculate the hash.
 */
public class DefaultCsrfMitigator
    implements
        CsrfMitigator
{
    private static final Log log = Log.getLogInstance( DefaultCsrfMitigator.class ); // our own, private logger

    /**
     * Constructor.
     */
    public DefaultCsrfMitigator()
    {
        // nothing right now
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSafeRequest(
            SaneRequest        request,
            StructuredResponse response )
    {
        boolean isSafe = true;

        String cookieValue = request.getCookieValue( COOKIE_NAME );

        if( cookieValue == null ) {
            cookieValue = createNewCookieValue( request );

            Cookie cook = new Cookie( COOKIE_NAME, cookieValue );
            cook.setPath( request.getContextPath() );
            response.addCookie( cook );
        }

        final String finalCookieValue = cookieValue; // sometimes all you can do is to hate Java
        request.setAttribute( TOKEN_ATTRIBUTE_NAME, new CreateWhenNeeded<String>() {
                @Override
                protected String instantiate()
                {
                    return calculateFormFieldValue( finalCookieValue );
                }
        });

        if( "POST".equalsIgnoreCase( request.getMethod() )) {

            boolean process = shouldProcess( request );

            if( process ) {
                String [] formValues = request.getMultivaluedPostedArgument(HIDDEN_INPUT_FIELD_NAME );

                if( cookieValue == null || formValues == null || formValues.length == 0 ) {
                    isSafe = false;

                } else {
                    String correctFormValue = calculateFormFieldValue( cookieValue );
                    isSafe = true;
                    for( String formValue : formValues ) {
                        // no harm if the same token is submitted more than once, but it better have the same value
                        if( !formValue.equals( correctFormValue )) {
                            isSafe = false;
                            break;
                        }
                    }
                }
            }
        }
        return isSafe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlFormFragment(
            SaneRequest request )
    {
        CreateWhenNeeded<String> onDemand = (CreateWhenNeeded<String>) request.getAttribute( TOKEN_ATTRIBUTE_NAME );

        if( onDemand != null ) {
            StringBuilder ret = new StringBuilder();

            ret.append( "<input name=\"" );
            ret.append(HIDDEN_INPUT_FIELD_NAME );
            ret.append( "\" type=\"hidden\" value=\"" );
            ret.append( onDemand.obtain() );
            ret.append( "\" />" );
            return ret.toString();

        } else {
            return null;
        }
    }

    /**
     * Generate a new cookie value.
     *
     * @param sane the incoming request
     * @return the cookie value
     */
    protected String createNewCookieValue(
            SaneRequest sane )
    {
        String cookieValue = theGenerator.createUniqueToken();
        return cookieValue;
    }

    /**
     * Calculate the form field value from the cookie value and the secret.
     *
     * @param cookieValue the value of the cookie
     * @return the value of the field in the form
     */
    protected String calculateFormFieldValue(
            String cookieValue )
    {
        try {
            MessageDigest md = MessageDigest.getInstance( DIGEST_ALGORITHM );
            md.update( cookieValue.getBytes( "UTF-8" ));

            byte hash [] = md.digest();

            String ret = Base64.base64encode( hash );
            ret = ret.replaceAll( "\\s", "" );
            return ret;

        } catch ( NoSuchAlgorithmException ex ) {
            log.error( ex );
        } catch ( UnsupportedEncodingException ex ) {
            log.error( ex );
        }
        return null;
    }

    /**
     * Overridable method to determine whether this particular request should be
     * processed. By default, it's all of them.
     * 
     * @param request the incoming request
     * @return true if the request should be processed
     */
    protected boolean shouldProcess(
            SaneRequest request )
    {
        return true;
    }

    /**
     * Our ResourceHelper, so field and cookie names are configurable.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance(DefaultCsrfMitigator.class );

    /**
     * Name of the hidden field in the form.
     */
    public static final String HIDDEN_INPUT_FIELD_NAME = theResourceHelper.getResourceStringOrDefault("InputFieldName",
            DefaultCsrfMitigator.class.getName().replace( '.', '-' ) + "-field" );

    /**
     * Name of the cookie.
     */
    public static final String COOKIE_NAME = theResourceHelper.getResourceStringOrDefault("CookieName",
            DefaultCsrfMitigator.class.getName().toLowerCase().replace( '.', '-' ) + "-cookie" );

    /**
     * Name of the cookie value as stored in the request attribute.
     */
    public static final String TOKEN_ATTRIBUTE_NAME
            = DefaultCsrfMitigator.class.getName().replace( '.', '-' ) + "-value";

    /**
     * The length of the token.
     */
    protected static final int TOKEN_LENGTH = theResourceHelper.getResourceIntegerOrDefault(
            "TokenLength",
            32 );

    /**
     * The underlying random generator.
     */
    protected static final UniqueStringGenerator theGenerator = UniqueStringGenerator.create( TOKEN_LENGTH );

    /**
     * The Digest algorithm to use.
     */
    public static final String DIGEST_ALGORITHM = "SHA-512";

    /**
     * The secret for this instance of SafeUnsafePostFilter. This can be overridden in the Resource in order to
     * allow easier test instrumentation.
     */
    protected String theMySecret = theResourceHelper.getResourceStringOrDefault( "MySecret", theGenerator.createUniqueToken() );
}
