package com.demo;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
	

	
	

}
