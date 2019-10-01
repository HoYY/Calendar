package com.example.demo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Schedule {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@Column(length=50)
	private String email;
	
	@NotNull
	private String title;
	
	@NotNull
	@Column(length=15000)
	private String contents;
	
	@NotNull
	private String start_date;
	
	@NotNull
	private String end_date;
	
	@NotNull
	@Column(length=3)
	private int term;
	
	@NotNull
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private Type type;
	
	
	public Schedule() {}
	public Schedule(String email, String title, String contents, String start_date
			, String end_date, int term, Type type) {
		this.email = email;
		this.title = title;
		this.contents = contents;
		this.start_date = start_date;
		this.end_date = end_date;
		this.term = term;
		this.type = type;
	}
	
	public enum Type{
		SERIAL,
		ONEDAY
	}
}
