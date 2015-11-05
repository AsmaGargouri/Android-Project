package com.thinktank.sps_ips_android.Model;

public class GsmCell {

	int cid;
	int lac;
	int psc;

	public GsmCell() {
		super();
	}

	public GsmCell(int cid, int lac, int psc) {
		super();
		this.cid = cid;
		this.lac = lac;
		this.psc = psc;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public int getPsc() {
		return psc;
	}

	public void setPsc(int psc) {
		this.psc = psc;
	}

}
