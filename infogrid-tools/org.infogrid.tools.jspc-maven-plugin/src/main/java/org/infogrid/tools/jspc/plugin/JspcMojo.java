//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//            (c) 2016 Indie Computing Corp.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.infogrid.tools.jspc.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jasper.JspC;
import org.apache.jasper.servlet.JspCServletContext;
import org.apache.jasper.servlet.TldScanner;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jetty.util.resource.Resource;

/**
 * This goal will compile jsps for a webapp so that they can be used from InfoGrid.
 * 
 * @goal jspc
 * @phase generate-sources
 * @requiresDependencyResolution compile+runtime
 */
public class JspcMojo extends AbstractMojo
{
    /**
     * JettyJspC
     *
     * Add some extra setters to standard JspC class to help configure it
     * for running in maven.
     * 
     * TODO move all setters on the plugin onto this jspc class instead.
     */
    public static class InfoGridJspC extends JspC
    {
   
        private boolean scanAll;
        
        public void setClassLoader (ClassLoader loader)
        {
            this.loader = loader;
        }
        
       public void setScanAllDirectories (boolean scanAll)
       {
           this.scanAll = scanAll;
       }
       
       public boolean getScanAllDirectories ()
       {
           return this.scanAll;
       }
       

        @Override
        protected TldScanner newTldScanner(JspCServletContext context, boolean namespaceAware, boolean validate, boolean blockExternal)
        {
            if (context != null && context.getAttribute(JarScanner.class.getName()) == null) 
            {
                StandardJarScanner jarScanner = new StandardJarScanner();             
                jarScanner.setScanAllDirectories(getScanAllDirectories());
                context.setAttribute(JarScanner.class.getName(), jarScanner);
            }
                
            return super.newTldScanner(context, namespaceAware, validate, blockExternal);
        }      

    }
    
    
    /**
     * Whether or not to include dependencies on the plugin's classpath with &lt;scope&gt;provided&lt;/scope&gt;
     * Use WITH CAUTION as you may wind up with duplicate jars/classes.
     * 
     * @since jetty-7.6.3
     * @parameter  default-value="false"
     */
    private boolean useProvidedScope;
    
    /**
     * The artifacts for the project.
     * 
     * @since jetty-7.6.3
     * @parameter default-value="${project.artifacts}"
     * @readonly
     */
    private Set projectArtifacts;
    
    
    /**
     * The maven project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    

    /**
     * The artifacts for the plugin itself.
     * 
     * @parameter default-value="${plugin.artifacts}"
     * @readonly
     */
    private List pluginArtifacts;
    
    
    /**
     * The destination directory into which to put the generated jsps.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources/jspc"
     */
    private String generatedSources;

    /**
     * Controls whether or not .java files generated during compilation will be
     * preserved.
     * 
     * @parameter default-value="false"
     */
    private boolean keepSources;


    /**
     * Root directory for all html/jsp etc files
     * 
     * @parameter default-value="${basedir}/src/main/webapp"
     * 
     */
    private String webAppSourceDirectory;
    
   
    
    /**
     * The comma separated list of patterns for file extensions to be processed. By default
     * will include all .jsp and .jspx files.
     * 
     * @parameter default-value="**\/*.jsp, **\/*.jspx"
     */
    private String includes;

    /**
     * The comma separated list of file name patters to exclude from compilation.
     * 
     * @parameter default_value="**\/.svn\/**";
     */
    private String excludes;

    /**
     * The location of the compiled classes for the webapp
     * 
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File classesDirectory;

    /**
     * @parameter default-value=""
     * (This translates to NULL as it turns out, not empty string)
     */
    private String targetPackage;

    /**
     * Source version - if not set defaults to jsp default (currently 1.7)
     * @parameter 
     */
    private String sourceVersion;
    
    
    /**
     * Target version - if not set defaults to jsp default (currently 1.7)
     * @parameter 
     */
    private String targetVersion;
    
    /**
     * 
     * The JspC instance being used to compile the jsps.
     * 
     * @parameter
     */
    private InfoGridJspC jspc;

