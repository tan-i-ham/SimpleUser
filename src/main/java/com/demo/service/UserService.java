package com.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.demo.model.User;

public interface UserService {
	void save(User user);

	void edit(User user);

	void delete(Long id);

	List<User> getUserList();

	Optional<User> findUserById(Long id);
	
	User findUserByUsername(String name);
	
	User findUserByEmail(String email);
	
	void saveUser(User user);
	

}
