package com.genesis.rest;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.genesis.rest.repositories.model.Apk;

public class DeltaGenerator {

	private static final Logger logger = LoggerFactory.getLogger(DeltaGenerator.class);

	public static final String PATCHES_BASE_DIR = "patches";

	private static LinkedBlockingQueue<Apk> queue = new LinkedBlockingQueue<>(100);

	static ConfigurableApplicationContext sprintContext = null;

	public static void init(ConfigurableApplicationContext ctx) {
		logger.info("Initializing delta generation event listener service");
		sprintContext = ctx;
		File baseFile = new File(PATCHES_BASE_DIR);
		if (!baseFile.exists()) {
			baseFile.mkdir();
		}

	}

	public static void run() {

		logger.info("Started delta generation event listener");

		while (true) {
			try {
				final Apk apk = queue.take();

				// start the delta patch generation thread for each apk put on the queue

				new DeltaGenerationThread(apk).start();

				logger.info("Started delta patch generation thread for " + apk.getApp().getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static void triggerEvent(Apk apk) throws InterruptedException {

		queue.put(apk);
	}

}
