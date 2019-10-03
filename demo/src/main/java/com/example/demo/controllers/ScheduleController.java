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
import com.example.demo.models.Schedule.Type;
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
		
		List<ScheduleDto> serialSchedules = scheduleService.getSchedulesByYearMonthType(year, month, Type.SERIAL);
		List<ScheduleDto> oneDaySchedules = scheduleService.getOneDaySchedulesByYearMonth(year, month);
		List<ScheduleDto> repetitionSchedules = scheduleService.getSchedulesByYearMonthType(year, month, Type.REPETITION);
		List<ScheduleDto> serialDailySchedules = scheduleService.getDailySchedulesByYearMonthDateType(year, month, date, Type.SERIAL);
		List<ScheduleDto> oneDayDailySchedules = scheduleService.getDailySchedulesByYearMonthDateType(year, month, date, Type.ONEDAY);
		List<ScheduleDto> repetitionDailySchedules = scheduleService.getDailySchedulesByYearMonthDateType(year, month, date, Type.REPETITION);
		
		model.addAttribute("calendar", scheduleUtil.createCalendar(year, month, serialSchedules, oneDaySchedules, repetitionSchedules));
		model.addAttribute("serialDailySchedules", serialDailySchedules);
		model.addAttribute("oneDayDailySchedules", oneDayDailySchedules);
		model.addAttribute("repetitionDailySchedules", repetitionDailySchedules);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("date", date);
		
		if(isDaily != null)
			model.addAttribute("isDaily", isDaily);
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		model.addAttribute("day", calendar.get(Calendar.DAY_OF_WEEK));
		
		return "/main/mainPage";
	}
	
	@PostMapping(value="")
	public String createSchedule(HttpServletRequest request, RedirectAttributes redirectAttr) {
		String inputTitle = request.getParameter("title");
		String inputContents = request.getParameter("contents");
		String inputStartDate = request.getParameter("startDate");
		String inputStartTime = request.getParameter("startTime");
		String inputEndDate = request.getParameter("endDate");
		String inputEndTime = request.getParameter("endTime");
		String dayAll = request.getParameter("dayAll");
		String isDaily = request.getParameter("isDaily");
		String repetition = request.getParameter("repetition");
		String referer = request.getHeader("Referer");
		
		if(referer == null) 
			return "redirect:/";
		String[] splittedReferer = referer.split("\\?");
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		
		if(dayAll.equals("true")) {
			if(util.isEmpty(inputTitle) || util.isEmpty(inputContents) || util.isEmpty(inputStartDate) || util.isEmpty(inputEndDate)) {
				redirectAttr.addFlashAttribute("message", "empty");
				return "redirect:"+splittedReferer[0];
			}
		}
		else {
			if(util.isEmpty(inputTitle) || util.isEmpty(inputContents) || util.isEmpty(inputStartDate) || util.isEmpty(inputEndDate)
					|| util.isEmpty(inputStartTime) || util.isEmpty(inputEndTime)) {
				redirectAttr.addFlashAttribute("message", "empty");
				return "redirect:"+splittedReferer[0];
			}
		}
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date startDate = dateFormat.parse(inputStartDate);
			Date endDate = dateFormat.parse(inputEndDate);
			if(startDate.getTime() > endDate.getTime()) {
				redirectAttr.addFlashAttribute("message", "lateThanEnd");
				return "redirect:"+splittedReferer[0];
			}
			
			switch(repetition) {
				case "null":
					scheduleService.insertSchedule(inputTitle, inputContents, inputStartDate, inputStartTime
							, inputEndDate, inputEndTime);
					break;
					
				case "everyDay":
					if(!startDate.equals(endDate)) {
						redirectAttr.addFlashAttribute("message", "notSameDate");
						return "redirect:"+splittedReferer[0];
					}
					scheduleService.insertScheduleEveryDay(inputTitle, inputContents, inputStartDate, inputStartTime
							, inputEndDate, inputEndTime);
					break;
					
				case "everyWeek":
					if(util.calculateTerm(startDate, endDate) > 7) {
						redirectAttr.addFlashAttribute("message", "longerThanWeek");
						return "redirect:"+splittedReferer[0];
					}
					scheduleService.insertScheduleEveryWeek(inputTitle, inputContents, inputStartDate, inputStartTime
							, inputEndDate, inputEndTime);
					break;
					
				case "everyMonth":
					break;
			}
		}
		catch(Exception e) {
			log.error("ScheduleController.createSchedule date processing and insert schedule error!!");
			log.error(e);
		}
		
		return "redirect:"+splittedReferer[0];
	}
	
	@DeleteMapping(value="/{id}")
	public String deleteSchedule(@PathVariable int id, HttpServletRequest request, RedirectAttributes redirectAttr) {
		scheduleService.deleteById(id);
		String referer = request.getHeader("Referer");
		String isDaily = request.getParameter("isDaily");
		
		if(referer == null) 
			return "redirect:/";
		String[] splittedReferer = referer.split("\\?");
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		
		return "redirect:"+splittedReferer[0];
	}
}
