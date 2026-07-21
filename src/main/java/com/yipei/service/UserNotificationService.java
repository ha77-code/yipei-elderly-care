package com.yipei.service;

import com.yipei.entity.UserNotification;
import com.yipei.mapper.SysUserMapper;
import com.yipei.mapper.UserNotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNotificationService {
    private final UserNotificationMapper mapper;
    private final SysUserMapper sysUserMapper;

    public UserNotificationService(UserNotificationMapper mapper, SysUserMapper sysUserMapper) {
        this.mapper = mapper;
        this.sysUserMapper = sysUserMapper;
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

    /** 给某个角色下所有启用账号发送通知，主要用于管理员待办提醒。 */
    public void sendToRole(String role, String type, String title, String content, Long relatedId) {
        for (Long userId : sysUserMapper.selectActiveIdsByRole(role)) {
            send(userId, type, title, content, relatedId);
        }
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
