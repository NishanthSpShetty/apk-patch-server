package com.genesis.rest.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

	// file.upload-base-dir=uploads
	private String uploadBaseDir;

	// file.patch-base-dir=patches
	private String patchBaseDir;

	public String getUploadBaseDir() {
		return uploadBaseDir;
	}

	public void setUploadBaseDir(String patchesBaseDir) {
		this.uploadBaseDir = patchesBaseDir;
	}

	public String getPatchBaseDir() {
		return patchBaseDir;
	}

	public void setPatchBaseDir(String patchesBaseDir) {
		this.patchBaseDir = patchesBaseDir;
	}
}