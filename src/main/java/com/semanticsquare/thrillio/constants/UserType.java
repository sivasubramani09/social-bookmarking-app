package com.semanticsquare.thrillio.constants;

public enum UserType {

	USER("user"),
	EDITOR("editor"),
	CHIEF_EDITOR("chiefeditor");
	
	private String value;
	
	private UserType(String value) {
		this.value =value;
	}
	
	public String getUserType() {
		return value;
	}
}
