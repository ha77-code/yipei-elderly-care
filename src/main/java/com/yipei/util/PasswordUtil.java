package com.yipei.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.util.Base64;

/** 密码加密工具：BCrypt（兼容旧 SHA-256 格式校验） */
public final class PasswordUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {}

    /** 加密密码，使用 BCrypt */
    public static String encode(String raw) {
        return ENCODER.encode(raw);
    }

    /** 校验密码，兼容旧 SHA-256 salt:hash 格式 */
    public static boolean matches(String raw, String encoded) {
        if (encoded == null) return false;
        if (encoded.startsWith("$2a$") || encoded.startsWith("$2b$")) {
            return ENCODER.matches(raw, encoded);
        }
        if (encoded.contains(":")) {
            return matchesLegacySha256(raw, encoded);
        }
        return false;
    }

    private static boolean matchesLegacySha256(String raw, String encoded) {
        try {
            String[] parts = encoded.split(":", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(raw.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashed).equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }
}
