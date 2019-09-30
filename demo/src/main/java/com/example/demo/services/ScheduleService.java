package com.example.demo.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;
import com.example.demo.repositories.ScheduleRepository;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Service
public class ScheduleService {
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
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
			
			Calendar sundayCalendar = new GregorianCalendar();
			Calendar endCalendar = new GregorianCalendar();
			Calendar saturdayCalendar = new GregorianCalendar();
			sundayCalendar.setTime(inputStartDate);
			endCalendar.setTime(inputEndDate);
			saturdayCalendar.setTime(inputStartDate);
			sundayCalendar.set(Calendar.DAY_OF_WEEK, 1);
			saturdayCalendar.set(Calendar.DAY_OF_WEEK, 7);
			
			boolean first = true;
			Schedule schedule;
			Date saturday;
			Date sunday;
			
			while(true) {
				if(endCalendar.compareTo(saturdayCalendar) > 0) {
					saturday = new Date(saturdayCalendar.getTimeInMillis());
					if(first) {
						//end > 토요일, 처음 -> SD+ST+토요일D
						schedule = new Schedule("test@naver.com", title, contents, startDate+startTime
								, dateFormat.format(saturday), util.calculateTerm(inputStartDate, inputEndDate));
					}
					else {
						//end > 토요일, 처음X -> 일요일D+토요일D
						sunday = new Date(sundayCalendar.getTimeInMillis());
						schedule = new Schedule("test@naver.com", title, contents, dateFormat.format(sunday)
								, dateFormat.format(saturday), util.calculateTerm(sunday, saturday));
					}
					//insert 하고, saturday and sunday 세팅
					scheduleRepository.save(schedule);
					if(saturdayCalendar.get(Calendar.WEEK_OF_MONTH) == saturdayCalendar.getMaximum(Calendar.WEEK_OF_MONTH) 
							&& saturdayCalendar.get(Calendar.MONTH) == 11) {
						saturdayCalendar.set(Calendar.YEAR, saturdayCalendar.get(Calendar.YEAR) +1);
						saturdayCalendar.set(Calendar.MONTH, 0);
						saturdayCalendar.set(Calendar.WEEK_OF_MONTH, 1);
						sundayCalendar.set(Calendar.YEAR, saturdayCalendar.get(Calendar.YEAR));
						sundayCalendar.set(Calendar.MONTH, saturdayCalendar.get(Calendar.MONTH));
					}
					else if(saturdayCalendar.get(Calendar.WEEK_OF_MONTH) == saturdayCalendar.getMaximum(Calendar.WEEK_OF_MONTH)) {
						saturdayCalendar.set(Calendar.MONTH, saturdayCalendar.get(Calendar.MONTH) +1);
						saturdayCalendar.set(Calendar.WEEK_OF_MONTH, 1);
						sundayCalendar.set(Calendar.MONTH, saturdayCalendar.get(Calendar.MONTH));
					}
					else {
						saturdayCalendar.set(Calendar.WEEK_OF_MONTH, saturdayCalendar.get(Calendar.WEEK_OF_MONTH) +1);
					}
					sundayCalendar.set(Calendar.WEEK_OF_MONTH, saturdayCalendar.get(Calendar.WEEK_OF_MONTH));
					first = false;
				}
				else {
					if(first) {
						//end <= 토요일, 처음 >> SD+ST+ED+ET
						schedule = new Schedule("test@naver.com", title, contents, startDate+startTime, endDate+endTime
								, util.calculateTerm(inputStartDate, inputEndDate));
						scheduleRepository.save(schedule);
						break;
					}
					else {
						//end <= 토요일, 처음X >> 일D+ED+ET
						sunday = new Date(sundayCalendar.getTimeInMillis());
						schedule = new Schedule("test@naver.com", title, contents, dateFormat.format(sunday), endDate+endTime
								, util.calculateTerm(sunday, inputEndDate));
						scheduleRepository.save(schedule);
						break;
					}
				}
			}
			log.info("Insert Schedule Success");
		}
		catch(Exception e) {
			log.error("ScheduleService.insertSchedule error!!");
			log.error(e);
		}
	}
	
	public List<ScheduleDto> getSchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findByYearMonth(util.getStartDateOfMonth(year, month)
				, util.getEndDateOfMonth(year, month)));
	}
	
	public List<ScheduleDto> getOneDaySchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findOneDayByYearMonth(util.getStartDateOfMonth(year, month)
				, util.getEndDateOfMonth(year, month)));
	}
	
	public void deleteById(int id) {
		try {
			scheduleRepository.deleteById(id);
			log.info("Delete Schedule Success. Id : "+ id);
		}
		catch(Exception e) {
			log.error("ScheduleService.deleteById error!!");
			log.error(e);
		}
	}

}
