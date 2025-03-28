package com.covenant.monitoring.integration;

import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testAdmin;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Pulisci il repository prima di ogni test
        userRepository.deleteAll();

        // Crea un utente admin di test
        testAdmin = new User();
        testAdmin.setUsername("admin@example.com");
        testAdmin.setPassword(passwordEncoder.encode("password123"));
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setEnabled(true);
        testAdmin = userRepository.save(testAdmin);

        // Crea un utente normale di test
        testUser = new User();
        testUser.setUsername("user@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Normal");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Pulisci il repository dopo ogni test
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.username == 'admin@example.com')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'user@example.com')]").exists())
                .andExpect(jsonPath("$[*].password").doesNotExist());

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_withValidId_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/users/{id}", testAdmin.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAdmin.getId().intValue())))
                .andExpect(jsonPath("$.username", is("admin@example.com")))
                .andExpect(jsonPath("$.firstName", is("Admin")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldCreateAndReturnUser() throws Exception {
        String newUserJson = "{"
                + "\"username\": \"nuovo@example.com\","
                + "\"password\": \"password123\","
                + "\"firstName\": \"Nuovo\","
                + "\"lastName\": \"Utente\","
                + "\"enabled\": true"
                + "}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("nuovo@example.com")))
                .andExpect(jsonPath("$.firstName", is("Nuovo")))
                .andExpect(jsonPath("$.lastName", is("Utente")))
                .andExpect(jsonPath("$.password").doesNotExist());

        List<User> users = userRepository.findAll();
        assertEquals(3, users.size());
        
        // Verifica che la password sia stata correttamente codificata
        User newUser = userRepository.findByUsername("nuovo@example.com").orElseThrow();
        assertTrue(passwordEncoder.matches("password123", newUser.getPassword()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_withValidId_shouldUpdateAndReturnUser() throws Exception {
        String updatedUserJson = "{"
                + "\"username\": \"admin@example.com\","
                + "\"firstName\": \"Admin\","
                + "\"lastName\": \"User Aggiornato\","
                + "\"enabled\": true"
                + "}";

        mockMvc.perform(put("/users/{id}", testAdmin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAdmin.getId().intValue())))
                .andExpect(jsonPath("$.lastName", is("User Aggiornato")));

        User updatedUser = userRepository.findById(testAdmin.getId()).orElseThrow();
        assertEquals("User Aggiornato", updatedUser.getLastName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_withNewPassword_shouldUpdatePasswordAndReturnUser() throws Exception {
        String updatedUserJson = "{"
                + "\"username\": \"admin@example.com\","
                + "\"password\": \"newpassword\","
                + "\"firstName\": \"Admin\","
                + "\"lastName\": \"User\","
                + "\"enabled\": true"
                + "}";

        mockMvc.perform(put("/users/{id}", testAdmin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAdmin.getId().intValue())));

        User updatedUser = userRepository.findById(testAdmin.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newpassword", updatedUser.getPassword()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_withInvalidId_shouldReturnNotFound() throws Exception {
        String updatedUserJson = "{"
                + "\"username\": \"nonexistent@example.com\","
                + "\"firstName\": \"Non\","
                + "\"lastName\": \"Esistente\","
                + "\"enabled\": true"
                + "}";

        mockMvc.perform(put("/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_withValidId_shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_withUserRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }
}
