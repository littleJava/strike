package com.netease.t.strike.core.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class TimeUnitTest extends Assert{
    @Test
    public void second2mills() throws Exception {
        assertEquals(3000, TimeUnit.SECONDS.toMillis(3));
    }
}
