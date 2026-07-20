package com.yipei.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserInfoRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 头像地址，可选；一般由头像上传接口返回后带上 */
    private String avatar;
}
