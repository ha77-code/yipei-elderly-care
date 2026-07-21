package com.yipei.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "yipei.jwt")
public class JwtProperties {

    /** JWT签名密钥，至少256位 */
    private String secret = "yipei-default-secret-key-change-in-production-min-256-bits!!";

    /** Token过期时间（毫秒），默认7天 */
    private long expiration = 7 * 24 * 60 * 60 * 1000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}