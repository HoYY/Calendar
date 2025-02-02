package com.example.demo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
	@NotNull
	@NotEmpty
	private String title;	
	
	@NotNull
	@NotEmpty
	@Size(max=15000)
	private String contents;
	
	@NotNull
	@NotEmpty
	private String startDate;
	
	private String startTime;
	
	@NotNull
	@NotEmpty
	private String endDate;
	
	private String endTime;
	private int term;
	private String data_content;
	private Type type;
	private String repetitionType;
	private String accept;
	
	public ScheduleDto() {}
	public ScheduleDto(String title, String contents, String start_date
			, String end_date, int term, Type type) {
		this.title = title;
		this.contents = contents;
		this.startDate = start_date;
		this.endDate = end_date;
		this.term = term;
		this.type = type;
	}
	
	public Schedule toEntity() {
		return new Schedule(title, contents, startDate+startTime, endDate+endTime, term, type);
	}
}
