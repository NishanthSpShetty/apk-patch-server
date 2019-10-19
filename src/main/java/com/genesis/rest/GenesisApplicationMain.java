package com.genesis.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import com.genesis.rest.file.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class GenesisApplicationMain {

	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(GenesisApplicationMain.class, args);
	
	
		
		
	}

}
