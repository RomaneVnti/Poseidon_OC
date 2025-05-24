package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        } else {
            model.addAttribute("username", "Invité");
        }
        model.addAttribute("errorMsg", "Vous n'êtes pas autorisé à accéder à cette page.");
        return "403";  // nom du template Thymeleaf 403.html
    }
}
