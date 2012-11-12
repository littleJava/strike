package com.netease.t.strike.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * plugin for strike
 */

@Mojo(name = "fire", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
//@Execute(goal = "fire", phase = LifecyclePhase.TEST)
public class StrikeMojo extends AbstractMojo {
    
    /**
     * profile path
     */
    @Parameter(defaultValue = "profiles", required = true)
    protected String profileDirectory;

    /**
     * java command path
     */
    @Parameter(defaultValue = "java", required = true)
    protected String executable;
    
    @Parameter(defaultValue = "-showversion", required = true)
    protected String jvmArgs;
    /**
     * the cool bar to go
     * 
     * @since 1.0
     */
    @Parameter(defaultValue = "com.netease.t.strike.core.StrikeRunner")
    protected String mainClass;

    /**
     * The classpath elements of the project being tested.
     */
    @Parameter(defaultValue = "${project.testClasspathElements}", readonly = true, required = true)
    protected List<String> additionalClasspathElements;

    /**
     * The classpath elements of the project being runned.
     */
    @Parameter(defaultValue = "${project.runtimeClasspathElements}", readonly = true, required = true)
    private List<String> classpathElements;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Location of the build directory.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;

    /**
     * Location of the test build directory.
     */
    @Parameter(defaultValue = "${project.build.testOutputDirectory}", required = true)
    private File buildDirectory;

    /**
     * Plexus compiler manager.
     */
    // @Component
    // protected CompilerManager compilerManager;

    /**
     *
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!project.getBuild().getOutputDirectory().equals(classesDirectory.getAbsolutePath())) {
            classpathElements.remove(project.getBuild().getOutputDirectory());
            classpathElements.add(classesDirectory.getAbsolutePath());
        }
        if (!project.getBuild().getTestOutputDirectory().equals(buildDirectory.getAbsolutePath())) {
            additionalClasspathElements.remove(project.getBuild().getTestOutputDirectory());
            additionalClasspathElements.add(buildDirectory.getAbsolutePath());
        }
//        System.out.println("classpathElements");
//        System.out.println(classpathElements);
//        System.out.println("additionalClasspathElements");
//        System.out.println(additionalClasspathElements);
//        System.out.println("java.class.path");
//        System.out.println(System.getProperty("java.class.path"));
//        try {
//            System.out.println(project.getTestClasspathElements());
//            System.out.println(project.getRuntimeClasspathElements());
//        } catch (Exception e) {
//        }
         Commandline cl = new Commandline();
         cl.setExecutable(executable);
         cl.createArg().setValue(jvmArgs);
         cl.createArg().setValue("-classpath");
         cl.createArg().setValue(StringUtils.join(classpathElements.iterator(), File.pathSeparator));
         cl.createArg().setValue(mainClass);
         cl.createArg().setValue(profileDirectory);
         System.out.println(cl.toString());
         StreamConsumer streamConsumer = new DefaultConsumer();
         try {
            int returnCode = CommandLineUtils.executeCommandLine(cl, streamConsumer, streamConsumer);
            System.out.println(returnCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
