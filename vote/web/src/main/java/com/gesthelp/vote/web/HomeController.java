package com.gesthelp.vote.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.java.Log;

@Controller
@Log
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}
}
