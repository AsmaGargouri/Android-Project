package com.thinktank.sps_ips_android;

import java.util.ArrayList;
import java.util.List;

import com.thinktank.sps_ips_android.Model.Catch;
import com.thinktank.sps_ips_android.Model.Point;
import com.thinktank.sps_ips_android.Model.Wifi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VisualizeDataFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.visualize_data_fragment, container, false);

		ImageView imageViewMap = (ImageView) view
				.findViewById(R.id.imageMapVizualizedata);

		CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

		imageViewMap.setOnTouchListener(MapVizualiseOnTouchListener);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();
		CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

	}

	OnTouchListener MapVizualiseOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

			int numberSplitColumns = MainActivity.numberSplitColumns;
			int numberSplitLines = MainActivity.numberSplitLines;

			Point[][] splitLimits = MainActivity.splitLimits;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ImageView map = (ImageView) getView().findViewById(
						R.id.imageMapVizualizedata);
				float[] f = new float[9];
				map.getImageMatrix().getValues(f);
				final float scaleX = f[Matrix.MSCALE_X];
				final float scaleY = f[Matrix.MSCALE_Y];
				final Drawable d = map.getDrawable();
				final int originalW = d.getIntrinsicWidth();
				final int originalH = d.getIntrinsicHeight();
				final int actualW = Math.round(originalW * scaleX);
				final int actualH = Math.round(originalH * scaleY);
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

				double icoorMin = 0, jcoorMin = 0, icoorMax = 0, jcoorMax = 0;

				List<TableRow> rowsList = new ArrayList<TableRow>();

				if ((coorx > 0) & (coorx < actualW) & (coory > 0)
						& (coory < actualH)) {

					for (int iw = 0; iw < numberSplitColumns; iw++) {
						if ((event.getX() >= (x + (iw * partitionW)))
								&& (event.getX() <= (x + ((iw + 1) * partitionW)))) {
							for (int jh = 0; jh < numberSplitLines; jh++) {
								if ((event.getY() >= (y + (jh * partitionH)))
										&& (event.getY() <= (y + ((jh + 1) * partitionH)))) {
									icoorMin = iw * partitionW;
									jcoorMin = jh * partitionH;
									icoorMax = (iw + 1) * partitionW;
									jcoorMax = (jh + 1) * partitionH;

								}
							}
						}
					}

					List<Catch> catchs = db.getAllCatchs();
					List<Catch> catchsHere = new ArrayList<Catch>();
					int sommeStrentgh = 0;
					List<Wifi> wifis = db.getAllWifi();

					TextView idText;
					TextView textOne;
					TableRow tableRow;
					int oneTime = 0 ;

					for (Wifi wifi : wifis) {
						tableRow = null;
						idText = null;
						textOne = null;
						catchsHere.clear();
						sommeStrentgh = 0;

						for (Catch ct : catchs) {
							if ((ct.getX() <= icoorMax)
									&& (ct.getX() >= icoorMin)
									&& (ct.getY() <= jcoorMax)
									&& (ct.getY() >= jcoorMin)) {
								catchsHere.add(ct);
								List<Wifi> wifiscatch = db
										.getAllWifisByCatch(ct.getId_catch());
								for (Wifi wif : wifiscatch) {
									if (wif.getBSSID().equals(wifi.getBSSID())) {
										System.out.println("somme++");

										sommeStrentgh = sommeStrentgh
												+ db.getWifiByCatch(
														ct.getId_catch(),
														wif.getBSSID());
										System.out.println("somme stren"
												+ sommeStrentgh);
									}
								}
							}
						}
						if (catchsHere.size() > 0) {
							int str = sommeStrentgh / catchsHere.size();
							System.out.println("average" + str);
							String wifiName = wifi.getSSID();
							System.out.println("wifname" + wifiName);

							tableRow = new TableRow(getActivity());
							tableRow.setLayoutParams(new TableLayout.LayoutParams(
									TableLayout.LayoutParams.MATCH_PARENT,
									TableLayout.LayoutParams.WRAP_CONTENT));

							idText = new TextView(getActivity());
							idText.setLayoutParams(new TableRow.LayoutParams(
									TableRow.LayoutParams.MATCH_PARENT,
									TableRow.LayoutParams.WRAP_CONTENT));
							idText.setText(wifiName);
							idText.setTextColor(Color.BLACK);
							tableRow.addView(idText);

							textOne = new TextView(getActivity());
							textOne.setLayoutParams(new TableRow.LayoutParams(
									TableRow.LayoutParams.MATCH_PARENT,
									TableRow.LayoutParams.WRAP_CONTENT));
							textOne.setText(str + "");
							textOne.setTextColor(Color.BLACK);
							tableRow.addView(textOne);
							
							rowsList.add(tableRow);

							System.out.println(rowsList.size());
						} else {
							if (oneTime == 0 ) {
							tableRow = new TableRow(getActivity());
							tableRow.setLayoutParams(new TableLayout.LayoutParams(
									TableLayout.LayoutParams.MATCH_PARENT,
									TableLayout.LayoutParams.WRAP_CONTENT));

							idText = new TextView(getActivity());
							idText.setLayoutParams(new TableRow.LayoutParams(
									TableRow.LayoutParams.MATCH_PARENT,
									TableRow.LayoutParams.WRAP_CONTENT));
							idText.setText("pas de catch dans cette position");
							idText.setTextColor(Color.BLACK);
							tableRow.addView(idText);
							rowsList.add(tableRow);
							oneTime++; 
							}
						}

					}
				}

				LayoutInflater li = LayoutInflater.from(getActivity());
				View tView = li.inflate(R.layout.table_wifi_alert, null);
				TableLayout tl = (TableLayout) tView;

				for (int k = 0; k < rowsList.size(); k++) {
					tl.addView(rowsList.get(k), new TableLayout.LayoutParams(
							TableLayout.LayoutParams.FILL_PARENT,
							TableLayout.LayoutParams.WRAP_CONTENT));

				}

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setView(tView);
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("OK", null)
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				final AlertDialog alertDialog;
				alertDialog = alertDialogBuilder.create();

				alertDialog
						.setOnShowListener(new DialogInterface.OnShowListener() {

							@Override
							public void onShow(DialogInterface dialog) {

								Button b = alertDialog
										.getButton(AlertDialog.BUTTON_POSITIVE);
							}
						});

				alertDialog.show();
			}

			return true;
		}
	};

}