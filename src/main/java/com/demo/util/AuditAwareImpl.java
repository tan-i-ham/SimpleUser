package com.demo.util;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

/**
 * 
 * helps to load the currently logged-in user:
 * 
 * @author tp6han
 *
 */
@Component
public class AuditAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

//        String returnUserName = ((User) authentication.getPrincipal()).getUsername();
		String returnUserName = authentication.getName();
		System.out.println("returnUserName >>>>> " + returnUserName);
		return Optional.of(returnUserName);
	}
}
