package com.example.demo.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;

@Component
public class ScheduleUtil {
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
		scheduleDto.setEmail(schedule.getEmail());
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
				+ "<h7 class='reservation'>"+ contents +"</div>";

		scheduleDto.setData_content(data_content);
		
		return scheduleDto;
	}
	
	public List<List<Day>> getDayOfYearMonth(int year, int month, List<ScheduleDto> serialSchedules, List<ScheduleDto> oneDaySchedules) {
		List<Day> week;
		List<List<Day>> cal = new ArrayList<>();
		int serialIndex = 0;
		int oneDayIndex = 0;
		int serilaLength = serialSchedules.size();
		int oneDayLength = oneDaySchedules.size();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		
		for(int numOfWeek=1; numOfWeek<calendar.getMaximum(Calendar.WEEK_OF_MONTH); numOfWeek++) {
			calendar.set(Calendar.WEEK_OF_MONTH, numOfWeek);
			week = new ArrayList<>();
			
			for(int i=1; i<8; i++) {
				Day day = new Day();
				calendar.set(Calendar.DAY_OF_WEEK, i);
				int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
				int calendarMonth = calendar.get(Calendar.MONTH) + 1;
				day.setDay(calendarDay);
				day.setMonth(calendarMonth);
				serialIndex = addDtoByMonthDay(day, calendarMonth, calendarDay, serialSchedules, serialIndex, serilaLength, 1);
				oneDayIndex = addDtoByMonthDay(day, calendarMonth, calendarDay, oneDaySchedules, oneDayIndex, oneDayLength, 2);
				week.add(day);
			}
			cal.add(week);
		}
		
		return cal;
	}
	
	public int addDtoByMonthDay(Day day, int calendarMonth, int calendarDay, List<ScheduleDto> schedules
			, int index, int Length, int kinds) {
		StringBuilder sb = new StringBuilder();
		
		if(calendarMonth > 9) {
			sb.append(String.valueOf(calendarMonth));
			sb.append(".");
			if(calendarDay > 9) {
				sb.append(String.valueOf(calendarDay));
			}
			else {
				sb.append("0");
				sb.append(String.valueOf(calendarDay));
			}
		}
		else {
			sb.append("0");
			sb.append(String.valueOf(calendarMonth));
			sb.append(".");
			if(calendarDay > 9) {
				sb.append(String.valueOf(calendarDay));
			}
			else {
				sb.append("0");
				sb.append(String.valueOf(calendarDay));
			}
		}
		String date = sb.toString();
		
		//date가 같으면 Day객체에 add Dto
		//kinds = 1 > 연속일정
		//kinds = 2 > 일반일정
		while(index<Length) {
			ScheduleDto scheduleDto = schedules.get(index);
			if(date.equals(scheduleDto.getStartDate().substring(5, 10))) {
				if(kinds == 1) 
					day.getSerialSchedules().add(scheduleDto);	
				else
					day.getOneDaySchedules().add(scheduleDto);
			
				index++;
			}
			//date가 다르면 바로 break;
			else
				break;
		}
		
		return index;
	}
	
}
