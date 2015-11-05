package com.thinktank.sps_ips_android;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private final Context context;

	boolean isGPSEnabled = false;

	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	Location location;
	double latitude;
	double longitude;
	double altitude;
	float accuracy;

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 10 * 60;

	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.context = context;
		getLocation();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v("provider enabled", "provider enabled");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public Location getLocation() {
		try {

			locationManager = (LocationManager) context
					.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {

			} else {
				this.canGetLocation = true;

				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MINIMUM_TIME_BETWEEN_UPDATES,
							MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);

					Log.d("Network", "Network");

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							System.out
									.println("il a trouvé par contre une location :/");
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							altitude = location.getAltitude();
							this.onLocationChanged(location);
						}
					}
				}

				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MINIMUM_TIME_BETWEEN_UPDATES,
								MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {

							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);

							if (location != null) {
								System.out
										.println("gps : il a trouvé par contre une location :/");
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								altitude = location.getAltitude();
								this.onLocationChanged(location);
							}
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public double getAltitude() {
		if (location != null) {
			altitude = location.getAltitude();
		}
		return altitude;
	}

	public float getAccuracy() {
		if (location != null) {
			accuracy = location.getAccuracy();
		}
		return accuracy;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public void showSettingsAlert() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		alertDialog.setTitle("GPS is settings");

		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

}