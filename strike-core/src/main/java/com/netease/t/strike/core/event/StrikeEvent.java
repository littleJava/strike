package com.netease.t.strike.core.event;

public class StrikeEvent {
    private Object source;

    public StrikeEvent() {
    }

    public StrikeEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
