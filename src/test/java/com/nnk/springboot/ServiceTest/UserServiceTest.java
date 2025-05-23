package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import exception.UserExistingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFullname("Test User");
        user.setRole("USER");
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void addUser_shouldSaveUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addUser(user);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_shouldThrowException_whenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        assertThrows(UserExistingException.class, () -> userService.addUser(user));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setRole("ADMIN");
        updatedUser.setFullname("Updated User");

        when(userRepository.findByUsername("updateduser")).thenReturn(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1, updatedUser);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUsernameAlreadyExists() {
        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(anotherUser);

        assertThrows(UserExistingException.class, () -> userService.updateUser(1, anotherUser));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).delete(user);
    }
}
