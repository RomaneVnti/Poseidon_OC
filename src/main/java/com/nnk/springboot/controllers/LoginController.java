package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Contrôleur gérant l'authentification des utilisateurs et les pages associées.
 */
@Controller
@RequestMapping("app")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la page de login personnalisée.
     *
     * @return la vue "login"
     */
    @GetMapping("login")
    public String login() {
        return "login";
    }

    /**
     * Affiche la liste des utilisateurs (accessible uniquement aux utilisateurs authentifiés).
     *
     * @return la vue "user/list" contenant tous les utilisateurs
     */
    @GetMapping("secure/article-details")
    public ModelAndView getAllUserArticles() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userRepository.findAll());
        mav.setViewName("user/list");
        return mav;
    }

    /**
     * Gère l'affichage d'une page d'erreur personnalisée 403 (accès refusé).
     *
     * @return la vue "403" avec un message d'erreur
     */
    @GetMapping("error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        String errorMessage = "You are not authorized for the requested data.";
        mav.addObject("errorMsg", errorMessage);
        mav.setViewName("403");
        return mav;
    }
}
