package com.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.model.TodoList;
import com.demo.model.TodoListType;
import com.demo.repository.TodoListRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
public class TodoListRepoTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private TodoListRepository todoListRepository;

	@Test
	public void findByCreatedYearAndMonthAndDay_HappyPath_ShouldReturn1Comment() {
		// Given
		TodoList todoList = new TodoList();
		todoList.setContent("Test list");
		todoList.setType(TodoListType.IMPORTANT);
		todoList.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		todoList.setDone(true);

		testEntityManager.persist(todoList);
		testEntityManager.flush();

		// when
		LocalDate now = LocalDate.now();
		List<TodoList> todoLists = todoListRepository.findByCreatedYearAndMonthAndDay(now.getYear(),
				now.getMonth().getValue(), now.getDayOfMonth());

		// then
		assertThat(todoLists).hasSize(1);
		assertThat(todoLists.get(0)).hasFieldOrPropertyWithValue("content", "Test list");

	}

	@Test
	public void save_HappyPath_ShouldSave1Comment() {
		TodoList todoList = new TodoList();
		todoList.setContent("Test list");
		todoList.setType(TodoListType.IMPORTANT);
		todoList.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		todoList.setDone(true);
		
		// When
        TodoList saved = todoListRepository.save(todoList);
        
        // then
        TodoList reObject = testEntityManager.find(TodoList.class, saved.getId());
        assertThat(reObject).isEqualTo(todoList);
	}
}
