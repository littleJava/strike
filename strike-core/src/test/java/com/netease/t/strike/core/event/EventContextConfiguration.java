package com.netease.t.strike.core.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netease.t.strike.core.impl.SchedulerImpl;
import com.netease.t.strike.core.log.LoggerBeanPostProcessor;
import com.netease.t.strike.core.task.TaskBuilder;
import com.netease.t.strike.core.task.TaskDecorator;
import com.netease.t.strike.core.task.TaskDecoratorImpl;

@Configuration
public class EventContextConfiguration {
    @Bean
    public SchedulerImpl strikeEventListener(){
        return new SchedulerImpl();
    }
    @Bean
    public EventManager eventManager(){
        return new EventManager();
    }
    @Bean
    public TaskBuilder taskBuilder(){
        return new TaskBuilder();
    }
    @Bean
    public TaskDecorator taskDecorator(){
        return new TaskDecoratorImpl();
    }
    @Bean
    public LoggerBeanPostProcessor loggerBeanPostProcessor(){
        return new LoggerBeanPostProcessor();
    }
}
