package com.netease.t.strike.core.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.netease.t.strike.core.Scheduler;
import com.netease.t.strike.core.event.HandleEvent;
import com.netease.t.strike.core.event.StrikeEvent;
import com.netease.t.strike.core.event.StrikeEventListener;
import com.netease.t.strike.core.event.ProfileSetupEvent;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Phase;
import com.netease.t.strike.core.profile.ProfileContext;

@Service
public class SchedulerImpl implements Scheduler{
    @Log
    private Logger logger;
    private ExecutorService workerService;
    public void addWorkder() {
        // TODO Auto-generated method stub
        
    }

    public void addWorkders(int numToAdd) {
        // TODO Auto-generated method stub
        
    }

    public void removeWorkder() {
        // TODO Auto-generated method stub
        
    }

    public void removeAllWorkers() {
        // TODO Auto-generated method stub
        
    }

    public void stop() {
        // TODO Auto-generated method stub
        
    }

    public void start() {
        // TODO Auto-generated method stub
        
    }

    public void start(String profileName) {
        // TODO Auto-generated method stub
        
    }

    public void start(ProfileContext profileContext) {
        // TODO Auto-generated method stub
        
    }

    public int getNumberOfActiveWorkers() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isRunning() {
        // TODO Auto-generated method stub
        return false;
    }

    public Phase getCurrentPhase() {
        // TODO Auto-generated method stub
        return null;
    }

}
