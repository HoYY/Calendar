package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controllers.MainController;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void getMainPageTest() throws Exception {
		mvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andDo(print());
	}
}
