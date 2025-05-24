package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HomeController
{
	@RequestMapping("/home")
	public String home(Model model, Principal principal)
	{
		if (principal != null) {
			model.addAttribute("username", principal.getName());
		} else {
			model.addAttribute("username", "Guest");
		}
		return "home";
	}

	@RequestMapping("/admin/home")
	public String adminHome(Model model)
	{
		return "redirect:/bidList/list";
	}


}