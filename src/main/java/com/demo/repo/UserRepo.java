package com.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.User;


@Repository
public interface UserRepo extends JpaRepository<User, Long>{
	Optional<User> findById(Long id);
	void deleteById(Long id);

}
