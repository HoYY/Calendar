package com.example.demo.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;

@Component
public class ScheduleUtil {
	private static final Logger log = LogManager.getLogger(ScheduleUtil.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	
	public List<ScheduleDto> toDtoList(List<Schedule> schedules) {
		if(schedules == null)
			return null;
		
		List<ScheduleDto> scheduleDtos = new ArrayList<>();
		for(Schedule schedule : schedules) {
			scheduleDtos.add(toDto(schedule));
		}
		
		return scheduleDtos;
	}
	
	public ScheduleDto toDto(Schedule schedule) {
		if(schedule == null)
			return null;
		String start = schedule.getStart_date();
		String end = schedule.getEnd_date();
		String title = schedule.getTitle();
		String contents = schedule.getContents();
		String startDate = start.substring(0, 10);
		String startTime = start.substring(10, start.length());
		String endDate = end.substring(0, 10);
		String endTime = end.substring(10, end.length());
		
		ScheduleDto scheduleDto = new ScheduleDto();
		scheduleDto.setTitle(title);
		scheduleDto.setContents(contents);
		scheduleDto.setStartDate(startDate);
		scheduleDto.setStartTime(startTime);
		scheduleDto.setEndDate(endDate);
		scheduleDto.setEndTime(endTime);
		scheduleDto.setTerm(schedule.getTerm());

		String data_content = "<div class='content-line'><div class='event-consecutive-marking'>"
				+ "</div><div class='title'><h5>"+ title +"</h5>"
				+ "<h7 class='reservation'>"+ startDate +" "+ startTime +"<br/>"
				+ "~ "+ endDate +" "+ endTime +"</div></div>"
				+ "<div class='content-line'><i class='material-icons'>notes</i><div class='title'>"
				+ "<h7 class='reservation'>"+ contents +"</h7><form action='/email' method='post'><div class='input-group'><div class='input-group-prepend'>"
						+ "<div class='input-group-text' id='btnGroupAddon'>@</div></div>"
						+ "<input type='text' class='form-control' name='target' placeholder='Input Invitation Mail' aria-label='Input Invitation Mail' "
						+ "aria-describedby='btnGroupAddon'></div></form>"
						+ "<div><button type='button' class='btn btn-danger btn-sm mt-2' "
						+ "data-toggle='modal' data-target='#scheduleDeleteModal' onclick='setDeleteId("+schedule.getId()+");'>일정 취소</button></div></div>";

		scheduleDto.setData_content(data_content);
		
		return scheduleDto;
	}
	
	public List<List<Day>> createCalendar(int year, int month, List<ScheduleDto> serialSchedules, List<ScheduleDto> oneDaySchedules
			, List<ScheduleDto> repetitionSchedules) {
		List<Day> week;
		List<List<Day>> cal = new ArrayList<>();
		int serialIndex = 0;
		int oneDayIndex = 0;
		int repetitionIndex = 0;
		int serilaLength = serialSchedules.size();
		int oneDayLength = oneDaySchedules.size();
		int repetitionLength = repetitionSchedules.size();
		Calendar tmpCalendar = new GregorianCalendar();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		
		for(int numOfWeek=1; numOfWeek<calendar.getMaximum(Calendar.WEEK_OF_MONTH); numOfWeek++) {
			calendar.set(Calendar.WEEK_OF_MONTH, numOfWeek);
			week = new ArrayList<>();
			
			for(int i=1; i<8; i++) {
				Day day = new Day();
				calendar.set(Calendar.DAY_OF_WEEK, i);
				day.setYear(calendar.get(Calendar.YEAR));
				day.setMonth(calendar.get(Calendar.MONTH) + 1);
				day.setDay(calendar.get(Calendar.DAY_OF_MONTH));
				serialIndex = addDtoToDay(day, calendar, tmpCalendar, serialSchedules, serialIndex, serilaLength, 1);
				oneDayIndex = addDtoToDay(day, calendar, tmpCalendar, oneDaySchedules, oneDayIndex, oneDayLength, 2);
				repetitionIndex = addDtoToDay(day, calendar, tmpCalendar, repetitionSchedules, repetitionIndex, repetitionLength, 3);
				week.add(day);
			}
			cal.add(week);
		}
		
		return cal;
	}
	
	public int addDtoToDay(Day day, Calendar calendar, Calendar tmpCalendar, List<ScheduleDto> schedules
			, int index, int Length, int kinds) {
				
		//date가 같으면 Day객체에 add Dto
		//kinds = 1 > 연속일정
		//kinds = 2 > 일반일정
		//kinds = 3 > 반복일정
		try {
			while(index<Length) {
				ScheduleDto scheduleDto = schedules.get(index);
				String start = scheduleDto.getStartDate().substring(0, 10);
				Date startDate = dateFormat.parse(start);
				tmpCalendar.setTime(startDate);
				if(calendar.get(Calendar.DAY_OF_YEAR) == tmpCalendar.get(Calendar.DAY_OF_YEAR)) {
					if(kinds == 1) 
						day.getSerialSchedules().add(scheduleDto);	
					else if(kinds == 2)
						day.getOneDaySchedules().add(scheduleDto);
					else
						day.getRepetitionSchedules().add(scheduleDto);
					
					index++;
				}
				//date가 다르면 바로 break;
				else
					break;
			}
		}
		catch(Exception e) {
			log.error("ScheduleUtil.addDtoToDay error!!");
			log.error(e);
		}
		
		return index;
	}
	
}
