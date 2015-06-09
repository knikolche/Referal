package com.referal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	
	@ManyToOne
	@JoinColumn(name="STYLE_ID")
	private LearningStyle style;

	public User() {
		
	}
	
	public User(String username, String password, LearningStyle style) {
		this.username=username;
		this.password=password;
		this.style=style;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LearningStyle getStyle() {
		return style;
	}

	public void setStyle(LearningStyle style) {
		this.style = style;
	}
}
