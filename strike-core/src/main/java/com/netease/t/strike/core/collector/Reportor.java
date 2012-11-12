package com.netease.t.strike.core.collector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * the reportor mark for data collection
 * @author hbliu
 *
 * hbliu
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface Reportor {
    String value();
}
