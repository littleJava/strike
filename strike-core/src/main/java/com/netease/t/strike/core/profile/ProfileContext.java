package com.netease.t.strike.core.profile;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileContext {
    private long currentRunDuration;
    private double currentSuccessRatio;
    private long startTime = 0;
    AtomicReference<Phase> currentPhase = new AtomicReference<Phase>(Phase.INIT);
    private int roundId = 0;
    private volatile boolean state = true;//没有执行测试
    private volatile boolean runEnd = false;//run event执行未完毕
    
    private long roundDuration = 0;

    public Phase moveToPhase() {
        return moveToPhase(currentPhase.get());
    }
    public Phase moveToPhase(Phase phase) {
        Phase nextPhase = getNextPhase(phase);
        if (currentPhase.compareAndSet(phase, nextPhase))
            return nextPhase;
        return Phase.NO;
    }

    private static Phase getNextPhase(Phase phase) {
        switch (phase) {
        case INIT:
            return Phase.START;
        case START:
            return Phase.SETUP;
        case SETUP:
            return Phase.WARMUP;
        case WARMUP:
            return Phase.RUN;
        case RUN:
            return Phase.COOLDOWN;
        case COOLDOWN:
            return Phase.END;
        case END:
            return Phase.NO;
        default:
//            throw new IllegalStateException("Unhandled enum: " + phase);
            return Phase.NO;
        }
    }
//    public boolean updateProfileState(){
//        return
//    }
    public void setEnd() {
        state = false;
    }
    public boolean isEnd() {
        return state;
    }
    
    
    public long getRoundDuration() {
        return roundDuration;
    }
    public void setRoundDuration(long roundDuration) {
        this.roundDuration = roundDuration;
    }
    public boolean isRunEnd() {
        return runEnd;
    }
    public void setRunEnd(boolean runEnd) {
        this.runEnd = runEnd;
    }
    public Phase getCurrentPhase() {
        return currentPhase.get();
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getStartTime() {
        return startTime;
    }
    public void updateRoundId(){
        this.roundId ++;
    }
    public int getRoundId() {
        return roundId;
    }
    
}
