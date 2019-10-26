package com.genesis.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.genesis.rest.bsdiff.BSDiff;
import com.genesis.rest.repositories.ApkRepositories;
import com.genesis.rest.repositories.AppPatchRepositories;
import com.genesis.rest.repositories.model.Apk;
import com.genesis.rest.repositories.model.AppPatch;

public class DeltaGenerationThread extends Thread {

	@Autowired
	private AppPatchRepositories appPatchRepositories;

	@Autowired
	private ApkRepositories apkRepositories;

	Apk apk;

	public DeltaGenerationThread(Apk apk) {
		this.apk = apk;

		if (apkRepositories == null) {
			apkRepositories = (ApkRepositories) DeltaGenerator.sprintContext.getBean(ApkRepositories.class);
			appPatchRepositories = (AppPatchRepositories) DeltaGenerator.sprintContext
					.getBean(AppPatchRepositories.class);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(DeltaGenerationThread.class);

	@Override
	public void run() {
		// get all the available APK in the system by giving version id as 0

		List<Apk> list = apkRepositories.findAllLatestVersion(apk.getApp().getId(), 0);

		String appName = apk.getApp().getName();
		// get the uploads path
		String basePath = DeltaGenerator.PATCHES_BASE_DIR + "/" + appName;

		File dir = new File(basePath);

		if (!dir.exists()) {
			dir.mkdir();
		}

		logger.info("found " + (list.size() - 1) + " old changes");

		list.stream().filter(it -> it.getVersion() != apk.getVersion()).forEach(fromApk -> {
			try {
				generatePatchFile(fromApk, apk);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		});

		logger.info("Delta generation thread completed successfully");
	}

	public void generatePatchFile(Apk fromApk, Apk toApk) throws IOException {

		String appName = toApk.getApp().getName();
		// get the uploads path
		String basePath = "uploads/" + appName;
		logger.info("generating delta between " + fromApk + " and " + toApk);
		String oldApkFilePath = basePath + "/" + fromApk.getApkFileName();
		String newApkFilePath = basePath + "/" + toApk.getApkFileName();

		File oldApkFile = new File(oldApkFilePath);
		File newApkFile = new File(newApkFilePath);

		if (!oldApkFile.exists() || !newApkFile.exists()) {

			logger.error(
					"Looks like one of the APK is not available in the path, please check the following path in the system");

			logger.error("Old APK File " + oldApkFilePath);
			logger.error("New APK File " + newApkFilePath);

		}

		// all good we can proceed with create delta patch file

		String fileName = appName + "_patch_v" + fromApk.getVersion() + "_to_v" + toApk.getVersion() + ".bin";

		File patchFile = new File(DeltaGenerator.PATCHES_BASE_DIR + "/" + appName + "/", fileName);

		patchFile.createNewFile();

		// generate patch
		BSDiff.bsdiff(oldApkFile, newApkFile, patchFile);

		AppPatch appPatch = new AppPatch(toApk.getApp(), fromApk, toApk, fileName);
		appPatchRepositories.save(appPatch);

	}
}
