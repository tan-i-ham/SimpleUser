package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	@GetMapping(value = { "/", "/login" })
	public String login() {
		return "login2";
	}
}
