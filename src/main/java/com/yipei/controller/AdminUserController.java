package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.UpdateStatusRequest;
import com.yipei.entity.UserVO;
import com.yipei.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/status")
    public ApiResponse<UserVO> updateStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String operatorRole,
            @Valid @RequestBody UpdateStatusRequest request) {
        UserVO userVO = userService.updateUserStatus(id, request.getStatus(), operatorRole);
        return ApiResponse.success(userVO);
    }
}