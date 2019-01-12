package com.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
public class UserRepoTest {
	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void findByUsername_HappyPath_ShouldReturn1User() throws Exception {
		Role role = new Role();
		role.setRole("USER");
		Set<Role> roles = new HashSet<Role>(Arrays.asList(role));

		// Given
		User user = new User();
		user.setUsername("shazin");
		user.setPassword("shaz980");
		user.setEmail("test@r.com");
		user.setRoles(roles);
		testEntityManager.persist(user);
		testEntityManager.flush();

		// When
		User actual = userRepository.findByUsername("shazin");

		// Then
		assertThat(actual).isEqualTo(user);
	}

	@Test
	public void save_HappyPath_ShouldSave1User() throws Exception {
		Role role = new Role();
		role.setRole("USER");
		Set<Role> roles = new HashSet<Role>(Arrays.asList(role));
		// Given
		User user = new User();
		user.setUsername("shazin");
		user.setPassword("shaz980");
		user.setEmail("test@r.com");
		user.setRoles(roles);

		// When
		User actual = userRepository.save(user);

		// Then
		assertThat(actual).isNotNull();
		assertThat(actual.getId()).isNotNull();
	}

}
