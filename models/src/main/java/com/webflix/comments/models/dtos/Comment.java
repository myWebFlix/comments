package com.webflix.comments.models.dtos;

import javax.persistence.Column;
import java.util.Date;

public class Comment {
	private String user_name;
	private Date timestamp;
	private String text;

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
