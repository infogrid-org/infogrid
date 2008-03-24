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

package org.infogrid.util.instrument;

import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

/**
 * This is pretty much the same thing as what a debugger calls a Breakpoint, except that
 * this one can be run without the debugger. It collaborates with InstrumentedThread,
 * and can only be used on Threads that are InstrumentedThread. The run-time overhead
 * is fairly small, so it can be incorporated in production code.
 */
public class Breakpoint
{
    private static final Log log = Log.getLogInstance( Breakpoint.class ); // our own, private logger

    /**
     * Constructor, not specifying a name for this Breakpoint.
     */
    public Breakpoint()
    {
        this( null, 0 );
    }

    /**
     * Constructor, with a name. The name is provided only for identification purposes
     * during debugging.
     *
     * @param name a name for this Breakpoint
     * @param delay the maximum time to stop at the breakpoint, in milliseconds. 0 means forever
     */
    public Breakpoint(
            String name,
            long   delay )
    {
        theName  = name;
        theDelay = delay;
    }

    /**
     * To declare that running code has reached this Breakpoint, invoke this method.
     * Depending on whether this Breakpoint is active or not, the thread will either
     * return immediately, or be suspended.
     *
     * @throws java.lang.InterruptedException thrown if the Thread was interrupted while waiting at this Breakpoint
     */
    public final void reached()
        throws
            InterruptedException
    {
        logEnter();

        Thread current = Thread.currentThread();
        if( current instanceof InstrumentedThread ) {
            InstrumentedThread realCurrent = (InstrumentedThread) current;

            synchronized( this ) {
                notifyAll();

                logWait();

                if( realCurrent.getNextBreakpoint() == this ) {
                    wait( theDelay );
                }
            }
        }
        logExit();
    }

    /**
     * This overridable method provides a hook through which we can log having entered this Breakpoint.
     */
    protected void logEnter()
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + " has been entered" );
        }
    }

    /**
     * This overridable method provides a hook through which we can log that we are now suspended at this Breakpoint.
     */
    protected void logWait()
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ": in wait state" );
        }
    }

    /**
     * This overridable method provides a hook through which we can log having exited this Breakpoint.
     */
    protected void logExit()
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + " has been exited" );
        }
    }

    /**
     * Convert to String, for debugging.
     * 
     * @return String representation
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "name"
                },
                new Object[] {
                    theName
                });
    }

    /**
     * A name for this Breakpoint.
     */
    protected String theName;
    
    /**
     * The maximum length of time to wait at the Breakpoint, in milliseconds. 0 means forever.
     */
    protected long theDelay;
}
