package com.netease.t.strike.core.collector;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;


public class DocReportorTest extends Assert{
    @Test
    public void gen() {
        try {
//            File file = ResourceUtils.getFile("test-gen.txt");
//            System.getProperties()
            System.out.println(System.getProperty("user.dir"));
            File file = new File("test-gen.txt");
            System.out.println(file.getAbsolutePath());
            file.deleteOnExit();
            assertFalse(file.exists());
            assertTrue(file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        File test = new File(".","test-gen.txt");
//        try {
//            test.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
