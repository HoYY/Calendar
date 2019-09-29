package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.services.ScheduleService;
import com.example.demo.utils.Util;

@Controller
@RequestMapping(value="/schedules")
public class ScheduleController {
	@Autowired
	private Util util;
	
	@Autowired
	private ScheduleService scheduleService;
	
	private static final Logger log = LogManager.getLogger(ScheduleController.class);
	
	@PostMapping(value="")
	public String createSchedule(HttpServletRequest request, RedirectAttributes redirectAttr) {
		String inputTitle = request.getParameter("title");
		String inputContents = request.getParameter("contents");
		String inputStartDate = request.getParameter("startDate");
		String inputStartTime = request.getParameter("startTime");
		String inputEndDate = request.getParameter("endDate");
		String inputEndTime = request.getParameter("endTime");
		
		if(util.isEmpty(inputTitle) || util.isEmpty(inputStartDate) || util.isEmpty(inputEndDate)) {
			redirectAttr.addFlashAttribute("message", "empty");
			return "redirect:/";
		}
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date startDate = dateFormat.parse(inputStartDate);
			Date endDate = dateFormat.parse(inputEndDate);
			if(startDate.getTime() > endDate.getTime()) {
				redirectAttr.addFlashAttribute("message", "lateThanEnd");
				return "redirect:/";
			}
		}
		catch(Exception e) {
			log.error("ScheduleController.createSchedule date compare error!!");
			log.error(e);
		}
			
		scheduleService.insertSchedule(inputTitle, inputContents, inputStartDate, inputStartTime
				, inputEndDate, inputEndTime);
		
		return "redirect:/";
	}
}
