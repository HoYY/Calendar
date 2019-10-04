package com.example.demo.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.services.MailServiceImpl;
import com.example.demo.services.OneDayScheduleServiceImpl;
import com.example.demo.utils.Util;

@Controller
@RequestMapping(value="/email")
public class MailController {
	private static final Logger log = LogManager.getLogger(MailController.class);
	
	@Autowired
	private MailServiceImpl mailServiceImpl;
	
	@Autowired
	private OneDayScheduleServiceImpl oneDayScheduleServiceImpl;
	
	@Autowired
	private Util util;
	
	@PostMapping("")
	public String sendMail(HttpServletRequest request, RedirectAttributes redirectAttr) {
		String target = request.getParameter("target");
		String isDaily = request.getParameter("isDaily");
		String scheduleTitle = request.getParameter("scheduleTitle");
		String referer = request.getHeader("Referer");
		
		if(referer == null) 
			return "redirect:/";
		String[] splittedReferer = referer.split("\\?");
		redirectAttr.addFlashAttribute("isDaily", isDaily);
		
		if(!util.isEmail(target)) {
			FieldError fieldError = new FieldError("scheduleDto", "target", "이메일 형식에 맞지 않습니다.");
			redirectAttr.addFlashAttribute("fieldError", fieldError);
			return "redirect:"+splittedReferer[0];
		}
		
		try {
			mailServiceImpl.sendMessage(target, scheduleTitle);
		}
		catch(Exception e) {
			log.error("MailController.sendMail error!!");
			log.error(e);
		}
		
		return "redirect:"+splittedReferer[0];
	}
	
	@GetMapping(value="/{scheduleTitle}")
	public String acceptSchedule(@PathVariable String scheduleTitle) {
		oneDayScheduleServiceImpl.acceptScheduleByTitle(scheduleTitle);
		return "redirect:/";
	}
}
