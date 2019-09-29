package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.DTO.ScheduleDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Day {
	private int day;
	private int month;
	private List<ScheduleDto> serialSchedules = new ArrayList<>();
	private List<ScheduleDto> oneDaySchedules = new ArrayList<>();
}
