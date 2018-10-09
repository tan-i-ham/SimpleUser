package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	/**
	 * use GET request method to login page
	 * @return name of the html => "login.html"
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
