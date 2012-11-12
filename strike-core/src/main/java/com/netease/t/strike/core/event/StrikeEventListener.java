package com.netease.t.strike.core.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;




/** 
 * 事件处理接口，实现此接口并且getEventClasses方法的返回结果条数大于0，方可处理对应的事件 
 */  
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface StrikeEventListener {  
    /** 
     * 事件处理的方法 
     */  
//    public void onEvent(StrikeEvent event);
    /** 
     * 业务层顶层接口，自定义的小框架里可以在顶层业务接口中直接继承事件接口，不影响性能 
     * 因为在初始化事件监听器时，已经过滤了没有真正实现接口方法的类，所以不会造成多余的调用 
     */  
//    public Class<? extends StrikeEventListener> getRealClass();
}
