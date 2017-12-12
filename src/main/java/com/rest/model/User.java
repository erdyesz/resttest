package com.rest.model;

import java.util.List;

public class User {
	public String username;
	public List<String> roles;
	
	public User(String username, List<String> roles) {
		this.username = username;
		this.roles = roles;
	}
}
