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

package org.infogrid.jee.viewlet.wikiobject;

import org.infogrid.context.Context;
import org.infogrid.jee.viewlet.SimpleJeeViewlet;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.BlobValue;
import org.infogrid.model.Wiki.WikiSubjectArea;

import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Viewlet that can display and edit WikiObjects.
 */
public class WikiObjectEditViewlet
        extends
            SimpleJeeViewlet
{
    private static final Log log = Log.getLogInstance( WikiObjectEditViewlet.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created Viewlet
     */
    public static WikiObjectEditViewlet create(
            Context c )
    {
        return new WikiObjectEditViewlet( c );
    }

    /**
     * Constructor. This is protected: use factory method or subclass.
     *
     * @param c the application context
     */
    protected WikiObjectEditViewlet(
            Context c )
    {
        super( c );
    }

    /**
     * <p>Invoked prior to the execution of the Servlet. It is the hook by which
     * the JeeViewlet can perform whatever operations needed prior to the execution of the servlet, e.g.
     * the evaluation of POST commands. Subclasses will often override this.</p>
     * 
     * @param context the ServletContext
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performAfter
     */
    @Override
    public void performBefore(
            ServletContext      context,
            HttpServletRequest  request,
            HttpServletResponse response )
        throws
            ServletException
    {
        SaneRequest sane = (SaneRequest) request.getAttribute( SaneRequest.class.getName() );

        String actionName = sane.getPostArgument( "action" );
        Action theAction  = Action.findAction( actionName );
        
        Mode theMode = theAction.determineMode();

        request.setAttribute( "action", theAction.getName() );
        request.setAttribute( "mode",   theMode.getName() );

        String currentContent = sane.getPostArgument( "current-content" );

        theCurrentContent = theAction.process( getSubject(), theMode, currentContent );
    }
    
    /**
     * Obtain the current content of the WikiObject, specific to the session.
     *
     * @return the current content
     */
    public String getCurrentContent()
    {
        return theCurrentContent;
    }
    
    /**
     * The current content of the WikiObject.
     */
    protected String theCurrentContent;

    /**
     * The modes in which this Viewlet can be.
     */
    static enum Mode
    {
        VIEW( "view" ),
        EDIT( "edit" ),
        PREVIEW( "preview" );
        
        /**
         * Constructur.
         *
         * @param modeName name of the mode in which the Viewlet is
         */
        private Mode(
                String modeName )
        {
            theModeName = modeName;
        }
        
        /**
         * Obtain the name of this Mode.
         *
         * @return the name of this Mode
         */
        public String getName()
        {
            return theModeName;
        }

        /**
         * Name of the mode, as used in the protocol, of the current mode.
         */
        protected String theModeName;
    }
    
    /**
     * The action that was taken by the user.
     */
    static enum Action
    {
        CANCEL( "cancel" )
        {
            /**
             * Process the incoming request.
             *
             * @param subject the WikiObject
             * @param mode the Mode to put the Viewlet in
             * @param currentContent the current content
             * @throws ServletException thrown if an error occurred
             * @return the new content
             */
            protected String process(
                    MeshObject subject,
                    Mode       mode,
                    String     currentContent )
                throws
                    ServletException
            {
                // restore old content
                try {
                    BlobValue oldValue = (BlobValue) subject.getPropertyValue( WikiSubjectArea.WIKIOBJECT_CONTENT );
                    String    ret;
                    if( oldValue != null ) {
                        ret = oldValue.getAsString();
                    } else {
                        ret = "";
                    }
                    return ret;

                } catch( NotPermittedException ex ) {
                    throw new ServletException( ex );
                }
            }

            /**
             * Given this action, determine the mode that the Viewlet is in.
             *
             * @return the Mode
             */
            public Mode determineMode()
            {
                return Mode.VIEW;
            }
        },

        PREVIEW( "preview" )
        {
            /**
             * Process the incoming request.
             *
             * @param subject the WikiObject
             * @param mode the Mode to put the Viewlet in
             * @param currentContent the current content
             * @throws ServletException thrown if an error occurred
             * @return the new content
             */
            protected String process(
                    MeshObject subject,
                    Mode       mode,
                    String     currentContent )
                throws
                    ServletException
            {
                return currentContent;
            }

            /**
             * Given this action, determine the mode that the Viewlet is in.
             *
             * @return the Mode
             */
            public Mode determineMode()
            {
                return Mode.PREVIEW;
            }
        },

        PUBLISH( "publish" )
        {
            /**
             * Process the incoming request.
             *
             * @param subject the WikiObject
             * @param mode the Mode to put the Viewlet in
             * @param currentContent the current content
             * @throws ServletException thrown if an error occurred
             * @return the new content
             */
            protected String process(
                    MeshObject subject,
                    Mode       mode,
                    String     currentContent )
                throws
                    ServletException
            {
                Transaction tx = null;
                try {
                    tx = subject.getMeshBase().createTransactionAsapIfNeeded();
                    
                    BlobValue newValue = BlobValue.create( currentContent );
                    subject.setPropertyValue( WikiSubjectArea.WIKIOBJECT_CONTENT, newValue );

                } catch( TransactionException ex ) {
                    log.error( ex );

                } catch( NotPermittedException ex ) {
                    throw new ServletException( ex );

                } finally {
                    if( tx != null ) {
                        tx.commitTransaction();
                    }
                }
                return currentContent;
            }


            /**
             * Given this action, determine the mode that the Viewlet is in.
             *
             * @return the Mode
             */
            public Mode determineMode()
            {
                return Mode.VIEW;
            }
        },
        EDIT( "edit" )
        {
            /**
             * Process the incoming request.
             *
             * @param subject the WikiObject
             * @param mode the Mode to put the Viewlet in
             * @param currentContent the current content
             * @throws ServletException thrown if an error occurred
             * @return the new content
             */
            protected String process(
                    MeshObject subject,
                    Mode       mode,
                    String     currentContent )
                throws
                    ServletException
            {
                // if there is new content, use new content, otherwise restore old content
                if( currentContent != null ) {
                    return currentContent;
                }

                try {
                    BlobValue oldValue = (BlobValue) subject.getPropertyValue( WikiSubjectArea.WIKIOBJECT_CONTENT );
                    String    ret;
                    if( oldValue != null ) {
                        ret = oldValue.getAsString();
                    } else {
                        ret = "";
                    }
                    return ret;

                } catch( NotPermittedException ex ) {
                    throw new ServletException( ex );
                }
            }

            /**
             * Given this action, determine the mode that the Viewlet is in.
             *
             * @return the Mode
             */
            public Mode determineMode()
            {
                return Mode.EDIT;
            }
        };

        /**
         * Constructor.
         *
         * @param actionName name of the action
         */
        private Action(
                String actionName )
        {
            theActionName = actionName;
        }

        /**
         * Obtain the name of this Action.
         *
         * @return the name of this Action
         */
        public String getName()
        {
            return theActionName;
        }

        /**
         * Find the correct action, given the keyword.
         *
         * @param keyword the keyword
         * @return the found Action
         */
        public static Action findAction(
                Object modeName )
        {
            if( modeName != null ) {
                for( Action candidate : Action.values() ) {
                    if( candidate.theActionName.equals( modeName )) {
                        return candidate;
                    }
                }
            }
            return CANCEL;            
        }
        
        /**
         * Process the incoming request.
         *
         * @param subject the WikiObject
         * @param mode the Mode to put the Viewlet in
         * @param currentContent the current content
         * @throws ServletException thrown if an error occurred
         * @return the new content
         */
        protected abstract String process(
                MeshObject subject,
                Mode       mode,
                String     currentContent )
            throws
                ServletException;

        /**
         * Given this action, determine the mode that the Viewlet is in.
         *
         * @return the Mode
         */
        public abstract Mode determineMode();
        
        /**
         * Name of the action, as used in the protocol, of the current action.
         */
        protected String theActionName;
    }
}
