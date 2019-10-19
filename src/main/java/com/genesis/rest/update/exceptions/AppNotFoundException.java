package com.genesis.rest.update.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1277272307495586716L;

	public AppNotFoundException(String msg) {
		super(msg);
	}
}
