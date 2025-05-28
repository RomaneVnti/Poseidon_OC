package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link CustomUserDetailService}.
 * <p>
 * Ces tests vérifient le comportement de la méthode {@code loadUserByUsername}
 * en simulant les interactions avec le dépôt {@link UserRepository}.
 * </p>
 * <ul>
 *     <li>Chargement d'un utilisateur existant : vérifie que les détails de l'utilisateur sont correctement retournés avec les rôles associés.</li>
 *     <li>Chargement d'un utilisateur inexistant : vérifie que l'exception {@link UsernameNotFoundException} est levée avec le bon message.</li>
 * </ul>
 */
public class CustomUserDetailServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailService customUserDetailService;

    /**
     * Initialise les mocks et injecte le dépôt {@code userRepository} dans
     * le service {@code customUserDetailService} avant chaque test.
     *
     * @throws Exception en cas d'erreur de réflexion lors de l'injection du mock
     */
    @BeforeEach
    public void setup() throws Exception {
        userRepository = mock(UserRepository.class);
        customUserDetailService = new CustomUserDetailService();

        Field userRepoField = CustomUserDetailService.class.getDeclaredField("userRepository");
        userRepoField.setAccessible(true);
        userRepoField.set(customUserDetailService, userRepository);
    }

    /**
     * Teste le chargement d'un utilisateur existant par nom d'utilisateur.
     * <p>
     * Vérifie que {@code loadUserByUsername} retourne un objet {@link UserDetails}
     * non null avec le bon nom d'utilisateur, mot de passe et rôle.
     * </p>
     */
    @Test
    public void testLoadUserByUsername_withExistingUser_shouldReturnUserDetails() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.findByUsername("john")).thenReturn(user);

        UserDetails userDetails = customUserDetailService.loadUserByUsername("john");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.getPassword()).isEqualTo("password123");

        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().toString()).isEqualTo("ROLE_USER");

        verify(userRepository, times(1)).findByUsername("john");
    }

    /**
     * Teste le comportement lorsque l'utilisateur recherché n'existe pas.
     * <p>
     * Vérifie que {@code loadUserByUsername} lance une {@link UsernameNotFoundException}
     * avec un message contenant "Utilisateur non trouvé".
     * </p>
     */
    @Test
    public void testLoadUserByUsername_withNonExistentUser_shouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThatThrownBy(() -> customUserDetailService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé");

        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
