package com.autocarepro.service.impl;

import com.autocarepro.entity.Notification;
import com.autocarepro.entity.User;
import com.autocarepro.repository.NotificationRepository;
import com.autocarepro.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification notify(User user, String message, String type) {
        Notification n = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .read(false)
                .build();
        return notificationRepository.save(n);
    }

    @Override
    public List<Notification> getForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    public void markAllRead(User user) {
        List<Notification> list = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }
}
