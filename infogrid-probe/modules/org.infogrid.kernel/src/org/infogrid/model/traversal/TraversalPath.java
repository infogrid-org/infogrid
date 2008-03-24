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

package org.infogrid.model.traversal;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.WrongMeshBaseException;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectStateEvent;
import org.infogrid.util.FlexiblePropertyChangeListenerSet;
import org.infogrid.util.logging.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.infogrid.util.StringHelper;

/**
 * <p>This represents a path of traversal from an (implied) start MeshObject,
 * to another or the same MeshObject, via a sequence of zero or more steps. One
 * step is the combination of a TraversalSpecification, and the resulting
 * MeshObject.</p>
 *
 * <p>TraversalPaths are a handy structure to capture a "path" through an MeshObject
 * graph, similar to a path in a file system, but more powerful. For one, one can
 * traverse different types of TraversalSpecification, not just one as in case of
 * the file system.</p>
 *
 * <p>A TraversalPath is immutable. However, one can add a special type of
 * PropertyChangeListener to it, which will be notified of all PropertyChangeEvents
 * of MeshObjects on the TraversalPath. It uses a special subclass of
 * PropertyChangeEvent, TraversalPathPropertyChangeEvent, to indicate which
 * TraversalPath the changed MeshObject was on.</p>
 *
 * <p>TraversalPath is implemented as a recursive data structure.</p>
 */
