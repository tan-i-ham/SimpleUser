package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.demo.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	/**
	 * use GET request method to login page
	 * 
	 * @return name of the html => "login.html"
	 */
	@GetMapping("/login")
	public String login() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {

			/* The user is logged in :) */
			return "redirect:/";
		}
		return "login2";
	}
}
