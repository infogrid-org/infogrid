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

package org.infogrid.meshbase;

import org.infogrid.mesh.BlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotBlessedException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;

import org.infogrid.mesh.set.MeshObjectSet;

import org.infogrid.meshbase.transaction.AbstractMeshObjectEquivalentsChangeEvent;
import org.infogrid.meshbase.transaction.AbstractMeshObjectLifecycleEvent;
import org.infogrid.meshbase.transaction.AbstractMeshObjectNeighborChangeEvent;
import org.infogrid.meshbase.transaction.AbstractMeshObjectRoleChangeEvent;
import org.infogrid.meshbase.transaction.AbstractMeshObjectTypeChangeEvent;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.MeshObjectCreatedEvent;
import org.infogrid.meshbase.transaction.MeshObjectDeletedEvent;
import org.infogrid.meshbase.transaction.MeshObjectEquivalentsAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectEquivalentsRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.transaction.MeshObjectTypeAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

import java.util.ArrayList;

/**
  * <p>Provides the functionality to perform a diff on any two IterableMeshBases.</p>
  *
  * <p>This class creates two MeshObjectNeighborChangeEvents for each changed relationships
  * (one for the source MeshObject, and one for the destination MeshObject). Strictly speaking,
  * only one is necessary and the other is redundant. However, overall it appears more efficient
  * (and is certainly easier to program) to generate and process both (i.e. ignore the second
  * one), so that's what we do.</p>
  */
