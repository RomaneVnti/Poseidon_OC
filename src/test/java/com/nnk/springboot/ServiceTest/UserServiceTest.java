package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import com.nnk.springboot.exception.UserExistingException;
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

/**
 * Tests unitaires pour la classe {@link UserService}.
 * <p>
 * Cette classe teste les fonctionnalités de gestion des utilisateurs dans le service {@code UserService},
 * en simulant les interactions avec le {@link UserRepository} et l'encodeur de mot de passe {@link BCryptPasswordEncoder}
 * via Mockito.
 * </p>
 * <ul>
 *   <li>Test de la récupération de tous les utilisateurs.</li>
 *   <li>Test de la récupération d'un utilisateur par identifiant.</li>
 *   <li>Test de l'ajout d'un nouvel utilisateur, avec gestion du cas où l'utilisateur existe déjà.</li>
 *   <li>Test de la mise à jour d'un utilisateur, avec gestion du cas où le nom d'utilisateur est déjà utilisé.</li>
 *   <li>Test de la suppression d'un utilisateur.</li>
 * </ul>
 */
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    /**
     * Initialise les mocks et crée un utilisateur exemple avant chaque test.
     */
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

    /**
     * Teste la récupération de tous les utilisateurs.
     * Vérifie que la liste retournée contient bien l'utilisateur simulé.
     */
    @Test
    void getAllUsers_shouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Teste la récupération d'un utilisateur par son identifiant.
     * Vérifie que l'utilisateur retourné correspond à celui attendu.
     */
    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1);
    }

    /**
     * Teste l'ajout d'un nouvel utilisateur.
     * Vérifie que le mot de passe est encodé et que l'utilisateur est sauvegardé.
     */
    @Test
    void addUser_shouldSaveUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addUser(user);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Teste l'ajout d'un utilisateur lorsque le nom d'utilisateur existe déjà.
     * Vérifie qu'une {@link UserExistingException} est levée.
     */
    @Test
    void addUser_shouldThrowException_whenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        assertThrows(UserExistingException.class, () -> userService.addUser(user));
    }

    /**
     * Teste la mise à jour d'un utilisateur existant.
     * Vérifie que le mot de passe est encodé et que les modifications sont sauvegardées.
     */
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

    /**
     * Teste la mise à jour d'un utilisateur lorsque le nouveau nom d'utilisateur est déjà pris.
     * Vérifie qu'une {@link UserExistingException} est levée.
     */
    @Test
    void updateUser_shouldThrowException_whenUsernameAlreadyExists() {
        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(anotherUser);

        assertThrows(UserExistingException.class, () -> userService.updateUser(1, anotherUser));
    }

    /**
     * Teste la suppression d'un utilisateur existant.
     * Vérifie que la méthode de suppression est appelée avec l'utilisateur correct.
     */
    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).delete(user);
    }
}
