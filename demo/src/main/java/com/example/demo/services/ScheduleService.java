package com.example.demo.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;
import com.example.demo.repositories.ScheduleRepository;
import com.example.demo.utils.Util;

@Service
public class ScheduleService {
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private Util util;
	
	private static final Logger log = LogManager.getLogger(ScheduleService.class);

	public void insertSchedule(String title, String contents, String startDate, String startTime
			, String endDate, String endTime) {
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date inputStartDate = dateFormat.parse(startDate);
			Date inputEndDate = dateFormat.parse(endDate);
			int term = (int) ((inputEndDate.getTime() - inputStartDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
			Schedule schedule = new Schedule("test@naver.com", title, contents, startDate+startTime, endDate+endTime, term);
			
			scheduleRepository.save(schedule);
			log.info("Insert Schedule Success");
		}
		catch(Exception e) {
			log.error("ScheduleService.insertSchedule error!!");
			log.error(e);
		}
	}
	
	public List<ScheduleDto> getSchedulesByYearMonth() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		String nowYearMonth = dateFormat.format(date);
		return util.toScheduleDtos(scheduleRepository.findByYearMonth(nowYearMonth));
	}
	
	public List<ScheduleDto> getOneDaySchedulesByYearMonth() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		String nowYearMonth = dateFormat.format(date);
		return util.toScheduleDtos(scheduleRepository.findOneDayByYearMonth(nowYearMonth));
	}

}
