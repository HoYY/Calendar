package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	@Query("select s from Schedule s where substring(s.start_date, 1, 10) between date_format(?1,'%Y.%m.%d') and "
			+ "date_format(?2,'%Y.%m.%d') order by s.start_date")
	List<Schedule> findByYearMonth(String startRange, String endRange);
	
	@Query("select s from Schedule s where (substring(s.start_date, 1, 10) between date_format(?1,'%Y.%m.%d') and date_format(?2,'%Y.%m.%d')) and "
			+ "substring(s.start_date, 1, 10) = substring(s.end_date, 1, 10) order by s.start_date")
	List<Schedule> findOneDayByYearMonth(String startRange, String endRange);
}
