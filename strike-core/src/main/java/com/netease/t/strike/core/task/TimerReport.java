package com.netease.t.strike.core.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.netease.t.strike.core.Scheduler;
import com.netease.t.strike.core.collector.Reportors;
import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.ProfileCooldownEvent;
import com.netease.t.strike.core.event.ProfileRunStressEvent;
import com.netease.t.strike.core.event.ProfileSetupEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;

/**
 * run the task when fired the <code>StrikeProfileRunEvent</code>
 * 
 * @author hbliu
 */
@StrikeEventListener
public class TimerReport implements Scheduler {
    @Log
    private Logger logger;
    private ScheduledExecutorService ses;
    private volatile boolean reportState = false;
    long sampleInterval = 0;
    int sampleCount = 0;
    long taskTimeout = 0;
    @Resource
    private Reportors reportors;
    private BlockingQueue<FutureTaskContext> futureQueue = null;

    @HandleEvent(ProfileSetupEvent.class)
    public void initTimeReport(ProfileSetupEvent event) {
        ses = Executors.newScheduledThreadPool(1);
        Profile profile = (Profile) event.getSource();
        sampleInterval = profile.getSampleInterval();
        sampleCount = profile.getSampleCount();
        taskTimeout = profile.getTaskTimeout();
        futureQueue = new ArrayBlockingQueue<FutureTaskContext>(sampleCount);
    }

    @HandleEvent(ProfileRunStressEvent.class)
    public void beginReport(ProfileRunStressEvent event) {
        final Profile profile = (Profile) event.getSource();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                futureQueue.clear();
                reportState = true;
                reportors.roundOn();
                int polledCount = 0;
                long start = System.currentTimeMillis();
                try {
                    // logger.debug("{},{}", polledCount < sampleCount, !profile.getProfileContext().isRunEnd());
                    while ((polledCount < sampleCount) && !profile.getProfileContext().isRunEnd()) {
                        FutureTaskContext futureContext = futureQueue.poll();
                        if (futureContext == null) {
                            Thread.yield();
                            // stopPressRun(profile);//如果没有任务，可能是已经结束
                            continue;
                        }
                        try {
                            futureContext.taskFuture.get(taskTimeout, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            futureContext.context.compareAndSetEnd(0, System.currentTimeMillis());
                            futureContext.context.setTaskPhase(Phase.TIMEOUT);
                            logger.error(futureContext.context.toString() + " run error! InterruptedException:"
                                    + e.getMessage());
                        } catch (TimeoutException e) {
                            futureContext.context.compareAndSetEnd(0, System.currentTimeMillis());
                            futureContext.context.setTaskPhase(Phase.TIMEOUT);
                            logger.error(futureContext.context.toString() + " run error! TimeoutException:"
                                    + e.getMessage());
                        } catch (Exception e) {
                            futureContext.context.compareAndSetEnd(0, System.currentTimeMillis());
                            futureContext.context.setTaskPhase(Phase.FAILED);
                            logger.error(futureContext.context.toString() + " run error!", e);
                        }
                        // logger.debug(polledCount+","+futureContext.context.toString());
                        reportors.collectRound(futureContext.context, profile);
                        // tasks.add(futureContext.context);
                        polledCount++;
                    }
                } finally {
                    long end = System.currentTimeMillis();
                    logger.debug("turn total time(ms):" + (end - start) + ",sample count:" + polledCount);
                    // reportors.summarize(tasks, profile);
                    reportState = false;
                    reportors.roundOff();
                    stopPressRun(profile);
                }
            }
        }, sampleInterval, sampleInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * report the task
     * 
     * @param taskFuture
     * @param context
     */
    public boolean reportTask(Future<Boolean> taskFuture, TaskContext context) {
        if (reportState) {
            reportState = futureQueue.offer(new FutureTaskContext(taskFuture, context));
            // futureQueue.offer(new FutureTaskContext(taskFuture,context));
        }
        return reportState;
    }

    @HandleEvent(ProfileCooldownEvent.class)
    public void gen(ProfileCooldownEvent event) {
        ses.shutdown();
    }

    /**
     * stop the stress run when running time exceeded duration
     * 
     * @param profile
     */
    private void stopPressRun(Profile profile) {
        if (!profile.getProfileContext().isRunEnd()) {
            long currentTime = System.currentTimeMillis();
            long startTime = profile.getProfileContext().getStartTime();
            if ((currentTime - startTime) >= profile.getDuration()) {
                profile.getProfileContext().setRunEnd(true);
            }
        }
    }

    class FutureTaskContext {
        TaskContext context;
        Future<Boolean> taskFuture;

        public FutureTaskContext(Future<Boolean> taskFuture, TaskContext context) {
            this.taskFuture = taskFuture;
            this.context = context;
        }
    }
}
