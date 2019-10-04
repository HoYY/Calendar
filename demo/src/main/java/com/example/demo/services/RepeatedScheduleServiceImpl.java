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
import com.example.demo.controllers.ScheduleController;
import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;
import com.example.demo.repositories.ScheduleRepository;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Service
public class RepeatedScheduleServiceImpl implements ScheduleService {
	private static final Logger log = LogManager.getLogger(ScheduleController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private SerialScheduleServiceImpl serialScheduleServiceImpl;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	@Autowired
	private Util util;
	
	public void insertSchedule(ScheduleDto scheduleDto) {
		String title = scheduleDto.getTitle();
		String contents = scheduleDto.getContents();
		String startDate = scheduleDto.getStartDate();
		String startTime = scheduleDto.getStartTime();
		String endTime = scheduleDto.getEndTime();
		String repetitionType = scheduleDto.getRepetitionType();
		
		if(util.isEmpty(startTime)) {
			startTime = "";
			endTime = "<하루 종일>";
		}
		
		switch(repetitionType) {
			case "everyDay":
				try {
					Date inputStartDate = dateFormat.parse(startDate);
					Calendar calendar = new GregorianCalendar();
					Schedule schedule;
					Date date;
					calendar.setTime(inputStartDate);
		
					for(int i=0; i<7; i++) {
						date = new Date(calendar.getTimeInMillis());
						String scheduleDate = dateFormat.format(date);
						schedule = new Schedule(title, contents, scheduleDate+startTime
								, scheduleDate+endTime, 1, Type.REPETITION);
						scheduleRepository.save(schedule);
						util.jumpOneDay(calendar);
					}
					log.info("Insert Repetition Schedule Case : everyDay Success");
				}
				catch(Exception e) {
					log.error("RepeatedScheduleServiceImpl.insertSchedule case : everyDay error!!");
					log.error(e);
				}
				break;
				
			case "everyWeek":
				try {
					Date inputStartDate = dateFormat.parse(startDate);
					Date inputEndDate = dateFormat.parse(scheduleDto.getEndDate());
					Calendar startCalendar = new GregorianCalendar();
					Calendar endCalendar = new GregorianCalendar();
					Calendar sundayCalendar = new GregorianCalendar();
					Calendar saturdayCalendar = new GregorianCalendar();
					Schedule schedule;
					Date start;
					Date end;
					Date sunday;
					Date saturday;
					startCalendar.setTime(inputStartDate);
					endCalendar.setTime(inputEndDate);
					
					if(startCalendar.get(Calendar.DAY_OF_WEEK) - endCalendar.get(Calendar.DAY_OF_WEEK) > 0) {
						sundayCalendar.setTime(inputEndDate);
						saturdayCalendar.setTime(inputStartDate);
						sundayCalendar.set(Calendar.DAY_OF_WEEK, 1);
						saturdayCalendar.set(Calendar.DAY_OF_WEEK, 7);
						
						for(int i=0; i<5; i++) {
							start = new Date(startCalendar.getTimeInMillis());
							end = new Date(endCalendar.getTimeInMillis());
							sunday = new Date(sundayCalendar.getTimeInMillis());
							saturday = new Date(saturdayCalendar.getTimeInMillis());
							
							if(endTime.equals("<하루 종일>"))
								schedule = new Schedule(title, contents, dateFormat.format(start)+startTime
										, dateFormat.format(saturday)+endTime, util.calculateTerm(start, saturday)
										, Type.REPETITION);
							else
								schedule = new Schedule(title, contents, dateFormat.format(start)+startTime
										, dateFormat.format(saturday), util.calculateTerm(start, saturday)
										, Type.REPETITION);
							scheduleRepository.save(schedule);
							
							schedule = new Schedule(title, contents, dateFormat.format(sunday)
									, dateFormat.format(end)+endTime, util.calculateTerm(sunday, end)
									, Type.REPETITION);
							scheduleRepository.save(schedule);
							
							util.jumpOneWeek(startCalendar);
							util.jumpOneWeek(endCalendar);
							util.jumpOneWeek(saturdayCalendar);
							util.jumpOneWeek(sundayCalendar);
						}
					}
					else {
						for(int i=0; i<5; i++) {
							start = new Date(startCalendar.getTimeInMillis());
							end = new Date(endCalendar.getTimeInMillis());
							schedule = new Schedule(title, contents, dateFormat.format(start)+startTime
									, dateFormat.format(end)+endTime, util.calculateTerm(start, end), Type.REPETITION);
							scheduleRepository.save(schedule);
							
							util.jumpOneWeek(startCalendar);
							util.jumpOneWeek(endCalendar);
						}
					}
					log.info("Insert Repetition Schedule Case : everyWeek Success");
				}
				catch(Exception e) {
					log.error("RepeatedScheduleServiceImpl.insertSchedule case : everyWeek error!!");
					log.error(e);
				}
				break;
				
			case "everyMonth":
				try {
					Date inputStartDate = dateFormat.parse(startDate);
					Date inputEndDate = dateFormat.parse(scheduleDto.getEndDate());
					Calendar startCalendar = new GregorianCalendar();
					Calendar endCalendar = new GregorianCalendar();
					Date start;
					Date end;
					startCalendar.setTime(inputStartDate);
					endCalendar.setTime(inputEndDate);
					scheduleDto.setType(Type.REPETITION);
					int originalStartDate = startCalendar.get(Calendar.DAY_OF_MONTH);
					int originalEndDate = endCalendar.get(Calendar.DAY_OF_MONTH);
					boolean isMax = originalEndDate == endCalendar.getMaximum(Calendar.DAY_OF_MONTH);
					
					for(int i=0; i<12; i++) {
						serialScheduleServiceImpl.insertSchedule(scheduleDto);
						util.jumpOneMonth(startCalendar, originalStartDate, false);
						util.jumpOneMonth(endCalendar, originalEndDate, isMax);
						start = new Date(startCalendar.getTimeInMillis());
						end = new Date(endCalendar.getTimeInMillis());
						scheduleDto.setStartDate(dateFormat.format(start));
						scheduleDto.setEndDate(dateFormat.format(end));
					}
					log.info("Insert Repetition Schedule Case : everyMonth Success");
				}
				catch(Exception e) {
					log.error("RepeatedScheduleServiceImpl.insertSchedule case : everyMonth error!!");
					log.error(e);
				}
				break;
		}
	}
	
	public List<ScheduleDto> getSchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findByRangeAndType(Type.REPETITION, 
				util.getStartDateOfMonth(year, month), util.getEndDateOfMonth(year, month)));
	}

	public List<ScheduleDto> getDailySchedulesByYearMonthDate(int year, int month, int date) {
		return scheduleUtil.toDtoList(scheduleRepository.findDailyScheduleByDateAndType(
				Type.REPETITION, util.getYYYYMMDD(year, month, date)));
	}
	
	public void deleteById(int id) {
		try {
			Schedule schedule = scheduleRepository.findById(id);
			scheduleRepository.deleteByTitleAndContentsAndType(schedule.getTitle(), schedule.getContents(), schedule.getType());
			log.info("Delete Schedule Success. Title : "+ schedule.getTitle());
		}
		catch(Exception e) {
			log.error("RepeatedScheduleServiceImpl.deleteById error!!");
			log.error(e);
		}
	}
	
	public boolean existsByTitleAndContents(ScheduleDto scheduleDto) {
		return scheduleRepository.existsByTypeAndTitleAndContents(Type.REPETITION, scheduleDto.getTitle(), scheduleDto.getContents());
	}
}
