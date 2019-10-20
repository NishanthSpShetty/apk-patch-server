package com.genesis.rest.file.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genesis.rest.DeltaGenerator;
import com.genesis.rest.HelperUtils;
import com.genesis.rest.file.service.FileStorageService;
import com.genesis.rest.file.service.SequenceService;
import com.genesis.rest.repositories.ApkRepositories;
import com.genesis.rest.repositories.AppPatchRepositories;
import com.genesis.rest.repositories.ApplicationRepository;
import com.genesis.rest.repositories.FileTableRepositories;
import com.genesis.rest.repositories.model.Apk;
import com.genesis.rest.repositories.model.AppPatch;
import com.genesis.rest.repositories.model.Application;
import com.genesis.rest.repositories.model.FileTable;
import com.genesis.rest.update.exceptions.AppNotFoundException;

@RestController
@RequestMapping("rest/v1/apk")
public class ApkController {

	private static final Logger logger = LoggerFactory.getLogger(ApkController.class);

	@Autowired
	private SequenceService sequenceService;

	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private AppPatchRepositories appPatchRepositories;

	@Autowired
	private ApkRepositories apkRepositories;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileTableRepositories fileTableRepository;

	@PostMapping("/app/{appName}/upload/file")
	public Integer uploadFile(@PathVariable("appName") String appName, @RequestParam("file") MultipartFile file) {

		String fileName = fileStorageService.storeFile(appName, sequenceService.getNextSequence(), file);

		logger.info("Upload file request recieved , processing file " + file + " for Application : " + appName);

		FileTable fil = fileTableRepository.save(new FileTable(fileName));

		return fil.getId();

	}

	@PostMapping("/app/{appName}/upload/meta/{id}")
	public String uploadFile(@PathVariable("appName") String appName, @PathVariable("id") Integer fileId,
			@RequestBody UploadData data) {

		logger.info("Received apk meta data update for " + appName + " -> " + fileId);
		// update the database
		Application app = applicationRepository.findByName(appName);

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

		try {
			DeltaGenerator.triggerEvent(apk);
		} catch (InterruptedException e) {

			// need to handle this scenario carefully
			// trigger again after sometime
			e.printStackTrace();
		}

		// we should trigger patch builder here

		return "{\"message\":\"ok\"}";
	}

	class Apps {
	}

	@GetMapping("/apps")
	public List<Apps> listAllApps() {

		return Arrays.asList(new Apps());
	}

	
	
	@GetMapping("/app/{appName}/patch/{id:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("appName") String appName,
			@PathVariable("id") Integer patchId, HttpServletRequest request) throws AppNotFoundException {

		logger.info("Patch download request recieved for app : " + appName);

		// get the appropriate ap[

		Application app = applicationRepository.findByName(appName);

		if (app == null) {
			throw new AppNotFoundException("invalid app name");
		}

		// get the patch file name

		AppPatch patch = appPatchRepositories.findById(patchId).orElseThrow(); 
		// Load file as Resource
		Resource resource = fileStorageService.loadPatchFileAsResource(appName, patch);

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
