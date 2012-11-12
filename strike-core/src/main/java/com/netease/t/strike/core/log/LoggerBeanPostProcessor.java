package com.netease.t.strike.core.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;


/**
 * init the slf4j logger in each spring bean
 * @author bjhbliu
 *
 * hbliu
 */
@Component
public class LoggerBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(),new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
//              if (field.getAnnotation(Log.class) != null) {
                if (field.getType().equals(Logger.class) && field.isAnnotationPresent(Log.class)) {
                    Log logAnnotation = field.getAnnotation(Log.class);
                    String loggerName = logAnnotation.value();
                    Logger logger = null;
                    if (loggerName != null && !loggerName.equals("")) {
                        logger = LoggerFactory.getLogger(loggerName);
                    }else {
                        logger = LoggerFactory.getLogger(bean.getClass());
                    }
                    field.set(bean, logger);
                }
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
