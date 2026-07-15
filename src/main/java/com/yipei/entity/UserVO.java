package com.yipei.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private long id;
    private String username;
    private String nickname;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
