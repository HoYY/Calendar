package com.example.demo.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.services.ScheduleService;
import com.example.demo.utils.ScheduleUtil;

@Controller
public class MainController {
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	@RequestMapping(value="/")
	public String getMainPage(Model model) {
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH) + 1;
		List<ScheduleDto> serialSchedules = scheduleService.getSerialSchedulesByYearMonth(nowYear, nowMonth);
		List<ScheduleDto> oneDaySchedules = scheduleService.getOneDaySchedulesByYearMonth(nowYear, nowMonth);
		model.addAttribute("calendar", scheduleUtil.createCalendar(nowYear, nowMonth, serialSchedules, oneDaySchedules));
		return "/main/testPage";
	}
}
