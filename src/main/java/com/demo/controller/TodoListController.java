package com.demo.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.model.TodoList;
import com.demo.model.TodoListType;
import com.demo.model.User;
import com.demo.service.TodoListService;
import com.demo.service.UserService;

@Controller
public class TodoListController {
	private final static Logger LOGGER = LoggerFactory.getLogger(TodoListController.class);

	@Autowired
	UserService userService;
	
	private final TodoListService todoListService;

	public TodoListController(TodoListService todoListService) {
		this.todoListService = todoListService;
	}

	@GetMapping("/addTodo")
	public String viewTodoPage(Model model) {
		model.addAttribute("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		List<TodoList> allTodoLists = todoListService.getAllTodoList();
		Map<TodoListType, List<TodoList>> groupedTodoLists = allTodoLists.stream()
				.collect(Collectors.groupingBy(TodoList::getType));

		model.addAttribute("impLists", groupedTodoLists.get(TodoListType.IMPORTANT));
		model.addAttribute("normalLists", groupedTodoLists.get(TodoListType.NORMAL));
		model.addAttribute("casualLists", groupedTodoLists.get(TodoListType.CASUAL));

		return "add-todo-list";

	}

	@PostMapping("/createTodo")
	public String createTodoList(@RequestParam(name = "impList", required = false) String impList,
			@RequestParam(name = "normalList", required = false) String normalList,
			@RequestParam(name = "casualList", required = false) String casualList) {
		List<TodoList> todoLists = new ArrayList<>();

		if (!StringUtils.isEmpty(impList)) {
			todoLists.add(getTodoList(impList, TodoListType.IMPORTANT));
		}

		if (!StringUtils.isEmpty(normalList)) {
			todoLists.add(getTodoList(normalList, TodoListType.NORMAL));
		}

		if (!StringUtils.isEmpty(casualList)) {
			todoLists.add(getTodoList(casualList, TodoListType.CASUAL));
		}

		if (!todoLists.isEmpty()) {
			LOGGER.info("Saved {}", todoListService.saveAll(todoLists));
		}
		return "redirect:/todo";
	}

	private TodoList getTodoList(String todoList, TodoListType todoListType) {
		// TODO Auto-generated method stub

		TodoList todoListObj = new TodoList();
		todoListObj.setContent(todoList);
		todoListObj.setType(todoListType);

		return todoListObj;
	}

	@GetMapping(value = "/listTodos")
	public String listTodo(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Page<TodoList> todoPage = todoListService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

		model.addAttribute("todoPage", todoPage);

		int totalPages = todoPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "listTodos";
	}

	/**
	 * get all the todo list record (admin user can get it)
	 * 
	 * @param model
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/todo2")
	public String pageAllListTodo(Model model,
			@PageableDefault(value = 5, sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable) {

		Page<TodoList> page = todoListService.findAll(pageable);
		model.addAttribute("page", page);

		return "todo2";
	}

	/**
	 * get the current user's todo lists
	 * current main using page method
	 * March, 2019
	 * 
	 * @param model
	 * @param principal
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/to-do")
	public String personalTodo(Model model, Principal principal,
			@PageableDefault(value = 10, sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable) {
		// get logged in username
		String username = principal.getName();

		Page<TodoList> page = todoListService.findTodoByUsernamePageable(username, pageable);
		model.addAttribute("page", page);

		return "todo";
	}
	
	@ModelAttribute
	public void addAttributes(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		User user = (User) userService.findUserByUsername(currentUser.getUsername());
		// set new attribute 'currentUser' to model, in order to show user's info on
		// navbar
		model.addAttribute("currentUser", user);
	}

}
