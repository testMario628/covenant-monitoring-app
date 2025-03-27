package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.service.NotificationService;
import com.covenant.monitoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getCurrentUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            List<Notification> notifications = notificationService.getNotificationsByUser(user.get());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUser(user.get());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/count-unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            Long count = notificationService.countUnreadNotificationsByUser(user.get());
            return new ResponseEntity<>(count, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}/mark-as-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            if (notification.isPresent() && notification.get().getUser().getId().equals(user.get().getId())) {
                Notification updatedNotification = notificationService.markAsRead(id);
                return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            if (notification.isPresent() && notification.get().getUser().getId().equals(user.get().getId())) {
                notificationService.deleteNotification(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
