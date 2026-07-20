package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ChatMessageVO;
import com.yipei.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /** 某订单聊天记录（自动标记已读） */
    @GetMapping("/{orderId}/messages")
    public ApiResponse<List<ChatMessageVO>> messages(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(chatService.listMessages(orderId, userId));
    }

    /** 发送消息 */
    @PostMapping("/{orderId}/send")
    public ApiResponse<ChatMessageVO> send(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        return ApiResponse.success(chatService.send(orderId, userId, body.get("content")));
    }

    /** 某订单聊天是否开启 */
    @GetMapping("/{orderId}/open")
    public ApiResponse<Boolean> open(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(chatService.orderChatOpen(orderId, userId));
    }

    /** 总未读数 */
    @GetMapping("/unread")
    public ApiResponse<Integer> unread(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(chatService.unreadTotal(userId));
    }
}
