package com.yipei.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** 密码加密工具：SHA-256 + 随机盐 */
public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /** 加密密码，返回 salt:hash 格式 */
    public static String encode(String raw) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String hash = hash(raw, salt);
        return saltStr + ":" + hash;
    }

    /** 校验密码 */
    public static boolean matches(String raw, String encoded) {
        if (encoded == null || !encoded.contains(":")) return false;
        String[] parts = encoded.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        return hash(raw, salt).equals(parts[1]);
    }

    private static String hash(String raw, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(raw.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
}
