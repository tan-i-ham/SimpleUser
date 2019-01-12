package com.demo.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional(rollbackFor = Exception.class)
	public void save(User user) {
		userRepository.save(user);
	}

	public void edit(User user) {
		userRepository.save(user);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public List<User> getUserList() {
		return userRepository.findAll();
	}

	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}

	public User findUserByUsername(String name) {
		return userRepository.findByUsername(name);
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveUser(User user) {
//		user.setPassword(user.getPassword());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleRepository.findByRole("ROLE_USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		Role[] convertRole = user.getRoles().stream().toArray(Role[]::new);

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				Arrays.asList(new SimpleGrantedAuthority(convertRole[0].getRole())));
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}
}
