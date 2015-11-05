package com.thinktank.sps_ips_android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinktank.sps_ips_android.Model.Catch;
import com.thinktank.sps_ips_android.Model.GsmCell;
import com.thinktank.sps_ips_android.Model.Point;
import com.thinktank.sps_ips_android.Model.Wifi;

public class MapFragment extends Fragment {

	CatchDataBaseHandler db;

	static List<TextView> textviewList;
	static List<TextView> textviewListForVisualize;

	TextView itv0;
	TextView itv2;
	public static int firstTime = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.collect_data_fragment, container, false);

		if (textviewList == null) {
			textviewList = new ArrayList<TextView>();
		}

		ImageView imageViewMap = (ImageView) view
				.findViewById(R.id.imageMapdata);

		imageViewMap.setOnTouchListener(MapOnTouchListener);

		db = new CatchDataBaseHandler(getActivity());

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onResume() {
		super.onResume();
	}

	OnTouchListener MapOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			int numberSplitColumns = MainActivity.numberSplitColumns;
			int numberSplitLines = MainActivity.numberSplitLines;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				ImageView map = (ImageView) getView().findViewById(
						R.id.imageMapdata);
				float[] f = new float[9];
				map.getImageMatrix().getValues(f);
				final float scaleX = f[Matrix.MSCALE_X];
				final float scaleY = f[Matrix.MSCALE_Y];
				final Drawable d = map.getDrawable();
				final int originalW = d.getIntrinsicWidth();
				final int originalH = d.getIntrinsicHeight();
				final int actualW = Math.round(originalW * scaleX);
				final int actualH = Math.round(originalH * scaleY);
				MainActivity.widthImage = map.getWidth();
				MainActivity.heightImage = map.getHeight();
				MainActivity.originaHImage = originalH;
				MainActivity.originalWImage = originalW;
				double imageViewRatio = (double) map.getWidth()
						/ (double) map.getHeight();
				double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));
				double h = 0, w = 0;
				double x = 0, y = 0;

				if (imgRatio > imageViewRatio) {
					h = (imageViewRatio / imgRatio) * map.getHeight();
					y = (map.getHeight() - h) / 2;
				} else {
					w = (imgRatio / imageViewRatio) * map.getWidth();
					x = (map.getWidth() - w) / 2;
				}

				double coorx = event.getX() - x;
				double coory = event.getY() - y;

				double partitionW = actualW / numberSplitColumns;
				double partitionH = actualH / numberSplitLines;

				if (firstTime == 0) {
					for (int iw = 0; iw < numberSplitColumns; iw++) {
						for (int jh = 0; jh < numberSplitLines; jh++) {

							TextView tv = new TextView(getActivity());
							tv.setId(iw * 10 + jh);
							RelativeLayout rl = (RelativeLayout) getActivity()
									.findViewById(R.id.relativeLayoutMap);

							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									actualW / numberSplitColumns, actualH
											/ numberSplitLines);

							params.setMargins((int) (x + (iw * partitionW)),
									(int) (y + (jh * partitionH)), 0, 0);

							tv.setText("0");

							tv.setLayoutParams(params);

							// tv.setVisibility(View.INVISIBLE);

							textviewList.add(tv);

							rl.addView(tv);

						}
					}
					
					firstTime++;
				}

				if (textviewList.size() > numberSplitColumns * numberSplitLines) {
					textviewList.subList(numberSplitColumns * numberSplitLines,
							textviewList.size()).clear();
				}

				if ((coorx > 0) & (coorx < actualW) & (coory > 0)
						& (coory < actualH)) {

					DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

					Catch p1 = new Catch(coorx, coory, "", "");

					long p1_id = db.createCatch(p1);

					p1.setId_catch((int) p1_id);

					String messageToDisplay = "";

					messageToDisplay = messageToDisplay + gpsInformations()
							+ wifiInformations((int) p1_id);

					gsmInformations((int) p1_id);
					updateNeighboringCids(new ArrayList<NeighboringCellInfo>());

					int id = 0;
					for (int iw = 0; iw < numberSplitColumns; iw++) {
						if ((event.getX() >= (x + (iw * partitionW)))
								&& (event.getX() <= (x + ((iw + 1) * partitionW)))) {
							for (int jh = 0; jh < numberSplitLines; jh++) {
								if ((event.getY() >= (y + (jh * partitionH)))
										&& (event.getY() <= (y + ((jh + 1) * partitionH)))) {
									id = iw * 10 + jh;
								}
							}
						}
					}

					try {
						RelativeLayout rl = (RelativeLayout) getActivity()
								.findViewById(R.id.relativeLayoutMap);

						TextView tv2 = (TextView) getActivity()
								.findViewById(id);

						int trans = Integer.parseInt((String) tv2.getText());

						rl.removeView(tv2);

						tv2.setText("");

						tv2.setVisibility(View.VISIBLE);

						rl.addView(tv2);

						trans++;

						tv2.setTextColor(Color.RED);

						tv2.setText("" + trans);

						for (TextView itv : textviewList) {
							System.out.println(itv.getText());
							if (itv.getId() == tv2.getId()) {
								textviewList
										.set(textviewList.indexOf(itv), tv2);
								break;
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					db.close();
				}
			}
			return true;
		}
	};

	private void placePin(float X, float Y) {
		int touchX = (int) X;
		int touchY = (int) Y;

		RelativeLayout rl = (RelativeLayout) getActivity().findViewById(
				R.id.relativeLayoutMap);
		ImageView PinImgViewConstant = new ImageView(getActivity());

		PinImgViewConstant.setImageResource(R.drawable.mapconstantpin);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				30, 30);
		PinImgViewConstant.setLayoutParams(params);
		params.leftMargin = touchX - 20;
		params.topMargin = touchY - 20;
		rl.addView(PinImgViewConstant, params);
		PinImgViewConstant.bringToFront();

	}

	private CharSequence gpsInformations() {
		GPSTracker gps = new GPSTracker(getActivity());

		Spanned formattedText;

		CharSequence sequenceMsg;

		String messageToDisplay = "<br/>GPS informations \n";

		formattedText = Html.fromHtml("<font color='#7FC6BC'><b>"
				+ messageToDisplay + ": </b></font>");
		sequenceMsg = formattedText;

		if (gps.canGetLocation()) {
			Location loc = gps.getLocation();
			if (loc != null) {
				formattedText = Html
						.fromHtml(" <br/> <font color='#046380'><b> Latitude : </b></font>"
								+ loc.getLatitude() + "\n . ");

				sequenceMsg = TextUtils.concat(sequenceMsg, formattedText);

				formattedText = Html
						.fromHtml(" <br/> <font color='#046380'><b> Longitude : </b></font>"
								+ loc.getLongitude() + "\n . ");

				sequenceMsg = TextUtils.concat(sequenceMsg, formattedText);
			}

			else {
				sequenceMsg = "cannot retrieve location!!";
			}
		} else {
			gps.showSettingsAlert();
		}
		return sequenceMsg;
	}

	private CharSequence wifiInformations(final int p1_id) {

		// final CatchDataBaseHandler db = new
		// CatchDataBaseHandler(getActivity());

		Spanned formattedText;

		CharSequence sequenceMsg;

		String messageToDisplay = "<br/>WIFI informations \n";

		formattedText = Html.fromHtml("<font color='#7FC6BC'><b>"
				+ messageToDisplay + ": </b></font>");
		sequenceMsg = formattedText;

		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

		if (wifiManager.isWifiEnabled()) {

			wifiManager = (WifiManager) getActivity().getSystemService(
					Context.WIFI_SERVICE);

			List<ScanResult> wifiList = null;

			List<Integer> SommesStrength = new ArrayList<Integer>();

			for (int i = 0; i < 10; i++) {

				wifiManager.startScan();

				wifiList = wifiManager.getScanResults();

				int size = wifiList.size();

				if (i == 0) {
					for (int j = 0; j < size + 2; j++) {
						SommesStrength.add(0);

					}
				}
				//Try : problem which comes one in a while : 
				if (SommesStrength.size() < size) {
					int diff = SommesStrength.size() - size;
					for (int d = 0; d < diff; d++) {
						SommesStrength.add(0);
					}
				}

				for (int j = 0; j < size; j++) {
					SommesStrength.set(j,
							SommesStrength.get(j) + wifiList.get(j).level);
				}

			}

			for (int i = 0; i < wifiList.size(); i++) {
				Wifi w1 = new Wifi(wifiList.get(i).BSSID, wifiList.get(i).SSID,
						wifiList.get(i).frequency, wifiList.get(i).capabilities);
				db.createWifi(w1, new long[] { p1_id },
						(int) Math.round(SommesStrength.get(i) / 10));

				formattedText = Html
						.fromHtml(" <br/> <font color='#046380'><b> "
								+ wifiList.get(i).SSID + ": </b></font>"
								+ (int) Math.round(SommesStrength.get(i) / 10)
								+ "\n . ");

				sequenceMsg = TextUtils.concat(sequenceMsg, formattedText);

			}

		}

		else {
			showWifiSettingAlerts();
			sequenceMsg = "WIFI not on";
		}

		return sequenceMsg;

	}

	public void showWifiSettingAlerts() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		alertDialog.setTitle("wifi settings");

		alertDialog
				.setMessage("wifi not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_WIFI_SETTINGS);
						getActivity().startActivity(intent);
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

	public void gsmInformations(final int p1_id) {

		// final CatchDataBaseHandler db = new
		// CatchDataBaseHandler(getActivity());

		TelephonyManager Tel;
		GsmSignalListener gsmSigListener;

		gsmSigListener = new GsmSignalListener();
		Tel = (TelephonyManager) getActivity().getSystemService(
				Context.TELEPHONY_SERVICE);
		Tel.listen(gsmSigListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		ArrayList<NeighboringCellInfo> neighbors = new ArrayList<NeighboringCellInfo>();
		List<CellInfo> cells = new ArrayList<CellInfo>();
		try {
			neighbors = (ArrayList) Tel.getNeighboringCellInfo();
			if (android.os.Build.VERSION.SDK_INT >= 13
					&& (!android.os.Build.MANUFACTURER.equals("SAMSUNG") || !android.os.Build.MANUFACTURER
							.equals("Samsung"))) {
				cells = Tel.getAllCellInfo();
			}
		} catch (Exception e) {
			System.out.println("NoSuchMethodError getAllCellInfo");
		}

		for (NeighboringCellInfo n : neighbors) {

			GsmCell cell = new GsmCell(n.getCid(), n.getLac(), n.getPsc());

			db.createGsmCell(cell, new long[] { p1_id }, n.getRssi());

		}

		updateNeighboringCids(neighbors);
		String networkOperator = Tel.getNetworkOperator();
		CellLocation l = Tel.getCellLocation();

	}

	private final void updateNeighboringCids(ArrayList<NeighboringCellInfo> cids) {

		StringBuilder sb = new StringBuilder();

		if (cids != null) {
			if (cids.isEmpty()) {
				sb.append("no neighboring cells");
			} else {
				for (NeighboringCellInfo cell : cids) {
					sb.append(cell.toString()).append(" ");
				}
			}
		} else {
			sb.append("unknown");
		}
	}

}
