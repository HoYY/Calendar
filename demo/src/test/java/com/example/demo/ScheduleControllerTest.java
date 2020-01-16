package com.example.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.controllers.ScheduleController;
import com.example.demo.dto.ScheduleDto;
import com.example.demo.models.Schedule.Type;
import com.example.demo.services.OneDayScheduleServiceImpl;
import com.example.demo.services.RepeatedScheduleServiceImpl;
import com.example.demo.services.SerialScheduleServiceImpl;
import com.example.demo.utils.Day;
import com.example.demo.utils.ScheduleUtil;
import com.example.demo.utils.Util;

@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

	@Autowired
    private MockMvc mvc;
	
	@MockBean
    private SerialScheduleServiceImpl serialScheduleServiceImpl;
	
	@MockBean
    private OneDayScheduleServiceImpl oneDayScheduleServiceImpl;
	
	@MockBean
    private RepeatedScheduleServiceImpl repeatedScheduleServiceImpl;
	
	@MockBean
	private ScheduleUtil scheduleUtil;
	
	@MockBean
	private Util util;
	
	@MockBean
	private Calendar calendar;
	
	@Test
	public void getCalendarTest() throws Exception {
		List<ScheduleDto> serialSchedules = new ArrayList<ScheduleDto>();
		serialSchedules.add(new ScheduleDto("title", "contents", "2000.01.02", "2000.01.04", 2, Type.SERIAL));
		List<ScheduleDto> oneDaySchedules = new ArrayList<ScheduleDto>();
		oneDaySchedules.add(new ScheduleDto("title", "contents", "2000.01.04", "2000.01.04", 1, Type.ONEDAY));
		List<ScheduleDto> repetitionSchedules = new ArrayList<ScheduleDto>();
		repetitionSchedules.add(new ScheduleDto("title", "contents", "2000.01.02", "2000.01.04", 2, Type.REPETITION));
		List<ScheduleDto> serialDailySchedules = new ArrayList<ScheduleDto>();
		serialDailySchedules.add(new ScheduleDto("title", "contents", "2000.01.02", "2000.01.04", 2, Type.SERIAL));
		List<ScheduleDto> oneDayDailySchedules = new ArrayList<ScheduleDto>();
		oneDayDailySchedules.add(new ScheduleDto("title", "contents", "2000.01.04", "2000.01.04", 1, Type.ONEDAY));
		List<ScheduleDto> repetitionDailySchedules = new ArrayList<ScheduleDto>();
		repetitionDailySchedules.add(new ScheduleDto("title", "contents", "2000.01.02", "2000.01.04", 2, Type.REPETITION));
		List<List<Day>> mockCalendar = new ArrayList<List<Day>>();
		List<Day> week1 = new ArrayList<>();
		List<Day> week2 = new ArrayList<>();
		Day sun = new Day();
		Day mon = new Day();
		Day tue = new Day();
		sun.setSerialSchedules(serialSchedules);
		sun.setRepetitionSchedules(repetitionSchedules);
		tue.setOneDaySchedules(oneDaySchedules);
		week2.add(sun);
		week2.add(mon);
		week2.add(tue);
		mockCalendar.add(week1);
		mockCalendar.add(week2);
		
		given(serialScheduleServiceImpl.getSchedulesByYearMonth(anyInt(), anyInt())).willReturn(serialSchedules);
		given(oneDayScheduleServiceImpl.getSchedulesByYearMonth(anyInt(), anyInt())).willReturn(serialSchedules);
		given(repeatedScheduleServiceImpl.getSchedulesByYearMonth(anyInt(), anyInt())).willReturn(serialSchedules);
		given(serialScheduleServiceImpl.getDailySchedulesByYearMonthDate(anyInt(), anyInt(), anyInt())).willReturn(serialSchedules);
		given(oneDayScheduleServiceImpl.getDailySchedulesByYearMonthDate(anyInt(), anyInt(), anyInt())).willReturn(serialSchedules);
		given(repeatedScheduleServiceImpl.getDailySchedulesByYearMonthDate(anyInt(), anyInt(), anyInt())).willReturn(serialSchedules);
		given(scheduleUtil.createCalendar(anyInt(), anyInt(), anyObject(), anyObject(), anyObject())).willReturn(mockCalendar);
		
		MvcResult mvcResult = mvc.perform(get("/schedules/{year}/{month}/{date}", 2000,1,4))
			.andExpect(status().isOk())
			.andReturn();
		
		List<List<Day>> calendar = (List<List<Day>>) mvcResult.getModelAndView().getModel().get("calendar");
		List<ScheduleDto> serialDailyScheduleList = (List<ScheduleDto>) mvcResult.getModelAndView().getModel().get("serialDailySchedules");
		List<ScheduleDto> oneDayDailyScheduleList = (List<ScheduleDto>) mvcResult.getModelAndView().getModel().get("serialDailySchedules");
		List<ScheduleDto> repetitionDailyScheduleList = (List<ScheduleDto>) mvcResult.getModelAndView().getModel().get("serialDailySchedules");
		
		//첫 주 1999.12.26~2000.01.01
		//get(1) -> 둘째 주 2000.01.02~2000.01.08
		//get(1).get(0) -> 일요일(2000.01.02)
		assertThat(calendar.get(1).get(0).getSerialSchedules().get(0).getStartDate(), is("2000.01.02"));
		assertThat(calendar.get(1).get(2).getOneDaySchedules().get(0).getStartDate(), is("2000.01.04"));
		assertThat(calendar.get(1).get(0).getRepetitionSchedules().get(0).getStartDate(), is("2000.01.02"));
		assertThat(serialDailyScheduleList.get(0).getEndDate(), is("2000.01.04"));
		assertThat(oneDayDailyScheduleList.get(0).getEndDate(), is("2000.01.04"));
		assertThat(repetitionDailyScheduleList.get(0).getEndDate(), is("2000.01.04"));
	}
	
	@Test
	public void getTodayTest() throws Exception {
		given(calendar.get(Calendar.YEAR)).willReturn(2019);
		given(calendar.get(Calendar.MONTH)).willReturn(9);
		given(calendar.get(Calendar.DAY_OF_MONTH)).willReturn(4);
		
		mvc.perform(get("/schedules"))
			.andExpect(status().is3xxRedirection())
			.andDo(print());
	}
	
	@Test
	public void redirectByDailyTest() throws Exception {
		mvc.perform(get("/schedules/{year}/{month}/{date}/{isDaily}", 2019,10,4,"true"))
			.andExpect(status().is3xxRedirection())
			.andDo(print());
	}
	
	@Test
	public void deleteSchedule() throws Exception {
		doNothing().when(oneDayScheduleServiceImpl).deleteById(anyInt());
		
		mvc.perform(delete("/schedules/{id}", 1))
	    	.andExpect(status().isFound())
	    	.andExpect(status().is3xxRedirection())
	    	.andDo(print());
	}
	
}
