package com.genesis.rest.file.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

	AtomicLong count = new AtomicLong(0);

	@Autowired
	public SequenceService() {

	}

	public Long getNextSequence() {
		return count.incrementAndGet();

	}

}
