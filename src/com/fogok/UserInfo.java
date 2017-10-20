package com.fogok;

public class UserInfo {
	private String username;
	private long userId;
	
	public UserInfo buildUsername(String username) {
		this.username = username;
		
		return this;
	}
	
	public UserInfo builduserId(int userId) {
		this.userId = userId;
		
		return this;
	}
	
	public final String getUsername() {
		return this.username;
	}
	
	public final void setUsername(String username) {
		this.username = username;
	}
	
	public final long getUserId() {
		return this.userId;
	}
	
	public final void setUserId(int userId) {
		this.userId = userId;
	}
}
