package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        customUserDetailService = new CustomUserDetailService();
        customUserDetailService.userRepository = userRepository; // injection manuelle
    }

    @Test
    public void testLoadUserByUsername_withExistingUser_shouldReturnUserDetails() {
        // GIVEN
        User user = new User();
        user.setUsername("john");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.findByUsername("john")).thenReturn(user);

        // WHEN
        UserDetails userDetails = customUserDetailService.loadUserByUsername("john");

        // THEN
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.getPassword()).isEqualTo("password123");

        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().toString()).isEqualTo("ROLE_USER");

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    public void testLoadUserByUsername_withNonExistentUser_shouldThrowException() {
        // GIVEN
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        // WHEN - THEN
        assertThatThrownBy(() -> customUserDetailService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé");

        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
