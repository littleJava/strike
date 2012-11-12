package com.netease.t.strike.core.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.netease.t.strike.core.common.StrikeException;

/**
 * 修改task中的属性
 * @author hbliu
 *
 * hbliu
 */
@Component
public class TaskDecoratorImpl implements TaskDecorator{
    @Override
    public Task decorateInstance(Task taskInstance, Map<String, Object> taskParams) {
        Method[] methods = taskInstance.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ParameterContext.class)) {
                String paramKey = method.getAnnotation(ParameterContext.class).value();
                if (Strings.isNullOrEmpty(paramKey)) {
                    continue;
                }else {
                    Object value = taskParams.get(paramKey);
                    try {
                        method.invoke(taskInstance, value);
                    } catch (IllegalArgumentException e) {
                        throw new StrikeException(e);
                    } catch (IllegalAccessException e) {
                        throw new StrikeException(e);
                    } catch (InvocationTargetException e) {
                        throw new StrikeException(e);
                    }
                }
            }
        }
        return taskInstance;
    }
}
