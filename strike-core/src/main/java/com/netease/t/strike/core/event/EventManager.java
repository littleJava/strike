package com.netease.t.strike.core.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netease.t.strike.core.common.StrikeException;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.task.TaskBuilder;

/**
 * 事件处理相关操作工具类
 */
@Service
public class EventManager implements ApplicationContextAware {
    @Log
    private Logger logger;
    private ApplicationContext applicationContext;
    private Multimap<String, CallbackInfo> listeners = ArrayListMultimap.create();
    private ExecutorService eventService = null;

    /**
     * 扫瞄所有bean,进行事件监听
     */
    @PostConstruct
    public void initEventListener() {
        // 取得所有业务类
        // Map<String, StrikeEventListener> beans = applicationContext.getBeansOfType(StrikeEventListener.class);
        Map<String, ? extends Object> beans = applicationContext.getBeansWithAnnotation(StrikeEventListener.class);
        TaskBuilder builder = applicationContext.getBean(TaskBuilder.class);
        Collection<? extends Object> values = beans.values();
        for (Object listener : values) {
            // 注意这里不能使用listener.getClass()方法，因此方法返回的只是SPRING的代理类，此代理类的方法没有注解信息
            // Method[] methods = listener.getClass().getDeclaredMethods();
            // logger.debug("spring class:{},original class:{}", listener.getClass(),
            // ClassUtils.getUserClass(listener));

            // Method method = ClassUtils.getMethod(listener.getClass(), "onEvent",StrikeEvent.class);
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(ClassUtils.getUserClass(listener));
            // 判断方法中是否有指定注解类型的注解
            for (Method method : methods) {

                boolean hasAnnotation = method.isAnnotationPresent(HandleEvent.class);
                if (hasAnnotation) {
                    // 根据注解类型返回方法的指定类型注解
                    HandleEvent annotation = method.getAnnotation(HandleEvent.class);
                    Class<? extends StrikeEvent>[] events = annotation.value();
                    if (events == null || events.length == 0) {// 这里过滤掉没有真正实现事件监听的业务类
                        continue;
                    }
                    for (int i = 0; i < events.length; i++) {
                        try {
                            // 注意这里要用代理类的方法，即listener.getClass().getMethod(method.getName())，不能直接使用method变量，下同
                            listeners.put(events[i].getName(), new CallbackInfo(listener, method));
                            logger.debug("spring class:{},original class:{},event:{},method:{}", new Object[] {
                                    listener.getClass(), ClassUtils.getUserClass(listener), events[i].getName(),method.toGenericString()});
                        } catch (Exception e) {
                            throw new StrikeException("初始化事件监听器时出错：", e);
                        }
                    }
                }
            }
        }
        if (logger.isInfoEnabled()) {
            Set<String> eventNames =  listeners.keySet();
            for (String eventName : eventNames) {
                logger.info("event:{},listener:{}",eventName,listeners.get(eventName).toString());
            }
        }
        eventService = Executors.newCachedThreadPool();
    }

    /**
     * 发布事件
     */
    public void publishEvent(StrikeEvent event) {
        Collection<CallbackInfo> callbacks = listeners.get(event.getClass().getName());
        List<Future> eventResult = new ArrayList<Future>(callbacks.size());
        for (CallbackInfo callback : callbacks) {
            eventResult.add(eventService.submit(new RunnableEvent(callback, event)));
        }
        for (Future result : eventResult) {
            try {
                result.get();
            } catch (Exception e) {
                logger.error("publish event error!" + event.toString(), e);
            }
        }
//        return true;
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @PreDestroy
    public void destroy(){
        eventService.shutdown();
    }
    /**
     * event listener callbacks
     * @author hbliu
     *
     * hbliu
     */
    class RunnableEvent implements Runnable{
        private CallbackInfo callback;
        private StrikeEvent event;
        public RunnableEvent(CallbackInfo callback, StrikeEvent event) {
            this.callback = callback;
            this.event = event;
        }
        @Override
        public void run() {
            ReflectionUtils.invokeMethod(callback.method, callback.obj, event);
        }
        
    }
    class CallbackInfo {
        Object obj;
        Method method;

        CallbackInfo(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
        }
        public String toString(){
            return ClassUtils.getQualifiedMethodName(method).toString();
        }
    }
}
