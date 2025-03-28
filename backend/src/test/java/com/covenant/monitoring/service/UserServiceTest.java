package com.covenant.monitoring.service;

import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("admin@example.com", result.get(0).getUsername());
        assertEquals("user@example.com", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("admin@example.com", result.get().getUsername());
        assertEquals("Mario", result.get().getFirstName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_withInvalidId_shouldReturnEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void saveUser_shouldEncodePasswordAndReturnSavedUser() {
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

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.saveUser(newUser);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("nuovo@example.com", result.getUsername());
        assertEquals("encoded_password", result.getPassword());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateUserDetails() {
        User updatedUser = new User();
        updatedUser.setUsername("admin@example.com");
        updatedUser.setFirstName("Mario");
        updatedUser.setLastName("Rossi Aggiornato");
        updatedUser.setEnabled(true);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin@example.com");
        existingUser.setPassword("encoded_password");
        existingUser.setFirstName("Mario");
        existingUser.setLastName("Rossi");
        existingUser.setEnabled(true);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("admin@example.com");
        savedUser.setPassword("encoded_password");
        savedUser.setFirstName("Mario");
        savedUser.setLastName("Rossi Aggiornato");
        savedUser.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("Rossi Aggiornato", result.getLastName());
        assertEquals("encoded_password", result.getPassword()); // Password should not change
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString()); // Password encoder should not be called
    }

    @Test
    void updateUser_withNewPassword_shouldEncodeAndUpdatePassword() {
        User updatedUser = new User();
        updatedUser.setUsername("admin@example.com");
        updatedUser.setPassword("new_password");
        updatedUser.setFirstName("Mario");
        updatedUser.setLastName("Rossi");
        updatedUser.setEnabled(true);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin@example.com");
        existingUser.setPassword("old_encoded_password");
        existingUser.setFirstName("Mario");
        existingUser.setLastName("Rossi");
        existingUser.setEnabled(true);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("admin@example.com");
        savedUser.setPassword("new_encoded_password");
        savedUser.setFirstName("Mario");
        savedUser.setLastName("Rossi");
        savedUser.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("new_password")).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("new_encoded_password", result.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("new_password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_withInvalidId_shouldThrowException() {
        User updatedUser = new User();
        updatedUser.setUsername("nonexistent@example.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(99L, updatedUser));
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("admin@example.com")).thenReturn(Optional.of(user1));

        Optional<User> result = userService.findByUsername("admin@example.com");

        assertTrue(result.isPresent());
        assertEquals("Mario", result.get().getFirstName());
        assertEquals("Rossi", result.get().getLastName());
        verify(userRepository, times(1)).findByUsername("admin@example.com");
    }

    @Test
    void findByUsername_withInvalidUsername_shouldReturnEmpty() {
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
    }
}
