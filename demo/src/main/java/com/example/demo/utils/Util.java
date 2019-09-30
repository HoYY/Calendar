package com.example.demo.utils;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Util {
	final String emptyString = new String("");
	
	public boolean isEmpty(String s) {
		if(s == null || s.replaceAll("\\s", "").equals(emptyString))
			return true;
		else return false;
	}
	
	public String getStartDateOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.WEEK_OF_MONTH, 1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH) + 1;
		int startDay = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(startYear));
		sb.append(".");
		sb.append(String.valueOf(startMonth));
		sb.append(".");
		sb.append(String.valueOf(startDay));
		
		return sb.toString();
	}
	
	public String getEndDateOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.WEEK_OF_MONTH, calendar.getMaximum(Calendar.WEEK_OF_MONTH));
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonth = calendar.get(Calendar.MONTH) + 1;
		int endDay = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(endYear));
		sb.append(".");
		sb.append(String.valueOf(endMonth));
		sb.append(".");
		sb.append(String.valueOf(endDay));
		
		return sb.toString();
	}
	
	public int calculateTerm(Date start, Date end) {
		return (int) ((end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000)) + 1;
	}
}
