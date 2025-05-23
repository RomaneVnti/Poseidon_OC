package com.nnk.springboot.ExceptionTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import exception.UserExistingException;

public class UserExistingExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "User already exists";
        UserExistingException exception = assertThrows(UserExistingException.class, () -> {
            throw new UserExistingException(message);
        });

        assertEquals(message, exception.getMessage());
    }
}
