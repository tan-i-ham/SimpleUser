package com.demo.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	
	
	public Page<TodoList> findPaginated(Pageable pageable) {
		List<TodoList> todos = todoListRepository.findAll();

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TodoList> list;
 
		if (todos.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, todos.size());
			list = todos.subList(startItem, toIndex);
		}
 
        Page<TodoList> todoPage
          = new PageImpl<TodoList>(list, PageRequest.of(currentPage, pageSize), todos.size());
 
     
        return todoPage;
        
    }
	
	
	/**
	 * 
	 * because JpaRepository also extends PagingAndSortingRepository, 
	 * we can user it directly by passing the Pageable parameter
	 * 
	 * @param pageable
	 * @return
	 */
	public Page<TodoList> findAll(Pageable pageable){
		return todoListRepository.findAll(pageable);
	}
	
	/**
	 * search current user's to-do list
	 * @param pageable
	 * @return
	 */
	public List<TodoList> findTodoByUser(String userName){
		return todoListRepository.findByCreatedBy(userName);
	}
}
