package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.User;
import com.covenant.monitoring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin@example.com");
        user1.setPassword("encoded_password");
        user1.setFirstName("Mario");
        user1.setLastName("Rossi");
        user1.setEnabled(true);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user@example.com");
        user2.setPassword("encoded_password");
        user2.setFirstName("Luigi");
        user2.setLastName("Bianchi");
        user2.setEnabled(true);

        userList = Arrays.asList(user1, user2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("admin@example.com")))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("user@example.com")))
                .andExpect(jsonPath("$[1].password").doesNotExist());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_withValidId_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("admin@example.com")))
                .andExpect(jsonPath("$.firstName", is("Mario")))
                .andExpect(jsonPath("$.lastName", is("Rossi")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldReturnCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("nuovo@example.com");
        newUser.setPassword("password123");
        newUser.setFirstName("Nuovo");
        newUser.setLastName("Utente");
        newUser.setEnabled(true);

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setUsername("nuovo@example.com");
        savedUser.setPassword("encoded_password");
        savedUser.setFirstName("Nuovo");
        savedUser.setLastName("Utente");
        savedUser.setEnabled(true);

        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.username", is("nuovo@example.com")))
                .andExpect(jsonPath("$.firstName", is("Nuovo")))
                .andExpect(jsonPath("$.lastName", is("Utente")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_withValidId_shouldReturnUpdatedUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("admin@example.com");
        updatedUser.setFirstName("Mario");
        updatedUser.setLastName("Rossi Aggiornato");
        updatedUser.setEnabled(true);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Rossi Aggiornato")));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_withInvalidId_shouldReturnNotFound() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(99L);
        updatedUser.setUsername("nonexistent@example.com");
        updatedUser.setFirstName("Non");
        updatedUser.setLastName("Esistente");

        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(99L);
        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_withValidId_shouldReturnNoContent() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_withInvalidId_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(99L);
        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_withUserRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    void getAllUsers_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAllUsers();
    }
}
