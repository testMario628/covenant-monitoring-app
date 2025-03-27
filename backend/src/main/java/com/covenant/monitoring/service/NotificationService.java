package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUser(user);
    }

    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByUserAndIsRead(user, false);
    }

    public Long countUnreadNotificationsByUser(User user) {
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification markAsRead(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setIsRead(true);
            return notificationRepository.save(n);
        }
        return null;
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void createNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
}
