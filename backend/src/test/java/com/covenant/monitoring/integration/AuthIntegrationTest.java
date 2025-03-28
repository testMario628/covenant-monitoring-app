package com.covenant.monitoring.integration;

import com.covenant.monitoring.dto.JwtRequest;
import com.covenant.monitoring.dto.JwtResponse;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.UserRepository;
import com.covenant.monitoring.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User testUser;
    private String rawPassword = "password123";

    @BeforeEach
    void setUp() {
        // Pulisci il repository prima di ogni test
        userRepository.deleteAll();

        // Crea un utente di test
        testUser = new User();
        testUser.setUsername("test@example.com");
        testUser.setPassword(passwordEncoder.encode(rawPassword));
        testUser.setFirstName("Test");
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
    void authenticate_withValidCredentials_shouldReturnToken() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test@example.com");
        request.setPassword(rawPassword);

        MvcResult result = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JwtResponse response = objectMapper.readValue(responseJson, JwtResponse.class);
        
        // Verifica che il token sia valido
        String token = response.getToken();
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Verifica che il token contenga il nome utente corretto
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void authenticate_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticate_withNonExistentUser_shouldReturnUnauthorized() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("nonexistent@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticate_withMissingUsername_shouldReturnBadRequest() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setPassword("password123");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticate_withMissingPassword_shouldReturnBadRequest() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test@example.com");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void accessProtectedEndpoint_withValidToken_shouldSucceed() throws Exception {
        // Prima ottieni un token valido
        JwtRequest request = new JwtRequest();
        request.setUsername("test@example.com");
        request.setPassword(rawPassword);

        MvcResult result = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JwtResponse response = objectMapper.readValue(responseJson, JwtResponse.class);
        String token = response.getToken();

        // Poi usa il token per accedere a un endpoint protetto
        mockMvc.perform(get("/contracts")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedEndpoint_withInvalidToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/contracts")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isUnauthorized());
    }
}
