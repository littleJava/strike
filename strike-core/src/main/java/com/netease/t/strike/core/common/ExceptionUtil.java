package com.netease.t.strike.core.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class ExceptionUtil {
    // public static StrikeException build(Exception e){
    // if (e instanceof StrikeException) {
    // return (StrikeException)e;
    // }else {
    // return new StrikeException(e);
    // }
    // }
    public static StrikeException build(String msg, Exception e) {
        if (e instanceof StrikeException) {
            return (StrikeException) e;
        } else {
            return new StrikeException(msg, e);
        }
    }

    public static StrikeException build(Exception e, Class<? extends StrikeException> exClz) {
        if (e instanceof StrikeException) {
            return (StrikeException) e;
        } else {
            try {
                Constructor<? extends StrikeException> constructor = exClz.getConstructor(Exception.class);
                return constructor.newInstance(e);
            } catch (NoSuchMethodException ex) {
                return new StrikeException(e.getMessage(),ex);
            } catch (InstantiationException ex) {
                return new StrikeException(e.getMessage(),ex);
            } catch (IllegalAccessException ex) {
                return new StrikeException(e.getMessage(),ex);
            } catch (InvocationTargetException ex) {
                return new StrikeException(e.getMessage(),ex);
            }
        }
    }

    public static StrikeException build(String msg, Class<? extends StrikeException> exClz) {

        try {
            Constructor<? extends StrikeException> constructor = exClz.getConstructor(String.class);
            return constructor.newInstance(msg);
        } catch (NoSuchMethodException ex) {
            return new StrikeException(msg,ex);
        } catch (InstantiationException ex) {
            return new StrikeException(msg,ex);
        } catch (IllegalAccessException ex) {
            return new StrikeException(msg,ex);
        } catch (InvocationTargetException ex) {
            return new StrikeException(msg,ex);
        }
    
    }

    public static StrikeException build(String msg, Exception e, Class<? extends StrikeException> exClz) {
        if (e instanceof StrikeException) {
            return (StrikeException) e;
        } else {
            try {
                Constructor<? extends StrikeException> constructor = exClz
                        .getConstructor(String.class, Exception.class);
                return constructor.newInstance(msg, e);
            } catch (NoSuchMethodException ex) {
                return new StrikeException(msg+"\r\n"+e.getMessage(), ex);
            } catch (InstantiationException ex) {
                return new StrikeException(msg+"\r\n"+e.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                return new StrikeException(msg+"\r\n"+e.getMessage(), ex);
            } catch (InvocationTargetException ex) {
                return new StrikeException(msg+"\r\n"+e.getMessage(), ex);
            }
        }
    }
}
