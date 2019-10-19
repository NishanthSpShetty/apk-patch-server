package com.genesis.rest.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.genesis.rest.repositories.model.Apk;

public interface ApkRepositories extends CrudRepository<Apk, Integer> {

	Apk findByVersion(Integer version);

	@Query("select a from Apk a where a.app.id =?2 and a.version >?1")
	List<Apk> findAllLatestVersion( Integer appId, Integer version);

	@Query("select a from Apk a where a.id =(select max(b.id) from Apk b where b.version>?2 and b.app.id =?1)")
	Apk findLatestVersion(Integer appId, Integer version);
}
