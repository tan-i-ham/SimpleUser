package com.demo.controller;

import java.util.Collection;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.demo.model.Group1;
import com.demo.model.Group2;
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
	 * @param currentUser : provided by Spring Security to get the login user object
	 * @return
	 */
	@GetMapping("/")
	public String home(Model model, @ModelAttribute("currentUser") User user) {
		String returnPage = "";
		// no user login
		if (user == null) {
			returnPage = "index";
		} else { // when some user is login
			// check if this user has any prog_lan skill
			// if not then return "index.html"
			if (user.getProgLang() == null) {
				returnPage = "index";
			} else {
				// separate the string into array, which can be iterated in html page
				String[] languages = user.getProgLang().split(",");
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
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/signup")
	public String toSignUp(Model model) {
		System.out.println("in GET /signup");

		// not a new registration process, is return back from confirm page
		if (model.containsAttribute("user")) {
			System.out.println("user already create");
		} else { // new register process
			model.addAttribute("user", new User());
		}

		return "sign-up";
	}

	/**
	 * 
	 * user click the submit button in sign-up.html action="/signup" method="POST"
	 * 
	 * @param user
	 * @param bindingResult : to validate the input string is atch to the rule
	 * @param session
	 * @return
	 */
	@PostMapping("/signup")
	public String signUp(@Validated({ Group1.class }) @ModelAttribute User user, BindingResult bindingResult,
			HttpSession session) {
		System.out.println("in POST /signup");

		User userNameUser = userService.findUserByUsername(user.getUsername());
		User userEmailUser = userService.findUserByEmail(user.getEmail());

		// check username been used or not
		if (userNameUser != null) {
			bindingResult.rejectValue("username", "error.user",
					"There is already a user registered with the same username");
			return "sign-up";
		}

		// check email been used or not
		if (userEmailUser != null) {
			bindingResult.rejectValue("email", "error.user", "There is already a user registered with the same email");
			return "sign-up";
		}

		// when input has length error or email form error
		if (bindingResult.hasErrors()) {
			return "sign-up";
		}

		// session
		session.setAttribute("user", user);

		// no error, continue to next step
		return "redirect:/signup2";
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/signup2")
	public String toSignUp2(Model model) {
		System.out.println("in GET /signup2");

		return "sign-up2";
	}

	/**
	 * 
	 * @param user
	 * @param bindingResult
	 * @param session
	 * @return
	 */
	@PostMapping("/signup2")
	public String signUp2(@Validated({ Group2.class }) @ModelAttribute User user, BindingResult bindingResult,
			HttpSession session) {
		System.out.println(">>>>>>>>>> POST in /signup2");
		session.setAttribute("user", user);
		System.out.println(user);

		// when progLang input is empty
		if (bindingResult.hasErrors()) {
			System.out.println("error in signup2");
			return "sign-up2";
		}

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
	public String confirm(@ModelAttribute User user, @RequestParam String action, HttpSession session,
			SessionStatus status) {
		System.out.println(">>>>>>>>>> in POST /confirm");

		System.out.println("action >>>>>> " + action);
		String returnStr = "";

		// when user confirm the data is right, then write into DB
		// click "Save" button
		if (action.equals("Save")) {
			System.out.println("in save");
			userService.saveUser(user);
			// kill session
			status.setComplete();
			returnStr = "redirect:/signup-succeeded";
		}
		// when user find something wrong, and want to correct
		// click "Back to SignUp" button
		else if (action.equals("Back to SignUp")) {
			System.out.println(">>>>>>>> in Back to SignUp");
			returnStr = "redirect:/signup";
		}
		// when user find something wrong, and want to correct
		// click "Back to SignUp2" button
		else if (action.equals("Back to SignUp2")) {
			System.out.println("in Back to SignUp2");
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

	/****************************************************************************************/
	/***********
	 * the following functions are out of request of OJT (can be skipped)
	 ***********/
	/****************************************************************************************/

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
	 * 
	 * @param id
	 * @param model
	 * @param currentUser
	 * @return
	 */
	@GetMapping("toEdit/{id}")
	public String toEdit(@PathVariable("id") long id, Model model, @AuthenticationPrincipal UserDetails currentUser) {
		User user = userService.findUserById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

		String returnPage = "";
		if (currentUser == null) {
			returnPage = "user-edit";
		} else {

			userService.findUserById(id);
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("user", user);
			returnPage = "user-edit";
		}

		return returnPage;

	}

	/**
	 * 
	 * @param user
	 * @param action
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 */
	@PostMapping("/update")
	public String updateUser(@ModelAttribute @Valid User user, @RequestParam String action, BindingResult result,
			Model model, HttpServletRequest request, Authentication authentication) {
		if (result.hasErrors()) {
			return "user-edit";
		}
		model.addAttribute("user", user);
		Collection<SimpleGrantedAuthority> authorities = 
				(Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
														.getAuthentication().getAuthorities();
		
		if (action.equals("Save")) {
			userService.save(user);

			// when user has admin role, redirect to show
			String role = authorities.toArray()[0].toString();
			String temp = "ROLE_ADMIN";
			if (role.equals(temp)) {
				System.out.println("role admin");
				return "redirect:/show";
			}
			else {
				return "redirect:/";
			}
		}

		
		return "redirect:/";

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
	
	/**
	 * The @ModelAttribute is an annotation that binds a method parameter or method 
	 * return value to a named model attribute and then exposes it to a web view.
	 * will be execute before any method in this controller
	 * 
	 * @param model
	 * @param currentUser
	 */
	@ModelAttribute
	public void addAttributes(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser == null) {
			model.addAttribute("currentUser", null);
		}
		else {
			User user = (User) userService.findUserByUsername(currentUser.getUsername());
			// set new attribute 'currentUser' to model, in order to show user's info on
			// navbar
			model.addAttribute("currentUser", user);
		}
	}

}