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
	
	@Query("select s from Schedule s where type = ?1 and ((substring(s.start_date, 1, 10) between ?2 and "
			+ "?3) or (?2 between substring(s.start_date, 1, 10) and substring(s.end_date, 1, 10))) order by s.start_date")
	List<Schedule> findByRangeAndType(Type type, String startRange, String endRange);
	
	@Query("select s from Schedule s where s.type = ?1 and (substring(s.start_date, 1, 10) between ?2 and ?3) "
			+ "order by s.start_date")
	List<Schedule> findOneDayByRangeAndType(Type type, String startRange, String endRange);
	
	@Query("select s from Schedule s where s.type = ?1 and ?2 between substring(s.start_date, 1, 10) and substring(s.end_date, 1, 10) order by s.start_date")
	List<Schedule> findDailyScheduleByDateAndType(Type type, String date);
	
	@Transactional
	@Modifying
	@Query("delete from Schedule s where s.title = ?1 and s.contents = ?2 and s.type = ?3")
	void deleteByTitleAndContentsAndType(String title, String contents, Type type);
	
	@Query("select count(1) > 0 from Schedule s where s.type = ?1 and s.title = ?2 and s.contents = ?3")
	boolean existsByTypeAndTitleAndContents(Type type, String title, String contents);
	
	@Transactional
	@Modifying
	@Query("update Schedule s set s.accept = 'Accept!!' where s.title = ?1")
	void updateByTitle(String title);
}
