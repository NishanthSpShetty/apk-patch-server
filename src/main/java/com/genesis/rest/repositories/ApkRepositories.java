package com.genesis.rest.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.genesis.rest.repositories.model.Apk;

public interface ApkRepositories extends CrudRepository<Apk, Integer> {

	@Query("select a from Apk a where a.app.id =?1 and a.version=?2")
	Apk findByAppVersion(Integer appId, Integer version);

	@Query("select a from Apk a where a.app.id =?1 and a.version >?2")
	List<Apk> findAllLatestVersion(Integer appId, Integer version);

	@Query("select a from Apk a where a.id =(select max(b.id) from Apk b where b.version>?2 and b.app.id =?1)")
	Apk findLatestVersion(Integer appId, Integer version);
}
