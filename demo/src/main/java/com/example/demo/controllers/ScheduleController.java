package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.services.ScheduleService;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Controller
@RequestMapping(value="/schedules")
public class ScheduleController {
	@Autowired
	private Util util;
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	private static final Logger log = LogManager.getLogger(ScheduleController.class);
	
	@GetMapping(value="/{year}/{month}/{date}")
	public String getCalendar(@PathVariable int year, @PathVariable int month, @PathVariable int date, Model model
			, HttpServletRequest request) {
		Calendar calendar = Calendar.getInstance();
		String isDaily = request.getParameter("isDaily");
		List<ScheduleDto> serialSchedules = scheduleService.getSerialSchedulesByYearMonth(year, month);
		List<ScheduleDto> oneDaySchedules = scheduleService.getOneDaySchedulesByYearMonth(year, month);
		List<ScheduleDto> serialDailySchedules = scheduleService.getSerialDailySchedulesByYearMonthDate(year, month, date);
		List<ScheduleDto> oneDayDailySchedules = scheduleService.getOneDayDailySchedulesByYearMonthDate(year, month, date);
		model.addAttribute("calendar", scheduleUtil.createCalendar(year, month, serialSchedules, oneDaySchedules));
		model.addAttribute("serialDailySchedules", serialDailySchedules);
		model.addAttribute("oneDayDailySchedules", oneDayDailySchedules);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("date", date);
		model.addAttribute("isDaily", isDaily);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		model.addAttribute("day", calendar.get(Calendar.DAY_OF_WEEK));
		
		return "/main/testPage";
	}
	
	@PostMapping(value="")
	public String createSchedule(HttpServletRequest request, RedirectAttributes redirectAttr) {
		String inputTitle = request.getParameter("title");
		String inputContents = request.getParameter("contents");
		String inputStartDate = request.getParameter("startDate");
		String inputStartTime = request.getParameter("startTime");
		String inputEndDate = request.getParameter("endDate");
		String inputEndTime = request.getParameter("endTime");
		String referer = request.getHeader("Referer");
		
		if(util.isEmpty(inputTitle) || util.isEmpty(inputContents) || util.isEmpty(inputStartDate) || util.isEmpty(inputEndDate)) {
			redirectAttr.addFlashAttribute("message", "empty");
			return "redirect:"+referer;
		}
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date startDate = dateFormat.parse(inputStartDate);
			Date endDate = dateFormat.parse(inputEndDate);
			if(startDate.getTime() > endDate.getTime()) {
				redirectAttr.addFlashAttribute("message", "lateThanEnd");
				return "redirect:"+referer;
			}
		}
		catch(Exception e) {
			log.error("ScheduleController.createSchedule date compare error!!");
			log.error(e);
		}
			
		scheduleService.insertSchedule(inputTitle, inputContents, inputStartDate, inputStartTime
				, inputEndDate, inputEndTime);
		
		return "redirect:"+referer;
	}
	
	@DeleteMapping(value="/{id}")
	public String deleteSchedule(@PathVariable int id, HttpServletRequest request) {
		scheduleService.deleteById(id);
		String[] referer = request.getHeader("Referer").split("\\?");
		String isDaily = request.getParameter("isDaily");
		return "redirect:"+referer[0]+"?isDaily="+isDaily;
	}
}
