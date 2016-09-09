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

package org.infogrid.web.taglib.util;

import java.io.IOException;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.taglib.AbstractInfoGridBodyTag;
import org.infogrid.web.taglib.IgnoreException;
import org.infogrid.web.taglib.candy.OverlayTag;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.security.CsrfMitigator;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseSection;

/**
 * <p>Allows the inclusion of JSP overlays as subroutines with parameters.</p>
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class CallJspoTag
    extends
        AbstractInfoGridBodyTag

{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public CallJspoTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theName          = null;
        theLinkTitle     = null;
        theAction        = null;
        theSubmitLabel   = null;
        theActivated     = false;
        theCssClass      = null;
        theDisabled      = false;
        theOldCallRecord = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the name property.
     *
     * @return value of the name property
     * @see #setName
     */
    public String getName()
    {
        return theName;
    }

    /**
     * Set value of the name property.
     *
     * @param newValue new value of the name property
     * @see #getName
     */
    public void setName(
            String newValue )
    {
        theName = newValue;
    }

    /**
     * Obtain value of the title property.
     *
     * @return value of the title property
     * @see #setLinkTitle
     */
    public String getLinkTitle()
    {
        return theLinkTitle;
    }

    /**
     * Set value of the title property.
     *
     * @param newValue new value of the title property
     * @see #getLinkTitle
     */
    public void setLinkTitle(
            String newValue )
    {
        theLinkTitle = newValue;
    }

    /**
     * Obtain value of the action property.
     *
     * @return value of the action property
     * @see #setAction
     */
    public String getAction()
    {
        return theAction;
    }

    /**
     * Set value of the action property.
     *
     * @param newValue new value of the action property
     * @see #getAction
     */
    public void setAction(
            String newValue )
    {
        theAction = newValue;
    }

    /**
     * Obtain value of the submitLabel property.
     *
     * @return value of the submitLabel property
     * @see #setSubmitLabel
     */
    public String getSubmitLabel()
    {
        return theSubmitLabel;
    }

    /**
     * Set value of the submitLabel property.
     *
     * @param newValue new value of the submitLabel property
     * @see #getSubmitLabel
     */
    public void setSubmitLabel(
            String newValue )
    {
        theSubmitLabel = newValue;
    }

    /**
     * Obtain value of the activated property.
     *
     * @return value of the activated property
     * @see #setActivated
     */
    public boolean getActivated()
    {
        return theActivated;
    }

    /**
     * Set value of the activated property.
     *
     * @param newValue new value of the activated property
     * @see #getActivated
     */
    public void setActivated(
            boolean newValue )
    {
        theActivated = newValue;
    }

    /**
     * Obtain value of the cssClass property.
     *
     * @return value of the cssClass property
     * @see #setCssClass
     */
    public String getCssClass()
    {
        return theCssClass;
    }

    /**
     * Set value of the cssClass property.
     *
     * @param newValue new value of the cssClass property
     * @see #getCssClass
     */
    public void setCssClass(
            String newValue )
    {
        theCssClass = newValue;
    }

    /**
     * Obtain value of the disabled property.
     *
     * @return value of the disabled property
     * @see #setDisabled
     */
    public boolean getDisabled()
    {
        return theDisabled;
    }

    /**
     * Set value of the disabled property.
     *
     * @param newValue new value of the disabled property
     * @see #getDisabled
     */
    public void setDisabled(
            boolean newValue )
    {
        theDisabled = newValue;
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     * @throws IOException thrown if an I/O Exception occurred
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        ServletRequest request    = pageContext.getRequest();
        theOldCallRecord          = (CallJspXRecord) request.getAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME );
        theCurrentCallRecord      = new CallJspoRecord( theName, theDisabled );
        request.setAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME, theCurrentCallRecord );

        return EVAL_BODY_BUFFERED; // contains parameter declarations
    }

    /**
     * Our implementation of doAfterBody(), to be provided by subclasses.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     * @throws IOException thrown if an I/O Exception occurred
     */
    @Override
    protected int realDoAfterBody()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        InfoGridWebApp app     = getInfoGridWebApp();

        SaneRequest request = (SaneRequest) pageContext.getRequest();
        BodyContent body    = getBodyContent();
        JspWriter   out     = body.getEnclosingWriter();

        try {
            StringBuilder domId = new StringBuilder();
            domId.append(theName );

            // This is not ordered, but that should not be a problem?
            for( Entry<String,Object> current : theCurrentCallRecord.getParameters() ) {
                Object value = current.getValue();
                if( value == null ) {
                    continue;
                }

                String parId;
                if( value instanceof HasIdentifier ) {
                    parId = ((HasIdentifier)value).getIdentifier().toExternalForm();
                } else {
                    parId = value.toString();
                }

                domId.append( "-" );
                domId.append( parId );
            }

            String bodyContentString = body.getString();
            if( bodyContentString.trim().length() > 0 ) {
                // skip if there's nothing between the CallJspoTags that generates anything other than white space

                if( !theDisabled ) {
                    out.print( "<a href=\"javascript:overlay_show( '" + domId + "', {} )\"" );
                    if( theLinkTitle != null ) {
                        out.print( " title=\"" + theLinkTitle + "\"" );
                    }
                    out.println( ">" );
                } else {
                    out.print( "<span class=\"jspo-disabled\">" );
                }

                out.print( bodyContentString );

                if( !theDisabled ) {
                    out.println( "</a>" );
                } else {
                    out.println( "</span>" );
                }
            }

            if( !theDisabled ) {
                out.print( "<div class=\"" );
                out.print( OverlayTag.class.getName().replace( '.', '-' ) );
                if( theCssClass != null && theCssClass.length() > 0 ) {
                    out.print( " " );
                    out.print( theCssClass );
                }
                out.print( "\" id=\"" + domId + "\"" );
                out.println( ">" );

                if( theAction != null ) {
                    out.print( "<form action=\"" + theAction + "\" method=\"post\" enctype=\"multipart/form-data\" accept-charset=\"" );
                    out.print( SaneServletRequest.FORM_CHARSET );
                    out.print( "\">" );
                    
                    CsrfMitigator mitigator = app.getCsrfMitigator();
                    if( mitigator != null ) {
                        String toInsert = mitigator.getHtmlFormFragment( request );
                        if( toInsert != null ) {
                            print( toInsert );
                        }
                    }
               }
                out.println( "<div class=\"dialog-content\">" );

                StructuredResponse        response   = (StructuredResponse) pageContext.getResponse();
                StructuredResponseSection oldDefault = response.swapInNewDefaultSection();
                try {

                    app.getResourceManager().processJspo( theName, pageContext );

                    response.getDefaultSection().copyContentTo( body.getEnclosingWriter() );

                    return EVAL_PAGE;

                } catch( ServletException ex ) {
                    throw new JspException( ex ); // why in the world are these two differnt types of exceptions?

                } finally {
                    response.setDefaultSection( oldDefault );

                    out.println( "</div>" );
                    if( theAction != null ) {
                        out.println( "<div class=\"dialog-buttons\">" );
                        out.println( "<table class=\"dialog-buttons\">" );
                        out.println( "<tr>" );
                        out.println( "<td><a class=\"cancel\" href=\"javascript:overlay_hide( '" + domId + "' )\">Cancel</a></td>" );
                        out.print( "<td><input type=\"submit\" class=\"submit\" value=\"" );
                        if( theSubmitLabel != null ) {
                            out.print( theSubmitLabel );
                        } else if( theLinkTitle != null ) { // seems like a reasonable default
                            out.print( theLinkTitle );
                        } else {
                            out.print( DEFAULT_SUBMIT_LABEL );
                        }
                        out.println( "\" /></td>" );
                        out.println( "</tr>" );
                        out.println( "</table>" );
                        out.println( "</div>" );
                    }
                    out.println( "</form>" );
                    out.println( "</div>" );

                    if( theActivated ) {
                        out.println( "<script type=\"text/javascript\">\n" );
                        out.println( "    overlay_show( '" + domId + "', {} );\n" );
                        out.println( "</script>\n" );
                    }
                }
            } else {
                return EVAL_PAGE;
            }
        } finally {
            request.setAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME, theOldCallRecord );
        }
    }

    /**
     * Name.
     */
    protected String theName;

    /**
     * Title attribute on the generated link, if any.
     */
    protected String theLinkTitle;

    /**
     * Action to take in the form.
     */
    protected String theAction;

    /**
     * Label on the submit button.
     */
    protected String theSubmitLabel;

    /**
     * If the overlay should be activated.
     */
    protected boolean theActivated;

    /**
     * Additional CSS class, if any.
     */
    protected String theCssClass;

    /**
     * If true, the popup will be disabled.
     */
    protected boolean theDisabled;

    /**
     * The CallJspXRecord to restore.
     */
    CallJspXRecord theOldCallRecord;

    /**
     * The CallJspXRecord for the current call.
     */
    CallJspXRecord theCurrentCallRecord;

    /**
     * The default label to be put on the submit button if none is given.
     */
    public static final String DEFAULT_SUBMIT_LABEL = ResourceHelper.getInstance( CallJspoTag.class ).getResourceStringOrDefault( "DefaultSubmitLabel", "Submit" );
}
