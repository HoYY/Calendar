package com.example.demo.DTO;

import javax.validation.constraints.Size;

import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ScheduleDto {
	private String title;	
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private int term;
	private String data_content;
	private Type type;
	
	@Size(max=15000)
	private String contents;
	
	public Schedule toEntity() {
		return new Schedule(title, contents, startDate+startTime, endDate+endTime, term, type);
	}
}
