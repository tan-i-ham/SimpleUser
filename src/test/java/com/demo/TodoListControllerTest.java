package com.demo;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.demo.controller.TodoListController;
import com.demo.service.TodoListService;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoListController.class)
public class TodoListControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TodoListService todoListService;
	
	/**
	 * when save the todoList, the page will redirect to "/todo"
	 * 
	 * @throws Exception
	 */
//	@Test
//	public void saveTodoList_HappyPath_ShouldReturnStatus302() throws Exception() {
//		ResultAction resultAction = mockMvc.perform(post("/createTodo"));
//	}

}
