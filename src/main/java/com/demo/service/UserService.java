package com.demo.service;

import java.util.List;

import com.demo.model.User;

public interface UserService {
	void save(User user);

	void edit(User user);

	void delete(Long id);

	List<User> getUserList();

	User findUserById(long id);
	
	User findUserByUsername(String name);

}
