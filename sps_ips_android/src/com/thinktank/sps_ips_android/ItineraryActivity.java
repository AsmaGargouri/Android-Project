package com.thinktank.sps_ips_android;

import java.util.ArrayList;
import java.util.List;

import com.thinktank.sps_ips_android.Model.Point;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Build;

public class ItineraryActivity extends ActionBarActivity {

	static Point lastPoint; 
	static List<Point> listPoints ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary);

		ImageView imageViewMap = (ImageView) findViewById(R.id.imageMapIten);

		imageViewMap.setOnTouchListener(LinesOnTouchListener);
		
		lastPoint = null; 
		listPoints = new ArrayList <Point> () ;
		
	}
	
	OnTouchListener LinesOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if ((event.getAction() == MotionEvent.ACTION_DOWN) ){
				Log.w("k","in ");
			if ( lastPoint != null ) {
			Point newPoint = new Point(event.getX() , event.getY()) ; 
			System.out.println("new point" + newPoint.toString());
			listPoints.add(newPoint);
			DrawLineView drawLineView = new DrawLineView(getApplicationContext(),lastPoint,newPoint);
			ItineraryActivity.lastPoint = newPoint ;
			drawLineView.setBackgroundColor(Color.TRANSPARENT);
	    	RelativeLayout rlA;
	    	rlA = (RelativeLayout) findViewById(
					R.id.relativeLayoutItenerary);
	    	RelativeLayout.LayoutParams params;

			params = new RelativeLayout.LayoutParams(1000,
					1000);
			params.leftMargin = 0 ;
			params.topMargin = 0 ;

			drawLineView.setLayoutParams(params);

			rlA.addView(drawLineView, params);
		
			drawLineView.bringToFront();
	       //setContentView(drawView);
			}
			
			else {
				lastPoint = new Point(event.getX() , event.getY()) ; 
				System.out.println("last point" + lastPoint.toString());
				listPoints.add(lastPoint);
			}
			System.out.println("ensemble de points"+listPoints.size());
			for ( Point p : listPoints ){
				System.out.println("point  " +  p.toString());
			}
			
			}
			   return true;
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itinerary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_itinerary,
					container, false);
			return rootView;
		}
	}

}
