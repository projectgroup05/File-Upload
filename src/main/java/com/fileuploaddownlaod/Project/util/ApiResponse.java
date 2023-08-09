package com.fileuploaddownlaod.Project.util;

import java.time.LocalDateTime;

public class ApiResponse {

	private String message;
	private LocalDateTime dateTime;
	private String fileName;

	public ApiResponse() {
	}

	public ApiResponse(String message, LocalDateTime dateTime) {
		super();
		this.message = message;
		this.dateTime = dateTime;
	}

	public ApiResponse(String message) {
		super();
		this.message = message;
	}

	public ApiResponse(String message, LocalDateTime dateTime, String fileName) {
		super();
		this.message = message;
		this.dateTime = dateTime;
		this.fileName = fileName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
