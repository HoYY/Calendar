package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule.Type;
import com.example.demo.services.OneDayScheduleServiceImpl;
import com.example.demo.services.RepeatedScheduleServiceImpl;
import com.example.demo.services.SerialScheduleServiceImpl;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Controller
@RequestMapping(value="/schedules")
public class ScheduleController {
	private static final Logger log = LogManager.getLogger(ScheduleController.class);
	
	@Autowired
	private SerialScheduleServiceImpl  serialScheduleServiceImpl;
	
	@Autowired
	private OneDayScheduleServiceImpl oneDayScheduleServiceImpl;
	
	@Autowired
	private RepeatedScheduleServiceImpl repeatedScheduleServiceImpl;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	@Autowired
	private Util util;
	
	@GetMapping(value="")
	public String getToday() {
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH) + 1;
		int nowDate = calendar.get(Calendar.DAY_OF_MONTH);
		
		return "redirect:/schedules/"+nowYear+"/"+nowMonth+"/"+nowDate;
	}
	
	@GetMapping(value="/{year}/{month}/{date}")
	public String getCalendar(@PathVariable int year, @PathVariable int month, @PathVariable int date, Model model
			, ScheduleDto scheduleDto) {
		Calendar calendar = Calendar.getInstance();

		List<ScheduleDto> serialSchedules = serialScheduleServiceImpl.getSchedulesByYearMonth(year, month);
		List<ScheduleDto> oneDaySchedules = oneDayScheduleServiceImpl.getSchedulesByYearMonth(year, month);
		List<ScheduleDto> repetitionSchedules = repeatedScheduleServiceImpl.getSchedulesByYearMonth(year, month);
		List<ScheduleDto> serialDailySchedules = serialScheduleServiceImpl.getDailySchedulesByYearMonthDate(year, month, date);
		List<ScheduleDto> oneDayDailySchedules = oneDayScheduleServiceImpl.getDailySchedulesByYearMonthDate(year, month, date);
		List<ScheduleDto> repetitionDailySchedules = repeatedScheduleServiceImpl.getDailySchedulesByYearMonthDate(year, month, date);
		
		model.addAttribute("calendar", scheduleUtil.createCalendar(year, month, serialSchedules, oneDaySchedules, repetitionSchedules));
		model.addAttribute("serialDailySchedules", serialDailySchedules);
		model.addAttribute("oneDayDailySchedules", oneDayDailySchedules);
		model.addAttribute("repetitionDailySchedules", repetitionDailySchedules);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("date", date);
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		model.addAttribute("day", calendar.get(Calendar.DAY_OF_WEEK));
		
		return "/main/mainPage";
	}
	
	@GetMapping(value="/{year}/{month}/{date}/{isDaily}")
	public String redirectByDaily(RedirectAttributes redirectAttr, @PathVariable int year, @PathVariable int month, @PathVariable int date
			, @PathVariable String isDaily) {
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		return "redirect:/schedules/"+year+"/"+month+"/"+date;
	}
	
	@PostMapping(value="")
	public String createSchedule(@ModelAttribute("scheduleDto") @Valid ScheduleDto scheduleDto, BindingResult bindingResult
			, HttpServletRequest request, RedirectAttributes redirectAttr) {
		String dayAll = request.getParameter("dayAll");
		String isDaily = request.getParameter("isDaily");
		String repetition = request.getParameter("repetition");
		String referer = request.getHeader("Referer");
		
		if(referer == null) 
			return "redirect:/";
		String[] splittedReferer = referer.split("\\?");
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		
		if(bindingResult.hasErrors()) {
			FieldError fieldError = new FieldError("scheduleDto", "title", "입력 칸을 모두 입력해 주셔야 합니다.");
			redirectAttr.addFlashAttribute("fieldError", fieldError);
			return "redirect:"+splittedReferer[0];
		}
		
		if(!dayAll.equals("true")) {
			if(util.isEmpty(scheduleDto.getStartTime()) || util.isEmpty(scheduleDto.getEndTime())) {
				FieldError fieldError = new FieldError("scheduleDto", "startTime", "입력 칸을 모두 입력해 주셔야 합니다.");
				redirectAttr.addFlashAttribute("fieldError", fieldError);
				return "redirect:"+splittedReferer[0];
			}
		}
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date startDate = dateFormat.parse(scheduleDto.getStartDate());
			Date endDate = dateFormat.parse(scheduleDto.getEndDate());
			if(startDate.getTime() > endDate.getTime()) {
				FieldError fieldError = new FieldError("scheduleDto", "startDate", "일정 시작 날짜는 일정 종료 날짜보다 이전이어야 합니다.");
				redirectAttr.addFlashAttribute("fieldError", fieldError);
				return "redirect:"+splittedReferer[0];
			}
			
			switch(repetition) {
				case "null":
					if(startDate.equals(endDate)) 
						oneDayScheduleServiceImpl.insertSchedule(scheduleDto);
					else
						serialScheduleServiceImpl.insertSchedule(scheduleDto);
					break;
					
				case "everyDay":
					if(!startDate.equals(endDate)) {
						FieldError fieldError = new FieldError("scheduleDto", "startDate", "매일 반복 일정은 시작 날짜와 종료 날짜가 같아야 합니다.");
						redirectAttr.addFlashAttribute("fieldError", fieldError);
						return "redirect:"+splittedReferer[0];
					}
					scheduleDto.setRepetitionType(repetition);
					repeatedScheduleServiceImpl.insertSchedule(scheduleDto);
					break;
					
				case "everyWeek":
					if(util.calculateTerm(startDate, endDate) > 7) {
						FieldError fieldError = new FieldError("scheduleDto", "startDate", "매주 반복 일정은 7일을 넘길 수 없습니다.");
						redirectAttr.addFlashAttribute("fieldError", fieldError);
						return "redirect:"+splittedReferer[0];
					}
					scheduleDto.setRepetitionType(repetition);
					repeatedScheduleServiceImpl.insertSchedule(scheduleDto);
					break;
					
				case "everyMonth":
					if(!scheduleDto.getStartDate().substring(5,7).equals(scheduleDto.getEndDate().substring(5,7))) {
						FieldError fieldError = new FieldError("scheduleDto", "startDate", "매월 반복 일정의 경우 시작 날짜와 종료 날짜는 같은 달이어야 합니다.");
						redirectAttr.addFlashAttribute("fieldError", fieldError);
						return "redirect:"+splittedReferer[0];
					}
					scheduleDto.setRepetitionType(repetition);
					repeatedScheduleServiceImpl.insertSchedule(scheduleDto);
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
		oneDayScheduleServiceImpl.deleteById(id);
		String referer = request.getHeader("Referer");
		String isDaily = request.getParameter("isDaily");
		
		if(referer == null) 
			return "redirect:/";
		String[] splittedReferer = referer.split("\\?");
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		
		return "redirect:"+splittedReferer[0];
	}
}
