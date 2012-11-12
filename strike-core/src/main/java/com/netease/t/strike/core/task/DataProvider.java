package com.netease.t.strike.core.task;

import java.util.Map;
/**
 * set test data to the context for current profile test when init the profile
 * @author hbliu
 *
 * hbliu
 */
public interface DataProvider {
    public void setToContext(Map<String, Object> context);
}
