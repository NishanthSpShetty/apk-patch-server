package com.genesis.rest.update.payload;

import java.util.Date;

/**
 * 
 * @author nishanth
 *
 *         response data to be sent to update check api
 */
public class UpdateResponse {

	private String name;
	private Integer version;
	private String versionDisplayName;
	private Date releaseDate;
	private Integer patchId;
	private String patchFile;

	public UpdateResponse(String name, Integer version, String versionDisplayName, Date releaseDate, String patchFile,
			Integer id) {
		this.name = name;
		this.releaseDate = releaseDate;
		this.version = version;
		this.versionDisplayName = versionDisplayName;
		this.setPatchFile(patchFile);
		this.setPatchId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getVersionDisplayName() {
		return versionDisplayName;
	}

	public void setVersionDisplayName(String versionDisplayName) {
		this.versionDisplayName = versionDisplayName;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Integer getPatchId() {
		return patchId;
	}

	public void setPatchId(Integer patchId) {
		this.patchId = patchId;
	}

	public String getPatchFile() {
		return patchFile;
	}

	public void setPatchFile(String patchFile) {
		this.patchFile = patchFile;
	}

}
