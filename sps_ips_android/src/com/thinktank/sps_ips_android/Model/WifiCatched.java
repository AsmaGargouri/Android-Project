package com.thinktank.sps_ips_android.Model;

public class WifiCatched {
	
	int id_catch;
	
	String BSSID;
	
	int Strength;

	
	
	public WifiCatched() {
	}

	public WifiCatched(int id_catch, String bSSID, int strength) {
		super();
		this.id_catch = id_catch;
		BSSID = bSSID;
		Strength = strength;
	}

	public int getId_catch() {
		return id_catch;
	}

	public void setId_catch(int id_catch) {
		this.id_catch = id_catch;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	public int getStrength() {
		return Strength;
	}

	public void setStrength(int strength) {
		Strength = strength;
	}
	
	
	

}
