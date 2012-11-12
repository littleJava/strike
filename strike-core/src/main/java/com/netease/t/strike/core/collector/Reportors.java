package com.netease.t.strike.core.collector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.ProfileCooldownEvent;
import com.netease.t.strike.core.event.ProfileSetupEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.profile.ReportorConfig;
import com.netease.t.strike.core.task.TaskContext;
import com.netease.t.strike.core.task.json.JsonConvertor;

/**
 * test date collectors
 * 
 * @author hbliu hbliu
 */
@StrikeEventListener
public class Reportors implements ApplicationContextAware {
    // public void measure(List<TaskContext> tasks);
    private AtomicBoolean runState = new AtomicBoolean(false);
    private AtomicBoolean roundState = new AtomicBoolean(false);
    private int taskInRound = 0;
    private int taskSuccessInRound = 0;
    private List<AbstractReportor> reportors = null;
    Map<String, AbstractReportor> reportorsMap = null;
    private ApplicationContext applicationContext;
    private List<TaskContext> tasks = null;
    private Profile profile = null;
    private long roundStart = 0;

//    @PostConstruct
//    public void initReportors() {
//        reportorsMap = applicationContext.getBeansOfType(AbstractReportor.class);
//        reportors = new ArrayList<AbstractReportor>(reportorsMap.size());
//    }

    public void summarizeRound(List<TaskContext> tasks, Profile profile, long roundDuration) {
        profile.getProfileContext().setRoundDuration(roundDuration);
        for (AbstractReportor reportor : reportors) {
            reportor.measure(tasks, profile);
        }
    }

    public void collectRound(TaskContext task, Profile profile) {
        if (runState.get()) {
            if (roundState.get()) {
                calcTaskCount(task);
                if (profile.isDev()) {
                    tasks.add(task);
                } else {
                    System.out.println(profile.getMark() + JsonConvertor.toJsonString(task));
                }
            }
        }
    }

    private void calcTaskCount(TaskContext task) {
        taskInRound++;
        switch (task.getTaskPhase()) {
        case SUCCESS:
            taskSuccessInRound++;
        }
    }

    /**
     * update the profile run state after every round over
     */
    private void updateProfileContextRunState() {
        double ratio = new BigDecimal(taskSuccessInRound).divide(new BigDecimal(taskInRound), 4, RoundingMode.HALF_UP)
                .doubleValue();
        if (ratio < profile.getSuccessRatio()) {// fail ratio
            profile.getProfileContext().setRunEnd(true);
        }
    }

    /**
     * init reportors for each profile
     * 
     * @param event
     */
    @HandleEvent({ ProfileSetupEvent.class })
    public void runFire(ProfileSetupEvent event) {
        reportorsMap = applicationContext.getBeansOfType(AbstractReportor.class);
        reportors = new ArrayList<AbstractReportor>(reportorsMap.size());
        runState.compareAndSet(false, true);
        profile = (Profile) event.getSource();
        // List<String> reportorNames = profile.getReportors();
        List<ReportorConfig> configs = profile.getReportorConfig();
        for (ReportorConfig config : configs) {
            AbstractReportor reportor = reportorsMap.get(config.getName());
            if (reportor != null) {
                reportor.setConfig(config);
                reportors.add(reportor);
            }
        }
        tasks = new ArrayList<TaskContext>(profile.getThreadMaxCount());
        System.out.println(profile.getMark() + Phase.RUN_START);
    }

    public void roundOn() {
        if (runState.get()) {
            tasks = new ArrayList<TaskContext>(profile.getThreadMaxCount());
            System.out.println(profile.getMark() + Phase.ROUND_START);
            roundState.set(true);
            roundStart = System.currentTimeMillis();
            profile.updateRoundId();
        }
    }

    public void roundOff() {
        if (runState.get()) {
            if (roundState.compareAndSet(true, false)) {
                System.out.println(profile.getMark() + Phase.ROUND_END);
                if (profile.isDev()) {
                    summarizeRound(tasks, profile, System.currentTimeMillis() - roundStart);
                }
                updateProfileContextRunState();
                taskInRound = 0;
                taskSuccessInRound = 0;
            }
        }
    }

    @HandleEvent({ ProfileCooldownEvent.class })
    public void cooldown(ProfileCooldownEvent event) {
        System.out.println(profile.getMark() + Phase.RUN_END);
        runState.compareAndSet(true, false);
        for (AbstractReportor reportor : reportors) {
            reportor.gen(profile);
        }
        tasks = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
