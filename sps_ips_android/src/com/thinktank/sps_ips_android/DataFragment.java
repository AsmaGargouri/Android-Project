package com.thinktank.sps_ips_android;

import java.util.List;

import com.thinktank.sps_ips_android.Model.Catch;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DataFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.collect_data_fragment, container, false);

		ImageView DefaultImageViewMap = (ImageView) view
				.findViewById(R.id.imageMapdata);

		CatchDataBaseHandler db = new CatchDataBaseHandler(getActivity());

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
}