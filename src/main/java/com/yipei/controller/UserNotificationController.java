package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.UserNotification;
import com.yipei.service.UserNotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class UserNotificationController {
    private final UserNotificationService service;

    public UserNotificationController(UserNotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<UserNotification>> list(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(service.list(userId));
    }

    @GetMapping("/unread")
    public ApiResponse<Integer> unread(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(service.unread(userId));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        service.markRead(id, userId);
        return ApiResponse.success();
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> readAll(@RequestHeader("X-User-Id") Long userId) {
        service.markAllRead(userId);
        return ApiResponse.success();
    }
}
