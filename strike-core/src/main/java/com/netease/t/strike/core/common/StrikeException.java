package com.netease.t.strike.core.common;

public class StrikeException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public StrikeException(String msg){
        super(msg);
    }
    public StrikeException(Exception e) {
        super(e);
    }
    public StrikeException(String msg,Exception e) {
        super(msg,e);
    }
}
