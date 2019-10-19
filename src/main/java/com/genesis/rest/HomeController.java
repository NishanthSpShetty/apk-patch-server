package com.genesis.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	
	@RequestMapping("/")
	@ResponseBody
	public String index() {
		return "<html> <head>"
				+ "<title> Genesis </title>"
				+ "</head><body>"
				+"<div style=\"border:1px solid;border-radius:10%;width:30%;margn-left:20%;align:center;margin:10%;padding:7%\">"
				+ "<h1> <p> Dev-Repo Service</p> </h1>"
				+ "<h3> <div> APK hosting and delta patching service</div></h3>"
				+ "</div></body>"
				+ "</html>";
	}
	
	
}
