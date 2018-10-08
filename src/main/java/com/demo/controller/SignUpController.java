package com.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	@PostMapping("/signup2")
	public String signUp(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpSession session) {
		System.out.println(">>>>>>>>>> POST /in signup2");
		System.out.println(user.toString());

		// length validation not pass
		if (bindingResult.hasErrors()) {
			return "sign-up";
		}
		/* session */
		session.setAttribute("user", user);

		return "sign-up2";
	}


	/* new user signup page 2 function */
	@PostMapping("/confirm")
	public String toSignUp2(@ModelAttribute User user, HttpSession session) {
		System.out.println(">>>>>>>>>> in /confirm");
		System.out.println(user.toString());
		

		session.setAttribute("user", user);
		
		return "confirm";
	}

	/* confirm */
	@PostMapping(value = "/finish-signup")
	public String confirm(@ModelAttribute User user, @RequestParam String action,HttpServletRequest request, HttpSession session) {
		System.out.println(">>>>>>>>>> in /finish-signup");

		System.out.println("action >>>>>> " + action);
		String returnStr = "";
		
		// when user confirm the data is right,
		// then write into DB
		// click "Save" button
		if (action.equals("Save")) {
			System.out.println("in save");
			userService.save(user);
			returnStr = "successed";
		} 
		// when user find something wrong, and want to correct
		// click "Back to edit: button
		else if (action.equals("Back to edit")) {
			System.out.println("in back-edit");
			// set redirect can also use post method
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			returnStr = "redirect:/signup2";
		}

		return returnStr;
	}


	@GetMapping(value = "/success")
	public String successedPage() {
		return "successed";
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
