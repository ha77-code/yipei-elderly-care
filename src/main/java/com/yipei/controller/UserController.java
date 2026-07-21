package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ChangePasswordRequest;
import com.yipei.entity.LoginVO;
import com.yipei.entity.UpdateUserInfoRequest;
import com.yipei.entity.UserLoginRequest;
import com.yipei.entity.UserRegisterRequest;
import com.yipei.entity.UserVO;
import com.yipei.security.SecurityUtils;
import com.yipei.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    /** 登录 */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/info")
    public ApiResponse<UserVO> getCurrentUser() {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    /** 获取用户列表（管理员） */
    @GetMapping("/list")
    public ApiResponse<List<UserVO>> list() {
        SecurityUtils.requireLogin();
        return ApiResponse.success(userService.getUserList());
    }

    /** 获取用户详情（需登录） */
    @GetMapping("/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        SecurityUtils.requireLogin();
        return ApiResponse.success(userService.getUserById(id));
    }

    /** 修改个人信息 */
    @PutMapping("/info")
    public ApiResponse<UserVO> updateInfo(
            @Valid @RequestBody UpdateUserInfoRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(userService.updateUserInfo(
                userId, request.getNickname(), request.getPhone(), request.getAvatar()));
    }

    /** 上传当前用户头像 */
    @PostMapping("/avatar")
    public ApiResponse<UserVO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(userService.uploadAvatar(userId, file));
    }

    /** 管理员修改用户状态 */
    @PutMapping("/{id}/status")
    public ApiResponse<UserVO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long operatorId = SecurityUtils.requireLoginUserId();
        Integer status = (Integer) body.get("status");
        return ApiResponse.success(userService.updateUserStatus(id, status, operatorId));
    }

    /** 修改密码 */
    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success();
    }
}