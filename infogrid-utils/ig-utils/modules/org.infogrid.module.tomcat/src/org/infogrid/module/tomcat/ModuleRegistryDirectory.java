/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.infogrid.module.tomcat;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.infogrid.module.ModuleRegistry;
import org.infogrid.module.SharedSpace;

/**
 *
 * This can't implement SmartFactory because this project cannot depend on org.infogrid.util.
 */
public abstract class ModuleRegistryDirectory
{
    private ModuleRegistryDirectory() {}

    /**
     * Obtain a newly created ModuleRegistry that is private.
     *
     * @param settingsPath
     * @return a newly created ModuleRegistry
     */
    public static ModuleRegistry obtainFor(
            File settingsPath )
        throws
            ModuleRegistryMetaParseException,
            IOException
    {
        return TomcatModuleRegistry.create( settingsPath );
    }

    /**
     * Obtain a ModuleRegistry that is shared with other apps, using a particular share name.
     *
     * @param settingsPath
     * @param sharename name by which this ModuleRegistry is shared
     * @return a newly created or shared ModuleRegistry
     */
    public static synchronized ModuleRegistry obtainFor(
            File   settingsPath,
            String sharename )
        throws
            ModuleRegistryMetaParseException,
            IncompatibleSettingsPathException,
            IOException
    {
        WeakReference<TomcatModuleRegistry> ref = theModuleRegistries.get( sharename );
        TomcatModuleRegistry                ret = null;
        
        if( ref != null ) {
            ret = ref.get();
        }
        if( ret == null ) {
            ret = TomcatModuleRegistry.create( settingsPath );

            theModuleRegistries.put( sharename, new WeakReference<TomcatModuleRegistry>( ret ));
        } else {
            File retSettingsPath = ret.getSettingsPath();
            if(    ( retSettingsPath == null && settingsPath != null )
                || ( retSettingsPath != null && !retSettingsPath.equals( settingsPath )))
            {
                throw new IncompatibleSettingsPathException( settingsPath, sharename, ret );
            }
        }
        return ret;
    }

    /**
     *
     */
    protected static HashMap<String,WeakReference<TomcatModuleRegistry>> theModuleRegistries
            = new HashMap<String,WeakReference<TomcatModuleRegistry>>();

    /**
     * Keep a reference to the SharedSpace.
     */
    public static final Class sharedSpace = SharedSpace.class;
}
