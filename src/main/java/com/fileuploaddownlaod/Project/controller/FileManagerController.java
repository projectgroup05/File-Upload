package com.fileuploaddownlaod.Project.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fileuploaddownlaod.Project.exception.FileManagerException;
import com.fileuploaddownlaod.Project.service.FileManagerService;

@RestController
@RequestMapping("/file")
public class FileManagerController {
	@Autowired
	private FileManagerService fileManagerSerive;

	@PostMapping("/save")
	public ResponseEntity<?> uploadFile(@RequestParam("fileName") MultipartFile file)
			throws IOException, FileManagerException {
		return fileManagerSerive.uploadFile(file);
	}

	@GetMapping("/get")
	public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName)
			throws IOException, FileManagerException {
		return fileManagerSerive.downloadFile(fileName);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteFile(@RequestParam("fileName") String fileName)
			throws IOException, FileManagerException {
		return fileManagerSerive.deleteFile(fileName);

	}
}
