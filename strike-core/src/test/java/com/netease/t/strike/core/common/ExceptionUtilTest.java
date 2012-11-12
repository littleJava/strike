package com.netease.t.strike.core.common;

import org.junit.Assert;
import org.junit.Test;


public class ExceptionUtilTest extends Assert{
    @Test(expected=StrikeException.class)
    public void buildMsg(){
        throw ExceptionUtil.build("test", StrikeException.class);
    }
    @Test(expected=StrikeException.class)
    public void buildStrikeExcepion(){
        throw ExceptionUtil.build(new StrikeException("test"), StrikeException.class);
    }
    @Test(expected=StrikeException.class)
    public void buildExcepion(){
        throw ExceptionUtil.build(new Exception("test"), StrikeException.class);
    }
}
