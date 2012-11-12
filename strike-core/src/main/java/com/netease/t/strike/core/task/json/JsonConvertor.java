package com.netease.t.strike.core.task.json;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.netease.t.strike.core.task.Phase;
import com.netease.t.strike.core.task.TaskContext;

public class JsonConvertor {

    public static String toJsonString(TaskContext task) {
        SerializeWriter out = new SerializeWriter();
        try {
            JSONSerializer serializer = new JSONSerializer(out);
//            serializer.config(SerializerFeature.WriteClassName, true);
            
            List<PropertyFilter> propertyFilters = serializer.getPropertyFilters();
            propertyFilters.add(taskFilter);
            propertyFilters.add(completedFilter);
            propertyFilters.add(durationFilter);
            serializer.write(task);
            return out.toString();
        } finally {
            out.close();
        }
    }
    public static TaskContext parse(String json){
        JSONObject object = JSON.parseObject(json);
//        return JSON.parseObject(json, TaskContext.class);
        TaskContext task = new TaskContext();
        task.setBegin(object.getLongValue("begin"));
        task.setEnd(object.getLongValue("end"));
        task.setTimeout(object.getLongValue("timeout"));
        task.setTaskPhase(Phase.valueOf(object.getString("taskPhase")));
        return task;
    }
    static final TaskPropertyFilter taskFilter = new TaskPropertyFilter("task");
    static final TaskPropertyFilter completedFilter = new TaskPropertyFilter("completed");
    static final TaskPropertyFilter durationFilter = new TaskPropertyFilter("duration");
}
