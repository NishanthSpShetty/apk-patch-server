package com.genesis.rest.file.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.genesis.rest.file.FileStorageProperties;
import com.genesis.rest.file.exception.FileStorageException;
import com.genesis.rest.file.exception.MyFileNotFoundException;
import com.genesis.rest.repositories.model.AppPatch;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	private final Path patchfileStorageLocation;

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {

		logger.info("Initializing the file storage service...");

		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadBaseDir()).toAbsolutePath().normalize();
		this.patchfileStorageLocation = Paths.get(fileStorageProperties.getPatchBaseDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
			Files.createDirectories(this.patchfileStorageLocation);

		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(String appName, Long version, MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// setup target dir
			Path targetDir = this.fileStorageLocation.resolve(appName);

			if (!targetDir.toFile().exists()) {
				Files.createDirectory(targetDir);
			}

			// strip the extension to add version id
			fileName = fileName.replace(".apk", "");
			// we need to store the apk in particular app folders, but if the apk has same
			// name we will be in trouble so we are adding version id
			fileName = fileName + "_" + version + ".apk";
			String fileNameWithAppBase = appName + "/" + fileName;

			logger.info("Copying request stream to file storage ");
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileNameWithAppBase);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			logger.debug("File copied to target storage location");
			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadPatchFileAsResource(String appName, AppPatch patch) {
		try {
			Path filePath = this.patchfileStorageLocation.resolve(appName + "/" + patch.getPatchFileName()).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + filePath.toFile());
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found ", ex);
		}
	}
}