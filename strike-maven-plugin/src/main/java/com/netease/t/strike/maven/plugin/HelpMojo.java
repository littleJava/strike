package com.netease.t.strike.maven.plugin;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "help", defaultPhase = LifecyclePhase.NONE, threadSafe = true,requiresProject=false, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class HelpMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> builder = new LinkedList<String>();
//        append( sb, "org.codehaus.mojo:exec-maven-plugin:1.2.1", 0 );
//        append( sb, "", 0 );
//
//        append( sb, "Exec Maven Plugin", 0 );
//        append( sb, "A plugin to allow execution of system and Java programs", 1 );
//        append( sb, "", 0 );
        
        builder.add("mvn strike:fire to run stress test");
        builder.add("mvn verify to run stress test");
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "" );
            for (String info : builder) {
                getLog().info( info );
            }
            getLog().info( "" );
            
        }
        
    }

}
