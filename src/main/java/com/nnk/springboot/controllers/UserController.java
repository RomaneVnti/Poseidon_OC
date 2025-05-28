package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur responsable de la gestion des utilisateurs (ajout, modification, suppression, affichage).
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la liste de tous les utilisateurs.
     *
     * @param model Le modèle pour transmettre les données à la vue
     * @return La vue "user/list"
     */
    @RequestMapping("/user/list")
    public String home(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    /**
     * Affiche le formulaire pour ajouter un nouvel utilisateur.
     *
     * @param user Objet User vide pour le formulaire
     * @return La vue "user/add"
     */
    @GetMapping("/user/add")
    public String addUser(User user) {
        return "user/add";
    }

    /**
     * Valide et enregistre un nouvel utilisateur.
     *
     * @param user   L'utilisateur à valider
     * @param result Le résultat de la validation
     * @param model  Le modèle pour la vue
     * @return Redirection vers la page de login si succès, sinon retour au formulaire
     */
    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            return "redirect:/app/login";
        }
        return "user/add";
    }

    /**
     * Affiche le formulaire de mise à jour d'un utilisateur existant.
     *
     * @param id    L'identifiant de l'utilisateur à mettre à jour
     * @param model Le modèle pour la vue
     * @return La vue "user/update"
     */
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword(""); // Réinitialise le champ password pour éviter de l'afficher chiffré
        model.addAttribute("user", user);
        return "user/update";
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id     L'identifiant de l'utilisateur
     * @param user   Les nouvelles données de l'utilisateur
     * @param result Le résultat de la validation
     * @param model  Le modèle pour la vue
     * @return Redirection vers la liste des utilisateurs
     */
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setId(id);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id    L'identifiant de l'utilisateur à supprimer
     * @param model Le modèle pour la vue
     * @return Redirection vers la liste des utilisateurs
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }
}
