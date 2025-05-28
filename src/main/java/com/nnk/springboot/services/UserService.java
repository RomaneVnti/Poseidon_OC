package com.nnk.springboot.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

import com.nnk.springboot.exception.UserExistingException;

/**
 * Service pour la gestion des utilisateurs (création, lecture, mise à jour, suppression).
 */
@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Récupère tous les utilisateurs.
     *
     * @return une liste contenant tous les utilisateurs
     */
    public List<User> getAllUsers() {
        try {
            logger.info("Tentative de récupération de la liste des utilisateurs.");
            List<User> userList = userRepository.findAll();
            logger.info("Liste des utilisateurs récupérée avec succès.");
            return userList;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la liste des utilisateurs.", e);
            throw new RuntimeException("Erreur lors de la récupération de la liste des utilisateurs.", e);
        }
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur correspondant
     */
    public User getUserById(Integer id) {
        try {
            logger.info("Récupération de l'utilisateur avec l'ID : {}", id);
            User user = userRepository.findById(id).orElseThrow();
            logger.info("Utilisateur avec l'ID {} récupéré avec succès.", id);
            return user;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur avec l'ID : {}", id, e);
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur.", e);
        }
    }

    /**
     * Ajoute un nouvel utilisateur après encodage du mot de passe.
     * Vérifie également qu'aucun autre utilisateur n'a déjà le même nom d'utilisateur.
     *
     * @param user l'utilisateur à ajouter
     * @return l'utilisateur ajouté
     * @throws UserExistingException si un utilisateur avec le même nom d'utilisateur existe déjà
     */
    public User addUser(User user) {
        logger.info("Ajout d'un nouvel utilisateur.");

        User userExisting = userRepository.findByUsername(user.getUsername());
        if (userExisting != null) {
            logger.error("L'utilisateur {} existe déjà.", user.getUsername());
            throw new UserExistingException("L'utilisateur existe déjà.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        logger.info("Utilisateur ajouté : {}", newUser);
        return newUser;
    }

    /**
     * Met à jour les informations d’un utilisateur existant.
     *
     * @param id   l'identifiant de l'utilisateur à mettre à jour
     * @param user les nouvelles données utilisateur
     * @return l'utilisateur mis à jour
     * @throws UserExistingException si un autre utilisateur avec le même nom d'utilisateur existe
     */
    public User updateUser(Integer id, User user) {
        logger.info("Mise à jour de l'utilisateur avec l'ID : {}", id);

        User userExisting = userRepository.findByUsername(user.getUsername());
        if (userExisting != null && !userExisting.getId().equals(id)) {
            logger.info("Conflit sur le nom d'utilisateur : {}", user.getUsername());
            throw new UserExistingException("Un utilisateur avec ce nom d'utilisateur existe déjà.");
        }

        String newPassword = bCryptPasswordEncoder.encode(user.getPassword());
        User userToUpdate = getUserById(id);

        userToUpdate.setRole(user.getRole());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setFullname(user.getFullname());
        userToUpdate.setPassword(newPassword);

        User userUpdated = userRepository.save(userToUpdate);
        logger.info("Utilisateur avec l'ID {} mis à jour.", id);
        return userUpdated;
    }

    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur à supprimer
     */
    public void deleteUser(Integer id) {
        logger.info("Suppression de l'utilisateur avec l'ID : {}", id);
        try {
            User userToDelete = getUserById(id);
            userRepository.delete(userToDelete);
            logger.info("Utilisateur avec l'ID {} supprimé.", id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur avec l'ID : {}", id, e);
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur.", e);
        }
    }
}
