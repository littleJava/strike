package com.netease.t.strike.core.demo;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.util.ResourceUtils;

import com.netease.t.strike.core.profile.Profile;

public class SimpleXml {
    public static void main(String[] args) {
        Serializer serializer = new Persister();
        try {
            Profile profile = serializer.read(Profile.class ,ResourceUtils.getFile("classpath:com/netease/t/strike/core/profile/profile.xml"));
            System.out.println(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
