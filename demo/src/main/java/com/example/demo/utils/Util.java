package com.example.demo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Util {
	final String emptyString = new String("");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	
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
		String startDate = dateFormat.format(new Date(calendar.getTimeInMillis()));
		
		return startDate;
	}
	
	public String getEndDateOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.WEEK_OF_MONTH, calendar.getMaximum(Calendar.WEEK_OF_MONTH) -1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		String endDate = dateFormat.format(new Date(calendar.getTimeInMillis()));
		
		return endDate;
	}
	
	public String getYYYYMMDD(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		return dateFormat.format(new Date(calendar.getTimeInMillis()));
	}
	
	public int calculateTerm(Date start, Date end) {
		return (int) ((end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000)) + 1;
	}
	
	public void jumpOneWeek(Calendar sundayCalendar, Calendar saturdayCalendar) {
		if(saturdayCalendar.get(Calendar.WEEK_OF_MONTH) == saturdayCalendar.getMaximum(Calendar.WEEK_OF_MONTH) -1 
				&& sundayCalendar.get(Calendar.MONTH) == 11) {
			saturdayCalendar.set(Calendar.YEAR, sundayCalendar.get(Calendar.YEAR) +1);
			saturdayCalendar.set(Calendar.MONTH, 0);
			saturdayCalendar.set(Calendar.WEEK_OF_MONTH, 1);
			sundayCalendar.set(Calendar.YEAR, sundayCalendar.get(Calendar.YEAR) +1);
			sundayCalendar.set(Calendar.MONTH, 0);
		}
		else if(saturdayCalendar.get(Calendar.WEEK_OF_MONTH) == saturdayCalendar.getMaximum(Calendar.WEEK_OF_MONTH) -1) {
			saturdayCalendar.set(Calendar.MONTH, sundayCalendar.get(Calendar.MONTH) +1);
			saturdayCalendar.set(Calendar.WEEK_OF_MONTH, 1);
			sundayCalendar.set(Calendar.MONTH, sundayCalendar.get(Calendar.MONTH) +1);
			sundayCalendar.set(Calendar.WEEK_OF_MONTH, 1);
		}
		else {
			saturdayCalendar.set(Calendar.WEEK_OF_MONTH, saturdayCalendar.get(Calendar.WEEK_OF_MONTH) +1);
			sundayCalendar.set(Calendar.WEEK_OF_MONTH, sundayCalendar.get(Calendar.WEEK_OF_MONTH) +1);
		}
	}
}
