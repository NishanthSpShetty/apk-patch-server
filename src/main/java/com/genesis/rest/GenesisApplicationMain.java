package com.genesis.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import com.genesis.rest.file.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class GenesisApplicationMain {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(GenesisApplicationMain.class, args);

		// start the delta generation service
		DeltaGenerator.init(ctx);
		DeltaGenerator.run();
		

	}

}
