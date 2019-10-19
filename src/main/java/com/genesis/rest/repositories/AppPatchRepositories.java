package com.genesis.rest.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.genesis.rest.repositories.model.AppPatch;

public interface AppPatchRepositories extends CrudRepository<AppPatch, Integer> {

	@Query("select p from AppPatch p where p.app.id= ?1 and p.fromApk.id= ?2 and p.toApk.id= ?3")
	AppPatch findPatchForApk(Integer appId, Integer oldApkId, Integer newApkId);

}