public class TraversalPath
        implements
            PropertyChangeListener
{
    private static final Log log = Log.getLogInstance( TraversalPath.class ); // our own, private logger

    /**
     * Factory method for only one step on the path.
     *
     * @param traversedSpec the TraversalSpecification that we traversed
     * @param reachedMeshObject the MeshObject that was reached by doing the traversal
     * @return the created TraversalPath
     */
    public static TraversalPath create(
            TraversalSpecification traversedSpec,
            MeshObject             reachedMeshObject )
    {
        return create( traversedSpec, reachedMeshObject, null );
    }

    /**
     * Factory method by prepending a step to an already-existing TraversalPath.
     *
     * @param traversedSpec the TraversalSpecification that we traversed
     * @param reachedMeshObject the MeshObject that was reached by doing the traversal
     * @param remainder the TraversalPath to which we prepend this step
     * @return the created TraversalPath
     * @throws WrongMeshBaseException thrown if a TraversalPath is prepended to a TraversalPath with MeshObjects in different MeshBases
     */
    public static TraversalPath create(
            TraversalSpecification traversedSpec,
            MeshObject             reachedMeshObject,
            TraversalPath          remainder )
        throws
            WrongMeshBaseException
    {
        int size;

        if( remainder != null ) {
            if( reachedMeshObject.getMeshBase() != remainder.getMeshBase() ) {
                throw new WrongMeshBaseException( reachedMeshObject.getMeshBase(), remainder.getMeshBase() );
            }
            size = remainder.getSize();
        } else {
            size = 1;
        }
        TraversalPath ret = new TraversalPath(
                traversedSpec,
                reachedMeshObject,
                remainder,
                size );
        
        return ret;
    }

    /**
     * Factory method to construct one as a concatenation of two other TraversalPaths.
     *
     * @param one the first TraversalPath
     * @param two the second TraversalPath
     * @throws WrongMeshBaseException thrown if a TraversalPath is prepended to a TraversalPath with MeshObjects in different MeshBases
     */
    public static TraversalPath create(
            TraversalPath one,
            TraversalPath two )
        throws
            WrongMeshBaseException
    {
        if( one == null || two == null ) {
            log.error( "argument is null" );
        }
        if( one.getMeshBase() != two.getMeshBase() ) {
            throw new WrongMeshBaseException( one.getMeshBase(), two.getMeshBase() );
        }

        TraversalPath remainder = recursiveCreate( one.theRemainder, two );

        TraversalPath ret = new TraversalPath(
                one.theTraversedSpec,
                one.theReached,
                remainder,
                one.theSize + two.theSize );

        return ret;
    }

    /**
     * Little helper method to recursively create.
     *
     * @param here the TraversalPath that is the first part
     * @param remainder the remainder TraversalPath
     * @return the created TraversalPath
     */
    private static TraversalPath recursiveCreate(
            TraversalPath here,
            TraversalPath remainder )
    {
        if( here == null ) {
            return remainder;
        } else {
            TraversalPath nextStep = recursiveCreate( here.theRemainder, remainder );
            int           size     = ( nextStep != null ) ? ( nextStep.theSize+1 ) : 1;
            return new TraversalPath(
                    here.theTraversedSpec,
                    here.theReached,
                    nextStep,
                    size );
        }
    }

    /**
     * Constructor.
     *
     * @param traversedSpec the TraversalSpecification that we traversed
     * @param reachedMeshObject the MeshObject that was reached by doing the traversal
     * @param size the number of steps in this TraversalPath
     */
    protected TraversalPath(
            TraversalSpecification traversedSpec,
            MeshObject             reachedMeshObject,
            TraversalPath          remainder,
            int                    size )
    {
        theTraversedSpec = traversedSpec;
        theReached       = reachedMeshObject;
        theRemainder     = remainder;
        theSize          = size;
    }

    /**
     * Set the debug name. This is to be used for debugging only.
     *
     * @param debugName the debug name
     */
    public void setDebugName(
            String newValue )
    {
        theDebugName = newValue;
    }

    /**
     * Obtain the debug name. This is to be used for debugging only.
     *
     * @return the name
     */
    public String getDebugName()
    {
        return theDebugName;
    }

    /**
     * Obtain the MeshBase to which this TraversalPath belongs.
     *
     * @return the MeshBase
     */
    public MeshBase getMeshBase()
    {
        return theReached.getMeshBase();
    }

    /**
     * Determine the number of steps in this TraversalPath.
     *
     * @return the number of steps in this TraversalPath
     */
    public int getSize()
    {
        return theSize;
    }

    /**
     * Obtain the TraversalSpecification at a certain position in this TraversalPath.
     *
     * @param index the index from which we want to obtain the TraversalSpecification
     * @return the TraversalSpecification at this index
     * @throws ArrayIndexOutOfBoundsException if index is out of range
     */
    public TraversalSpecification getTraversalSpecificationAt(
            int index )
    {
        if( index >= theSize ) {
            throw new ArrayIndexOutOfBoundsException( index );
        }
        if( index == 0 ) {
            return theTraversedSpec;
        }
        return theRemainder.getTraversalSpecificationAt( index-1 );
    }

    /**
     * Obtain the MeshObject at a certain position in this TraversalPath.
     *
     * @param index the index from which we want to obtain the MeshObject
     * @return the MeshObject at this index
     * @throws ArrayIndexOutOfBoundsException if index is out of range
     */
    public MeshObject getMeshObjectAt(
            int index )
    {
        if( index >= theSize ) {
            throw new ArrayIndexOutOfBoundsException( index );
        }
        if( index == 0 ) {
            return theReached;
        }
        return theRemainder.getMeshObjectAt( index-1 );
    }

    /**
     * This convenience method returns all MeshObjects in this TraversalPath in sequence.
     *
     * @return an array of size getSize(), which contains the MeshObject on the TraversalPath
     */
    public MeshObject [] getEntitiesInSequence()
    {
        MeshObject [] ret = new MeshObject[ getSize() ];

        TraversalPath current = this;
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = current.theReached;
            current = current.getNextSegment();
        }
        return ret;
    }

    /**
     * Obtain the first MeshObject on this TraversalPath.
     *
     * @return the first MeshObject on this TraversalPath
     */
    public MeshObject getFirstMeshObject()
    {
        return theReached;
    }

    /**
     * Obtain the last MeshObject on this TraversalPath.
     *
     * @return the last MeshObject on this TraversalPath
     */
    public MeshObject getLastMeshObject()
    {
        if( theRemainder != null ) {
            return theRemainder.getLastMeshObject();
        } else {
            return theReached;
        }
    }

    /**
     * Obtain the next segment in this TraversalPath.
     *
     * @return obtain the next segment in this TraversalPath, if there is one
     */
    public TraversalPath getNextSegment()
    {
        return theRemainder;
    }

    /**
     * Determine equality.
     *
     * @param other the Object to compare against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof TraversalPath ) {

            TraversalPath realOther = (TraversalPath) other;

            if( theRemainder == null && realOther.theRemainder != null ) {
                return false;
            }
            if( theRemainder != null && realOther.theRemainder == null ) {
                return false;
            }

            if( !theTraversedSpec.equals( realOther.theTraversedSpec )) {
                return false;
            }

            if( !theReached.equals( realOther.theReached )) {
                return false;
            }

            if( theRemainder != null ) {
                return theRemainder.equals( realOther.theRemainder );
            }
            return true;
        }
        return false;
    }

    /**
     * Obtain the hash code, which is a combination of of the reached MeshObjects' hash codes.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return theReached.hashCode() | theTraversedSpec.hashCode(); // good enough
    }

    /**
     * Determine whether this TraversalPath starts with all of the TraversalPath elements
     * of the passed-in test path.
     *
     * @param testPath the TraversalPath to test against
     * @return true if testPath is fully contained in this TraversalPath at the beginning
     */
    public boolean startsWith(
            TraversalPath testPath )
    {
        if( testPath == null ) {
            return true;
        }

        if( !theTraversedSpec.equals( testPath.theTraversedSpec )) {
            return false;
        }

        if( !theReached.equals( testPath.theReached )) {
            return false;
        }

        if( theRemainder != null ) {
            return theRemainder.startsWith( testPath.theRemainder );
        } else {
            return testPath.theRemainder == null;
        }
    }

    /**
     * Add a PropertyChangeListener listening to PropertyChangeEvents of Entities
     * on the TraversalPath.
     *
     * @param newListener the listener to add
     * @see #removeTraversalPathPropertyChangeListener
     */
    public synchronized void addDirectTraversalPathPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        if( theListeners == null ) {
            theListeners = new FlexiblePropertyChangeListenerSet();
        }
        theListeners.addDirect( newListener );

        if( theListeners.size() == 1 ) {
            // subscribe
            TraversalPath current = this;
            while( current != null ) {
                current.getFirstMeshObject().addDirectPropertyChangeListener( this );
                current = current.getNextSegment();
            }
        }
    }

    /**
     * Add a PropertyChangeListener listening to PropertyChangeEvents of Entities
     * on the TraversalPath.
     *
     * @param newListener the listener to add
     * @see #removeTraversalPathPropertyChangeListener
     */
    public synchronized void addWeakTraversalPathPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        if( theListeners == null ) {
            theListeners = new FlexiblePropertyChangeListenerSet();
        }
        theListeners.addDirect( newListener );

        if( theListeners.size() == 1 ) {
            // subscribe
            TraversalPath current = this;
            while( current != null ) {
                current.getFirstMeshObject().addWeakPropertyChangeListener( this );
                current = current.getNextSegment();
            }
        }
    }

    /**
     * Add a PropertyChangeListener listening to PropertyChangeEvents of Entities
     * on the TraversalPath.
     *
     * @param newListener the listener to add
     * @see #removeTraversalPathPropertyChangeListener
     */
    public synchronized void addSoftTraversalPathPropertyChangeListener(
            PropertyChangeListener newListener )
    {
        if( theListeners == null ) {
            theListeners = new FlexiblePropertyChangeListenerSet();
        }
        theListeners.addDirect( newListener );

        if( theListeners.size() == 1 ) {
            // subscribe
            TraversalPath current = this;
            while( current != null ) {
                current.getFirstMeshObject().addSoftPropertyChangeListener( this );
                current = current.getNextSegment();
            }
        }
    }

    /**
     * Remove a PropertyChangeListener listening to PropertyChangeEvents of Entities
     * on the TraversalPath.
     *
     * @param oldListener the listener to remove
     * @see #addTraversalPathPropertyChangeListener
     */
    public synchronized void removeTraversalPathPropertyChangeListener(
            PropertyChangeListener oldListener )
    {
        if( theListeners == null ) {
            return; // defensive programming
        }

        theListeners.remove( oldListener );

        if( theListeners.isEmpty() ) {
            // unsubscribe
            theListeners = null;

            TraversalPath current = this;
            while( current != null ) {
                current.getFirstMeshObject().removePropertyChangeListener( this );
                current = current.getNextSegment();
            }
        }
    }

    /**
     * Callback from one of the MeshObjects in this set -- forward property change event.
     *
     * @param theEvent the event
     */
    public void propertyChange(
            PropertyChangeEvent theEvent )
    {
        // we don't need to check that we have subscribers -- we only get this when we have subscribers

        PropertyChangeEvent delegatedEvent;
        if( theEvent instanceof MeshObjectPropertyChangeEvent ) {
            delegatedEvent = new TraversalPathDelegatedPropertyChangeEvent(
                    this,
                    (MeshObjectPropertyChangeEvent) theEvent,
                    theReached.getMeshBase() );

        } else if( theEvent instanceof MeshObjectRoleAddedEvent ) {
            MeshObjectRoleAddedEvent realEvent = (MeshObjectRoleAddedEvent) theEvent;
            delegatedEvent = new TraversalPathDelegatedRoleChangeEvent.Added( this, realEvent, theReached.getMeshBase() );

        } else if( theEvent instanceof MeshObjectRoleRemovedEvent ) {
            MeshObjectRoleRemovedEvent realEvent = (MeshObjectRoleRemovedEvent) theEvent;
            delegatedEvent = new TraversalPathDelegatedRoleChangeEvent.Removed( this, realEvent, theReached.getMeshBase() );

        } else if( theEvent instanceof MeshObjectStateEvent ) {
            return;

        } else {
            log.error( "unexpected subtype of PropertyChangeEvent: " + theEvent );
            return;
        }

        FlexiblePropertyChangeListenerSet listeners = theListeners;
        if( listeners != null ) {
            listeners.fireEvent( delegatedEvent );
        }
    }

    /**
     * Convert to String, for debugging.
     *
     * @return String representation of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "debugName",
                    "traversedSpec",
                    "reached",
                    "remainder"
                },
                new Object[] {
                    theDebugName,
                    theTraversedSpec,
                    theReached,
                    theRemainder
                });
    }

    /**
     * Our name (if any). For debugging only.
     */
    protected String theDebugName;

    /**
     * The first TraversalSpecification that we traversed.
     */
    protected TraversalSpecification theTraversedSpec;

    /**
     * The MeshObject that we reached after we traversed the TraversalSpecification.
     */
    protected MeshObject theReached;

    /**
     * The remainder (aka second part) of this TraversalPath.
     */
    protected TraversalPath theRemainder;

    /**
     * The length of this TraversalPath. We store this to avoid the frequent, recursive
     * calculations.
     */
    protected int theSize;

    /**
     * Our listeners, if any.
     */
    protected FlexiblePropertyChangeListenerSet theListeners;
}
