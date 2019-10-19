package com.genesis.rest.file.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.genesis.rest.HelperUtils;
import com.genesis.rest.file.UploadFileResponse;
import com.genesis.rest.file.service.FileStorageService;
import com.genesis.rest.file.service.SequenceService;
import com.genesis.rest.repositories.ApkRepositories;
import com.genesis.rest.repositories.AppPatchRepositories;
import com.genesis.rest.repositories.ApplicationRepository;
import com.genesis.rest.repositories.FileTableRepositories;
import com.genesis.rest.repositories.model.Apk;
import com.genesis.rest.repositories.model.Application;
import com.genesis.rest.repositories.model.FileTable;

@RestController
@RequestMapping("rest/v1/apk")
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private SequenceService sequenceService;

	@Autowired
	private AppPatchRepositories appPatchRepositories;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private ApkRepositories apkRepositories;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileTableRepositories fileTableRepository;

	@PostMapping("/app/{appName}/upload/file")
	public Integer uploadFile(@PathVariable("appName") String appName, @RequestParam("file") MultipartFile file) {

		String fileName = fileStorageService.storeFile(appName, sequenceService.getNextSequence(), file);

		logger.info("Upload file request recieved , processing file " + file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(fileName)
				.toUriString();

		logger.info("Download URI for file : " + fileDownloadUri);
		FileTable fil = fileTableRepository.save(new FileTable(fileName));

		return fil.getId();

	}

	@PostMapping("/app/{appName}/upload/meta/{id}")
	public String uploadFile(@PathVariable("appName") String appName, @RequestParam("id") Integer fileId) {

		// update the database
		Application app = applicationRepository.findByName(appName);

		UploadData data = new UploadData();

		if (app == null) {
			// create new application profile
			app = new Application(appName, data.getCategory());
			applicationRepository.save(app);
		}

		FileTable fil = fileTableRepository.findById(fileId).get();

		// convert to Date object from the json str repr
		Date toDate = HelperUtils.toUtilDate(data.getReleaseDate());

		// create new Apk
		Apk apk = new Apk(app, fil.getName(), toDate, data.getVersionDisplayName(), data.getVersionId());

		apkRepositories.save(apk);

		// we should trigger patch builder here

		return "";
	}

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

		logger.info("Download request recieved for file : " + fileName);

		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
