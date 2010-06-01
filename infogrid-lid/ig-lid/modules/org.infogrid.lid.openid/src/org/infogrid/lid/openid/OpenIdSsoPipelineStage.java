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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.openid;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.crypto.diffiehellman.DiffieHellmanEndpoint;
import org.infogrid.lid.LidClientAuthenticationStatus;
import org.infogrid.lid.LidPipelineInstructions;
import org.infogrid.lid.LidSsoPipelineStage;
import org.infogrid.lid.SimpleLidSsoPipelineStageInstructions;
import org.infogrid.lid.openid.auth.OpenId1CredentialType;
import org.infogrid.lid.openid.auth.OpenId2CredentialType;
import org.infogrid.lid.openid.auth.OpenIdCredentialType;
import org.infogrid.lid.yadis.AbstractDeclaresYadisFragment;
import org.infogrid.util.Base64;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * Knows how to process incoming OpenID SSO requests. This does NOT handle dumb mode.
 */
public class OpenIdSsoPipelineStage
        extends
            AbstractDeclaresYadisFragment
        implements
            LidSsoPipelineStage,
            OpenIdConstants
{
    private static final Log log = Log.getLogInstance( OpenIdSsoPipelineStage.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param smartAssocMgr manages smart OpenID associations
     * @param dumbAssocMgr manages dumb OpenID associations
     * @param c the context in which this <code>ObjectInContext</code> runs.
     * @return the created OpenIdSsoPipelineStage
     */
    public static OpenIdSsoPipelineStage create(
            OpenIdIdpSideAssociationManager smartAssocMgr,
            OpenIdIdpSideAssociationManager dumbAssocMgr,
            Context                         c )
    {
        OpenIdSsoPipelineStage ret = new OpenIdSsoPipelineStage( smartAssocMgr, dumbAssocMgr, c );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param smartAssocMgr manages smart OpenID associations
     * @param dumbAssocMgr manages dumb OpenID associations
     * @param c the context in which this <code>ObjectInContext</code> runs.
     */
    protected OpenIdSsoPipelineStage(
            OpenIdIdpSideAssociationManager smartAssocMgr,
            OpenIdIdpSideAssociationManager dumbAssocMgr,
            Context                         c )
    {
        super( YADIS_FRAGMENT, c );

        theSmartAssociationManager = smartAssocMgr;
        theDumbAssociationManager  = dumbAssocMgr;
    }

    /**
     * Process this stage.
     *
     * @param lidRequest the incoming request
     * @param requestedResource the requested resource, if any
     * @param instructionsSoFar the instructions assembled by the pipeline so far
     * @return the instructions for constructing a response to the client, if any
     */
    public SimpleLidSsoPipelineStageInstructions processStage(
            SaneRequest                       lidRequest,
            HasIdentifier                     requestedResource,
            LidPipelineInstructions instructionsSoFar )
    {
        // to be safe, look for multi-valued arguments first
        if( lidRequest.matchUrlArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME, "error" ) || lidRequest.matchPostedArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME, "error" )) {
            log.error(  "OpenID error", lidRequest );
            return null; // FIXME?
        }
        if( lidRequest.matchUrlArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME, "cancel" ) || lidRequest.matchPostedArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME, "cancel" ) ) {
            return null;
        }

        String mode = lidRequest.getUrlArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME );
        if( mode == null ) {
            mode = lidRequest.getPostedArgument( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME );
        }
        if( mode == null ) {
            return null;
        }

        LidClientAuthenticationStatus         clientAuthStatus = instructionsSoFar.getClientAuthenticationStatus();
        SimpleLidSsoPipelineStageInstructions ret              = null;

        if( "associate".equals( mode )) {
            ret = processAssociate( lidRequest );

        } else if( "checkid_immediate".equals( mode )) {
            ret = processCheckId( lidRequest, clientAuthStatus, requestedResource, true );

        } else if( "checkid_setup".equals( mode )) {
            ret = processCheckId( lidRequest, clientAuthStatus, requestedResource, false );

        } else if( "check_authentication".equals( mode )) {
            ret = processCheckAuthentication( lidRequest, clientAuthStatus, requestedResource );

        } else {
            log.warn( "Unsupported value given for openid.mode", mode );
        }
        return ret;
    }

    /**
     * Process incoming associate requests.
     *
     * @param lidRequest the incoming request
     * @return the created instructions, if any
     */
    public SimpleLidSsoPipelineStageInstructions processAssociate(
            SaneRequest lidRequest )
    {
        String assocType                 = lidRequest.getPostedArgument( "openid.assoc_type" );
        String sessionType               = lidRequest.getPostedArgument( "openid.session_type" );
        String dh_modulus_string         = lidRequest.getPostedArgument( "openid.dh_modulus" );
        String dh_gen_string             = lidRequest.getPostedArgument( "openid.dh_gen" );
        String dh_consumer_public_string = lidRequest.getPostedArgument( "openid.dh_consumer_public" );

        BigInteger dh_modulus         = null;
        BigInteger dh_gen             = null;
        BigInteger dh_consumer_public = null;

        if( dh_modulus_string != null ) {
            dh_modulus = new BigInteger( Base64.base64decode( dh_modulus_string ));
        }
        if( dh_gen_string != null ) {
            dh_gen = new BigInteger( Base64.base64decode( dh_gen_string ));
        }
        if(    DH_SHA1.equals( sessionType )
            && ( dh_consumer_public_string == null || dh_consumer_public_string.length() == 0 ))
        {
             throw new OpenIdAssociationException.InvalidPublicKey();
        }
        if( dh_consumer_public_string != null && dh_consumer_public_string.length() > 0 ) {
            dh_consumer_public = new BigInteger( Base64.base64decode( dh_consumer_public_string ));
        }

        // insert defaults
        assocType   = useDefaultIfNeeded( assocType,   HMAC_SHA1 );
        sessionType = useDefaultIfNeeded( sessionType, null ); // null means cleartext
        dh_modulus  = useDefaultIfNeeded( dh_modulus,  DEFAULT_P );
        dh_gen      = useDefaultIfNeeded( dh_gen,      DEFAULT_G );

        // sanity check

        if( !HMAC_SHA1.equals( assocType )) {
            throw new OpenIdAssociationException.UnknownAssociationType( assocType );
        }
        if( sessionType != null && !DH_SHA1.equals( sessionType )) {
            throw new OpenIdAssociationException.UnknownSessionType( sessionType );
        }
        if( DH_SHA1.equals( sessionType ) && dh_consumer_public == null ) {
            throw new OpenIdAssociationException.InvalidPublicKey();
        }

        OpenIdIdpSideAssociation assoc = theSmartAssociationManager.create( sessionType );

        byte [] mac_key     = null;
        byte [] enc_mac_key = null;

        String dh_server_public_string = null;

        if( sessionType == null ) {
            // cleartext
            mac_key = assoc.getSharedSecret();

        } else {
            // Diffie-Hellman
            DiffieHellmanEndpoint dh             = DiffieHellmanEndpoint.create( dh_modulus, dh_gen );
            BigInteger            sharedDhSecret = dh.computeSharedSecret( dh_consumer_public );

            byte [] sharedSecret = assoc.getSharedSecret();
            enc_mac_key = CryptUtils.do160BitXor(
                    CryptUtils.calculateSha1( sharedDhSecret ),
                    sharedSecret );

            dh_server_public_string = Base64.base64encodeNoCr( dh.getPublicKey().toByteArray());
        }

        StringBuilder buf = new StringBuilder();
        long          now = System.currentTimeMillis();

        if( assocType != null ) {
            buf.append( "assoc_type:" );
            buf.append( assocType );
            buf.append( "\n" );
        }
        if( assoc.getAssociationHandle() != null ) {
            buf.append( "assoc_handle:" );
            buf.append( assoc.getAssociationHandle() );
            buf.append( "\n" );
        }
        if( assoc.getTimeExpires() > 0 ) {
            long delta = assoc.getTimeExpires() - now;
            buf.append( "expires_in:" );
            buf.append( delta / 1000L ); // need seconds
            buf.append( "\n" );
        }
        if( sessionType != null ) {
            buf.append( "session_type:" );
            buf.append( sessionType );
            buf.append( "\n" );
        }
        if( dh_server_public_string != null ) {
            buf.append( "dh_server_public:" );
            buf.append( dh_server_public_string );
            buf.append( "\n" );
        }
        if( enc_mac_key != null ) {
            buf.append( "enc_mac_key:" );
            buf.append( Base64.base64encodeNoCr( enc_mac_key ));
            buf.append( "\n" );
        }
        if( mac_key != null ) {
            buf.append( "mac_key:" );
            buf.append( Base64.base64encodeNoCr( mac_key ));
            buf.append( "\n" );
        }

        return SimpleLidSsoPipelineStageInstructions.createContentOnly( buf.toString(), "text/plain" );
    }

    /**
     * Process incoming checkid_immediate and checkid_setup requests.
     * 
     * @param lidRequest the incoming request
     * @param clientAuthStatus knows the authentication status of the client
     * @param requestedResource the requested resource, if any
     * @param checkIdImmediate if true, performed checkid_immediate; if false, perform checkid_setup
     * @return the created instructions, if any
     */
    protected SimpleLidSsoPipelineStageInstructions processCheckId(
            SaneRequest                   lidRequest,
            LidClientAuthenticationStatus clientAuthStatus,
            HasIdentifier                 requestedResource,
            boolean                       checkIdImmediate )
    {
        // This has to react to GET or POST, because input arguments to a login dialog may be submitted at the
        // same time

        if( theSmartAssociationManager == null ) {
            return null;
        }
        
        String identifier = lidRequest.getUrlArgument( OpenId2CredentialType.OPENID2_IDENTIFIER_PARAMETER_NAME ); // "openid.identity" );
        if( identifier == null ) {
            identifier = lidRequest.getUrlArgument( OpenId1CredentialType.OPENID1_IDENTIFIER_PARAMETER_NAME );
        }
        String assoc_handle = lidRequest.getUrlArgument( OpenIdCredentialType.OPENID_ASSOC_HANDLE_PARAMETER_NAME ); //  "openid.assoc_handle" );
        String return_to    = lidRequest.getUrlArgument( OPENID_RETURN_TO_PARAMETER_NAME );
        String trust_root   = lidRequest.getUrlArgument( OPENID_TRUST_ROOT_PARAMETER_NAME );

        if( identifier == null || identifier.length() == 0 ) {
            log.warn( "openid.identifier must not be empty", lidRequest );
            return null;
        }
        if( assoc_handle == null || assoc_handle.length() == 0 ) {
            log.warn( "openid.assoc_handle must not be empty", lidRequest );
            return null;
        }
        if( return_to == null || return_to.length() == 0 ) {
            log.warn( OPENID_RETURN_TO_PARAMETER_NAME + " must not be empty" );
            // The spec says it's optional, but I can't see how that can work
            return null;
        }
        if( trust_root == null || trust_root.length() == 0 ) {
            trust_root = return_to;
        }
        
        OpenIdIdpSideAssociation assoc = theSmartAssociationManager.get( assoc_handle );
        if( assoc == null ) {
            log.warn( "Cannot find association with handle", assoc_handle, lidRequest );
            return SimpleLidSsoPipelineStageInstructions.create( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
        // checking whether it is valid happens later
        
        boolean authenticationConfirmed = false; // don't do it unless we are sure we want to

        if(    clientAuthStatus.isAuthenticated()
            && clientAuthStatus.getClientIdentifier().toExternalForm().equals( identifier ))
        {
            authenticationConfirmed = true;
        }

        // assemble response
        
        if( authenticationConfirmed ) {
            StringBuilder redirect       = new StringBuilder();
            StringBuilder token_contents = new StringBuilder();
            String        redirectUrl;

            construct( redirect, token_contents, "mode", "id_res" );
            construct( redirect, token_contents, "identity",  identifier );
            construct( redirect, token_contents, "return_to", return_to );

            if( assoc != null ) {
                if( assoc.isCurrentlyValid() ) {
                    construct( redirect, token_contents, "assoc_handle", assoc_handle );
                } else {
                    theSmartAssociationManager.remove( assoc.getAssociationHandle() );
                    // the spec seems to allow not to automatically createContentOnly a new association
                    construct( redirect, token_contents, "invalidate_handle", assoc_handle );
                }
            }
            
            try {
                byte [] sharedSecret          = assoc.getSharedSecret();
                String  token_contents_string = token_contents.toString();

                byte [] signed = CryptUtils.calculateHmacSha1( sharedSecret, token_contents_string.getBytes( "US-ASCII" ) );
                String  signedFields = Base64.base64encodeNoCr( signed );

                redirect.append( "&" ).append( OpenIdCredentialType.OPENID_SIGNED_PARAMETER_NAME ).append( "=" );
                redirect.append( "mode,identity,return_to,assoc_handle" );
                redirect.append( "&" ).append( OpenIdCredentialType.OPENID_SIGNATURE_PARAMETER_NAME ).append( "=" );
                redirect.append( HTTP.encodeToValidUrlArgument( signedFields ));
                // FIXME? We don't do openid.invalidate_handle right now

                redirectUrl = HTTP.appendArgumentPairToUrl( return_to, redirect.toString().substring( 1 ));
                return SimpleLidSsoPipelineStageInstructions.create( redirectUrl, 302 );

            } catch( UnsupportedEncodingException ex ) {
                log.error( ex );
                return null;
            }

        } else if( checkIdImmediate ) {
            StringBuilder redirect       = new StringBuilder();
            StringBuilder token_contents = new StringBuilder();
            String        redirectUrl;

            construct( redirect, token_contents, "mode", "cancel" );
            redirect.append( "&openid.user_setup_url=" );
            redirect.append(
                    HTTP.encodeToValidUrlArgument(
                            lidRequest.getOriginalSaneRequest().getAbsoluteBaseUri()
                            + "?lid=" + HTTP.encodeToValidUrlArgument( identifier )));

            redirectUrl = HTTP.appendArgumentPairToUrl( return_to, redirect.toString().substring( 1 ));
            return SimpleLidSsoPipelineStageInstructions.create( redirectUrl, 302 );

        } else {
            // else: !checkIdImmediate but no valid session -- have user log in
            return SimpleLidSsoPipelineStageInstructions.createNeedAuth();
        }
    }

    /**
     * Process incoming check_authentication requests.
     * 
     * @param lidRequest the incoming request
     * @param clientAuthStatus knows the authentication status of the client
     * @param requestedResource the requested resource, if any
     * @return the created instructions, if any
     */
    protected SimpleLidSsoPipelineStageInstructions processCheckAuthentication(
            SaneRequest                   lidRequest,
            LidClientAuthenticationStatus clientAuthStatus,
            HasIdentifier                 requestedResource )
    {
        if( !"POST".equals( lidRequest.getMethod() )) {
            return null;
        }
        if( theDumbAssociationManager == null ) {
            return null;
        }

        Map<String,String[]> postPars = lidRequest.getPostedArguments();
        if( postPars == null || postPars.isEmpty() ) {
            return null;
        }

        String assoc_handle = lidRequest.getPostedArgument( OpenIdCredentialType.OPENID_ASSOC_HANDLE_PARAMETER_NAME );
        String sig          = lidRequest.getPostedArgument( OpenIdCredentialType.OPENID_SIGNATURE_PARAMETER_NAME );
        String signed       = lidRequest.getPostedArgument( OpenIdCredentialType.OPENID_SIGNED_PARAMETER_NAME );

        boolean valid = false;
        OpenIdIdpSideAssociation assoc = theDumbAssociationManager.get( assoc_handle );
        if( assoc != null ) {
            StringBuilder toSign    = new StringBuilder( 128 );
            String        fields [] = signed.split( "," );
            for( int i=0 ; i<fields.length ; ++i ) {
                String field = fields[i];
                String value;
                if( "mode".equals( field )) { // see http://lists.danga.com/pipermail/yadis/2005-June/000734.html
                    value = OpenIdCredentialType.OPENID_MODE_IDRES_PARAMETER_VALUE;
                } else {
                    value = lidRequest.getPostedArgument( field );
                }
                if( value != null ) {
                    toSign.append( field ).append( ":" ).append( value );
                    toSign.append( "\n" );
                }
            }

            byte [] bytesToSign   = toSign.toString().getBytes();
            byte [] hmacSha1      = CryptUtils.calculateHmacSha1( assoc.getSharedSecret(), bytesToSign );
            String  locallySigned = Base64.base64encode( hmacSha1 );

            if( locallySigned.equals( sig )) {
                valid = true;
            } else {
                log.warn(
                        "Error comparing sig '"
                        + sig
                        + "' with locally computed '"
                        + locallySigned
                        + "', toSign was '"
                        + toSign
                        + "'"
                        + " for request "
                        + lidRequest );
            }
        }

        // convert to the right format
        String validString;
        if( valid ) {
            validString = "true";
        } else {
            validString = "false";
        }

        // construct response
        StringBuilder responseBuffer = new StringBuilder( 64 );
        // obsoleted: responseBuffer.append( "lifetime:" ).append( assoc.getExpiresInSeconds()).append( "\n" );
        responseBuffer.append( OpenIdCredentialType.OPENID_MODE_PARAMETER_NAME ).append( ":" ).append( OpenIdCredentialType.OPENID_MODE_IDRES_PARAMETER_VALUE ).append( "\n" );
        responseBuffer.append( "is_valid:" ).append( validString ).append( "\n" );

        return SimpleLidSsoPipelineStageInstructions.createContentOnly( responseBuffer.toString(), "text/plain" );
    }

    /**
     * Internal String construction helper.
     *
     * @param redirect the redirect content to assemble
     * @param token_contents the token_content to assemble
     * @param appendName the name component of the name-value pair to append
     * @param appendValue the value component of the name-value pair to append
     */
    private static void construct(
            StringBuilder redirect,
            StringBuilder token_contents,
            String        appendName,
            String        appendValue )
    {
        redirect.append( "&openid." );
        redirect.append( appendName );
        redirect.append( "=" );
        redirect.append( HTTP.encodeToValidUrlArgument( appendValue ));

        token_contents.append( appendName );
        token_contents.append( ":" );
        token_contents.append( appendValue );
        token_contents.append( "\n" );
    }

    /**
     * Insert default if no value is given.
     *
     * @param value the given value, if any
     * @param defaultValue the default value
     * @return the return value
     * @param <T> allows us to use the same method for different types
     */
    protected static <T> T useDefaultIfNeeded(
            T value,
            T defaultValue )
    {
        if( value == null ) {
            return defaultValue;

        } else if( value instanceof String && ((String)value).length() == 0 ) {
            return defaultValue;

        } else {
            return value;
        }
    }

    /**
     * The association manager to use for smart associations.
     */
    protected OpenIdIdpSideAssociationManager theSmartAssociationManager;
    
    /**
     * The association manager to use for dumb associations.
     */
    protected OpenIdIdpSideAssociationManager theDumbAssociationManager;
    
    /**
     * Yadis service fragment.
     */
    public static final String YADIS_FRAGMENT
            = "<xrd:Type>http://openid.net/signon/1.0</xrd:Type>\n"
            + "<xrd:URI>{0}</xrd:URI>\n"
            + "<openid:Delegate xmlns:openid=\"http://openid.net/xmlns/1.0\">{0}</openid:Delegate>\n";

    /**
     * Name of the URL parameter that contains the OpenID return_to URL.
     */
    public static final String OPENID_RETURN_TO_PARAMETER_NAME = "openid.return_to";

    /**
     * Name of the URL parameter that contains the OpenID trust root.
     */
    public static final String OPENID_TRUST_ROOT_PARAMETER_NAME = "openid.trust_root";
}
