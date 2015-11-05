package com.thinktank.sps_ips_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentsPagerAdapter extends FragmentPagerAdapter {

	final int PAGE_COUNT = 4 ;

	public FragmentsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {

		switch (arg0) {

		case 0:
			StartFragment startFragment = new StartFragment();
			return startFragment;

		case 1:
			MapFragment mapFragment = new MapFragment();
			return mapFragment;
		
		case 2:
			VisualizeDataFragment visualizeDataFragment = new VisualizeDataFragment();
			return visualizeDataFragment;
		
		case 3:
			AnalysisFragment AnalysisFragment = new AnalysisFragment();
			return AnalysisFragment;		
			
		}
		return null;
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}
}