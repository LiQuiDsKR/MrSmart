package com.care4u.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	private final Logger logger = Logger.getLogger(MainController.class);
	
	
	@GetMapping(value = "/")
	public String main(Model model){
		return "index";
	}
	
	@GetMapping(value = "/load_member")
	public String price(Model model){
		
		return "load";
	}
}