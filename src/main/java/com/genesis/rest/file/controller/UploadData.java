package com.genesis.rest.file.controller;

public class UploadData {

	private String name;
	private String releaseDate;
	private String versionDisplayName;
	private Integer versionId;
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getVersionDisplayName() {
		return versionDisplayName;
	}

	public void setVersionDisplayName(String versionDisplayName) {
		this.versionDisplayName = versionDisplayName;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
