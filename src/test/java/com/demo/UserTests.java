package com.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.demo.model.User;


public class UserTests {
	@Test
	public void userManagedByLombokShouldWork() { 
        User user = new User("idusername", "1qazxsw2", "emla@r.com"); 
        assertThat(user.getUsername()).isEqualTo("idusername"); 
        assertThat(user.getPassword()).isEqualTo("1qazxsw2"); 
        assertThat(user.getEmail()).isEqualTo("emla@r.com");
      } 
}
