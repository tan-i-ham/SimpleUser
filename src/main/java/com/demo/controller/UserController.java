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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
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
	public String home() {
		return "index";
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
	
	@GetMapping("/admin")
	public ModelAndView admin() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        
        modelAndView.addObject("username", "Welcome " + user.getFirstname() + " " + user.getLastname() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("login-successed");
//		return "index";
        return modelAndView;
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
	public String redirectSignUp( @ModelAttribute User user, HttpSession session, Model model) {
		System.out.println("in POST /signup");
		System.out.println(user.toString());
		

		model.addAttribute("inuse", "in used username");
		// session
		session.setAttribute("user", user);
		return "sign-up";
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
	public String signUp( @Valid @ModelAttribute User user, BindingResult bindingResult, 
			HttpServletRequest request, HttpSession session) {
		System.out.println(">>>>>>>>>> POST /in signup2");
		System.out.println(user.toString());
		
		// check username been used or not
		User userNameUser = userService.findUserByUsername(user.getUsername());
		if (userNameUser != null) {
			System.out.println("username repeated <<<<<<<<");
			bindingResult
            .rejectValue("username", "error.user",
                    "There is already a user registered with the same username");
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			return "redirect:/signup";
//			return result(ExceptionMsg.UserNameUsed);
		}
		// length validation not pass
		if (bindingResult.hasErrors()) {
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			return "redirect:/signup";
		}
		
	
		// session
		session.setAttribute("user", user);

		return "sign-up2";
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
	 * @return "successed" or "redirect:/signup2"
	 */
	@PostMapping(value = "/signup-successed")
	public String confirm(@ModelAttribute User user, @RequestParam String action, 
			HttpServletRequest request,
			HttpSession session) {
		System.out.println(">>>>>>>>>> in /signup-successed");

		System.out.println("action >>>>>> " + action);
		String returnStr = "";

		// when user confirm the data is right, then write into DB
		// click "Save" button
		if (action.equals("Save")) {
			System.out.println("in save");
			userService.saveUser(user);
//			userService.save(user);
			returnStr = "successed";
		}
		else if (action.equals("Back to SignUp")) {
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
	 * @return "successed.html"
	 */
	@GetMapping(value = "/successed")
	public String successedPage() {
		return "successed";
	}

	/**
	 * list all the user
	 * 
	 * @param model
	 * @return "show.html"
	 */
	@GetMapping("/show")
	public String showAll(Model model) {
		// get data from db
		List<User> users = userService.getUserList();
		// set the attribute name
		// let the thymeleaf know which data it is
		model.addAttribute("users", users);
		return "show";
	}

	/**
	 * edit a data
	 * 
	 * @param model
	 * @param id    : the id of a data to be edited
	 * @return
	 */
	@GetMapping("/{id}/toEdit")
	public String toEdit(Model model, Long id) {
//		User user = userService.findUserById(id);

		// the attribute name used in html in thymeleaf
//		model.addAttribute("user", user);
		return "user-edit";
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