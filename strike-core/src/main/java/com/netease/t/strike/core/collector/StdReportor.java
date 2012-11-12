package com.netease.t.strike.core.collector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.task.TaskContext;

@Reportor("STDOUT")
public class StdReportor extends AbstractReportor{
    private String format="{profile:%s,round:%d,Minimum time:%dms,Maximum time:%dms,Average time:%dms,Duration time:%dms,Total:%d,Success:%d,Failed:%d,Timeout:%d,NotRun:%d,TPS:%.2f Success Ratio:%.2f%%}\n";
    private Map<String, List<String>> detailResults = new LinkedHashMap<String, List<String>>();
    @Override
    public void measure(List<TaskContext> tasks, Profile profile) {
        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0;
        int success = 0;
        int timeout = 0;
        int failed = 0;
        int ready = 0;
        long totalTime = 0;
        boolean[] detailed = getDetailedIndex(tasks.size());
        int index = 0;
        List<String> detailTasks = new ArrayList<String>(tasks.size());
        for (TaskContext task : tasks) {
            minDuration = Math.min(minDuration, task.getDuration());
            maxDuration = Math.max(maxDuration, task.getDuration());
            totalTime += task.getDuration();
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
            if (detailed[index]) {
                detailTasks.add(task.toFormatString());
            }
            index ++;
            if (index >= detailed.length) {
                index = 0;
            }
        }
        
        double ratio = new BigDecimal(success*100).divide(new BigDecimal(tasks.size()),2,RoundingMode.HALF_UP).doubleValue();
        double tps = new BigDecimal(success*1000).divide(new BigDecimal(profile.getProfileContext().getRoundDuration()),2,RoundingMode.HALF_UP).doubleValue();
        
        String profileResult = String.format(format, profile.getId(),profile.getRoundId(),minDuration,maxDuration,totalTime/tasks.size(),profile.getProfileContext().getRoundDuration(),tasks.size(),success,failed,timeout,ready,tps,ratio);
//        System.out.println("StdReportor.measure():"+profileResult+",taskSize="+detailTasks.size());
        detailResults.put(profileResult, detailTasks);
    }
    @Override
    public void gen(Profile profile) {
        System.out.println("STDOUT.StdReportor:");
        Set<String> roundResults = detailResults.keySet();
        for (String round : roundResults) {
            System.out.println(round);
            List<String> tasks = detailResults.get(round);
            for (String task : tasks) {
                System.out.println(task);
            }
        }
        System.out.println();
    }
}
