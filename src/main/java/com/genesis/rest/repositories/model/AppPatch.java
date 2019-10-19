package com.genesis.rest.repositories.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="app_patch")
public class AppPatch {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "app_id")
	private Application app;

	@ManyToOne
	@JoinColumn(name = "from_apk_id")
	private Apk fromApk;

	@ManyToOne
	@JoinColumn(name = "to_apk_id")
	private Apk toApk;

	private String patchFileName;

	public String getPatchFileName() {
		return patchFileName;
	}

	public void setPatchFileName(String patchFileName) {
		this.patchFileName = patchFileName;
	}

	@Override
	public String toString() {
		return "[ ID :" + id + ", App ID : " + app.getId() + ", From : " + fromApk.getId() + " --> To : "
				+ toApk.getId() + "]";
	}
}