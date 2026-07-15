package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.LoginRequest;
import com.yipei.entity.RegisterRequest;
import com.yipei.entity.UpdateUserInfoRequest;
import com.yipei.entity.UserVO;
import com.yipei.exception.ForbiddenException;
import com.yipei.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 登录 */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        return ApiResponse.success(result);
    }

    /** 注册 */
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success();
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/info")
    public ApiResponse<UserVO> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long userId = resolveUserId(authHeader, headerUserId);
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    /** 管理员：获取用户列表 */
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
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Valid @RequestBody UpdateUserInfoRequest request) {
        Long userId = resolveUserId(authHeader, headerUserId);
        return ApiResponse.success(userService.updateUserInfo(userId, request.getNickname(), request.getPhone()));
    }

    private Long resolveUserId(String authHeader, Long headerUserId) {
        Long userId = userService.resolveUserId(authHeader);
        if (userId == null) userId = headerUserId;
        if (userId == null) throw new ForbiddenException("请先登录");
        return userId;
    }
}
