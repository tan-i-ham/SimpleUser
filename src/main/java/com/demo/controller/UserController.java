package com.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;

import com.demo.model.User;
import com.demo.service.UserService;

@Controller
@SessionAttributes("user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	/**
	 * Home page
	 * 
	 * @return name of html page => "index.html"
	 */
	@GetMapping("/")
	public String home(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		String returnPage = "";
		if (currentUser == null) {
			returnPage = "index";
		} else {
			User user = (User) userService.findUserByUsername(currentUser.getUsername());
			model.addAttribute("currentUser", user);
			if (user.getProgrammingLanguage() == null) {
				returnPage = "index";
			} else {
				String[] languages = user.getProgrammingLanguage().split(",");
				model.addAttribute("languages", languages);
				returnPage = "index";
			}
		}

		return returnPage;
	}

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
			log.info("The user is logged in.");
			return "redirect:/";
		}
		return "login";
	}

	/**
	 * SignUp page
	 * 
	 * @param model
	 * @return name of html page => "sign-up.html"
	 */
	@GetMapping("/signup")
	public String toSignUp(Model model) {
		System.out.println("in GET /signup");

		model.addAttribute("user", new User());
		return "sign-up";
	}

	@PostMapping("/signup")
	public String redirectSignUp(@ModelAttribute User user, HttpSession session, Model model) {
		System.out.println("in POST /signup");
		System.out.println(user.toString());

		model.addAttribute("inuse", "in used username");
		// session
		session.setAttribute("user", user);
		return "sign-up";
	}

	@GetMapping("/signup2")
	public String toSignUp2(Model model) {
		System.out.println("in GET /signup2");

		model.addAttribute("user", new User());
		return "sign-up2";
	}

	/**
	 * new user signup first function
	 * 
	 * @param user
	 * @param bindingResult : check user's input is valid or not
	 * @param session       : to store the user's input value between multiple page
	 * @return name of html page => "sign-up2.html"
	 */
	@PostMapping("/signup2")
	public String signUp(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpServletRequest request,
			HttpSession session) {
		System.out.println(">>>>>>>>>> POST /in signup2");
		System.out.println(user.toString());

		// check username been used or not
		User userNameUser = userService.findUserByUsername(user.getUsername());
		if (userNameUser != null) {
			System.out.println("username repeated <<<<<<<<");

			bindingResult.rejectValue("username", "error.user",
					"There is already a user registered with the same username");
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			return "redirect:/signup";
//			return "sign-up";
//			return result(ExceptionMsg.UserNameUsed);
		}
		// length validation not pass
		if (bindingResult.hasErrors()) {
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			return "redirect:/signup";
//			return "sign-up";
		}

		// session
		session.setAttribute("user", user);

		return "sign-up2";
	}

	@GetMapping("/confirm")
	public String toConfirm(Model model) {
		System.out.println("in GET /confirm");

		model.addAttribute("user", new User());
		return "confirm";
	}

	/**
	 * 
	 * @param user
	 * @param session
	 * @return name of html page => "confirm.html"
	 */
	@PostMapping("/confirm")
	public String toSignUp2(@ModelAttribute User user, HttpSession session) {
		System.out.println(">>>>>>>>>> POST in /confirm");
		session.setAttribute("user", user);
		System.out.println(user.toString());
		return "confirm";
	}

	/**
	 * confirm
	 * 
	 * @param user
	 * @param action  : check which button been clicked
	 * @param request
	 * @param session
	 * @return "signup-succeeded" or "redirect:/signup2"
	 */
	@PostMapping("/signup-succeeded")
	public String confirm(@ModelAttribute User user, @RequestParam String action, HttpServletRequest request,
			HttpSession session) {
		System.out.println(">>>>>>>>>> in /signup-succeeded");

		System.out.println("action >>>>>> " + action);
		String returnStr = "";

		// when user confirm the data is right, then write into DB
		// click "Save" button
		if (action.equals("Save")) {
			System.out.println("in save");
			userService.saveUser(user);
//			userService.save(user);
			returnStr = "signup-succeeded";
		} else if (action.equals("Back to SignUp")) {
			System.out.println(">>>>>>>> in Back to SignUp");
			// set redirect post method
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			returnStr = "redirect:/signup";
		}
		// when user find something wrong, and want to correct
		// click "Back to SignUp2" button
		else if (action.equals("Back to SignUp2")) {
			System.out.println("in Back to SignUp2");
			// set redirect post method
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			returnStr = "redirect:/signup2";
		}

		return returnStr;
	}

	/**
	 * 
	 * @return "signup-succeeded.html"
	 */
	@GetMapping("/succeeded")
	public String toSignupSucceededPage() {
		return "signup-succeeded";
	}

	/**
	 * list all the user
	 * 
	 * @param model
	 * @return "show.html"
	 */
	@GetMapping("/show")
	public String showAll(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		// get data from db
		List<User> users = userService.getUserList();

		// set the attribute name
		// let the thymeleaf know which data it is
		model.addAttribute("users", users);

		User user = (User) userService.findUserByUsername(currentUser.getUsername());
		model.addAttribute("currentUser", user);
		return "show";
	}

	/**
	 * edit a data
	 * 
	 * @param model
	 * @param id    : the id of a data to be edited
	 * @return
	 */
	@GetMapping("/toEdit")
	public String toEdit(Model model, Long id, @AuthenticationPrincipal UserDetails currentUser) {

		String returnPage = "";
		if (currentUser == null) {
			returnPage = "user-edit";
		} else {
			User user = (User) userService.findUserByUsername(currentUser.getUsername());

			String a = user.getPassword();
			System.out.println(a);
			userService.findUserById(id);
			model.addAttribute("currentUser", user);
			model.addAttribute("user", user);
			returnPage = "user-edit";
		}

		return returnPage;

	}

	/**
	 * userService edit method execute
	 * 
	 * @param user
	 * @return "redirect:/show"
	 */
	@PostMapping("/edit")
	public String edit(User user) {
		userService.edit(user);
		return "redirect:/show";
	}

	/**
	 * delete a user, userService delete method execute
	 * 
	 * @param id
	 * @return "redirect:/show"
	 */
	@GetMapping("/delete")
	public String delete(Long id) {
		userService.delete(id);
		return "redirect:/show";
	}

}
