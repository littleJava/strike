package com.netease.t.strike.core.task.json;

import com.alibaba.fastjson.serializer.PropertyFilter;

public class TaskPropertyFilter implements PropertyFilter{
    private String propertyName;
    public TaskPropertyFilter(String propertyName) {
        this.propertyName = propertyName;
    }
    @Override
    public boolean apply(Object source, String name, Object value) {
        if(propertyName.equals(name)) {
            return false;
        }
        return true;
    }
}
