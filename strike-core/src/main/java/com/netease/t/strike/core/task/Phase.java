package com.netease.t.strike.core.task;

public enum Phase {
    READY, RUNNING, SUCCESS, TIMEOUT, FAILED;
    
    public static boolean isEnd(Phase phase){
        switch (phase) {
        case SUCCESS:
        case TIMEOUT:
        case FAILED:
            return true;

        default:
            return false;
        }
    }
}
