package com.netease.t.strike.core.task;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.netease.t.strike.core.collector.Reportors;
import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.ProfileRunHitEvent;
import com.netease.t.strike.core.event.ProfileRunStressEvent;
import com.netease.t.strike.core.event.ProfileWarmupEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;

/**
 * run the task when fired the <code>StrikeProfileRunEvent</code>
 * 
 * @author hbliu hbliu
 */
@StrikeEventListener
public class TaskRunner {
    @Resource
    private TaskBuilder builder;
    @Resource
    private Reportors reportors;
    @Resource
    private TimerReport timerReport;
    @Log
    private Logger logger;

    // ThreadPoolExecutor workerService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
    // 60L, TimeUnit.SECONDS,
    // new SynchronousQueue<Runnable>());;

    /**
     * run the profile hit test
     */
    @HandleEvent({ ProfileRunHitEvent.class })
    public void runHit(ProfileRunHitEvent event) {
        Profile profile = (Profile) event.getSource();
        List<TaskContext> tasks = null;
        while ((tasks = (List<TaskContext>) builder.getTasks()).size() > 0) {
            ThreadPoolExecutor workerService = (ThreadPoolExecutor) Executors.newFixedThreadPool(tasks.size());
            try {
                workerService.prestartAllCoreThreads();
                reportors.roundOn();
                List<Future<Boolean>> run = workerService.invokeAll(tasks, profile.getTaskTimeout(),
                        TimeUnit.MILLISECONDS);
                long end = System.currentTimeMillis();
                for (int i = 0; i < run.size(); i++) {
                    Future<Boolean> future = run.get(i);
                    // boolean op = false;
                    TaskContext taskContext = tasks.get(i);
                    if (future.isDone() && future.isCancelled()&&taskContext.compareAndSetEnd(0, end)) {
                        taskContext.compareAndSetPhase(Phase.TIMEOUT,Phase.RUNNING, Phase.READY);
                    }
                    reportors.collectRound(taskContext, profile);
//                    logger.debug(future.isDone() + "-" + future.isCancelled() + "," + taskContext.toString());
                }
                logger.debug("workerService:ExecutingTaskCount=" + workerService.getActiveCount() + ",CompletedTaskCount="
                        + workerService.getCompletedTaskCount() + ", begin next ...");
//                reportors.summarize(tasks, profile);

            } catch (InterruptedException e) {
//                e.printStackTrace();
            } finally {
                reportors.roundOff();
                workerService.shutdownNow();
            }
        }
    }

    /**
     * stress test
     * 
     * @param event
     */
    @HandleEvent({ ProfileRunStressEvent.class })
    public void runStress(ProfileRunStressEvent event) {
        Profile profile = (Profile) event.getSource();
        ThreadPoolExecutor workerService = new ThreadPoolExecutor(profile.getThreadInitCount(),
                profile.getThreadInitCount(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),new DiscardPolicy());
        
//        ThreadPoolExecutor releaseService = new ThreadPoolExecutor(profile.getThreadInitCount(),
//                profile.getThreadInitCount(), 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//        ThreadPoolExecutor releaseService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor releaseService = (ThreadPoolExecutor)Executors.newFixedThreadPool(profile.getThreadInitCount(), new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("Release-service-pool-" + Thread.currentThread().getId());
				return thread;
			}
		});
        workerService.prestartAllCoreThreads();
        releaseService.prestartAllCoreThreads();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(workerService);//decorator

        final Semaphore total = new Semaphore(profile.getThreadInitCount());// the total limit of the task
        BlockingQueue<TaskContext> tasks = null;
        try {
//            final AtomicInteger a = new AtomicInteger(0);
//            final AtomicInteger r = new AtomicInteger(0);
            if ((tasks = (BlockingQueue<TaskContext>) builder.getTasks()).size() > 0) {
                TaskContext taskContext = null;
                profile.getProfileContext().setStartTime(System.currentTimeMillis());
                while ((!profile.getProfileContext().isRunEnd()) && ((taskContext = tasks.take()) != null)) {// consumer/productor
//                    logger.debug("a={},r={}",tasks.size(),total.availablePermits());
//                    logger.debug("a={},r={}",a.get(),r.get());
                    total.acquire();// avoid the exceed task
//                    a.incrementAndGet();
                    ListenableFuture<Boolean> taskFuture = service.submit(taskContext);
//                    if (timerReport.reportTask(taskFuture, taskContext)) {
//                        logger.debug("task size:{}, semaphore total:{}",tasks.size(),total.availablePermits());
//                    }
                    timerReport.reportTask(taskFuture, taskContext);
                    taskFuture.addListener(new Runnable() {
                        @Override
                        public void run() {
                            total.release();
//                            r.incrementAndGet();
                        }
                    }, releaseService);
                    
                }
                // long end = System.currentTimeMillis();
                logger.debug("workerService:ExecutingTaskCount=" + workerService.getActiveCount() + ",CompletedTaskCount="
                        + workerService.getCompletedTaskCount() + ", begin next ...");
                logger.debug("realesService:ExecutingTaskCount=" + releaseService.getActiveCount() + ",CompletedTaskCount="
                        + releaseService.getCompletedTaskCount() );
                // reportors.summarize(tasks);
            }
        } catch (Exception e) {
            logger.error(this.toString()+" execute error! "+workerService.toString(), e);
        } finally {
            workerService.getQueue().clear();
            workerService.shutdown();
            releaseService.shutdown();
        }
    }

    @HandleEvent({ ProfileWarmupEvent.class })
    public void warmup(ProfileWarmupEvent event) {
        Profile profile = (Profile) event.getSource();
        List<TaskContext> tasks = builder.getWarmupTask();
        ThreadPoolExecutor workerService = (ThreadPoolExecutor) Executors.newFixedThreadPool(tasks.size());
        try {
            logger.debug("workerService:ExecutingTaskCount=" + workerService.getActiveCount() + ",ActiveCount="
                    + workerService.getCompletedTaskCount());
            List<Future<Boolean>> warmup = workerService.invokeAll(tasks, profile.getTaskTimeout(),
                    TimeUnit.MILLISECONDS);
            for (Future<Boolean> future : warmup) {
//                if (future.get()) {
//                    
//                }
            }
            logger.debug("workerService:ExecutingTaskCount=" + workerService.getActiveCount() + ",ActiveCount="
                    + workerService.getCompletedTaskCount());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            workerService.shutdown();
        }
    }

    // @HandleEvent({StrikeProfileSetupEvent.class})
    // public void setup(StrikeProfileSetupEvent event) {
    // Profile profile = (Profile)event.getSource();
    // int initWorkers = profile.getInitialNumberThreads();
    // int increment = profile.getInitialNumberThreads();
    // int maxWorkers = profile.getMaxNumberThreads();
    // workerService.setCorePoolSize(initWorkers);
    // int coreSize = workerService.prestartAllCoreThreads();
    // logger.debug("started core threads:{}",coreSize);
    // }

}
