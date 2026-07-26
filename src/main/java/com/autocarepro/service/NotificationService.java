package com.autocarepro.service;

import com.autocarepro.entity.Notification;
import com.autocarepro.entity.User;

import java.util.List;

public interface NotificationService {
    Notification notify(User user, String message, String type);
    List<Notification> getForUser(User user);
    long getUnreadCount(User user);
    void markAllRead(User user);
}
