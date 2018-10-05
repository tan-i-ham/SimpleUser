package com.demo.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.demo.model.User;
import com.demo.service.UserService;

@Controller
@SessionAttributes("user")
public class SignUpController {
	@Autowired
	UserService userService;

	/* Home page */
	@GetMapping("/")
	public String index() {
		return "index";
	}

	/* SignUp page */
	@GetMapping("/signup")
	public String toSignUp(Model model) {
		model.addAttribute("user", new User());
		return "sign-up";
	}

	/* new user signup function */
	@PostMapping("/signup")
	public String signUp(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpSession session) {
		System.out.println(">>>>>>>>>> in signup1");
		System.out.println(user.toString());

		// length validation not pass
		if (bindingResult.hasErrors()) {
			return "sign-up";
		}
		/* session */
		session.setAttribute("user", user);

		return "sign-up2";
	}
	
	@GetMapping("/signup2")
	public String redirectfromConfirm(@Valid @ModelAttribute User user) {
		return "sign-up2";
	}
	/* new user signup page 2 function */
	@PostMapping("/signup2")
	public String toSignUp2(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpSession session) {
		System.out.println(">>>>>>>>>> in signup2");
		System.out.println(user.toString());

		// length validation not pass
		if (bindingResult.hasErrors()) {
			return "sign-up2";
		}
//		userService.save(user);
		session.setAttribute("user", user);
		return "temp-confirm";
	}

	/* confirm */
	@PostMapping(value = "/confirm", params = "save")
	public String confirm(@ModelAttribute User user) {
		System.out.println(">>>>>>>>>> in confirm");
		System.out.println(user.toString());

		// when user confirm the data is right,
		// then write into DB
		userService.save(user);
		return "redirect:/show";
	}
	/* confirm click back */
	@PostMapping(value = "/confirm", params = "back-edit")
	public String confirmBackToEdit(@ModelAttribute User user) {
		System.out.println(">>>>>>>>>> BACK TO SIGN UP2 TO EDIT");
		return "redirect:/signup2";
	}

	/* list all the user */
	@GetMapping("/show")
	public String showAll(Model model) {
		// get data from db
		List<User> users = userService.getUserList();
		// set the attribute name
		// let the thymeleaf know which data it is
		model.addAttribute("users", users);
		return "show";
	}

	/* edit part */
	@GetMapping("/{id}/toEdit")
	public String toEdit(Model model, Long id) {
		User user = userService.findUserById(id);
		model.addAttribute("user", user);// the attribute name used in html in thymeleaf
		return "user-edit";
	}

	@PostMapping("/edit")
	public String edit(User user) {
		userService.edit(user);
		return "redirect:/show";
	}

	/* delete a user */
	@GetMapping("/delete")
	public String delete(Long id) {
		userService.delete(id);
		return "redirect:/show";
	}

}
