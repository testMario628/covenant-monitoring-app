package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification1;
    private Notification notification2;
    private List<Notification> notificationList;

    @BeforeEach
    void setUp() {
        notification1 = new Notification();
        notification1.setId(1L);
        notification1.setTitle("Covenant in scadenza");
        notification1.setMessage("Il covenant DSCR del contratto Finanziamento Progetto Eolico deve essere verificato entro 7 giorni");
        notification1.setType("Warning");
        notification1.setCreatedAt(LocalDateTime.now().minusDays(2));
        notification1.setRead(false);
        notification1.setUserId(1L);

        notification2 = new Notification();
        notification2.setId(2L);
        notification2.setTitle("Covenant non rispettato");
        notification2.setMessage("Il covenant Leverage Ratio del contratto Finanziamento Impianto Solare non è stato rispettato");
        notification2.setType("Alert");
        notification2.setCreatedAt(LocalDateTime.now().minusDays(1));
        notification2.setRead(true);
        notification2.setUserId(1L);

        notificationList = Arrays.asList(notification1, notification2);
    }

    @Test
    void getAllNotifications_shouldReturnAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(notificationList);

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(2, result.size());
        assertEquals("Covenant in scadenza", result.get(0).getTitle());
        assertEquals("Covenant non rispettato", result.get(1).getTitle());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getNotificationById_withValidId_shouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertTrue(result.isPresent());
        assertEquals("Covenant in scadenza", result.get().getTitle());
        assertEquals("Warning", result.get().getType());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_withInvalidId_shouldReturnEmpty() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(99L);

        assertFalse(result.isPresent());
        verify(notificationRepository, times(1)).findById(99L);
    }

    @Test
    void saveNotification_shouldReturnSavedNotification() {
        Notification newNotification = new Notification();
        newNotification.setTitle("Nuova notifica");
        newNotification.setMessage("Messaggio di test");
        newNotification.setType("Info");
        newNotification.setCreatedAt(LocalDateTime.now());
        newNotification.setRead(false);
        newNotification.setUserId(2L);

        Notification savedNotification = new Notification();
        savedNotification.setId(3L);
        savedNotification.setTitle("Nuova notifica");
        savedNotification.setMessage("Messaggio di test");
        savedNotification.setType("Info");
        savedNotification.setCreatedAt(LocalDateTime.now());
        savedNotification.setRead(false);
        savedNotification.setUserId(2L);

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.saveNotification(newNotification);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Nuova notifica", result.getTitle());
        assertEquals("Info", result.getType());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_shouldReturnUpdatedNotification() {
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setTitle("Covenant in scadenza");
        updatedNotification.setMessage("Il covenant DSCR del contratto Finanziamento Progetto Eolico deve essere verificato entro 7 giorni");
        updatedNotification.setType("Warning");
        updatedNotification.setCreatedAt(notification1.getCreatedAt());
        updatedNotification.setRead(true);
        updatedNotification.setUserId(1L);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updatedNotification);

        Notification result = notificationService.markAsRead(1L);

        assertNotNull(result);
        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_withInvalidId_shouldThrowException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(99L));
        verify(notificationRepository, times(1)).findById(99L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_shouldCallRepositoryDelete() {
        doNothing().when(notificationRepository).deleteById(anyLong());

        notificationService.deleteNotification(1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getNotificationsByUserId_shouldReturnFilteredNotifications() {
        when(notificationRepository.findByUserId(1L)).thenReturn(notificationList);

        List<Notification> result = notificationService.getNotificationsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals("Covenant in scadenza", result.get(0).getTitle());
        assertEquals("Covenant non rispettato", result.get(1).getTitle());
        verify(notificationRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getUnreadNotifications_shouldReturnUnreadNotifications() {
        when(notificationRepository.findByReadFalse()).thenReturn(List.of(notification1));

        List<Notification> result = notificationService.getUnreadNotifications();

        assertEquals(1, result.size());
        assertEquals("Covenant in scadenza", result.get(0).getTitle());
        assertFalse(result.get(0).isRead());
        verify(notificationRepository, times(1)).findByReadFalse();
    }

    @Test
    void getNotificationsByType_shouldReturnFilteredNotifications() {
        when(notificationRepository.findByType("Warning")).thenReturn(List.of(notification1));

        List<Notification> result = notificationService.getNotificationsByType("Warning");

        assertEquals(1, result.size());
        assertEquals("Covenant in scadenza", result.get(0).getTitle());
        assertEquals("Warning", result.get(0).getType());
        verify(notificationRepository, times(1)).findByType("Warning");
    }
}
