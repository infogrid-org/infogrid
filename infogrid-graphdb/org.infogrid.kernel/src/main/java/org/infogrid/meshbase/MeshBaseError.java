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

package org.infogrid.meshbase;

import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * Superclass of errors that can occur in a MeshBase. Concrete subclasses are
 * contained as inner classes.
 */
public abstract class MeshBaseError
    implements
        CanBeDumped
{
    /**
     * Constructor.
     * 
     * @param meshBase the MeshBase in which the error occurred
     * @param meshObject the MeshObject affected by the error
     */
    protected MeshBaseError(
            MeshBase               meshBase,
            ExternalizedMeshObject meshObject )
    {
        theMeshBase   = meshBase;
        theMeshObject = meshObject;
    }
    
    /**
     * Obtain the MeshBase in which the error occurred.
     * 
     * @return the MeshBase
     */
    public MeshBase getMeshBase()
    {
        return theMeshBase;
    }
    
    /**
     * Obtain the affected MeshObject.
     * 
     * @return the MeshObject
     */
    public ExternalizedMeshObject getMeshObject()
    {
        return theMeshObject;
    }

    /**
     * The MeshBase in which the error occurred.
     */
    protected MeshBase theMeshBase;
    
    /**
     * The MeshObject affected by the error.
     */
    protected ExternalizedMeshObject theMeshObject;
    
    /**
     * A type could not be resolved.
     */
    public static abstract class UnresolvableType
        extends
            MeshBaseError
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param typeIdentifier the identifier of the MeshType that could not be resolved
         */
        protected UnresolvableType(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                MeshTypeIdentifier     typeIdentifier )
        {
            super( meshBase, meshObject );
            
            theTypeIdentifier = typeIdentifier;
        }
        
        /**
         * Obtain the identifier of the MeshType that could not be resolved.
         * 
         * @return the identifier
         */
        public MeshTypeIdentifier getMeshTypeIdentifier()
        {
            return theTypeIdentifier;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dump(
                Dumper d )
        {
            d.dump( this,
                    new String[] {
                        "theMeshBase",
                        "theMeshObject",
                        "theTypeIdentifier"
                    },
                    new Object[] {
                        theMeshBase,
                        theMeshObject,
                        theTypeIdentifier
                    });
        }
        
        /**
         * The identifier of the MeshType that could not be resolved.
         */
        protected MeshTypeIdentifier theTypeIdentifier;
    }

    /**
     * An EntityType could not be resolved.
     */
    public static class UnresolvableEntityType
        extends
            UnresolvableType
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param typeIdentifier the identifier of the MeshType that could not be resolved
         */
        public UnresolvableEntityType(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                MeshTypeIdentifier     typeIdentifier )
        {
            super( meshBase, meshObject, typeIdentifier );
        }
    }

    /**
     * A RoleType could not be resolved.
     */
    public static class UnresolvableRoleType
        extends
            UnresolvableType
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param typeIdentifier the identifier of the MeshType that could not be resolved
         */
        public UnresolvableRoleType(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                MeshTypeIdentifier     typeIdentifier )
        {
            super( meshBase, meshObject, typeIdentifier );
        }
    }
            
    /**
     * An PropertyType could not be resolved.
     */
    public static class UnresolvablePropertyType
        extends
            UnresolvableType
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param typeIdentifier the identifier of the MeshType that could not be resolved
         */
        public UnresolvablePropertyType(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                MeshTypeIdentifier     typeIdentifier )
        {
            super( meshBase, meshObject, typeIdentifier );
        }
    }

    /**
     * The value read from disk was in conflict with the DataType of a Property.
     */
    public static class IncompatibleDataType 
        extends
            MeshBaseError
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param propertyType the PropertyType
         * @param propertyValue the PropertyValue
         */
        public IncompatibleDataType(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                PropertyType           propertyType,
                PropertyValue          propertyValue )
        {
            super( meshBase, meshObject );
            
            thePropertyType  = propertyType;
            thePropertyValue = propertyValue;
        }

        /**
         * Obtain the PropertyType whose DataType was in conflict with the PropertyValue.
         * 
         * @return the PropertyType
         */        
        public PropertyType getPropertyType()
        {
            return thePropertyType;
        }

        /**
         * Obtain the PropertyValue that was in conflict with the DataType of the PropertyType.
         * 
         * @return the PropertyValue
         */
        public PropertyValue getPropertyValue()
        {
            return thePropertyValue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dump(
                Dumper d )
        {
            d.dump( this,
                    new String[] {
                        "theMeshBase",
                        "theMeshObject",
                        "thePropertyType",
                        "thePropertyValue"
                    },
                    new Object[] {
                        theMeshBase,
                        theMeshObject,
                        thePropertyType,
                        thePropertyValue
                    });
        }

        /**
         * The PropertyType whose DataType was in conflict with thePropertyValue.
         */
        protected PropertyType thePropertyType;
        
        /**
         * The PropertyValue that was in conflict with the DataType of the PropertyType.
         */
        protected PropertyValue thePropertyValue;
    }    

    /**
     * A null value was given for a Property that is not optional.
     */
    public static class PropertyNotOptional
        extends
            MeshBaseError
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param propertyType the PropertyType
         */
        public PropertyNotOptional(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                PropertyType           propertyType )
        {
            super( meshBase, meshObject );
            
            thePropertyType  = propertyType;
        }

        /**
         * Obtain the PropertyType whose DataType was in conflict with the PropertyValue.
         * 
         * @return the PropertyType
         */        
        public PropertyType getPropertyType()
        {
            return thePropertyType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dump(
                Dumper d )
        {
            d.dump( this,
                    new String[] {
                        "theMeshBase",
                        "theMeshObject",
                        "thePropertyType"
                    },
                    new Object[] {
                        theMeshBase,
                        theMeshObject,
                        thePropertyType
                    });
        }

        /**
         * The PropertyType whose DataType was in conflict with thePropertyValue.
         */
        protected PropertyType thePropertyType;
    }    

    /**
     * Another, uncategorized error occurred.
     */
    public static class OtherError
        extends
            MeshBaseError
    {
        /**
         * Constructor.
         * 
         * @param meshBase the MeshBase in which the error occurred
         * @param meshObject the MeshObject affected by the error
         * @param ex the Exception indicating the error
         */
        public OtherError(
                MeshBase               meshBase,
                ExternalizedMeshObject meshObject,
                Exception              ex )
        {
            super( meshBase, meshObject );
            
            theException = ex;
        }
        
        /**
         * Obtain the Exception.
         * 
         * @return the Exception
         */
        public Exception getException()
        {
            return theException;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void dump(
                Dumper d )
        {
            d.dump( this,
                    new String[] {
                        "theMeshBase",
                        "theMeshObject",
                        "theException"
                    },
                    new Object[] {
                        theMeshBase,
                        theMeshObject,
                        theException
                    });
        }

        /**
         * The Exception.
         */
        protected Exception theException;
    }
}
