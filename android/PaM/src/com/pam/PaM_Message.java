package com.pam;

import org.json.JSONException;
import org.json.JSONObject;

public class PaM_Message {
	private String Message;
	private String User;
	private int date_created;
	
	public PaM_Message(String message_string) {
		try {
			JSONObject json = new JSONObject(message_string);
			setMessage(json.getString("content"));
			setUser(json.getString("owner"));
			setDate(json.getInt("date_created"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public int getDate() {
		return date_created;
	}
	public void setDate(int date_created) {
		this.date_created = date_created;
	}
}
