package com.genesis.rest.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genesis.rest.repositories.ApkRepositories;
import com.genesis.rest.repositories.AppPatchRepositories;
import com.genesis.rest.repositories.ApplicationRepository;
import com.genesis.rest.repositories.model.Apk;
import com.genesis.rest.repositories.model.AppPatch;
import com.genesis.rest.repositories.model.Application;
import com.genesis.rest.update.exceptions.AppNotFoundException;
import com.genesis.rest.update.payload.UpdateResponse;

@RestController
@RequestMapping("/rest/v1/apk")
public class UpdateController {

	// get the database tables
	@Autowired
	private AppPatchRepositories appPatchRepositories;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private ApkRepositories apkRepositories;

	private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);

	@GetMapping("/app/{appName}/update/{version}")
	public ResponseEntity<UpdateResponse> checkForUpdate(@PathVariable("appName") String appName,
			@PathVariable("version") Integer version) throws AppNotFoundException {

		logger.info("Checking for apk update for app " + appName + ", current version : " + version + " ");
		// check if any update available for the app

		Application app = applicationRepository.findByName(appName);

		if (app == null) {
			throw new AppNotFoundException("invalid app name");
		}

		// get the apk current version
		Apk oldApk = apkRepositories.findByAppVersion(app.getId(), version);

		if (oldApk == null) {
			throw new AppNotFoundException("invalid version ");
		}

		// we have the reference to app and apk,
		// get the all versions ?
//		List<Apk> latestApkList = apkRepositories.findAllLatestVersion(version);

		logger.info("Searching for changes in app " + appName + " , version " + version + ", app id :" + app.getId());
		Apk newApk = apkRepositories.findLatestVersion(app.getId(), version);

		logger.info("We found new versions available for app " + app.getName() + ", Version :"
				+ newApk.getVersionDisplayName() + " [" + newApk.getVersion() + "]");

		// get the latest patch and send it to user
		AppPatch appPatch = appPatchRepositories.findPatchForApk(app.getId(), oldApk.getId(), newApk.getId());
		if (appPatch == null) {

			logger.error("Failed to query the patch details for the apk " + app.getName() + " Old Version " + oldApk
					+ " : New Version " + newApk);
			throw new AppNotFoundException("patch is unavailable at the moment, try again");
		}
		logger.info("Found the new patch file for the update , sending data to client " + appPatch);
		return ResponseEntity.ok()
				.body(new UpdateResponse(app.getName(), newApk.getVersion(), newApk.getVersionDisplayName(),
						newApk.getReleaseDate(), appPatch.getPatchFileName(), appPatch.getId()));
	}

}
