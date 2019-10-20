package com.genesis.rest.repositories.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.genesis.rest.HelperUtils;

@Entity
public class Apk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// many versions of apk will map to one application
	@ManyToOne
	@JoinColumn(name = "app_id")
	private Application app;

	private Integer version;
	private String versionDisplayName;
	private String apkFileName;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date releaseDate;

	public Apk() {}
	
	public Apk(Application app, String fileName, Date releaseDate, String versionDisplayName, Integer versionId) {
		this.app = app;
		this.apkFileName = fileName;
		this.releaseDate = releaseDate;
		this.version = versionId;
		this.versionDisplayName = versionDisplayName;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getApkFileName() {
		return apkFileName;
	}

	public void setApkFileName(String apkFileName) {
		this.apkFileName = apkFileName;
	}

	public String getVersionDisplayName() {
		return versionDisplayName;
	}

	public void setVersionDisplayName(String versionDisplayName) {
		this.versionDisplayName = versionDisplayName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Application getApp() {
		return app;
	}

	public void setApp(Application app) {
		this.app = app;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String toString() {

		return "[ Version : " + getVersionDisplayName() + " , Release Date : " + getFormattedDate() + ", Apk :"
				+ getApkFileName() + " ]";
	}

	public String getFormattedDate() {
		return HelperUtils.printableDate(releaseDate);
	}

}
