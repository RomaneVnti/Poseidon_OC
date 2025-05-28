package com.nnk.springboot.ExceptionTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.nnk.springboot.exception.UserExistingException;

/**
 * Tests unitaires pour la classe d'exception {@link UserExistingException}.
 * <p>
 * Cette classe vérifie que l'exception {@code UserExistingException} est bien levée
 * avec le message attendu.
 * </p>
 */
public class UserExistingExceptionTest {

    /**
     * Teste que l'exception {@code UserExistingException} est levée avec le message correct.
     */
    @Test
    public void testExceptionMessage() {
        String message = "User already exists";
        UserExistingException exception = assertThrows(UserExistingException.class, () -> {
            throw new UserExistingException(message);
        });

        assertEquals(message, exception.getMessage());
    }
}
