package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Contrôleur pour la gestion de la page d'accueil utilisateur et de la redirection administrateur.
 */
@Controller
public class HomeController {

	/**
	 * Affiche la page d'accueil pour les utilisateurs connectés.
	 *
	 * @param model     le modèle utilisé pour transmettre les données à la vue
	 * @param principal l'utilisateur actuellement connecté (peut être null)
	 * @return la vue "home"
	 */
	@RequestMapping("/home")
	public String home(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("username", principal.getName());
		} else {
			model.addAttribute("username", "Guest");
		}
		return "home";
	}

	/**
	 * Redirige les administrateurs vers la liste des BidList.
	 *
	 * @param model le modèle (non utilisé ici, mais injecté pour compatibilité)
	 * @return redirection vers "/bidList/list"
	 */
	@RequestMapping("/admin/home")
	public String adminHome(Model model) {
		return "redirect:/bidList/list";
	}
}
