package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	
	Schedule findById(int id);
	
	@Query("select s from Schedule s where ((substring(s.start_date, 1, 10) between ?1 and "
			+ "?2) or (?1 between substring(s.start_date, 1, 10) and substring(s.end_date, 1, 10))) and type = ?3 order by s.start_date")
	List<Schedule> findByRange(String startRange, String endRange, Type type);
	
	@Query("select s from Schedule s where (substring(s.start_date, 1, 10) between ?1 and ?2) and "
			+ "s.type = ?3 order by s.start_date")
	List<Schedule> findOneDayByRangeAndType(String startRange, String endRange, Type type);
	
	@Transactional
	@Modifying
	@Query("delete from Schedule s where s.title = ?1 and s.contents = ?2 and s.type = ?3")
	void deleteByTitleAndContentsAndType(String title, String contents, Type type);
}
