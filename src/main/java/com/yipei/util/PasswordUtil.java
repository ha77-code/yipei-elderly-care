package com.yipei.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** 密码加密工具：SHA-256 + 随机 salt */
public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /** 加密密码，生成 base64(salt):base64(sha256(salt+raw)) 格式 */
    public static String encode(String raw) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash(raw, salt);
    }

    /** 校验密码 */
    public static boolean matches(String raw, String encoded) {
        if (encoded == null || !encoded.contains(":")) return false;
        try {
            String[] parts = encoded.split(":", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            return hash(raw, salt).equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }

    private static String hash(String raw, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(raw.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
