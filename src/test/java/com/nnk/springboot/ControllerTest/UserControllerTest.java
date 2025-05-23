package com.nnk.springboot.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testHome_ShouldReturnUserListView() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testAddUser_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/user/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"));
    }

    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.validate(new User(), bindingResult, model);

        assert(view.equals("user/add"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testValidate_WithoutErrors_ShouldRedirectAndEncodePassword() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User()));

        User user = new User();
        user.setPassword("plainPassword");

        String view = userController.validate(user, bindingResult, model);

        assert(view.equals("redirect:/user/list"));
        // Password should be encoded (not equals to plain)
        assert(!user.getPassword().equals("plainPassword"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testShowUpdateForm_ShouldReturnUpdateView() throws Exception {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedPassword");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(model().attributeExists("user"));

        // Password must be cleared in controller
        assert(user.getPassword().isEmpty());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testShowUpdateForm_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        try {
            userController.showUpdateForm(999, model);
            assert false; // should not reach here
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Invalid user Id");
        }
    }

    @Test
    public void testUpdateUser_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.updateUser(1, new User(), bindingResult, model);

        assert(view.equals("user/update"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_WithoutErrors_ShouldRedirectAndEncodePassword() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User()));

        User user = new User();
        user.setPassword("plainPassword");

        String view = userController.updateUser(1, user, bindingResult, model);

        assert(view.equals("redirect:/user/list"));
        // Id should be set to 1
        assert(user.getId() == 1);
        // Password should be encoded
        assert(!user.getPassword().equals("plainPassword"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteUser_ShouldRedirect() throws Exception {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User()));

        mockMvc.perform(get("/user/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userRepository, times(1)).delete(user);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        try {
            userController.deleteUser(999, model);
            assert false; // should not reach here
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Invalid user Id");
        }
    }
}
