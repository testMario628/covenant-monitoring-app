package com.covenant.monitoring.controller;

import com.covenant.monitoring.dto.JwtRequest;
import com.covenant.monitoring.dto.JwtResponse;
import com.covenant.monitoring.security.JwtTokenUtil;
import com.covenant.monitoring.security.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequest validRequest;
    private JwtRequest invalidRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        validRequest = new JwtRequest();
        validRequest.setUsername("admin@example.com");
        validRequest.setPassword("password");

        invalidRequest = new JwtRequest();
        invalidRequest.setUsername("invalid@example.com");
        invalidRequest.setPassword("wrongpassword");

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin@example.com")
                .password("encoded_password")
                .authorities("ROLE_ADMIN")
                .build();
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnToken() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(validRequest.getUsername(), validRequest.getPassword()));
        when(userDetailsService.loadUserByUsername(validRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("valid_jwt_token");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("valid_jwt_token")));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(validRequest.getUsername());
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void authenticate_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticate_withNonExistentUser_shouldReturnUnauthorized() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(invalidRequest.getUsername(), invalidRequest.getPassword()));
        when(userDetailsService.loadUserByUsername(invalidRequest.getUsername()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(invalidRequest.getUsername());
        verify(jwtTokenUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticate_withMissingUsername_shouldReturnBadRequest() throws Exception {
        JwtRequest requestWithoutUsername = new JwtRequest();
        requestWithoutUsername.setPassword("password");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithoutUsername)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_withMissingPassword_shouldReturnBadRequest() throws Exception {
        JwtRequest requestWithoutPassword = new JwtRequest();
        requestWithoutPassword.setUsername("admin@example.com");

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithoutPassword)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
