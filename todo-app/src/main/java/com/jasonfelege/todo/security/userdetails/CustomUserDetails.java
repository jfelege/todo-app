package com.jasonfelege.todo.security.userdetails;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.jasonfelege.todo.security.data.User;

public class CustomUserDetails extends User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		StringBuilder sb = new StringBuilder();
		
		super.getRoles().forEach(role -> { 
			sb.append(role.getName());
			sb.append(',');
		});
		
		if (sb.length() > 0) {
			// strip trailing comma from string
			sb.deleteCharAt(sb.length() - 1);
		}
		
		return AuthorityUtils.commaSeparatedStringToAuthorityList(sb.toString());
	}

	@Override
	public String getUsername() {
		return super.getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO enhance this feature
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return super.isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO enhance this feature
		return true;
	}

	
	public static CustomUserDetails fromUser(User user) {
		CustomUserDetails details = new CustomUserDetails();
		details.setEnabled(user.isEnabled());
		details.setId(user.getId());
		details.setName(user.getName());
		details.setPassword(user.getPassword());
		details.setRoles(user.getRoles());
		return details;
	}
}
