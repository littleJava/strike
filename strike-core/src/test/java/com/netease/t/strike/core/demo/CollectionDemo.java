package com.netease.t.strike.core.demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;

public class CollectionDemo extends Assert{
    @Test
    public void queue(){
        LinkedList<String> list = new LinkedList<String>();
        list.add("3");list.add("4");
        Queue<String> queue = (Queue<String>) list;
        queue.poll();
        System.out.println(queue);
    }
}
