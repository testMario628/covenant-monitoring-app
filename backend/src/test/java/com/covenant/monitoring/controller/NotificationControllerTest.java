package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "USER")
    void getAllNotifications_shouldReturnAllNotifications() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(notificationList);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Covenant in scadenza")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Covenant non rispettato")));

        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationById_withValidId_shouldReturnNotification() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification1));

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Covenant in scadenza")))
                .andExpect(jsonPath("$.type", is("Warning")))
                .andExpect(jsonPath("$.read", is(false)));

        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(notificationService.getNotificationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notifications/99"))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(99L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationsByUserId_shouldReturnNotifications() throws Exception {
        when(notificationService.getNotificationsByUserId(1L)).thenReturn(notificationList);

        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].userId", is(1)));

        verify(notificationService, times(1)).getNotificationsByUserId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createNotification_shouldReturnCreatedNotification() throws Exception {
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

        when(notificationService.saveNotification(any(Notification.class))).thenReturn(savedNotification);

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNotification)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Nuova notifica")))
                .andExpect(jsonPath("$.type", is("Info")))
                .andExpect(jsonPath("$.userId", is(2)));

        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void markNotificationAsRead_withValidId_shouldReturnUpdatedNotification() throws Exception {
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setTitle("Covenant in scadenza");
        updatedNotification.setMessage("Il covenant DSCR del contratto Finanziamento Progetto Eolico deve essere verificato entro 7 giorni");
        updatedNotification.setType("Warning");
        updatedNotification.setCreatedAt(notification1.getCreatedAt());
        updatedNotification.setRead(true);
        updatedNotification.setUserId(1L);

        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification1));
        when(notificationService.markAsRead(1L)).thenReturn(updatedNotification);

        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.read", is(true)));

        verify(notificationService, times(1)).getNotificationById(1L);
        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void markNotificationAsRead_withInvalidId_shouldReturnNotFound() throws Exception {
        when(notificationService.getNotificationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/notifications/99/read"))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(99L);
        verify(notificationService, never()).markAsRead(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNotification_withValidId_shouldReturnNoContent() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification1));
        doNothing().when(notificationService).deleteNotification(anyLong());

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).getNotificationById(1L);
        verify(notificationService, times(1)).deleteNotification(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNotification_withInvalidId_shouldReturnNotFound() throws Exception {
        when(notificationService.getNotificationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/notifications/99"))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(99L);
        verify(notificationService, never()).deleteNotification(anyLong());
    }

    @Test
    void getAllNotifications_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isUnauthorized());

        verify(notificationService, never()).getAllNotifications();
    }
}
