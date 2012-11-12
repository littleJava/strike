package com.netease.t.strike.core;

import javax.annotation.Resource;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.netease.t.strike.core.event.EventBuilder;
import com.netease.t.strike.core.event.EventManager;
import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.ProfileEndEvent;
import com.netease.t.strike.core.event.StrikeEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.profile.ProfileBuilder;

/**
 * run each profile in the single jvm
 * 
 * @author hbliu hbliu
 */
//@Scope("prototype")
@StrikeEventListener
public class ProfileVmRunner {
    @Log
    Logger logger;
    @Resource
    private EventManager eventManager;
    @Resource
    private EventBuilder eventBuilder;

    public static void main(String... args) throws Exception {
        try {
            System.out.println("执行ProfileVmRunner.main(String... args):"+args[0]+","+args[1]);
            ApplicationContext context = new ClassPathXmlApplicationContext("strike-core-spring-applicationContext.xml");
            ProfileVmRunner vmRunner = context.getBean(ProfileVmRunner.class);
            vmRunner.run(args);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }finally{
            System.exit(0);
        }
    }

    /**
     * run the test case
     * 
     * @param args
     */
    public void run(String[] args) {
        // org.springframework.core.io.Resource profileResource = new ClassPathResource(args[0]);
        run(ProfileBuilder.build(args[0],args[1]));
    }

    /**
     * run the test case
     * 
     * @param profile
     */
    public void run(Profile profile) {
        StrikeEvent event = null;
        if (!profile.isDev()) {//close the logger
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory(); 
            System.out.println(loggerFactory);
//            LoggerContext loggerContext = (LoggerContext) loggerFactory;  
//            loggerContext.stop();
        }
        while (profile.isTested()) {
            event = eventBuilder.build(profile);
            logger.debug(profile.getProfileContext().getCurrentPhase() +","+ event.toString());
            eventManager.publishEvent(event);
        }
    }

    @HandleEvent(ProfileEndEvent.class)
    public void stopProfile(ProfileEndEvent event) {
        Profile profile = (Profile) event.getSource();
        profile.setTested();
    }
}
