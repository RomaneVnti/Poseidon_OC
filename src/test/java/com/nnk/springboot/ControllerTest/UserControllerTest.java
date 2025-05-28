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

/**
 * Tests unitaires pour la classe {@link UserController}.
 * <p>
 * Cette classe utilise Mockito pour simuler les dépendances telles que {@link UserRepository},
 * {@link Model} et {@link BindingResult}. Elle vérifie le comportement des méthodes du contrôleur,
 * incluant l'affichage de la liste des utilisateurs, l'ajout, la validation, la mise à jour et la suppression.
 * Les tests utilisent {@link MockMvc} de Spring pour simuler des requêtes HTTP et valider les réponses et vues.
 * </p>
 */
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

    /**
     * Initialise les mocks et configure MockMvc avant chaque test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    /**
     * Teste que la requête GET /user/list renvoie un statut OK,
     * affiche la vue "user/list" et contient l'attribut "users" dans le modèle.
     */
    @Test
    public void testHome_ShouldReturnUserListView() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));

        verify(userRepository, times(1)).findAll();
    }

    /**
     * Teste que la requête GET /user/add renvoie un statut OK
     * et affiche la vue "user/add".
     */
    @Test
    public void testAddUser_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/user/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"));
    }

    /**
     * Teste que si des erreurs de validation sont présentes,
     * la méthode validate() renvoie la vue "user/add"
     * et ne sauvegarde pas l'utilisateur.
     */
    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.validate(new User(), bindingResult, model);

        assert(view.equals("user/add"));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Teste que si la validation passe,
     * la méthode validate() sauvegarde l'utilisateur,
     * encode son mot de passe,
     * et redirige vers la page de connexion.
     */
    @Test
    public void testValidate_WithoutErrors_ShouldRedirectAndEncodePassword() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User user = new User();
        user.setPassword("plainPassword");

        String view = userController.validate(user, bindingResult, model);

        assert(view.equals("redirect:/app/login"));
        assert(!user.getPassword().equals("plainPassword"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Teste que la requête GET /user/update/{id} affiche la vue "user/update"
     * avec l'attribut utilisateur dans le modèle,
     * et que le mot de passe de l'utilisateur est vidé pour des raisons de sécurité.
     */
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

        assert(user.getPassword().isEmpty());

        verify(userRepository, times(1)).findById(1);
    }

    /**
     * Teste que la méthode showUpdateForm() lève une IllegalArgumentException
     * lorsqu'un identifiant utilisateur invalide est fourni.
     */
    @Test
    public void testShowUpdateForm_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        try {
            userController.showUpdateForm(999, model);
            assert false; // Ne devrait pas atteindre ce point
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Invalid user Id");
        }
    }

    /**
     * Teste que si des erreurs de validation sont présentes,
     * la méthode updateUser() renvoie la vue "user/update"
     * et ne sauvegarde pas l'utilisateur.
     */
    @Test
    public void testUpdateUser_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.updateUser(1, new User(), bindingResult, model);

        assert(view.equals("user/update"));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Teste que si la validation passe,
     * la méthode updateUser() sauvegarde l'utilisateur avec le mot de passe encodé,
     * assigne l'identifiant utilisateur,
     * et redirige vers la liste des utilisateurs.
     */
    @Test
    public void testUpdateUser_WithoutErrors_ShouldRedirectAndEncodePassword() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User()));

        User user = new User();
        user.setPassword("plainPassword");

        String view = userController.updateUser(1, user, bindingResult, model);

        assert(view.equals("redirect:/user/list"));
        assert(user.getId() == 1);
        assert(!user.getPassword().equals("plainPassword"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Teste que la requête GET /user/delete/{id} supprime l'utilisateur
     * et redirige vers la liste des utilisateurs.
     */
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

    /**
     * Teste que la méthode deleteUser() lève une IllegalArgumentException
     * lorsqu'un identifiant utilisateur invalide est fourni.
     */
    @Test
    public void testDeleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        try {
            userController.deleteUser(999, model);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Invalid user Id");
        }
    }
}
