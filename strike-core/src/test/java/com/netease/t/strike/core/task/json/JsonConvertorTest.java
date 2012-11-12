package com.netease.t.strike.core.task.json;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.netease.t.strike.core.task.Task;
import com.netease.t.strike.core.task.TaskContext;


public class JsonConvertorTest extends Assert{
    @Test
    public void toJson(){
        TaskContext ctx = new TaskContext(Mockito.mock(Task.class),1000);
        String json =  JsonConvertor.toJsonString(ctx);
        System.out.println(json);
        TaskContext task = JsonConvertor.parse(json);
        System.out.println(task.toFormatString());
    }
}
