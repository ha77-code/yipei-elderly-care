package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.UpdateUserInfoRequest;
import com.yipei.entity.UserVO;
import com.yipei.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/list")
    public List<UserVO> list(){
        return userService.getUserList();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return ApiResponse.success(userVO);
    }

    @PutMapping("/info")
    public ApiResponse<UserVO> updateInfo(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserInfoRequest request) {
        UserVO userVO = userService.updateUserInfo(userId, request.getNickname(), request.getPhone());
        return ApiResponse.success(userVO);
    }
}
