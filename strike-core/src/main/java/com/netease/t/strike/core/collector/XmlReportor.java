package com.netease.t.strike.core.collector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.task.TaskContext;

@Reportor("XML")
public class XmlReportor extends AbstractReportor{
    @Log
    private Logger logger;
    private String format="{Minimum time:%dms,Maximum time:%dms,Total:%d,Success:%d,Failed:%d,Timeout:%d,NotRun:%d, Success Ratio:%.4f}\n";
    private List<String> results = new LinkedList<String>();
    @Override
    public void measure(List<TaskContext> tasks, Profile profile) {
        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0;
        int success = 0;
        int timeout = 0;
        int failed = 0;
        int ready = 0;
        for (TaskContext task : tasks) {
            minDuration = Math.min(minDuration, task.getDuration());
            maxDuration = Math.max(maxDuration, task.getDuration());
            switch (task.getTaskPhase()) {
            case SUCCESS:
                success++;
                break;
            case TIMEOUT:
                timeout++;
                break;
            case FAILED:
                failed++;
                break;
            case READY:
                ready++;
                break;
            default:
                System.out.println(task.getTaskPhase());
                break;
            }
        }
        
        double ratio = new BigDecimal(success).divide(new BigDecimal(tasks.size()),4,RoundingMode.HALF_UP).doubleValue();
//        System.out.printf(format, minDuration,maxDuration,tasks.size(),success,failed,timeout,ready,ratio);
        results.add(String.format(format, minDuration,maxDuration,tasks.size(),success,failed,timeout,ready,ratio));
    }
    @Override
    public void gen(Profile profile) {
        System.out.println("xml.XmlReportor:");
        for (String summary : results) {
            System.out.println(summary);
        }
    }
}
