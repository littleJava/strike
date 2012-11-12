package com.netease.t.strike.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.google.common.util.concurrent.Atomics;
import com.netease.t.strike.core.task.json.JsonConvertor;

/**
 * Task unit, decoration for user defined task
 * 
 * @author hbliu hbliu
 */
public class TaskContext implements Callable<Boolean> {
    private Task task;
    // private CountDownLatch latch;
    private long begin = 0l;
    private AtomicLong end = new AtomicLong(0);
    private long timeout;
    private AtomicReference<Phase> taskPhase = Atomics.newReference();

    /**
     * @param task user defined task
     * @param latch
     */
    public TaskContext() {
    }

    public TaskContext(Task task, long timeout) {
        this.task = task;
        this.taskPhase.set(Phase.READY);
        this.timeout = timeout;
    }

    public Boolean call() {
        try {
            beforeRun();
            task.execute();
            // latch.countDown();
            afterRun();
            return true;
        } catch (InterruptedException e) {// Task sleep
            afterRun(Phase.TIMEOUT);
            return true;
        } catch (Exception e) {
            afterRun(Phase.FAILED);
            return true;
        }
    }

    private void afterRun() {
        this.end.compareAndSet(0, System.currentTimeMillis());
        if (timeout > 0 && end.get() - begin > timeout) {
            this.taskPhase.compareAndSet(Phase.RUNNING, Phase.TIMEOUT);
        } else {
            this.taskPhase.compareAndSet(Phase.RUNNING, Phase.SUCCESS);
        }
    }

    private void afterRun(Phase phase) {
        this.end.compareAndSet(0, System.currentTimeMillis());
        this.taskPhase.compareAndSet(Phase.RUNNING, phase);
    }

    private void beforeRun() {
        this.begin = System.currentTimeMillis();
        this.taskPhase.set(Phase.RUNNING);
    }

    public long getDuration() {
        return end.longValue() - begin;
    }

    public void setEnd(long end) {
        this.end.set(end);
    }

    public boolean compareAndSetEnd(long expected, long update) {
        return end.compareAndSet(expected, update);
    }

    public boolean compareAndSetPhase(Phase update, Phase... expected) {
        for (Phase phase : expected) {
            if (taskPhase.compareAndSet(phase, update)) {
                return true;
            }
        }
        return false;
    }

//    public boolean compareAndSetPhase(Phase update, Phase expected) {
//        return taskPhase.compareAndSet(expected, update);
//    }

    public void setTaskPhase(Phase phase) {
        taskPhase.set(phase);
    }

    public boolean isCompleted() {
        return Phase.isEnd(this.taskPhase.get());
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Phase getTaskPhase() {
        return taskPhase.get();
    }

    // public String toString() {
    // // return Objects.toStringHelper(this).add("duration", getDuration()).add("end", this.end.longValue())
    // // .add("phase", this.taskPhase).add("task", this.task.toString()).toString();
    // return JsonConvertor.toJsonString(this);
    // }

    public String toFormatString() {
        return Objects.toStringHelper(this).add("duration", getDuration()).add("phase", this.taskPhase).toString();
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public AtomicLong getEnd() {
        return end;
    }

    public void setEnd(AtomicLong end) {
        this.end = end;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setTaskPhase(AtomicReference<Phase> taskPhase) {
        this.taskPhase = taskPhase;
    }

}
