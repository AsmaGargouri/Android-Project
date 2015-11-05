package com.thinktank.sps_ips_android.Model;

public class Wifi {

		String BSSID;
		
		String  SSID ;
		
		int frequency;
		
		String capabilities;

		public Wifi() {
		}

		public Wifi(String bSSID, String sSID, int frequency,
				String capabilities) {
			super();
			BSSID = bSSID;
			SSID = sSID;
			this.frequency = frequency;
			this.capabilities = capabilities;
		}

		public String getBSSID() {
			return BSSID;
		}

		public void setBSSID(String bSSID) {
			BSSID = bSSID;
		}

		public String getSSID() {
			return SSID;
		}

		public void setSSID(String sSID) {
			SSID = sSID;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		public String getCapabilities() {
			return capabilities;
		}

		public void setCapabilities(String capabilities) {
			this.capabilities = capabilities;
		}

		
	}
