package com.example.demo.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/schedules")
public class ScheduleController {
	@PostMapping(value="")
	public String createSchedule(HttpServletRequest request) {
		String inputTitle = request.getParameter("title");
		
		return "redirect:/";
	}
}
