package com.thinktank.sps_ips_android;

import com.thinktank.sps_ips_android.Model.Point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawLineView extends View {

	Point p1;
	Point p2;

	Paint paint = new Paint();

	public DrawLineView(Context context, Point p1, Point p2) {
		super(context);
		paint.setColor(Color.RED);
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawLine(this.p1.getX(), this.p1.getY(),this.p2.getX(), this.p2.getY(), paint);
		//canvas.drawLine(120, 0, 0, 200, paint);
	}
}
