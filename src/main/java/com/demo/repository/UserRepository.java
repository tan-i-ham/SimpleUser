package com.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long id);

	User findByEmail(String email);

	User findByUsername(String name);

	void deleteById(Long id);
}
