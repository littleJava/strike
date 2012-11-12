package com.netease.t.strike.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.netease.t.strike.core.collector.Reportors;
import com.netease.t.strike.core.common.ExceptionUtil;
import com.netease.t.strike.core.common.ProfileInitException;
import com.netease.t.strike.core.event.EventManager;
import com.netease.t.strike.core.event.ProfileCooldownEvent;
import com.netease.t.strike.core.event.ProfileSetupEvent;
import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.task.ParseReader;
import com.netease.t.strike.core.task.TaskContext;
import com.netease.t.strike.core.vm.VmBuilder;

/**
 * 测试启动类
 * 
 * @author hbliu hbliu
 */
@Service
public class StrikeRunner {
    // @Resource
    // private ProfileContext profileContext = null;
    @Resource
    private ProfileManager profileManager = null;

    @Resource
    ProfileVmRunner vmRunner = null;
    
    @Resource
    EventManager eventManager = null;
    
    @Resource
    Reportors reportors = null;

    public static void main(String[] args) {
        try {
            System.out.println("StrikeRunner.main().classpath="+System.getProperty("java.class.path"));
            ApplicationContext context = new ClassPathXmlApplicationContext("strike-core-spring-applicationContext.xml");
            StrikeRunner runner = context.getBean(StrikeRunner.class);
            runner.run(args);
            System.exit(0); // user code may have leave non-daemon threads behind!
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
    }

    public void run(String... args) {
        // Profile profile = new Profile(profileConfig);
        String profileDir = null;
        if (args == null || args.length == 0) {
            profileDir = "classpath:profiles";
        }else {
            profileDir = args[0];
        }
        List<Profile> profiles = profileManager.select(profileDir);
        for (Profile profile : profiles) {
            int loop = profile.getLoop();
            for (int i = 0; i < loop; i++) {
                if (i >= 1) {
                    profile.appendId("" + i);
                }
                if (profile.isDev()) {
                    test(profile);
                } else {
                    runInVm(profile);
                }
                profile.reset();
            }
        }
        return;
    }

    /**
     * @param profilePath profile configuration path
     */
    private void test(Profile profile) {
        try {
            vmRunner.run(profile);
        } catch (Exception e) {
            throw ExceptionUtil.build(e, ProfileInitException.class);
        }
    }

    private void runInVm(Profile profile) {
        try {
            // DebugMeasurer measurer = new DebugMeasurer(debugReps);
            // for (Profile profile : scenarioSelection.select()) {
            // System.out.println("running " + debugReps + " debug reps of " + scenario);
            // vmRunner.run(scenarioSelection, scenario, measurer);
            // }
            // vmRunner.run(new String[]{profilePath});
            eventManager.publishEvent(new ProfileSetupEvent(profile));
            ProcessBuilder builder = VmBuilder.createCommand(profile);
            Process process = builder.start();
            // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            ParseReader reader = new ParseReader(new BufferedReader(new InputStreamReader(process.getInputStream(),
                    "GBK")), profile.getMark(), profile.getThreadMaxCount());
            List<TaskContext> tasks = null;
            while ((tasks = reader.read()).size() > 0) {
//                System.out.println("StrikeRunner.runInVm()-------------------------"+tasks.size());
                reportors.summarizeRound(tasks, profile,reader.getRoundDuration());
            }
            eventManager.publishEvent(new ProfileCooldownEvent(profile));
            reader.close();
        } catch (Exception e) {
            throw ExceptionUtil.build(e, ProfileInitException.class);
        }

    }
}
