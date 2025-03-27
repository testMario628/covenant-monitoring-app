package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.User;
import com.covenant.monitoring.model.Role;
import com.covenant.monitoring.service.UserService;
import com.covenant.monitoring.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (userService.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            user.setId(id);
            User updatedUser = userService.saveUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Role> role = roleService.getRoleById(roleId);
        
        if (user.isPresent() && role.isPresent()) {
            User u = user.get();
            u.getRoles().add(role.get());
            User updatedUser = userService.saveUser(u);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Role> role = roleService.getRoleById(roleId);
        
        if (user.isPresent() && role.isPresent()) {
            User u = user.get();
            u.getRoles().remove(role.get());
            User updatedUser = userService.saveUser(u);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
