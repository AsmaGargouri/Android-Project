package com.thinktank.sps_ips_android.Coloriage;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Bitmap> imageChunks;
	private int imageWidth, imageHeight;

	public ImageAdapter(Context c, ArrayList<Bitmap> images) {
		mContext = c;
		imageChunks = images;
		imageWidth = images.get(0).getWidth();
		imageHeight = images.get(0).getHeight();
	}

	@Override
	public int getCount() {
		return imageChunks.size();
	}

	@Override
	public Object getItem(int position) {
		return imageChunks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image;
		if (convertView == null) {
			image = new ImageView(mContext);
			image.setLayoutParams(new GridView.LayoutParams(imageWidth - 2,
					imageHeight));
			image.setPadding(0, 0, 0, 0);
		} else {
			image = (ImageView) convertView;
		}
		image.setImageBitmap(imageChunks.get(position));
		
		
		
		
		
		
		return image;
	}
}
