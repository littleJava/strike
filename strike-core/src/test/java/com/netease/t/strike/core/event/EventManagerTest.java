package com.netease.t.strike.core.event;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,classes={EventContextConfiguration.class})
public class EventManagerTest extends Assert{
    @Resource
    EventManager eventManager;
    @Test
    public void eventHandler(){
        assertNotNull(eventManager);
    }
}
