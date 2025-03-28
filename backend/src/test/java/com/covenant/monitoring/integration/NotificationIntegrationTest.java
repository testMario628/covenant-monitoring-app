package com.covenant.monitoring.integration;

import com.covenant.monitoring.model.Notification;
import com.covenant.monitoring.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        // Pulisci il repository prima di ogni test
        notificationRepository.deleteAll();

        // Crea una notifica di test
        testNotification = new Notification();
        testNotification.setTitle("Covenant in scadenza");
        testNotification.setMessage("Il covenant DSCR del contratto Finanziamento Progetto Eolico deve essere verificato entro 7 giorni");
        testNotification.setType("Warning");
        testNotification.setCreatedAt(LocalDateTime.now().minusDays(2));
        testNotification.setRead(false);
        testNotification.setUserId(1L);
        testNotification = notificationRepository.save(testNotification);
    }

    @AfterEach
    void tearDown() {
        // Pulisci il repository dopo ogni test
        notificationRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllNotifications_shouldReturnAllNotifications() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testNotification.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is("Covenant in scadenza")))
                .andExpect(jsonPath("$[0].type", is("Warning")))
                .andExpect(jsonPath("$[0].read", is(false)));

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationById_withValidId_shouldReturnNotification() throws Exception {
        mockMvc.perform(get("/notifications/{id}", testNotification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testNotification.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Covenant in scadenza")))
                .andExpect(jsonPath("$.type", is("Warning")))
                .andExpect(jsonPath("$.read", is(false)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getNotificationsByUserId_shouldReturnNotifications() throws Exception {
        mockMvc.perform(get("/notifications/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testNotification.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is("Covenant in scadenza")))
                .andExpect(jsonPath("$[0].userId", is(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createNotification_shouldCreateAndReturnNotification() throws Exception {
        String newNotificationJson = "{"
                + "\"title\": \"Nuova notifica\","
                + "\"message\": \"Messaggio di test\","
                + "\"type\": \"Info\","
                + "\"createdAt\": \"" + LocalDateTime.now() + "\","
                + "\"read\": false,"
                + "\"userId\": 2"
                + "}";

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newNotificationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Nuova notifica")))
                .andExpect(jsonPath("$.type", is("Info")))
                .andExpect(jsonPath("$.userId", is(2)));

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getTitle().equals("Nuova notifica")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void markNotificationAsRead_withValidId_shouldUpdateAndReturnNotification() throws Exception {
        mockMvc.perform(put("/notifications/{id}/read", testNotification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testNotification.getId().intValue())))
                .andExpect(jsonPath("$.read", is(true)));

        Notification updatedNotification = notificationRepository.findById(testNotification.getId()).orElseThrow();
        assertTrue(updatedNotification.isRead());
    }

    @Test
    @WithMockUser(roles = "USER")
    void markNotificationAsRead_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/notifications/999/read"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteNotification_withValidId_shouldDeleteNotification() throws Exception {
        mockMvc.perform(delete("/notifications/{id}", testNotification.getId()))
                .andExpect(status().isNoContent());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(0, notifications.size());
    }

    @Test
    void getAllNotifications_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
