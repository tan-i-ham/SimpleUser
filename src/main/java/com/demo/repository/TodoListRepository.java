package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.model.TodoList;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
	
	// custom method with a @Query annotation 
	// for using a database-independent SQL query to filter out data from the database
//	@Query("SELECT c FROM Todolist c WHERE year(c.createdAt) = ?1 AND "
//			+ "month(c.createdAt) = ?2 AND day(c.createdAt) = ?3")
//	@Query(value = "from Todolist u where u.name=:name")
//	@Query(value = "select * from #{#entityName} c where year(c.createdAt) = ?1 "
//			+ "AND month(c.createdAt) = ?2 AND day(c.createdAt) = ?3", nativeQuery = true)
	@Query(value = "select * from todo_list c where year(c.created_at) = ?1 "
			+ "AND month(c.created_at) = ?2 AND day(c.created_at) = ?3", nativeQuery = true)
	List<TodoList> findByCreatedYearAndMonthAndDay(int year, int month, int day);

}
