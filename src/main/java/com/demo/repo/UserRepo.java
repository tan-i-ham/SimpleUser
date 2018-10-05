package com.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.User;


@Repository
public interface UserRepo extends JpaRepository<User, Long>{
	User findById(long id);
//	void deleteById(Long id);
	void deleteById(Long id);

}
