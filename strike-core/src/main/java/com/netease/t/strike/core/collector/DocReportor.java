package com.netease.t.strike.core.collector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.task.TaskContext;

@Reportor("FILE")
public class DocReportor extends AbstractReportor {
    private String format="{profile:%s,round:%d,Minimum time:%dms,Maximum time:%dms,Average time:%dms,Duration time:%dms,Total:%d,Success:%d,Failed:%d,Timeout:%d,NotRun:%d,TPS:%.2f Success Ratio:%.2f%%}\n";
    private Map<String, List<String>> detailResults = new LinkedHashMap<String, List<String>>();
    private Charset charset = Charset.forName("utf-8");
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
            index++;
            if (index >= detailed.length) {
                index = 0;
            }
        }

        double ratio = new BigDecimal(success * 100).divide(new BigDecimal(tasks.size()), 2, RoundingMode.HALF_UP)
                .doubleValue();
        double tps = new BigDecimal(success * 1000).divide(
                new BigDecimal(profile.getProfileContext().getRoundDuration()), 2, RoundingMode.HALF_UP).doubleValue();

        // System.out.printf(format, minDuration,maxDuration,tasks.size(),success,failed,timeout,ready,ratio);
        String profileResult = String.format(format, profile.getId(),profile.getRoundId(), minDuration, maxDuration,
                totalTime / tasks.size(), profile.getProfileContext().getRoundDuration(), tasks.size(), success,
                failed, timeout, ready, tps, ratio);
        // results.add(String.format(format,
        // minDuration,maxDuration,totalTime/tasks.size(),tasks.size(),success,failed,timeout,ready,ratio));
        detailResults.put(profileResult, detailTasks);
    }

    @Override
    public void gen(Profile profile) {
        System.out.println("DOC.DocReportor:");
        FileOutputStream fos = null;
        try {
            Set<String> roundResults = detailResults.keySet();
            fos = new FileOutputStream(createFile(profile));
            FileChannel fc = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            for (String round : roundResults) {
                writeToFile(fc, buffer, round);
                List<String> tasks = detailResults.get(round);
                System.out.println("DOC.DocReportor:"+round+","+tasks.size());
                for (String task : tasks) {
                    writeToFile(fc, buffer, task);
                }
            }
            fc.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void writeToFile(FileChannel fc, ByteBuffer buffer, String task) throws IOException {
        buffer.put((task + "\r\n").getBytes(charset));
        buffer.flip();
        fc.write(buffer);
        buffer.clear();
    }

    private File createFile(Profile profile) throws IOException {
        String path = System.getProperty("user.dir");
        // File file = new File(path+"/reports/",profile.getId()+".txt");
        File file = new File(path + "/" + profile.getId() + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        System.out.println("DocReportor.createFile():"+file.getAbsolutePath());
        return file;
    }
}
