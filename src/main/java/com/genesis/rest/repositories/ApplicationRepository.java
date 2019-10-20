package com.genesis.rest.repositories;

import org.springframework.data.repository.CrudRepository;

import com.genesis.rest.repositories.model.Application;

/**
 * 
 * @author nishanth
 *
 *         manage application table
 */
public interface ApplicationRepository extends CrudRepository<Application, Integer> {

	Application findByName(String name);

}
