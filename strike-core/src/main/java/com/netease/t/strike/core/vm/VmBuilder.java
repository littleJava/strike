package com.netease.t.strike.core.vm;

import java.io.File;

import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.netease.t.strike.core.profile.Profile;

public class VmBuilder {
    public static ProcessBuilder createCommand(Profile profile) {
        File workingDirectory = new File(System.getProperty("user.dir"));

        String classPath = System.getProperty("java.class.path");
        System.out.println("VmBuilder.createCommand():"+classPath);
        if (classPath == null || classPath.length() == 0) {
          throw new IllegalStateException("java.class.path is undefined in " + System.getProperties());
        }

        ProcessBuilder process = new ProcessBuilder();
        process.directory(workingDirectory);
        process.command().add(profile.getJavaPath());
        String[] args = profile.getJavaArgs().split(" ");
        for (String arg : args) {
            String trim = StringUtils.trimWhitespace(arg);
            if (Strings.isNullOrEmpty(trim)) {
                continue;
            }
            process.command().add(arg);
        }
        process.command().add("-cp");
        process.command().add(classPath);
        process.command().add("com.netease.t.strike.core.ProfileVmRunner");
        process.command().add(profile.getAbsolutePath());
        process.command().add(profile.getId());
        System.out.println("process.command():"+process.command());
//      ImmutableList.Builder<String> vmArgs = ImmutableList.builder();
        
//        return vm.newProcessBuilder(workingDirectory, classPath,
//            vmArgs.build(), InProcessRunner.class.getName(), caliperArgs.build());
        process.redirectErrorStream(true);
        return process;
      }
}