    /**
     * Whether dirs on the classpath should be scanned as well as jars.
     * True by default. This allows for scanning for tlds of dependent projects that
     * are in the reactor as unassembled jars.
     * 
     * @parameter default-value=true
     */
    private boolean scanAllDirectories;
    

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (getLog().isDebugEnabled()) {
            getLog().info("webAppSourceDirectory=" + webAppSourceDirectory);
            getLog().info("generatedClasses=" + generatedSources);
            getLog().info("keepSources=" + keepSources);
            if (sourceVersion != null) {
                getLog().info("sourceVersion="+sourceVersion);
            }
            if (targetVersion != null) {
                getLog().info("targetVersion="+targetVersion);
            }
        }
        try {
            prepare();
            compile();
            cleanupSrcs();
        } catch (Exception e) {
            throw new MojoExecutionException("Failure processing jsps", e);
        }
    }

    public void compile() throws Exception
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        //set up the classpath of the webapp
        List<URL> webAppUrls = setUpWebAppClassPath();
        
        //set up the classpath of the container (ie jetty and jsp jars)
        Set<URL> pluginJars = getPluginJars();
        Set<URL> providedJars = getProvidedScopeJars(pluginJars);
 

        //Make a classloader so provided jars will be on the classpath
        List<URL> sysUrls = new ArrayList<>();      
        sysUrls.addAll(providedJars);     
        URLClassLoader sysClassLoader = new URLClassLoader((URL[])sysUrls.toArray(new URL[0]), currentClassLoader);
      
        //make a classloader with the webapp classpath
        URLClassLoader webAppClassLoader = new URLClassLoader((URL[]) webAppUrls.toArray(new URL[0]), sysClassLoader);
        StringBuilder webAppClassPath = new StringBuilder();

        for (int i=0; i<webAppUrls.size(); i++) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("webappclassloader contains: " + webAppUrls.get(i));
            }

            webAppClassPath.append(new File(webAppUrls.get(i).toURI()).getCanonicalPath());
            
            if (getLog().isDebugEnabled()) {
                getLog().debug("added to classpath: " + ((URL) webAppUrls.get(i)).getFile());
            }
            if (i+1<webAppUrls.size()) {
                webAppClassPath.append(System.getProperty("path.separator"));
            }
        }
        
        //Interpose a fake classloader as the webapp class loader. This is because the Apache JspC class
        //uses a TldScanner which ignores jars outside of the WEB-INF/lib path on the webapp classloader.
        //It will, however, look at all jars on the parents of the webapp classloader.
        URLClassLoader fakeWebAppClassLoader = new URLClassLoader(new URL[0], webAppClassLoader);
        Thread.currentThread().setContextClassLoader(fakeWebAppClassLoader);
  
        if (jspc == null) {
            jspc = new InfoGridJspC();
        }

        jspc.setUriroot(webAppSourceDirectory);     
        jspc.setOutputDir(generatedSources);
        jspc.setClassLoader(fakeWebAppClassLoader);
        jspc.setScanAllDirectories(scanAllDirectories);
