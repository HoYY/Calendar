package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	@Query("select s from Schedule s where date_format(s.start_date,'%Y-%m') = ?1")
	List<Schedule> findByYearMonth(String yearMonth);
	
	@Query("select s from Schedule s where date_format(s.start_date,'%Y-%m') = ?1 and "
			+ "date_format(s.start_date,'%Y-%m-%d') = date_format(s.end_date,'%Y-%m-%d')")
	List<Schedule> findOneDayByYearMonth(String yearMonth);
}
