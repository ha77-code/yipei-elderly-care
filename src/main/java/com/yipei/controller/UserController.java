package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.LoginVO;
import com.yipei.entity.UpdateUserInfoRequest;
import com.yipei.entity.UserLoginRequest;
import com.yipei.entity.UserRegisterRequest;
import com.yipei.entity.UserVO;
import com.yipei.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ApiResponse<UserVO> getCurrentUser(@RequestParam("id") Long userId) {
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    /** 获取用户列表 */
    @GetMapping("/list")
    public ApiResponse<List<UserVO>> list() {
        return ApiResponse.success(userService.getUserList());
    }

    /** 获取用户详情 */
    @GetMapping("/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    /** 修改个人信息 */
    @PutMapping("/info")
    public ApiResponse<UserVO> updateInfo(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserInfoRequest request) {
        return ApiResponse.success(userService.updateUserInfo(userId, request.getNickname(), request.getPhone()));
    }
}
