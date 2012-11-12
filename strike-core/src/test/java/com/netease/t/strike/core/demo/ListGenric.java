package com.netease.t.strike.core.demo;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ListGenric extends Assert{
    @Test
    public void addInter(){
        List<ParentInter> list = new LinkedList<ParentInter>();
        assertTrue(list.add(new Impl1()));
        assertTrue(list.add(new Impl2()));
        assertEquals(Impl1.class, list.get(0).getClass());
        assertEquals(Impl2.class, list.get(1).getClass());
    }
}

interface ParentInter{}
class Impl1 implements ParentInter{}
class Impl2 implements ParentInter{}