package com.example.demo.services;

import java.util.List;

import com.example.demo.dto.ScheduleDto;

public interface ScheduleService {

	public void insertSchedule(ScheduleDto scheduleDto);
	
	public List<ScheduleDto> getSchedulesByYearMonth(int year, int month);
	
	public List<ScheduleDto> getDailySchedulesByYearMonthDate(int year, int month, int date);

	public void deleteById(int id);
	
	public boolean existsByTitleAndContents(ScheduleDto scheduleDto);
}
