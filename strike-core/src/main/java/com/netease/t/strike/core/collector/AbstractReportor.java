package com.netease.t.strike.core.collector;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.BeanNameAware;

import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.profile.ReportorConfig;
import com.netease.t.strike.core.task.TaskContext;

public abstract class AbstractReportor implements BeanNameAware {
    private Random random = new SecureRandom();

    /**
     * format the task results
     */
    public abstract void measure(List<TaskContext> tasks, Profile profile);

    /**
     * generate the formatted results
     */
    public abstract void gen(Profile profile);

    protected String reportorName;

    public void setBeanName(String name) {
        this.reportorName = name;
    }

    protected ReportorConfig config;

    /**
     * set reportor configuration
     */
    public void setConfig(ReportorConfig config) {
        this.config = config;
    }

    public boolean[] getDetailedIndex(int size) {

        double detailRatio = this.config.getDetailRatio();
        int detailCount = (int) (detailRatio * 100);

        if (size < 100) {// total task less than 100,
            boolean[] detailed = new boolean[size];
            if (detailCount == 0) {
                return detailed;
            }
            if (detailCount > 0) {
                for (int i = 0; i < size; i++) {
                    detailed[i] = true;
                }
                return detailed;
            }
        }
        
        boolean[] detailed = new boolean[100];
        if (detailCount >= 50) {
            for (int i = 0; i < detailed.length; i++) {
                detailed[i] = true;
            }
            position(detailed, 100 - detailCount, false);
        } else {
            if (detailCount == 0) {
                return detailed;
            }
            position(detailed, detailCount, true);
        }
        return detailed;
    }

    private void position(boolean[] detailed, int count, boolean state) {
        int index = 0;
        while (index < count) {
            int i = random.nextInt(100);
            if (detailed[i] == state) {
                continue;
            }
            detailed[i] = state;
            index++;
        }
    }
}
