package com.netease.t.strike.core.collector;

import org.junit.Assert;
import org.junit.Test;


public class StdReportorTest extends Assert{
    @Test
    public void format(){
        String format = "{%d%%}";
        assertEquals("{5%}", String.format(format, 5));
    }
}
