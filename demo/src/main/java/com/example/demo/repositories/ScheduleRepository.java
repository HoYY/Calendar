package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}
