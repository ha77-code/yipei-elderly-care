package com.yipei.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginVO {
    private String token;
    private long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private String pendingAvatar;
    private Integer avatarAuditStatus;
    private String role;
    private Integer status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
