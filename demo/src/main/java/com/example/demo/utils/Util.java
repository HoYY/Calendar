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
	
	public void jumpOneWeek(Calendar calendar) {
		int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
		int maxWeekOfMonth = calendar.getMaximum(Calendar.WEEK_OF_MONTH) - 1;
		int maxDayOfYear = calendar.getMaximum(Calendar.DAY_OF_YEAR);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int lastDayOfMonth = calendar.getMaximum(Calendar.DAY_OF_MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if(weekOfMonth == maxWeekOfMonth && maxDayOfYear - dayOfYear < 6) {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.WEEK_OF_MONTH, 2);
		}
		else if(maxDayOfYear - dayOfYear <= 6) {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.WEEK_OF_MONTH, 1);
		}
		else if(weekOfMonth == maxWeekOfMonth && lastDayOfMonth - dayOfMonth < 6) {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
			calendar.set(Calendar.WEEK_OF_MONTH, 2);
		}
		else if(lastDayOfMonth - dayOfMonth <= 6) {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
			calendar.set(Calendar.WEEK_OF_MONTH, 1);
		}
		else {
			calendar.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
		}
	}
}
