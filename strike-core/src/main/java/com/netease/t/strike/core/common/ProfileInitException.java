package com.netease.t.strike.core.common;

public class ProfileInitException extends StrikeException{

    public ProfileInitException(Exception e) {
        super(e);
    }
    
    public ProfileInitException(String msg,Exception e) {
        super(msg,e);
    }
    public ProfileInitException(String msg){
        super(msg);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
