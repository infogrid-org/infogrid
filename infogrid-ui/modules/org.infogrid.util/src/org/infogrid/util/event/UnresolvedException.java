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

package org.infogrid.util.event;

/**
 * Thrown if a member of an ExternalizableEvent object is accessed that has not been resolved.
 */
public abstract class UnresolvedException
        extends
            RuntimeException
{
    /** 
     * Constructor, for subclasses only.
     *
     * @param event the ExternalizableEvent that was not resolved
     * @param cause the cause of the Exception, if any
     */
    protected UnresolvedException(
            ExternalizableEvent event,
            Throwable           cause )
    {
        super( cause );

        theEvent = event;
    }
    
    /**
     * Obtain the ExternalizableEvent that was not resolved.
     * 
     * @return the ExternalizableEvent that was not resolved
     */
    public ExternalizableEvent getEvent()
    {
        return theEvent;
    }

    /**
     * The ExternalizableEvent that was not resolved.
     */
    protected ExternalizableEvent theEvent;
    
    /**
     * Indicates that the source of the ExternalizableEvent was not resolved.
     */
    public static class Source
            extends
                UnresolvedException
    {
        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         */
        public Source(
                ExternalizableEvent event )
        {
            super( event, null );
        }

        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         * @param cause the cause of the Exception, if any
         */
        public Source(
                ExternalizableEvent event,
                Throwable           cause )
        {
            super( event, cause );
        }
    }

    /**
     * Indicates that the value of the ExternalizableEvent was not resolved.
     */
    public static class Value
            extends
                UnresolvedException
    {
        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         */
        public Value(
                ExternalizableEvent event )
        {
            super( event, null );
        }

        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         * @param cause the cause of the Exception, if any
         */
        public Value(
                ExternalizableEvent event,
                Throwable           cause )
        {
            super( event, cause );
        }
    }

    /**
     * Indicates that the property of the ExternalizablePropertyChangeEvent was not resolved.
     */
    public static class Property
            extends
                UnresolvedException
    {
        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         */
        public Property(
                ExternalizablePropertyChangeEvent event )
        {
            super( event, null );
        }

        /**
         * Constructor.
         *
         * @param event the ExternalizablePropertyChangeEvent that was not resolved
         * @param cause the cause of the Exception, if any
         */
        public Property(
                ExternalizablePropertyChangeEvent event,
                Throwable                         cause )
        {
            super( event, cause );
        }

        /**
         * Obtain the ExternalizableEvent that was not resolved.
         * 
         * @return the ExternalizableEvent that was not resolved
         */
        @Override
        public final ExternalizablePropertyChangeEvent getEvent()
        {
            return (ExternalizablePropertyChangeEvent) super.getEvent();
        }
    }

    /**
     * Indicates that some other component of the ExternalizableEvent was not resolved.
     */
    public static class Other
            extends
                UnresolvedException
    {
        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         */
        public Other(
                ExternalizableEvent event )
        {
            super( event, null );
        }

        /**
         * Constructor.
         *
         * @param event the ExternalizableEvent that was not resolved
         * @param cause the cause of the Exception, if any
         */
        public Other(
                ExternalizableEvent event,
                Throwable           cause )
        {
            super( event, cause );
        }
    }
}
