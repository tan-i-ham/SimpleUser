package com.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.demo.model.TodoList;
import com.demo.model.User;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
	// custom method with a @Query annotation
	// for using a database-independent SQL query to filter out data from the
	// database
	@Query(value = "select * from todo_list c where year(c.created_at) = ?1 "
			+ "AND month(c.created_at) = ?2 AND day(c.created_at) = ?3", nativeQuery = true)
	List<TodoList> findByCreatedYearAndMonthAndDay(int year, int month, int day);

	List<TodoList> findByCreatedBy(String user);
	
	// custom methis with current username with pagination feature
	@Query(value="select * from todo_list todo where todo.created_by= ?1", nativeQuery = true)
	Page<TodoList> findByCreatedByPage(String user, Pageable pageable);

}
