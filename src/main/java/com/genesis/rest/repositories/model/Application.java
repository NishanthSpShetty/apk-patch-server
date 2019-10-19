package com.genesis.rest.repositories.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * 
 * @author nishanth
 *
 *
 *
 *         Application Table containing generic application information such as
 *         id, name and category <br>
 *         Can be extended to add more specification
 */

@Entity
public class Application {

	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
