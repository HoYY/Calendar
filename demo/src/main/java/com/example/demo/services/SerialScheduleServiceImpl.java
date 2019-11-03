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

import com.example.demo.dto.ScheduleDto;
import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;
import com.example.demo.repositories.ScheduleRepository;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Service
public class SerialScheduleServiceImpl implements ScheduleService {
	private static final Logger log = LogManager.getLogger(ScheduleService.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	@Autowired
	private Util util;

	public void insertSchedule(ScheduleDto scheduleDto) {
		try{
			String title = scheduleDto.getTitle();
			String contents = scheduleDto.getContents();
			String startDate = scheduleDto.getStartDate();
			String startTime = scheduleDto.getStartTime();
			String endDate = scheduleDto.getEndDate();
			String endTime = scheduleDto.getEndTime();
			Type type = scheduleDto.getType();
			
			Date inputStartDate = dateFormat.parse(startDate);
			Date inputEndDate = dateFormat.parse(endDate);
			Calendar sundayCalendar = new GregorianCalendar();
			Calendar endCalendar = new GregorianCalendar();
			Calendar saturdayCalendar = new GregorianCalendar();
			boolean first = true;
			Schedule schedule;
			Date saturday;
			Date sunday;
			sundayCalendar.setTime(inputStartDate);
			endCalendar.setTime(inputEndDate);
			saturdayCalendar.setTime(inputStartDate);
			sundayCalendar.set(Calendar.DAY_OF_WEEK, 1);
			saturdayCalendar.set(Calendar.DAY_OF_WEEK, 7);
			
			if(util.isEmpty(startTime)) {
				startTime = "";
				endTime = "<하루 종일>";
			}
			
			while(true) {
				if(endCalendar.compareTo(saturdayCalendar) > 0) {
					saturday = new Date(saturdayCalendar.getTimeInMillis());
					if(first) {
						//end > 토요일, 처음, 하루종일 -> SD+ST+토요일D+ET
						if(endTime.equals("<하루 종일>"))
							schedule = new Schedule(title, contents, startDate+startTime
									, dateFormat.format(saturday)+endTime, util.calculateTerm(inputStartDate, saturday)
									, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
						
						//end > 토요일, 처음 -> SD+ST+토요일D
						else
							schedule = new Schedule(title, contents, startDate+startTime
									, dateFormat.format(saturday), util.calculateTerm(inputStartDate, saturday)
									, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
					}
					else {
						//end > 토요일, 처음X, 하루종일 -> 일요일D+토요일D+ET
						sunday = new Date(sundayCalendar.getTimeInMillis());
						if(endTime.equals("<하루 종일>"))
							schedule = new Schedule(title, contents, dateFormat.format(sunday)
									, dateFormat.format(saturday)+endTime, util.calculateTerm(sunday, saturday)
									, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
						
						//end > 토요일, 처음X -> 일요일D+토요일D
						else
							schedule = new Schedule(title, contents, dateFormat.format(sunday)
									, dateFormat.format(saturday), util.calculateTerm(sunday, saturday)
									, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
					}
					//insert 하고, saturday and sunday 세팅
					scheduleRepository.save(schedule);
					util.jumpOneWeek(sundayCalendar);
					util.jumpOneWeek(saturdayCalendar);
					first = false;
				}
				else {
					if(first) {
						//end <= 토요일, 처음 >> SD+ST+ED+ET
						schedule = new Schedule(title, contents, startDate+startTime, endDate+endTime
								, util.calculateTerm(inputStartDate, inputEndDate)
								, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
						scheduleRepository.save(schedule);
						break;
					}
					else {
						//end <= 토요일, 처음X >> 일D+ED+ET
						sunday = new Date(sundayCalendar.getTimeInMillis());
						schedule = new Schedule(title, contents, dateFormat.format(sunday), endDate+endTime
								, util.calculateTerm(sunday, inputEndDate)
								, type == Type.REPETITION ? Type.REPETITION : Type.SERIAL);
						scheduleRepository.save(schedule);
						break;
					}
				}
			}
			log.info("Insert "+type+" Schedule Success");
		}
		catch(IllegalArgumentException e) {
			log.error("SerialScheduleServiceImpl.insertSchedule IllegalArgumentException error!!");
			log.error(e);
		}
		catch(NullPointerException ne) {
			log.error("SerialScheduleServiceImpl.insertSchedule NullPointerException error!!");
			log.error(ne);
		}
		catch(Exception e) {
			log.error("SerialScheduleServiceImpl.insertSchedule error!!");
			log.error(e);
		}
	}
	
	public List<ScheduleDto> getSchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findByRangeAndType(Type.SERIAL, 
				util.getStartDateOfMonth(year, month), util.getEndDateOfMonth(year, month)));
	}
	
	public List<ScheduleDto> getDailySchedulesByYearMonthDate(int year, int month, int date) {
		return scheduleUtil.toDtoList(scheduleRepository.findDailyScheduleByDateAndType(
				Type.SERIAL, util.getYYYYMMDD(year, month, date)));
	}

	public void deleteById(int id) {
		try {
			Schedule schedule = scheduleRepository.findById(id);
			scheduleRepository.deleteByTitleAndContentsAndType(schedule.getTitle(), schedule.getContents(), schedule.getType());
			log.info("Delete Schedule Success. Title : "+ schedule.getTitle());
		}
		catch(IllegalArgumentException e) {
			log.error("SerialScheduleServiceImpl.deleteById IllegalArgumentException error!!");
			log.error(e);
		}
		catch(NullPointerException ne) {
			log.error("SerialScheduleServiceImpl.deleteById NullPointerException error!!");
			log.error(ne);
		}
		catch(Exception e) {
			log.error("SerialScheduleServiceImpl.deleteById error!!");
			log.error(e);
		}
	}
	
	public boolean existsByTitleAndContents(ScheduleDto scheduleDto) {
		return scheduleRepository.existsByTypeAndTitleAndContents(Type.SERIAL, scheduleDto.getTitle(), scheduleDto.getContents());
	}
}