package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;

@Component
public class Util {
	final String emptyString = new String("");
	
	public boolean isEmpty(String s) {
		if(s == null || s.replaceAll("\\s", "").equals(emptyString))
			return true;
		else return false;
	}
	
	public List<ScheduleDto> toScheduleDtos(List<Schedule> schedules) {
		if(schedules == null)
			return null;
		
		List<ScheduleDto> scheduleDtos = new ArrayList<>();
		for(Schedule schedule : schedules) {
			scheduleDtos.add(toScheduleDto(schedule));
		}
		
		return scheduleDtos;
	}
	
	public ScheduleDto toScheduleDto(Schedule schedule) {
		if(schedule == null)
			return null;
		String start = schedule.getStart_date();
		String end = schedule.getEnd_date();
		String title = schedule.getTitle();
		String startDate = start.substring(0, 10);
		String startTime = start.substring(10, start.length());
		String endDate = end.substring(0, 10);
		String endTime = end.substring(10, end.length());
		
		ScheduleDto scheduleDto = new ScheduleDto();
		scheduleDto.setEmail(schedule.getEmail());
		scheduleDto.setTitle(title);
		scheduleDto.setContents(schedule.getContents());
		scheduleDto.setStartDate(startDate);
		scheduleDto.setStartTime(startTime);
		scheduleDto.setEndDate(endDate);
		scheduleDto.setEndTime(endTime);
		scheduleDto.setTerm(schedule.getTerm());

		String data_content = "<div class='content-line'><div class='event-consecutive-marking'></div><div class='title'><h5>"
				+ (title.length() > 4 ? title.substring(0,4)+".." : title)+"</h5>"
				+ "<h7 class='reservation'>"+ scheduleDto.getStartDate() +" "+ scheduleDto.getStartTime() +"<br/>"
				+ "~ "+ scheduleDto.getEndDate() +" "+ scheduleDto.getEndTime()+"</div></div>"
				+ "<div class='content-line'><i class='material-icons'>notes</i><div class='title'>"
				+ "<h7 class='reservation'>"+ schedule.getContents() +"</div>";

		scheduleDto.setData_content(data_content);
		
		return scheduleDto;
	}
}