public class IterableMeshBaseDifferencer
{
    private static final Log log = Log.getLogInstance( IterableMeshBaseDifferencer.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param baselineBase the IterableMeshBase that forms the "base" against which compare
     */
    public IterableMeshBaseDifferencer(
            IterableMeshBase baselineBase )
    {
        theBaselineBase = baselineBase;
    }

    /**
     * Obtain the base IterableMeshBase against which we compare, and onto which we apply changes.
     *
     * @return the base IterableMeshBase
     */
    public IterableMeshBase getBaselineMeshBase()
    {
        return theBaselineBase;
    }

    /**
     * This method performs the diff between the baseline IterableMeshBase (set in the constructor)
     * and the comparison IterableMeshBase (provided as argument to this call). The differences between
     * the MeshObjects in comparison IterableMeshBase as compared to the MeshObjects in the baseline
     * IterableMeshBase are returned as a ChangeSet. The ChangeSet includes all necessary changes that,
     * when applied on the baseline IterableMeshBase, transform it into the comparison IterableMeshBase.
     *
     * @param comparisonBase the IterableMeshBase to compare against
     * @return the ChangeSet between the baseline IterableMeshBase and the comparison IterableMeshBase
     */
    public ChangeSet determineChangeSet(
            IterableMeshBase comparisonBase )
    {
        return determineChangeSet( comparisonBase, false );
    }

    /**
     * This method performs the diff between the baseline IterableMeshBase (set in the constructor)
     * and the comparison IterableMeshBase (provided as argument to this call). The differences between
     * the MeshObjects in comparison IterableMeshBase as compared to the MeshObjects in the baseline
     * IterableMeshBase are returned as a ChangeSet. The ChangeSet includes all necessary changes that,
     * when applied on the baseline IterableMeshBase, transform it into the comparison IterableMeshBase.
     *
     * @param comparisonBase the IterableMeshBase to compare against
     * @param checkDates if true, this will also check created, updated, and auto-delete dates
     * @return the ChangeSet between the baseline IterableMeshBase and the comparison IterableMeshBase
     */
    public ChangeSet determineChangeSet(
            IterableMeshBase comparisonBase,
            boolean          checkDates )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".determineChangeSet()" );
        }

        long      now                    = System.currentTimeMillis();
        
        ChangeSet entityChanges          = ChangeSet.create();
        ChangeSet graphChanges           = ChangeSet.create();
        ChangeSet roleAdditionChanges    = ChangeSet.create();
        ChangeSet roleSubtractionChanges = ChangeSet.create();

        for( MeshObject meshObjectInBase : theBaselineBase ) {
            MeshObjectIdentifier identifier             = meshObjectInBase.getIdentifier();
            MeshObject           meshObjectInComparison = comparisonBase.findMeshObjectByIdentifier( identifier );

            if( meshObjectInComparison == null ) {
                // If the MeshObject from BASE does not exist in COMPARISON,
                // add MeshObjectObsoletionChange in the ChangeSet

                AbstractMeshObjectLifecycleEvent obsoletionChange = createMeshObjectDeletedEvent( meshObjectInBase, identifier, now );
                entityChanges.addChange( obsoletionChange );

            } else {
                // If the MeshObject from BASE exists on COMPARISON,
                // we need to check for any changes made to it.

                // Let's compare the types. First add missing types, then remove obsoleted ones (that way we
                // won't miss PropertyValues later).
                EntityType [] objectTypesInBase       = meshObjectInBase.getTypes();
                EntityType [] objectTypesInComparison = meshObjectInComparison.getTypes();

                ArrayList<EntityType> addedTypes = new ArrayList<EntityType>();
                for( EntityType objectTypeInComparison : objectTypesInComparison ) {
                    if( !ArrayHelper.isIn( objectTypeInComparison, objectTypesInBase, false ) ) {

                        addedTypes.add( objectTypeInComparison );
                    }
                }
                EntityType [] addedTypesArray      = ArrayHelper.copyIntoNewArray( addedTypes, EntityType.class );
                EntityType [] hypotheticalAllTypes = ArrayHelper.append( objectTypesInBase, addedTypesArray, EntityType.class ); // need to do this given that subtractions are separate

                if( !addedTypes.isEmpty() ) {
                    AbstractMeshObjectTypeChangeEvent addedTypeChange = createMeshObjectTypesAddedEvent( 
                            meshObjectInBase,
                            meshObjectInComparison,
                            objectTypesInBase,
                            addedTypesArray,
                            hypotheticalAllTypes );
                    entityChanges.addChange( addedTypeChange );
                }

                ArrayList<EntityType> removedTypes = new ArrayList<EntityType>();
                for( EntityType objectTypeInBase : objectTypesInBase ) {
                    if( !ArrayHelper.isIn( objectTypeInBase, objectTypesInComparison, false ) ) {

                        removedTypes.add( objectTypeInBase );
                    }
                }
                if( !removedTypes.isEmpty() ) {
                    AbstractMeshObjectTypeChangeEvent removedTypeChange = createMeshObjectTypesRemovedEvent(
                            meshObjectInBase,
                            meshObjectInComparison,
                            hypotheticalAllTypes,
                            ArrayHelper.copyIntoNewArray( removedTypes, EntityType.class ),
                            objectTypesInComparison );
                    entityChanges.addChange( removedTypeChange );
                }
                
                // Now compare the properties. We only compare the ones in COMPARISON, because the ones
                // in BASE become irrelevant as the type changes are applied
                for( EntityType objectTypeInComparison : objectTypesInComparison ) {
                    for( PropertyType propertyTypeInComparison : objectTypeInComparison.getAllPropertyTypes() ) {

                        try {
                            PropertyValue propertyValueInBase       = meshObjectInBase.getPropertyValue( propertyTypeInComparison );
                            PropertyValue propertyValueInComparison = meshObjectInComparison.getPropertyValue( propertyTypeInComparison );

                            if( PropertyValue.compare( propertyValueInBase, propertyValueInComparison ) != 0 ) {
                                // there is a change
                                MeshObjectPropertyChangeEvent propertyChange = createMeshObjectPropertyChangeEvent(
                                        meshObjectInBase,
                                        meshObjectInComparison,
                                        propertyTypeInComparison,
                                        propertyValueInBase,
                                        propertyValueInComparison );

                                entityChanges.addChange( propertyChange );
                            }
                        } catch( NotPermittedException ex ) {
                            log.error( ex );
                        } catch( EntityNotBlessedException ex ) {
                            // that's fine (FIXME?)
                        }

                        if( checkDates ) {
                            // also check creation and update times
                            if( meshObjectInBase.getTimeCreated() != meshObjectInComparison.getTimeCreated() ) {
                                log.error(
                                        "Different creation times for "
                                        + meshObjectInBase.getIdentifier()
                                        + ": "
                                        + meshObjectInBase.getTimeCreated()
                                        + " vs. "
                                        + meshObjectInComparison.getTimeCreated() );
                            }
                            if( meshObjectInBase.getTimeUpdated() != meshObjectInComparison.getTimeUpdated() ) {
                                log.error(
                                        "Different update times for "
                                        + meshObjectInBase.getIdentifier()
                                        + ": "
                                        + meshObjectInBase.getTimeUpdated()
                                        + " vs. "
                                        + meshObjectInComparison.getTimeUpdated() );
                            }
                            if( meshObjectInBase.getTimeRead() != meshObjectInComparison.getTimeRead() ) {
                                log.error(
                                        "Different update times for "
                                        + meshObjectInBase.getIdentifier()
                                        + ": "
                                        + meshObjectInBase.getTimeRead()
                                        + " vs. "
                                        + meshObjectInComparison.getTimeRead() );
                            }
                        }
                    }
                }

                // now look at the attached relationships
                MeshObjectSet otherSidesInBase       = meshObjectInBase.traverseToNeighborMeshObjects( false );
                MeshObjectSet otherSidesInComparison = meshObjectInComparison.traverseToNeighborMeshObjects( false );

                for( MeshObject currentOtherSideInBase : otherSidesInBase ) {
                    if( ! otherSidesInComparison.contains( currentOtherSideInBase.getIdentifier() ) ) {
                        // check whether this MeshObject has been obsoleted, so we don't have to do anything
                        if( comparisonBase.findMeshObjectByIdentifier( currentOtherSideInBase.getIdentifier() ) != null ) {
                            
                            AbstractMeshObjectNeighborChangeEvent relChange = createMeshObjectNeighborRemovedEvent(
                                    meshObjectInBase,
                                    meshObjectInComparison,
                                    otherSidesInBase,
                                    currentOtherSideInBase,
                                    otherSidesInComparison );
                                    
                            graphChanges.addChange( relChange );
                        }
                    } else {
                        // noop, nothing changed
                    }
                }
                
                for( MeshObject currentOtherSideInComparison : otherSidesInComparison ) {
                    if( ! otherSidesInBase.contains( currentOtherSideInComparison.getIdentifier() ) ) {
                        // in this case, we have to do things anyway
                        
                        RoleType [] addedRoleTypes = meshObjectInComparison.getRoleTypes( currentOtherSideInComparison );
                        
                        AbstractMeshObjectNeighborChangeEvent relChange = createMeshObjectNeighborAddedEvent(
                                meshObjectInBase,
                                meshObjectInComparison,
                                otherSidesInBase,
                                currentOtherSideInComparison,
                                otherSidesInComparison,
                                addedRoleTypes );

                        graphChanges.addChange( relChange );
                    }

                    // in any case, check the RoleTypes
                    MeshObject currentOtherSideInBase = theBaselineBase.findMeshObjectByIdentifier( currentOtherSideInComparison.getIdentifier() );
                    if( currentOtherSideInBase != null ) {
                        RoleType [] roleTypesInBase;
                        RoleType [] roleTypesInComparison;

                        if( meshObjectInBase.isRelated( currentOtherSideInBase )) {
                            roleTypesInBase = meshObjectInBase.getRoleTypes( currentOtherSideInBase );
                        } else {
                            roleTypesInBase = null;
                        }
                        if( meshObjectInComparison.isRelated( currentOtherSideInComparison )) {
                            roleTypesInComparison = meshObjectInComparison.getRoleTypes( currentOtherSideInComparison );
                        } else {
                            roleTypesInComparison = null;
                        }

                        RoleType [] hypotheticalMinRoleTypes = new RoleType[0];
                        if( roleTypesInBase != null ) {
                            RoleType [] removed = new RoleType[ roleTypesInBase.length ];
                            int         count   = 0;
                            
                            for( RoleType currentRoleTypeInBase : roleTypesInBase ) {
                                if( roleTypesInComparison == null || !ArrayHelper.isIn( currentRoleTypeInBase, roleTypesInComparison, false )) {
                                    removed[ count++ ] = currentRoleTypeInBase;
                                }
                            }
                            if( count > 0 ) {
                                if( count < removed.length ) {
                                    removed = ArrayHelper.subarray( removed, 0, count, RoleType.class );
                                }
                                hypotheticalMinRoleTypes = ArrayHelper.removeIfPresent( roleTypesInBase, removed, false, RoleType.class ); // the "if present" is probably not good -- FIXME
                                
                                AbstractMeshObjectRoleChangeEvent change = createMeshObjectRoleRemovedEvent(
                                        meshObjectInBase,
                                        meshObjectInComparison,
                                        roleTypesInBase,
                                        removed,
                                        hypotheticalMinRoleTypes,
                                        currentOtherSideInBase );
                                roleSubtractionChanges.addChange( change );
                            }
                        }

                        if( roleTypesInComparison != null ) {
                            RoleType [] added = new RoleType[ roleTypesInComparison.length ];
                            int         count = 0;

                            for( RoleType currentRoleTypeInComparison : roleTypesInComparison ) {
                                if( roleTypesInBase == null || !ArrayHelper.isIn( currentRoleTypeInComparison, roleTypesInBase, false )) {
                                    added[ count++ ] = currentRoleTypeInComparison;
                                }
                            }
                            if( count > 0 ) {
                                if( count < added.length ) {
                                    added = ArrayHelper.subarray( added, 0, count, RoleType.class );
                                }
                                AbstractMeshObjectRoleChangeEvent change = createMeshObjectRoleAddedEvent(
                                        meshObjectInBase,
                                        meshObjectInComparison,
                                        hypotheticalMinRoleTypes,
                                        added,
                                        roleTypesInComparison,
                                        currentOtherSideInComparison );

                                roleAdditionChanges.addChange( change );
                            }
                        }
                    }
                }
                
                MeshObjectSet      equivalentsInBase       = meshObjectInBase.getEquivalents();
                MeshObjectSet      equivalentsInComparison = meshObjectInComparison.getEquivalents();
                MeshObjectIdentifier [] equivalentsInBaseIdentifiers       = equivalentsInBase.asIdentifiers();
                MeshObjectIdentifier [] equivalentsInComparisonIdentifiers = equivalentsInComparison.asIdentifiers();

                ArrayHelper.Difference<MeshObject> diff
                        = ArrayHelper.determineDifference(
                                equivalentsInBase.getMeshObjects(),
                                equivalentsInComparison.getMeshObjects(),
                                true,
                                MeshObject.class );

                MeshObject [] removal  = diff.getRemovals();
                MeshObject [] addition = diff.getAdditions();

                MeshObjectSet hypotheticalMinEquivalents;
                if( removal != null && removal.length > 0 ) {
                    hypotheticalMinEquivalents = theBaselineBase.getMeshObjectSetFactory().createImmutableMeshObjectSetMinus(
                            equivalentsInBase,
                            theBaselineBase.getMeshObjectSetFactory().createImmutableMeshObjectSet( removal ));

                    AbstractMeshObjectEquivalentsChangeEvent equivChange = createMeshObjectEquivalentsRemovedEvent(
                            meshObjectInBase,
                            meshObjectInComparison,
                            equivalentsInBase,
                            removal,
                            hypotheticalMinEquivalents );
                            
                    graphChanges.addChange( equivChange );

                } else {
                    hypotheticalMinEquivalents = equivalentsInBase;
                }

                if( addition != null && addition.length > 0 ) {
                    
                    AbstractMeshObjectEquivalentsChangeEvent equivChange = createMeshObjectEquivalentsAddedEvent(
                            meshObjectInBase,
                            meshObjectInComparison,
                            hypotheticalMinEquivalents,
                            addition,
                            equivalentsInComparison );
                    
                    graphChanges.addChange( equivChange );                    
                }
            }
        }

        // iterate through all the objects in COMPARISON
        for( MeshObject meshObjectInComparison : comparisonBase ) {

            MeshObject meshObjectInBase = theBaselineBase.findMeshObjectByIdentifier( meshObjectInComparison.getIdentifier() );

            // If the MeshObject from COMPARISON does not exist in BASE, add a MeshObjectCreatedEvent to the ChangeSet
            if( meshObjectInBase == null ) {

                AbstractMeshObjectLifecycleEvent creationChange = new MeshObjectCreatedEvent(
                        comparisonBase,
                        comparisonBase.getIdentifier(),
                        meshObjectInComparison,
                        meshObjectInComparison.getTimeCreated() );
                entityChanges.addChange( creationChange );
                
                // we don't need to do anything about blessing -- that's captured in the MeshObjectCreatedEvent
                for( EntityType objectTypeInComparison : meshObjectInComparison.getTypes() ) {
                    for( PropertyType propertyTypeInComparison : objectTypeInComparison.getAllPropertyTypes() ) {
                        try {
                            PropertyValue propertyValueInComparison = meshObjectInComparison.getPropertyValue( propertyTypeInComparison );
                            if( PropertyValue.compare( propertyTypeInComparison.getDefaultValue(), propertyValueInComparison ) != 0 ) {

                                MeshObjectPropertyChangeEvent propertyChange = createMeshObjectPropertyChangeEvent(
                                        meshObjectInComparison,
                                        meshObjectInComparison,
                                        propertyTypeInComparison,
                                        null,
                                        propertyValueInComparison );

                                entityChanges.addChange( propertyChange );
                            }

                        } catch( NotPermittedException ex ) {
                            log.error( ex );
                        }
                    }
                }

                MeshObjectSet otherSidesInComparison = meshObjectInComparison.traverseToNeighborMeshObjects( false );
                MeshObjectSet oldOtherSides          = comparisonBase.getMeshObjectSetFactory().obtainEmptyImmutableMeshObjectSet();

                for( MeshObject currentOtherSideInComparison : otherSidesInComparison ) {

                    RoleType [] addedRoleTypes = meshObjectInComparison.getRoleTypes( currentOtherSideInComparison );

                    MeshObjectSet newOtherSides = comparisonBase.getMeshObjectSetFactory().createImmutableMeshObjectSetUnification(
                            oldOtherSides,
                            currentOtherSideInComparison );

                    AbstractMeshObjectNeighborChangeEvent relChange = createMeshObjectNeighborAddedEvent(
                            meshObjectInComparison,
                            meshObjectInComparison,
                            comparisonBase.getMeshObjectSetFactory().obtainEmptyImmutableMeshObjectSet(),
                            currentOtherSideInComparison,
                            newOtherSides,
                            addedRoleTypes );

                    graphChanges.addChange( relChange );
                    
                    oldOtherSides = newOtherSides;
                }
                
                MeshObjectSet equivalentsInComparison = meshObjectInComparison.getEquivalents();
                if( equivalentsInComparison.size() > 1 ) {
                    
                    AbstractMeshObjectEquivalentsChangeEvent equivChange = createMeshObjectEquivalentsAddedEvent(
                            meshObjectInComparison,
                            meshObjectInComparison,
                            comparisonBase.getMeshObjectSetFactory().obtainEmptyImmutableMeshObjectSet(),
                            ArrayHelper.remove( equivalentsInComparison.getMeshObjects(), meshObjectInComparison, false, MeshObject.class ),
                            equivalentsInComparison );
                            
                    graphChanges.addChange( equivChange );
                }
            }
        }

        ChangeSet changes = ChangeSet.create( new ChangeSet[] {
                entityChanges,
                graphChanges,
                roleAdditionChanges,
                roleSubtractionChanges
        });
        return changes;
    }

    /**
      * Apply the changes in the ChangeSet to the baseline MeshBase. This method silently
      * ignores most Exceptions in order to apply as many Changes as possible.
      *
      * @param theChangeSet the ChangeSet whose changes we apply
      * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
      */
    public void applyChangeSet(
            ChangeSet theChangeSet )
        throws
            TransactionException
    {
        if( log.isInfoEnabled() ) {
            log.info( this + ".applyChangeSet( " + theChangeSet + " )" );
        }

        for( Change currentChange : theChangeSet ) {
            try {
                currentChange.applyTo( theBaselineBase );

            } catch( CannotApplyChangeException ex ) {
                Throwable cause = ex.getCause();

                if( cause instanceof RelatedAlreadyException ) {
                    // do nothing
                } else if( cause instanceof NotRelatedException ) {
                    // do nothing
                } else if( cause instanceof RoleTypeBlessedAlreadyException ) {
                    // do nothing, it's the other end of the same relationship
                } else if( cause instanceof BlessedAlreadyException ) {
                    log.warn( ex );

                } else if( cause instanceof NotBlessedException ) {
                    // do nothing
                } else {
                    log.error( ex );
                }
            }
        }
    }
    
    /**
     * Allow subclasses to instantiate a more specific event.
     * 
     * @param canonicalIdentifier the canonical Identifier of the MeshObject that was deleted
     * @param obj the MeshObject that was deleted
     * @return the MeshObjectDeletedEvent or subclass
     */
    protected MeshObjectDeletedEvent createMeshObjectDeletedEvent(
            MeshObject           obj,
            MeshObjectIdentifier canonicalIdentifier,
            long                 time )
    {
        MeshObjectDeletedEvent ret = new MeshObjectDeletedEvent(
                obj.getMeshBase(),
                obj.getMeshBase().getIdentifier(),
                obj,
                canonicalIdentifier,
                time );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param addedTypes the added EntityTypes
     * @return the MeshObjectTypeAddedEvent or subclass
     */
    protected MeshObjectTypeAddedEvent createMeshObjectTypesAddedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            EntityType [] oldTypes,
            EntityType [] addedTypes,
            EntityType [] newTypes )
    {
        MeshObjectTypeAddedEvent ret = new MeshObjectTypeAddedEvent(
                meshObjectInBase,
                oldTypes,
                addedTypes,
                newTypes,
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param removedTypes the removed EntityTypes
     * @return the MeshObjectTypeRemovedEvent or subclass
     */
    protected MeshObjectTypeRemovedEvent createMeshObjectTypesRemovedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            EntityType [] oldTypes,
            EntityType [] removedTypes,
            EntityType [] newTypes )
    {
        MeshObjectTypeRemovedEvent ret = new MeshObjectTypeRemovedEvent(
                meshObjectInBase,
                oldTypes,
                removedTypes,
                newTypes,
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param propertyType the PropertyType that was changed
     * @param oldValue the value before the change
     * @param newValue the value after the change
     * @return the MeshObjectPropertyChangeEvent or subclass
     */
    protected MeshObjectPropertyChangeEvent createMeshObjectPropertyChangeEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            PropertyType  propertyType,
            PropertyValue oldValue,
            PropertyValue newValue )
    {
        MeshObjectPropertyChangeEvent ret = new MeshObjectPropertyChangeEvent(
                meshObjectInBase,
                propertyType,
                oldValue,
                newValue,
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param otherSidesInBase the MeshObjectSet of "other sides" in the baseline
     * @param addedNeighborInComparison the MeshObject that was added as a neighbor
     * @param otherSidesInComparison the MeshObjectSet of "other sides" in the comparison
     * @param addedRoleTypes the RoleTypes that the new neighbor relationship was blessed with
     * @return the MeshObjectNeighborAddedEvent or subclass
     */
    protected MeshObjectNeighborAddedEvent createMeshObjectNeighborAddedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            MeshObjectSet otherSidesInBase,
            MeshObject    addedNeighborInComparison,
            MeshObjectSet otherSidesInComparison,
            RoleType []   addedRoleTypes )
    {
        MeshObjectNeighborAddedEvent ret = new MeshObjectNeighborAddedEvent(
                meshObjectInBase,
                addedRoleTypes,
                otherSidesInBase.getMeshObjects(),
                addedNeighborInComparison,
                otherSidesInComparison.getMeshObjects(),
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }
    
    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param otherSidesInBase the MeshObjectSet of "other sides" in the baseline
     * @param removedNeighborInBase the MeshObject that was removed as a neighbor
     * @param otherSidesInComparison the MeshObjectSet of "other sides" in the comparison
     * @return the MeshObjectNeighborRemovedEvent or subclass
     */
    protected MeshObjectNeighborRemovedEvent createMeshObjectNeighborRemovedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            MeshObjectSet otherSidesInBase,
            MeshObject    removedNeighborInBase,
            MeshObjectSet otherSidesInComparison )
    {
        MeshObjectNeighborRemovedEvent ret = new MeshObjectNeighborRemovedEvent(
                meshObjectInBase,
                otherSidesInBase.getMeshObjects(),
                removedNeighborInBase,
                otherSidesInComparison.getMeshObjects(),
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param addedRoleTypes the RoleTypes that were added
     * @param otherSideInBase the "other side" MeshObject in the baseline
     * @return the MeshObjectRoleAddedEvent or subclass
     */
    protected MeshObjectRoleAddedEvent createMeshObjectRoleAddedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            RoleType []   oldRoleTypes,
            RoleType []   addedRoleTypes,
            RoleType []   newRoleTypes,
            MeshObject    otherSideInBase )
    {
        MeshObjectRoleAddedEvent ret = new MeshObjectRoleAddedEvent(
                meshObjectInBase,
                oldRoleTypes,
                addedRoleTypes,
                newRoleTypes,
                otherSideInBase,
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param removedRoleTypes the RoleTypes that were removed
     * @param otherSideInBase the "other side" MeshObject in the baseline
     * @return the MeshObjectRoleRemovedEvent or subclass
     */
    protected MeshObjectRoleRemovedEvent createMeshObjectRoleRemovedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            RoleType []   oldRoleTypes,
            RoleType []   removedRoleTypes,
            RoleType []   newRoleTypes,
            MeshObject    otherSideInBase )
    {
        MeshObjectRoleRemovedEvent ret = new MeshObjectRoleRemovedEvent(
                meshObjectInBase,
                oldRoleTypes,
                removedRoleTypes,
                newRoleTypes,
                otherSideInBase,
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param added the MeshObjects that were added as neighbors
     * @return the MeshObjectDeletedEvent or subclass
     */
    protected MeshObjectEquivalentsAddedEvent createMeshObjectEquivalentsAddedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            MeshObjectSet oldEquivalents,
            MeshObject [] added,
            MeshObjectSet newEquivalents )
    {
        MeshObjectEquivalentsAddedEvent ret = new MeshObjectEquivalentsAddedEvent(
                meshObjectInBase,
                oldEquivalents.getMeshObjects(),
                added,
                newEquivalents.getMeshObjects(),
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Allow subclasses to instantiate a more specific event.
     *
     * @param meshObjectInBase the MeshObject in the baseline
     * @param meshObjectInComparison the MeshObject in the comparison
     * @param removed the Identifiers of the MeshObjects that were removed as equivalents
     * @param remainingEquivalents the remaining equivalents
     * @return the MeshObjectEquivalentsRemovedEvent or subclass
     */
    protected MeshObjectEquivalentsRemovedEvent createMeshObjectEquivalentsRemovedEvent(
            MeshObject    meshObjectInBase,
            MeshObject    meshObjectInComparison,
            MeshObjectSet oldEquivalents,
            MeshObject [] removed,
            MeshObjectSet newEquivalents )
    {
        MeshObjectEquivalentsRemovedEvent ret = new MeshObjectEquivalentsRemovedEvent(
                meshObjectInBase,
                oldEquivalents.getMeshObjects(),
                removed,
                newEquivalents.getMeshObjects(),
                meshObjectInComparison.getTimeUpdated() );

        return ret;
    }

    /**
     * Convert to String representation, for debugging.
     *
     * @return String representation of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theBaselineBase"
                },
                new Object[] {
                    theBaselineBase
                });
    }

    /**
     * The IterableMeshBase against which we are comparing, and to which we apply any changes.
     */
    protected IterableMeshBase theBaselineBase;
}
