package com.example.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;
import com.example.demo.repositories.ScheduleRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleRepositoryTest {
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Test
	public void serialScheduleSaveAndFindTest() {
		final Schedule schedule = new Schedule("title", "contents", "2000.01.02", "2000.01.04", 2, Type.SERIAL);
		final Schedule savedSchedule = scheduleRepository.save(schedule);
		final List<Schedule> findSchedules = scheduleRepository.findByRangeAndType(Type.SERIAL, "1999.12.26", "2000.02.05");
		final Schedule findSchedule = findSchedules.get(0);
		assertThat(findSchedule.getId(), is(savedSchedule.getId()));
	}
	
	@Test
	public void oneDayScheduleSaveAndFindTest() {
		final Schedule schedule = new Schedule("title", "contents", "2000.01.02", "2000.01.02", 1, Type.ONEDAY);
		final Schedule savedSchedule = scheduleRepository.save(schedule);
		final List<Schedule> findSchedules = scheduleRepository.findOneDayByRangeAndType(Type.ONEDAY, "1999.12.26", "2000.02.05");
		final Schedule findSchedule = findSchedules.get(0);
		assertThat(findSchedule.getId(), is(savedSchedule.getId()));
	}
	
	@Test
	public void repeatedScheduleSaveAndFindTest() {
		final Schedule schedule = new Schedule("title", "contents", "2000.01.02", "2000.01.02", 2, Type.REPETITION);
		final Schedule savedSchedule = scheduleRepository.save(schedule);
		final List<Schedule> findSchedules = scheduleRepository.findByRangeAndType(Type.REPETITION, "1999.12.26", "2000.02.05");
		final Schedule findSchedule = findSchedules.get(0);
		assertThat(findSchedule.getId(), is(savedSchedule.getId()));
	}
	
	@Test
	public void findDailyScheduleTest() {
		final Schedule schedule = new Schedule("title", "contents", "2000.01.06", "2000.01.08", 2, Type.SERIAL);
		final Schedule savedSchedule = scheduleRepository.save(schedule);
		final List<Schedule> findSchedules = scheduleRepository.findDailyScheduleByDateAndType(Type.SERIAL, "2000.01.07");
		final Schedule findSchedule = findSchedules.get(0);
		assertThat(findSchedule.getId(), is(savedSchedule.getId()));
	}
	
	@Test
	public void deleteAndExistsTest() {
		final Schedule schedule = new Schedule("title", "contents", "2000.01.06", "2000.01.08", 2, Type.SERIAL);
		final Schedule savedSchedule = scheduleRepository.save(schedule);
		final boolean exists = scheduleRepository.existsByTypeAndTitleAndContents(savedSchedule.getType(), savedSchedule.getTitle(), savedSchedule.getContents());
		assertThat(exists, is(true));
		scheduleRepository.deleteByTitleAndContentsAndType(savedSchedule.getTitle(), savedSchedule.getContents(), savedSchedule.getType());
		final boolean reExists = scheduleRepository.existsByTypeAndTitleAndContents(savedSchedule.getType(), savedSchedule.getTitle(), savedSchedule.getContents());
		assertThat(reExists, is(false));
	}
}
