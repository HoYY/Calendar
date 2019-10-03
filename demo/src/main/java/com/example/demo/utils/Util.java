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
	
	public void jumpOneMonth(Calendar calendar, int original, boolean isMax) {
		if(calendar.get(Calendar.MONTH) == 11) {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, 0);
		}
		else {
			calendar.add(Calendar.MONTH, 1);
		}
		
		int maxDayOfNextMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayOfNextMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if(isMax) {
			calendar.set(Calendar.DAY_OF_MONTH, maxDayOfNextMonth);
			System.out.println(maxDayOfNextMonth);
		}
		else if(original > dayOfNextMonth && maxDayOfNextMonth != dayOfNextMonth) {
			calendar.set(Calendar.DAY_OF_MONTH, original);
		}
	}
	
	public void jumpOneWeek(Calendar calendar) {
		int maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

		if(dayOfYear + 7 > maxDayOfYear) {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.DAY_OF_YEAR, 7 - (maxDayOfYear-dayOfYear));
		}
		else {
			calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 7);
		}
	}
	
	public void jumpOneDay(Calendar calendar) {
		int maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

		if(dayOfYear == maxDayOfYear) {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
		}
		else {
			calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);
		}
	}
}
