package com.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.TodoList;
import com.demo.repository.TodoListRepository;

@Service
@Transactional(readOnly = true)
public class TodoListService {
	private static final Logger LOGGER =  LoggerFactory.getLogger(TodoListService.class);

	private final TodoListRepository todoListRepository;

	public TodoListService(TodoListRepository todoListRepository) {
		this.todoListRepository = todoListRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<TodoList> saveAll(List<TodoList> todoLists) {
		LOGGER.info("Saving {}", todoLists);
		return todoListRepository.saveAll(todoLists);
	}
	
	public List<TodoList> getAllTodoList(){
		return todoListRepository.findAll();
	}

	public List<TodoList> getAllTodoListsForToday() {
		LocalDate localDate = LocalDate.now();
		return todoListRepository.findByCreatedYearAndMonthAndDay(
				localDate.getYear(), 
				localDate.getMonth().getValue(),
				localDate.getDayOfMonth());
	}
}
