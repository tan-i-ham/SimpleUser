package com.demo.service;

import java.util.List;


import com.demo.model.User;


public interface UserService{

	List<User> getUserList();

	void save(User user);

	void edit(User user);

	void delete(Long id);

	User findUserById(long id);

}
