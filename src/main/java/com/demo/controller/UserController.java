package com.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.demo.model.User;
import com.demo.service.UserService;

@Controller
@SessionAttributes("user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	/**
	 * 
	 * @param model
	 * @param currentUser
	 * @return
	 */
	@GetMapping("/")
	public String home(Model model, @AuthenticationPrincipal UserDetails currentUser, HttpSession session) {
		String returnPage = "";
//		session.invalidate();
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
	public String login(HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		session.invalidate();
		if (!(auth instanceof AnonymousAuthenticationToken)) {

			/* The user is logged in :) */
			log.info("The user is logged in.");
			return "redirect:/";
		}
		return "login";
	}

	/**
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/signup")
	public String toSignUp(Model model, HttpServletRequest request, HttpSession session) {
		System.out.println(">>>>>>>>>>>>>" + request.getRequestURI());
		System.out.println("in GET /signup");

		if (session.getAttribute("user") == null) {
			System.out.println("session is null");
			model.addAttribute("user", new User());
		}

		System.out.println(session.getAttribute("user"));

		return "sign-up";
	}

	/**
	 * 
	 * @param user
	 * @param bindingResult
	 * @param request
	 * @param session
	 * @return
	 */
	@PostMapping("/signup")
	public String signUp(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpServletRequest request,
			HttpSession session) {
		System.out.println("in POST /signup");

		User userNameUser = userService.findUserByUsername(user.getUsername());
		User userEmailUser = userService.findUserByEmail(user.getEmail());

		// check username been used or not
		if (userNameUser != null) {
			System.out.println("username repeated <<<<<<<<");
			bindingResult.rejectValue("username", "error.user",
					"There is already a user registered with the same username");
			return "sign-up";
		}

		// check email been used or not
		if (userEmailUser != null) {
			System.out.println("email repeated <<<<<<<<");
			bindingResult.rejectValue("email", "error.user", "There is already a user registered with the same email");
			return "sign-up";
		}

		// when input has length error
		if (bindingResult.hasErrors()) {
			return "sign-up";
		}

		// session
		session.setAttribute("user", user);

		return "redirect:/signup2";
	}

	/**
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/signup2")
	public String toSignUp2(Model model, HttpSession session) {
		System.out.println("in GET /signup2");

//		model.addAttribute("user", new User());
		User user = (User) session.getAttribute("user");
		model.addAttribute("user", user);
		System.out.println(user);
//		System.out.println("PL >>> " + user.getProgrammingLanguage());

		String[] checkbox_choices = { "python", "java", "c", "cplusplus", "kotlin", "javascript", "go", "other" };
		model.addAttribute("checkbox_choices", checkbox_choices);
		// split programming languages to array
		if (user.getProgrammingLanguage() == null) {
			return "sign-up2";
		} else {
			String[] languages = user.getProgrammingLanguage().split(",");
			model.addAttribute("pl", languages);
		}

		return "sign-up2";
	}

	/**
	 * 
	 * @param user
	 * @param bindingResult
	 * @param request
	 * @param session
	 * @return
	 */
	@PostMapping("/signup2")
	public String signUp2(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpServletRequest request,
			HttpSession session) {
		System.out.println(">>>>>>>>>> POST in /signup2");
		session.setAttribute("user", user);
		System.out.println(user);

		return "redirect:/confirm";
	}

	/**
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/confirm")
	public String toConfirm(Model model, HttpSession session) {
		System.out.println("in GET /confirm");
		return "confirm";
	}

	/**
	 * 
	 * @param user
	 * @param action
	 * @param request
	 * @param session
	 * @return
	 */
	@PostMapping("/confirm")
	public String confirm(@ModelAttribute User user, @RequestParam String action, HttpServletRequest request,
			HttpSession session) {
		System.out.println(">>>>>>>>>> in POST /confirm");

		System.out.println("action >>>>>> " + action);
		String returnStr = "";

		// when user confirm the data is right, then write into DB
		// click "Save" button
		if (action.equals("Save")) {
			System.out.println("in save");
			userService.saveUser(user);
			// kill session
			session.invalidate();
			returnStr = "redirect:/signup-succeeded";
		} else if (action.equals("Back to SignUp")) {
			System.out.println(">>>>>>>> in Back to SignUp");
			returnStr = "redirect:/signup";
		}
		// when user find something wrong, and want to correct
		// click "Back to SignUp2" button
		else if (action.equals("Back to SignUp2")) {
			System.out.println("in Back to SignUp2");
			
			System.out.println();
			
			
			
			returnStr = "redirect:/signup2";
		}

		return returnStr;
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/signup-succeeded")
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