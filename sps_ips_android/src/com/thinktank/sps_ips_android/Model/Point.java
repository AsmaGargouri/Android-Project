package com.thinktank.sps_ips_android.Model;

public class Point {

	float x;
	float y;

	public Point() {
	}

	public Point(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(double x2, double y2) {
		super();
		this.x = (float) x2;
		this.y = (float) y2;
		}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String toString() {
		return "x:" + x + "y : " + y ; 
	}
}
