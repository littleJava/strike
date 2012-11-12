package com.netease.t.strike.core.task;

import java.util.Map;

/**
 * decorate the task after task instanced
 * @author hbliu
 *
 * hbliu
 */
public interface TaskDecorator {
    public Task decorateInstance(Task taskInstance, Map<String, Object> taskParams);
}
