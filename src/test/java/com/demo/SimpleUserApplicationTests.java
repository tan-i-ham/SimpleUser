package com.demo;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.demo.repository.UserRepository;

@RunWith(SpringRunner.class)
//@WebMvcTest(value= UserController.class, secure= false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SimpleUserApplication.class)
public class SimpleUserApplicationTests {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	@LocalServerPort
	private int port;

	private MockMvc mvc;

	@Before
	public void setupMockmvc() {
		mvc = webAppContextSetup(context).build();
	}

	@Test
	public void contextLoads() throws Exception {
		System.out.println("start testing");
		assertEquals(6, userRepository.count());

	}

	@Test
	public void homePageApiTest() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void loginPageApiTest() throws Exception {
		mvc.perform(get("/login")).andExpect(status().is3xxRedirection());
	}

	@Test
	public void signup1PageApiTest() throws Exception {
		mvc.perform(get("/signup")).andExpect(status().isOk());
	}

//	@Test
//	public void signup2PageApiTest() throws Exception {
//		mvc.perform(get("/signup2")).andExpect(status().isOk());
//	}
	/**
	 * when user's role is admin, can access to the "/show" page
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "testmac", password = "1qazxsw2", roles = "ADMIN")
	public void roleIsAdminTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/show")
				.accept(MediaType.ALL))
				.andExpect(status().isOk());
	}
	
	/**
	 * when user's role is user, can't access to the "/show" page
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "testuser", password = "1qazxsw2", roles = "USER")
	public void roleIsUserTest() throws Exception {
//		mvc.perform(get("/show")).andExpect(redirectedUrl("/login"));
		mvc.perform(MockMvcRequestBuilders.get("/show")
				.accept(MediaType.ALL))
				.andExpect(redirectedUrl("/error"));
	}
	
	@Test
	public void guestTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/show")
				.accept(MediaType.ALL))
				.andExpect(redirectedUrl("/login"));
	}

}
