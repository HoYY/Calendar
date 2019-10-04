package com.example.demo.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.ScheduleDto;
import com.example.demo.models.Schedule;
import com.example.demo.models.Schedule.Type;
import com.example.demo.repositories.ScheduleRepository;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@Service
public class OneDayScheduleServiceImpl implements ScheduleService {
	private static final Logger log = LogManager.getLogger(ScheduleService.class);
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private ScheduleUtil scheduleUtil;
	
	@Autowired
	private Util util;
	
	public void insertSchedule(ScheduleDto scheduleDto) {
		try{
			String startTime = scheduleDto.getStartTime();
			String endTime = scheduleDto.getEndTime();
			
			if(util.isEmpty(startTime)) {
				startTime = "";
				endTime = "<하루 종일>";
			}
			
			Schedule schedule = new Schedule(scheduleDto.getTitle(), scheduleDto.getContents()
					, scheduleDto.getStartDate()+startTime, scheduleDto.getEndDate()+endTime
					, 1, Type.ONEDAY);	
			scheduleRepository.save(schedule);
			
			log.info("Insert OneDay Schedule Success");
		}
		catch(Exception e) {
			log.error("OneDayScheduleServiceImpl.insertSchedule error!!");
			log.error(e);
		}
	}
	
	public List<ScheduleDto> getSchedulesByYearMonth(int year, int month) {
		return scheduleUtil.toDtoList(scheduleRepository.findOneDayByRangeAndType(Type.ONEDAY,
				util.getStartDateOfMonth(year, month), util.getEndDateOfMonth(year, month)));
	}
	
	public List<ScheduleDto> getDailySchedulesByYearMonthDate(int year, int month, int date) {
		return scheduleUtil.toDtoList(scheduleRepository.findDailyScheduleByDateAndType(
				Type.ONEDAY, util.getYYYYMMDD(year, month, date)));
	}
	
	public void deleteById(int id) {
		try {
			Schedule schedule = scheduleRepository.findById(id);
			scheduleRepository.deleteByTitleAndContentsAndType(schedule.getTitle(), schedule.getContents(), schedule.getType());
			log.info("Delete Schedule Success. Title : "+ schedule.getTitle());
		}
		catch(Exception e) {
			log.error("OneDayScheduleServiceImpl.deleteById error!!");
			log.error(e);
		}
	}
	
	public boolean existsByTitleAndContents(ScheduleDto scheduleDto) {
		return scheduleRepository.existsByTypeAndTitleAndContents(Type.ONEDAY, scheduleDto.getTitle(), scheduleDto.getContents());
	}
}
