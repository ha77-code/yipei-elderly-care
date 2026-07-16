package com.yipei.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** 角色常量，统一校验复用 */
public final class RoleConstants {
    private RoleConstants() {}

    public static final String CUSTOMER  = "CUSTOMER";
    public static final String COMPANION = "COMPANION";
    public static final String ADMIN     = "ADMIN";

    public static final Set<String> ALL = new HashSet<>(
            Arrays.asList(CUSTOMER, COMPANION, ADMIN));

    public static boolean isValid(String role) {
        return role != null && ALL.contains(role);
    }
}
