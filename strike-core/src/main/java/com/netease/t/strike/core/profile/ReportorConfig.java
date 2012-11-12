package com.netease.t.strike.core.profile;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="reportor")
public class ReportorConfig {
    /**
     * reportor name
     */
    @Attribute
    private String name=null;
    @Attribute(name="detail-ratio",required=false)
    private String detailRatioStr = "100%";
    private double detailRatio;
//    private int detailCount;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getDetailRatio() {
        return detailRatio;
    }
    public void setDetailRatio(double detailRatio) {
        this.detailRatio = detailRatio;
    }
    
    
//    public int getDetailCount() {
//        return detailCount;
//    }
//    public void setDetailCount(int detailRatioCount) {
//        this.detailCount = detailRatioCount;
//    }
    public String getDetailRatioStr() {
        return detailRatioStr;
    }
}