//        jspc.setCompile(true);
        if (targetPackage != null) {
            jspc.setPackage(targetPackage );
        }
        if (sourceVersion != null) {
            jspc.setCompilerSourceVM(sourceVersion);
        }
        if (targetVersion != null) {
            jspc.setCompilerTargetVM(targetVersion);
        }

        // JspC#setExtensions() does not exist, so 
        // always set concrete list of files that will be processed.
        String jspFiles = getJspFiles(webAppSourceDirectory);
       
        try
        {
            if (jspFiles == null | jspFiles.equals("")) {
                getLog().info("No files selected to compile");

            } else {
                getLog().info("Compiling "+jspFiles+" from includes="+includes+" excludes="+excludes);
                jspc.setJspFiles(jspFiles);
                jspc.execute();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private String getJspFiles(
            String webAppSourceDirectory )
        throws
            IOException
    {
        List fileNames =  FileUtils.getFileNames(new File(webAppSourceDirectory),includes, excludes, false);
        return StringUtils.join(fileNames.toArray(new String[0]), ",");
    }

    /**
     * Until Jasper supports the option to generate the srcs in a different dir
     * than the classes, this is the best we can do.
     * 
     * @throws Exception if unable to clean srcs
     */
    public void cleanupSrcs() throws Exception
    {
        // delete the .java files - depending on keepGenerated setting
        if (!keepSources) {
            File generatedClassesDir = new File(generatedSources);

            if(generatedClassesDir.exists() && generatedClassesDir.isDirectory()) {
                delete(generatedClassesDir, (File f) -> f.isDirectory() || f.getName().endsWith(".java"));
            }
        }
    }
    
    static void delete(File dir, FileFilter filter)
    {
        File[] files = dir.listFiles(filter);
        if (files != null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    delete(f, filter);
                } else {
                    f.delete();
                }
            }
        }
    }

    private void prepare() throws Exception
    {
        // For some reason JspC doesn't like it if the dir doesn't
        // already exist and refuses to create the web.xml fragment
        File generatedSourceDirectoryFile = new File(generatedSources);
        if (!generatedSourceDirectoryFile.exists())
            generatedSourceDirectoryFile.mkdirs();
    }

    /**
     * Set up the execution classpath for Jasper.
     * 
     * Put everything in the classesDirectory and all of the dependencies on the
     * classpath.
     * 
     * @returns a list of the urls of the dependencies
     * @throws Exception
     */
    private List<URL> setUpWebAppClassPath() throws Exception
    {
        //add any classes from the webapp
        List<URL> urls = new ArrayList<>();
        String classesDir = classesDirectory.getCanonicalPath();
        classesDir = classesDir + (classesDir.endsWith(File.pathSeparator) ? "" : File.separator);
        urls.add(Resource.toURL(new File(classesDir)));

        if (getLog().isDebugEnabled())
            getLog().debug("Adding to classpath classes dir: " + classesDir);

        //add the dependencies of the webapp (which will form WEB-INF/lib)
        for (Iterator<?> iter = project.getArtifacts().iterator(); iter.hasNext();)
        {
            Artifact artifact = (Artifact)iter.next();

            // Include runtime and compile time libraries
            if (!Artifact.SCOPE_TEST.equals(artifact.getScope()) && !Artifact.SCOPE_PROVIDED.equals(artifact.getScope()))
            {
                String filePath = artifact.getFile().getCanonicalPath();
                if (getLog().isDebugEnabled())
                    getLog().debug("Adding to classpath dependency file: " + filePath);

                urls.add(Resource.toURL(artifact.getFile()));
            }
        }
        return urls;
    }
    
    
    
    /**
     * @return
     * @throws MalformedURLException
     */
    private Set<URL> getPluginJars () throws MalformedURLException
    {
        HashSet<URL> pluginJars = new HashSet<>();
        for (Iterator<?> iter = pluginArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact pluginArtifact = (Artifact) iter.next();
            if ("jar".equalsIgnoreCase(pluginArtifact.getType()))
            {
                if (getLog().isDebugEnabled()) { getLog().debug("Adding plugin artifact "+pluginArtifact);}
                pluginJars.add(pluginArtifact.getFile().toURI().toURL());
            }
        }
        
        return pluginJars;
    }
    
    
    
    /**
     * @param pluginJars
     * @return
     * @throws MalformedURLException
     */
    private Set<URL>  getProvidedScopeJars (Set<URL> pluginJars) throws MalformedURLException
    {
        if (!useProvidedScope)
            return Collections.emptySet();
        
        HashSet<URL> providedJars = new HashSet<>();
        
        for ( Iterator<?> iter = projectArtifacts.iterator(); iter.hasNext(); )
        {                   
            Artifact artifact = (Artifact) iter.next();
            if (Artifact.SCOPE_PROVIDED.equals(artifact.getScope()))
            {
                //test to see if the provided artifact was amongst the plugin artifacts
                URL jar = artifact.getFile().toURI().toURL();
                if (!pluginJars.contains(jar))
                {
                    providedJars.add(jar);
                    if (getLog().isDebugEnabled()) { getLog().debug("Adding provided artifact: "+artifact);}
                }  
                else
                {
                    if (getLog().isDebugEnabled()) { getLog().debug("Skipping provided artifact: "+artifact);}
                }
            }
        }
        return providedJars;
    }
}
