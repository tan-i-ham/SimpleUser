package com.demo.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.model.User;
import com.demo.service.UserService;

@Controller
public class SignUpController {
	@Resource
	UserService userService;

	/* Home page */
	@GetMapping("/")
	public String index() {
		return "index";
	}

	/* SignUp page */
	@GetMapping("/signup")
	public String SignUp(Model model) {
		System.out.println(">>>>>>>>>> in GET mapping method");
		model.addAttribute("user", new User());
		return "sign-up";
	}

	/* new user signup function */
	@PostMapping("/signup")
	public String addUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {
		System.out.println(">>>>>>>>>> in POST mapping method");
		System.out.println(user.toString());

		// length validation not pass
		if (bindingResult.hasErrors()) {
			return "sign-up";
		}
		userService.save(user);

		return "redirect:/show";
	}

	/* list all the user */
	@GetMapping("/show")
	public String showAll(Model model) {
		// get data from db
		List<User> users = userService.getUserList();
		model.addAttribute("users", users);
		return "show";
	}

}
