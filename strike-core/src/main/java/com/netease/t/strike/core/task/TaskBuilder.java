package com.netease.t.strike.core.task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.util.ClassUtils;

import com.netease.t.strike.core.common.ExceptionUtil;
import com.netease.t.strike.core.common.ProfileInitException;
import com.netease.t.strike.core.common.StrikeException;
import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.ProfileEndEvent;
import com.netease.t.strike.core.event.ProfileSetupEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;

/**
 * make the task context
 * 
 * @author hbliu hbliu
 */
@StrikeEventListener
public class TaskBuilder {
    @Log
    private Logger logger;
    @Resource
    private TaskDecorator decorator;
    /**
     * the thread pool to construct the task instance
     */
    private ExecutorService taskInitService = null;

    private DataProvider provider = null;

    private BlockingQueue<Collection<TaskContext>> tasks = new SynchronousQueue<Collection<TaskContext>>();
    private List<TaskContext> warmupTasks = new LinkedList<TaskContext>();

    @HandleEvent({ ProfileSetupEvent.class })
    public void initTask(ProfileSetupEvent event) {
        taskInitService = Executors.newSingleThreadExecutor();
        Profile profile = (Profile) event.getSource();
        int initWorkers = profile.getThreadInitCount();
        String taskDataProviderImpl = profile.getTaskDataProviderImpl();

        String taskImpl = profile.getTaskImpl();
        try {

            initContextParam(profile, taskDataProviderImpl);

            Class<?> taskClz = ClassUtils.resolveClassName(taskImpl, Thread.currentThread().getContextClassLoader());

            warmupTasks = loopInitTask(initWorkers, taskClz, profile.getTaskParamContext(),profile.getTaskTimeout());
            taskInitService.execute(new InitTaskRunnable(profile, taskClz));
        } catch (IllegalArgumentException e) {
            throw new StrikeException(e);
        } catch (InstantiationException e) {
            throw new StrikeException(e);
        } catch (IllegalAccessException e) {
            throw new StrikeException(e);
        }
        // logger.debug(warmupTasks.toString());
    }

    /**
     * init the shared parameters data-provider for the task
     * 
     * @param profile
     * @param taskDataProviderImpl
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void initContextParam(Profile profile, String taskDataProviderImpl) throws InstantiationException,
            IllegalAccessException {
        Class<?> dataclz = ClassUtils.resolveClassName(taskDataProviderImpl, Thread.currentThread()
                .getContextClassLoader());
        provider = (DataProvider) dataclz.newInstance();
        provider.setToContext(profile.getTaskParamContext());
    }

    private List<TaskContext> loopInitTask(int initWorkers, Class<?> taskClz, Map<String, Object> taskParams,long taskTimeout)
            throws InstantiationException, IllegalAccessException {
        List<TaskContext> loopTasks = new LinkedList<TaskContext>();
        for (int i = 0; i < initWorkers; i++) {
            Task task = (Task) taskClz.newInstance();
            TaskContext context = new TaskContext(decorator.decorateInstance(task, taskParams),taskTimeout);
            loopTasks.add(context);
        }
        return loopTasks;
    }

    public List<TaskContext> getWarmupTask() {
        return warmupTasks;
    }

    public Collection<TaskContext> getTasks() {
        try {
            return tasks.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return new LinkedList<TaskContext>();
        }
    }

    /**
     * clear current tasks when profile is ended
     */
    @HandleEvent({ ProfileEndEvent.class })
    public void clearTasks(ProfileEndEvent event) {
        tasks.clear();
        warmupTasks.clear();
        taskInitService.shutdown();
    }

    /**
     * init the task instance and saved in the SynchronizedQueue
     * 
     * @author hbliu hbliu
     */
    private class InitTaskRunnable implements Runnable {
        int initWorkers = 0;
        int increment = 0;
        int maxWorkers = 0;
        private Profile profile;
        private Class<?> taskClz;

        public InitTaskRunnable(Profile profile, Class<?> taskClass) {
            this.initWorkers = profile.getThreadInitCount();
            this.increment = profile.getThreadIncrCount();
            this.maxWorkers = profile.getThreadMaxCount();
            this.profile = profile;
            this.taskClz = taskClass;
        }

        @Override
        public void run() {
            if ("hit".equalsIgnoreCase(profile.getType())) {
                try {
                    if (initWorkers < maxWorkers) {
                        int currentWorkers = initWorkers;
                        do {
                            List<TaskContext> loopTasks = loopInitTask(currentWorkers, taskClz,
                                    profile.getTaskParamContext(),profile.getTaskTimeout());
                            tasks.put(loopTasks);
                            currentWorkers += increment;
                        } while (currentWorkers < maxWorkers);

                        List<TaskContext> loopTasks = loopInitTask(maxWorkers, taskClz, profile.getTaskParamContext(),profile.getTaskTimeout());
                        tasks.put(loopTasks);
                    } else if (initWorkers == maxWorkers) {
                        List<TaskContext> loopTasks = loopInitTask(initWorkers, taskClz, profile.getTaskParamContext(),
                                profile.getTaskTimeout());
                        tasks.put(loopTasks);
                    }
                    tasks.put(new LinkedList<TaskContext>());
                } catch (InstantiationException e) {
                    logger.error("init task error " + taskClz, e);
                    throw ExceptionUtil.build("init task error " + taskClz, e, ProfileInitException.class);
                } catch (IllegalAccessException e) {
                    logger.error("init task error " + taskClz, e);
                    throw ExceptionUtil.build("init task error " + taskClz, e, ProfileInitException.class);
                } catch (InterruptedException e) {
                    logger.error("init task over " + taskClz, e);
                }
            } else {//stress
                try {
                    BlockingQueue<TaskContext> continualTasks = new ArrayBlockingQueue<TaskContext>(initWorkers);
                    boolean first = true;//flag means that only put the pipe into SynchronousQueue once.
                    
                    while (true) {
                        Task task = (Task) taskClz.newInstance();
                        TaskContext context = new TaskContext(decorator.decorateInstance(task,
                                profile.getTaskParamContext()),profile.getTaskTimeout());
                        if(!continualTasks.offer(context,3,TimeUnit.MILLISECONDS)&&first)//task pipe is full,
                            {tasks.put(continualTasks);first = false;}
                    }
                } catch (InstantiationException e) {
                    logger.error("init task error " + taskClz, e);
                    throw ExceptionUtil.build("init task error " + taskClz, e, ProfileInitException.class);
                } catch (IllegalAccessException e) {
                    logger.error("init task error " + taskClz, e);
                    throw ExceptionUtil.build("init task error " + taskClz, e, ProfileInitException.class);
                } catch (InterruptedException e) {
                    logger.error("init task over " + taskClz, e);
                }
            }
        }
    }
}
