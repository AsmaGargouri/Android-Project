package com.thinktank.sps_ips_android.Model;

public class CellCatched {

	int id_cell;

	long cid;

	int rssi;

	public CellCatched() {
		super();
	}

	public CellCatched(int id_cell, long cid, int rssi) {
		super();
		this.id_cell = id_cell;
		this.cid = cid;
		this.rssi = rssi;
	}

	public int getId_cell() {
		return id_cell;
	}

	public void setId_cell(int id_cell) {
		this.id_cell = id_cell;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

}
