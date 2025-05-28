package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Contrôleur pour la gestion des erreurs d'accès, notamment les erreurs 403 (Accès interdit).
 */
@Controller
public class ErrorController {

    /**
     * Gère l'affichage de la page d'erreur 403 (Accès refusé).
     *
     * @param model     le modèle utilisé pour transmettre les données à la vue
     * @param principal l'utilisateur actuellement connecté (peut être null)
     * @return la vue "403" (page d'erreur personnalisée)
     */
    @GetMapping("/403")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        } else {
            model.addAttribute("username", "Invité");
        }

        model.addAttribute("errorMsg", "Vous n'êtes pas autorisé à accéder à cette page.");
        return "403";
    }
}
