package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.services.ScheduleService;

@Controller
public class MainController {
	@Autowired
	private ScheduleService scheduleService;
	
	@RequestMapping(value="/")
	public String getMainPage(Model model) {
		List<ScheduleDto> schedules = scheduleService.getSchedulesByYearMonth();
		List<ScheduleDto> oneDaySchedules = scheduleService.getOneDaySchedulesByYearMonth();
		model.addAttribute("schedules", schedules);
		model.addAttribute("oneDaySchedules", oneDaySchedules);
		
		return "/main/testPage";
	}
}
