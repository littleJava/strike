package com.netease.t.strike.core.profile;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.util.ResourceUtils;

public class ProfileTest  extends Assert{
    @Test
    public void load(){
        Serializer serializer = new Persister();
        try {
            Profile profile = serializer.read(Profile.class ,ResourceUtils.getFile("classpath:com/netease/t/strike/core/profile/profile.xml"));
            assertEquals("com.netease.t.strike.app.sample.SharedValueProvider", profile.getTaskDataProviderImpl());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void ratio(){
        assertTrue(Pattern.matches("^\\d+%$","80%"));
        assertFalse(Pattern.matches("^([0-9]{1,2}|100)%$","180%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","0%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","9%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","80%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","00%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","99%"));
        assertTrue(Pattern.matches("^([0-9]{1,2}|100)%$","100%"));
        assertFalse(Pattern.matches("^([0-9]{1,2}|100)%$","101%"));
    }
}
