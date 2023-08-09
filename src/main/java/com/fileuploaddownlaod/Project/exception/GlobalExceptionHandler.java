package com.fileuploaddownlaod.Project.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fileuploaddownlaod.Project.util.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(FileManagerException.class)
	public ResponseEntity<?> gradeIdNotPresent(FileManagerException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,LocalDateTime.now());
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);

	}

}