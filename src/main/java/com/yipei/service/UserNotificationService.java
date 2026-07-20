package com.yipei.service;

import com.yipei.entity.UserNotification;
import com.yipei.mapper.UserNotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNotificationService {
    private final UserNotificationMapper mapper;

    public UserNotificationService(UserNotificationMapper mapper) {
        this.mapper = mapper;
    }

    public void send(Long userId, String type, String title, String content, Long relatedId) {
        if (userId == null) return;
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        mapper.insert(notification);
    }

    public List<UserNotification> list(Long userId) {
        return mapper.selectByUser(userId);
    }

    public int unread(Long userId) {
        return mapper.countUnread(userId);
    }

    public void markRead(Long id, Long userId) {
        mapper.markRead(id, userId);
    }

    public void markAllRead(Long userId) {
        mapper.markAllRead(userId);
    }
}
