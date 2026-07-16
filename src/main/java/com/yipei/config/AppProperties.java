package com.yipei.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yipei")
public class AppProperties {

    /** 文件上传目录 */
    private String uploadDir = "uploads";

    /** 平台抽成比例 */
    private double platformFeeRate = 0.1;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public double getPlatformFeeRate() {
        return platformFeeRate;
    }

    public void setPlatformFeeRate(double platformFeeRate) {
        this.platformFeeRate = platformFeeRate;
    }
}
