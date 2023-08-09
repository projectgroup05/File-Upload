package com.fileuploaddownlaod.Project.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fileuploaddownlaod.Project.exception.FileManagerException;
import com.fileuploaddownlaod.Project.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FileManagerService {

	@Value("${upload.directory}")
	private String uploadDir;

	@Value("${upload.directory.max-size}")
	private String maxSize;

	@Value("${upload.directory.type}")
	private String[] fileType;

	public ResponseEntity<?> uploadFile(MultipartFile file) throws FileManagerException {
		if (file.isEmpty()) {
			String message = "File is empty. Please select a file to upload.";
			throw new FileManagerException(message);
		}

		try {

			// Create the upload directory if it doesn't exist
			File uploadDir = new File(this.uploadDir);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}
			// Check The Type Of File
			String originalFileName = file.getOriginalFilename();

			int index = originalFileName.lastIndexOf('.');

			String extension = originalFileName.substring(index + 1);

			for (int i = 0; i < fileType.length; i++) {
				fileType[i] = fileType[i].toUpperCase();
			}

			List<String> arrayAsList = Arrays.asList(fileType);
			if (!arrayAsList.contains(extension.toUpperCase())) {
				String message = "File Type " + extension + "is not valid";
				throw new FileManagerException(message);
			}

			// Check for maximum size of file
			long maxAllowedSizeInBytes = parseMaxFileSize(maxSize);

			if (file.getSize() > maxAllowedSizeInBytes) {
				String message = "File size exceeds the allowed limit.";
				throw new FileManagerException(message);
			}

			// Get the file name from the uploaded file
			originalFileName = file.getOriginalFilename();
			assert originalFileName != null;

			// Generate a random UUID
			String randomUUIDPart = UUID.randomUUID().toString().substring(0, 8);
			String saveFileName = randomUUIDPart + "__" + originalFileName;
			// Get the server name and port number dynamically
			String serverName = getServerName();
			int port = getServerPort();

			// Create the new filename with server name, port, and UUID
			String uniqueFileName = "http://" + "" + serverName + ":" + port + "/" + "file/get?fileName="
					+ saveFileName;

			String filePath = this.uploadDir + File.separator + saveFileName;

			// Create a new file in the server directory
			File serverFile = new File(filePath);
			try (FileOutputStream fos = new FileOutputStream(serverFile)) {
				fos.write(file.getBytes());
			}

			return new ResponseEntity<>(new ApiResponse(uniqueFileName, LocalDateTime.now(), saveFileName),
					HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			String message = "Error occurred while uploading the file.";
			throw new FileManagerException(message);
		}
	}

	public ResponseEntity<Resource> downloadFile(String fileName) throws FileManagerException {
		try {
			Path filePath = Paths.get(this.uploadDir).resolve(fileName);
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists() && resource.isReadable()) {
				// Get the media type based on the file extension
				MediaType mediaType = MediaTypeFactory.getMediaType(resource)
						.orElse(MediaType.APPLICATION_OCTET_STREAM);

				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + resource.getFilename() + "\"")
						.contentType(mediaType).body(resource);
			} else {
				throw new FileManagerException("File not found or could not be read.");
			}
		} catch (IOException e) {
			throw new FileManagerException("Error occurred while getting the file resource.");
		}
	}

	public ResponseEntity<?> deleteFile(String fileName) throws FileManagerException {
		try {
			String filePath = this.uploadDir + File.separator + fileName;
			File fileToDelete = new File(filePath);

			if (fileToDelete.exists()) {
				if (fileToDelete.delete()) {
					return new ResponseEntity<>(new ApiResponse("File Removed", LocalDateTime.now()), HttpStatus.OK);
				} else {
					throw new FileManagerException("Error occurred while deleting the file.");
				}
			} else {
				throw new FileManagerException("File not found or already deleted.");
			}
		} catch (Exception e) {
			throw new FileManagerException(e.getMessage());
		}
	}

	private String getServerName() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
			return "Unknown IP Address";
		}
	}

	private int getServerPort() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request.getServerPort();
	}

	private long parseMaxFileSize(String maxFileSize) {
		if (maxFileSize == null || maxFileSize.isEmpty()) {
			return -1;
		}

		long multiplier = 1;
		char lastChar = maxFileSize.charAt(maxFileSize.length() - 2);

		if (Character.isDigit(lastChar)) {
			return Long.parseLong(maxFileSize);
		}

		String numericPart = maxFileSize.substring(0, maxFileSize.length() - 2);
		long numericValue = Long.parseLong(numericPart);

		if (Character.toUpperCase(lastChar) == 'K') {
			multiplier = 1024;
		} else if (Character.toUpperCase(lastChar) == 'M') {
			multiplier = 1024 * 1024;
		} else if (Character.toUpperCase(lastChar) == 'G') {
			multiplier = 1024 * 1024 * 1024;
		}

		return numericValue * multiplier;
	}
}
