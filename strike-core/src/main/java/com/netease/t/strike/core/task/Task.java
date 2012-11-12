package com.netease.t.strike.core.task;

public interface Task {
    /**
     * Main method called by a component to execute this <code>Task</code>.  This should ensure that
     * counters of failures/successes are kept up to date.
     *
     * @throws Throwable The re-thrown exception thrown by the task
     */
    public void execute() throws Exception;
}
