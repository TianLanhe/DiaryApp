package com.example.diarypractice;

import java.io.Serializable;
import java.util.Calendar;

public class Diary implements Serializable {
	private int month; // 1表示1月，2表示2月，以此类推
	private int year;
	private int date;
	private int week; // 0表示周日，1表示周一，以此类推
	private String content;

	Diary() {

	}

	Diary(int year, int month, int date) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.week = countWeek();
		this.content = "";
	}

	Diary(int year, int month, int date, String string) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.week = countWeek();
		this.content = string;
	}

	public int countWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, date);
		return calendar.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDate() {
		return date;
	}

	public void setContent(String str) {
		content = str;
	}

	public String getContent() {
		return content;
	}

	public int getWeek() {
		return week;
	}
}
