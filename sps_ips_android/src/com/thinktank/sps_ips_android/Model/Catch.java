package com.thinktank.sps_ips_android.Model;

import java.util.Date;

import android.text.format.Time;

public class Catch {

	int id_catch;
	double x;
	double y;
	String time;
	String date;

	public Catch() {
		super();
	}

	public Catch(int id_catch, double x, double y, String time, String date) {
		super();
		this.id_catch = id_catch;
		this.x = x;
		this.y = y;
		this.time = time;
		this.date = date;
	}
	
	public Catch (double x, double y, String time, String date) {
		super();
		this.x = x;
		this.y = y;
		this.time = time;
		this.date = date;
	}

	public int getId_catch() {
		return id_catch;
	}

	public void setId_catch(int id_catch) {
		this.id_catch = id_catch;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
