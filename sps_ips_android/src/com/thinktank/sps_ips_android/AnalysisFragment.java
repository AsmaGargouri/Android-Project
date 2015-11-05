package com.thinktank.sps_ips_android;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.thinktank.sps_ips_android.Model.Point;
import com.thinktank.sps_ips_android.Model.Wifi;

public class AnalysisFragment extends Fragment {

	int wifiNumber = 0;

	TextView wifiTextView;

	TextView progressTV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.analysis_fragment, container, false);

		ImageView imageViewMap = (ImageView) view
				.findViewById(R.id.imageMapAnalysis);

		progressTV = (TextView) view.findViewById(R.id.tv_progressbar);

		progressTV.setTextColor(Color.parseColor("#C9001A"));

		progressTV.setText("Please click");

		imageViewMap.setOnTouchListener(ColorifyOnTouchListener);

		return view;
	}

	OnTouchListener ColorifyOnTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {

			AsyncTaskRunner runner = new AsyncTaskRunner();
			runner.execute(event);
			return true;

		}

	};

	public int getAverageSWifi(int limXInf, int limXSup, int limYInf,
			int limYSup, Wifi wi) {

		int sommeStrentgh = 0;

		final CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity()
				.getApplicationContext());

		List<Catch> catchs = db.getAllCatchs();
		List<Catch> catchsHere = new ArrayList<Catch>();

		for (Catch ct : catchs) {
			if ((ct.getX() <= limXSup) && (ct.getX() >= limXInf)
					&& (ct.getY() <= limYSup) && (ct.getY() >= limYInf)) {
				catchsHere.add(ct);
				List<Wifi> wifiscatch = db.getAllWifisByCatch(ct.getId_catch());
				for (Wifi w : wifiscatch) {
					if (w.getBSSID().equals(wi.getBSSID())) {
						sommeStrentgh = sommeStrentgh
								+ db.getWifiByCatch(ct.getId_catch(),
										wi.getBSSID());
					}
				}
			}
		}
		int str = 0;
		if (catchsHere.size() > 0) {
			str = (sommeStrentgh / catchsHere.size());
			str = (150 - Math.abs(str)) * 250 / (55);
		}
		if (catchsHere.size() == 0) {
			str = -1;
		}
		db.close();
		return str;
	}

	public Bitmap changeColor(int width, int height, int couleur) {
	
		int[] couleurs = { Color.RED, Color.CYAN, Color.BLUE, Color.YELLOW,
				Color.GREEN, Color.MAGENTA };
	Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.blanc);
		Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, width,
				height);
		Paint p = new Paint();
		ColorFilter filter;
	if (couleur != -1) {
			filter = new LightingColorFilter(couleurs[couleur], 1);
		} else {
			filter = new LightingColorFilter(Color.BLACK, 1);
		}
		p.setColorFilter(filter);
		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(resultBitmap, 0, 0, p);
		return resultBitmap;
	}

	public void drawEach(int portionImgW, int portionImgH, int WifiSize,
			double x, double y, Wifi wi) {

	
		int numberSplitColumns = MainActivity.numberSplitColumns;
		int numberSplitLines = MainActivity.numberSplitLines;

		Point[][] splitLimits = MainActivity.splitLimits;

		Bitmap colorBitmap;
		RelativeLayout rlA;
		Bitmap resizedColorBitmap;
		RelativeLayout.LayoutParams params;
		ImageView ColorImg;

		rlA = (RelativeLayout) getActivity().findViewById(
				R.id.relativeLayoutAnalysis);

		for (int i = 0; i < numberSplitColumns; i++) {

			for (int j = 0; j < numberSplitLines; j++) {

				int alpha = getAverageSWifi((0 + portionImgW * i),
						(portionImgW + portionImgW * i), (0 + portionImgH * j),
						(portionImgH + portionImgH * j), wi);

				if (alpha >= 0) {

					colorBitmap = changeColor(portionImgW, portionImgH,
							wifiNumber % WifiSize);

					resizedColorBitmap = Bitmap.createScaledBitmap(colorBitmap,
							portionImgW, portionImgH, true);

					params = new RelativeLayout.LayoutParams(portionImgW,
							portionImgH);

					ColorImg = new ImageView(getActivity());

					ColorImg.setImageBitmap(resizedColorBitmap);
					params.leftMargin = (int) x + portionImgW * i;
					params.topMargin = (int) y + portionImgH * j;

					ColorImg.setLayoutParams(params);

					rlA.addView(ColorImg, params);
					ColorImg.setAlpha(alpha);

				} else {

				}

			}
		}
	
	}

	public void drawEach2(int portionImgW, int portionImgH, int WifiSize,
			double x, double y, Wifi wi) {

	
		int numberSplitColumns = MainActivity.numberSplitColumns;
		int numberSplitLines = MainActivity.numberSplitLines;

		int[] couleurs = { Color.RED, Color.CYAN, Color.BLUE, Color.YELLOW,
				Color.GREEN, Color.MAGENTA };

		RelativeLayout rlA;
		RelativeLayout.LayoutParams params;

		rlA = (RelativeLayout) getActivity().findViewById(
				R.id.relativeLayoutAnalysis);

		for (int i = 0; i < numberSplitColumns; i++) {

			for (int j = 0; j < numberSplitLines; j++) {

				int alpha = getAverageSWifi((0 + portionImgW * i),
						(portionImgW + portionImgW * i), (0 + portionImgH * j),
						(portionImgH + portionImgH * j), wi);

				if (alpha >= 0) {

					TextView colorTv = new TextView(getActivity());
					colorTv.setBackgroundColor(couleurs[wifiNumber % WifiSize]);

					params = new RelativeLayout.LayoutParams(portionImgW,
							portionImgH);

					params.leftMargin = (int) x + portionImgW * i;
					params.topMargin = (int) y + portionImgH * j;

					colorTv.setLayoutParams(params);
					colorTv.getBackground().setAlpha(alpha);
					rlA.addView(colorTv, params);

				} else {

				}

			}
		}
	
	}

	public int getPortionImgW() {
	
		int numberSplitColumns = MainActivity.numberSplitColumns;
		ImageView Map = (ImageView) getView().findViewById(
				R.id.imageMapAnalysis);
		float[] f = new float[9];
		Map.getImageMatrix().getValues(f);
		final float scaleX = f[Matrix.MSCALE_X];
		final Drawable d = Map.getDrawable();
		final int originalW = d.getIntrinsicWidth();
		final int actualW = Math.round(originalW * scaleX);
		int portionImgW = actualW / numberSplitColumns;

	
		return portionImgW;
	}

	public int getPortionImgH() {
	
		int numberSplitLines = MainActivity.numberSplitLines;
		ImageView Map = (ImageView) getView().findViewById(
				R.id.imageMapAnalysis);
		float[] f = new float[9];
		Map.getImageMatrix().getValues(f);
		final float scaleY = f[Matrix.MSCALE_Y];
		final Drawable d = Map.getDrawable();
		final int originalH = d.getIntrinsicHeight();
		final int actualH = Math.round(originalH * scaleY);
		int portionImgH = actualH / numberSplitLines;

	
		return portionImgH;
	}

	public int getWifiSize() {

	
		final CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity()
				.getApplicationContext());
		List<Wifi> wifis = db.getAllWifi();
	
		return wifis.size();

	}

	public float getScaleXY(int choice) {
		float scale = 0;
		ImageView Map = (ImageView) getView().findViewById(
				R.id.imageMapAnalysis);
		float[] f = new float[9];
		Map.getImageMatrix().getValues(f);
		if (choice == 1) {
			scale = f[Matrix.MSCALE_X];
		} else {
			scale = f[Matrix.MSCALE_Y];
		}

		
		return scale;

	}

	public double getXY(float scaleX, float scaleY, int choice) {
		
		ImageView Map = (ImageView) getView().findViewById(
				R.id.imageMapAnalysis);

		final Drawable d = Map.getDrawable();
		final int originalW = d.getIntrinsicWidth();
		final int originalH = d.getIntrinsicHeight();
		final int actualW = Math.round(originalW * scaleX);
		final int actualH = Math.round(originalH * scaleY);

		double imageViewRatio = (double) Map.getWidth()
				/ (double) Map.getHeight();
		double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));
		double h = 0, w = 0;
		double x = 0, y = 0;

		if (imgRatio > imageViewRatio) {
			h = (imageViewRatio / imgRatio) * Map.getHeight();
			y = (Map.getHeight() - h) / 2;
		} else {
			w = (imgRatio / imageViewRatio) * Map.getWidth();
			x = (Map.getWidth() - w) / 2;
		}

		if (choice == 1)
			return x;
		else
			return y;

	}

	public Wifi getWi() {

		final CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity()
				.getApplicationContext());
		List<Wifi> wifis = db.getAllWifi();
		Wifi wi = wifis.get(wifiNumber % getWifiSize());
		db.close();
		return wi;
	}

	public void colorier2(MotionEvent event) {
	
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			wifiNumber++;
			ImageView Map = (ImageView) getView().findViewById(
					R.id.imageMapAnalysis);

			Map.bringToFront();
			Map.setOnTouchListener(null);
		}
	
	}

	public void colorier(MotionEvent event) {

		progressTV = (TextView) getView().findViewById(R.id.tv_progressbar);
		progressTV.setText("Please wait . . . ");

		ImageView Map = (ImageView) getView().findViewById(
				R.id.imageMapAnalysis);

		Map.bringToFront();
		progressTV.bringToFront();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			wifiNumber++;

			float[] f = new float[9];
			Map.getImageMatrix().getValues(f);
			final float scaleX = f[Matrix.MSCALE_X];
			final float scaleY = f[Matrix.MSCALE_Y];
			final Drawable d = Map.getDrawable();
			final int originalW = d.getIntrinsicWidth();
			final int originalH = d.getIntrinsicHeight();
			final int actualW = Math.round(originalW * scaleX);
			final int actualH = Math.round(originalH * scaleY);
			double imageViewRatio = (double) Map.getWidth()
					/ (double) Map.getHeight();
			double imgRatio = (double) ((actualW * scaleX) / (actualH * scaleY));
			double h = 0, w = 0;
			double x = 0, y = 0;

			if (imgRatio > imageViewRatio) {
				h = (imageViewRatio / imgRatio) * Map.getHeight();
				y = (Map.getHeight() - h) / 2;
			} else {
				w = (imgRatio / imageViewRatio) * Map.getWidth();
				x = (Map.getWidth() - w) / 2;
			}

			int portionImgW = actualW / MainActivity.numberSplitColumns;

			int portionImgH = actualH / MainActivity.numberSplitLines;

			final CatchDataBaseHandler db = new CatchDataBaseHandler(
					getActivity().getApplicationContext());

			List<Wifi> wifis = db.getAllWifi();

			if (wifis.size() > 0) {

				Wifi wi = wifis.get(wifiNumber % wifis.size());

				Toast.makeText(getActivity().getApplicationContext(),
						"wifi:" + wi.getSSID(), Toast.LENGTH_LONG).show();
				drawEach2(portionImgW, portionImgH, wifis.size(), x, y, wi);

			}
			db.close();
		}

	}

	class AsyncTaskRunner extends AsyncTask<MotionEvent, Void, Integer> {

		@Override
		protected Integer doInBackground(MotionEvent... ev) {

			
			colorier2(ev[0]);

			return 1;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			
			progressTV = (TextView) getView().findViewById(R.id.tv_progressbar);
			progressTV.setText("Please wait . . . ");

			ImageView Map = (ImageView) getView().findViewById(
					R.id.imageMapAnalysis);

			Map.bringToFront();
			progressTV.bringToFront();

			
		}

		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			
			if (result != null && result == 1) {

				int wifiSize = getWifiSize();
				if (wifiSize > 0) {
					Wifi wi = getWi();

					drawEach2(getPortionImgW(), getPortionImgH(), wifiSize,
							getXY(getScaleXY(1), getScaleXY(2), 1),
							getXY(getScaleXY(1), getScaleXY(2), 2), wi);

					ActionBar actionBar = getActivity().getActionBar();
					actionBar.show();
					actionBar.setSubtitle(wi.getSSID());
					actionBar.setTitle("Analysis of WIFI  : ");
				}

			} else {
			}

			progressTV.setText("");
			ImageView Map = (ImageView) getView().findViewById(
					R.id.imageMapAnalysis);

			Map.setOnTouchListener(ColorifyOnTouchListener);

		
		}

	}
}
