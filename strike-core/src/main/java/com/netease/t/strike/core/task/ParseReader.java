package com.netease.t.strike.core.task;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.t.strike.core.collector.Phase;
import com.netease.t.strike.core.task.json.JsonConvertor;

public class ParseReader {
    private static final Logger logger = LoggerFactory.getLogger(ParseReader.class);
    private Phase currentPhase;
    private final BufferedReader reader;
    private final String mark;
    private final int maxCount;//max thread worker
    private ExecutorService collector = Executors.newSingleThreadExecutor();
    private long start = 0;
    private long end = 0;
    private BlockingQueue<List<TaskContext>> reportsQueue = new SynchronousQueue<List<TaskContext>>();
    public ParseReader(BufferedReader bufReader,String stdoutMark,int maxTask){
        this.reader = bufReader;
        this.mark = stdoutMark;
        this.maxCount = maxTask;
        collector.execute(new Runnable() {
            @Override
            public void run() {
                String stdout = null;
                List<TaskContext> tasks = new ArrayList<TaskContext>();
                try {
                    while ((stdout = reader.readLine()) != null) {
                        if (stdout.startsWith(mark)) {
                            String content = stdout.substring(mark.length());
                            if (content.startsWith("{")) {
                                tasks.add(JsonConvertor.parse(content));
                            } else {
                                Phase newPhase = Phase.valueOf(content);
                                if (newPhase == Phase.ROUND_END) {
                                    try {
                                        end = System.currentTimeMillis();
                                        reportsQueue.put(tasks);
                                    } catch (InterruptedException e) {
                                        logger.error("process stdout collect error!",e);
                                    }
                                } else if (newPhase == Phase.ROUND_START) {
                                    tasks = new ArrayList<TaskContext>(maxCount);
                                    start = System.currentTimeMillis();
                                } else if(newPhase == Phase.RUN_END){
                                    reportsQueue.put(new ArrayList<TaskContext>(0));
                                    return;
                                }
                            }
                        } else {
                            logger.debug(stdout);
                            continue;
                        }
                    }
                } catch (Exception e) {
                    logger.error("process stdout collect error!",e);
                }finally {
                }

            }
        });
    }
    public List<TaskContext> read() throws InterruptedException {
            return reportsQueue.take();
    }
    /**
     * get the cost time of every round test
     * @return
     */
    public long getRoundDuration(){
        return end-start;
    }
    private boolean readOver(String phase){
        Phase newPhase = Phase.valueOf(phase);
        switch (currentPhase) {
        case ROUND_START:
            if (newPhase == Phase.ROUND_END) {
                return true;
            }
        case ROUND_END:
            if (newPhase == Phase.ROUND_START || newPhase == Phase.RUN_START) {
                return true;
            }
        case RUN_END:
        default:
            return false;
        }
    }
    private boolean isValidReportPhase(String phase) {
        Phase newPhase = Phase.valueOf(phase);
        switch (currentPhase) {
        case RUN_START:
            if (newPhase == Phase.ROUND_START) {
                return true;
            }
        case ROUND_START:
            if (newPhase == Phase.ROUND_END) {
                return true;
            }
        case ROUND_END:
            if (newPhase == Phase.ROUND_START || newPhase == Phase.RUN_START) {
                return true;
            }
        case RUN_END:
        default:
            return false;
        }
    }
    public void close() {
        try {
            reader.close();
        } catch (Exception e) {
        }
    }
}
