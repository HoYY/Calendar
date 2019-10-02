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
import com.example.demo.models.Schedule.Type;
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
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

	public void insertSchedule(String title, String contents, String startDate, String startTime
			, String endDate, String endTime) {
		try{
			Date inputStartDate = dateFormat.parse(startDate);
			Date inputEndDate = dateFormat.parse(endDate);
			
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
								, dateFormat.format(saturday), util.calculateTerm(inputStartDate, saturday), Type.SERIAL);
					}
					else {
						//end > 토요일, 처음X -> 일요일D+토요일D
						sunday = new Date(sundayCalendar.getTimeInMillis());
						schedule = new Schedule("test@naver.com", title, contents, dateFormat.format(sunday)
								, dateFormat.format(saturday), util.calculateTerm(sunday, saturday), Type.SERIAL);
					}
					//insert 하고, saturday and sunday 세팅
					scheduleRepository.save(schedule);
					util.jumpOneWeek(sundayCalendar, saturdayCalendar);
					first = false;
				}
				else {
					if(first) {
						if(startDate.equals(endDate)) {
							//end = 토요일, 처음 >> SD+ST+ED+ET
							schedule = new Schedule("test@naver.com", title, contents, startDate+startTime, endDate+endTime
									, util.calculateTerm(inputStartDate, inputEndDate), Type.ONEDAY);
						}
						else {
							//end < 토요일, 처음 >> SD+ST+ED+ET
							schedule = new Schedule("test@naver.com", title, contents, startDate+startTime, endDate+endTime
									, util.calculateTerm(inputStartDate, inputEndDate), Type.SERIAL);
						}
						scheduleRepository.save(schedule);
						break;
					}
					else {
						//end <= 토요일, 처음X >> 일D+ED+ET
						sunday = new Date(sundayCalendar.getTimeInMillis());
						schedule = new Schedule("test@naver.com", title, contents, dateFormat.format(sunday), endDate+endTime
								, util.calculateTerm(sunday, inputEndDate), Type.SERIAL);
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
	
	public List<ScheduleDto> getSerialSchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findSerialByRangeAndType(Type.SERIAL, 
				util.getStartDateOfMonth(year, month), util.getEndDateOfMonth(year, month)));
	}
	
	public List<ScheduleDto> getOneDaySchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findOneDayByRangeAndType(Type.ONEDAY,
				util.getStartDateOfMonth(year, month), util.getEndDateOfMonth(year, month)));
	}
	
	public List<ScheduleDto> getSerialDailySchedulesByYearMonthDate(int year, int month, int date) {
		return scheduleUtil.toDtoList(scheduleRepository.findDailyScheduleByDateAndType(
				Type.SERIAL, util.getYYYYMMDD(year, month, date)));
	}
	
	public List<ScheduleDto> getOneDayDailySchedulesByYearMonthDate(int year, int month, int date) {
		return scheduleUtil.toDtoList(scheduleRepository.findDailyScheduleByDateAndType(
				Type.ONEDAY, util.getYYYYMMDD(year, month, date)));
	}
	
	public void deleteById(int id) {
		try {
			Schedule schedule = scheduleRepository.findById(id);
			scheduleRepository.deleteByTitleAndContentsAndType(schedule.getTitle(), schedule.getContents(), schedule.getType());
			log.info("Delete Schedule Success. Title : "+ schedule.getTitle());
		}
		catch(Exception e) {
			log.error("ScheduleService.deleteById error!!");
			log.error(e);
		}
	}

}
